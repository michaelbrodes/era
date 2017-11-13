package era.uploader.data.database;

import era.uploader.data.StudentDAO;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

import java.util.Collection;
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
    @Override
    public void insert(Student student) {
        students.add(student);
        sequenceNum++;
        student.setUniqueId(sequenceNum);
    }

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

    @Override
    public void update(Student changedStudent) {
        Student prevStudent = read(changedStudent.getUniqueId());
        getDb().remove(prevStudent);
        getDb().add(changedStudent);
    }

    @Override
    public void delete(long id) {
        Student student = read(id);
        getDb().remove(student);
    }

    @Override
    public Collection<Student> fromCourse(Course course) {
        return course.getStudentsEnrolled();
    }

    @Override
    public Student fromQRMapping(QRCodeMapping mapping) {
        return mapping.getStudent();
    }

    @Override
    public Student fromAssignment(Assignment assignment) {
        return assignment.getStudent();
    }
}
