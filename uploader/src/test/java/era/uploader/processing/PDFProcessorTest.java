package era.uploader.processing;

import com.google.common.collect.ImmutableSet;
import era.uploader.data.database.MockPageDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PDFProcessorTest {
    @Test
    public void process() throws Exception {
    }

    @Test
    public void associateStudentsWithPage() throws Exception {
    }

    @Test
    public void mergePDF() throws Exception {
        PDDocument testDoc = PDDocument.load(new File("src/test/resources/split/1.pdf"));
        Course testCourse = new Course();
        Student test_student = new Student();
        test_student.setSchoolId("schoolid");
        testCourse.setName("COURSE_NAME");
        PDFProcessor p = new PDFProcessor(new MockPageDAOImpl(), Collections.singletonList(testDoc), testCourse, "testAssignment");
        Set<Assignment> test_assignments = new HashSet<>();
        Page test_page = Page.builder().create("testuuid");
        Page test_page2 = Page.builder().create("testuuid2");
        test_page.setDocument(testDoc);
        test_page2.setDocument(testDoc);
        Assignment a = new Assignment("src/test/resources/split/tests.pdf", "assignment_name", ImmutableSet.of(test_page, test_page2), test_student);
        test_assignments.add(a);
        p.mergePDF(test_assignments);
        Assert.assertTrue(new File("src/test/resources/split/tests.pdf").exists());
    }
}