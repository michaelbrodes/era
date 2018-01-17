package era.uploader.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import era.uploader.creation.QRCode;
import era.uploader.creation.QRCreator;
import era.uploader.creation.QRSaver;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains the business logic for QR Creation.
 */
@ParametersAreNonnullByDefault
public class QRCreationService {
    private final QRCodeMappingDAO QRCodeMappingDAO;
    public  final static String QRCODEDIRECTORY = "QRCode Directory";
    public  final static String ASSIGNMENTS_DIR = "assignments";

    public QRCreationService(QRCodeMappingDAO QRCodeMappingDAO) {
        Preconditions.checkNotNull(QRCodeMappingDAO);
        this.QRCodeMappingDAO = QRCodeMappingDAO;
    }

    /**
     * Provided a course with students, an assignment name, and a number of
     * pages on that assignment: this method generates all the QR codes each
     * student would need on an assignment. The QR codes will encode
     * <em>Universally Unique IDs</em> to match students to their assignment
     * page. Universally Unique IDs make it so that students can't easily
     * reverse engineer the qr codes and student pages can be anywhere in the
     * large pdf that is processed through this system.
     *
     * <strong>NOTE</strong>: Because we need to match UUIDs to pages at a
     * later date and time we end up creating new QRCodeMapping entries in the
     * database that aren't matched to a particular assignment that has been
     * handed in.
     *
     * @see java.util.UUID
     * @param course    A set of students that are in a course.
     * @param numberOfPages the number of pages in a single assignment
     * @return numberOfPages * course#getStudentsEnrolled()#size worth of
     *         QRCodeMappings grouped by the Students they are
     *         associated with.
     * @see Multimap to find out how a multimap works.
     */
    public Multimap<Student, QRCodeMapping> createQRs(Course course, String assignmentName, int numberOfPages) {
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(course.getStudentsEnrolled());
        Preconditions.checkNotNull(assignmentName);

        int processors = Runtime.getRuntime().availableProcessors();
        ListeningExecutorService threads = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(processors)
        );
        // gross but needed because we assign batches of QR code creations to each of the different cpu cores
        List<Future<List<QRCode>>> futures =
                Lists.newArrayList();
        List<List<Student>> studentBatches = bucketStudentsIntoLabelBatches(
                course.getStudentsEnrolled(),
                processors
        );


        QRSaver saver = new QRSaver();
        for (List<Student> students : studentBatches) {
            QRCreator creator = new QRCreator(course, students, assignmentName, numberOfPages);
            ListenableFuture<List<QRCode>> creationFuture = threads.submit(creator);
            Futures.addCallback(creationFuture, saver, MoreExecutors.directExecutor());
            futures.add(creationFuture);
        }

        threads.shutdown();

        try {
            if (!threads.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                System.err.println("Thread Timed Out");
            }
        } catch (InterruptedException e) {
            System.err.println("Tread Interrrupted Before Shutdown");
        }

        ImmutableMultimap.Builder<Student, QRCodeMapping> studentsToPages =
                ImmutableMultimap.builder();

        for (Future<List<QRCode>> futureQRCodeBatch : futures) {
            try {
                for (QRCode qrCode : futureQRCodeBatch.get()) {
                    QRCodeMapping QRCodeMapping = qrCode.createQRCodeMapping();
                    studentsToPages.put(QRCodeMapping.getStudent(), QRCodeMapping);
                }
            } catch (InterruptedException e) {
                System.err.println("Tread Interrrupted Before Shutdown");
            } catch (ExecutionException e) {
                System.err.println("Exception Generating QR Code");
            }
        }

        Multimap<Student, QRCodeMapping> ret = studentsToPages.build();
        QRCodeMappingDAO.insertAll(ret.values());
        String assignmentFileName = assignmentFileName(assignmentName);
        try {
            saver.save(assignmentFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public String assignmentFileName(String assignmentName) {
        String assignmentDate = LocalDate.now().toString();
        return ASSIGNMENTS_DIR + File.separator + assignmentName + "-" + assignmentDate + ".pdf";
    }


    /**
     * Divides the students up into batches that can be split evenly into the
     * different processors on this system. The idea is that we want to evenly
     * distribute the labor of creating new QR codes amongst the processors.
     *
     * @param students all the students in a course.
     * @param processors the number of processors on this system.
     * @return all the students in the course bucketed into buckets of size:
     * students.size()/processors.
     */
    @VisibleForTesting
    List<List<Student>> bucketStudentsIntoLabelBatches(Collection<Student> students, int processors) {
        Preconditions.checkNotNull(students);

        // each batch (bucket) will have students#size()/processors number of
        // students. For instance, on a quad core system a class of 500
        // students will have batches of 125
        List<Student> studentList = Lists.newArrayList(students);
        List<List<Student>> buckets = Lists.newArrayListWithExpectedSize(processors);
        for (int i = 0; i < processors; i++) {
            buckets.add(Lists.newLinkedList());
        }

        for (int i = 0; i < students.size(); i++) {
            Student currentStudent = studentList.get(i);
            int bucketIndex = i % processors;
            buckets.get(bucketIndex).add(currentStudent);
        }

        return buckets;
    }

    /**
     * Saves a QRCodeMapping to a PNG file located in the ./"QRCode Directory"
     *
     * @param qrCodeMapping the qr code that you wish to save to a file.
     * @throws IOException couldn't save the PNG for some reason.
     * @deprecated in the actual release application we are using
     * {@link QRSaver} to save qrs to a pdf. This is just here to generate
     * test documents. It will be removed by release
     *
     * TODO remove before the end of the semester.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Deprecated
    public void saveQRCodeMapping(QRCodeMapping qrCodeMapping) throws IOException {
        File directory = new File(QRCODEDIRECTORY);
        if(!directory.exists()){
            directory.mkdir();
        }

        String path = directory.getAbsolutePath() + File.separator + qrCodeMapping.getStudent().getUserName() +"_" + qrCodeMapping.getUuid() + ".PNG";

        BufferedImage byteMatrix = qrCodeMapping.getQrCode();
        File file = new File (QRCODEDIRECTORY + File.separator + qrCodeMapping.getUuid() + ".png");
        ImageIO.write(byteMatrix, "png", file);
//        MatrixToImageWriter.writeToPath(byteMatrix, "PNG", Paths.get(path));
    }
}
