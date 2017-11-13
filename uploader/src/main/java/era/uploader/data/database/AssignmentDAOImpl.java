package era.uploader.data.database;


import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.StudentDAO;
import era.uploader.data.converters.AssignmentConverter;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;

/**
 * Provides CRUD functionality for Assignments inside a database.
 */
public class AssignmentDAOImpl extends DatabaseDAO<AssignmentRecord, Assignment> implements AssignmentDAO {
    private static final AssignmentConverter CONVERTER = AssignmentConverter.INSTANCE;
    private static AssignmentDAO INSTANCE;

    private AssignmentDAOImpl() {
    }

    public static AssignmentDAO instance() {
        if (INSTANCE == null) {
            synchronized (AssignmentDAOImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AssignmentDAOImpl();
                }
            }
        }

        return INSTANCE;
    }

    public void storeAssignment(Assignment assignment) {
        insert(assignment);
    }

    @Override
    public Assignment insert(Assignment assignment) {
        try (DSLContext ctx = connect()) {
            assignment.setUniqueId(ctx.insertInto(
                    //table
                    ASSIGNMENT,
                    //columns
                    ASSIGNMENT.NAME,
                    ASSIGNMENT.IMAGE_FILE_PATH,
                    ASSIGNMENT.COURSE_ID,
                    ASSIGNMENT.STUDENT_ID
                    )
                    .values(
                            assignment.getName(),
                            assignment.getImageFilePath(),
                            assignment.getCourse().getUniqueId(),
                            assignment.getStudent().getUniqueId()
                    )
                    .returning(
                            ASSIGNMENT.UNIQUE_ID
                    )
                    .fetchOne().getUniqueId()
            );
        }

        return assignment;
    }

    @Override
    public Assignment read(long id) {
        try (DSLContext ctx = connect()) {
            AssignmentRecord assignmentRecord = ctx.selectFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.UNIQUE_ID.eq((int) id))
                    .fetchOne();

            return convertToModel(assignmentRecord);
        }
    }

    /* Modify data stored in already existing Course in database */
    @Override
    public void update(Assignment changedAssignment) {
        try (DSLContext ctx = connect()) {
            ctx.update(ASSIGNMENT)
                    .set(ASSIGNMENT.COURSE_ID, changedAssignment.getCourse().getUniqueId())
                    .set(ASSIGNMENT.IMAGE_FILE_PATH, changedAssignment.getImageFilePath())
                    .set(ASSIGNMENT.NAME, changedAssignment.getName())
                    .set(ASSIGNMENT.STUDENT_ID, changedAssignment.getStudent().getUniqueId())
                    .where(ASSIGNMENT.UNIQUE_ID.eq(changedAssignment.getUniqueId()))
                    .execute();
        }
    }

    @Override
    public Collection<Assignment> fromCourse(Course model) {
        try (DSLContext ctx = connect()) {
            return ctx.selectFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.COURSE_ID.eq(model.getUniqueId()))
                    .fetch()
                    .stream()
                    .map(this::convertToModel)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void delete(Assignment assignment) {
        try (DSLContext ctx = connect()) {
            ctx.deleteFrom(ASSIGNMENT)
                    .where(ASSIGNMENT.UNIQUE_ID.eq(assignment.getUniqueId()))
                    .execute();
        }
    }

    @Override
    public Assignment convertToModel(AssignmentRecord record) {
        Optional<Assignment> model = Optional.ofNullable(CONVERTER.convert(record));
        final CourseDAO courseDAO = CourseDAOImpl.instance();
        final StudentDAO studentDAO = StudentDAOImpl.instance();
        model.ifPresent((assignment) -> {
            assignment.setCourse(courseDAO.fromAssignment(assignment));
            assignment.setStudent(studentDAO.fromAssignment(assignment));
        });
        return model.orElse(null);
    }

    @Override
    public AssignmentRecord convertToRecord(Assignment model, DSLContext ctx) {
        AssignmentRecord assignment = ctx.newRecord(ASSIGNMENT);
        assignment.setCourseId(model.getCourse_id());
        assignment.setImageFilePath(model.getImageFilePath());
        assignment.setName(model.getName());
        assignment.setStudentId(model.getStudent_id());

        if (model.getUniqueId() != 0) {
            assignment.setUniqueId(model.getUniqueId());
        }

        return assignment;
    }

    @Override
    public AssignmentRecord convertToRecord(Assignment model) {
        return CONVERTER.reverse().convert(model);
    }
}
