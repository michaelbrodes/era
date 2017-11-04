package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.*;
import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;


/**
 * This class is mock of {@link CourseDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link CourseDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockCourseDAOImpl implements CourseDAO, MockDAO<Course>, DatabaseDAO<CourseRecord, Course> {
    private Set<Course> courses = new HashSet<>();
    private static int idCount = 0;

    /* Create and Insert a new Course object into the database */
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
                //TODO: Ask michael why we arent setting the 'Assignment' uniqueId here
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
        courses.addAll(coursesToStudents.keys());
        for(Map.Entry<Course, Collection<Student>> studentsInCourse:
                coursesToStudents.asMap().entrySet()) {
            Course key = studentsInCourse.getKey();
            key.getStudentsEnrolled().addAll(studentsInCourse.getValue());
        }
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
    public Set<Course> getDb() {
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