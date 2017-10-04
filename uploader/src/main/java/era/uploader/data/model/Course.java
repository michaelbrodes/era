package era.uploader.data.model;

import java.util.Set;
import java.util.HashSet;

/* Class that will represent Courses which assignments will belong to. This
 * will allow the instructor to associate multiple assignments to a single
 * Course.
 */

public class Course {
    /* Class Fields */
    // will be an unsigned int in the database.
    private long id;
    private String department;                              /* Department where the course is held */
    private String name;                                    /* Name of Course */
    private String semester;                                /* Semester of Course */
    private String courseNumber;                            /* Number of Course */
    private String sectionNumber;                           /* Number for the Course Section */
    private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
    private Set<Assignment> assignments;

    /* Constructor */
    public Course(
            String department,
            String name,
            String semester,
            String courseNumber,
            String sectionNumber,
            Set<Assignment> assignments
    ) {
        this.department = department;
        this.name = name;
        this.semester = semester;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.assignments = assignments;
    }

    public Course(Builder builder) {
        this.department = builder.department;
        this.name = builder.name;
        this.semester = builder.semester;
        this.courseNumber = builder.courseNumber;
        this.sectionNumber = builder.sectionNumber;
        this.assignments = builder.assignments;
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

    public Set<Student> getStudentsEnrolled() {
        return studentsEnrolled;
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

    public static Builder create() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course course = (Course) o;

        if (department != null ? !department.equalsIgnoreCase(course.department) : course.department != null) return false;
        if (semester != null ? !semester.equalsIgnoreCase(course.semester) : course.semester != null) return false;
        if (courseNumber != null ? !courseNumber.equalsIgnoreCase(course.courseNumber) : course.courseNumber != null)
            return false;
        return sectionNumber != null ? sectionNumber.equals(course.sectionNumber) : course.sectionNumber == null;
    }

    @Override
    public int hashCode() {
        int result = department != null ? department.hashCode() : 0;
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + (courseNumber != null ? courseNumber.hashCode() : 0);
        result = 31 * result + (sectionNumber != null ? sectionNumber.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private long id;
        private String department;                              /* Department where the course is held */
        private String name;                                    /* Name of Course */
        private String semester;                                /* Semester of Course */
        private String courseNumber;                            /* Number of Course */
        private String sectionNumber;                           /* Number for the Course Section */
        private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
        private Set<Assignment> assignments;

        public Builder withDatabaseId(long id) {
            this.id = id;
            return this;
        }

        public Builder forDepartment(String department) {
            this.department = department.toUpperCase();
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder forSemester(String semester) {
            this.semester = semester.toUpperCase();
            return this;
        }

        public Builder withCourseNumber(String courseNumber) {
            this.courseNumber = courseNumber.toUpperCase();
            return this;
        }

        public Builder withSectionNumber(String sectionNumber) {
            this.sectionNumber = sectionNumber.toUpperCase();
            return this;
        }

        public Builder havingStudentsEnrolled(Set<Student> studentsEnrolled) {
            this.studentsEnrolled = studentsEnrolled;
            return this;
        }

        public Builder havingAssignments(Set<Assignment> assignments) {
            this.assignments = assignments;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
