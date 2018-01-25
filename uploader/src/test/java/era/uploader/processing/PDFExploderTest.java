package era.uploader.processing;

import era.uploader.common.IOUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PDFExploderTest {
    @Test
    public void explode_300DPIValidFile() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/300dpi.pdf"));
        PDDocument document = PDDocument.load(pdf.toFile());

        Map<Integer, String> pages = PDFExploder.explodeFile(Optional.of(document));

        assertEquals(5, pages.size());
        Thread.sleep(500);
    }

    @Test
    public void explode_200DPIValidFile() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/200dpi.pdf"));
        PDDocument document = PDDocument.load(pdf.toFile());

        Map<Integer, String> pages = PDFExploder.explodeFile(Optional.of(document));

        assertEquals(5, pages.size());
    }

    @Test
    public void explode_FileNotFound() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/100dpi.pdf"));
        boolean notFound = false;
        Map<Integer, String> pages = null;

        try {
            PDDocument document = PDDocument.load(pdf.toFile());
            pages = PDFExploder.explodeFile(Optional.of(document));
        } catch (IOException e) {
            notFound = true;
        }

        assertTrue(notFound);
    }

    @Test
    @Ignore
    public void explode_OutputSplit() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/300dpi.pdf"));
        PDDocument document = PDDocument.load(pdf.toFile());
        Map<Integer, String> pages = PDFExploder.explodeFile(Optional.of(document));

        for (int i = 0; i < pages.size(); i++) {
            File file = new File(IOUtil.convertToLocal("src/test/resources/split/" + i + ".pdf"));
            try {
                PDDocument.load(new File(pages.get(i))).save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}