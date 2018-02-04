package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * This class is mock of {@link CourseDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link CourseDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockCourseDAOImpl implements CourseDAO, MockDAO<Course> {
    private Set<Course> courses = new HashSet<>();
    private static int idCount = 0;

    /* Create and Insert a new Course object into the database */
    @Override
    public Course insert(@Nonnull Course course) {
        courses.add(course);
        course.setUniqueId(++idCount);
        return course;
    }

    @Override
    public void insertCourseAndStudents(@Nonnull Multimap<Course, Student> coursesToStudents, Semester semester) {
        courses.addAll(coursesToStudents.keys());
        for(Map.Entry<Course, Collection<Student>> studentsInCourse:
                coursesToStudents.asMap().entrySet()) {
            Course key = studentsInCourse.getKey();
            key.getStudentsEnrolled().addAll(studentsInCourse.getValue());
        }
    }

    /* Access data from existing Course object from database */
    @Override
    public Course read(long id) {
        for (Course otherCourse :
                courses) {
            if (otherCourse.getUniqueId() == id) {
                return otherCourse;
            }
        }
        return null;
    }

    @Override
    public void update(@Nonnull Course changedCourse) {
        Course prevCourse = read(changedCourse.getUniqueId());
        courses.remove(changedCourse);
        courses.add(changedCourse);
    }

    @Override
    public void delete(long id) {
        Course course = read(id);
        courses.remove(course);
    }

    @Nonnull
    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    @Override
    public Set<Course> fromStudent(@Nonnull Student student) {
        return student.getCourses();
    }

    @Override
    public Set<Course> getDb() {
        return courses;
    }

    @Nonnull
    @Override
    public Course fromAssignment(@Nonnull Assignment assignment) {
        return assignment.getCourse();
    }
}