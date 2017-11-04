package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.model.Assignment;

import java.util.Set;

public class MockAssignmentDAOImpl implements AssignmentDAO{
    Set<Assignment> db = Sets.newHashSet();
    static int idCounter = 0;

    @Override
    public void storeAssignment(Assignment assignment) {
        idCounter++;
        assignment.setUniqueId(idCounter);
        db.add(assignment);
    }

    @Override
    public Assignment insert(Assignment assignment) {
        db.add(assignment);
        idCounter++;
        assignment.setUniqueId(idCounter);
        return assignment;
    }

    @Override
    public Assignment read(long id) {
        return db.stream()
                .filter(assignment -> assignment.getUniqueId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Assignment changedAssignment) {
        Assignment prevAssignment = read(changedAssignment.getUniqueId());
        db.remove(prevAssignment);
        db.add(changedAssignment);
    }
}
