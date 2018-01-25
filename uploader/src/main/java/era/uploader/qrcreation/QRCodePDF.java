package era.uploader.qrcreation;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.MustBeClosed;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillClose;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class QRCodePDF implements Closeable {
    private PDDocument pdf;
    private PDPage currentPage;
    private int currentCell;
    private final int cellsPerPage;

    QRCodePDF(final int cellsPerPage) {
        this.cellsPerPage = cellsPerPage;
        this.pdf = new PDDocument();
        this.currentPage = new PDPage();
        pdf.addPage(currentPage);
    }

    public PDPageContentStream nextPage(@WillClose PDPageContentStream editor) throws IOException{
        editor.close();
        currentPage = new PDPage();
        currentCell = 0;
        pdf.addPage(currentPage);
        return getPageEditor();
    }

    public void nextCell() {
        currentCell++;
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

    public int getCellsPerPage() {
        return cellsPerPage;
    }
}
