package era.server.data.access;

import era.server.api.UUIDGenerator;
import era.server.data.AdminDAO;
import era.server.data.database.tables.records.StudentRecord;
import era.server.data.model.Admin;
import era.server.data.model.Student;
import org.jooq.DSLContext;

import java.util.Optional;

import static era.server.data.database.Tables.ADMIN;
import static era.server.data.database.Tables.STUDENT;

public class AdminDAOImpl extends DatabaseDAO implements AdminDAO {
    private static AdminDAO INSTANCE;

    private AdminDAOImpl() {
        // no op
    }

    public static AdminDAO instance() {
        if (INSTANCE == null) {
            synchronized (AdminDAO.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdminDAOImpl();
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public Optional<Admin> fetchByStudentId(String studentId) {
        try (DSLContext create = connect()) {
            return create.select(STUDENT.UUID, STUDENT.EMAIL, STUDENT.USERNAME)
                    .from(ADMIN)
                    .join(STUDENT)
                    .on(ADMIN.STUDENT_ID.eq(STUDENT.UUID))
                    .where(ADMIN.STUDENT_ID.eq(studentId))
                    .fetchOptional()
                    .map((record) -> Admin.builder()
                            .withUUID(record.get(STUDENT.UUID))
                            .withEmail(record.get(STUDENT.EMAIL))
                            .create(record.get(STUDENT.USERNAME)));
        }
    }

    @Override
    public Optional<Admin> fetchByUsername(String username) {
        try(DSLContext create = connect()) {
            return create.select(STUDENT.UUID, STUDENT.EMAIL, STUDENT.USERNAME)
                    .from(ADMIN)
                    .join(STUDENT)
                    .on(ADMIN.STUDENT_ID.eq(STUDENT.UUID))
                    .where(STUDENT.USERNAME.eq(username))
                    .fetchOptional()
                    .map((record) -> Admin.builder()
                            .withUUID(record.get(STUDENT.UUID))
                            .withEmail(record.get(STUDENT.EMAIL))
                            .create(record.get(STUDENT.USERNAME)));
        }
    }

    @Override
    public boolean storeAsAdmin(String student) {
        try (DSLContext create = connect()) {
            Optional<StudentRecord> studentRecord = create.selectFrom(STUDENT)
                    .where(STUDENT.USERNAME.eq(student))
                    .fetchOptional();
            int stored;
            if (studentRecord.isPresent()) {
                stored = create.insertInto(ADMIN, ADMIN.STUDENT_ID)
                        .values(studentRecord.get().getUuid())
                        .execute();
            } else {
                String uuid = UUIDGenerator.uuid();
                create.insertInto(STUDENT,
                        STUDENT.UUID,
                        STUDENT.USERNAME,
                        STUDENT.EMAIL)
                        .values(uuid, student, student + "@siue.edu")
                        .execute();
                stored = create.insertInto(ADMIN, ADMIN.STUDENT_ID)
                        .values(uuid)
                        .execute();
            }

            return stored != 0;
        }
    }
}
