package era.uploader.data.service;

import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.util.Set;

/**
 * This class will eventually map to a RESTful API on the server.
 */
public class CourseService implements CourseDAO {
    @Override
    public Course insert(Course course) {
        return null;
    }

    @Override
    public void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents) {

    }

    @Override
    public Course read(long id) {
        return null;
    }

    @Override
    public void update(Course changedCourse) {

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Set<Course> getAllCourses() {
        return null;
    }
}
