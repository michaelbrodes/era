package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Grader;
import era.uploader.data.model.Student;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * database. A course has many {@link Student}s and many {@link Grader}s.
 */
public class CourseDAOImpl implements CourseDAO {
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the database table */

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
    public Course read(Course course) {
        for (Course otherCourse :
                courses) {
            if (otherCourse.equals(course)) {
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
}
