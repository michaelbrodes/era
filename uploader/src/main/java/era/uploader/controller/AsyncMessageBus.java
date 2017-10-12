package era.uploader.controller;

import com.google.common.base.Preconditions;
import era.uploader.view.MessageObserver;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Allows GUIs to communicate asynchronously with controllers. The GUIs will
 * be responsible for implementing the observer pattern through implementing
 * {@link MessageObserver}. This class is usually subclassed by error message
 * queues.
 */
@ParametersAreNonnullByDefault
public abstract class AsyncMessageBus<T> implements Runnable {
    private final BlockingQueue<MessageObserver<T>> observers;
    // this should be made protected if any subclasses need access to the messageQueue
    private final BlockingQueue<T> messageQueue;

    protected AsyncMessageBus() {
        observers = new LinkedBlockingQueue<>();
        messageQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Adds a new message to the message queue for it to be processed in
     * {@link #run}. NOTE: If this method errors out it will just print a stack
     * trace. Right now this method doesn't handle errors well
     *
     * @param message a message we want to be communicated to a GUI (usually
     *                an event caused by an error)
     */
    public void fire(T message) {
        Preconditions.checkNotNull(message);
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            // TODO: this should be handled better in the US13.
            // The reason it is not handled better now is because this class
            // is used literally for passing error messages
            e.printStackTrace();
        }
    }

    /**
     * Adds a new observer to the observer queue. An observer is a GUI that
     * handles new messages coming in through {@link #fire}
     *
     * @param observer A GUI that handles a message being fired asynchronously
     */
    public void register(MessageObserver<T> observer) {
        Preconditions.checkNotNull(observer);
        try {
            observers.put(observer);
        } catch (InterruptedException e) {
            // TODO: this should be handled better in the US13.
            // The reason it is not handled better now is because this class
            // is used literally for passing error messages
            e.printStackTrace();
        }

    }

    /**
     * @return If the message queue is empty then the bus is empty
     */
    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    /**
     * Grabs the current message in the front of the message queue and calls
     * {@link MessageObserver#onMessage(Object)} on all of the observers for
     * that type of message.
     */
    @Override
    public void run() {
        try {
            T current;
            while ((current = messageQueue.take()) != null) {
                for (MessageObserver<T> observer : observers) {
                    observer.onMessage(current);
                }
            }
        } catch (InterruptedException e) {
            // TODO: handle error better in US13
            e.printStackTrace();
        }
    }
}
