package era.uploader.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import era.uploader.common.IOUtil;
import era.uploader.data.database.MockCourseDAOImpl;
import era.uploader.data.database.MockTeacherDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Teacher;
import era.uploader.data.model.Term;
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
        CourseCreationService service = new CourseCreationService(new MockCourseDAOImpl(), new MockTeacherDAOImpl());
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
        Semester currentSemester = Semester.of(Term.FALL, Year.now());
        Course eighteen = Course.builder()
                .withSemester(currentSemester)
                .createUnique("CHEM", "131", "018");
        Course two = Course.builder()
                .withSemester(currentSemester)
                .createUnique("CHEM", "131", "002");
        Course notExist = Course.builder()
                .withSemester(currentSemester)
                .createUnique("Spooky spooky ghosts", "101", "001");
        Teacher teacher = new Teacher(1, "Mr. Spooky");
        Collection<Course> courses = service.createCourses(Paths.get(roster), currentSemester, teacher);

        // 002 and 018 each have one member while 018 has two
        assertTrue(courses.size() == 3);
    }
}
