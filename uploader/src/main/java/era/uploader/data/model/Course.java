package era.uploader.data.model;

import java.util.Set;
import java.util.HashSet;

/* Class that will represent Courses which assignments will belong to. This
 * will allow the instructor to associate multiple assignments to a single
 * Course.
 */

public class Course {
    /* Class Fields */
    private String name;                             /* Name of Course */
    private String semester;                         /* Semester of Course */
    private int courseNumber;                        /* Number of Course */
    private int sectionNumber;                       /* Number for the Course Section */
    public Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */

    /* Constructor */
    public Course(String name, String semester, int courseNumber, int sectionNumber) {
        this.name = name;
        this.semester = semester;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
    }

    /* Getters and Setters */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    /* Class Methods */
    public void addStudent(Student student) {
        studentsEnrolled.add(student);
    }

    public void removeStudent(Student student) {
        studentsEnrolled.remove(student);
    }
    public Student getStudent(Student student) {
        for (Student otherStudent :
                studentsEnrolled) {
            if (otherStudent.equals(student)) {
                return otherStudent;
            }
        }
        return null;
    }
}
