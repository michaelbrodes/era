package era.uploader.view;

import era.uploader.controller.AsyncMessageBus;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Forces a GUI to implement the <em>observer</em> pattern to receive messages
 * asynchronously through a {@link AsyncMessageBus}. By asynchronously that
 * usually means on another thread, so make sure the GUI makes appropriate
 * thread safety precautions to avoid race conditions.
 */
@NotThreadSafe
public interface MessageObserver<T> {
    void onMessage(T message);
}
