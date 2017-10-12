package era.uploader.view;

import era.uploader.controller.StatusChangeBus;
import era.uploader.processing.StatusChangeEvent;

/**
 * An implementation of the "Observer Pattern" handles
 * {@link StatusChangeEvent}s fired by {@link StatusChangeBus}. Eventually this
 * class should asynchronously update the view it represents on another thread.
 */
public interface StatusChangeObserver extends MessageObserver<StatusChangeEvent> {
}
