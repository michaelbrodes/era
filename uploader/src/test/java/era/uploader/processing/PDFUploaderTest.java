package era.uploader.processing;


import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.Year;
import java.util.Collections;

@Ignore
public class PDFUploaderTest {
    private static final String SINGLE_TEST = "src/test/resources/single-page_300dpi.pdf";

    @Test
    public void uploadOneAssignment() throws Exception {
        Course course = Course.builder()
                .withName("Intro to Chemistry")
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .create("CHEM", "111", "001");
        Student student = Student.builder()
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withSchoolId("800222222")
                .withUniqueId(1)
                .create("sarcher");

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setImageFilePath(SINGLE_TEST);
        assignment.setName("TestAssignment");
        assignment.setStudent(student);
        assignment.setUniqueId(1);

        AssignmentUploader.uploadAssignments(Collections.singletonList(assignment),"http://127.0.0.1:3000");

        Assert.assertTrue(true);
    }

}
