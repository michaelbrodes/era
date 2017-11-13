package era.uploader.data;

import com.google.common.collect.Multimap;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.util.Set;

/* Interface for the CourseDAOImpl class which will be used to
 * implement the CRUD functionality for the Course objects in the
 * database.
 */
public interface CourseDAO extends DAO {
    Course insert(Course course);                               /* Create new course object */
    void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents);
    Course read(long id);                               /* Access data from course object */
    void update(Course changedCourse); /* Change data from existing course object */
    void delete(long id);                               /* Delete existing course object */

    Set<Course> getAllCourses();

    Set<Course> fromStudent(Student student);

    Course fromAssignment(Assignment assignment);
}
