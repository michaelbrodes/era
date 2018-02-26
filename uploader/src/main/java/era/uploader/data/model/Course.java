package era.uploader.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import era.uploader.common.UUIDGenerator;

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
    private final String department;                              /* Department where the course is held */
    private final String courseNumber;                            /* Number of Course */
    private final String sectionNumber;                           /* Number for the Course Section */
    private final Set<Student> studentsEnrolled; /* Set of Students in the Class */
    private final Set<Assignment> assignments;
    private final String uuid;
    /* Class Fields */
    // will be an unsigned int in the database.
    private int uniqueId;
    private int semesterId;
    private Semester semester;
    private String name;                                    /* Name of Course */

    private Course(
            String department,
            String courseNumber,
            String sectionNumber,
            String uuid,
            @Nonnull Builder builder) {
        this.semesterId = builder.semesterId;
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.semester = builder.semester;
        this.name = builder.name;
        this.studentsEnrolled = builder.studentsEnrolled == null ?
                Sets.newHashSet() :
                builder.studentsEnrolled;
        this.assignments = builder.assignments == null ?
                Sets.newHashSet() :
                builder.assignments;
        this.uniqueId = builder.uniqueId;
        this.uuid = uuid;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* Getters and Setters */
    @Nonnull
    public String getDepartment() {
        return department;
    }

    @Nonnull
    public String getName() {
        if (name == null) {
            return department + '-' + courseNumber + '-' + sectionNumber;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(@Nonnull Semester semester) {
        Preconditions.checkNotNull(semester);

        this.semester = semester;
        this.semesterId = semester.getUniqueId();
    }

    @Nonnull
    public String getCourseNumber() {
        return courseNumber;
    }

    @Nonnull
    public String getSectionNumber() {
        return sectionNumber;
    }

    public Set<Student> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getSemesterId() {
        if (semesterId == 0) {
            return semester.getUniqueId();
        } else {
            return semesterId;
        }
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return getUniqueId() == course.getUniqueId() &&
                Objects.equal(getDepartment(), course.getDepartment()) &&
                Objects.equal(getCourseNumber(), course.getCourseNumber()) &&
                Objects.equal(getSectionNumber(), course.getSectionNumber()) &&
                Objects.equal(getUuid(), course.getUuid()) &&
                Objects.equal(getSemester(), course.getSemester());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDepartment(), getCourseNumber(), getSectionNumber(), getUuid(), getUniqueId(), getSemester());
    }

    @Override
    public String toString() {
        String identifier;
        if (Strings.isNullOrEmpty(name)) {
            identifier = "department='" + department + '\'' +
                ", courseNumber='" + courseNumber + '\'' +
                ", sectionNumber='" + sectionNumber + '\'';
        } else {
            identifier = "name='" + name + '\'';
        }
        return "Course{" +
                identifier +
                ", uuid='" + uuid + '\'' +
                ", uniqueId=" + uniqueId +
                ", semester=" + semester +
                '}';
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Course} class has a great deal of potentially Nullable
     * fields. For each nullable field an exponential amount of constructor
     * overloads could be required. If you would like to do those constructor
     * overloads then more power to you, but I am too lazy for that.
     * <p>
     * Our convention is that each field that can be null will have a Builder
     * setter prefixed by the word <code>with</code> and suffixed by the field
     * name in camel case. All nonnull fields will be specified in
     * {@link #create}.
     */
    public static class Builder {
        private int uniqueId;
        private String name;                                    /* Name of Course */
        private Semester semester;
        private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
        private Set<Assignment> assignments;
        private int semesterId;

        public Builder withDatabaseId(int uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSemester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder withSemesterId(int semesterId) {
            this.semesterId = semesterId;
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
                @Nonnull String name,
                @Nonnull String uuid,
                @Nonnull Semester semester
        ) {
            this.semester = semester;
            String[] nameArray = name.split("-");
            String department = nameArray[0];
            String courseNumber = nameArray[1];
            String sectionNumber = nameArray[2];
            return create(
                    department,
                    courseNumber,
                    sectionNumber,
                    uuid
            );
        }

        public Course create(
                @Nonnull String department,
                @Nonnull String courseNumber,
                @Nonnull String sectionNumber,
                @Nonnull String uuid
        ) {
            Preconditions.checkNotNull(department, "Cannot create a course with a null department");
            Preconditions.checkNotNull(courseNumber, "Cannot create a course with a null courseNumber");
            Preconditions.checkNotNull(sectionNumber, "Cannot create a course with a null sectionNumber");
            Preconditions.checkNotNull(uuid, "Cannot create a course with a null uuid");

            return new Course(department, courseNumber, sectionNumber, uuid, this);
        }

        public Course createUnique(
                @Nonnull String department,
                @Nonnull String courseNumber,
                @Nonnull String sectionNumber
        ) {
            return create(department, courseNumber, sectionNumber, UUIDGenerator.uuid());
        }
    }
}
