package era.uploader.qrcreation;

import com.google.common.annotations.VisibleForTesting;
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
    protected float calcNextY(int currentCell) {
        return 0;
    }

    @Override
    protected float calcNextX(int nextCell) {
        return 0;
    }

    @Override
    public int numberOfCells() {
        return AveryConstants.Address.CELLS_PER_PAGE;
    }
}
