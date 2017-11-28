package era.uploader.data.database;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.time.Year;
import java.util.Collection;

public class CourseDAOImplTest {
    private CourseDAOImpl courseDAO = (CourseDAOImpl) CourseDAOImpl.instance();
    @Test
    public void squashMap_MultipleCourses() throws Exception {
        /*
        ARRANGE
         */
        Course crush = Course.builder()
                .withDatabaseId(1)
                .withName("Crush Odin")
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .create("CHEM", "111", "001");
        Course chem = Course.builder()
                .withDatabaseId(1)
                .withName("General chemistry")
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .create("CHEM", "121", "001");
        Student lana = Student.builder()
                .withFirstName("Lana")
                .withLastName("Kane")
                .withSchoolId("800542124")
                .create("lkane");
        Student archer = Student.builder()
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withSchoolId("800444444")
                .create("sarcher");
        Student pam = Student.builder()
                .withFirstName("Pam")
                .withLastName("Poovey")
                .withSchoolId("800666666")
                .create("ppoovey");
        Student cheryl = Student.builder()
                .withFirstName("Cheryl")
                .withLastName("Tunt")
                .withSchoolId("800777777")
                .create("ctunt");
        Student cyril = Student.builder()
                .withFirstName("Cyril")
                .withLastName("Figgis")
                .withSchoolId("800888888")
                .create("cfiggis");
        ImmutableMultimap.Builder<Course, Student> coursesToStudentsBuilder = ImmutableMultimap.builder();
        coursesToStudentsBuilder.put(chem, lana);
        coursesToStudentsBuilder.put(chem, pam);
        coursesToStudentsBuilder.put(chem, cyril);
        coursesToStudentsBuilder.put(crush, cheryl);
        coursesToStudentsBuilder.put(crush, archer);
        coursesToStudentsBuilder.put(crush, pam);
        ImmutableMultimap<Course, Student> coursesToStudents = coursesToStudentsBuilder.build();

        /*
        ACT
         */
        Collection<Course> courses = courseDAO.squashMap(coursesToStudents);

        /*
        ASSERT
         */
        Assert.assertNotNull(courses);
        Assert.assertEquals(2, courses.size());
        Assert.assertTrue(courses.contains(crush));
        Assert.assertTrue(courses.contains(chem));
        Course firstSquashedCourse = Iterables.getFirst(courses, null);
        Course secondSquashedCourse = Iterables.getLast(courses);
        Assert.assertNotNull(firstSquashedCourse);
        Assert.assertNotNull(secondSquashedCourse);
        Assert.assertTrue(firstSquashedCourse.getStudentsEnrolled().contains(pam));
        Assert.assertTrue(secondSquashedCourse.getStudentsEnrolled().contains(pam));
        if (firstSquashedCourse.equals(chem)) {
            Assert.assertTrue(firstSquashedCourse.getStudentsEnrolled().contains(lana));
        } else {
            Assert.assertTrue(firstSquashedCourse.getStudentsEnrolled().contains(archer));
        }
    }

    @Test
    public void squashMap_ZeroCourses() {
        ImmutableMultimap<Course, Student> mmap = ImmutableMultimap.of();
        Collection<Course> courses = courseDAO.squashMap(mmap);
        Assert.assertTrue(courses.isEmpty());
    }

}