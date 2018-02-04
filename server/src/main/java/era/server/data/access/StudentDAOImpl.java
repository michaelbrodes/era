package era.server.data.access;


import era.server.data.StudentDAO;
import era.server.data.model.Course;
import era.server.data.model.Student;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * Create and Insert a new Student object into the database
     */
     @Override
     public void insert(Student student) {
         try (DSLContext create = connect()) {
             create.insertInto(
                     STUDENT,
                     STUDENT.UNIQUE_ID,
                     STUDENT.EMAIL,
                     STUDENT.USERNAME
             ).values(
                     student.getUniqueId(),
                     student.getEmail(),
                     student.getUserName()
             )
             .execute();
         }
     }

    /**
     * Access data from existing Student object from access
     */
    @Override
    @Nullable
    public Student read(long id) {
        try (DSLContext create = connect()) {
            return create.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq(id))
                    .fetchOptional()
                    .map((record) -> Student.builder()
                            .withEmail(record.getEmail())
                            .withUniqueId(record.getUniqueId())
                            .create(record.getUsername())
                    )
                    .orElse(null);
        }
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