package era.uploader.controller;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.sun.javafx.collections.ObservableListWrapper;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

/**
 * A <em>ViewModel</em> that holds meta data of students
 */
public class StudentMetaData {
    private final SimpleStringProperty studentName = new SimpleStringProperty("");
    private final SimpleStringProperty studentId = new SimpleStringProperty("");
    private final SimpleStringProperty courseName = new SimpleStringProperty("");

    public StudentMetaData(
            String studentName,
            String studentId,
            String courseName
    ) {
        setStudentName(studentName);
        setStudentId(studentId);
        setCourseName(courseName);
    }

    public String getStudentName() {
        return studentName.get();
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public String getStudentId() {
        return studentId.get();
    }

    public SimpleStringProperty studentIdProperty() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
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

    public static ObservableList<StudentMetaData> fromMultimap(Multimap<Course, Student> coursesToStudents) {
        return coursesToStudents
                .entries()
                .stream()
                .map((courseToStudent) -> {
                    Student student = courseToStudent.getValue();
                    String name = student.getFirstName() + student.getLastName();
                    String course = courseToStudent.getKey().getName();
                    String id = student.getSchoolId();

                    return new StudentMetaData(name, id, course);
                })
                .collect(Collector.of(FXCollections::observableArrayList, List::add,(l, r) -> {
                    l.addAll(r);
                    return l;
                }));
    }
}
