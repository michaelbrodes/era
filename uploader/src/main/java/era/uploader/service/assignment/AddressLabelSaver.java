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

@ParametersAreNonnullByDefault
public class AddressLabelSaver extends AbstractQRSaver {
    private final Student owner;

    @VisibleForTesting
    AddressLabelSaver(QRCodePDF aggregator, @Nullable Student student, @Nullable Course course) {
        super(new CountDownLatch(Integer.MAX_VALUE));
        Preconditions.checkNotNull(aggregator);
        this.pdf = aggregator;
        this.owner = student;
    }

    AddressLabelSaver(CountDownLatch finishedLatch, @Nullable Student student, @Nullable Course course) {
        super(finishedLatch);
        this.owner = student;
    }

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
        editor.showText("Course: " + qrCode.getCourseName());
        editor.newLine();

        editor.showText("Assignment: " + qrCode.getAssignmentName());
        editor.newLine();

        editor.showText("Page number: " + qrCode.getPageNumber());
        editor.newLine();

        editor.endText();

    }

    @Override
    void writeHeader(@WillNotClose PDPageContentStream editor, float height, float width) throws IOException {
        if (owner != null) {
            beginText(editor, AveryConstants.Address.NEW_LINE_OFFSET, AveryConstants.Address.FONT_SIZE);
            editor.newLineAtOffset(
                    width/2.0f - AveryConstants.Address.HEADER_MARGIN_CENTER,
                    height - AveryConstants.Address.HEADER_MARGIN_TOP
            );

            editor.showText("Student: " + owner.getLastName() + ", " + owner.getFirstName());
            editor.newLine();
            editor.endText();
        }
    }

    @Override
    protected float calcNextY(int currentCell, float height) {
        return height - AveryConstants.Address.POINTS_FROM_TOP
                - currentCell / AveryConstants.Address.CELLS_PER_ROW * AveryConstants.Address.POINTS_TO_NEXT_ROW;
    }

    @Override
    protected float calcNextX(int currentCell, float width) {
        return AveryConstants.Address.POINTS_FROM_LEFT_EDGE
                + currentCell % AveryConstants.Address.CELLS_PER_ROW * AveryConstants.Address.POINTS_TO_NEXT_COLUMN;
    }

    @Override
    public int numberOfCells() {
        return AveryConstants.Address.CELLS_PER_PAGE;
    }
}
