package era.server.data;

import era.server.data.model.Course;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Provides CRUD access functionality for {@link Course} objects. Abstracted
 * to an interface, so that we can mock this for unit tests.
 */
@ParametersAreNonnullByDefault
public interface CourseDAO {
    void insert(Course course);

    /**
     * Access data from existing Course object from access
     */
    @Nullable
    Course read(long id);

    /**
     * Modify data stored in already existing Course in access
     */
    void update(Course courseToChange, Course courseChanged);

    /**
     * Delete existing Course object in access
     */
    void delete(Course course);
}
