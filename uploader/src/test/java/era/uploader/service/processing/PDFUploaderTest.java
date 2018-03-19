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

@Ignore
public class PDFUploaderTest {
    private static final String SINGLE_TEST = "src/test/resources/single-page_300dpi.pdf";

    @Test
    public void uploadOneAssignment() throws Exception {
        Course course = Course.builder()
                .withSemester(Semester.of(Term.FALL, Year.now()))
                .createUnique("CHEM", "111", "001");
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

        AssignmentUploader.uploadAssignments(Collections.singletonList(assignment),"http://127.0.0.1:3001");

        Assert.assertTrue(true);
    }

}
