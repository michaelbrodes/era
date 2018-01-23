package era.uploader.creation;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.MustBeClosed;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class QRCodePDF implements Closeable {
    private PDDocument pdf;
    private PDPage currentPage;
    private int currentCell;

    QRCodePDF() {
        pdf = new PDDocument();
    }

    @Deprecated
    QRCodePDF(String path) throws IOException {
        pdf = PDDocument.load(new File(path));
        currentPage = pdf.getPage(0);
        currentCell = 0;
    }

    public void nextPage() {
        currentPage = new PDPage();
        currentCell = 0;
        pdf.addPage(currentPage);
    }

    public int nextCell() {
        return currentCell++;
    }

    @MustBeClosed
    public PDPageContentStream getPageEditor() throws IOException {
        return new PDPageContentStream(pdf, currentPage, PDPageContentStream.AppendMode.APPEND, true);
    }

    public PDImageXObject newImage(QRCode qrCode) throws IOException {
        Preconditions.checkNotNull(qrCode);
        return LosslessFactory.createFromImage(pdf, qrCode.getQrCode());
    }

    public int getCurrentCell() {
        return currentCell;
    }

    public float getHeight() {
        return this.currentPage.getMediaBox().getHeight();
    }

    @Override
    public void close() throws IOException {
        pdf.close();
    }

    public void save(String assignmentName) throws IOException {
        Preconditions.checkNotNull(assignmentName);
        pdf.save(new File(assignmentName));
        pdf.close();
    }

    @Override
    protected void finalize() throws Throwable {
        pdf.close();
        super.finalize();
    }
}
