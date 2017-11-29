package era.server.data;

import era.server.data.model.Assignment;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Provides CRUD functionality for {@link Assignment} objects. We made an interface
 * for to make unit tests easier.
 */
@ParametersAreNonnullByDefault
public interface AssignmentDAO {
    void storeAssignment(Assignment assignment);
}
