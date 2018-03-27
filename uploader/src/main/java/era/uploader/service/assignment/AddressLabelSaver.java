package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillNotClose;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * The {@link AbstractQRSaver} for 1" by 2.625" address labels.
 */
@ParametersAreNonnullByDefault
public class AddressLabelSaver extends AbstractQRSaver {
    /**
     * Testing constructor to make the saver start with an existing PDF
     * document (good for overlaying QR Codes on top of a template provided by
     * https://avery.com).
     */
    @VisibleForTesting
    AddressLabelSaver(QRCodePDF aggregator, @Nullable Student student, @Nullable Course course) {
        super(new CountDownLatch(Integer.MAX_VALUE), student, course);
        Preconditions.checkNotNull(aggregator);
        this.pdf = aggregator;
    }

    /**
     * Sets the owner and the owner's course for this PDF. We are organizing
     * QR code PDFs by student, so each student will have a pdf of all QR codes
     * they need for a single course.
     */
    AddressLabelSaver(CountDownLatch finishedLatch, @Nullable Student student, @Nullable Course course) {
        super(finishedLatch, student, course);
    }

    /**
     * Writes a single QR code to the PDF as well as the meta data for the QR
     * code. Meta data for address labels include "Course" which is the course
     * name, "Assignment" which is the name of the assignment this QR code is
     * for, and "Page Number" which is the page number on that assignment.
     *
     * @param qrCode the QR code we want to add to the document.
     * @param editor the editor that allows us to write to a page.
     * @param xPosition the x coordinate of where the image should be
     * @param yPosition the y coordinate of where the image should be
     * @throws IOException something went wrong when saving to the PDF
     */
    @Override
    protected void writeSingleQRCode(
            QRCode qrCode,
            @WillNotClose PDPageContentStream editor,
            float xPosition,
            float yPosition
    ) throws IOException {
        Preconditions.checkNotNull(qrCode);
        Preconditions.checkNotNull(qrCode.getQrCode());
        Preconditions.checkNotNull(editor);

        drawQRCode(editor, qrCode, xPosition, yPosition - AveryConstants.Address.QR_HEIGHT);
        beginText(editor, AveryConstants.Address.NEW_LINE_OFFSET, AveryConstants.Address.FONT_SIZE);
        editor.newLineAtOffset(
                xPosition + AveryConstants.Address.QR_WIDTH + AveryConstants.Address.TEXT_MARGIN_LEFT,
                yPosition - AveryConstants.Address.TEXT_MARGIN_TOP
        );

        // The student's name is already in the header so, to save space, we omit it here.
        editor.showText("Name: " + qrCode.getStudentName());
        editor.newLine();

        editor.showText("Assignment: " + qrCode.getAssignmentName());
        editor.newLine();

        editor.showText("Page number: " + qrCode.getPageNumber());
        editor.newLine();

        editor.showText("Student: " + qrCode.getStudentName());
        editor.newLine();

        editor.endText();

    }

    /**
     * Writes the student's name to the top of the PDF document.
     *
     * @param editor the editor that allows us to write to the header.
     * @param height the height of the page
     * @param width the width of the page
     * @throws IOException something went wrong when writing to the PDF.
     */
    @Override
    void writeHeader(@WillNotClose PDPageContentStream editor, float height, float width) throws IOException {
        // Old versions of QRSaverFactory#saver do not supply the owner, so we need to check for null.
//        if (getStudent() != null) {
//            beginText(editor, AveryConstants.Address.NEW_LINE_OFFSET, AveryConstants.Address.FONT_SIZE);
//            editor.newLineAtOffset(
//                    width/2.0f - AveryConstants.Address.HEADER_MARGIN_CENTER,
//                    height - AveryConstants.Address.HEADER_MARGIN_TOP
//            );
//
//            editor.showText("Student: " + getStudent().getLastName() + ", " + getStudent().getFirstName());
//            editor.newLine();
//            editor.endText();
//        }
    }

    @Override
    protected float calcNextY(int nextCell, float height) {
        return height - AveryConstants.Address.POINTS_FROM_TOP
                - nextCell / AveryConstants.Address.CELLS_PER_ROW * AveryConstants.Address.POINTS_TO_NEXT_ROW;
    }

    @Override
    protected float calcNextX(int nextCell, float width) {
        return AveryConstants.Address.POINTS_FROM_LEFT_EDGE
                + nextCell % AveryConstants.Address.CELLS_PER_ROW * AveryConstants.Address.POINTS_TO_NEXT_COLUMN;
    }

    @Override
    public int numberOfCells() {
        return AveryConstants.Address.CELLS_PER_PAGE;
    }
}
