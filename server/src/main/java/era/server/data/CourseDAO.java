package era.server.data;

import era.server.data.model.Course;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;

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
    void insert(Course course) throws SQLException;

    /**
     * Access data from existing Course object from access
     */
    @Nullable
    Course read(String id);
}
