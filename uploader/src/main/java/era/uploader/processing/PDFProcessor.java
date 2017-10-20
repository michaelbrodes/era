package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final List<PDDocument> pages;
    private final Course course;
    private final String assignmentName;
    private final PageDAO pageDAO;

    PDFProcessor(
            PageDAO pageDao,
            List<PDDocument> pages,
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
     * {@link QRScanner#extractQRCodeInformation(PDDocument)} to grab uuid ->
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
    public static List<Assignment> process(
            PageDAO pageDAO,
            Path pdf,
            Course course,
            String assignmentName
    ) throws IOException {
        Preconditions.checkNotNull(pdf);
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(assignmentName);
        Preconditions.checkNotNull(pageDAO);

        List<PDDocument> pages = TASKalfaConverter.convertFile(pdf);
        PDFProcessor processor = new PDFProcessor(pageDAO, pages, course, assignmentName);

        return processor.startPipeline();
    }

    private List<Assignment> startPipeline() {
        return null;
    }

    /**Given a singular UUID, find and update a Placeholder page into a
     * Valid page. Create multimap object and populate with student
     * associated with the page in the argument
     */
    public Multimap<Student, Page> associateStudentsWithPage(Page page) {
        Page pageFromDB;
        pageFromDB = pageDAO.read(page.getUuid());

        Multimap<Student, Page> mmap = ArrayListMultimap.create();

        mmap.put (
                pageFromDB.getStudent(),
                pageFromDB
        );

        return mmap;
    }
    /**After this is called, we now have a multimap object with each page
     * mapped to a student
     */
    public void addAssignments(Multimap<Student, Page> mmap) {
        Set<Assignment> assignments = new HashSet<>();
        for (Map.Entry<Student, Collection<Page>> pages :
            mmap.asMap().entrySet()
            ) {
            Student student = pages.getKey();
            Set<Page> pagesToAdd = (Set<Page>)pages.getValue();
            assignments.add(new Assignment(assignmentName, pagesToAdd, student, course));
            mergePDF(assignments);
        }
    }

    /**After this is called, all pages for each student are now merged, and saved at the
     * appropriate location. also stores the assignment file location in the database
     */
    public void mergePDF(Set<Assignment> assignments) {
        PDFMergerUtility merger = new PDFMergerUtility();
        AssignmentDAOImpl assignmentDAO = new AssignmentDAOImpl();
        for (Assignment a : assignments
             ) {
            PDDocument allPages = new PDDocument();
            for (Page p : a.getPages()
                    ) {
                try {
                    merger.appendDocument(allPages, p.getDocument()); //merge individual PDDocuments into 1 document
                } catch (java.io.IOException e) {
                    BUS.fire(new QRErrorEvent(QRErrorStatus.MERGE_ERROR));
                }
            }
            try {
                allPages.save(a.getImageFilePath()); //save PDDocument with all pages to server
                assignmentDAO.storeAssignment(a);//store assignment in database
            } catch (java.io.IOException e) {
                    BUS.fire(new QRErrorEvent(QRErrorStatus.SAVE_ERROR));
            }
        }
    }
}
