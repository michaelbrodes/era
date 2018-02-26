package era.uploader.service.assignment;

public class SaveException extends RuntimeException {
    public SaveException(Throwable cause) {
        super("Problem saving QR Code", cause);
    }
}
