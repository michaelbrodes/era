package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.sun.org.apache.xpath.internal.operations.Mult;
import era.uploader.controller.StatusChangeBus;
import era.uploader.creation.QRErrorBus;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import era.uploader.data.model.Page;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final List<PDDocument> pages;
    private final Course course;
    private final String assignmentName;
    private PageDAOImpl pageDAO;

    PDFProcessor(List<PDDocument> pages, Course course, String assignmentName) {
        this.pages = pages;
        this.course = course;
        this.assignmentName = assignmentName;
    }

    public static List<Assignment> process(Path pdf, Course course, String assignmentName)
            throws IOException {
        Preconditions.checkNotNull(pdf);
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(assignmentName);

        List<PDDocument> pages = TASKalfaConverter.convertFile(pdf);
        PDFProcessor processor = new PDFProcessor(pages, course, assignmentName);

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
