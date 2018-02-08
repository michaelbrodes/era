package era.server.data.model;

import com.google.common.base.Objects;
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
    private final String uuid;
    private String name;                                    /* Name of Course */
    private Semester semester;
    private Set<Student> studentsEnrolled = Sets.newHashSet(); /* Set of Students in the Class */
    private Set<Assignment> assignments = Sets.newHashSet();

    /* Constructor */
    private Course(
            @Nonnull String name,
            @Nonnull Semester semester,
            @Nonnull String uuid,
            @Nonnull Builder builder) {
        Preconditions.checkNotNull(name, "Cannot create a course with a null name");
        Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");
        Preconditions.checkNotNull(uuid, "Cannot create a course with a null uuid");

        this.name = name;
        this.semester = semester;
        this.uuid = uuid;
        this.studentsEnrolled = builder.studentsEnrolled == null ?
                Sets.newHashSet() :
                builder.studentsEnrolled;
        this.assignments = builder.assignments == null ?
                Sets.newHashSet() :
                builder.assignments;
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

    public String getUuid() {
        return uuid;
    }

    public static Builder builder() {
        return new Builder();
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
                "uuid", uuid,
                "name", name,
                "semester", semester.toString(),
                "studentsEnrolled", studentNames,
                "assignments", assignmentNames
        );
    }

    @Override
    public String toString() {
        return "Course{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", semester=" + semester +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equal(getUuid(), course.getUuid()) &&
                Objects.equal(getName(), course.getName()) &&
                Objects.equal(getSemester(), course.getSemester());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid(), getName(), getSemester());
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
        private Set<Student> studentsEnrolled = new HashSet<>(); /* Set of Students in the Class */
        private Set<Assignment> assignments = new HashSet<>();
        private Semester semester;
        private String name;
        private String uuid;

        public Builder withStudents(Set<Student> studentsEnrolled) {
            this.studentsEnrolled = studentsEnrolled;
            return this;
        }

        public Builder withAssignments(Set<Assignment> assignments) {
            this.assignments = assignments;
            return this;
        }

        public Builder withSemester(@Nonnull Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public String getDatabaseId() {
            return this.uuid;
        }

        public Course create(
                @Nonnull String name,
                @Nonnull String uuid,
                @Nonnull Semester semester
        ) {
            Preconditions.checkNotNull(name, "Cannot create a course with a null name");
            Preconditions.checkNotNull(uuid, "Cannot create a course with a null uuid");
            Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");

            return new Course(name, semester, uuid, this);
        }

        public Course create() {
            Preconditions.checkNotNull(name, "Cannot create a course with a null name");
            Preconditions.checkNotNull(semester, "Cannot create a course with a null semester");

            return create(name, uuid, semester);
        }
    }
}

