package era.server.data.access;


import era.server.api.UUIDGenerator;
import era.server.data.StudentDAO;
import era.server.data.database.tables.records.StudentRecord;
import era.server.data.model.Assignment;
import era.server.data.model.Student;
import org.jooq.Condition;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;
import java.util.Optional;

import static era.server.data.database.Tables.ASSIGNMENT;
import static era.server.data.database.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * access
 */
@ParametersAreNonnullByDefault
public class StudentDAOImpl extends DatabaseDAO implements StudentDAO {
    private static StudentDAO INSTANCE;

    /**
     * private No-op constructor so we can't construct instances without using
     * {@link #instance()}
     */
    private StudentDAOImpl() {

    }

    public Student resolveStudent(Student student) {
        try (DSLContext ctx = connect()) {
            Condition filterer = STUDENT.USERNAME.eq(student.getUserName());
            Optional<StudentRecord> maybeStudent = ctx.selectFrom(STUDENT)
                    .where(filterer)
                    .fetchOptional();
            String uuid;

            if (!maybeStudent.isPresent()) {
                uuid = UUIDGenerator.uuid();
                ctx.insertInto(
                        STUDENT,
                        STUDENT.UUID,
                        STUDENT.USERNAME,
                        STUDENT.EMAIL
                )
                        .values(
                                uuid,
                                student.getUserName(),
                                student.getEmail()
                        )
                        .execute();
            } else {
                uuid = maybeStudent.get().getUuid();
            }

            return Student.builder()
                    .withEmail(student.getEmail())
                    .withUUID(uuid)
                    .create(student.getUserName());
        }
    }

    /**
     * Create and Insert a new Student object into the database
     */
     @Override
     public void insert(Student student) {
         try (DSLContext create = connect()) {
             create.insertInto(
                     STUDENT,
                     STUDENT.UUID,
                     STUDENT.EMAIL,
                     STUDENT.USERNAME
             ).values(
                     student.getUuid(),
                     student.getEmail(),
                     student.getUserName()
             )
             .execute();
         }
     }

     private Optional<Student> findStudent(Condition filterer, boolean fetchAssignments) {
         try (DSLContext create = connect()) {
             Optional<Student> student = create.selectFrom(STUDENT)
                     .where(filterer)
                     .fetchOptional()
                     .map((record) -> Student.builder()
                             .withEmail(record.getEmail())
                             .withUUID(record.getUuid())
                             .create(record.getUsername())
                     );
             if (student.isPresent() && fetchAssignments) {
                 List<Assignment> studentAssignments = create.selectFrom(ASSIGNMENT)
                         .where(ASSIGNMENT.STUDENT_ID.eq(student.get().getUuid()))
                         .fetch()
                         .map((assignmentRecord) -> Assignment.builder()
                                 .withCreatedDateTime(assignmentRecord
                                         .getCreatedDateTime()
                                         .toLocalDateTime())
                                 .withImageFilePath(assignmentRecord.getImageFilePath())
                                 .withStudent(student.get())
                                 .create(assignmentRecord.getName(), assignmentRecord.getUuid()));
                 student.get().getAssignments().addAll(studentAssignments);
             }

             return student;
         }
     }

    /**
     * Access data from existing Student object from access
     */
    @Override
    @Nullable
    public Student read(String id) {
        return findStudent(STUDENT.UUID.eq(id), true).orElse(null);
    }

    @Override
    public Optional<Student> readByUsername(String username) {
        return findStudent(STUDENT.USERNAME.eq(username), false);
    }

    public static StudentDAO instance() {
        if (INSTANCE == null) {
            synchronized (StudentDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StudentDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}