package era.uploader.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import era.uploader.controller.QRErrorBus;
import era.uploader.creation.QRCreator;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * This class contains the business logic for QR Creation. If any errors occur
 * during QR creation they will be inserted into the
 * {@link QRCreationService#BUS}
 */
@ParametersAreNonnullByDefault
public class QRCreationService {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final QRCodeMappingDAO QRCodeMappingDAO;
    private final CourseDAO courseDAO;

    public QRCreationService(QRCodeMappingDAO QRCodeMappingDAO, CourseDAO courseDAO) {
        Preconditions.checkNotNull(QRCodeMappingDAO);
        Preconditions.checkNotNull(courseDAO);
        this.QRCodeMappingDAO = QRCodeMappingDAO;
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
     * later date and time we need up creating new QRCodeMapping entries in the database
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
    public Multimap<Student, QRCodeMapping> createQRs(Collection<Student> students, int numberOfQRs) {
        Preconditions.checkNotNull(students);

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService threads = Executors.newFixedThreadPool(processors);
        List<Future<QRCodeMapping>> futures =
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

        ImmutableMultimap.Builder<Student, QRCodeMapping> studentsToPages =
                ImmutableMultimap.builder();

        for (Future<QRCodeMapping> futurePage : futures) {
            try {
                QRCodeMapping QRCodeMapping = futurePage.get();
                studentsToPages.put(QRCodeMapping.getStudent(), QRCodeMapping);
            } catch (InterruptedException e) {
                QRErrorStatus status = QRErrorStatus.INTERRUPT_ERROR;
                BUS.fire(new QRErrorEvent(status));
            } catch (ExecutionException e) {
                QRErrorStatus status = QRErrorStatus.GENERATION_ERROR;
                BUS.fire(new QRErrorEvent(status));
            }
        }

        Multimap<Student, QRCodeMapping> ret = studentsToPages.build();
        QRCodeMappingDAO.insertAll(ret.values());

        return ret;
    }
}
