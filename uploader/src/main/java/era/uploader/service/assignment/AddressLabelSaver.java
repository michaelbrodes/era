package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public class AddressLabelSaver extends AbstractQRSaver {

    AddressLabelSaver(CountDownLatch finishedLatch) {
        super(finishedLatch);
    }

    AddressLabelSaver(CountDownLatch finishedLatch, Student student, Course course) {
        super(finishedLatch, student, course);
    }

    @VisibleForTesting
    AddressLabelSaver(@Nonnull QRCodePDF aggregator) {
        super(aggregator);
    }

    @Override
    protected void writeSingleQRCode(QRCode qrCode, PDPageContentStream editor, float xPosition, float yPosition) throws IOException {

    }

    @Override
    protected void writeHeader(PDPageContentStream editor) {

    }

    @Override
    protected float calcNextY(int currentCell, float height) {
        return 0;
    }

    @Override
    protected float calcNextX(int nextCell, float width) {
        return 0;
    }

    @Override
    public int numberOfCells() {
        return AveryConstants.Address.CELLS_PER_PAGE;
    }
}
