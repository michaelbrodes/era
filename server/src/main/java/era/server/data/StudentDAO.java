package era.server.data;

import era.server.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface StudentDAO {
    /**
     * Inserts <strong>only</strong> one student into the database. No Courses
     * or Assignments will be inserted with this student.
     *
     * @param student the student we want inserted into the database.
     */
    void insert(Student student);

    Student resolveStudent(Student student);

    /**
     * Get a student from the database with the corresponding id
     *
     * @param id id of a student in the database
     * @return a student from the database.
     */
    Student read(String id);

    Student getOrCreateStudent(String username);


    Optional<Student> readByUsername(String username);
}
