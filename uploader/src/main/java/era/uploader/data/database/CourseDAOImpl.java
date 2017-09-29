package era.uploader.data.database;

import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import java.util.Set;
import java.util.HashSet;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * database
 */
public class CourseDAOImpl implements CourseDAO {
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the database table */

    /* Create and Insert a new Course object into the database */
    public void insert(Course course) {
        courses.add(course);
    }

    /* Access data from existing Course object from database */
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
    public void update(Course courseToChange, Course courseChanged) {
        courses.remove(courseToChange);
        courses.add(courseChanged);
    }

    /* Delete existing Course object in database */
    public void delete(Course course) {
        courses.remove(course);
    }
}
