package era.uploader.data;

import era.uploader.data.model.Course;

/* Interface for the CourseDAOImpl class which will be used to
 * implement the CRUD functionality for the Course objects in the
 * database.
 */
public interface CourseDAO {
    void insert(Course course);                               /* Create new course object */
    Course read(Course course);                               /* Access data from course object */
    void update(Course courseToChange, Course courseChanged); /* Change data from existing course object */
    void delete(Course course);                               /* Delete existing course object */
}
