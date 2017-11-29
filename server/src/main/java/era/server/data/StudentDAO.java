package era.server.data;

import era.server.data.model.Student;

public interface StudentDAO {
    /* Create and Insert a new Student object into the access */
    void insert(Student student);

    /* Access data from existing Student object from access */
    Student read(long id);

    /* Modify data stored in already existing Student in access */
    void update(Student studentToChange, Student studentChanged);

    /* Delete existing Student object in access */
    void delete(Student student);
}
