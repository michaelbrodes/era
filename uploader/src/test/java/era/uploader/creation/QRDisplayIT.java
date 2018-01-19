package era.uploader.creation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class QRDisplayIT {
    public static final String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "QRTest_1";

    @Test
    public void QRDisplayTest() throws Exception {
        //test to find out if the UUID that is encoded into the QR Code is done so properly and shows up on a visualized QR Code
        Student robMcGuy = Student.builder()
                .withSchoolId("800999999")
                .withFirstName("Rob")
                .withLastName("McGuy")
                .create("rmcguy");
        Course course = Course.builder()
                .withStudents(ImmutableSet.of(robMcGuy))
                .withName("ebrbrbrbrbrbrbrb")
                .create("CS", "111", "001");
        int sequenceNumber = 1;
        QRCreator creator = new QRCreator(course, Collections.singletonList(robMcGuy),"", sequenceNumber);

        QRCode robsQRCode = Iterables.getOnlyElement(creator.call());
        String uniqueOutputPath = path + " " + LocalDateTime.now() + ".png";

        try {
            BufferedImage bufferedImage = robsQRCode.getQrCode();
            assertTrue(bufferedImage != null);
            File outputFile = new File(uniqueOutputPath);
            ImageIO.write(bufferedImage, "png", outputFile);

        } catch (IOException e) {
            System.out.println("Encountered error: " + e);
            assertTrue(false);
        }

        QRCodeMapping robsQRCodeMapping = robsQRCode.createQRCodeMapping();
        System.out.println("UUID: " + robsQRCodeMapping.getUuid());

        //Test to find out if stored qr code is the same as the one generated
        InputStream is = new FileInputStream(uniqueOutputPath);
        BufferedImage readImage = ImageIO.read(is);

        LuminanceSource tmpSource = new BufferedImageLuminanceSource(readImage);
        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
        MultiFormatReader tmpQRcodeReader = new MultiFormatReader();
        Result tmpResult;
        String tmpFinalResult;

        tmpResult = tmpQRcodeReader.decode(tmpBitmap);

        tmpFinalResult = String.valueOf(tmpResult.getText());

        assertEquals(robsQRCodeMapping.getUuid(), tmpFinalResult);

    }


}
