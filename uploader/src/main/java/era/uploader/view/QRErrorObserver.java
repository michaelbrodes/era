package era.uploader.view;

import era.uploader.creation.QRErrorEvent;

/**
 * An observer of the {@link QRErrorEvent}
 */
public interface QRErrorObserver {
    void onError(QRErrorEvent event);
}
