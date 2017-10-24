package era.uploader.processing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import era.uploader.common.IOUtil;
import era.uploader.data.PageDAO;
import era.uploader.data.database.MockPageDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PDFProcessorTest {
    private static final String SINGLE_TEST = "src/test/resources/single-page_300dpi.pdf";
    private static final String MULTI_TEST = "src/test/resources/test-pdfs/300dpi.pdf";

    @Test
    public void process_fivePageInput() throws Exception {
        Path testDocPath = Paths.get(IOUtil.convertToLocal(MULTI_TEST));
        Course course = Course.builder()
                .withCourseNumber("111")
                .withSectionNumber("001")
                .forDepartment("CHEM")
                .withName("Intro to Chemistry")
                .forSemester("FALL")
                .create();
        Student student = Student.builder()
                .takingCourses(ImmutableSet.of(course))
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .withUserName("sarcher")
                .create();
        Page dbPage = Page.builder()
                .byStudent(student)
                .withSequenceNumber(1)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Infiltrate the Kremlin";
        PageDAO pageDAO = new MockPageDAOImpl();
        pageDAO.insert(dbPage);

        Collection<Assignment> processed = PDFProcessor.process(
                pageDAO,
                testDocPath,
                course,
                assignmentName
        );

        Assert.assertTrue(processed.size() == 1);
        Assignment assignment = Iterators.getOnlyElement(processed.iterator());
        Assert.assertEquals(assignmentName, assignment.getName());
        Assert.assertTrue(assignment.getPages().size() == 5);
    }

    @Test
    public void process_onePageInput() throws Exception {
        Path testDocPath = Paths.get(IOUtil.convertToLocal(SINGLE_TEST));
        Course course = Course.builder()
                .withCourseNumber("111")
                .withSectionNumber("001")
                .forDepartment("CHEM")
                .withName("Intro to Chemistry")
                .forSemester("FALL")
                .create();
        Student student = Student.builder()
                .takingCourses(ImmutableSet.of(course))
                .withFirstName("Lana")
                .withLastName("Kane")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .withUserName("lkane")
                .create();
        Page dbPage = Page.builder()
                .byStudent(student)
                .withSequenceNumber(1)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Defeat ODIN";
        PageDAO pageDAO = new MockPageDAOImpl();
        pageDAO.insert(dbPage);

        Collection<Assignment> processed = PDFProcessor.process(
                pageDAO,
                testDocPath,
                course,
                assignmentName
        );

        Assert.assertTrue(processed.size() == 1);
        Assignment assignment = Iterators.getOnlyElement(processed.iterator());
        Assert.assertEquals(assignmentName, assignment.getName());
        Assert.assertTrue(assignment.getPages().size() == 1);
    }

    @Test
    public void mergePDF() throws Exception {
        PDDocument testDoc = PDDocument.load(new File(IOUtil.convertToLocal(SINGLE_TEST)));
        Course testCourse = new Course();
        Student test_student = new Student();
        test_student.setSchoolId("schoolid");
        testCourse.setName("COURSE_NAME");
        PDFProcessor p = new PDFProcessor(new MockPageDAOImpl(), Collections.singletonList(SINGLE_TEST), testCourse, "testAssignment");
        Set<Assignment> test_assignments = new HashSet<>();
        Page test_page = Page.builder().create("testuuid");
        Page test_page2 = Page.builder().create("testuuid2");
        test_page.setDocument(testDoc);
        test_page2.setDocument(testDoc);
        Assignment a = new Assignment("src/test/resources/split/tests.pdf", "assignment_name", ImmutableSet.of(test_page, test_page2), test_student);
        test_assignments.add(a);
        p.mergeAssignmentPages(test_assignments);
        Assert.assertTrue(new File("src/test/resources/split/tests.pdf").exists());
    }
}