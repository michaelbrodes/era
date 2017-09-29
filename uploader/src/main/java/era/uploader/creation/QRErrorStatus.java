package era.uploader.creation;

/**
 * The different reasons why QR creation could fail
 */
public enum QRErrorStatus {
    TIMEOUT_ERROR("It has taken the QR creation process a really long time so we stopped it."),
    GENERATION_ERROR("The QR code could not be generated."),
    INTERRUPT_ERROR("QR creation could not finish because it was interrupted.");

    private final String reason;

    QRErrorStatus(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
