package era.uploader.data.database;

import com.google.common.collect.Multimap;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.CourseConverter;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.database.jooq.tables.records.SemesterRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Grader;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;
import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.SEMESTER;
import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * database. A course has many {@link Student}s and many {@link Grader}s.
 */
public class CourseDAOImpl extends DatabaseDAO<CourseRecord, Course> implements CourseDAO {
    private static final CourseConverter CONVERTER = CourseConverter.INSTANCE;
    private static CourseDAO INSTANCE;
    @Deprecated
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the database table */

    private CourseDAOImpl() {
    }

    /* Create and Insert a new Course object into the database */
    @Override
    public Course insert(Course course) {
        try (DSLContext ctx = connect()) {
            final Semester semesterToResolve = course.getSemesterObj();
            Condition filterer = DSL.and(
                    SEMESTER.TERM.eq(semesterToResolve.getTermString()),
                    SEMESTER.YEAR.eq(semesterToResolve.getYearInt())
            );

            // try to find the semester if it exists, otherwise insert it
            Semester semester = ctx.selectFrom(SEMESTER)
                    .where(filterer)
                    .fetchOptional()
                    .orElseGet(() -> {
                        SemesterRecord semesterRecord = ctx.insertInto(
                                SEMESTER,
                                SEMESTER.TERM,
                                SEMESTER.YEAR
                        ).values(
                                semesterToResolve.getTermString(),
                                semesterToResolve.getYearInt()
                        ).returning(
                                SEMESTER.UNIQUE_ID
                        ).fetchOne();

                        semesterRecord.setTerm(semesterToResolve.getTermString());
                        semesterRecord.setYear(semesterToResolve.getYearInt());
                        return semesterRecord;
                    })
                    .map((record) -> {
                        SemesterRecord semesterRecord = record.into(SEMESTER);
                        return new Semester(
                                semesterRecord.getUniqueId(),
                                semesterRecord.getTerm(),
                                semesterRecord.getYear()
                        );
                    });

            course.setUniqueId(
                    ctx.insertInto(
                            // table
                            COURSE,
                            // columns
                            COURSE.DEPARTMENT,
                            COURSE.COURSE_NUMBER,
                            COURSE.SECTION_NUMBER,
                            COURSE.NAME,
                            COURSE.SEMESTER_ID
                    ).values(
                            course.getDepartment(),
                            course.getCourseNumber(),
                            course.getSectionNumber(),
                            course.getName(),
                            semester.getUniqueId()
                    ).returning(
                            COURSE.UNIQUE_ID
                    ).fetchOne()
                            .getUniqueId()
            );

            for (Student student : course.getStudentsEnrolled()) {
                student.setUniqueId(ctx.insertInto(
                        // table
                        STUDENT,
                        // columns
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
                ctx.insertInto(
                        // table
                        ASSIGNMENT,
                        ASSIGNMENT.COURSE_ID,
                        ASSIGNMENT.NAME,
                        ASSIGNMENT.IMAGE_FILE_PATH
                )
                        .values(
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
        for (Map.Entry<Course, Collection<Student>> studentsInCourse :
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
        try (DSLContext ctx = connect()) {
            CourseRecord courseRecord = ctx.selectFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq((int) id))
                    .fetchOne();

            return convertToModel(courseRecord);
        }
    }

    /* Modify data stored in already existing Course in database */
    @Override
    public void update(Course changedCourse) {
        try (DSLContext ctx = connect()) {
            Semester semester = changedCourse.getSemesterObj();

            // one is not a valid database id
            if (semester.getUniqueId() < 1) {
                Condition filterer = DSL.and(
                        SEMESTER.TERM.eq(semester.getTermString()),
                        SEMESTER.YEAR.eq(semester.getYearInt())
                );
                semester.setUniqueId(
                        ctx.select(SEMESTER.UNIQUE_ID)
                                .from(SEMESTER)
                                .where(filterer)
                                .fetchOptional()
                                .map(Record1::value1)
                                .orElseGet(() -> ctx.insertInto(SEMESTER, SEMESTER.TERM, SEMESTER.YEAR)
                                        .values(semester.getTermString(), semester.getYearInt())
                                        .returning(SEMESTER.UNIQUE_ID)
                                        .fetchOne()
                                        .getUniqueId()
                                )
                );
            }

            ctx.update(COURSE)
                    .set(COURSE.COURSE_NUMBER, changedCourse.getCourseNumber())
                    .set(COURSE.DEPARTMENT, changedCourse.getDepartment())
                    .set(COURSE.NAME, changedCourse.getName())
                    .set(COURSE.SECTION_NUMBER, changedCourse.getSectionNumber())
                    .set(COURSE.SEMESTER_ID, semester.getUniqueId())
                    .where(COURSE.UNIQUE_ID.eq(changedCourse.getUniqueId()))
                    .execute();
        }
    }

    /* Delete existing Course object in database */
    @Override
    public void delete(long id) {
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq((int) id))
                    .execute();
        }

    }

    @Override
    public Set<Course> getAllCourses() {
        return courses;
    }

    @Override
    public Set<Course> fromStudent(Student student) {
        try(DSLContext ctx = connect()) {
            return ctx.selectFrom(COURSE_STUDENT)
                    .where(COURSE_STUDENT.STUDENT_ID.eq(student.getUniqueId()))
                    .fetch()
                    .stream()
                    .map((junction) -> ctx.selectFrom(COURSE)
                            .where(COURSE.UNIQUE_ID.eq(junction.getCourseId()))
                            .fetchOne())
                    .map(CONVERTER::convert)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public Course fromAssignment(Assignment assignment) {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq(assignment.getCourse_id()))
                    .fetchOne()
                    .into(Course.class);
        }
    }

    @Override
    public Course convertToModel(CourseRecord record) {
        Optional<Course> course = Optional.ofNullable(CONVERTER.convert(record));
        final StudentDAO studentDAO = StudentDAOImpl.instance();
        final AssignmentDAO assignmentDAO = AssignmentDAOImpl.instance();
        course.ifPresent((model) -> {
            model.getStudentsEnrolled().addAll(studentDAO.fromCourse(model));
            model.getAssignments().addAll(assignmentDAO.fromCourse(model));
        });
        return course.orElse(null);
    }

    @Override
    @Deprecated
    public CourseRecord convertToRecord(Course model, DSLContext ctx) {
        CourseRecord course = ctx.newRecord(COURSE);
        course.setCourseNumber(model.getCourseNumber());
        course.setDepartment(model.getDepartment());
        course.setName(model.getName());
        course.setSectionNumber(model.getSectionNumber());
        // unique id cannot be 0 because that is an invalid database id
        if (model.getUniqueId() != 0) {
            course.setUniqueId(model.getUniqueId());
        }
        return course;
    }

    @Override
    public CourseRecord convertToRecord(Course model) {
        return CONVERTER.reverse().convert(model);
    }

    public static CourseDAO instance() {
        if (INSTANCE == null) {
            synchronized (CourseDAO.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CourseDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}
