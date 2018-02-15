package era.server.data;

import era.server.data.model.Course;
import era.server.data.model.Semester;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Provides CRUD access functionality for {@link Course} objects. Abstracted
 * to an interface, so that we can mock this for unit tests.
 */
@ParametersAreNonnullByDefault
public interface CourseDAO {
    /**
     * Create and Insert a new Course object into the database. This method
     * will also insert all students and assignments attached to a course, so
     * <em>use wisely</em>.
     */
    Course insert(Course course) throws SQLException;

    Course resolveCourse(Course course);

    /**
     * Access data from existing Course object from access
     */
    @Nullable
    Course read(String id);

    /**
     * Reeds in a course by its name and semester. These fields can uniquely
     * identify a course.
     */
    Optional<Course> readByCourseNameAndSemester(String name, Semester semester);
}
