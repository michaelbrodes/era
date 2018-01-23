package era.server.data.access;


import era.server.data.StudentDAO;
import era.server.data.model.Student;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * access
 */
public class StudentDAOImpl implements StudentDAO {
    private static int sequenceNum = 0; /* Used to make sure each Student gets their own unique id. */
    private Set<Student> students = new HashSet<>(); /* A set of students to act as the access table */

    /* Create and Insert a new Student object into the access */
     @Override
     public void insert(Student student) {
        students.add(student);
        sequenceNum++;
        student.setUniqueId(sequenceNum);
     }

    /* Access data from existing Student object from access */
    @Override
    public Student read(long id) {
        for (Student otherStudent :
                students) {
            if (otherStudent.getUniqueId() == id) {
                return otherStudent;
            }
       }
       return null;
    }

    /* Modify data stored in already existing Student in access */
    @Override
    public void update(Student studentToChange, Student studentChanged) {
        students.remove(studentToChange);
        students.add(studentChanged);
    }

    /* Delete existing Student object in access */
    @Override
    public void delete(Student student) {
        students.remove(student);
    }
}