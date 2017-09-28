package era.uploader.data.model;

import java.util.Set;
import java.util.HashSet;

/* Class that will represent Courses which assignments will belong to. This
 * will allow the instructor to associate multiple assignments to a single
 * Course.
 */

public class Course {
    /* Class Fields */
    private String department;                              /* Department where the course is held */
    private String name;                                    /* Name of Course */
    private String semester;                                /* Semester of Course */
    private String courseNumber;                            /* Number of Course */
    private String sectionNumber;                           /* Number for the Course Section */
    private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
    private Set<Assignment> assignments;

    /* Constructor */
    public Course(String department, String name, String semester, String courseNumber, String sectionNumber) {
        this.department = department;
        this.name = name;
        this.semester = semester;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
    }

    /* Getters and Setters */
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

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

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
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

    public Set<Assignment> getAssignments() {
        return assignments;
    }
}
