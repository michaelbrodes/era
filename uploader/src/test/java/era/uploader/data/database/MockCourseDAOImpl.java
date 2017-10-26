package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.util.Collection;
import java.util.HashSet;
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

    /* Create and Insert a new Course object into the database */
    @Override
    public void insert(Course course) {
        courses.add(course);
    }

    @Override
    public void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents) {
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
            if (otherCourse.getId() == id) {
                return otherCourse;
            }
        }
        return null;
    }

    /* Modify data stored in already existing Course in database */
    @Override
    public void update(Course courseToChange, Course courseChanged) {
        courses.remove(courseToChange);
        courses.add(courseChanged);
    }

    /* Delete existing Course object in database */
    @Override
    public void delete(Course course) {
        courses.remove(course);
    }

    @Override
    public Set<Course> getAllCourses() {
        return courses;
    }

    @Override
    public Set<Course> getDb() {
        return courses;
    }
}