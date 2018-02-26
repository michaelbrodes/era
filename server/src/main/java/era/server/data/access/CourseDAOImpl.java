package era.server.data.access;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import era.server.api.UUIDGenerator;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.database.tables.records.CourseRecord;
import era.server.data.database.tables.records.SemesterRecord;
import era.server.data.model.Assignment;
import era.server.data.model.Course;
import era.server.data.model.Semester;
import era.server.data.model.Student;
import era.server.data.model.Term;
import org.jooq.Condition;
import org.jooq.DSLContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Timestamp;
import java.time.Year;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static era.server.data.database.Tables.ASSIGNMENT;
import static era.server.data.database.Tables.COURSE;
import static era.server.data.database.Tables.COURSE_STUDENT;
import static era.server.data.database.Tables.SEMESTER;
import static era.server.data.database.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * access. A course has many {@link Student}s and many Grader.
 */
@ParametersAreNonnullByDefault
public class CourseDAOImpl extends DatabaseDAO implements CourseDAO {
    private static CourseDAO INSTANCE;
    private static final StudentDAO STUDENT_DAO = StudentDAOImpl.instance();

    /**
     * private No-op constructor so we can't construct instances without using
     * {@link #instance()}
     */
    private CourseDAOImpl() {

    }

    /**
     * Create and Insert a new Course object into the access
     */
    @Override
    public Course insert(Course course) {
        Preconditions.checkNotNull(course);
        course.setSemester(resolveSemester(course.getSemester()));
        Course dbCourse = resolveCourse(course);
        Set<Student> resolvedStudents = Sets.newHashSet();

        for (Student student: course.getStudentsEnrolled()) {
            Student dbStudent = STUDENT_DAO.resolveStudent(student);
            insertCourseStudent(dbCourse, dbStudent);
            resolvedStudents.add(dbStudent);
        }

        dbCourse.getStudentsEnrolled().clear();
        dbCourse.getStudentsEnrolled().addAll(resolvedStudents);

        return dbCourse;
    }

    @Override
    @Nonnull
    public Course resolveCourse (Course course) {
        try (DSLContext ctx = connect()) {
            Condition sameNameAndSemester = COURSE.NAME.eq(course.getName())
                    .and(COURSE.SEMESTER_ID.eq(course.getSemester().getUuid()));
            Optional<CourseRecord> maybeCourse = ctx.selectFrom(COURSE)
                    .where(sameNameAndSemester)
                    .fetchOptional();

            String uuid = UUIDGenerator.uuid();

            if (!maybeCourse.isPresent()) {
                ctx.insertInto(
                        COURSE,
                        COURSE.UUID,
                        COURSE.NAME,
                        COURSE.SEMESTER_ID)
                        .values(
                                uuid,
                                course.getName(),
                                course.getSemester().getUuid())
                        .execute();
            } else {
                uuid = maybeCourse.get().getUuid();
            }

            return Course.builder()
                    .withName(course.getName())
                    .withStudents(course.getStudentsEnrolled())
                    .withSemester(course.getSemester())
                    .withUuid(uuid)
                    .create();
        }
    }

    private Optional<Course> findCourse(Condition filterer) {
        try (DSLContext create = connect()) {
            Optional<Course.Builder> builder = create.select()
                    .from(COURSE)
                    .join(SEMESTER)
                    .on(SEMESTER.UUID.eq(COURSE.SEMESTER_ID))
                    .where(filterer)
                    .fetchOptional()
                    .map(record -> {
                        Term term = Term.valueOf(record.get(SEMESTER.TERM));
                        Year year = Year.of(record.get(SEMESTER.YEAR));

                        return Course.builder()
                                .withSemester(new Semester(record.get(SEMESTER.UUID), term, year))
                                .withName(record.get(COURSE.NAME))
                                .withUuid(record.get(COURSE.UUID));
                    });

            builder.ifPresent((courseBuilder) -> {
                Set<Student> studentSet = create.select(STUDENT.UUID, STUDENT.EMAIL, STUDENT.USERNAME)
                        .from(STUDENT)
                        .join(COURSE_STUDENT)
                        .on(COURSE_STUDENT.STUDENT_ID.eq(STUDENT.UUID))
                        .where(COURSE_STUDENT.COURSE_ID.eq(courseBuilder.getDatabaseId()))
                        .fetchLazy()
                        .stream()
                        .map(record -> {
                            String studentId = record.get(STUDENT.UUID);
                            String email = record.get(STUDENT.EMAIL);
                            String username = record.get(STUDENT.USERNAME);
                            return Student.builder()
                                    .withEmail(email)
                                    .withUUID(studentId)
                                    .create(username);
                        })
                        .collect(Collectors.toSet());
                courseBuilder.withStudents(studentSet);
            });

            builder.ifPresent((courseBuilder) -> {
                Set<Assignment> assignmentSet = create.select(
                        ASSIGNMENT.UUID,
                        ASSIGNMENT.NAME,
                        ASSIGNMENT.CREATED_DATE_TIME,
                        ASSIGNMENT.IMAGE_FILE_PATH,
                        ASSIGNMENT.COURSE_ID
                )
                        .from(ASSIGNMENT)
                        .where(ASSIGNMENT.COURSE_ID.eq(courseBuilder.getDatabaseId()))
                        .fetchLazy()
                        .stream()
                        .map(record -> {
                            String assignmentId = record.get(ASSIGNMENT.UUID);
                            String assignmentName = record.get(ASSIGNMENT.NAME);
                            Timestamp createdDateTime = record.get(ASSIGNMENT.CREATED_DATE_TIME);
                            String path = record.get(ASSIGNMENT.IMAGE_FILE_PATH);

                            return Assignment.builder()
                                    .withCourse_id(courseBuilder.getDatabaseId())
                                    .withCreatedDateTime(createdDateTime.toLocalDateTime())
                                    .withImageFilePath(path)
                                    .create(assignmentName, assignmentId);
                        })
                        .collect(Collectors.toSet());
                courseBuilder.withAssignments(assignmentSet);
            });

            return builder.map(Course.Builder::create);
        }
    }

