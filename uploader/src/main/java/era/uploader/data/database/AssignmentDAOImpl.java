package era.uploader.data.database;


import com.google.common.collect.Sets;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;
import era.uploader.data.model.Assignment;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;

/**
 * Provides CRUD functionality for Assignments inside a database.
 */
public class AssignmentDAOImpl implements AssignmentDAO, DatabaseDAO<AssignmentRecord, Assignment> {
    private final Set<Assignment> db = new HashSet<>();

    public void storeAssignment(Assignment assignment) {
        db.add(assignment);
    }

    @Override
    public Assignment insert(Assignment assignment) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            assignment.setUniqueId(ctx.insertInto(
                    //table
                    ASSIGNMENT,
                    //columns
                    ASSIGNMENT.NAME,
                    ASSIGNMENT.IMAGE_FILE_PATH,
                    ASSIGNMENT.COURSE_ID,
                    ASSIGNMENT.STUDENT_ID
                    )
                    .values(
                            assignment.getName(),
                            assignment.getImageFilePath(),
                            assignment.getCourse().getUniqueId(),
                            assignment.getStudent().getUniqueId()
                    )
                    .returning(
                            ASSIGNMENT.UNIQUE_ID
                    )
                    .fetchOne().getUniqueId()
            );
        }

        return assignment;
    }

    @Override
    public Assignment read(long id) {
        Assignment x = new Assignment();
        return x;
    }

    @Override
    public void delete(Assignment assignment) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.deleteFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.UNIQUE_ID.eq(assignment.getUniqueId()))
                    .execute();
        }
    }

    @Override
    public Assignment convertToModel(AssignmentRecord record) {
        Assignment newAssignment = new Assignment(
                record.getImageFilePath(),
                record.getName(),
                record.getCourseId(),
                record.getStudentId(),
                record.getUniqueId()
        );

        return newAssignment;
    }



    @Override
    public AssignmentRecord convertToRecord(Assignment model, DSLContext ctx) {
        AssignmentRecord assignment = ctx.newRecord(ASSIGNMENT);
        assignment.setCourseId(model.getCourse_id());
        assignment.setImageFilePath(model.getImageFilePath());
        assignment.setName(model.getName());
        assignment.setStudentId(model.getStudent_id());

        if (model.getUniqueId() != 0) {
            assignment.setUniqueId(model.getUniqueId());
        }

        return assignment;
    }
}
