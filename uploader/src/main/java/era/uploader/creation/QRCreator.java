package era.uploader.creation;

import com.google.common.base.Preconditions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * A multithreaded QRCode creator. It creates a QR code out of a hash of the
 * combination of {@link Student#schoolId} and a unique sequence number.
 */
@ParametersAreNonnullByDefault
public class QRCreator implements Callable<Page> {
    private static final int QR_HEIGHT = 100;
    private static final int QR_WIDTH = 100;
    private static final QRErrorBus BUS = QRErrorBus.instance();
    private final Student student;
    private final int sequenceNumber;

    public QRCreator(Student student, int sequenceNumber) {
        Preconditions.checkNotNull(student);
        this.student = student;
        this.sequenceNumber = sequenceNumber;
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
    public Page call() throws Exception {
        String uuid = UUID.randomUUID().toString();
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix qrCode = null;
        try {
            qrCode = writer.encode(
                    uuid,
                    BarcodeFormat.QR_CODE,
                    QR_WIDTH,
                    QR_HEIGHT
            );
        } catch (WriterException e) {
            QRErrorStatus status = QRErrorStatus.GENERATION_ERROR;
            BUS.fire(new QRErrorEvent(status, student));
        }

        return Page.builder()
                .withUuid(uuid)
                .withQRCode(qrCode)
                .withSequenceNumber(sequenceNumber)
                .byStudent(student)
                .create();
    }
}
