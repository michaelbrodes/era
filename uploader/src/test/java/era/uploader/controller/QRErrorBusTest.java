package era.uploader.controller;

import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.model.Student;
import era.uploader.view.QRErrorObserver;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import static java.lang.System.exit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class QRErrorBusTest {
    /**
     * Asserts that {@link QRErrorEvent}s arrive in order when fired down the
     * error bus and that the {@link QRErrorObserver} registers only two events.
     */
    @Test
    public void fire_TwoEventsInQueue() throws Exception {
        Student timmy = Student.create()
                .withFirstName("timmy")
                .withLastName("dude")
                .withSchoolId("tdude")
                .build();

        QRErrorEvent generation = new QRErrorEvent(QRErrorStatus.GENERATION_ERROR, timmy);
        QRErrorEvent timeout = new QRErrorEvent(QRErrorStatus.TIMEOUT_ERROR);

        MockErrorObserver observer = new MockErrorObserver();
        final QRErrorBus errorBus = QRErrorBus.instance();
        errorBus.register(observer);

        errorBus.fire(generation);
        errorBus.fire(timeout);
        QRErrorEvent one = observer.checkMessage();
        QRErrorEvent two = observer.checkMessage();

        assertTrue(errorBus.isEmpty());
        assertTrue(observer.sentMessages.isEmpty());
        assertEquals(generation, one);
        assertEquals(timeout, two);
        assertEquals(timmy, one.getErroredStudent());
        assertNull(two.getErroredStudent());
    }

    /**
     * Asserts that no {@link QRErrorEvent}s are registered with the
     * {@link QRErrorObserver} when no events are shot down the bus.
     */
    @Test
    public void fire_NoEvents() throws Exception {
        final QRErrorBus errorBus = QRErrorBus.instance();
        MockErrorObserver observer = new MockErrorObserver();
        errorBus.register(observer);

        assertTrue(errorBus.isEmpty());
        assertTrue(observer.sentMessages.isEmpty());
    }

    /**
     * Makes sure that the error message shot down the bust must not be null
     */
    @Test(expected = NullPointerException.class)
    public void fire_NullEvent() throws Exception {
        QRErrorBus.instance().fire(null);
    }

    private static class MockErrorObserver implements QRErrorObserver {
        private final ConcurrentLinkedQueue<QRErrorEvent> sentMessages =
                new ConcurrentLinkedQueue<>();
        private final Semaphore sem = new Semaphore(0);

        QRErrorEvent checkMessage() {
            try {
                // Messages are fired asynchronously; make sure we actually
                // have a message before trying to give it to the caller
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                exit(1);
            }
            return sentMessages.poll();
        }

        @Override
        public void onMessage(QRErrorEvent message) {
            sem.release();
            sentMessages.offer(message);
        }
    }

}