package era.server.data;

import era.server.data.model.Assignment;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Provides CRUD functionality for {@link Assignment} objects. We made an interface
 * for to make unit tests easier.
 */
@ParametersAreNonnullByDefault
public interface AssignmentDAO {
    /**
     * Insert a new assignment into the database.
     *
     * @param assignment the assignment to insert.
     */
    void storeAssignment(Assignment assignment);
}
