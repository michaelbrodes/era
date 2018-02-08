package era.uploader.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import era.uploader.common.UUIDGenerator;
import era.uploader.common.UploaderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Class that will represent each individual student and provide a means to
 * builder QR codes, and match the information inside of each code to its
 * respective student.
 */
public class Student {
    private final String uuid;
    /* Class Fields */
    private final String firstName; /* Student's first name */
    private final String lastName;  /* Student's last name */
    private final String schoolId;  /* Identifier for each student provided by the school */
    private final String userName;
    private final String email;     /* Student's SIUE email */
    private int uniqueId;/* Identifier that we generate to uniquely identify each student inside the QR code */
    // every course that the student belongs to
    private Set<Course> courses;

    private Student(@Nonnull String userName, @Nonnull String uuid, Builder builder) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.courses = builder.courses == null ? Sets.newHashSet() : builder.courses;
        this.firstName = builder.firstName;
        this.schoolId = builder.schoolId;
        this.lastName = builder.lastName;
        this.uniqueId = builder.uniqueId;
        this.email = builder.email;
        this.uuid = uuid;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* Getters and Setters */
    @Nullable
    public String getFirstName() {
        return firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    @Nullable
    public String getSchoolId() {
        return schoolId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Nonnull
    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses == null ? Sets.newHashSet() : courses;
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
        return this.email;
    }


    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return getUniqueId() == student.getUniqueId() &&
                Objects.equal(getUuid(), student.getUuid()) &&
                Objects.equal(getSchoolId(), student.getSchoolId()) &&
                Objects.equal(getUserName(), student.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid(), getSchoolId(), getUserName(), getUniqueId());
    }

    @Override
    public String toString() {
        return "Student{" +
                "uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", uniqueId=" + uniqueId +
                '}';
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Student} class has a great deal of potentially Nullable
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
        private String firstName; /* Student's first name */
        private String lastName;  /* Student's last name */
        private String schoolId;  /* Identifier for each student provided by the school */
        private String email;     /* Student's SIUE email */
        private int uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
        private String uuid;
        // every course that the student belongs to
        private Set<Course> courses = Sets.newHashSet();

        private Builder() {

        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withSchoolId(String schoolId) {
            this.schoolId = schoolId;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withUniqueId(int uniqueId) {
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

        public Student create(@Nonnull String userName, @Nonnull String uuid) {
            Preconditions.checkNotNull(userName);
            this.email = userName
                    + "@"
                    + UploaderProperties.instance().getEmailSuffix();
            return new Student(userName, uuid, this);
        }

        public Student createUnique(@Nonnull String userName) {
            Preconditions.checkNotNull(userName);
            return create(userName, UUIDGenerator.uuid());
        }
    }
}
