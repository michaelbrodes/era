package era.uploader.controller;

import era.uploader.processing.StatusChangeEvent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The object that is responsible for firing {@link StatusChangeEvent}s to
 * {@link era.uploader.view.StatusChangeObserver}s. When a StatusChangeEvent
 * is fired the StatusChangeObserver should asynchronously update the view it
 * represents.
 */
@ThreadSafe
public class StatusChangeBus extends AsyncMessageBus<StatusChangeEvent> {
    private static StatusChangeBus INSTANCE;

    private StatusChangeBus() {
        super();

        final ExecutorService singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(this);
    }

    /**
     * Threadsafe singleton instance method
     *
     * @return the one and only instance of this class in ERA
     */
    public static StatusChangeBus instance() {
        if (INSTANCE == null) {
            synchronized (StatusChangeBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StatusChangeBus();
                }
            }
        }

        return INSTANCE;
    }
}
