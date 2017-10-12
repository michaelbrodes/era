package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.sun.org.apache.xpath.internal.operations.Mult;
import era.uploader.controller.StatusChangeBus;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import era.uploader.data.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private static final StatusChangeBus BUS = StatusChangeBus.instance();
    private final List<PDDocument> pages;
    private final Course course;
    private final String assignmentName;
    private PageDAOImpl pageDAO;

    private PDFProcessor(List<PDDocument> pages, Course course, String assignmentName) {
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

    /* Given a singular UUID, find and update a Placeholder page into a
     * Valid page
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
    public void addAssignments(Multimap<Student, Page> mmap) {
        Set<Assignment> assignments = new HashSet<>();
        for (Map.Entry<Student, Collection<Page>> pages :
            mmap.asMap().entrySet()
            ) {
            Student student = pages.getKey();
            Set<Page> pagesToAdd = (Set<Page>)pages.getValue();

            assignments.add(new Assignment(assignmentName, pagesToAdd, student, course));
        }
        mmap.asMap().entrySet();

    }
}
