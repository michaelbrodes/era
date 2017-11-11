package era.uploader.data.database;

import era.uploader.data.StudentDAO;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.HashSet;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
public class StudentDAOImpl implements StudentDAO, DatabaseDAO<StudentRecord, Student> {
    @Deprecated
    private static int sequenceNum = 0; /* Used to make sure each Student gets their own unique id. */
    @Deprecated
    private Set<Student> students = new HashSet<>(); /* A set of students to act as the database table */

    /* Create and Insert a new Student object into the database as
     * well as any Courses the student is associated with by default
     */
    public void insert(Student student) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
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
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            StudentRecord studentRecord = ctx.selectFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq((int)id))
                    .fetchOne();

            return convertToModel(studentRecord);
        }
    }

    /* Modify data stored in already existing Student in database */
    public void update(Student changedStudent) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
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
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.deleteFrom(STUDENT)
                    .where(STUDENT.UNIQUE_ID.eq((int)id))
                    .execute();
        }

    }

    @Override
    public Student convertToModel(StudentRecord record) {
        Student newStudent = new Student(
                record.getFirstName(),
                record.getLastName(),
                record.getSchoolId(),
                record.getUsername(),
                record.getUniqueId());
        return newStudent;
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
}