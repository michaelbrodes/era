package era.uploader.creation;

import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.junit.Test;

import static org.junit.Assert.*;

public class QRCreatorTest {
    /**
     * Checks that a page with a QR code is created when we give the
     * {@link QRCreator} a non-null student
     */
    @Test
    public void call_NotNullStudent() throws Exception {
        Student robMcGuy = new Student();
        robMcGuy.setSchoolId("rmcguy");
        robMcGuy.setName("Rob McGuy");
        int sequenceNumber = 1;
        QRCreator creator = new QRCreator(robMcGuy, sequenceNumber);
        QRErrorBus bus = QRErrorBus.instance();

        Page robsPage = creator.call();

        assertNotNull(robsPage);
        assertTrue(bus.isEmpty());
        assertNotNull(robsPage.getQrCode());
        assertEquals(robMcGuy, robsPage.getStudent());
        assertEquals(sequenceNumber, robsPage.getSequenceNumber());
    }

    /**
     * QRCreator has a precondition on its constructor that doesn't allow
     * nulls. This test is to make sure that precondition works
     * @throws Exception NPE because we don't allow null students
     */
    @Test(expected = NullPointerException.class)
    public void call_NullStudent() throws Exception {
        QRCreator creator = new QRCreator(null, 0);
    }

}