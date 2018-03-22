package era.uploader.data.viewmodel;

import era.uploader.data.model.Course;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.annotation.ParametersAreNullableByDefault;
import java.util.Objects;

/**
 * A javafx model representing all the information that a user would like to
 * know about an assignment. This is based off of the
 * {@link era.uploader.data.database.jooq.tables.AllAssignments} database
 * view.
 */
@ParametersAreNullableByDefault
public class AssignmentPrintoutMetaData {
    private final SimpleStringProperty assignmentName = new SimpleStringProperty("");
    private final SimpleIntegerProperty numPages = new SimpleIntegerProperty();
    private Course courseSection;
    private final SimpleStringProperty courseName = new SimpleStringProperty("");

    public AssignmentPrintoutMetaData( String assignmentName, int numPages, String courseName, Course courseSection){
        setAssignmentName(assignmentName);
        setNumPages(numPages);
        setCourseSection(courseSection);
        setCourseName(courseName);
    }

    public String getAssignmentName() {
        return assignmentName.get();
    }

    public SimpleStringProperty assignmentNameProperty() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName.set(assignmentName);
    }

    public int getNumPages() {
        return numPages.get();
    }

    public SimpleIntegerProperty numPagesProperty() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages.set(numPages);
    }

    public Course getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(Course courseSection){
        this.courseSection = courseSection;
    }

    public String getCourseName() {
        return courseName.get();
    }

    public SimpleStringProperty courseNameProperty() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentPrintoutMetaData that = (AssignmentPrintoutMetaData) o;
        return Objects.equals(getCourseName(), that.getCourseName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseName());
    }
}
