package era.uploader.communication;

import com.google.common.base.MoreObjects;

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
        return "Failed Assignment: "
                .concat(assignmentName)
                .concat(" for student ")
                .concat(studentName)
                .concat(" and course ")
                .concat(courseName)
                .concat(" ")
                .concat(semesterName)
                .concat(" ")
                .concat(reason);
                
    }
}
