package era.server.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import era.server.api.UUIDGenerator;
import era.server.data.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that will represent each individual student and provide a means to
 * builder QR codes, and match the information inside of each code to its
 * respective student.
 */
@ParametersAreNullableByDefault
public class Student implements Model {
    public static final String ENDPOINT = "/student";
    /* Class Fields */
    private final String userName;
    private String email;
    // every course that the student belongs to
    private final Set<Course> courses;
    private final Set<Assignment> assignments;
    private String uuid;

    /* Constructors */
    protected Student(@Nonnull String userName, Builder builder) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.uuid = builder.uuid;
        this.email = builder.email;
        this.courses = builder.courses == null ? Sets.newHashSet() : builder.courses;
        this.assignments = builder.assignments == null ? Sets.newHashSet() : builder.assignments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Student(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.courses = new HashSet<>();
        this.assignments = new HashSet<>();
        this.uuid = UUIDGenerator.uuid();
    }

    /**
     * This getter will return null if we haven't stored this student into the database.
     */
    @Nullable
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Map<String, Object> toViewModel() {
        List<String> courseNames = ImmutableList.copyOf(
                courses.stream()
                .map(Course::getName)
                .collect(Collectors.toList()));

        return ImmutableMap.of(
                "uuid", uuid,
                "userName", userName,
                "email", email,
                "courses", courseNames
        );
    }

    @Nonnull
    public String getUserName() {
        return userName;
    }

    /**
     * A getter for the email of a student. It defaults to an SIUE type email.
     *
     * @return an email based off of {@link #userName}
     */
    @Nonnull
    public String getEmail() {
        return email == null ? userName + "@siue.edu" : email;
    }

    @Nonnull
    public Set<Course> getCourses() {
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equal(getUserName(), student.getUserName()) &&
                Objects.equal(getEmail(), student.getEmail()) &&
                Objects.equal(getUuid(), student.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserName(), getEmail(), getUuid());
    }

    @Nonnull
    public Set<Assignment> getAssignments() {
        return assignments;
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Student} class has a great deal of potentially Nullable
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
        private String email = null;
        private String uuid = null;
        // every course that the student belongs to
        private Set<Course> courses = Sets.newHashSet();
        private Set<Assignment> assignments = Sets.newHashSet();

        public Builder() {

        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withCourses(@Nonnull Set<Course> courses) {
            Preconditions.checkNotNull(courses);

            this.courses.addAll(courses);
            return this;
        }

        public Builder withCourse(Course course) {
            this.courses.add(course);
            return this;
        }

        public Builder withAssignments(@Nonnull Collection<Assignment> assignments) {
            Preconditions.checkNotNull(assignments);

            this.assignments.addAll(assignments);
            return this;
        }

        public Builder withUUID(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Student create(@Nonnull String userName) {
            return new Student(userName, this);
        }
    }
}

