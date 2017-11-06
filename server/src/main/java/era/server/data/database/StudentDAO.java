package era.server.data.database;


import era.server.data.model.Student;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
public class StudentDAO {
    private static int sequenceNum = 0; /* Used to make sure each Student gets their own unique id. */
    private Set<Student> students = new HashSet<>(); /* A set of students to act as the database table */

    /* Create and Insert a new Student object into the database */
     public void insert(Student student) {
        students.add(student);
        sequenceNum++;
        student.setUniqueId(sequenceNum);
     }

    /* Access data from existing Student object from database */
    public Student read(long id) {
        for (Student otherStudent :
                students) {
            if (otherStudent.getUniqueId() == id) {
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