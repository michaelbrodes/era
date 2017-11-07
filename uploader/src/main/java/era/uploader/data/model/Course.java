package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.HashSet;
import java.util.Set;

/* Class that will represent Courses which assignments will belong to. This
 * will allow the instructor to associate multiple assignments to a single
 * Course.
 */

@ParametersAreNullableByDefault
public class Course {
    /* Class Fields */
    // will be an unsigned int in the database.
    private int uniqueId;
    private String department;                              /* Department where the course is held */
    private String name;                                    /* Name of Course */
    private String semester;                                /* Semester of Course */
    private String courseNumber;                            /* Number of Course */
    private String sectionNumber;                           /* Number for the Course Section */
    private Set<Student> studentsEnrolled = Sets.newHashSet(); /* Set of Students in the Class */
    private Set<Assignment> assignments = Sets.newHashSet();

    /* Constructor */

    /**
     * This default constructor is deprecated because a valid course would
     * require a Nonnull department, courseNumber, and sectionNumber. Please
     * use constructors that have that instead
     */
    @Deprecated
    public Course() {

    }

    public Course(
            @Nonnull String department,
            String name,
            String semester,
            @Nonnull String courseNumber,
            @Nonnull String sectionNumber,
            Set<Assignment> assignments
    ) {
        Preconditions.checkNotNull(department, "Cannot create a course with a null department");
        Preconditions.checkNotNull(courseNumber, "Cannot create a course with a null courseNumber");
        Preconditions.checkNotNull(sectionNumber, "Cannot create a course with a null sectionNumber");
        this.department = department;
        this.name = name;
        this.semester = semester;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.assignments = assignments == null ? Sets.newHashSet() : assignments;
    }

    /**
     * Essentially our default constructor for a course object. It is just
     * supplied only the required fields of this object.
     *
     * @param department the University department that this course belongs to
     *                   (e.g. CHEM)
     * @param courseNumber the course number of this course
     * @param sectionNumber the section number of this course.
     */
    public Course (
            @Nonnull String department,
            @Nonnull String courseNumber,
            @Nonnull String sectionNumber
    ) {
        Preconditions.checkNotNull(department, "Cannot create a course with a null department");
        Preconditions.checkNotNull(courseNumber, "Cannot create a course with a null courseNumber");
        Preconditions.checkNotNull(sectionNumber, "Cannot create a course with a null sectionNumber");
        // default as we are not in the database yet.
        this.uniqueId = 0;
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.studentsEnrolled = Sets.newHashSet();
        this.assignments = Sets.newHashSet();
    }

    private Course(String department, String courseNumber, String sectionNumber, Builder builder) {
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.name = builder.name;
        this.semester = builder.semester;
        this.studentsEnrolled = builder.studentsEnrolled == null ?
                Sets.newHashSet() :
                builder.studentsEnrolled;
        this.assignments = builder.assignments == null ?
                Sets.newHashSet() :
                builder.assignments;
        this.uniqueId = builder.uniqueId;
    }


    /* Getters and Setters */
    @Nonnull
    public String getDepartment() {
        return department;
    }

    public void setDepartment(@Nonnull String department) {
        Preconditions.checkNotNull(department);
        this.department = department;
    }

    public String getName() {
        if (name == null) {
            return department + '-' + courseNumber + '-' + sectionNumber;
        }
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

    @Nonnull
    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(@Nonnull String courseNumber) {
        Preconditions.checkNotNull(courseNumber);
        this.courseNumber = courseNumber;
    }

    @Nonnull
    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(@Nonnull String sectionNumber) {
        Preconditions.checkNotNull(sectionNumber);
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

    public static Builder builder() {
        return new Builder();
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
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
        private int uniqueId;
        private String department;                              /* Department where the course is held */
        private String name;                                    /* Name of Course */
        private String semester;                                /* Semester of Course */
        private String courseNumber;                            /* Number of Course */
        private String sectionNumber;                           /* Number for the Course Section */
        private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
        private Set<Assignment> assignments;

        public Builder withDatabaseId(int uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSemester(String semester) {
            this.semester = semester.toUpperCase();
            return this;
        }

        @Deprecated
        public Builder withCourseNumber(String courseNumber) {
            this.courseNumber = courseNumber.toUpperCase();
            return this;
        }

        @Deprecated
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

        public Course create(
                @Nonnull String department,
                @Nonnull String courseNumber,
                @Nonnull String sectionNumber
        ) {
            Preconditions.checkNotNull(department, "Cannot create a course with a null department");
            Preconditions.checkNotNull(courseNumber, "Cannot create a course with a null courseNumber");
            Preconditions.checkNotNull(sectionNumber, "Cannot create a course with a null sectionNumber");

            return new Course(department, courseNumber, sectionNumber, this);
        }
    }
}
