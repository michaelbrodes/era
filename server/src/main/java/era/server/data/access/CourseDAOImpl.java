package era.server.data.access;

import com.google.common.base.Preconditions;
import era.server.data.CourseDAO;
import era.server.data.model.Course;
import era.server.data.model.Student;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * access. A course has many {@link Student}s and many Grader.
 */
@ParametersAreNonnullByDefault
public class CourseDAOImpl implements CourseDAO {
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the access table */

    /* Create and Insert a new Course object into the access */

    @Override
    public void insert(Course course) {
        Preconditions.checkNotNull(course);
        courses.add(course);
    }


    /* Access data from existing Course object from access */
    @Override
    @Nullable
    public Course read(long id) {
        for (Course otherCourse :
                courses) {
            if (otherCourse.getUniqueId() == id) {
                return otherCourse;
            }
        }
        return null;
    }

    /* Modify data stored in already existing Course in access */
    @Override
    public void update(Course courseToChange, Course courseChanged) {
        Preconditions.checkNotNull(courseToChange);
        Preconditions.checkNotNull(courseChanged);
        courses.remove(courseToChange);
        courses.add(courseChanged);
    }

    /* Delete existing Course object in access */
    @Override
    public void delete(Course course) {
        Preconditions.checkNotNull(course);
        courses.remove(course);
    }
}
