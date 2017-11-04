package era.uploader.data;

import era.uploader.data.model.Assignment;

public interface AssignmentDAO extends DAO {
    void storeAssignment(Assignment assignment);
    Assignment insert(Assignment assignment);
    Assignment read(long id);
    void delete(Assignment assignment);
    void update(Assignment changedAssignment); /* Change data from existing course object */
}
