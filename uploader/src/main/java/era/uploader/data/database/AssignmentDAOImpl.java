package era.uploader.data.database;


import com.google.common.collect.Sets;
import era.uploader.data.model.Assignment;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides CRUD functionality for Assignments inside a database.
 */
public class AssignmentDAOImpl {
    private final Set<Assignment> db = new HashSet<>();

    public void storeAssignment(Assignment assignment) {
        db.add(assignment);
    }
}
