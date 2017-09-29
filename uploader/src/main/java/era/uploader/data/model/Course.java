package era.uploader.data.model;

import java.util.Set;

public class Course {
    private int id;
    private Set<Student> students;
    private Set<Assignment> assignments;

    public Set<Student> getStudents() {
        return students;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }
}
