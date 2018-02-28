package era.uploader.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import era.uploader.common.IOUtil;
import era.uploader.common.UUIDGenerator;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;

/**
 * An assignment is a pdf scan of the student's actual assignment. One Course
 * can have many assignments. Additionally one Student can also have many
 * assignments.
 */
public class Assignment {
    private final String name;                        /* Name of the Assignment */
    private final String uuid;
    private int course_id;
    private int student_id;
    // UNIVERSALLY UNIQUE ID used to allow the server to handle mainly Uploader clients
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private transient Collection<QRCodeMapping> qrCodeMappings; /* Set of QRCodeMapping objects for each Assignment */
    private transient PDDocument image;
    private LocalDateTime createdDateTime;
    private Student student;
    private Course course;
    private int uniqueId;

    private Assignment(
            String name,
            LocalDateTime createdDateTime,
            String uuid,
            Builder builder
    ) {
        this.name = name;
        this.course = builder.course;
        this.student = builder.student;
        this.createdDateTime = createdDateTime;

        if (builder.imageFilePath != null) {
            this.imageFilePath = builder.imageFilePath;
        } else if (course != null && student != null){
            this.imageFilePath = generateFileLocation();
        } else {
            this.imageFilePath = null;
        }

        this.course_id = builder.course_id;
        this.student_id = builder.student_id;
        this.image = builder.image;
        this.qrCodeMappings = builder.qrCodeMappings;
        this.uniqueId = builder.uniqueId;
        this.uuid = uuid;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * This generates the filename that we are going to save this assignment at.
     * We name assignments like:
     * "course name"-"student id (800 number at SIUE)"-"assignment name".pdf.
     * Note that no spaces are allowed in the filename.
     */
    private String generateFileLocation() {
        return IOUtil.removeSpaces(course.getName())
                + '_'
                + student.getUserName()
                + "_"
                + IOUtil.removeSpaces(name)
                + ".pdf";
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

    public int getCourse_id() {
        if (course_id == 0) {
            return course.getUniqueId();
        } else {
            return course_id;
        }
    }

    public int getStudent_id() {
        if (student_id == 0) {
            return student.getUniqueId();
        } else {
            return student_id;
        }
    }

    @Nonnull
    public Collection<QRCodeMapping> getQRCodeMappings() {
        return qrCodeMappings;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(@Nonnull Course course) {
        Preconditions.checkNotNull(course, "a null course cannot be stored in the database");
        this.course = course;
        this.course_id = course.getUniqueId();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(@Nonnull Student student) {
        Preconditions.checkNotNull(student);

        this.student = student;
        this.student_id = student.getUniqueId();
    }

    public int getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Nullable
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(@Nonnull LocalDateTime createdDateTime) {
        Preconditions.checkNotNull(createdDateTime, "Created date time cannot be null");
        this.createdDateTime = createdDateTime;
    }

    @Nonnull
    public String getCreatedDateTimeString() {
        return createdDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return (getCourse_id() == that.getCourse_id() || Objects.equal(getCourse(), that.getCourse())) &&
                (getStudent_id() == that.getStudent_id() || Objects.equal(getStudent(), that.getStudent())) &&
                getUniqueId() == that.getUniqueId() &&
                Objects.equal(getName(), that.getName()) &&
                Objects.equal(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getUuid(), getCourse_id(), getStudent_id(), getStudent(), getCourse(), getUniqueId());
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", student=" + student +
                ", course=" + course +
                ", uniqueId=" + uniqueId +
                '}';
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link Assignment} class has a great deal of potentially Nullable
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
        private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
        private Collection<QRCodeMapping> qrCodeMappings = new HashSet<>();  /* Set of QRCodeMapping objects for each Assignment */
        private transient PDDocument image;
        private int course_id;
        private int student_id;
        private int uniqueId;
        private Student student;
        private Course course;
        private LocalDateTime createDateTime;

        public Builder withImageFilePath(String imageFilePath) {
            this.imageFilePath = imageFilePath;
            return this;
        }

        public Builder withQRCodeMappings(Collection<QRCodeMapping> qrCodeMappings) {
            this.qrCodeMappings = qrCodeMappings;
            return this;
        }

        public Builder withImage(PDDocument image) {
            this.image = image;
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

        public Builder withCourse_id(int course_id) {
            this.course_id = course_id;
            return this;
        }

        public Builder withStudent_id(int student_id) {
            this.student_id = student_id;
            return this;
        }

        public Builder withUniqueId(int uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createDateTime) {
            this.createDateTime = createDateTime;
            return this;
        }

        public Assignment create(@Nonnull String name, @Nonnull String uuid) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(uuid, "we need a UUID to store the Assignment Server Side");
            if (createDateTime == null) {
                createDateTime = LocalDateTime.now();
            }

            return new Assignment(name, createDateTime, uuid, this);
        }

        public Assignment createUnique(@Nonnull String name) {
            return create(name, UUIDGenerator.uuid());
        }

    }
}
