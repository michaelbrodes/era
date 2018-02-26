package era.server.data;

import era.server.data.model.Assignment;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Set;

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

    Optional<Assignment> fetch(String uuid);

    Set<Assignment> fetchAllByStudent(String username);
}
