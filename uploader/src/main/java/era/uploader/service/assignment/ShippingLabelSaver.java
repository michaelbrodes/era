package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Saves a collection of {@link QRCode}s into a large pdf of avery labels.
 * This is the <em>gather</em> step of the QR qrcreation process. This class
 * will listen in on the batch {@link QRCreator}s and save their batch as they
 * finish.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public class ShippingLabelSaver extends AbstractQRSaver {

    ShippingLabelSaver(CountDownLatch finishedLatch) {
        super(finishedLatch);
    }

    @VisibleForTesting
    ShippingLabelSaver(QRCodePDF aggregator) {
        super(aggregator);
    }

    /**
     * Calculates the next x coordinate to write a QR code. If the current cell
     * being written to is even then the cell is on the right hand side of the
     * document otherwise it is on the left hand side of the document
     *
     * @see AveryConstants.Shipping#CELLS_PER_ROW
     * @see AveryConstants.Shipping#POINTS_FROM_LEFT_EDGE
     * @see AveryConstants.Shipping#POINTS_TO_RIGHT_CELL
     * @param currentCell the current cell being written to
     * @return the y coordinate of that cell. It is a float because that is
     * required by PDFBox.
     */
    @Override
    protected float calcNextX(int currentCell, float width) {
        if (currentCell % AveryConstants.Shipping.CELLS_PER_ROW == 0) {
            return AveryConstants.Shipping.POINTS_FROM_LEFT_EDGE;
        } else {
            return AveryConstants.Shipping.POINTS_FROM_LEFT_EDGE
                    + AveryConstants.Shipping.POINTS_TO_RIGHT_CELL;
        }
    }

    @Override
    public int numberOfCells() {
        return AveryConstants.Shipping.CELLS_PER_PAGE;
    }

    /**
     * Calculates the y coordinate of the next cell to be written to. 2" by 4"
     * avery shipping labels have an ~50pt gap between the top of the paper
     * and the first label, cells that belong to the same row should have the
     * same y coordinate, and there is an ~150pt gap between the middles of
     * two cells that are vertically adjacent to each other.
     *
     * @param currentCell the current cell to be written to
     * @return the y coordinate of that current cell. It is a float because
     * that is required by PDFBox
     */
    @Override
    protected float calcNextY(int currentCell, float height) {
        return height
                - AveryConstants.Shipping.POINTS_FROM_TOP
                - currentCell
                / AveryConstants.Shipping.CELLS_PER_ROW
                * AveryConstants.Shipping.POINTS_TO_JUMP;
    }

    /**
     * Attaches a single QR Code to the aggregating pdf. It draws the QR code
     * image to the page then writes a description to the right of it.
     */
    @Override
    protected void writeSingleQRCode(
            QRCode qrCode,
            PDPageContentStream editor,
            float xPosition,
            float yPosition
    ) throws IOException {
        Preconditions.checkNotNull(qrCode);
        Preconditions.checkNotNull(qrCode.getQrCode(), "The QR code generated got wiped");
        Preconditions.checkNotNull(editor);

        // BufferedImage of the QR code to a PDImageXObject for the document.
        PDImageXObject pdfQrImage = createQRImage(qrCode);
        editor.drawImage(pdfQrImage, xPosition, yPosition - AveryConstants.Shipping.QR_HEIGHT);

        editor.beginText();
        editor.setLeading(AveryConstants.NEW_LINE_OFFSET);
        editor.setFont(AveryConstants.FONT, AveryConstants.FONT_SIZE);

        editor.newLineAtOffset(
                xPosition + AveryConstants.Shipping.QR_WIDTH + AveryConstants.QR_CODE_TEXT_PADDING,
                yPosition - AveryConstants.Shipping.TEXT_PADDING_FROM_TOP_OF_LABEL
        );
        editor.showText("Student: " + qrCode.getStudentName());

        editor.newLine();
        editor.showText("Course: " + qrCode.getCourseName());

        editor.newLine();
        editor.showText("Assignment: " + qrCode.getAssignmentName());
        editor.newLine();
        editor.showText("Page Number: " + qrCode.getPageNumber());
        editor.endText();
    }

    @Override
    protected void writeHeader(PDPageContentStream editor) {
        // no-op each student has their own name on the label
    }

}
