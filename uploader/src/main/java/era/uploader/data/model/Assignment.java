package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import era.uploader.common.IOUtil;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Assignment {
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private Collection<QRCodeMapping> QRCodeMappings = new HashSet<>();  /* Set of QRCodeMapping objects for each Assignment */
    private PDDocument image;
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

        this.imageFilePath = IOUtil.removeSpaces(course.getName())
                + '_'
                + student.getSchoolId()
                + "_"
                + IOUtil.removeSpaces(name)
                + ".pdf";
        this.name = name;
        this.QRCodeMappings = QRCodeMappings == null ? Sets.newHashSet() : QRCodeMappings;
        this.student = student;
        this.course = course;
    }

    public Assignment(
            String imageFilePath,
            String name,
            int course_id,
            int student_id,
            int uniqueId
    ) {
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.course_id = course_id;
        this.student_id = student_id;
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;

        Assignment that = (Assignment) o;

        if (getImageFilePath() != null ? !getImageFilePath().equals(that.getImageFilePath()) : that.getImageFilePath() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getQRCodeMappings() != null ? !getQRCodeMappings().equals(that.getQRCodeMappings()) : that.getQRCodeMappings() != null) return false;
        return student != null ? student.equals(that.student) : that.student == null;
    }

    @Override
    public int hashCode() {
        int result = getImageFilePath() != null ? getImageFilePath().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getQRCodeMappings() != null ? getQRCodeMappings().hashCode() : 0);
        result = 31 * result + (student != null ? student.hashCode() : 0);
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

    @Nonnull
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
}
