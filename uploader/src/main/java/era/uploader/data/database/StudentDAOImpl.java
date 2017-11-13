package era.uploader.data.database;

import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.StudentConverter;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
public class StudentDAOImpl extends DatabaseDAO<StudentRecord, Student> implements StudentDAO {
    public static StudentDAO INSTANCE;
    private static final StudentConverter CONVERTER = StudentConverter.INSTANCE;

    private StudentDAOImpl() {

    }

    /**
     * Create and Insert a new Student object into the database as
     * well as any Courses the student is associated with by default
     */
    public void insert(Student student) {
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
    public Student read(long id) {
        try (DSLContext ctx = connect()) {
            StudentRecord studentRecord = ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq((int)id))
                    .fetchOne();

            return convertToModel(studentRecord);
        }
    }

    /* Modify data stored in already existing Student in database */
    public void update(Student changedStudent) {
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
    public Collection<Student> fromCourse(Course course) {
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
    public Student fromQRMapping(QRCodeMapping mapping) {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq(mapping.getStudentId()))
                    .fetchOne()
                    .into(Student.class);
        }
    }

    @Override
    public Student fromAssignment(Assignment assignment) {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq(assignment.getStudent_id()))
                    .fetchOne()
                    .into(Student.class);
        }
    }

    @Override
    public Student convertToModel(StudentRecord record) {
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
    public StudentRecord convertToRecord(Student model, DSLContext ctx) {
        StudentRecord studentRecord = ctx.newRecord(STUDENT);
        studentRecord.setEmail(model.getEmail());
        studentRecord.setFirstName(model.getFirstName());
        studentRecord.setLastName(model.getLastName());
        studentRecord.setSchoolId(model.getSchoolId());
        studentRecord.setUsername(model.getUserName());
        if (model.getUniqueId() != 0) {
            studentRecord.setUniqueId(model.getUniqueId());
        }
        return studentRecord;
    }

    @Override
    public StudentRecord convertToRecord(Student model) {
        return CONVERTER.reverse().convert(model);
    }

    public static StudentDAO instance() {
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