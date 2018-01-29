package era.uploader.service;

import era.uploader.common.UploaderProperties;
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
import java.util.List;
import java.util.Optional;

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
            Course course,
            String assignment,
            String host
    ) {
        Optional<Boolean> uploadingEnabled = UploaderProperties
                .instance()
                .isUploadingEnabled();
        if (!uploadingEnabled.orElse(Boolean.FALSE)) {
            host = null;
        }

        return PDFProcessor.process(qrCodeMappingDAO, assignmentDAO, pdf, course, assignment, host);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

}
