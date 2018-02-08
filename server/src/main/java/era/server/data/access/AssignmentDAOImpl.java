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
    private static AssignmentDAO INSTANCE;

    /**
     * private No-op constructor so we can't construct instances without using
     * {@link #instance()}
     */
    private AssignmentDAOImpl() {

    }

    public void storeAssignment(Assignment assignment) {
        Preconditions.checkNotNull(assignment);
        try (DSLContext ctx = connect()) {
            String courseId = assignment.getCourse() == null ?
                    assignment.getCourse_id() :
                    assignment.getCourse().getUuid();
            String studentId = assignment.getStudent() == null ?
                    assignment.getStudent_id() :
                    assignment.getStudent().getUuid();
            ctx.insertInto(
                    ASSIGNMENT,
                    ASSIGNMENT.UUID,
                    ASSIGNMENT.NAME,
                    ASSIGNMENT.IMAGE_FILE_PATH,
                    ASSIGNMENT.COURSE_ID,
                    ASSIGNMENT.STUDENT_ID,
                    ASSIGNMENT.CREATED_DATE_TIME
            ).values(
                    assignment.getUuid(),
                    assignment.getName(),
                    assignment.getImageFilePath(),
                    courseId,
                    studentId,
                    assignment.getCreatedDateTimeStamp()
            ).execute();
        }
    }

    public static AssignmentDAO instance() {
        if (INSTANCE == null) {
            synchronized (AssignmentDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AssignmentDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}
