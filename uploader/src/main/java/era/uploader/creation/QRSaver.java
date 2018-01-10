package era.uploader.creation;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import era.uploader.data.model.QRCodeMapping;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

@ParametersAreNullableByDefault
public class QRSaver implements FutureCallback<QRCodeMappingFactory> {
    private final PDDocument aggregator;
    private PDPage currentPage;
    private int currentCell;
    private static final int CELLS_PER_PAGE = 10;

    public QRSaver() {
        aggregator = new PDDocument();
        currentPage = new PDPage();
        currentCell = 1;
        aggregator.addPage(currentPage);
    }

    @Override
    public void onSuccess(@Nullable QRCodeMappingFactory result) {
        Preconditions.checkNotNull(result, "QRCreater#call should never return null");
        // TECHNICALLY QRSaver should only be ran using a direct executor (single thread), but, if not, this operation
        // should be forced to be serial
        synchronized (QRSaver.class) {
            attachQRCodeToDocument(result, 0, 0);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        throw new SaveException(t);
    }

    private int calcNextX() {

    }

    private int calcNextY() {

    }

    private void attachQRCodeToDocument(@Nonnull QRCodeMappingFactory qrCode, double xpos, double ypos) throws IOException {
        Preconditions.checkNotNull(qrCode);
        Preconditions.checkNotNull(qrCode.getQrCode(), "The QR code generated got wiped");
        // start streaming content to the aggregator file
        PDPageContentStream pageStream = new PDPageContentStream(aggregator, currentPage);

        // convert the BitMatrix to a BufferedImage and convert the BufferedImage to a PDImageXObject
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(qrCode.getQrCode());
        PDImageXObject pdfQrImage = LosslessFactory.createFromImage(aggregator, qrImage);

        pageStream.drawImage(pdfQrImage, (float) xpos, (float) ypos);
        pageStream.beginText();
        pageStream.newLineAtOffset(ypos + );
    }
}
