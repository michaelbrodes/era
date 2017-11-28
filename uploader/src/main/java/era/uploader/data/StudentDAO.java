package era.uploader.data;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

/**
 * Interface for the StudentDAOImpl class which will be used to
 * implement the CRUD functionality for the Student objects in the
 * database.
 */
@ParametersAreNonnullByDefault
public interface StudentDAO extends DAO {
    void insert(Student student);                       /* Create new student object */
    @Nullable
    Student read(long student);                           /* Access data from student object */
    void update(Student changedStudent);                  /* Change data from existing student object */
    void delete(long id);                                 /* Delete existing student object */
    Collection<Student> fromCourse(Course course);
    Student fromQRMapping(QRCodeMapping mapping);
    Student fromAssignment(Assignment assignment);
}
