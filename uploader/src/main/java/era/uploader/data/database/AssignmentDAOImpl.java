package era.uploader.data.database;


import com.google.common.collect.Sets;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;
import era.uploader.data.model.Assignment;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.HashSet;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;

/**
 * Provides CRUD functionality for Assignments inside a database.
 */
public class AssignmentDAOImpl implements AssignmentDAO {
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
                    ASSIGNMENT.COURSE_ID
                    )
                    .values(
                            assignment.getName(),
                            assignment.getImageFilePath(),
                            assignment.getCourse().getUniqueId()
                    )
                    .returning(
                            ASSIGNMENT.UNIQUE_ID
                    )
                    .fetchOne().getUniqueId()
            );
        }

        return assignment;
    }
}
