package era.uploader.service.processing;


import era.uploader.communication.AssignmentUploader;
import era.uploader.communication.FailedAssignment;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Term;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Year;
import java.util.Collections;
import java.util.List;

@Ignore
public class PDFUploaderTest {
    private static final String SINGLE_TEST = "src/test/resources/test-pdfs/single-page_300dpi.pdf";

    @Test
    public void uploadOneAssignment() throws Exception {
        Course course = Course.builder()
                .withSemester(Semester.of(Term.SPRING, Year.of(2020)))
                .createUnique("CHEM", "131", "004");
        Student student = Student.builder()
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withSchoolId("800222222")
                .withUniqueId(1)
                .createUnique("sarcher");

        Assignment assignment = Assignment.builder()
                .withCourse(course)
                .withImageFilePath(SINGLE_TEST)
                .withStudent(student)
                .withUniqueId(1)
                .createUnique("TestAssignment");

        List<FailedAssignment> failedAssignments = AssignmentUploader
                .uploadAssignments(Collections.singletonList(assignment), "https://my-assignments.isg.siue.edu");

        Assert.assertTrue(failedAssignments.isEmpty());
    }

}
