package era.uploader.data.database;

import era.uploader.data.TeacherDAO;
import era.uploader.data.database.jooq.tables.records.TeacherRecord;
import era.uploader.data.model.Teacher;
import org.jooq.DSLContext;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

import static era.uploader.data.database.jooq.Tables.TEACHER;

public class TeacherDAOImpl extends DatabaseDAO<TeacherRecord, Teacher>implements TeacherDAO {
    private static TeacherDAO INSTANCE;

    private TeacherDAOImpl() {
        // no op
    }

    @Override
    public Set<Teacher> getAllTeachers() {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(TEACHER)
                    .fetchStream()
                    .map(this::convertToModel)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public Teacher insert(Teacher newTeacher) {
        try (DSLContext ctx = connect()) {
            int insertedId = ctx.insertInto(
                    TEACHER,
                    TEACHER.NAME)
                    .values(newTeacher.getName())
                    .returning(TEACHER.UNIQUE_ID)
                    .fetchOne()
                    .getUniqueId();
            newTeacher.setUniqueId(insertedId);
            return newTeacher;
        }
    }

    @Nullable
    @Override
    public Teacher convertToModel(@Nullable TeacherRecord record) {
        if (record == null) {
            return null;
        } else {
            return new Teacher(record.getUniqueId(), record.getName());
        }
    }

    @Nullable
    @Override
    public TeacherRecord convertToRecord(@Nullable Teacher teacher) {
        return teacher == null
                ? null
                : new TeacherRecord(teacher.getUniqueId(), teacher.getName());
    }

    public static TeacherDAO instance() {
        if (INSTANCE == null) {
            synchronized (TeacherDAO.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TeacherDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}