    private Optional<Semester> findSemester(Condition filterer) {
        try (DSLContext create = connect()) {
            return create.selectFrom(SEMESTER)
                    .where(filterer)
                    .fetchOptional()
                    .map(semesterRecord -> new Semester(
                            semesterRecord.getUuid(),
                            Term.valueOf(semesterRecord.getTerm()),
                            Year.of(semesterRecord.getYear())));
        }
    }


    /* Access data from existing Course object from access */
    @Override
    @Nullable
    public Course read(String id) {
        return findCourse(COURSE.UUID.eq(id)).orElse(null);
    }

    @Override
    public Optional<Course> readByCourseNameAndSemester(String name, Semester semester) {
        Optional<Semester> dbSemester = findSemester(SEMESTER.TERM.eq(semester.getTermString())
                                                .and(SEMESTER.YEAR.eq(semester.getYearInt())));
        if (dbSemester.isPresent()) {
            return findCourse(COURSE.NAME.eq(name)
                         .and(COURSE.SEMESTER_ID.eq(dbSemester.get().getUuid())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<Course> readAllCoursesEnrolledIn(String byStudent) {
        try (DSLContext ctx = connect()) {
            return ctx.select(COURSE.UUID, COURSE.NAME)
                    .from(COURSE)
                    .join(COURSE_STUDENT)
                        .on(COURSE.UUID.eq(COURSE_STUDENT.COURSE_ID))
                    .join(STUDENT)
                        .on(COURSE_STUDENT.STUDENT_ID.eq(STUDENT.UUID))
                    .where(STUDENT.USERNAME.eq(byStudent))
                    .fetchStream()
                    .map(record -> Course.builder()
                            .withName(record.get(COURSE.NAME))
                            .create())
                    .collect(Collectors.toSet());
        }
    }

    private void insertCourseStudent(Course course, Student student) {
        try (DSLContext ctx = connect()) {
            ctx.insertInto(
                COURSE_STUDENT,
                COURSE_STUDENT.STUDENT_ID,
                COURSE_STUDENT.COURSE_ID
            )
            .values(
                student.getUuid(),
                course.getUuid()
            )
            .onDuplicateKeyIgnore()
            .execute();
        }
    }

    @Nonnull
    private Semester resolveSemester(@Nonnull Semester apiSemester) {
        try (DSLContext ctx = connect()) {
            Condition filter = SEMESTER.TERM.eq(apiSemester.getTermString())
                    .and(SEMESTER.YEAR.eq(apiSemester.getYearInt()));

            Optional<SemesterRecord> maybeSemester = ctx.selectFrom(SEMESTER)
                    .where(filter)
                    .fetchOptional();
            String uuid;

            if (!maybeSemester.isPresent()) {
                uuid = UUIDGenerator.uuid();
                ctx.insertInto(
                                SEMESTER,
                                SEMESTER.UUID,
                                SEMESTER.TERM,
                                SEMESTER.YEAR
                        )
                        .values(
                                uuid,
                                apiSemester.getTerm().name(),
                                apiSemester.getYearInt()
                        ).execute();
            } else {
                uuid = maybeSemester.get().getUuid();
            }

            return new Semester(uuid, apiSemester.getTerm(), apiSemester.getYear());
        }
    }

    public static CourseDAO instance() {
        if (INSTANCE == null) {
            synchronized (CourseDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CourseDAOImpl();
                }
            }
        }

        return INSTANCE;
    }
}
