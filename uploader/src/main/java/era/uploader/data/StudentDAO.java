package era.uploader.data;

import era.uploader.data.model.Student;

/* Interface for the StudentDAOImpl class which will be used to
 * implement the CRUD functionality for the Student objects in the
 * database.
 */
public interface StudentDAO extends DAO {
    void insert(Student student);                       /* Create new student object */
    Student read(long student);                           /* Access data from student object */
    void update(Student changedStudent);                  /* Change data from existing student object */
    void delete(long id);                                 /* Delete existing student object */
}
