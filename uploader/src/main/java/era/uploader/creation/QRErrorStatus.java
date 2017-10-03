package era.uploader.creation;

import javax.annotation.Nonnull;

/**
 * The different reasons why QR creation could fail
 */
public enum QRErrorStatus {
    TIMEOUT_ERROR("It has taken the QR creation process a really long time so we stopped it."),
    GENERATION_ERROR("The QR code could not be generated."),
    PARSE_ERROR("Error occurred while trying to parse a record in the input CSV file. Please have all records in the file be of the form: Last Name, First Name, Username, Student ID, Child Course ID"),
    INTERRUPT_ERROR("QR creation could not finish because it was interrupted.");

    private final String reason;

    QRErrorStatus(String reason) {
        this.reason = reason;
    }

    @Nonnull
    public String getReason() {
        return reason;
    }
}
