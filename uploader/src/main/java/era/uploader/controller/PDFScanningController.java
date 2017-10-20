package era.uploader.controller;

import era.uploader.data.PageDAO;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.processing.PDFProcessor;

import java.io.IOException;
import java.nio.file.Path;
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
    private final PageDAO pageDAO = new PageDAOImpl();
    public void scanPDF(Path pdf, Course course, String assignment) throws IOException {
        List<Assignment> assignments = PDFProcessor.process(pageDAO, pdf, course, assignment);

    }
}
