package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
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
    /* Class Fields */
    private String firstName; /* Student's first name */
    private String lastName;  /* Student's last name */
    private String schoolId;  /* Identifier for each student provided by the school */
    private String userName;
    private int uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
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
            String firstName,
            String lastName,
            String schoolId,
            @Nonnull String userName,
            int uniqueId,
            Set<Course> courses
    ) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.userName = userName;
        this.uniqueId = uniqueId;
        this.courses = courses == null ? Sets.newHashSet() : courses;
    }

    public Student(
            String firstName,
            String lastName,
            String schoolId,
            @Nonnull String userName,
            int uniqueId
    ) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.userName = userName;
        this.uniqueId = uniqueId;
    }

    private Student(@Nonnull String userName, Builder builder) {
        Preconditions.checkNotNull(userName, "Cannot create a Student object with a null userName");
        this.userName = userName;
        this.courses = builder.courses == null ? Sets.newHashSet() : builder.courses;
        this.firstName = builder.firstName;
        this.schoolId = builder.schoolId;
        this.lastName = builder.lastName;
        this.uniqueId = builder.uniqueId;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* Getters and Setters */
    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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

        Student student = (Student) o;

        return uniqueId == student.uniqueId && student.schoolId.equals(schoolId);
    }

    @Override
    public int hashCode() {
        int result = getFirstName() != null ? getFirstName().hashCode() : 0;
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getSchoolId() != null ? getSchoolId().hashCode() : 0);
        result = 31 * result + getUserName().hashCode();
        result = 31 * result + getUniqueId();
        return result;
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
        return userName
                + "@"
                + UploaderProperties.instance().getEmailSuffix();
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
        private String firstName; /* Student's first name */
        private String lastName;  /* Student's last name */
        private String schoolId;  /* Identifier for each student provided by the school */
        private int uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
        // every course that the student belongs to
        private Set<Course> courses = Sets.newHashSet();

        public Builder() {

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

        public Student create(@Nonnull String userName) {
            Preconditions.checkNotNull(userName);
            return new Student(userName, this);
        }
    }
}
