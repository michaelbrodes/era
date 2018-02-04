//package era.uploader.creation;
//
//import era.uploader.data.model.QRCodeMapping;
//import era.uploader.data.model.Student;
//import era.uploader.service.assignment.QRCreator;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//public class QRCreatorTest {
//    /**
//     * Checks that a page with a QR code is created when we give the
//     * {@link QRCreator} a non-null student
//     */
//    @Test
//    public void call_NotNullStudent() throws Exception {
//        Student robMcGuy = Student.builder()
//                .withSchoolId("800999999")
//                .withFirstName("McGuy")
//                .withFirstName("Rob")
//                .create("rmcguy");
//        int sequenceNumber = 1;
//        QRCreator creator = new QRCreator(robMcGuy, sequenceNumber);
//
//        QRCodeMapping robsQRCodeMapping = creator.call();
//
//        assertNotNull(robsQRCodeMapping);
//        assertNotNull(robsQRCodeMapping.getQrCode());
//        assertEquals(robMcGuy, robsQRCodeMapping.getStudent());
//        assertEquals(sequenceNumber, robsQRCodeMapping.getSequenceNumber());
//    }
//
//    /**
//     * QRCreator has a precondition on its constructor that doesn't allow
//     * nulls. This test is to make sure that precondition works
//     * @throws Exception NPE because we don't allow null students
//     */
//    @Test(expected = NullPointerException.class)
//    public void call_NullStudent() throws Exception {
//        new QRCreator(null, 0);
//    }
//
//}