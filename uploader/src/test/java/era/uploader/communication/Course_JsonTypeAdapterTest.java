package era.uploader.communication;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Term;
import era.uploader.data.model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.time.Year;
import java.util.Collections;
import java.util.List;

public class Course_JsonTypeAdapterTest {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Course.class, new Course_JsonTypeAdapter())
            .create();

    @Test
    public void createSingleCourseJSON() {
        final String expectedJSON = "{\"uuid\":\"1\",\"name\":\"CHEM-101-001\",\"studentsEnrolled\":[{\"userName\":\"user\",\"email\":\"user@email.com\",\"uuid\":\"1\"}],\"semester\":{\"uuid\":\"1\",\"term\":\"FALL\",\"year\":{\"year\":2014}}}";
        Course course = Course.builder()
                .withSemester(new Semester("1", Term.FALL, Year.of(2014)))
                .withStudents(Sets.newHashSet(Student.builder()
                        .withEmail("user@email.com")
                        .withFirstName("firstname")
                        .withLastName("lastname")
                        .withSchoolId("800524927")
                        .create("user", "1")))
                .create("CHEM", "101", "001", "1");

        String json = GSON.toJson(course, Course.class);

        Assert.assertNotNull(json);
        Assert.assertEquals(expectedJSON, json);
    }

    @Test
    public void createArrayOfCourseJSON() {
        final String expectedJSON = "[{\"uuid\":\"1\",\"name\":\"CHEM-101-001\",\"studentsEnrolled\":[{\"userName\":\"user\",\"email\":\"user@email.com\",\"uuid\":\"1\"}],\"semester\":{\"uuid\":\"1\",\"term\":\"FALL\",\"year\":{\"year\":2014}}}]";
        Course course = Course.builder()
                .withSemester(new Semester("1", Term.FALL, Year.of(2014)))
                .withStudents(Sets.newHashSet(Student.builder()
                        .withEmail("user@email.com")
                        .withFirstName("firstname")
                        .withLastName("lastname")
                        .withSchoolId("800524927")
                        .create("user", "1")))
                .create("CHEM", "101", "001", "1");
        Type courseList = new TypeToken<List<Course>>() {
        }.getType();
        String json = GSON.toJson(Collections.singletonList(course), courseList);

        Assert.assertNotNull(json);
        Assert.assertEquals(expectedJSON, json);
    }

    @Test
    public void parseSingleCourseJSON() {
        Student student = Student.builder()
                .withEmail("user@email.com")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withSchoolId("800524927")
                .create("user", "1");
        Course expectedCourse = Course.builder()
                .withSemester(new Semester("1", Term.FALL, Year.of(2014)))
                .withStudents(Sets.newHashSet(student))
                .create("CHEM", "101", "001", "1");
        final String json = "{\"uuid\":\"1\",\"name\":\"CHEM-101-001\",\"studentsEnrolled\":[{\"userName\":\"user\",\"email\":\"user@email.com\",\"uuid\":\"1\"}],\"semester\":{\"uuid\":\"1\",\"term\":\"FALL\",\"year\":{\"year\":2014}}}";
        Course returnedCourse = GSON.fromJson(json, Course.class);

        Assert.assertEquals(expectedCourse.getUuid(), returnedCourse.getUuid());
        Assert.assertEquals(expectedCourse.getName(), returnedCourse.getName());
        Assert.assertEquals(expectedCourse.getSemester().getTerm(), returnedCourse.getSemester().getTerm());
        Assert.assertEquals(expectedCourse.getSemester().getUuid(), returnedCourse.getSemester().getUuid());
        Assert.assertEquals(expectedCourse.getSemester().getYear(), returnedCourse.getSemester().getYear());
        Assert.assertEquals(student.getUuid(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getUuid());
        Assert.assertEquals(student.getEmail(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getEmail());
        Assert.assertEquals(student.getUserName(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getUserName());
    }

    @Test
    public void parseArrayOfCourseJSON() {
        Student student = Student.builder()
                .withEmail("user@email.com")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withSchoolId("800524927")
                .create("user", "1");
        Course expectedCourse = Course.builder()
                .withSemester(new Semester("1", Term.FALL, Year.of(2014)))
                .withStudents(Sets.newHashSet(student))
                .create("CHEM", "101", "001", "1");
        final String json = "[{\"uuid\":\"1\",\"name\":\"CHEM-101-001\",\"studentsEnrolled\":[{\"userName\":\"user\",\"email\":\"user@email.com\",\"uuid\":\"1\"}],\"semester\":{\"uuid\":\"1\",\"term\":\"FALL\",\"year\":{\"year\":2014}}}]";
        Type courseList = new TypeToken<List<Course>>() {
        }.getType();
        List<Course> returnedCourses = GSON.fromJson(json, courseList);

        Course returnedCourse = Iterables.getOnlyElement(returnedCourses);
        Assert.assertEquals(expectedCourse.getUuid(), returnedCourse.getUuid());
        Assert.assertEquals(expectedCourse.getName(), returnedCourse.getName());
        Assert.assertEquals(expectedCourse.getSemester().getTerm(), returnedCourse.getSemester().getTerm());
        Assert.assertEquals(expectedCourse.getSemester().getUuid(), returnedCourse.getSemester().getUuid());
        Assert.assertEquals(expectedCourse.getSemester().getYear(), returnedCourse.getSemester().getYear());
        Assert.assertEquals(student.getUuid(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getUuid());
        Assert.assertEquals(student.getEmail(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getEmail());
        Assert.assertEquals(student.getUserName(), Iterables.getOnlyElement(returnedCourse.getStudentsEnrolled()).getUserName());
    }

}