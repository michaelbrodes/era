package era.uploader.data;

import com.google.common.collect.Multimap;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

/* Interface for the CourseDAOImpl class which will be used to
 * implement the CRUD functionality for the Course objects in the
 * database.
 */
public interface CourseDAO {
    void insert(Course course);                               /* Create new course object */
    void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents);
    Course read(long id);                               /* Access data from course object */
    void update(Course courseToChange, Course courseChanged); /* Change data from existing course object */
    void delete(Course course);                               /* Delete existing course object */
}
