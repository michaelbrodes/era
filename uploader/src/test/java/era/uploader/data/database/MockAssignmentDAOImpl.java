package era.uploader.data.database;

import com.google.common.collect.Sets;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;

public class MockAssignmentDAOImpl implements AssignmentDAO{
    private Set<Assignment> db = Sets.newHashSet();
    private static int idCounter = 0;

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

    @Override
    public Collection<Assignment> fromCourse(Course model) {
        return model.getAssignments();
    }

    @Override
    public void delete(Assignment assignment) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.deleteFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.UNIQUE_ID.eq(assignment.getUniqueId()))
                    .execute();
        }
    }
}
