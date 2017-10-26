package era.uploader.data;

import era.uploader.data.model.Assignment;

public interface AssignmentDAO extends DAO {
    void storeAssignment(Assignment assignment);
}
