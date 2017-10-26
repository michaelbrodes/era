package era.uploader.controller;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import era.uploader.creation.CSVParser;
import era.uploader.creation.QRCreator;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.CourseDAO;
import era.uploader.data.PageDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static era.uploader.common.MultimapCollector.toMultimap;


/**
 * This class contains the business logic for QR Creation. If any errors occur
 * during QR creation they will be inserted into the
 * {@link QRCreationController#BUS}
 */
@ParametersAreNonnullByDefault
public class QRCreationController {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final PageDAO pageDAO;
    private final CourseDAO courseDAO;

    public QRCreationController(PageDAO pageDAO, CourseDAO courseDAO) {
        Preconditions.checkNotNull(pageDAO);
        Preconditions.checkNotNull(courseDAO);
        this.pageDAO = pageDAO;
        this.courseDAO = courseDAO;
    }

    /**
     * Provided a course with students and a number of QR codes to generate
     * for each student this method generates QR codes for students. The QR
     * codes will encode <em>Universally Unique IDs</em> to match students to
     * their assignment "Pages". Universally Unique IDs make it so the
     * students can't easily reverse engineer the QR Codes and allow for the
     * primary keys in the remote database to match the primary keys in the
     * local database (traditional sequence numbers aren't guaranteed to be
     * synced up).
     *
     * <strong>NOTE</strong>: Because we need to match UUIDs to pages at a
     * later date and time we need up creating new Page entries in the database
     * that aren't matched to a particular pdf page.
     *
     * @see java.util.UUID
     * @param students    A set of students that are partaking in a course.
     * @param numberOfQRs the number of qr codes we should generate for each
     *                    student
     * @return numberOfQrs worth of Pages grouped by the Students they are
     *         associated with. See {@link Multimap} to find out how a multimap
     *         works
     */
    public Multimap<Student, Page> createQRs(Collection<Student> students, int numberOfQRs) {
        Preconditions.checkNotNull(students);

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService threads = Executors.newFixedThreadPool(processors);
        List<Future<Page>> futures =
                Lists.newArrayList();

        for (int i = 0; i < numberOfQRs; i++) {
            for (Student student : students) {
                QRCreator creator = new QRCreator(student, i);
                futures.add(threads.submit(creator));
            }
        }

        threads.shutdown();

        try {
            if (!threads.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                QRErrorStatus status = QRErrorStatus.TIMEOUT_ERROR;
                BUS.fire(new QRErrorEvent(status));
            }
        } catch (InterruptedException e) {
            QRErrorStatus status = QRErrorStatus.INTERRUPT_ERROR;
            BUS.fire(new QRErrorEvent(status));
        }

        ImmutableMultimap.Builder<Student, Page> studentsToPages =
                ImmutableMultimap.builder();

        for (Future<Page> futurePage : futures) {
            try {
                Page page = futurePage.get();
                studentsToPages.put(page.getStudent(), page);
            } catch (InterruptedException e) {
                QRErrorStatus status = QRErrorStatus.INTERRUPT_ERROR;
                BUS.fire(new QRErrorEvent(status));
            } catch (ExecutionException e) {
                QRErrorStatus status = QRErrorStatus.GENERATION_ERROR;
                BUS.fire(new QRErrorEvent(status));
            }
        }

        Multimap<Student, Page> ret = studentsToPages.build();
        pageDAO.insertAll(ret.values());

        return ret;
    }

    /**
     * Generates a set of students given an input CSV file. The schema for
     * that CSV file is of Last Name, First Name, Username, Student ID, Child
     * Course ID, *. Anything after Child Course ID is ignored.
     *
     * @param filePath Path to the CSV file we are grabbing the students form
     * @return a map of courses to students.
     * @throws IOException We couldn't find the file you inputted.
     */
    public Multimap<Course, Student> generateStudents(Path filePath) throws IOException {
        Preconditions.checkNotNull(filePath);
        // ignore the title record
        final int firstRecord = 1;

        // The stream needs to be closed after the first reduce is done
        Multimap<Course, Student> courseToStudents;
        try (Stream<String> csvLines = Files.lines(filePath)) {
            courseToStudents = csvLines.skip(firstRecord)
                    .map((inputStudent) -> inputStudent.split(","))
                    .map(CSVParser::parseLine)
                    .filter(Objects::nonNull)
                    .collect(toMultimap());
        }
        courseDAO.insertCourseAndStudents(courseToStudents);
        return courseToStudents;
    }
}
