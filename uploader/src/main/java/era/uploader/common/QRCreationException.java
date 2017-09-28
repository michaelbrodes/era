package era.uploader.common;

/**
 * Indicates that something was wrong in the QR creation process. Gives a human readable
 * message as to why
 */
public class QRCreationException extends Exception {
    /**
     * Convenience constructor that gives the default message for a system
     * interrupt.
     */
    public QRCreationException(InterruptedException e) {
        super("The QR creation processes was interrupted before it was done.");
    }

    public QRCreationException(String message) {
        super(message);
    }
}
