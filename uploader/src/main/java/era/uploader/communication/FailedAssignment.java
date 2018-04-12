package era.uploader.communication;

public class FailedAssignment {
    private final String assignmentName;
    private final String studentName; 
    private final String courseName; 
    private final String semesterName;
    private final String reason;

    public FailedAssignment(String assignmentName, String studentName, String courseName, String semesterName, String reason) {
        this.assignmentName = assignmentName;
        this.studentName = studentName;
        this.courseName = courseName;
        this.semesterName = semesterName;
        this.reason = reason; 
    }

    @Override
    public String toString() {
        return "Failed Assignment for student "
                .concat(studentName);
    }
}
