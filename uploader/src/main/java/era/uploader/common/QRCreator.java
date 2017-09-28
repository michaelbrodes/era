package era.uploader.common;

import com.google.common.collect.ImmutableMultimap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import era.uploader.data.model.Student;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.concurrent.Callable;

/**
 * A multithreaded QRCode creator. It creates a QR code out of a hash of the
 * combination of {@link Student#schoolId} and a unique sequence number.
 */
public class QRCreator implements Callable<ImmutableMultimap<Student, BitMatrix>> {
    public static final int QR_HEIGHT = 100;
    public static final int QR_WIDTH = 100;
    private static final int SALT_LEN = 8;
    private static final int STRETCH_ITER = 100;
    private static final int KEY_LENGTH = 256;
    private static final String HASH_ALGO = "PBKDF2WithHmacSHA512";
    private final Student student;
    private final int sequenceNumber;

    public QRCreator(Student student, int sequenceNumber) {
        this.student = student;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Hashes a QR id using PBKDF2WithHmacSHA512.
     *
     * @param schoolId       student's external school Id.
     * @param sequenceNumber the unique id for the QR code we are generating
     * @return the hash of the student's schoolId with the salt and sequenceNumber
     */
    public String hash(String schoolId, int sequenceNumber) {
        byte[] salt = new byte[SALT_LEN];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(salt);
        String hashedId = null;

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGO);
            PBEKeySpec spec = new PBEKeySpec(
                    schoolId.toCharArray(),
                    salt,
                    STRETCH_ITER,
                    KEY_LENGTH
            );
            byte[] hashedIdBytes = factory.generateSecret(spec).getEncoded();
            Base64.Encoder encoder = Base64.getEncoder();

            // store the hashed SchoolId with the salt and sequenceNumber
            hashedId = encoder.encodeToString(hashedIdBytes)
                    + ":"
                    + encoder.encodeToString(salt)
                    + ":"
                    + sequenceNumber;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // These shouldn't happen unless I supplied invalid string constants.
            throw new RuntimeException(e);
        }

        return hashedId;
    }

    @Override
    public ImmutableMultimap<Student, BitMatrix> call() throws Exception {
        String hashedId = hash(student.getSchoolId(), sequenceNumber);
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix qrCode = writer.encode(
                hashedId,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );
        return ImmutableMultimap.of(student, qrCode);
    }
}
