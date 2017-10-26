package era.uploader.creation;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QRDisplayIT {
    public static final String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "QRTest_1.png";

    @Test
    public void QRDisplayTest() throws Exception {
        //test to find out if the UUID that is encoded into the QR Code is done so properly and shows up on a visualized QR Code
        Student robMcGuy = Student.builder().withSchoolId("rmcguy").withFirstName("Rob").withLastName("McGuy").create();
        int sequenceNumber = 1;
        QRCreator creator = new QRCreator(robMcGuy, sequenceNumber);

        Page robsPage = creator.call();

        try {
            BitMatrix byteMatrix = robsPage.getQrCode();
            assertTrue(byteMatrix != null);
            MatrixToImageWriter.writeToPath(byteMatrix, "PNG", Paths.get(path));

        } catch (IOException e) {
            System.out.println("Encountered error: " + e);
            assertTrue(false);
        }
        System.out.println("Hash: " + robsPage.getUuid());

        //Test to find out if stored qr code is the same as the one generated
        InputStream is = new FileInputStream(QRDisplayIT.path);
        BufferedImage readImage = ImageIO.read(is);

        LuminanceSource tmpSource = new BufferedImageLuminanceSource(readImage);
        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
        MultiFormatReader tmpQRcodeReader = new MultiFormatReader();
        Result tmpResult;
        String tmpFinalResult;

        tmpResult = tmpQRcodeReader.decode(tmpBitmap);

        tmpFinalResult = String.valueOf(tmpResult.getText());

        assertEquals(robsPage.getUuid(), tmpFinalResult);

    }


}
