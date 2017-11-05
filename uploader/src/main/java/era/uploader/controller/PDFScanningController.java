package era.uploader.controller;

import com.google.common.collect.ImmutableSet;
import era.uploader.data.PageDAO;
import era.uploader.data.database.PageDAOImpl;
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

        List<Assignment> assignments = PDFProcessor.process(pageDAO, pdf, course, assignment);

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
