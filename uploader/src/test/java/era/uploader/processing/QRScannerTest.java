package era.uploader.processing;

import era.uploader.data.model.Page;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class QRScannerTest {

    public static final String path = "src" + File.separator + "test"+ File.separator + "resources"+ File.separator +"single-page_300dpi.pdf";

    @Test
    public void extractQRCodeInformation() throws Exception {
        QRScanner qrScanner = new QRScanner();
        Page scannedPage = qrScanner.extractQRCodeInformation(path);
        Assert.assertEquals(scannedPage.getUuid(), "6ab251a5-6c4e-4688-843f-60aea570c3a6");
    }

}