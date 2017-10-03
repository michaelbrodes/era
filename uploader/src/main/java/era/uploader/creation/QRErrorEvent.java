package era.uploader.creation;

import com.google.common.base.Preconditions;
import era.uploader.data.model.Student;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * If a QR creation algorithm fails on any page we should display that to the
 * user. This Event happens asynchronously and is given to observing view
 * classes.
 */
public class QRErrorEvent {
    private final Student erroredStudent;
    private final String erroredLine;
    private final QRErrorStatus status;

    public QRErrorEvent(@Nonnull QRErrorStatus status) {
        Preconditions.checkNotNull(status);
        this.status = status;
        this.erroredStudent = null;
        this.erroredLine = null;
    }

    public QRErrorEvent(@Nonnull QRErrorStatus status, @Nullable Student erroredStudent) {
        Preconditions.checkNotNull(status);
        this.erroredStudent = erroredStudent;
        this.status = status;
        this.erroredLine = null;
    }

    public QRErrorEvent(
            @Nonnull QRErrorStatus status,
            @Nullable Student erroredStudent,
            @Nullable String erroredLine
    ) {
        Preconditions.checkNotNull(status);
        this.erroredStudent = erroredStudent;
        this.status = status;
        this.erroredLine = erroredLine;
    }

    @Nullable
    public Student getErroredStudent() {
        return erroredStudent;
    }

    @Nonnull
    public QRErrorStatus getStatus() {
        return status;
    }

    @Nullable
    public String getErroredLine() {
        return erroredLine;
    }
}
