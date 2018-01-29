package era.uploader.service.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import era.uploader.common.MultimapCollector;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.QRErrorStatus;
import era.uploader.data.model.Student;
import era.uploader.communication.AssignmentUploader;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private final Path pdfInput;
    private final Course course;
    private final String assignmentName;
    private final QRCodeMappingDAO QRCodeMappingDAO;
    private final AssignmentDAO assignmentDAO;
    private final String host;
    private final ScanningProgress scanningProgress;

    PDFProcessor(
            QRCodeMappingDAO QRCodeMappingDao,
            AssignmentDAO assignmentDAO,
            Path pdfInput,
            Course course,
            String assignmentName,
            @Nullable String host
    ) {
        this.pdfInput = pdfInput;
        this.assignmentDAO = assignmentDAO;
        this.course = course;
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
     * match {@link #associateStudentsWithPage(QRCodeMapping)} -> mergeMappings student
     * associated Pages into one pdf -> store in either the local database or
     * the remote database.
     *
     * @param pdf            a path to a large pdf filled with multiple student
     *                       assignments, in arbitrary order.
     * @param course         the course this pdf was submitted to.
     * @param assignmentName the name of the assignment that this pdf was for
     * @return a list of PDFs that have pdfInput that were associated with
     * students.
     */
    public static ScanningProgress process(
            QRCodeMappingDAO QRCodeMappingDAO,
            AssignmentDAO assignmentDAO,
            Path pdf,
            Course course,
            String assignmentName,
            @Nullable String host
    ) {
        Preconditions.checkNotNull(pdf);
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(assignmentName);
        Preconditions.checkNotNull(QRCodeMappingDAO);

        PDFProcessor processor = new PDFProcessor(
                QRCodeMappingDAO,
                assignmentDAO,
                pdf,
                course,
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
            Multimap<Student, QRCodeMapping> studentsToPages = idToPage.entrySet().parallelStream()
                    .map(scanner::extractQRCodeInformation)
                    .filter(Objects::nonNull)
                    .map(this::associateStudentsWithPage)
                    .collect(MultimapCollector.toMultimap());

            // group pages into assignments based on their students
            Collection<Assignment> assignments = groupIntoAssignments(studentsToPages);

            // if we have remote uploading enabled, upload assignments to the server
            if (host != null) {

                try {
                    AssignmentUploader.uploadAssignments(assignments, host);
                } catch (IOException e) {
                    scanningProgress.addError("Unable to upload assignments to server.");
                    e.printStackTrace();
                }

            }

        };

        new Thread(pipelineTask).start();

        return scanningProgress;
    }

    /**
     * Given a singular UUID, find and update a Placeholder QRCodeMapping into a
     * Valid QRCodeMapping. Create multimap object and populate with student
     * associated with the QRCodeMapping in the argument
     */
    private Multimap<Student, QRCodeMapping> associateStudentsWithPage(QRCodeMapping QRCodeMapping) {
        Preconditions.checkNotNull(QRCodeMapping);
        QRCodeMapping QRCodeMappingFromDB;
        QRCodeMappingFromDB = QRCodeMappingDAO.read(QRCodeMapping.getUuid());
        if(QRCodeMappingFromDB == null){
            return ArrayListMultimap.create();
        } else{
            QRCodeMappingFromDB = mergeMappings(QRCodeMappingFromDB, QRCodeMapping);
            Multimap<Student, QRCodeMapping> mmap = ArrayListMultimap.create();
            mmap.put(
                    QRCodeMappingFromDB.getStudent(),
                    QRCodeMappingFromDB
            );

            return mmap;
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
    private Collection<Assignment> groupIntoAssignments(Multimap<Student, QRCodeMapping> mmap) {
        Set<Assignment> assignments = new HashSet<>();
        for (Map.Entry<Student, Collection<QRCodeMapping>> pages :
                mmap.asMap().entrySet()
                ) {
            Student student = pages.getKey();
            if (student != null) {
                Collection<QRCodeMapping> pagesToAdd = pages.getValue();
                assignments.add(new Assignment(assignmentName, pagesToAdd, student, course, LocalDateTime.now()));
            }
        }
        mergeAssignmentPages(assignments);

        return assignments;
    }

    /**
     * After this is called, all pdfInput for each student are now merged, and saved at the
     * appropriate location. also stores the assignment file location in the database
     */
    private void mergeAssignmentPages(Set<Assignment> assignments) {
        PDFMergerUtility merger = new PDFMergerUtility();
        for (Assignment a : assignments
                ) {
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