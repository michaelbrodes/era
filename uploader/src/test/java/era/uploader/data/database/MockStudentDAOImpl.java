package era.uploader.data.database;

import era.uploader.data.StudentDAO;
import era.uploader.data.model.Student;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is mock of {@link StudentDAOImpl} to make unit testing methods
 * using CRUD functionality easier - you don't have to generate a new SQLite
 * database per each test. <strong>This is not a unit test for
 * {@link StudentDAOImpl}</strong>. You should test the that DAO against the
 * real database using a corresponding integration test.
 */
public class MockStudentDAOImpl implements MockDAO<Student>, StudentDAO {
    private static int sequenceNum = 0; /* Used to make sure each Student gets their own unique id. */
    private Set<Student> students = new HashSet<>(); /* A set of students to act as the database table */

    @Override
    public Set<Student> getDb() {
        return students;
    }

    /* Create and Insert a new Student object into the database */
    /*@Override
    public void insert(Student student) {
        students.add(student);
        sequenceNum++;
        student.setUniqueId(sequenceNum);
    }*/

    /* Access data from existing Student object from database */
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

    /* Modify data stored in already existing Student in database */
    @Override
    public void update(Student studentToChange, Student studentChanged) {
        students.remove(studentToChange);
        students.add(studentChanged);
    }

    /* Delete existing Student object in database */
    @Override
    public void delete(Student student) {
        students.remove(student);
    }
}
