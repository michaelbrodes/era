package era.uploader.data.database;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.CourseConverter;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.database.jooq.tables.records.SemesterRecord;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * database. A course has many {@link Student}s and many {@link Assignment}s
 */
@ParametersAreNonnullByDefault
public class CourseDAOImpl extends DatabaseDAO<CourseRecord, Course> implements CourseDAO {
    private static final CourseConverter CONVERTER = CourseConverter.INSTANCE;
    private static CourseDAO INSTANCE;

    private CourseDAOImpl() {
    }

    /**
     * Create and Insert a new Course object into the database
     */
    @Override
    @Nonnull
    public Course insert(@Nonnull Course course) {
        Preconditions.checkNotNull(course, "Cannot insert a null course");
        Preconditions.checkNotNull(course.getSemester(), "Cannot resolve a null semester");
        Preconditions.checkNotNull(course.getStudentsEnrolled(), "Cannot insert null students");
        Preconditions.checkNotNull(course.getAssignments(), "Cannot insert null students");
        try (DSLContext ctx = connect()) {
            final Semester semesterToResolve = course.getSemester();
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
                StudentRecord studentRecord = ctx.selectFrom(STUDENT).
                        where(STUDENT.SCHOOL_ID.eq(student.getSchoolId())).fetchOne();
                if (studentRecord == null) {
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
                }
                else {
                    student.setUniqueId(studentRecord.getUniqueId());
                }

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

    /**
     * This method takes in a multimap of courses to students. A multimap means
     * that a {@link Course} can be a key of many {@link Student}s. Just think
     * of this as grouping students by their course.
     *
     * This method first goes over that multimap and attaches the grouped
     * students to the course they are grouped by. Then it iterates over the
     * keys of the multimap, and inserts them all.
     *
     * @param coursesToStudents Students grouped by the course they belong to.
     */
    @Override
    public void insertCourseAndStudents(@Nonnull Multimap<Course, Student> coursesToStudents) {
        Preconditions.checkNotNull(coursesToStudents, "Cannot insert null multimap");
        for (Course course: squashMap(coursesToStudents)) {
            insert(course);
        }
    }

    @VisibleForTesting
    Collection<Course> squashMap(Multimap<Course, Student> coursesToStudents) {
        for (Map.Entry<Course, Collection<Student>> studentsInCourse :
                coursesToStudents.asMap().entrySet()) {
            Course course = studentsInCourse.getKey();
            Collection<Student> students = studentsInCourse.getValue();
            course.getStudentsEnrolled().addAll(students);
            for (Student student : students) {
                student.getCourses().add(course);
            }
        }

        return coursesToStudents.keySet();
    }

    /* Access data from existing Course object from database */
    @Override
    @Nullable
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
    public void update(@Nonnull Course changedCourse) {
        Preconditions.checkNotNull(changedCourse, "Can't update a null course.");
        try (DSLContext ctx = connect()) {
            Semester semester = changedCourse.getSemester();

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

    /**
     *  Delete existing Course object in database
     */
    @Override
    public void delete(long id) {
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq((int) id))
                    .execute();
        }

    }

    /**
     * Get all courses currently in the system.
     */
    @Override
    @Nonnull
    public List<Course> getAllCourses() {
        List<Course> courses;
        StudentDAOImpl studentDAO = StudentDAOImpl.instance();
        try(DSLContext ctx = DSL.using(CONNECTION_STR)) {
             courses = ctx.selectFrom(COURSE).fetch().map(this::convertToModel);
            for (Course course : courses) {
                List<Integer> student_ids = new ArrayList<>();
                student_ids.addAll(
                        ctx.selectFrom(COURSE_STUDENT)
                            .where(COURSE_STUDENT.COURSE_ID.eq(course.getUniqueId()))
                            .fetch()
                            .getValues(COURSE_STUDENT.STUDENT_ID)
                );
                course.getStudentsEnrolled()
                        .addAll(ctx.selectFrom(STUDENT)
                                .where(STUDENT.UNIQUE_ID.in(student_ids))
                                .fetch()
                                .map(studentDAO::convertToModel)
                        );
            }
        }
        return courses;
    }

    @Override
    public Set<Course> fromStudent(@Nonnull Student student) {
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
    public Course fromAssignment(@Nonnull Assignment assignment) {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(COURSE)
                    .where(COURSE.UNIQUE_ID.eq(assignment.getCourse_id()))
                    .fetchOne()
                    .into(Course.class);
        }
    }

    @Override
    public Course convertToModel(@Nullable CourseRecord record) {
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
    public CourseRecord convertToRecord(@Nullable Course model) {
        return CONVERTER.reverse().convert(model);
    }

    @Nonnull
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
