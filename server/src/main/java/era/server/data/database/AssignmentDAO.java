package era.server.data.database;

import era.server.data.model.Assignment;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides CRUD functionality for Assignments inside a database.
 */
public class AssignmentDAO {
    private final Set<Assignment> db = new HashSet<>();

    public void storeAssignment(Assignment assignment) {
        db.add(assignment);
    }
}
