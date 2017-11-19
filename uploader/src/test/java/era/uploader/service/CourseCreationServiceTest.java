package era.uploader.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import era.uploader.common.IOUtil;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.junit.Test;

import java.nio.file.Paths;
import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourseCreationServiceTest {
    @Test
    public void generateStudents_MyronsFile() throws Exception {
        String roster = IOUtil.convertToLocal("src/test/resources/mockRoll.csv");
        CourseCreationService service = new CourseCreationService(new MockCourseDAOImpl());
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
        Semester currentSemester = new Semester(Semester.Term.FALL, Year.now());
        Course eighteen = new Course("CHEM", "131", "018", currentSemester);
        Course two = new Course("CHEM", "131", "002", currentSemester);
        Course notExist = new Course("Spooky spooky ghosts", "101", "001", currentSemester);

        Multimap<Course, Student> coursesToStudents = service.createCourses(Paths.get(roster), currentSemester);

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
