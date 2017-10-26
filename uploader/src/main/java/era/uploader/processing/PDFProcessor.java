package era.uploader.processing;

import com.google.common.base.Preconditions;
import era.uploader.controller.StatusChangeBus;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@ParametersAreNonnullByDefault
public class PDFProcessor {
    private static final StatusChangeBus BUS = StatusChangeBus.instance();
    private final List<PDDocument> pages;
    private final Course course;
    private final String assignmentName;

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
}
