package era.uploader.data.database;

import era.uploader.data.StudentDAO;
import era.uploader.data.model.Student;
import java.util.Set;
import java.util.HashSet;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
public class StudentDAOImpl implements StudentDAO {
    static int sequenceNum = 0; /* Used to make sure each Student gets their own unique id. */
    private Set<Student> students = new HashSet<>(); /* A set of students to act as the database table */

    /* Create and Insert a new Student object into the database */
     public void insert(Student student) {
        students.add(student);
        sequenceNum++;
        student.setUniqueId(sequenceNum);
     }

    /* Access data from existing Student object from database */
    public Student read(Student student) {
        for (Student otherStudent :
                students) {
            if (otherStudent.equals(student)) {
                return otherStudent;
            }
       }
       return null;
    }

    /* Modify data stored in already existing Student in database */
    public void update(Student studentToChange, Student studentChanged) {
        students.remove(studentToChange);
        students.add(studentChanged);
    }

    /* Delete existing Student object in database */
    public void delete(Student student) {
        students.remove(student);
    }
}