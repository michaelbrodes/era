package era.uploader.controller;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.zxing.common.BitMatrix;
import era.uploader.common.QRCreationException;
import era.uploader.common.QRCreator;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class QRCreationController {
    private static final String AWAIT_ERROR = "It has taken the QR creation process a really long time so we stopped it";

    /**
     * Provided a course with students and a number of QR codes to generate
     * for each student this method generates QR codes for students. The QR
     * codes will be a hash of the students schoolId with a sequence number
     * for the page.
     *
     * @param course      A course filled with students.
     * @param numberOfQRs the number of qr codes we should generate for each
     *                    student
     */
    public Multimap<Student, BitMatrix> createQRs(Course course, int numberOfQRs)
            throws QRCreationException {
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService threads = Executors.newFixedThreadPool(processors);
        List<Future<ImmutableMultimap<Student, BitMatrix>>> futures =
                Lists.newArrayList();

        for (int i = 0; i < numberOfQRs; i++) {
            for (Student student : course.getStudents()) {
                QRCreator creator = new QRCreator(student, i);
                futures.add(threads.submit(creator));
            }
        }

        threads.shutdown();

        try {
            if (!threads.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                throw new QRCreationException(AWAIT_ERROR);
            }
        } catch (InterruptedException e) {
            throw new QRCreationException(e);
        }

        ImmutableMultimap.Builder<Student, BitMatrix> studentsToQRs =
                ImmutableMultimap.builder();

        for (Future<ImmutableMultimap<Student, BitMatrix>> singletonFuture : futures) {
            try {
                studentsToQRs.putAll(singletonFuture.get());
            } catch (InterruptedException e) {
                throw new QRCreationException(e);
            } catch (ExecutionException e) {
                throw new QRCreationException(e.getMessage());
            }
        }

        return studentsToQRs.build();
    }
}
