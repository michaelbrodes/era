package era.uploader.data.model;

import javax.annotation.Nonnull;

/**
 * The different reasons why QR qrcreation could fail
 */
public enum QRErrorStatus {
    TIMEOUT_ERROR("It has taken the QR qrcreation process a really long time so we stopped it."),
    GENERATION_ERROR("The QR code could not be generated."),
    PARSE_ERROR("Error occurred while trying to parse a record in the input CSV file. Please have all records in the file be of the form: Last Name, First Name, Username, Student ID, Child Course ID"),
    COURSE_PARSE_ERROR("Error occurred while trying to parse the course id of a CSV record. Please have all course ID's be of the form DEPARTMENT-COURSE NUMBER-SECTION NUMBER-REFERENCE NUMBER"),
    INVALID_FILE("The file you have inserted doesn't exist. We cannot builder any QR codes from it"),
    INTERRUPT_ERROR("QR qrcreation could not finish because it was interrupted."),
    MERGE_ERROR("Error occurred when trying to merge PDF documents"),
    SAVE_ERROR("Error occurred when trying to save the complete PDF with all pages"),
    UUID_ERROR("Could not find value of UUID on QR Code.")
    ;


    private final String reason;

    QRErrorStatus(String reason) {
        this.reason = reason;
    }

    @Nonnull
    public String getReason() {
        return reason;
    }
}
