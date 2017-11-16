package era.uploader.data;

import era.uploader.data.database.jooq.tables.records.AllAssignmentsRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;

import java.util.Collection;

public interface AssignmentDAO extends DAO {
    void storeAssignment(Assignment assignment);
    Assignment insert(Assignment assignment);
    Assignment read(long id);                               /* Access data from course object */
    void update(Assignment changedAssignment); /* Change data from existing course object */
    void delete(Assignment assignment);

    /**
     * Get every assignment that belongs to a course.
     * @param model the course we want assignments from
     * @return every assignment that belongs to model
     */
    Collection<Assignment> fromCourse(Course model);

    /**
     * @return Every single assignment joined with meta-data, and outputted
     * reverse chronological order.
     */
    Collection<AllAssignmentsRecord> getAllAssignments();
}
