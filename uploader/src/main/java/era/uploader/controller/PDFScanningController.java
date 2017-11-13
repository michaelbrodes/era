package era.uploader.controller;

import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.QRCodeMappingDAOImpl;
import era.uploader.data.database.StudentDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.processing.PDFProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * This controller is responsible for taking in PDFs, extracting the UUID
 * embedded in them, and mapping those UUIDs to placeholder pages in the
 * database. This controller will rely heavily on the
 * {@link era.uploader.processing} package.
 *
 *
 */
public class PDFScanningController {
    private final QRCodeMappingDAO qrCodeMappingDAO = QRCodeMappingDAOImpl.instance();
    private final CourseDAO courseDAO = CourseDAOImpl.instance();
    private final StudentDAO studentDAO = StudentDAOImpl.instance();
    private final AssignmentDAO assignmentDAO = AssignmentDAOImpl.instance();

    public Collection<Assignment> scanPDF(Path pdf, Course course, String assignment) throws IOException {
        return PDFProcessor.process(qrCodeMappingDAO, assignmentDAO, pdf, course, assignment, host);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }
}
