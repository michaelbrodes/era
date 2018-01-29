package era.uploader.service.processing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.service.processing.QRScanner;
import era.uploader.service.processing.ScanningProgress;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class QRScannerTest {

    public static final String path = "src" + File.separator + "test"+ File.separator + "resources"+ File.separator + "test-pdfs" + File.separator +"single-page_300dpi.pdf";

    @Test
    public void extractQRCodeInformation() {
        QRScanner qrScanner = new QRScanner(new ScanningProgress());
        ImmutableMap<Integer, String> idsToDocuments = ImmutableMap.of(0, path);
        Map.Entry<Integer, String> idToDocument = Iterables.getOnlyElement(idsToDocuments.entrySet());
        QRCodeMapping scannedQRCodeMapping = qrScanner.extractQRCodeInformation(idToDocument);
        Assert.assertEquals(scannedQRCodeMapping.getUuid(), "6ab251a5-6c4e-4688-843f-60aea570c3a6");
    }

}