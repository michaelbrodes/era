package era.server.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import era.server.data.Model;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Class that will represent each individual student and provide a means to
 * builder QR codes, and match the information inside of each code to its
 * respective student.
 */
public class Student implements Model {
    public static final String ENDPOINT = "/student";
    /* Class Fields */
    private String userName;
    private String email;
    private long uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
    // every course that the student belongs to
    private Set<Course> courses = Sets.newHashSet();

    /* Constructors */
    /**
     * This is basically our default constructor because it just takes the
     * only required field.
     *
     * @param userName the only required field to create a valid student.
     */
    public Student(@Nonnull String userName) {
        Preconditions.checkNotNull(userName);
        this.userName = userName;
    }

    public Student(
            @Nonnull String userName,
            String email,
            int uniqueId,
            Set<Course> courses
    ) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.uniqueId = uniqueId;
        this.email = email;
        this.courses = courses == null ? Sets.newHashSet() : courses;
    }

    public Student(
            @Nonnull String userName,
            String email,
            int uniqueId
    ) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.email = email;
        this.uniqueId = uniqueId;
    }

    private Student(@Nonnull String userName, Builder builder) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.courses = builder.courses == null ? Sets.newHashSet() : builder.courses;
        this.uniqueId = builder.uniqueId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Nonnull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nonnull String userName) {
        Preconditions.checkNotNull(userName);
        this.userName = userName;
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

    public void setCourses(Set<Course> courses) {
        this.courses = courses == null ? Sets.newHashSet() : courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return getUniqueId() == student.getUniqueId() && getUserName().equals(student.getUserName()) && getEmail().equals(student.getEmail());
    }

    @Override
    public int hashCode() {
        int result = getUserName().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + (int) (getUniqueId() ^ (getUniqueId() >>> 32));
        return result;
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
        private long uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
        private String email;
        // every course that the student belongs to
        private Set<Course> courses = Sets.newHashSet();

        public Builder() {

        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withUniqueId(long uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder withCourses(Set<Course> courses) {
            this.courses.addAll(courses);
            return this;
        }

        public Builder withCourse(Course course) {
            this.courses.add(course);
            return this;
        }

        public Student create(@Nonnull String userName) {
            Preconditions.checkNotNull(userName);
            return new Student(userName, this);
        }
    }
}

