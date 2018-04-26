package era.uploader.service;

import era.uploader.common.UploaderProperties;
import era.uploader.communication.CourseUploader;
import era.uploader.communication.RESTException;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.QRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.service.processing.PDFProcessor;
import era.uploader.service.processing.ScanningProgress;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * This service is responsible for taking in PDFs, extracting the UUID
 * embedded in them, and mapping those UUIDs to placeholder pages in the
 * database. This controller will rely heavily on the
 * {@link era.uploader.service.processing} package.
 */
public class PDFScanningService {
    private final QRCodeMappingDAO qrCodeMappingDAO = QRCodeMappingDAOImpl.instance();
    private final CourseDAO courseDAO = CourseDAOImpl.instance();
    private final AssignmentDAO assignmentDAO = AssignmentDAOImpl.instance();


    /**
     * Takes in a large pdf "pdf", splits it into individual pages, associates
     * students with pages, groups student pages into assignments, and then,
     * if uploading is enabled, uploads each assignment to server.
     */
    public ScanningProgress scanPDF(
            Path pdf,
            Collection<Course> courses,
            String assignment,
            String host
    ) {

        return PDFProcessor.process(qrCodeMappingDAO, assignmentDAO, pdf, courses, assignment, host);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    /**
     * The user could upload assignments for courses that don't currently exist
     * on the server (has happened in the wild). This method checks if the
     * supplied courses exist on the server and, if not, it will upload them.
     *
     * @param currentCourses
     */
    public void createConflictingCourses(List<Course> currentCourses) throws RESTException {
        UploaderProperties uploaderConfiguration = UploaderProperties.instance();
        String serverURL = uploaderConfiguration
                .getServerURL()
                .orElse("http://localhost:3001");
        CourseUploader.uploadBadCourses(currentCourses, serverURL);
    }
}
