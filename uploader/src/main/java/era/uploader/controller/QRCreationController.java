package era.uploader.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import era.uploader.creation.QRCreator;
import era.uploader.creation.QRErrorBus;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.PageDAO;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the business logic for QR Creation. If any errors occur
 * during QR creation they will be inserted into the
 * {@link QRCreationController#BUS}
 */
@ParametersAreNonnullByDefault
public class QRCreationController {
    private static final QRErrorBus BUS = QRErrorBus.instance();
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
     * @param students    A set of students that are partaking in a course.
     * @param numberOfQRs the number of qr codes we should generate for each
     *                    student
     */
    public Multimap<Student, Page> createQRs(Set<Student> students, int numberOfQRs) {
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
}
