package era.uploader.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.database.MockQRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

        Course course = Course.builder()
                .withStudents(students)
                .create("CS", "111", "001");

        Multimap<Student, QRCodeMapping> qRs = ctrl.createQRs(course, "", numberOfAssignments);
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
        Course course = Course.builder()
                .withStudents(students)
                .create("CS", "499", "001");

        Multimap<Student, QRCodeMapping> qRs = ctrl.createQRs(course,"", numberOfAssignments);

        assertEquals(0, qRs.size());
    }

    @Test
    public void bucketStudentsIntoLabelBatches_QuadCore() {
        Student jayz = Student.builder()
                .withFirstName("Jay")
                .withLastName("Z")
                .withSchoolId("800444444")
                .create("jz");

        ImmutableList<Student> students =
                ImmutableList.of(
                        Student.builder()
                            .withFirstName("young")
                            .withLastName("thug")
                            .withSchoolId("800111111")
                            .create("ythug"),
                        Student.builder()
                            .withFirstName("Ghostface")
                            .withLastName("Killa")
                            .withSchoolId("800222222")
                            .create("gkilla"),
                        Student.builder()
                            .withFirstName("Pusha")
                            .withLastName("T")
                            .withSchoolId("800333333")
                            .create("pt"),
                        jayz
                );
        int numProcessors = 4;

        List<List<Student>> buckets = ctrl.bucketStudentsIntoLabelBatches(students, numProcessors);

        assertEquals(numProcessors, buckets.size());
        assertEquals(1, buckets.get(0).size());
        assertEquals(jayz, Iterables.getOnlyElement(buckets.get(3)));

    }

    @Test
    public void bucketStudentsIntoLabelBatches_SingleCore() {
        Student jayz = Student.builder()
                .withFirstName("Jay")
                .withLastName("Z")
                .withSchoolId("800444444")
                .create("jz");

        ImmutableList<Student> students =
                ImmutableList.of(
                        Student.builder()
                                .withFirstName("young")
                                .withLastName("thug")
                                .withSchoolId("800111111")
                                .create("ythug"),
                        Student.builder()
                                .withFirstName("Ghostface")
                                .withLastName("Killa")
                                .withSchoolId("800222222")
                                .create("gkilla"),
                        Student.builder()
                                .withFirstName("Pusha")
                                .withLastName("T")
                                .withSchoolId("800333333")
                                .create("pt"),
                        jayz
                );
        int numProcessors = 1;

        List<List<Student>> buckets = ctrl.bucketStudentsIntoLabelBatches(students, numProcessors);

        assertEquals(numProcessors, buckets.size());
        assertEquals(4, buckets.get(0).size());
        assertEquals(jayz, buckets.get(0).get(3));
    }

    @Test
    public void bucketStudentsIntoLabelBatches_EmptyStudentSet() {
        int numProcessors = 1;

        List<List<Student>> buckets = ctrl.bucketStudentsIntoLabelBatches(Collections.emptyList(), numProcessors);

        assertEquals(numProcessors, buckets.size());
        assertEquals(0, buckets.get(0).size());
        assertEquals(0, buckets.get(3).size());
    }

}