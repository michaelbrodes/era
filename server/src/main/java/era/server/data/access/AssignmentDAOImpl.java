package era.server.data.access;

import com.google.common.base.Preconditions;
import era.server.data.AssignmentDAO;
import era.server.data.model.Assignment;
import org.jooq.DSLContext;

import javax.annotation.ParametersAreNonnullByDefault;

import static era.server.data.database.Tables.ASSIGNMENT;

/**
 * Provides CRUD functionality for Assignments inside a access.
 */
@ParametersAreNonnullByDefault
public class AssignmentDAOImpl extends DatabaseDAO implements AssignmentDAO{

    public void storeAssignment(Assignment assignment) {
        Preconditions.checkNotNull(assignment);
        try (DSLContext ctx = connect()) {
            Integer courseId = assignment.getCourse() == null ?
                    assignment.getCourse_id() :
                    assignment.getCourse().getUniqueId();
            Integer studentId = assignment.getStudent() == null ?
                    assignment.getStudent_id() :
                    assignment.getStudent().getUniqueId();
            ctx.insertInto(
                    ASSIGNMENT,
                    ASSIGNMENT.UNIQUE_ID,
                    ASSIGNMENT.NAME,
                    ASSIGNMENT.IMAGE_FILE_PATH,
                    ASSIGNMENT.COURSE_ID,
                    ASSIGNMENT.STUDENT_ID,
                    ASSIGNMENT.CREATED_DATE_TIME
            ).values(
                    assignment.getUniqueId(),
                    assignment.getName(),
                    assignment.getImageFilePath(),
                    (long) courseId,
                    (long) studentId,
                    assignment.getCreatedDateTimeStamp()
            );
        }
    }
}
