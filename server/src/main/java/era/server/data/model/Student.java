package era.server.data.model;

import com.google.common.collect.Sets;

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
    private long uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
    // every course that the student belongs to
    private Set<Course> courses = Sets.newHashSet();

    /* Constructors */
    public Student() {

    }
    public Student(
            String firstName,
            String lastName,
            String schoolId,
            String userName,
            long uniqueId,
            Set<Course> courses
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.userName = userName;
        this.uniqueId = uniqueId;
        this.courses = courses;
    }

    public Student(Builder builder) {
        this.courses = builder.courses;
        this.firstName = builder.firstName;
        this.schoolId = builder.schoolId;
        this.lastName = builder.lastName;
        this.uniqueId = builder.uniqueId;
        this.userName = builder.userName;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* Getters and Setters */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        return uniqueId == student.uniqueId;
    }

    @Override
    public int hashCode() {
        return (int) uniqueId;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. The reason why a builder is used on
     * this class is that there is a plethora of potentially nullable fields in
     * this class, which would require an exponentially large amount of
     * constructor overloads. You are welcome to write those constructor
     * overloads but I am too lazy for it.
     */
    public static class Builder {
        private String firstName; /* Student's first name */
        private String lastName;  /* Student's last name */
        private String schoolId;  /* Identifier for each student provided by the school */
        private String userName;
        private long uniqueId;    /* Identifier that we generate to uniquely identify each student inside the QR code */
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

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withSchoolId(String schoolId) {
            this.schoolId = schoolId;
            return this;
        }

        public Builder withUniqueId(long uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder takingCourses(Set<Course> courses) {
            this.courses.addAll(courses);
            return this;
        }

        public Builder takingCourse(Course course) {
            this.courses.add(course);
            return this;
        }

        public Student create() {
            return new Student(this);
        }
    }
}
