package era.server.data;

import era.server.data.model.Assignment;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    Collection<Assignment> fetchAllByStudent(String username);

    Map<String, List<Assignment>> fetchAllAssignmentsGroupedByCourse();

    /**
     * Deletes an assignment from the database if one exists with the supplied
     * uuid.
     *
     * @param uuid the uuid of the assignment we want to delete.
     * @return whether or not we deleted the assignment
     */
    boolean delete(String uuid);
}
