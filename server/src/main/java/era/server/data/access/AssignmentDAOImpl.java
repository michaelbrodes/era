package era.server.data.access;

import com.google.common.base.Preconditions;
import era.server.data.AssignmentDAO;
import era.server.data.model.Assignment;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record5;
import org.jooq.Result;

import javax.annotation.ParametersAreNonnullByDefault;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static era.server.data.database.Tables.ASSIGNMENT;
import static era.server.data.database.Tables.COURSE;
import static era.server.data.database.Tables.STUDENT;

/**
 * Provides CRUD functionality for Assignments inside a access.
 */
@ParametersAreNonnullByDefault
public class AssignmentDAOImpl extends DatabaseDAO implements AssignmentDAO {
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

    @Override
    public Collection<Assignment> fetchAllByStudent(String username) {
        try (DSLContext create = connect()) {
           ArrayList<Assignment> assignments = new ArrayList<>();
            Result<Record5<String, String, Timestamp, String, String>> result = create.select(ASSIGNMENT.UUID, ASSIGNMENT.NAME, ASSIGNMENT.CREATED_DATE_TIME, COURSE.NAME, STUDENT.USERNAME)
                    .from(ASSIGNMENT)
                    .join(STUDENT)
                    .on(ASSIGNMENT.STUDENT_ID.eq(STUDENT.UUID))
                    .join(COURSE)
                    .on(ASSIGNMENT.COURSE_ID.eq(COURSE.UUID))
                    .where(STUDENT.USERNAME.eq(username))
                    .orderBy(ASSIGNMENT.CREATED_DATE_TIME.desc())
                    .fetch();

            for (Record5<String, String, Timestamp, String, String> record: result) {

                Assignment assignment = Assignment.builder()
                        .withStudentUname(record.get(STUDENT.USERNAME))
                        .withCourseName(record.get(COURSE.NAME))
                        .withCreatedDateTime(record.get(ASSIGNMENT.CREATED_DATE_TIME).toLocalDateTime())
                        .create(record.get(ASSIGNMENT.NAME), record.get(ASSIGNMENT.UUID));

                assignments.add(assignment);


            }

            return assignments;
        }

    }

    @Override
    public Optional<Assignment> fetch(String uuid) {
        try (DSLContext create = connect()) {
            return create.selectFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.UUID.eq(uuid))
                    .fetchOptional()
                    .map(dbAssignment -> Assignment.builder()
                            .withImageFilePath(dbAssignment.getImageFilePath())
                            .withCreatedDateTime(dbAssignment.getCreatedDateTime().toLocalDateTime())
                            .withStudent_id(dbAssignment.getStudentId())
                            .withCourse_id(dbAssignment.getCourseId())
                            .create(dbAssignment.getName(), dbAssignment.getUuid()));
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
