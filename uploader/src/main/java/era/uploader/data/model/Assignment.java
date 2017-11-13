package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import era.uploader.common.IOUtil;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An assignment is a pdf scan of the student's actual assignment. One Course
 * can have many assignments. Additionally one Student can also have many
 * assignments.
 */
public class Assignment {
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private transient Collection<QRCodeMapping> QRCodeMappings = new HashSet<>();  /* Set of QRCodeMapping objects for each Assignment */
    private transient PDDocument image;
    private Student student;
    private Course course;
    private int course_id;
    private int student_id;
    private int uniqueId;

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
     * @param QRCodeMappings the QRCodeMappings that make up this assignment
     * @param student the student who turned this assignment in
     */
    public Assignment(
            @Nonnull String imageFilePath,
            @Nonnull String name,
            @Nonnull Course course,
            Collection<QRCodeMapping> QRCodeMappings,
            @Nonnull Student student
    ) {
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(imageFilePath, "Cannot create an Assignment with a null imageFilePath");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");

        this.imageFilePath = imageFilePath;
        this.name = name;
        this.QRCodeMappings = QRCodeMappings == null ? Sets.newHashSet() : QRCodeMappings;
        this.student = student;
        this.course = course;
    }

    /**
     * Creates a new Assignment object. All Nonnull arguments are required
     * arguments and match "NOT NULL" columns in the database.
     *
     * @param name the name of the assignment
     * @param course the course that this assignment belongs to
     * @param QRCodeMappings the QRCodeMappings that make up this assignment
     * @param student the student who turned this assignment in
     */
    public Assignment(
            @Nonnull String name,
            Collection<QRCodeMapping> QRCodeMappings,
            @Nonnull Student student,
            @Nonnull Course course
    ) {
        Preconditions.checkNotNull(name, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(student, "Cannot create an Assignment with a null student");
        Preconditions.checkNotNull(course, "Cannot create an Assignment with a null course");
        this.name = name;
        this.QRCodeMappings = QRCodeMappings == null ? Sets.newHashSet() : QRCodeMappings;
        this.student = student;
        this.course = course;
        this.imageFilePath = generateFileLocation();
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
            Builder builder
    ) {
        this.name = name;
        this.course = builder.course;
        this.student = builder.student;

        this.imageFilePath = course == null || student == null ?
                null :
                generateFileLocation();

        this.course_id = builder.course_id;
        this.student_id = builder.student_id;
        this.image = builder.image;
        this.QRCodeMappings = builder.QRCodeMappings;
        this.uniqueId = builder.uniqueId;
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
                + student.getSchoolId()
                + "_"
                + IOUtil.removeSpaces(name)
                + ".pdf";
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
        result = 31 * result + uniqueId;
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

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    @Nonnull
    public Collection<QRCodeMapping> getQRCodeMappings() {
        return QRCodeMappings;
    }

    /**
     * Convenience method for if you want a set of QRCodeMappings.
     */
    @Nonnull
    public Set<QRCodeMapping> getPagesSet() {
        return Sets.newHashSet(QRCodeMappings);
    }

    public void setQRCodeMappings(Set<QRCodeMapping> QRCodeMappings) {
        this.QRCodeMappings = QRCodeMappings == null ? Sets.newHashSet() : QRCodeMappings;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(@Nonnull Course course) {
        Preconditions.checkNotNull(course, "a null course cannot be stored in the database");
        this.course = course;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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
        private Collection<QRCodeMapping> QRCodeMappings = new HashSet<>();  /* Set of QRCodeMapping objects for each Assignment */
        private transient PDDocument image;
        private int course_id;
        private int student_id;
        private int uniqueId;
        private Student student;
        private Course course;

        public Builder withImageFilePath(String imageFilePath) {
            this.imageFilePath = imageFilePath;
            return this;
        }

        public Builder withQRCodeMappings(Collection<QRCodeMapping> QRCodeMappings) {
            this.QRCodeMappings = QRCodeMappings;
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

        public Assignment create(@Nonnull String name) {
            Preconditions.checkNotNull(name);
            return new Assignment(name, this);
        }

    }
}
