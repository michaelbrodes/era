package era.server.data.access;

import com.google.common.base.Preconditions;
import era.server.data.CourseDAO;
import era.server.data.database.tables.records.SemesterRecord;
import era.server.data.model.Assignment;
import era.server.data.model.Course;
import era.server.data.model.Semester;
import era.server.data.model.Student;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
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
    public void insert(Course course) throws SQLException {
        Preconditions.checkNotNull(course);
        try (DSLContext create = connect()) {
            Semester dbSemester = resolveOrInsertSemester(course.getSemester());
            int affected = create.insertInto(
                           COURSE,
                           COURSE.UNIQUE_ID,
                           COURSE.SEMESTER_ID,
                           COURSE.NAME
                    )
                    .values(
                            course.getUniqueId(),
                            dbSemester.getUniqueId(),
                            course.getName()
                    )
                    .execute();
            if (affected == 0) {
                throw new SQLException("Insert of " + course.toString() + " failed");
            }

            for (Student student: course.getStudentsEnrolled()) {
                create.insertInto(
                        STUDENT,
                        STUDENT.EMAIL,
                        STUDENT.UNIQUE_ID,
                        STUDENT.USERNAME
                        )
                        .values(student.getEmail(), student.getUniqueId(), student.getUserName())
                        .execute();
                create.insertInto(
                        COURSE_STUDENT,
                        COURSE_STUDENT.COURSE_ID,
                        COURSE_STUDENT.STUDENT_ID
                        )
                        .values(course.getUniqueId(), student.getUniqueId())
                        .execute();
            }

            for (Assignment assignment: course.getAssignments()) {
                create.insertInto(
                                ASSIGNMENT,
                                ASSIGNMENT.NAME,
                                ASSIGNMENT.IMAGE_FILE_PATH,
                                ASSIGNMENT.CREATED_DATE_TIME,
                                ASSIGNMENT.UNIQUE_ID,
                                ASSIGNMENT.STUDENT_ID,
                                ASSIGNMENT.COURSE_ID
                        )
                        .values(
                                assignment.getName(),
                                assignment.getImageFilePath(),
                                assignment.getCreatedDateTimeStamp(),
                                assignment.getUniqueId(),
                                assignment.getStudent_id(),
                                assignment.getCourse_id()
                        )
                        .execute();
            }
        }
    }


    /* Access data from existing Course object from access */
    @Override
    @Nullable
    public Course read(long id) {
        try (DSLContext create = connect()) {
            Optional<Course.Builder> builder = create.select()
                    .from(COURSE)
                    .join(SEMESTER)
                    .on(SEMESTER.UNIQUE_ID.eq(COURSE.SEMESTER_ID))
                    .where(COURSE.UNIQUE_ID.eq(id))
                    .fetchOptional()
                    .map(record -> {
                        Semester.Term term = Semester.Term.valueOf(record.get(SEMESTER.TERM));
                        Year year = Year.of(record.get(SEMESTER.YEAR));

                        return Course.builder()
                                .withDatabaseId(record.get(COURSE.UNIQUE_ID))
                                .withSemester(Semester.of(term, year))
                                .withName(record.get(COURSE.NAME));
                    });

            builder.ifPresent((courseBuilder) -> {
                Set<Student> studentSet = create.select(STUDENT.UNIQUE_ID, STUDENT.EMAIL, STUDENT.USERNAME)
                        .from(STUDENT)
                        .join(COURSE_STUDENT)
                        .on(COURSE_STUDENT.STUDENT_ID.eq(STUDENT.UNIQUE_ID))
                        .where(COURSE_STUDENT.COURSE_ID.eq(courseBuilder.getDatabaseId()))
                        .fetchLazy()
                        .stream()
                        .map(record -> {
                            Long studentId = record.get(STUDENT.UNIQUE_ID);
                            String email = record.get(STUDENT.EMAIL);
                            String username = record.get(STUDENT.USERNAME);
                            return Student.builder()
                                    .withUniqueId(studentId)
                                    .withEmail(email)
                                    .create(username);
                        })
                        .collect(Collectors.toSet());
                courseBuilder.withStudents(studentSet);
            });

            builder.ifPresent((courseBuilder) -> {
                Set<Assignment> assignmentSet = create.select(
                        ASSIGNMENT.UNIQUE_ID,
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
                            Long assignmentId = record.get(ASSIGNMENT.UNIQUE_ID);
                            String assignmentName = record.get(ASSIGNMENT.NAME);
                            Timestamp createdDateTime = record.get(ASSIGNMENT.CREATED_DATE_TIME);
                            String path = record.get(ASSIGNMENT.IMAGE_FILE_PATH);

                            return Assignment.builder()
                                    .withUniqueId(assignmentId)
                                    .withCourse_id(courseBuilder.getDatabaseId())
                                    .withCreatedDateTime(createdDateTime.toLocalDateTime())
                                    .withImageFilePath(path)
                                    .create(assignmentName);
                        })
                        .collect(Collectors.toSet());
                courseBuilder.withAssignments(assignmentSet);
            });

            return builder.map(Course.Builder::create).orElse(null);
        }
    }

    private Semester resolveOrInsertSemester(Semester apiSemester) {
        try (DSLContext ctx = connect()) {
            final Semester semesterToResolve = apiSemester;
            Condition filterer = DSL.and(
                    SEMESTER.TERM.eq(semesterToResolve.getTermString()),
                    SEMESTER.YEAR.eq(semesterToResolve.getYearInt())
            );

            // try to find the semester if it exists, otherwise insert it
            return ctx.selectFrom(SEMESTER)
                    .where(filterer)
                    .fetchOptional()
                    .orElseGet(() -> {
                        SemesterRecord semesterRecord = ctx.insertInto(
                                SEMESTER,
                                SEMESTER.UNIQUE_ID,
                                SEMESTER.TERM,
                                SEMESTER.YEAR
                        ).values(
                                // uniqueId is sent over the API
                                semesterToResolve.getUniqueId(),
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
