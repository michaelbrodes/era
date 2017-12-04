package era.uploader.data.database;

import com.google.common.base.Preconditions;
import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.StudentConverter;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
@ParametersAreNonnullByDefault
public class StudentDAOImpl extends DatabaseDAO<StudentRecord, Student> implements StudentDAO {
    public static StudentDAOImpl INSTANCE;
    private static final StudentConverter CONVERTER = StudentConverter.INSTANCE;

    private StudentDAOImpl() {

    }

    /**
     * Create and Insert a new Student object into the database as
     * well as any Courses the student is associated with by default
     */
    public void insert(@Nonnull Student student) {
        Preconditions.checkNotNull(student, "Cannot insert a null student");
        try (DSLContext ctx = connect()) {
            student.setUniqueId(ctx.insertInto(
                    //table
                    STUDENT,
                    //columns
                    STUDENT.FIRST_NAME,
                    STUDENT.LAST_NAME,
                    STUDENT.USERNAME,
                    STUDENT.SCHOOL_ID,
                    STUDENT.EMAIL
                    )
                    .values(
                            student.getFirstName(),
                            student.getLastName(),
                            student.getUserName(),
                            student.getSchoolId(),
                           student.getEmail()
                    )
                    .returning(
                            STUDENT.UNIQUE_ID
                    )
                    .fetchOne()
                    .getUniqueId()
            );
            for (Course course : student.getCourses()) {
                ctx.insertInto(
                        //table
                        COURSE_STUDENT,
                        //columns
                        COURSE_STUDENT.COURSE_ID,
                        COURSE_STUDENT.STUDENT_ID
                )
                        .values(
                                course.getUniqueId(),
                                student.getUniqueId()
                        )
                        .execute();
            }
        }
     }

    /* Access data from existing Student object from database */
    @Nullable
    public Student read(long id) {
        try (DSLContext ctx = connect()) {
            StudentRecord studentRecord = ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq((int)id))
                    .fetchOne();

            return convertToModel(studentRecord);
        }
    }

    /* Modify data stored in already existing Student in database */
    public void update(@Nonnull Student changedStudent) {
        Preconditions.checkNotNull(changedStudent, "cannot update a null student");
        try (DSLContext ctx = connect()) {
            ctx.update(STUDENT)
                    .set(STUDENT.FIRST_NAME, changedStudent.getFirstName())
                    .set(STUDENT.LAST_NAME, changedStudent.getLastName())
                    .set(STUDENT.EMAIL, changedStudent.getEmail())
                    .set(STUDENT.SCHOOL_ID, changedStudent.getSchoolId())
                    .set(STUDENT.USERNAME, changedStudent.getUserName())
                    .where(STUDENT.UNIQUE_ID.eq(changedStudent.getUniqueId()))
                    .execute();
        }
    }

    /* Delete existing Student object in database */
    public void delete(long id) {
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq((int)id))
                    .execute();
        }

    }

    @Override
    public Collection<Student> fromCourse(@Nonnull Course course) {
        Preconditions.checkNotNull(course, "cannot grab a student from a null course");
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(COURSE_STUDENT)
                    .where(COURSE_STUDENT.COURSE_ID.eq(course.getUniqueId()))
                    .fetch()
                    .stream()
                    .map((junction) -> ctx.selectFrom(STUDENT)
                                .where(STUDENT.UNIQUE_ID.eq(junction.getStudentId()))
                                .fetchOne())
                    .map(CONVERTER::convert)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Student fromQRMapping(@Nonnull QRCodeMapping mapping) {
        Preconditions.checkNotNull(mapping, "Cannot grab a student from a null qrCodeMapping");
        try (DSLContext ctx = connect()) {
            StudentRecord student = ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq(mapping.getStudentId()))
                    .fetchOne();
                    return CONVERTER.convert(student);
        }
    }

    @Override
    public Student fromAssignment(@Nonnull Assignment assignment) {
        Preconditions.checkNotNull(assignment, "Cannot grab a student from a null assignment");
        try (DSLContext ctx = connect()) {
            StudentRecord student = ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq(assignment.getStudent_id()))
                    .fetchOne();
            return CONVERTER.convert(student);
        }
    }

    @Override
    @Nullable
    public Student convertToModel(@Nullable StudentRecord record) {
        Optional<Student> newStudent = Optional.ofNullable(
                CONVERTER.convert(record)
        );
        CourseDAO courseDAO = CourseDAOImpl.instance();
        newStudent.ifPresent((student) -> student.setCourses(
                courseDAO.fromStudent(student)
        ));
        return newStudent.orElse(null);
    }

    @Override
    @Nullable
    public StudentRecord convertToRecord(@Nullable Student model) {
        return CONVERTER.reverse().convert(model);
    }

    public static StudentDAOImpl instance() {
        if (INSTANCE == null) {
            synchronized (QRCodeMappingDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StudentDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}