package era.uploader.data.service;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

/**
 * This class will eventually map to a RESTful API on the server.
 */
public class CourseService implements CourseDAO {
    @Override
    public void insert(Course course) {

    }

    @Override
    public void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents) {

    }

    @Override
    public Course read(Course course) {
        return null;
    }

    @Override
    public void update(Course courseToChange, Course courseChanged) {

    }

    @Override
    public void delete(Course course) {

    }
}
