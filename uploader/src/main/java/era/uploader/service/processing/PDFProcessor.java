package era.uploader.service.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import era.uploader.communication.AssignmentUploader;
import era.uploader.communication.FailedAssignment;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.QRErrorStatus;
import era.uploader.data.model.Student;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private final Path pdfInput;
    private final Collection<Course> courses;
    private final String assignmentName;
    private final QRCodeMappingDAO QRCodeMappingDAO;
    private final AssignmentDAO assignmentDAO;
    private final String host;
    private final ScanningProgress scanningProgress;

    PDFProcessor(
            QRCodeMappingDAO QRCodeMappingDao,
            AssignmentDAO assignmentDAO,
            Path pdfInput,
            Collection<Course> courses,
            String assignmentName,
            @Nullable String host
    ) {
        this.pdfInput = pdfInput;
        this.assignmentDAO = assignmentDAO;
        this.courses = courses;
        this.assignmentName = assignmentName;
        this.QRCodeMappingDAO = QRCodeMappingDao;
        this.host = host;
        this.scanningProgress = new ScanningProgress();
    }

    /**
     * Entry point into the {@link era.uploader.service.processing} package. It is our
     * PDF processing algorithm (i.e. the algorithm that matches students to
     * pdfInput in an inputted PDF). The algorithm is heavily based upon
     * MapReduce. Map reduce allows for high concurrency by creating a pipeline
     * of sorts, while also keeping logic modularized. The stages of the
     * pipeline are Spit a large pdf into pdfInput -> scatter those pdfInput with
     * {@link List#parallelStream()} -> feed them into
     * {@link QRScanner#extractQRCodeInformation(Map.Entry)} to grab uuid ->
     * match {@link #associateWithExistingQR(QRCodeMapping)} -> mergeMappings student
     * associated Pages into one pdf -> store in either the local database or
     * the remote database.
     *
     * @param pdf            a path to a large pdf filled with multiple student
     *                       assignments, in arbitrary order.
     * @param courses        the courses this pdf was submitted to.
     * @param assignmentName the name of the assignment that this pdf was for
     * @return a list of PDFs that have pdfInput that were associated with
     * students.
     */
    public static ScanningProgress process(
            QRCodeMappingDAO QRCodeMappingDAO,
            AssignmentDAO assignmentDAO,
            Path pdf,
            Collection<Course> courses,
            String assignmentName,
            @Nullable String host
    ) {
        Preconditions.checkNotNull(pdf);
        Preconditions.checkNotNull(courses);
        Preconditions.checkNotNull(assignmentName);
        Preconditions.checkNotNull(QRCodeMappingDAO);

        PDFProcessor processor = new PDFProcessor(
                QRCodeMappingDAO,
                assignmentDAO,
                pdf,
                courses,
                assignmentName,
                host
        );

        return processor.startPipeline();
    }

    /**
     * The pipeline for scanning qr codes off of assignment pdfInput to find which
     * students they belong to. Essentially we are provided a list of
     * {@link PDDocument} which represents all the pdfInput that belong to all
     * the submissions for the assignment with the name {@link #assignmentName}.
     * For each of those pdfInput we pull out the QR code attached to the page,
     * then we match the UUID on that QR code to a student, then put those
     * matches into a {@link Multimap} that groups the Pages by student, and
     * then finally we mergeMappings all pdfInput together into an {@link Assignment}
     * object and store it in the database.
     *
     * @return the assignment submissions we generated during this run.
     */
    private ScanningProgress startPipeline() {
        Runnable pipelineTask = () -> {
            // load and exlode document
            Optional<PDDocument> pages = loadPDF(pdfInput);
            Map<Integer, String> idToPage = ImmutableMap.of();

            try {
                idToPage = PDFExploder.explodeFile(pages);
            } catch (IOException e) {
                // this shouldn't happen, but we want to user to know when it does.
                scanningProgress.addError("Cannot split inputted pdf into pages.");
                e.printStackTrace();
            }

            scanningProgress.setPdfFileSize(idToPage.size());

            // scan and match pages in parallel
            QRScanner scanner = new QRScanner(scanningProgress);
            Map<Student, List<QRCodeMapping>> studentsToPages = idToPage.entrySet().parallelStream()
                    .map(scanner::extractQRCodeInformation)
                    .filter(Objects::nonNull)
                    .map(this::associateWithExistingQR)
                    .filter(Objects::nonNull)
                    .sorted((left, right) -> ComparisonChain.start()
                            .compare(left.getStudentId(), right.getStudentId())
                            .compare(left.getSequenceNumber(), right.getSequenceNumber())
                            .result())
                    .collect(Collectors.groupingBy(QRCodeMapping::getStudent));

            // group pages into assignments based on their students
            Collection<Assignment> assignments = groupIntoAssignments(studentsToPages);

            // if we have remote uploading enabled, upload assignments to the server
            if (host != null) {

                try {
                    List<FailedAssignment> failedAssignments = AssignmentUploader.uploadAssignments(assignments, host);
                    for (FailedAssignment failure : failedAssignments) {
                        scanningProgress.addError(failure.toString());
                    }
                } catch (IOException e) {
                    scanningProgress.addError("Unable to upload assignments to server.");
                    e.printStackTrace();
                }

            }

            scanningProgress.done();

        };

        new Thread(pipelineTask).start();

        return scanningProgress;
    }

    /**
     * Given a singular UUID, find and update a Placeholder QRCodeMapping into a
     * Valid QRCodeMapping. Create multimap object and populate with student
     * associated with the QRCodeMapping in the argument
     */
    private QRCodeMapping associateWithExistingQR(QRCodeMapping qrCodeMapping) {
        Preconditions.checkNotNull(qrCodeMapping);

        QRCodeMapping qrCodeMappingFromDB = QRCodeMappingDAO.read(qrCodeMapping.getUuid());
        if (qrCodeMappingFromDB == null) {
            scanningProgress.addError("Student Does Not Exist");
            return null;
        } else {
            qrCodeMappingFromDB.setTempDocumentName(qrCodeMapping.getTempDocumentName());
            qrCodeMappingFromDB.setDocument(qrCodeMapping.getDocument());
            return qrCodeMappingFromDB;
        }
    }

    /**
     * Merges a QRCodeMapping from the database to the QRCodeMapping we saw on
     * the page.
     */
    private QRCodeMapping mergeMappings(QRCodeMapping dbQRCodeMapping, QRCodeMapping scannedQRCodeMapping) {
        Preconditions.checkNotNull(dbQRCodeMapping);
        Preconditions.checkNotNull(scannedQRCodeMapping);

        dbQRCodeMapping.setTempDocumentName(scannedQRCodeMapping.getTempDocumentName());
        dbQRCodeMapping.setDocument(scannedQRCodeMapping.getDocument());

        return dbQRCodeMapping;
    }

    /**
     * After this is called, we now have a multimap object with each page
     * mapped to a student
     */
    private Collection<Assignment> groupIntoAssignments(Map<Student, List<QRCodeMapping>> studentQRGroupings) {
        List<Assignment> assignments = new LinkedList<>();
        for (Map.Entry<Student, List<QRCodeMapping>> pages :
                studentQRGroupings.entrySet()) {
            Student student = pages.getKey();
            Course studentsCourse = findCourseContainingStudent(student);
            if (student != null && studentsCourse != null) {
                Collection<QRCodeMapping> pagesToAdd = pages.getValue();
                assignments.add(
                        Assignment.builder()
                                .withQRCodeMappings(pagesToAdd)
                                .withStudent(student)
                                .withCourse(studentsCourse)
                                .withCreatedDateTime(LocalDateTime.now())
                                .createUnique(assignmentName)
                );
            } else if (student == null) {
                scanningProgress.addError("Student doesn't exist. THIS SHOULDN'T HAPPEN");
            } else {
                scanningProgress.addError("Student " + student.getUserName() + " doesn't belong to an inputted course");
            }
        }
        mergeAssignmentPages(assignments);

        return assignments;
    }

    @Nullable
    private Course findCourseContainingStudent(@Nullable Student student) {
        Course studentsCourse = null;

        if (student != null) {
            for (Course course : courses) {
                if (course.getStudentsEnrolled().contains(student)) {
                    studentsCourse = course;
                    break;
                }
            }
        }

        return studentsCourse;
    }

    /**
     * After this is called, all pdfInput for each student are now merged, and saved at the
     * appropriate location. also stores the assignment file location in the database
     */
    private void mergeAssignmentPages(Collection<Assignment> assignments) {
        PDFMergerUtility merger = new PDFMergerUtility();
        for (Assignment a : assignments) {
            PDDocument allPages = new PDDocument();
            for (QRCodeMapping p : a.getQRCodeMappings()
                    ) {
                try {
                    PDDocument document;
                    if (p.getDocument() == null) {
                        document = PDDocument.load(new File(p.getTempDocumentName()));
                    } else {
                        document = p.getDocument();
                    }
                    merger.appendDocument(allPages, document); //mergeMappings individual PDDocuments into 1 document
                    document.close();
                    if (p.getTempDocumentName() != null) {
                        Files.delete(Paths.get(p.getTempDocumentName()));
                    }
                } catch (java.io.IOException e) {
                    scanningProgress.addError(QRErrorStatus.MERGE_ERROR.getReason());
                }
            }
            try {
                allPages.save(a.getImageFilePath()); //save PDDocument with all pdfInput to server
                assignmentDAO.storeAssignment(a);//store assignment in database
                allPages.close();
            } catch (java.io.IOException e) {
                scanningProgress.addError(QRErrorStatus.SAVE_ERROR.getReason());
            }
        }
    }

    /**
     * Loads a PDF from the filesystem. If said pdf document doesn't exist, or
     * we don't have the permissions to access it, an error will show up in
     * {@link ScanningProgress}
     *
     * @param pdf the path to the pdf we want to load
     * @return {@link Optional#EMPTY} if there was a problem loading a pdf, or
     * a present optional if we did load it
     */
    private Optional<PDDocument> loadPDF(Path pdf) {
        Optional<PDDocument> loaded = Optional.empty();

        try {
            loaded = Optional.of(PDDocument.load(pdf.toFile()));
        } catch (IOException e) {
            scanningProgress.addError("Could not open PDF document");
        }

        return loaded;
    }
}
