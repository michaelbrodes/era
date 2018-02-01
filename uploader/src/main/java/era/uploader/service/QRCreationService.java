package era.uploader.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import era.uploader.service.assignment.AbstractQRSaver;
import era.uploader.service.assignment.AveryTemplate;
import era.uploader.service.assignment.QRCode;
import era.uploader.service.assignment.QRCreator;
import era.uploader.service.assignment.QRSaverFactory;
import era.uploader.service.assignment.AddressLabelSaver;
import era.uploader.service.assignment.ShippingLabelSaver;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * This class contains the business logic for QR Creation.
 *
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@ParametersAreNonnullByDefault
public class QRCreationService {
    private final QRCodeMappingDAO qrCodeMappingDAO;
    @Deprecated
    private final static String QRCODEDIRECTORY = "QRCode Directory";
    public  final static String ASSIGNMENTS_DIR = "assignments";

    public QRCreationService(QRCodeMappingDAO qrCodeMappingDAO) {
        Preconditions.checkNotNull(qrCodeMappingDAO);
        this.qrCodeMappingDAO = qrCodeMappingDAO;
    }

    /**
     * In order to match students to their assignments we create QR codes that
     * should be attached to those assignments. In order to allow Graders to
     * put in assignment pages out of order, we generate a unique QR code for
     * each page of an assignment.
     *
     * This method takes in a list of assignments that are part of a course
     * and generates qr codes for every page of every assignment for every
     * student in that. These QR codes are then printed out into a PDF in a
     * format that allows them to be printed on Avery Labels. We support
     * templates of 2" by 4" shipping labels and of 1" by 2.625" address
     * labels.
     *
     * Each QR code encodes an "Universally Unique ID" {@link UUID} that is
     * unique per page. UUIDs prevent students from easily reverse engineering
     * who has what QR code. Each QR code will be saved in our database as a
     * {@link QRCodeMapping} row. QRCodeMappings store the QR code's UUID, the
     * student associated with it, and the page number that the QR code was put
     * on.
     *
     * NOTE: Despite what it may seem, we actually don't create Assignments in
     * this method. We will eventually create assignments when we implement the
     * "Multi-page Ordering" feature of this project.
     *
     * @param course the course that contains the students we want to map QR
     *               codes to
     * @param assignmentName the name of this assignment.
     * @param numberOfPages the number of pages for this particular assignment
     * @param template the type of Avery Label template we want to save QR
     *                    codes on. This will allow us to determine if we want
     *                    an {@link AddressLabelSaver}
     *                    or a {@link ShippingLabelSaver}
     *                    strategy at runtime. Strategies are instantiated
     *                    using {@link QRSaverFactory}
     * @return the QR code -> student mappings for each page of every
     * assignment in the "assignments" parameter.
     * @see QRCreator for the QR code creation logic
     * @see AbstractQRSaver for the general strategy
     * of saving QR codes.
     * @see era.uploader.service.assignment for all classes used in this process
     */
    public List<QRCodeMapping> createQRs(
            Course course,
            String assignmentName,
            int numberOfPages,
            AveryTemplate template
    ) {
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(course.getStudentsEnrolled());
        Preconditions.checkNotNull(assignmentName);
        Preconditions.checkNotNull(template);

        int processors = Runtime.getRuntime().availableProcessors();

        // A thread pool of N threads where N is the number of processors on a system.
        ListeningExecutorService creatorThreads = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(processors)
        );

        // The one single thread used for saving. This is so we can pause the main thread.
        // TODO - you will probably need to replace this with the previous thread pool once we have a saver for each student.
        ListeningExecutorService saverThread = MoreExecutors.listeningDecorator(
                Executors.newSingleThreadExecutor()
        );

        // this next variable kind of blows but here is a break done:
        // QRCode - a QR code that should be attached to a single page on a single assignment
        // List<QRCode> - every QR code for every assignment for a single student
        // Future<List<QRCode>> - the **PROMISE** of every QR code for a single student once QRCreator#call is done
        // List<Future<List<QRCode>>> - every promise for every student in the course that we will get back every
        // QR Code from QRCreator#call
        // TODO - Josh please make this so. Right now it is every QRCode from every batch.
        List<Future<List<QRCode>>> futures =
                Lists.newArrayList();

