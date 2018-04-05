package era.server.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import era.server.data.Model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * An assignment is a pdf scan of the student's actual assignment. One Course
 * can have many assignments. Additionally one Student can also have many
 * assignments.
 */
@ParametersAreNullableByDefault
public class Assignment implements Model {
    public static final String ENDPOINT = "/assignment";
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private LocalDateTime createdDateTime;
    private Student student;
    private Course course;
    private String course_id;
    private String student_id;
    private String courseName;
    private String studentUname;
    private final String uuid;

    /**
     * Creates a new Assignment object. All Nonnull arguments are required
     * arguments and match "NOT NULL" columns in the database.
     *
     * @param imageFilePath the path to the pdf storing this assignment
     * @param name the name of the assignment
     * @param course the course that this assignment belongs to
     * @param student the student who turned this assignment in
     */
    public Assignment(
            String uuid,
            @Nonnull String imageFilePath,
            @Nonnull String name,
            @Nonnull Course course,
            @Nonnull Student student,
            @Nonnull LocalDateTime createdDateTime
    ) {
        Preconditions.checkNotNull(uuid, "uuid id must be a valid database id");
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(imageFilePath, "Cannot create an Assignment with a null imageFilePath");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");
        Preconditions.checkNotNull(createdDateTime, "Created date time cannot be null");

        this.uuid = uuid;
        this.createdDateTime = createdDateTime;
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.student = student;
        this.course = course;
    }


    private Assignment(
            @Nonnull String name,
            @Nonnull LocalDateTime createdDateTime,
            @Nonnull String uuid,
            Builder builder
    ) {
        this.name = name;
        this.course = builder.course;
        this.student = builder.student;
        this.createdDateTime = createdDateTime;
        this.imageFilePath = builder.imageFilePath;
        this.course_id = builder.course_id;
        this.student_id = builder.student_id;
        this.uuid = uuid;
        this.courseName = builder.course == null
                ? builder.courseName
                : builder.course.getName();
        this.studentUname = builder.studentUname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equal(getName(), that.getName()) &&
                Objects.equal(getStudent(), that.getStudent()) &&
                Objects.equal(getCourse(), that.getCourse()) &&
                Objects.equal(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getStudent(), getCourse(), getUuid());
    }

    /* Getters and Setters */
    @Nonnull
    public String getImageFilePath() {
        return imageFilePath;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public Course getCourse() {
        return course;
    }

    public Student getStudent() {
        return student;
    }

    @Nonnull
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    @Nonnull
    public Timestamp getCreatedDateTimeStamp() {
        return Timestamp.valueOf(createdDateTime);
    }

    @Nonnull
    public String getCreatedDateTimeString() {
        return createdDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStudentUname() {
        return studentUname;
    }

    @Override
    public Map<String, Object> toViewModel() {
        // Format: Month/Day/Year e.g. July/13/2014
        String assignmentDate = createdDateTime.format(DateTimeFormatter.ofPattern("L/d/y"));
        return ImmutableMap.of(
                "name", name,
                "createdDateTime", assignmentDate,
                "course", courseName,
                "uuid", uuid,
                "username", studentUname
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Assignment} class has a great deal of potentially Nullable
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
        private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
        private String course_id;
        private String student_id;
        private Student student;
        private Course course;
        private LocalDateTime createDateTime;
        private String courseName;
        private String studentUname;

        public Builder withImageFilePath(String imageFilePath) {
            this.imageFilePath = imageFilePath;
            return this;
        }

        public Builder withCourse(Course course) {
            this.course = course;
            return this;
        }

        public Builder withStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder withCourse_id(String course_id) {
            this.course_id = course_id;
            return this;
        }

        public Builder withStudent_id(String student_id) {
            this.student_id = student_id;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createDateTime) {
            this.createDateTime = createDateTime;
            return this;
        }

        public Builder withCourseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public Builder withStudentUname(String studentUname) {
            this.studentUname = studentUname;
            return this;
        }

        public Assignment create(@Nonnull String name, @Nonnull String uuid) {
            Preconditions.checkNotNull(name);
            if (createDateTime == null) {
                createDateTime = LocalDateTime.now();
            }
            return new Assignment(name, createDateTime, uuid, this);
        }

    }
}

