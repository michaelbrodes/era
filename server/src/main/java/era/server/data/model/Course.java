package era.server.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import era.server.data.Model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that will represent Courses which assignments will belong to. This
 * will allow the instructor to associate multiple assignments to a single
 * Course.
 */
@ParametersAreNullableByDefault
public class Course implements Model {
    public static final String ENDPOINT = "/course";
    /* Class Fields */
    // will be an unsigned int in the database.
    private long uniqueId;
    private String name;                                    /* Name of Course */
    private Semester semester;
    private Set<Student> studentsEnrolled = Sets.newHashSet(); /* Set of Students in the Class */
    private Set<Assignment> assignments = Sets.newHashSet();

    /* Constructor */
    public Course(
            @Nonnull String name,
            @Nonnull String semester,
            Set<Assignment> assignments
    ) {
        Preconditions.checkNotNull(name, "Cannot create a course with a null name");
        Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
        this.name = name;
        this.semester = Semester.of(Semester.Term.valueOf(semester), Year.now());
        this.assignments = assignments == null ? Sets.newHashSet() : assignments;
    }

    /**
     * Essentially our default constructor for a course object. It is just
     * supplied only the required fields of this object.
     *
     * @param name the name of the course (typically of the form
     *             &lt;department&gt;-&lt;courseNumber&gt;-&lt;sectionNumber&gt;
     * @param semester the semester that the course belongs to
     */
    public Course (
            @Nonnull String name,
            @Nonnull Semester semester
    ) {
        Preconditions.checkNotNull(name, "Cannot create a course with a null name");
        Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
        // default as we are not in the database yet.
        this.uniqueId = 0;
        this.name = name;
        this.studentsEnrolled = Sets.newHashSet();
        this.assignments = Sets.newHashSet();
        this.semester = semester;
    }

    private Course(
            @Nonnull String name,
            @Nonnull Semester semester,
            @Nonnull Builder builder) {
        Preconditions.checkNotNull(name, "Cannot create a course with a null name");
        Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
        this.name = name;
        this.semester = semester;
        this.studentsEnrolled = builder.studentsEnrolled == null ?
                Sets.newHashSet() :
                builder.studentsEnrolled;
        this.assignments = builder.assignments == null ?
                Sets.newHashSet() :
                builder.assignments;
        this.uniqueId = builder.uniqueId;
    }


    /* Getters and Setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Semester getSemester() {
        return semester;
    }

    public Set<Student> getStudentsEnrolled() {
        // GSON might send over a null students set, so we need an empty one
        if (studentsEnrolled == null) {
            studentsEnrolled = Sets.newHashSet();
        }

        return studentsEnrolled;
    }

    public Set<Assignment> getAssignments() {
        // GSON might send over a null assignments set, so we need an empty one
        if (assignments == null){
            assignments = Sets.newHashSet();
        }

        return assignments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getUniqueId() {
        return uniqueId;
    }

    @Override
    public Map<String, Object> toViewModel() {
        List<String> assignmentNames = ImmutableList.copyOf(
                assignments.stream()
                    .map(Assignment::getName)
                    .collect(Collectors.toList())
        );
        List<String> studentNames = ImmutableList.copyOf(
                studentsEnrolled.stream()
                        .map(Student::getUserName)
                        .collect(Collectors.toList())
        );
        return ImmutableMap.of(
                "uniqueId", uniqueId,
                "name", name,
                "semester", semester.toString(),
                "studentsEnrolled", studentNames,
                "assignments", assignmentNames
        );
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return "Course{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                ", semester=" + semester +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return (uniqueId == 0 || uniqueId == course.uniqueId)
                && (name != null ? name.equals(course.name) : course.name == null)
                && semester.equals(course.semester);
    }

    @Override
    public int hashCode() {
        int result = (int) uniqueId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
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
        private long uniqueId;
        private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
        private Set<Assignment> assignments = new HashSet<>();
        private String name;
        private Semester semester;

        public long getDatabaseId() {
            return uniqueId;
        }

        public Builder withDatabaseId(long uniqueId) {
            this.uniqueId = uniqueId;
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

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSemester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Course create(
                @Nonnull String name,
                @Nonnull Semester semester
        ) {
            Preconditions.checkNotNull(name, "Cannot create a course with a null name");
            Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
            return new Course(name, semester, this);
        }
        public Course create() {
            Preconditions.checkNotNull(name, "Cannot create a course with a null name");
            Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
            return new Course(name, semester, this);
        }

    }
}