        // TODO - this should be replace by a single list of all students. That way we can save every QR code for a particular student into one PDF
        List<List<Student>> studentBatches = bucketStudentsIntoLabelBatches(
                course.getStudentsEnrolled(),
                processors
        );
        // a latch that will force the main thread to wait until all QR Codes have been saved to PDFs
        CountDownLatch finishedLatch = new CountDownLatch(studentBatches.size());

        // distribute out the labor of creating and saving.
        // TODO - saver creation should be done in the loop
        AbstractQRSaver saver = QRSaverFactory.saver(template, finishedLatch);
        for (List<Student> students : studentBatches) {
            QRCreator creator = new QRCreator(
                    course,
                    students,
                    assignmentName,
                    numberOfPages,
                    template.getQRHeight(),
                    template.getQRWidth()
            );
            ListenableFuture<List<QRCode>> creationFuture = creatorThreads.submit(creator);
            // once a AbstractQRSaver's corresponding QRCreator has finished it
            // will be scheduled to save. See https://github.com/google/guava/wiki/ListenableFutureExplained
            Futures.addCallback(creationFuture, saver, saverThread);
            futures.add(creationFuture);
        }

        waitTillSavingIsDone(finishedLatch);

        // gather the results of each QRCreator#call into one list of QRCodeMappings and insert them into the DB
        ImmutableList<QRCodeMapping> studentQRMappings = gatherMappings(futures);
        qrCodeMappingDAO.insertAll(studentQRMappings);

        // start calling QRSaver#save to save the PDF documents to the disk
        // TODO - replace with loop that goes through all students and saves individual PDFs
        String assignmentFileName = assignmentFileName(assignmentName);
        try {
            saver.save(assignmentFileName);
            Desktop.getDesktop().open(new File(assignmentFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return studentQRMappings;
    }

    /**
     * Pause the main thread until all {@link AbstractQRSaver#writeBatch(List)}
     * are finished writing QR Codes.
     *
     * @param finishedLatch the {@link CountDownLatch} that will be decremented
     *                      when each QRSaver is finished.
     */
    private void waitTillSavingIsDone(CountDownLatch finishedLatch) {
        try {
            finishedLatch.await();
        } catch (InterruptedException e) {
            System.err.println("QR Creation tasks got interrupted before getting finished.");
            e.printStackTrace();
        }
    }

    /**
     * Create a filename that can be used to save the assignment to disk
     * this should be a lot like
     *
     * {@code assignments/{{studentEID}}_{{assignmentName}}_{{todaysDate}}.pdf}
     *
     * @param assignmentName the name of the assignment which should be used to
     *                      generate the filename
     */
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
     * Flattens out the each result for every {@link QRCreator#call()} into one
     * huge list, so it can be saved to the database and returned to the GUI
     *
     * @param qrCreationFutures every result for every QRCreator
     * @return a flattened out list that contains all QRCodes (as
     * {@link QRCodeMapping}s) that were in the qrCreationFutures parameter
     */
    private ImmutableList<QRCodeMapping> gatherMappings(List<Future<List<QRCode>>> qrCreationFutures) {
        Preconditions.checkNotNull(qrCreationFutures);

        ImmutableList.Builder<QRCodeMapping> studentQRMappings =
                ImmutableList.builder();

        for (Future<List<QRCode>> futureQRCodeBatch : qrCreationFutures) {
            try {
                List<QRCodeMapping> mappings = futureQRCodeBatch.get()
                        .stream()
                        .map(QRCode::asQRCodeMapping)
                        .collect(Collectors.toList());

                studentQRMappings.addAll(mappings);
            } catch (InterruptedException e) {
                System.err.println("Tread Interrrupted Before Shutdown");
            } catch (ExecutionException e) {
                System.err.println("Exception Generating QR Code");
            }
        }

        return studentQRMappings.build();
    }

    /**
     * Saves a QRCodeMapping to a PNG file located in the ./"QRCode Directory"
     *
     * @param qrCodeMapping the qr code that you wish to save to a file.
     * @throws IOException couldn't save the PNG for some reason.
     * @deprecated in the actual release application we are using
     * {@link ShippingLabelSaver} to save qrs to a pdf. This is just here to generate
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
        File file = new File (QRCODEDIRECTORY + path);
        ImageIO.write(byteMatrix, "png", file);
    }
}
