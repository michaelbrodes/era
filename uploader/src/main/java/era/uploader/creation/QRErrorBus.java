package era.uploader.creation;

import era.uploader.view.QRErrorObserver;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * The bus for when QR creation error has occurred.
 */
public class QRErrorBus implements Runnable {
    private final BlockingDeque<QRErrorEvent> errorStack;
    private final BlockingQueue<QRErrorObserver> observers;
    private static QRErrorBus INSTANCE;

    public QRErrorBus() {
        final ExecutorService singleThread = Executors.newSingleThreadExecutor();
        observers = new LinkedBlockingQueue<>();
        errorStack = new LinkedBlockingDeque<>();

        singleThread.submit(this);
    }

    public void fire (QRErrorEvent event) {
        try {
            errorStack.putLast(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void register(QRErrorObserver observer) {
        try {
            observers.put(observer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return errorStack.isEmpty();
    }

    public static QRErrorBus instance() {
        if (INSTANCE == null) {
            INSTANCE = new QRErrorBus();
        }
        return INSTANCE;
    }

    @Override
    public void run() {
        try {
            QRErrorEvent current;
            while ((current = errorStack.takeLast()) != null) {
                for (QRErrorObserver observer : observers) {
                    observer.onMessage(current);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
