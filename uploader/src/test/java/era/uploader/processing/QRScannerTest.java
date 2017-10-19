package era.uploader.processing;

import era.uploader.data.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class QRScannerTest {

    public static final String path = "src" + File.separator + "test"+ File.separator + "resources"+ File.separator +"single-page_300dpi.pdf";

    @Test
    public void convertPDFtoBufferedImg() throws Exception {
        PDDocument testScan = PDDocument.load(new File(path));
        QRScanner qrScanner = new QRScanner();
        Page scannedPage = qrScanner.convertPDFtoBufferedImg(testScan);
        Assert.assertEquals(scannedPage.getUuid(), "6ab251a5-6c4e-4688-843f-60aea570c3a6");
        Assert.assertEquals(testScan,scannedPage.getDocument());
    }

}