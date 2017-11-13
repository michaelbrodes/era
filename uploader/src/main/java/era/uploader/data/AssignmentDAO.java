package era.uploader.data;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;

import java.util.Collection;

public interface AssignmentDAO extends DAO {
    void storeAssignment(Assignment assignment);
    Assignment insert(Assignment assignment);
    Assignment read(long id);                               /* Access data from course object */
    void update(Assignment changedAssignment); /* Change data from existing course object */
    void delete(Assignment assignment);
    Collection<Assignment> fromCourse(Course model);
}
