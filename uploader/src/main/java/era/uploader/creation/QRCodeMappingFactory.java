package era.uploader.creation;

import com.google.zxing.common.BitMatrix;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

public class QRCodeMappingFactory {
    public static final int QR_HEIGHT = 100;
    public static final int QR_WIDTH = 100;
    // final: starting parameters of the factory
    private final Student student;
    private final int pageNumber;
    private String uuid;
    private BitMatrix qrCode;

    QRCodeMappingFactory(Student student, int pageNumber) {
        this.student = student;
        this.pageNumber = pageNumber;
    }

    public QRCodeMapping createQRCodeMapping() {
        return QRCodeMapping.builder()
                .create(null);
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setQRCode(BitMatrix qrCode) {
        this.qrCode = qrCode;
    }

    public BitMatrix getQrCode() {
        return qrCode;
    }
}
