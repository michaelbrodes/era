package era.uploader.processing;

import com.google.common.base.Preconditions;
import era.uploader.data.model.FileStatus;

import javax.annotation.Nonnull;

/**
 * An event that is fired when a Page's status has changed.
 */
public class StatusChangeEvent {
    private final FileStatus status;

    public StatusChangeEvent(@Nonnull FileStatus status) {
        Preconditions.checkNotNull(status);
        this.status = status;
    }

    @Nonnull
    public FileStatus getStatus() {
        return status;
    }
}
