package era.uploader.service.assignment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.service.assignment.AveryConstants;
import era.uploader.service.assignment.QRCode;
import era.uploader.service.assignment.QRCreator;
import org.junit.Test;

import java.time.Year;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QRCreatorTest {
    /**
     * Checks that a page with a QR code is created when we give the
     * {@link QRCreator} a non-null student
     */
    @Test
    public void call_NotNullStudent() throws Exception {
        Student robMcGuy = Student.builder()
                .withSchoolId("800999999")
                .withLastName("McGuy")
                .withFirstName("Rob")
                .create("rmcguy");
        Course course = Course.builder()
                .withStudents(ImmutableSet.of(robMcGuy))
                .withName("ebrbrbrbrbrbrbrb")
                .create("CS", "111", "001");
        int sequenceNumber = 1;
        QRCreator creator = new QRCreator(course, Collections.singletonList(robMcGuy), "Ambitions of a Rider", sequenceNumber, AveryConstants.Shipping.QR_HEIGHT, AveryConstants.Shipping.QR_WIDTH);

        QRCode robsQRCodeMapping = Iterables.getOnlyElement(creator.call());

        assertNotNull(robsQRCodeMapping);
        assertNotNull(robsQRCodeMapping.getQrCode());
        assertEquals(robMcGuy.getFirstName() + " " + robMcGuy.getLastName(), robsQRCodeMapping.getStudentName());
        assertEquals(sequenceNumber, robsQRCodeMapping.getPageNumber());
    }

    /**
     * QRCreator has a precondition on its constructor that doesn't allow
     * nulls. This test is to make sure that precondition works
     * @throws Exception NPE because we don't allow null students
     */
    @Test(expected = NullPointerException.class)
    public void call_NullStudent() throws Exception {
        new QRCreator(null, null, null, 0, AveryConstants.Shipping.QR_HEIGHT, AveryConstants.Shipping.QR_WIDTH);
    }

    @Test
    public void createAssignmentForStudent() {
        String assignmentName = "It is 1 am and I am writing code. I hate myself";
        Student chance = Student.builder()
                .withFirstName("chance")
                .withLastName("rapper")
                .create("crapper");
        Course course = Course.builder()
                .withName("I am a name")
                .withSemester(Semester.of(Semester.Term.SPRING, Year.of(2017)))
                .withDatabaseId(1)
                .create("CS", "111", "001");
        int pageSize = 9;
        QRCreator creator = new QRCreator(course, Collections.singletonList(chance), assignmentName, pageSize, AveryConstants.Shipping.QR_HEIGHT, AveryConstants.Shipping.QR_WIDTH);

        Assignment assignment = creator.createAssignmentForStudent(chance);

        assertEquals(assignmentName, assignment.getName());
        assertEquals(chance, assignment.getStudent());
        assertEquals(0, assignment.getStudent_id());
        assertEquals(1, assignment.getCourse_id());
    }

}