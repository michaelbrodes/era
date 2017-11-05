package era.uploader.data.model;

import era.uploader.processing.PDFProcessor;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class Assignment {
    /* Class Fields */
    private String imageFilePath;               /* Path to the PDF file with the images associated with the assignment */
    private String name;                        /* Name of the Assignment */
    private Set<Page> pages = new HashSet<>();  /* Set of Page objects for each Assignment */
    private PDDocument image;
    private Student student;
    private Course course;

    /* Constructor */
    public Assignment(
            String imageFilePath,
            String name,
            Set<Page> pages,
            Student student
    ) {
        this.imageFilePath = imageFilePath;
        this.name = name;
        this.pages = pages;
        this.student = student;
    }

    public Assignment(
            String name,
            Set<Page> pages,
            Student student,
            Course course
    ) {
        this.imageFilePath = course.getName() + '_' + student.getSchoolId() + name;
        this.name = name;
        this.pages = pages;
        this.student = student;
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;

        Assignment that = (Assignment) o;

        if (getImageFilePath() != null ? !getImageFilePath().equals(that.getImageFilePath()) : that.getImageFilePath() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getPages() != null ? !getPages().equals(that.getPages()) : that.getPages() != null) return false;
        return student != null ? student.equals(that.student) : that.student == null;
    }

    @Override
    public int hashCode() {
        int result = getImageFilePath() != null ? getImageFilePath().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPages() != null ? getPages().hashCode() : 0);
        result = 31 * result + (student != null ? student.hashCode() : 0);
        return result;
    }

    /* Getters and Setters */
    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void setPages(Set<Page> pages) {
        this.pages = pages;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() { return this.student; }
}
