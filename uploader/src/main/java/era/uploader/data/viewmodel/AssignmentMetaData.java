package era.uploader.data.viewmodel;

import com.google.common.base.Preconditions;
import era.uploader.data.database.jooq.tables.records.AllAssignmentsRecord;
import javafx.beans.property.SimpleStringProperty;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * A javafx model representing all the information that a user would like to
 * know about an assignment. This is based off of the
 * {@link era.uploader.data.database.jooq.tables.AllAssignments} database
 * view.
 */
@ParametersAreNullableByDefault
public class AssignmentMetaData {
    private final SimpleStringProperty assignment = new SimpleStringProperty("");
    private final SimpleStringProperty student = new SimpleStringProperty("");
    private final SimpleStringProperty eightHundred = new SimpleStringProperty("");
    private final SimpleStringProperty course = new SimpleStringProperty("");
    private final SimpleStringProperty courseId = new SimpleStringProperty("");
    private final SimpleStringProperty semester = new SimpleStringProperty("");
    private final SimpleStringProperty created = new SimpleStringProperty("");
    private final SimpleStringProperty location = new SimpleStringProperty("");

    /**
     * Can't use builder because javafx is weird, so here is a constructor
     * with all fields
     */
    public AssignmentMetaData(
            String assignment,
            String student,
            String eightHundred,
            String course,
            String courseId,
            String semester,
            String created,
            String location
    ){
        setAssignment(assignment);
        setStudent(student);
        setEightHundred(eightHundred);
        setCourse(course);
        setCourseId(courseId);
        setSemester(semester);
        setCreated(created);
        setLocation(location);
    }

    public String getAssignment() {
        return assignment.get();
    }

    public SimpleStringProperty assignmentProperty() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment.set(assignment);
    }

    public String getStudent() {
        return student.get();
    }

    public SimpleStringProperty studentProperty() {
        return student;
    }

    public void setStudent(String student) {
        this.student.set(student);
    }

    public String getEightHundred() {
        return eightHundred.get();
    }

    public SimpleStringProperty eightHundredProperty() {
        return eightHundred;
    }

    public void setEightHundred(String eightHundred) {
        this.eightHundred.set(eightHundred);
    }

    public String getCourse() {
        return course.get();
    }

    public SimpleStringProperty courseProperty() {
        return course;
    }

    public void setCourse(String course) {
        this.course.set(course);
    }

    public String getCourseId() {
        return courseId.get();
    }

    public SimpleStringProperty courseIdProperty() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId.set(courseId);
    }

    public String getSemester() {
        return semester.get();
    }

    public SimpleStringProperty semesterProperty() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester.set(semester);
    }

    public String getCreated() {
        return created.get();
    }

    public SimpleStringProperty createdProperty() {
        return created;
    }

    public void setCreated(String created) {
        this.created.set(created);
    }

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public static AssignmentMetaData fromAllAssignments(@Nonnull AllAssignmentsRecord record) {
        Preconditions.checkNotNull(record, "Cannot convert a null AllAssignmentRecord");
        return new AssignmentMetaData(
                record.getAssignment(),
                record.getStudent().toString(),
                record.getEightHundredNumber(),
                record.getCourse(),
                record.getChildCourseId().toString(),
                record.getSemester().toString(),
                record.getCreated().toString(),
                record.getFileLocation()
        );
    }
}
