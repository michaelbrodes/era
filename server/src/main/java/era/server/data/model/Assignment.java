package era.server.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import era.server.data.Model;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * An assignment is a pdf scan of the student's actual assignment. One Course
 * can have many assignments. Additionally one Student can also have many
 * assignments.
 */
public class Assignment implements Model {
    public static final String ENDPOINT = "/assignment";
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private LocalDateTime createdDateTime;
    private Student student;
    private Course course;
    private long course_id;
    private long student_id;
    private long uniqueId;

    /* Constructors */
    public Assignment() {

    }

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
            @Nonnull String imageFilePath,
            @Nonnull String name,
            @Nonnull Course course,
            @Nonnull Student student,
            @Nonnull LocalDateTime createdDateTime
    ) {
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(imageFilePath, "Cannot create an Assignment with a null imageFilePath");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");
        Preconditions.checkNotNull(createdDateTime, "Created date time cannot be null");

        this.createdDateTime = createdDateTime;
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.student = student;
        this.course = course;
    }

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
            long uniqueId,
            @Nonnull String imageFilePath,
            @Nonnull String name,
            @Nonnull Course course,
            @Nonnull Student student,
            @Nonnull LocalDateTime createdDateTime
    ) {
        Preconditions.checkArgument(uniqueId != 0, "unique id must be a valid database id");
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(imageFilePath, "Cannot create an Assignment with a null imageFilePath");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");
        Preconditions.checkNotNull(createdDateTime, "Created date time cannot be null");

        this.uniqueId = uniqueId;
        this.createdDateTime = createdDateTime;
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.student = student;
        this.course = course;
    }

    /**
     * Creates a new Assignment object. All Nonnull arguments are required
     * arguments and match "NOT NULL" columns in the database.
     *
     * @param name the name of the assignment
     * @param course the course that this assignment belongs to
     * @param student the student who turned this assignment in
     * @param createdDateTime the date and time that the assignment has been
     *                        created
     */
    public Assignment(
            @Nonnull String name,
            @Nonnull Student student,
            @Nonnull Course course,
            @Nonnull LocalDateTime createdDateTime,
            @Nonnull String imageFilePath
    ) {
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");
        Preconditions.checkNotNull(createdDateTime, "Cannot create an Assignment with a null create date");
        Preconditions.checkNotNull(imageFilePath, "Cannot create an Assignment with a null create date");

        this.name = name;
        this.student = student;
        this.course = course;
        this.imageFilePath = imageFilePath;
    }

    public Assignment(
            @Nonnull String imageFilePath,
            @Nonnull String name,
            int course_id,
            int student_id,
            int uniqueId
    ) {
        Preconditions.checkNotNull(imageFilePath, "Assignment cannot have a null imageFilePath!");
        Preconditions.checkNotNull(name, "Assignment cannot have a null name!");
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.course_id = course_id;
        this.student_id = student_id;
        this.uniqueId = uniqueId;
    }

    private Assignment(
            String name,
            LocalDateTime createdDateTime,
            Builder builder
    ) {
        this.name = name;
        this.course = builder.course;
        this.student = builder.student;
        this.createdDateTime = createdDateTime;
        this.imageFilePath = builder.imageFilePath;
        this.course_id = builder.course_id;
        this.student_id = builder.student_id;
        this.uniqueId = builder.uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assignment that = (Assignment) o;

        return uniqueId == that.uniqueId && name.equals(that.name) && student.equals(that.student) && course.equals(that.course);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + student.hashCode();
        result = 31 * result + course.hashCode();
        result = 31 * result + Long.hashCode(uniqueId);
        return result;
    }

    /* Getters and Setters */
    @Nonnull
    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(@Nonnull String imageFilePath) {
        Preconditions.checkNotNull(imageFilePath, "a null imageFilePath cannot be stored in the database");
        this.imageFilePath = imageFilePath;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        Preconditions.checkNotNull(name, "a null name cannot be stored in the database.");
        this.name = name;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }

    public long getStudent_id() {
        return student_id;
    }

    public void setStudent_id(long student_id) {
        this.student_id = student_id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(@Nonnull Course course) {
        Preconditions.checkNotNull(course, "a null course cannot be stored in the database");
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Map<String, Object> toViewModel() {
        // Format: Month/Day/Year Era e.g. July/13/2014 AD
        String assignmentDate = createdDateTime.format(DateTimeFormatter.ofPattern("L/d/y G"));
        return ImmutableMap.of(
                "name", name,
                "createdDateTime", assignmentDate,
                "student", student.getUserName(),
                "course", course.getName(),
                "uniqueId", uniqueId
        );
    }

    public static Builder builder() {
        return new Builder();
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

    public void setCreatedDateTime(@Nonnull LocalDateTime createdDateTime) {
        Preconditions.checkNotNull(createdDateTime, "Created date time cannot be null");
        this.createdDateTime = createdDateTime;
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
        private long course_id;
        private long student_id;
        private long uniqueId;
        private Student student;
        private Course course;
        private LocalDateTime createDateTime;

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

        public Builder withCourse_id(long course_id) {
            this.course_id = course_id;
            return this;
        }

        public Builder withStudent_id(long student_id) {
            this.student_id = student_id;
            return this;
        }

        public Builder withUniqueId(long uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createDateTime) {
            this.createDateTime = createDateTime;
            return this;
        }

        public Assignment create(@Nonnull String name) {
            Preconditions.checkNotNull(name);
            if (createDateTime == null) {
                createDateTime = LocalDateTime.now();
            }
            return new Assignment(name, createDateTime, this);
        }

    }
}

