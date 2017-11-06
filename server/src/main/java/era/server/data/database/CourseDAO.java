package era.server.data.database;

import com.google.common.collect.Multimap;
import era.server.data.model.Course;
import era.server.data.model.Student;


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides CRUD functionality for {@link Course} objects stored in the
 * database. A course has many {@link Student}s and many Grader.
 */
public class CourseDAO {
    private Set<Course> courses = new HashSet<>(); /* A set of Courses to act as the database table */

    /* Create and Insert a new Course object into the database */

    public void insert(Course course) {
        courses.add(course);
    }


    public void insertCourseAndStudents(Multimap<Course, Student> coursesToStudents) {
        for(Map.Entry<Course, Collection<Student>> studentsInCourse:
                coursesToStudents.asMap().entrySet()) {
            Course course = studentsInCourse.getKey();
            Collection<Student> students = studentsInCourse.getValue();
            course.getStudentsEnrolled().addAll(students);
            for (Student student : students) {
                student.getCourses().add(course);
            }
        }
        courses.addAll(coursesToStudents.keys());
    }

    /* Access data from existing Course object from database */

    public Course read(long id) {
        for (Course otherCourse :
                courses) {
            if (otherCourse.getId() == id) {
                return otherCourse;
            }
        }
        return null;
    }

    /* Modify data stored in already existing Course in database */

    public void update(Course courseToChange, Course courseChanged) {
        courses.remove(courseToChange);
        courses.add(courseChanged);
    }

    /* Delete existing Course object in database */

    public void delete(Course course) {
        courses.remove(course);
    }

    public Set<Course> getAllCourses() {
        return courses;
    }
}
