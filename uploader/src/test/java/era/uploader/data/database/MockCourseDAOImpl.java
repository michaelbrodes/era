package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;
import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;


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
     /* Create and Insert a new Course object into the database */
    @Override
    public Course insert(Course course) {
        courses.add(course);
        course.setUniqueId(idCount++);
        return course;
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
        return courses.stream().filter(course -> course.getUniqueId() == id).findFirst().orElse(null);
    }


    @Override
    public void update(Course changedCourse) {
        courses.remove(changedCourse);
        courses.add(changedCourse);
    }

    @Override
    public void delete(long id) {
        Course nullableCourse = courses.stream().filter(course -> course.getUniqueId() == id).findFirst().orElse(null);
        courses.remove(nullableCourse);
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