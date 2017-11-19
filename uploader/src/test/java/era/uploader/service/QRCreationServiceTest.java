package era.uploader.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import era.uploader.common.IOUtil;
import era.uploader.controller.QRErrorBus;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.database.MockQRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.service.QRCreationService;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QRCreationServiceTest {
    private QRCreationService ctrl;

    @Before
    public void setUp() {
        MockQRCodeMappingDAOImpl pageDAO = new MockQRCodeMappingDAOImpl();
        MockCourseDAOImpl courseDAO = new MockCourseDAOImpl();
        ctrl = new QRCreationService(pageDAO, courseDAO);
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
        QRErrorBus bus = QRErrorBus.instance();

        Multimap<Student, QRCodeMapping> qRs = ctrl.createQRs(students, numberOfAssignments);
        Collection<QRCodeMapping> values = qRs.values();

        assertFalse(values.isEmpty());
        assertTrue(bus.isEmpty());
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