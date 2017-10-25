package era.uploader.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import era.uploader.common.IOUtil;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.database.MockPageDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QRCreationControllerTest {
    private QRCreationController ctrl;

    @Before
    public void setUp() {
        MockPageDAOImpl pageDAO = new MockPageDAOImpl();
        MockCourseDAOImpl courseDAO = new MockCourseDAOImpl();
        ctrl = new QRCreationController(pageDAO, courseDAO);
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

        Multimap<Student, Page> qRs = ctrl.createQRs(students, numberOfAssignments);
        Collection<Page> values = qRs.values();

        assertFalse(values.isEmpty());
        assertTrue(bus.isEmpty());
        assertEquals(2, values.size());
        int i = 0;
        for (Page page : values) {
            assertEquals(i, page.getSequenceNumber());
            assertEquals(robMcGuy, page.getStudent());
            assertNotNull(page.getQrCode());
            i++;
        }
    }

    @Test
    public void createQRs_EmptySet() throws Exception {
        int numberOfAssignments = 2;
        ImmutableSet<Student> students = ImmutableSet.of();

        Multimap<Student, Page> qRs = ctrl.createQRs(students, numberOfAssignments);

        assertEquals(0, qRs.size());
    }

    @Test
    public void generateStudents_MyronsFile() throws Exception {
        String roster = IOUtil.convertToLocal("src/test/resources/mockRoll.csv");
        List<String> sections = ImmutableList.of(
                "004",
                "018",
                "002",
                "018"
        );
        List<String> firstNames = ImmutableList.of(
                "Sterling",
                "Pam",
                "Lana",
                "Dr"
        );
        List<String> studentIds = ImmutableList.of(
                "800000000",
                "811111111",
                "822222222",
                "833333333"
        );
        Course eighteen = new Course("CHEM", "131", "018");
        Course two = new Course("CHEM", "131", "002");
        Course notExist = new Course("Spooky spooky ghosts", "101", "001");

        Multimap<Course, Student> coursesToStudents = ctrl.generateStudents(Paths.get(roster));

        Map<Course, Collection<Student>> coursesToStudentsMap = coursesToStudents.asMap();
        // 002 and 018 each have one member while 018 has two
        assertTrue(coursesToStudents.size() == 4);
        assertTrue(coursesToStudents.get(eighteen).size() == 2);
        assertTrue(coursesToStudents.get(two).size() == 1);
        assertTrue(coursesToStudents.get(notExist).isEmpty());
        for (Map.Entry<Course, Collection<Student>> courseToStudents :
                coursesToStudentsMap.entrySet()) {
            Course course = courseToStudents.getKey();
            assertEquals("CHEM", course.getDepartment());
            assertEquals("131", course.getCourseNumber());
            assertTrue(sections.contains(course.getSectionNumber()));
            Collection<Student> students = courseToStudents.getValue();
            for (Student student: students) {
                assertTrue(firstNames.contains(student.getFirstName()));
                assertTrue(studentIds.contains(student.getSchoolId()));
            }
        }
    }
}