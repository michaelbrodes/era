package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that will represent Courses which assignments will belong to. This
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
    @Deprecated
    private String semesterStr;                                /* Semester of Course */
    private Semester semester;
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
        this.semesterStr = semester;
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
            @Nonnull String sectionNumber,
            @Nonnull Semester semester
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
        this.semester = semester;
    }

    private Course(
            String department,
            String courseNumber,
            String sectionNumber,
            @Nonnull Builder builder) {
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.semester = builder.semester;
        this.name = builder.name;
        this.semesterStr = builder.semesterDeprecated;
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

    /**
     * TODO: remove this method by the end of sprint 4
     * @deprecated this can no longer be stored in the database, please use
     * {@link #getSemesterObj()}
     */
    @Deprecated
    public String getSemester() {
        return semesterStr;
    }

    @Deprecated
    public void setSemester(String semester) {
        this.semesterStr = semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    /**
     * @return A non-null {@link Semester} object that this course belongs to.
     * This getter is suffixed with <code>Obj</code> because previously we
     * stored the semester as a String and we had to deprecate that method
     */
    public Semester getSemesterObj() {
        return semester;
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

    public Set<Student> getStudentsEnrolled() {
        return studentsEnrolled;
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
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return (uniqueId == 0 || uniqueId == course.uniqueId)
                && department.equals(course.department)
                && (name != null ? name.equals(course.name) : course.name == null)
                && semester.equals(course.semester)
                && courseNumber.equals(course.courseNumber)
                && sectionNumber.equals(course.sectionNumber);
    }

    @Override
    public int hashCode() {
        int result = uniqueId;
        result = 31 * result + department.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + courseNumber.hashCode();
        result = 31 * result + sectionNumber.hashCode();
        return result;
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Course} class has a great deal of potentially Nullable
     * fields. For each nullable field an exponential amount of constructor
     * overloads could be required. If you would like to do those constructor
     * overloads then more power to you, but I am too lazy for that.
     *
     * Our convention is that each field that can be null will have a Builder
     * setter prefixed by the word <code>with</code> and suffixed by the field
     * name in camel case. All nonnull fields will be specified in
     * {@link #create}.
     */
    public static class Builder {
        private int uniqueId;
        private String department;                              /* Department where the course is held */
        private String name;                                    /* Name of Course */
        @Deprecated
        private String semesterDeprecated;                                /* Semester of Course */
        private Semester semester;
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

        @Deprecated
        public Builder withSemester(String semester) {
            this.semesterDeprecated = semester.toUpperCase();
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

        public Builder withSemester(Semester semester) {
            this.semester =  semester;
            return this;
        }

        public Builder withStudents(Set<Student> studentsEnrolled) {
            this.studentsEnrolled = studentsEnrolled;
            return this;
        }

        public Builder withAssignments(Set<Assignment> assignments) {
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
