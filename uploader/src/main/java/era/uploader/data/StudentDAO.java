package era.uploader.data;

import era.uploader.data.model.Student;

/* Interface for the StudentDAOImpl class which will be used to
 * implement the CRUD functionality for the Student objects in the
 * database.
 */
public interface StudentDAO {
    void insert(Student student);                                 /* Create new student object */
    Student read(Student student);                                /* Access data from student object */
    void update(Student studentToChange, Student studentChanged); /* Change data from existing student object */
    void delete(Student student);                                 /* Delete existing student object */
}
