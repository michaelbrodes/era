package era.uploader.qrcreation;

public class SaveException extends RuntimeException {
    public SaveException(Throwable cause) {
        super("Problem saving QR Code", cause);
    }
}
