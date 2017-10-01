package era.uploader.controller;

import era.uploader.creation.QRErrorEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The bus for when QR creation error has occurred.
 */
public class QRErrorBus extends AsyncMessageBus<QRErrorEvent> {
    private static QRErrorBus INSTANCE;

    private QRErrorBus() {
        super();

        final ExecutorService singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(this);
    }


    public static QRErrorBus instance() {
        if (INSTANCE == null) {
            INSTANCE = new QRErrorBus();
        }
        return INSTANCE;
    }
}
