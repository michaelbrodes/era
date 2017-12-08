package era.uploader.data;

import com.google.common.collect.Multimap;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

/* Interface for the CourseDAOImpl class which will be used to
 * implement the CRUD functionality for the Course objects in the
 * database.
 */
@ParametersAreNonnullByDefault
public interface CourseDAO extends DAO {
    Course insert(Course course);                               /* Create new course object */
    void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents, Semester semester);
    @Nullable
    Course read(long id);                               /* Access data from course object */
    void update(Course changedCourse); /* Change data from existing course object */
    void delete(long id);                               /* Delete existing course object */
    Set<Course> fromStudent(Student student);
    Course fromAssignment(Assignment assignment);
    @Nonnull
    List<Course> getAllCourses();
}
