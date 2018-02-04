package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillClose;
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

    ShippingLabelSaver(CountDownLatch finishedLatch, Student student, Course course) {
        super(finishedLatch, student, course);
    }

   @VisibleForTesting
   ShippingLabelSaver(@WillClose QRCodePDF aggregator) {
        super(new CountDownLatch(Integer.MAX_VALUE));
        Preconditions.checkNotNull(aggregator);
        this.pdf = aggregator;
    }
    /**
     * Calculates the next x coordinate to write a QR code. If the current cell
     * being written to is even then the cell is on the right hand side of the
     * document otherwise it is on the left hand side of the document
     *
     * @see AveryConstants.Shipping#CELLS_PER_ROW
     * @see AveryConstants.Shipping#POINTS_FROM_LEFT_EDGE
     * @see AveryConstants.Shipping#POINTS_TO_NEXT_COLUMN
     * @param nextCell the current cell being written to
     * @return the y coordinate of that cell. It is a float because that is
     * required by PDFBox.
     */
    @Override
    protected float calcNextX(int nextCell, float width) {
        if (nextCell % AveryConstants.Shipping.CELLS_PER_ROW == 0) {
            return AveryConstants.Shipping.POINTS_FROM_LEFT_EDGE;
        } else {
            return AveryConstants.Shipping.POINTS_FROM_LEFT_EDGE
                    + AveryConstants.Shipping.POINTS_TO_NEXT_COLUMN;
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
     * @param nextCell the current cell to be written to
     * @return the y coordinate of that current cell. It is a float because
     * that is required by PDFBox
     */
    @Override
    protected float calcNextY(int nextCell, float height) {
        return height
                - AveryConstants.Shipping.POINTS_FROM_TOP
                - nextCell
                / AveryConstants.Shipping.CELLS_PER_ROW
                * AveryConstants.Shipping.POINTS_TO_NEXT_ROW;
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

        drawQRCode(editor, qrCode, xPosition, yPosition - AveryConstants.Shipping.QR_HEIGHT);

        beginText(editor, AveryConstants.Shipping.NEW_LINE_OFFSET, AveryConstants.Shipping.FONT_SIZE);
        editor.newLineAtOffset(
                xPosition + AveryConstants.Shipping.QR_WIDTH + AveryConstants.Shipping.TEXT_MARGIN_LEFT,
                yPosition - AveryConstants.Shipping.TEXT_MARGIN_TOP
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
    protected void writeHeader(PDPageContentStream editor, float height, float width) {
        // no-op each student has their own name on the label
    }

}
