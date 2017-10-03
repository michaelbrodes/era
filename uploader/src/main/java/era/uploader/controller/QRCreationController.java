package era.uploader.controller;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import era.uploader.creation.QRCreator;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.PageDAO;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;


/**
 * This class contains the business logic for QR Creation. If any errors occur
 * during QR creation they will be inserted into the
 * {@link QRCreationController#BUS}
 */
@ParametersAreNonnullByDefault
public class QRCreationController {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private static final int STUDENT_RECORD_SIZE = 5;
    // the position of the different fields in the CSV file
    private static final int LAST_NAME = 0;
    private static final int FIRST_NAME = 1;
    // the school user name for the student
    private static final int USER_NAME = 2;
    private static final int SCHOOL_ID = 3;
    private static final int COURSE_ID = 4;

    // the sub field COURSE_ID
    private static final int COURSE_ID_SIZE = 4;
    private static final int DEPARTMENT = 0;
    private static final int COURSE_NBR = 1;
    private static final int SECTION_NBR = 2;
    private static final int REFERENCE_NBR = 3;

    private final PageDAO pageDAO;


    public QRCreationController(PageDAO pageDAO) {
        Preconditions.checkNotNull(pageDAO);
        this.pageDAO = pageDAO;
    }

    /**
     * Provided a course with students and a number of QR codes to generate
     * for each student this method generates QR codes for students. The QR
     * codes will be a hash of the students schoolId with a sequence number
     * for the page.
     *
     * @param studentRoster A set of students that are partaking in a course.
     * @param numberOfQRs   the number of qr codes we should generate for each
     *                      student
     */
    public Multimap<Student, Page> createQRs(String studentRoster, int numberOfQRs) {
        Preconditions.checkNotNull(studentRoster);
        Set<Student> students = null;
        try {
            students = generateStudents(studentRoster);
        } catch (IOException e) {
            e.printStackTrace();
            return ImmutableMultimap.of();
        }

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
     * @param inputFile Path to the CSV file we are grabbing the students form
     * @return A set of students that are contained in that CSV file
     * @throws IOException We couldn't find the file you inputted.
     */
    @VisibleForTesting
    Set<Student> generateStudents(String inputFile) throws IOException {
        Preconditions.checkNotNull(inputFile);
        return Files.readAllLines(Paths.get(inputFile))
                .stream()
                .map((inputStudent) -> inputStudent.split(","))
                .map((studentRecord) -> {
                    if (studentRecord.length < STUDENT_RECORD_SIZE) {
                        String erroredLine = String.join(",", studentRecord);
                        QRErrorEvent event = new QRErrorEvent(
                                QRErrorStatus.PARSE_ERROR,
                                null,
                                erroredLine
                        );
                        BUS.fire(event);
                        return null;
                    } else {
                        return new Student();
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collector.of(
                        ImmutableSet.Builder::new,
                        ImmutableSet.Builder::add,
                        (l, r) -> l.addAll(r.build()),
                        ImmutableSet.Builder<Student>::build,
                        Collector.Characteristics.UNORDERED
                ));
    }
}
