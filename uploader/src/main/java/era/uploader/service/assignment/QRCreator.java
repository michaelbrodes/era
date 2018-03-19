package era.uploader.service.assignment;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * A multithreaded QRCode creator. It generates the UUIDs to be stored in the
 * QR code and creates an image of those QR codes. It processes QR Codes in
 * batches based off of how many cpu cores are on the machine generating them.
 */
@ParametersAreNonnullByDefault
public class QRCreator implements Callable<List<QRCode>> {
    private final List<QRCode> qrCodes;
    private final int qrHeight;
    private final int qrWidth;

    public QRCreator(
            // TODO - should be moved over to a list of Assignment objects each with a name and number of pages
            List<QRCode> qrCodes,
            int qrHeight,
            int qrWidth
    ) {
        Preconditions.checkNotNull(qrCodes);

        this.qrCodes = qrCodes;
        this.qrHeight = qrHeight;
        this.qrWidth = qrWidth;
    }

    /**
     * Creates new QR code for a student. The QR code encodes Universally
     * Unique ID. There should be a QR code for every page of every
     * assignment.
     *
     * @return A page object mapping a QR code to a student.
     * @throws Exception Most probably a QR writing exception. Callable makes
     *                   the class throw a general exception
     */
    @Override
    public List<QRCode> call() throws Exception {
        // TODO - iterate through every assignment for that single student
        for (QRCode qrCode: qrCodes ) {
            generateQRCode(qrCode);
        }
        return qrCodes;
    }

    /**
     * Generates a new UUID to unique identify a page of an assignment. Then
     * it encodes that UUID into a QR code. We create a new QRCode object off
     * of the generated UUID, encoded image, student, course, and page number
     * to keep track of the meta data related to a QR code.
     */
    @VisibleForTesting
    QRCode generateQRCode(
        QRCode qrCode
    ) throws WriterException {
        String uuid = UUID.randomUUID().toString();
        qrCode.setUUID(uuid);

        try {
            qrCode.setQRCode(createNewQRCodeImage(uuid));
        } catch (WriterException e) {
            System.err.println("Issue writing QR Code");
            throw e;
        }
        return qrCode;
    }

    /**
     * Encodes the uuid into a QR Code and writes that QR Code to an image.
     *
     * @param uuid universally unique id that identifies a student on an
     *             assignment page.
     * @return an image of a generated QR Code
     * @throws WriterException We had a problem either encoding the UUID or
     *                         converting the QR Code's {@link BitMatrix} to a
     *                         BufferedImage.
     */
    private BufferedImage createNewQRCodeImage(String uuid) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix qrBitMatrix = writer.encode(
                uuid,
                BarcodeFormat.QR_CODE,
                qrWidth,
                qrHeight
        );
        return MatrixToImageWriter.toBufferedImage(qrBitMatrix);
    }
}
