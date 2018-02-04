package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A general <em>Strategy</em> for saving a list QR codes into a PDF of avery
 * labels. This is the "gather" step in the QR creation process. Each QRSaver
 * will wait on a corresponding {@link QRCreator#call()} to finish creating QR
 * codes and then saves all those QR codes into a large pdf.
 * {@link #writeBatch(List)} handles this by looping through the returned QR
 * codes and delegating to {@link #writeSingleQRCode(QRCode, PDPageContentStream, float, float)}
 * to save them individually. Subclasses are responsible for calculating where
 * to save the next QR code based on cell position.
 *
 * Use {@link QRSaverFactory} to create new Savers.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractQRSaver implements FutureCallback<List<QRCode>> {
    protected QRCodePDF pdf;
    private final CountDownLatch finishedLatch;

    private final Course course;

    private final Student student;

    public AbstractQRSaver(CountDownLatch finishedLatch) {
        this.finishedLatch = finishedLatch;
        this.pdf = new QRCodePDF(numberOfCells());
        this.student = null;
        this.course = null;
    }

    public AbstractQRSaver(CountDownLatch finishedLatch, Student student, Course course) {
        this.finishedLatch = finishedLatch;
        this.pdf = new QRCodePDF(numberOfCells());
        this.student = student;
        this.course = course;
    }

    @VisibleForTesting
    AbstractQRSaver(@Nonnull @WillClose QRCodePDF aggregator) {
        Preconditions.checkNotNull(aggregator);
        this.pdf = aggregator;
        // will never be used by an outside source.
        this.finishedLatch = new CountDownLatch(Integer.MAX_VALUE);
        this.student = null;
        this.course = null;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    /**
     * Writes a batch of QR Codes resulting from a successful
     * {@link QRCreator#call()}
     */
    @Override
    public final void onSuccess(@Nullable List<QRCode> result) {
        if (result != null) {
            try {
                writeBatch(result);
            } catch (IOException e) {
                throw new SaveException(e);
            }
        } else {
            System.err.println("QRCreator is returning null batches. Look into this.");
        }

        finishedLatch.countDown();
    }

    /**
     * Writes a single batch of qr codes to the aggregating pdf. It resets
     * {@link QRCodePDF#currentCell}
     *
     * @param batch a batch of qr codes for a particular student.
     * @throws IOException something bad happened when writing qr codes.
     */
    private void writeBatch(List<QRCode> batch) throws IOException {
        Preconditions.checkNotNull(batch);
        // start streaming content to the aggregator file
        PDPageContentStream editor = pdf.getPageEditor();
        writeHeader(editor, pdf.getHeight(), pdf.getWidth());

        for (QRCode code : batch) {
            float x = calcNextX(pdf.getCurrentCell(), pdf.getWidth());
            float y = calcNextY(pdf.getCurrentCell(), pdf.getHeight());
            writeSingleQRCode(code, editor, x, y);
            pdf.nextCell();

            // new page so update the editor
            if (pdf.getCurrentCell() >= pdf.getCellsPerPage()) {
                editor = pdf.nextPage(editor);
            }
        }
        editor.close();
    }

    /**
     * Throws a {@link RuntimeException} that says that the QRCodes can't be
     * saved. This method should never be called, but if it does, we want to
     * be notified.
     *
     * @param t the reason we failed
     */
    @Override
    public final void onFailure(Throwable t) {
        throw new SaveException(t);
    }

    /**
     * Helper method to create a new {@link PDImageXObject} that should be
     * inserted in the PDF. It just delegates to
     * {@link QRCodePDF#newImage(QRCode)}, but we have it because we don't
     * want to expose the actually PDF instance variable to the subclasses.
     *
     * @param code the QRCode that we need an image of
     * @throws IOException something bad happened when creating a new Image
     */
    @SuppressWarnings("WeakerAccess")
    protected void drawQRCode(
            PDPageContentStream editor,
            QRCode code,
            float x,
            float y
    ) throws IOException {
        Preconditions.checkNotNull(editor);

        PDImageXObject qrImage = pdf.newImage(code);
        editor.drawImage(qrImage, x, y);
    }

    /**
     * Change the editor to "Text Mode" and setup the new line offset and font size.
     */
    @SuppressWarnings("WeakerAccess")
    protected void beginText(PDPageContentStream editor, float newLineOffset, float fontSize) throws IOException {
        Preconditions.checkNotNull(editor);

        editor.beginText();
        editor.setLeading(newLineOffset);
        editor.setFont(AveryConstants.FONT, fontSize);
    }

    /**
     * Saves and closes the aggregating PDF using the supplied assignmentName.
     */
    public void save(String assignmentName) throws IOException {
        Preconditions.checkNotNull(assignmentName);

        final Path path = Paths.get(assignmentName);
        if(!Files.exists(path.getParent())){
            Files.createDirectory(path.getParent());
        }
        pdf.save(assignmentName);
    }

    /**
     * Attaches a single QR code to the aggregating document at position
     * ("xPosition", "yPosition").
     *
     * @param qrCode the QR code we want to add to the document.
     * @param editor the editor that allows us to write to a page.
     * @param xPosition the x coordinate of where the image should be
     * @param yPosition the y coordinate of where the image should be
     * @throws IOException there was a problem interacting with the PDF
     */
    protected abstract void writeSingleQRCode(
            QRCode qrCode,
            @WillNotClose PDPageContentStream editor,
            float xPosition,
            float yPosition
    ) throws IOException;

    /**
     * What should be written at the top of every page in the document. This
     * will usually be the students name or other student/course meta data
     *
     * @param editor the editor that allows us to write to the header.
     */
    @VisibleForTesting
    abstract void writeHeader (@WillNotClose PDPageContentStream editor, float height, float width) throws IOException;

    /**
     * Calculates the y coordinate of the next cell (label) to be written to
     * on the PDF.
     *
     * @param nextCell the current cell to be written to
     * @return the y coordinate of that current cell. It is a float because
     * that is required by PDFBox
     * @see QRCodePDF for the class that wraps the meta data of a PDF.
     */
    protected abstract float calcNextY(int nextCell, float height);

    /**
     * Calculates the x coordinate of the next cell (label) to be written to
     * on the PDF.
     *
     * @param nextCell the next cell to be written to
     * @return the x coordinate of that cell. It is a float because that is
     * required by PDFBox
     * @see QRCodePDF for the class that wraps the meta data of a PDF.
     */
    protected abstract float calcNextX(int nextCell, float width);

    /**
     * The number of cells on a single page of {@link #pdf}
     */
    public abstract int numberOfCells();

}
