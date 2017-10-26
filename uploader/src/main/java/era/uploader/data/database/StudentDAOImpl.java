package era.uploader.data.database;

import era.uploader.data.StudentDAO;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;

import java.util.Set;
import java.util.HashSet;

import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * Provides CRUD functionality for {@link Student} objects stored in the
 * database
 */
public class StudentDAOImpl implements StudentDAO, DatabaseDAO<StudentRecord, Student> {
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

    @Override
    public Student convertToModel(StudentRecord record) {
        // TODO: cam implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public StudentRecord convertToRecord(Student model, DSLContext ctx) {
        StudentRecord studentRecord = ctx.newRecord(STUDENT);
        studentRecord.setEmail(model.getEmail());
        studentRecord.setFirstName(model.getFirstName());
        studentRecord.setLastName(model.getLastName());
        studentRecord.setSchoolId(model.getSchoolId());
        studentRecord.setUsername(model.getUserName());
        if (model.getUniqueId() != 0) {
            studentRecord.setUniqueId(model.getUniqueId());
        }
        return studentRecord;
    }
}