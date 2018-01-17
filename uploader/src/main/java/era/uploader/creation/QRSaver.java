package era.uploader.creation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.WillClose;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.List;

/**
 * Saves a collection of {@link QRCode}s into a large pdf of avery labels.
 * This is the <em>gather</em> step of the QR creation process. This class
 * will listen in on the batch {@link QRCreator}s and save their batch as they
 * finish.
 *
 * Despite listening in on multithreaded callables this class itself is
 * <strong>NOT</strong> meant to be used in a multithreaded environment. It
 * will actually force serial execution using its intrinsic in
 * {@link #onSuccess(List)}.
 */
@NotThreadSafe
@ParametersAreNullableByDefault
public class QRSaver implements FutureCallback<List<QRCode>> {
    private QRCodePDF pdf;

    public QRSaver() {
        pdf = new QRCodePDF();
    }

    @VisibleForTesting
    QRSaver(@Nonnull @WillClose QRCodePDF aggregator) {
        Preconditions.checkNotNull(aggregator);
        this.pdf = aggregator;
    }

    /**
     * Once a {@link QRCreator} finishes creating a batch of qr codes this
     * method picks up that batch and saves it to an aggregating PDF.  First
     * it partitions the batch into pages and then it writes those pages.
     *
     * @param result the result of a {@link QRCreator#call()} call. It should
     *               never be null. Guava just enforces the nullable
     *               annotation.
     */
    @Override
    public void onSuccess(@Nullable List<QRCode> result) {
        Preconditions.checkNotNull(result, "QRCreater#call should never return null");
        // TECHNICALLY QRSaver should only be ran using a direct executor (single thread), but, if not, this operation
        // should be forced to be serial
        synchronized (QRSaver.class) {
            try {
                for (List<QRCode> page : partitionIntoPages(result)) {
                    writePage(page);
                }
            } catch (IOException e) {
                throw new SaveException(e);
            }
        }
    }

    /**
     * Partitions the batch into individual pages based off the number of
     * cells in a page. Essentially it sublists the list of QR codes into
     * chunks of CELLS_PER_PAGE length. The last page will potentially have
     * less than CELLS_PER_PAGE QR codes.
     *
     * @see Lists#partition(List, int)
     */
    private List<List<QRCode>> partitionIntoPages(@Nonnull List<QRCode> batch) {
        Preconditions.checkNotNull(batch);
        return Lists.partition(batch, AveryConstants.CELLS_PER_PAGE);
    }


    /**
     * If the {@link QRCreator#call()} call fails we should throw a
     * RuntimeException
     */
    @Override
    public void onFailure(Throwable t) {
        throw new SaveException(t);
    }

    /**
     * Calculates the next x coordinate to write a QR code. If the current cell
     * being written to is even then the cell is on the right hand side of the
     * document otherwise it is on the left hand side of the document
     *
     * @see AveryConstants#CELLS_PER_ROW
     * @see AveryConstants#POINTS_FROM_LEFT_EDGE
     * @see AveryConstants#POINTS_TO_RIGHT_CELL
     * @param currentCell the current cell being written to
     * @return the y coordinate of that cell. It is a float because that is
     * required by PDFBox.
     */
    private float calcNextX(int currentCell) {
        if (currentCell % AveryConstants.CELLS_PER_ROW == 0) {
            return AveryConstants.POINTS_FROM_LEFT_EDGE;
        } else {
            return AveryConstants.POINTS_FROM_LEFT_EDGE + AveryConstants.POINTS_TO_RIGHT_CELL;
        }
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
    private float calcNextY(int currentCell) {
        return pdf.getHeight()
                - AveryConstants.POINTS_FROM_TOP
                - currentCell
                / AveryConstants.CELLS_PER_ROW
                * AveryConstants.POINTS_TO_JUMP;
    }

    /**
     * Writes a single page of qr codes to the aggregating pdf. It resets
     * {@link QRCodePDF#currentCell} and creates a new
     * {@link QRCodePDF#currentPage}
     *
     * @param qrCodes page of qr codes to be written to
     * @throws IOException something bad happened when writing qr codes.
     */
    private void writePage(@Nonnull List<QRCode> qrCodes) throws IOException {
        Preconditions.checkNotNull(qrCodes);
        pdf.nextPage();
        // start streaming content to the aggregator file
        PDPageContentStream editor = pdf.getPageEditor();

        for (QRCode code : qrCodes) {
            float x = calcNextX(pdf.getCurrentCell());
            float y = calcNextY(pdf.getCurrentCell());
            attachQRCodeToDocument(code, editor, x, y);
            pdf.nextCell();
        }
        editor.close();
    }

    /**
     * Attaches a single QR Code to the aggregating pdf. It draws the QR code
     * image to the page then writes a description to the right of it.
     */
    private void attachQRCodeToDocument(
            @Nonnull QRCode qrCode,
            @Nonnull PDPageContentStream editor,
            float xPosition,
            float yPosition
    ) throws IOException {
        Preconditions.checkNotNull(qrCode);
        Preconditions.checkNotNull(qrCode.getQrCode(), "The QR code generated got wiped");
        Preconditions.checkNotNull(editor);

        // BufferedImage of the QR code to a PDImageXObject for the document.
        PDImageXObject pdfQrImage = pdf.newImage(qrCode);
        editor.drawImage(pdfQrImage, xPosition, yPosition - AveryConstants.QR_HEIGHT);

        editor.beginText();
        editor.setLeading(AveryConstants.NEW_LINE_OFFSET);
        editor.setFont(AveryConstants.FONT, AveryConstants.FONT_SIZE);

        editor.newLineAtOffset(
                xPosition + AveryConstants.QR_WIDTH + AveryConstants.QR_CODE_TEXT_PADDING,
                yPosition - AveryConstants.TEXT_PADDING_FROM_TOP
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

    /**
     * Saves and closes the aggregating PDF using the supplied assignmentName.
     */
    public void save(@Nonnull String assignmentName) throws IOException {
        pdf.save(assignmentName);
    }
}
