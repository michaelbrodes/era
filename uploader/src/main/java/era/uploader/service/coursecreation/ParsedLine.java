package era.uploader.service.coursecreation;

import com.google.common.base.Strings;

public class ParsedLine {
    private final String firstName;
    private final String lastName;
    private final String userName;
    private final String studentId;
    private final String courseDepartment;
    private final String courseNumber;
    private final String courseSection;

    private ParsedLine(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.userName = builder.userName;
        this.studentId = builder.studentId;
        this.courseDepartment = builder.courseDepartment;
        this.courseNumber = builder.courseNumber;
        this.courseSection = builder.courseSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getCourseSection() {
        return courseSection;
    }

    public boolean hasAllFields() {
        return !Strings.isNullOrEmpty(firstName)
                && !Strings.isNullOrEmpty(lastName)
                && !Strings.isNullOrEmpty(userName)
                && !Strings.isNullOrEmpty(studentId)
                && !Strings.isNullOrEmpty(courseDepartment)
                && !Strings.isNullOrEmpty(courseNumber)
                && !Strings.isNullOrEmpty(courseSection);
    }


    public static final class Builder {
        private String firstName;
        private String lastName;
        private String userName;
        private String studentId;
        private String courseDepartment;
        private String courseNumber;
        private String courseSection;

        private Builder() {
        }

        public ParsedLine create() {
            return new ParsedLine(this);
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder courseDepartment(String courseDepartment) {
            this.courseDepartment = courseDepartment;
            return this;
        }

        public Builder courseNumber(String courseNumber) {
            this.courseNumber = courseNumber;
            return this;
        }

        public Builder courseSection(String courseSection) {
            this.courseSection = courseSection;
            return this;
        }
    }
}
