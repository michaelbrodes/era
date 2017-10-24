package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import era.uploader.common.MultimapCollector;
import era.uploader.creation.QRErrorBus;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.PageDAO;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final List<String> pages;
    private final Course course;
    private final String assignmentName;
    private final PageDAO pageDAO;

    PDFProcessor(
            PageDAO pageDao,
            List<String> pages,
            Course course,
            String assignmentName
    ) {
        this.pages = pages;
        this.course = course;
        this.assignmentName = assignmentName;
        this.pageDAO = pageDao;
    }

    /**
     * Entry point into the {@link era.uploader.processing} package. It is our
     * PDF processing algorithm (i.e. the algorithm that matches students to
     * pages in an inputted PDF). The algorithm is heavily based upon
     * MapReduce. Map reduce allows for high concurrency by creating a pipeline
     * of sorts, while also keeping logic modularized. The stages of the
     * pipeline are Spit a large pdf into pages -> scatter those pages with
     * {@link List#parallelStream()} -> feed them into
     * {@link QRScanner#extractQRCodeInformation(String)} to grab uuid ->
     * match {@link #associateStudentsWithPage(Page)} -> merge student
     * associated Pages into one pdf -> store in either the local database or
     * the remote database.
     *
     * @param pdf a path to a large pdf filled with multiple student
     *            assignments, in arbitrary order.
     * @param course the course this pdf was submitted to.
     * @param assignmentName the name of the assignment that this pdf was for
     * @return a list of PDFs that have pages that were associated with
     *         students.
     * @throws IOException couldn't find the pdf from the path specified.
     */
    public static Collection<Assignment> process(
            PageDAO pageDAO,
            Path pdf,
            Course course,
            String assignmentName
    ) throws IOException {
        Preconditions.checkNotNull(pdf);
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(assignmentName);
        Preconditions.checkNotNull(pageDAO);

        List<String> pages = TASKalfaConverter.convertFile(pdf);
        PDFProcessor processor = new PDFProcessor(pageDAO, pages, course, assignmentName);

        return processor.startPipeline();
    }

    /**
     * The pipeline for scanning qr codes off of assignment pages to find which
     * students they belong to. Essentially we are provided a list of
     * {@link PDDocument} which represents all the pages that belong to all
     * the submissions for the assignment with the name {@link #assignmentName}.
     * For each of those pages we pull out the QR code attached to the page,
     * then we match the UUID on that QR code to a student, then put those
     * matches into a {@link Multimap} that groups the Pages by student, and
     * then finally we merge all pages together into an {@link Assignment}
     * object and store it in the database.
     *
     * @return the assignment submissions we generated during this run.
     */
    private Collection<Assignment> startPipeline() {
        QRScanner scanner = new QRScanner();
        Multimap<Student, Page> collect = pages.parallelStream()
                .map(scanner::extractQRCodeInformation)
                .filter(Objects::nonNull)
                .map(this::associateStudentsWithPage)
                .collect(MultimapCollector.toMultimap());

        return addAssignments(collect);
    }

    /**Given a singular UUID, find and update a Placeholder page into a
     * Valid page. Create multimap object and populate with student
     * associated with the page in the argument
     */
    public Multimap<Student, Page> associateStudentsWithPage(Page page) {
        Preconditions.checkNotNull(page);
        Page pageFromDB;
        // TODO: use loading cache so we don't get weird magic happening because we are accessing the db concurrently
        pageFromDB = pageDAO.read(page.getUuid());
        pageFromDB = merge(pageFromDB, page);

        Multimap<Student, Page> mmap = ArrayListMultimap.create();

        mmap.put (
                pageFromDB.getStudent(),
                pageFromDB
        );

        return mmap;
    }

    private Page merge(Page dbPage, Page scannedPage) {
        dbPage.setTempDocumentName(scannedPage.getTempDocumentName());
        dbPage.setDocument(scannedPage.getDocument());

        return dbPage;
    }
    /**After this is called, we now have a multimap object with each page
     * mapped to a student
     */
    public Collection<Assignment> addAssignments(Multimap<Student, Page> mmap) {
        Set<Assignment> assignments = new HashSet<>();
        for (Map.Entry<Student, Collection<Page>> pages :
            mmap.asMap().entrySet()
            ) {
            Student student = pages.getKey();
            Collection<Page> pagesToAdd = pages.getValue();
            assignments.add(new Assignment(assignmentName, pagesToAdd, student, course));
            mergeAssignmentPages(assignments);
        }

        return assignments;
    }

    /**After this is called, all pages for each student are now merged, and saved at the
     * appropriate location. also stores the assignment file location in the database
     */
    public void mergeAssignmentPages(Set<Assignment> assignments) {
        PDFMergerUtility merger = new PDFMergerUtility();
        AssignmentDAOImpl assignmentDAO = new AssignmentDAOImpl();
        for (Assignment a : assignments
             ) {
            PDDocument allPages = new PDDocument();
            for (Page p : a.getPages()
                    ) {
                try {
                    PDDocument document;
                    if (p.getDocument() == null) {
                        document = PDDocument.load(new File(p.getTempDocumentName()));
                    } else {
                        document = p.getDocument();
                    }
                    merger.appendDocument(allPages, document); //merge individual PDDocuments into 1 document
                    document.close();
                    if (p.getTempDocumentName() != null) {
                        Files.delete(Paths.get(p.getTempDocumentName()));
                    }
                } catch (java.io.IOException e) {
                    BUS.fire(new QRErrorEvent(QRErrorStatus.MERGE_ERROR));
                }
            }
            try {
                allPages.save(a.getImageFilePath()); //save PDDocument with all pages to server
                assignmentDAO.storeAssignment(a);//store assignment in database
                allPages.close();
            } catch (java.io.IOException e) {
                    BUS.fire(new QRErrorEvent(QRErrorStatus.SAVE_ERROR));
            }
        }
    }
}
