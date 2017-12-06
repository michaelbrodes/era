package era.uploader.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.database.MockQRCodeMappingDAOImpl;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class QRCreationServiceTest {
    private QRCreationService ctrl;

    @Before
    public void setUp() {
        MockQRCodeMappingDAOImpl pageDAO = new MockQRCodeMappingDAOImpl();
        MockCourseDAOImpl courseDAO = new MockCourseDAOImpl();
        ctrl = new QRCreationService(pageDAO);
    }

    @Test
    public void createQRs_SingletonSet() throws Exception {
        Student robMcGuy = Student.builder()
                .withFirstName("Rob")
                .withLastName("Mcguy")
                .withSchoolId("800999999")
                .create("rmcguy");
        int numberOfAssignments = 2;
        ImmutableSet<Student> students = ImmutableSet.of(robMcGuy);

        Multimap<Student, QRCodeMapping> qRs = ctrl.createQRs(students, numberOfAssignments);
        Collection<QRCodeMapping> values = qRs.values();

        assertFalse(values.isEmpty());
        assertEquals(2, values.size());
        int i = 0;
        for (QRCodeMapping QRCodeMapping : values) {
            assertEquals(i, QRCodeMapping.getSequenceNumber());
            assertEquals(robMcGuy, QRCodeMapping.getStudent());
            assertNotNull(QRCodeMapping.getQrCode());
            i++;
        }
    }

    @Test
    public void createQRs_EmptySet() throws Exception {
        int numberOfAssignments = 2;
        ImmutableSet<Student> students = ImmutableSet.of();

        Multimap<Student, QRCodeMapping> qRs = ctrl.createQRs(students, numberOfAssignments);

        assertEquals(0, qRs.size());
    }

}