package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.database.jooq.tables.records.CourseStudentRecord;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Grader;
import era.uploader.data.model.Student;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;
import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;
/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * database. A course has many {@link Student}s and many {@link Grader}s.
 */
public class CourseDAOImpl implements CourseDAO, DatabaseDAO<CourseRecord, Course> {
    @Deprecated
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the database table */
    private static final StudentDAOImpl STUDENT_DAO = new StudentDAOImpl();

    /* Create and Insert a new Course object into the database */
    @Override
    public Course insert(Course course) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            course.setUniqueId(ctx.insertInto(
                    // table
                    COURSE,
                    // columns
                    COURSE.DEPARTMENT,
                    COURSE.COURSE_NUMBER,
                    COURSE.SECTION_NUMBER,
                    COURSE.NAME,
                    COURSE.SEMESTER
               )
               .values (
                       course.getDepartment(),
                       course.getCourseNumber(),
                       course.getSectionNumber(),
                       course.getName(),
                       course.getSemester()
               )
                    .returning(
                            COURSE.UNIQUE_ID
                    )
               .fetchOne().getUniqueId()
            );

            for (Student student : course.getStudentsEnrolled()) {
                student.setUniqueId(ctx.insertInto (
                        // table
                        STUDENT,
                        // columns
                        STUDENT.FIRST_NAME,
                        STUDENT.LAST_NAME,
                        STUDENT.USERNAME,
                        STUDENT.SCHOOL_ID,
                        STUDENT.EMAIL
                   )
                   .values (
                           student.getFirstName(),
                           student.getLastName(),
                           student.getUserName(),
                           student.getSchoolId(),
                           student.getEmail()
                   )
                        .returning(
                                STUDENT.UNIQUE_ID
                        )
                   .fetchOne().getUniqueId());

                ctx.insertInto(
                        // table
                        COURSE_STUDENT,
                        // columns
                        COURSE_STUDENT.COURSE_ID,
                        COURSE_STUDENT.STUDENT_ID
                   )
                   .values(
                           course.getUniqueId(),
                           student.getUniqueId()
                   )
                   .execute();
            }

            for (Assignment assignment : course.getAssignments()) {
                ctx.insertInto (
                        // table
                        ASSIGNMENT,
                        ASSIGNMENT.COURSE_ID,
                        ASSIGNMENT.NAME,
                        ASSIGNMENT.IMAGE_FILE_PATH
                   )
                   .values (
                        course.getUniqueId(),
                        assignment.getName(),
                        assignment.getImageFilePath()
                   )
                   .execute();
            }
        }

        return course;
    }

    @Override
    public void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents) {
        for(Map.Entry<Course, Collection<Student>> studentsInCourse:
                coursesToStudents.asMap().entrySet()) {
            Course course = studentsInCourse.getKey();
            Collection<Student> students = studentsInCourse.getValue();
            course.getStudentsEnrolled().addAll(students);
            for (Student student : students) {
                student.getCourses().add(course);
            }
        }

        courses.addAll(coursesToStudents.keys());
    }

    /* Access data from existing Course object from database */
    @Override
    public Course read(long id) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            CourseRecord courseRecord = ctx.selectFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq((int) id))
                    .fetchOne();

            return convertToModel(courseRecord);
        }
    }

    /* Modify data stored in already existing Course in database */
    @Override
    public void update(Course changedCourse) {
        try (DSLContext ctx = DSL.using(CONNECTION_STR)) {
            ctx.update(COURSE)
            .set(COURSE.COURSE_NUMBER, changedCourse.getCourseNumber())
            .set(COURSE.DEPARTMENT, changedCourse.getDepartment())
            .set(COURSE.NAME, changedCourse.getName())
            .set(COURSE.SECTION_NUMBER, changedCourse.getSectionNumber())
            .set(COURSE.SEMESTER, changedCourse.getSemester())
            .where(COURSE.UNIQUE_ID.eq(changedCourse.getUniqueId()))
            .execute();
        }
    }

    /* Delete existing Course object in database */
    @Override
    public void delete(long id) {
        try(DSLContext ctx = DSL.using(CONNECTION_STR)){
            ctx.deleteFrom(COURSE)
            .where(COURSE.UNIQUE_ID.eq((int)id))
            .execute();
        }

    }

    @Override
    public Set<Course> getAllCourses() {
        return courses;
    }

    @Override
    public Course convertToModel(CourseRecord record) {
        return Course.builder()
                .withName(record.getName())
                .withDatabaseId(record.getUniqueId())
                .withSemester(record.getSemester())
                .create(record.getDepartment(), record.getCourseNumber(), record.getSectionNumber());
    }

    @Override
    public CourseRecord convertToRecord(Course model, DSLContext ctx) {
        CourseRecord course = ctx.newRecord(COURSE);
        course.setCourseNumber(model.getCourseNumber());
        course.setDepartment(model.getDepartment());
        course.setName(model.getName());
        course.setSectionNumber(model.getSectionNumber());
        course.setSemester(model.getSemester());
        // unique id cannot be 0 because that is an invalid database id
        if (model.getUniqueId() != 0) {
            course.setUniqueId(model.getUniqueId());
        }
        return course;
    }
}
