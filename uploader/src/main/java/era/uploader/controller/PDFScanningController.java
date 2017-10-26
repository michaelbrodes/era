package era.uploader.controller;

<<<<<<< HEAD
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
=======
import era.uploader.data.PageDAO;
import era.uploader.data.database.PageDAOImpl;
>>>>>>> 72f8f1bc7567fc474d707f3fa96e336d9cfccbe0
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.processing.PDFProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * This controller is responsible for taking in PDFs, extracting the UUID
 * embedded in them, and mapping those UUIDs to placeholder pages in the
 * database. This controller will rely heavily on the
 * {@link era.uploader.processing} package.
 *
 *
 */
public class PDFScanningController {
    private final PageDAO pageDAO = new PageDAOImpl();
    public void scanPDF(Path pdf, Course course, String assignment) throws IOException {
<<<<<<< HEAD
        //System.out.print("Recieved PDFs");
        List<Assignment> assignments = PDFProcessor.process(pdf, course, assignment);
=======
        List<Assignment> assignments = PDFProcessor.process(pageDAO, pdf, course, assignment);
>>>>>>> 72f8f1bc7567fc474d707f3fa96e336d9cfccbe0

    }

    public Set<Course> getAllCourses() {
        //TODO Method to return all Courses instead of this dummy data
        return ImmutableSet.of(
                Course.create()
                    .withCourseNumber("111")
                    .withName("John Mills")
                    .forDepartment("CHEM")
                    .withSectionNumber("002")
                    .build()
        );
    }
}
