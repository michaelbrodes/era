package era.uploader.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import era.uploader.data.viewmodel.AssignmentPrintoutMetaData;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * This class contains the business logic for QR Creation.
 *
 */
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

    //TODO: assignment qrcodes aren't being saved to the pdf file, figure it out and fix it, also fix unit tests that I broke changing around code
    public void printAndSaveQRCodes(List<AssignmentPrintoutMetaData> assignmentsList, AveryTemplate averyTemplate){
         createQRs( groupAssignmentByStudent( assignmentsList ), averyTemplate ); //groups Assignments by student then creates the QRCodes for each student, then saves those QRCodes
    }

    public Map<Student, List<AssignmentPrintoutMetaData>> groupAssignmentByStudent (List<AssignmentPrintoutMetaData> assignmentsList){
        Map< Student, List<AssignmentPrintoutMetaData>> assignmentsToPrint = new HashMap<>();
        for ( AssignmentPrintoutMetaData assignment : assignmentsList ) {
            for ( Student student: assignment.getCourse().getStudentsEnrolled() ) {
                if ( !assignmentsToPrint.containsKey(student) ){
                    assignmentsToPrint.put(student, new LinkedList<>());
                }
                assignmentsToPrint.get(student).add(assignment);
            }
        }
        return assignmentsToPrint;
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
     * TODO: Josh add in the changed params for this mehtod please -past Josh
     *
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

    public List<QRCode> createQRs(
            Map<Student, List<AssignmentPrintoutMetaData>> groupAssignmentByStudent,
            AveryTemplate template
    ) {
        Preconditions.checkNotNull(groupAssignmentByStudent);
        Preconditions.checkNotNull(template);

        int processors = Runtime.getRuntime().availableProcessors();

        // A thread pool of N threads where N is the number of processors on a system.
        ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(processors)
        );



        // this next variable kind of blows but here is a break down:
        // QRCode - a QR code that should be attached to a single page on a single assignment
        // List<QRCode> - every QR code for every assignment for a single student
        // Future<List<QRCode>> - the **PROMISE** of every QR code for a single student once QRCreator#call is done
        // List<Future<List<QRCode>>> - every promise for every student in the course that we will get back every
        // QR Code from QRCreator#call
        List<Future<List<QRCode>>> futures =
                Lists.newArrayList();

        // Changing the assignments in gropuAssignmentsByStudents Map to instead map to QRCodes
        Map<Student, List<QRCode>> studentBatches = mapStudentsToQrCodes(
                groupAssignmentByStudent
        );
        // a latch that will force the main thread to wait until all QR Codes have been saved to PDFs
        CountDownLatch finishedLatch = new CountDownLatch(studentBatches.size());
        List<AbstractQRSaver> abstractQRSavers = new ArrayList<>();

        // distribute out the labor of creating and saving.
        // TODO - saver creation should be done in the loop
        for (Map.Entry<Student, List<QRCode>> qrCode : studentBatches.entrySet()) {
            QRCreator creator = new QRCreator(
                    qrCode.getValue(),
                    template.getQRHeight(),
                    template.getQRWidth()
            );
            AbstractQRSaver saver = QRSaverFactory.saver(template, finishedLatch, qrCode.getKey());
            abstractQRSavers.add(saver);
            ListenableFuture<List<QRCode>> creationFuture = threadPool.submit(creator);
            // once a AbstractQRSaver's corresponding QRCreator has finished it
            // will be scheduled to save. See https://github.com/google/guava/wiki/ListenableFutureExplained
            Futures.addCallback(creationFuture, saver, threadPool);
            futures.add(creationFuture);
        }

        waitTillSavingIsDone(finishedLatch);

        // gather the results of each QRCreator#call into one list of QRCodeMappings and insert them into the DB
        ImmutableList<QRCode> studentQRCodes = gatherQrCodes(futures);
        saveQRCodes(studentQRCodes, abstractQRSavers);


        return studentQRCodes;
    }

    public Map<Student, List<QRCode>> mapStudentsToQrCodes(Map<Student, List<AssignmentPrintoutMetaData>> groupAssignmentByStudent){
        Map<Student, List<QRCode>> studentToQRCodes = new HashMap<>();
        for ( Map.Entry<Student, List<AssignmentPrintoutMetaData>> entry: groupAssignmentByStudent.entrySet() ) {
            List<QRCode> qrCodes = new ArrayList<>();
            for (AssignmentPrintoutMetaData assignment: entry.getValue() ) {
                Assignment currentAssignment =  createAssignmentForStudent(entry.getKey(), assignment);
                for (int i = 1; i <= assignment.getNumPages(); i++){
                    qrCodes.add(new QRCode(assignment.getCourse(), entry.getKey(), currentAssignment, assignment.getNumPages()));
                }
            }
            studentToQRCodes.put(entry.getKey(), qrCodes );
        }
        return studentToQRCodes;
    }

    Assignment createAssignmentForStudent(Student student, AssignmentPrintoutMetaData assignmentPrintoutMetaData) {
        Preconditions.checkNotNull(student);
        Course course = assignmentPrintoutMetaData.getCourse();
        Assignment.Builder assignmentBuilder = Assignment.builder()
                .withCourse(course)
                .withStudent(student);

        // anything less than 0 is an invalid database id
        if (course.getUniqueId() > 0) {
            assignmentBuilder.withCourse_id(course.getUniqueId());
        }

        if (student.getUniqueId() > 0) {
            assignmentBuilder.withStudent_id(student.getUniqueId());
        }

        return assignmentBuilder.create(assignmentPrintoutMetaData.getAssignmentName());
    }

    //TODO: save qr code correctly
    public void saveQRCodes(Collection <QRCode> qrCodes, List<AbstractQRSaver>abstractQRSavers){
        List <QRCodeMapping> qrCodeMappings = new ArrayList<QRCodeMapping>();

        for ( QRCode qrCode : qrCodes ) {
            qrCodeMappings.add(qrCode.asQRCodeMapping());
        }
        qrCodeMappingDAO.insertAll(qrCodeMappings);
        // start calling QRSaver#save to save the PDF documents to the disk
        for (AbstractQRSaver saver: abstractQRSavers ) {
            String assignmentFileName = studentFileName(saver.getStudent());
            try {
                saver.save(assignmentFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
     * @param student The student associated with the file containing all their assignment QRCodes
     */
    public String studentFileName(Student student) {
        String assignmentDate = LocalDate.now().toString();
        return ASSIGNMENTS_DIR + File.separator + student.getUserName() + "-" + assignmentDate + ".pdf";
    }


    /**
     * Flattens out the each result for every {@link QRCreator#call()} into one
     * huge list, so it can be saved to the database and returned to the GUI
     *
     * @param qrCreationFutures every result for every QRCreator
     * @return a flattened out list that contains all QRCodes (as
     * {@link QRCodeMapping}s) that were in the qrCreationFutures parameter
     */
    private ImmutableList<QRCode> gatherQrCodes(List<Future<List<QRCode>>> qrCreationFutures) {
        Preconditions.checkNotNull(qrCreationFutures);

        ImmutableList.Builder<QRCode> studentQRCodes =
                ImmutableList.builder();

        for (Future<List<QRCode>> futureQRCodeBatch : qrCreationFutures) {
            try {
                List<QRCode> mappings = new ArrayList<>(futureQRCodeBatch.get());

                studentQRCodes.addAll(mappings);
            } catch (InterruptedException e) {
                System.err.println("Tread Interrrupted Before Shutdown");
            } catch (ExecutionException e) {
                System.err.println("Exception Generating QR Code");
            }
        }

        return studentQRCodes.build();
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
