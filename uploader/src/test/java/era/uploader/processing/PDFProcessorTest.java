package era.uploader.processing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import era.uploader.common.IOUtil;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.PageDAO;
import era.uploader.data.database.MockAssignmentDAOImpl;
import era.uploader.data.database.MockPageDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
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
                .withName("Intro to Chemistry")
                .withSemester("FALL")
                .create("CHEM", "111", "001");
        Student student = Student.builder()
                .takingCourses(ImmutableSet.of(course))
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .create("sarcher");
        QRCodeMapping dbQRCodeMapping = QRCodeMapping.builder()
                .withSequenceNumber(1)
                .byStudent(student)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Infiltrate the Kremlin";
        PageDAO pageDAO = new MockPageDAOImpl();
        AssignmentDAO assignmentDAO = new MockAssignmentDAOImpl();
        pageDAO.insert(dbQRCodeMapping);

        Collection<Assignment> processed = PDFProcessor.process(
                pageDAO,
                assignmentDAO,
                testDocPath,
                course,
                assignmentName
        );

        Assert.assertTrue(processed.size() == 1);
        Assignment assignment = Iterators.getOnlyElement(processed.iterator());
        Assert.assertEquals(assignmentName, assignment.getName());
        Assert.assertTrue(assignment.getQRCodeMappings().size() == 5);
    }

    @Test
    public void process_onePageInput() throws Exception {
        Path testDocPath = Paths.get(IOUtil.convertToLocal(SINGLE_TEST));
        Course course = Course.builder()
                .withName("Intro to Chemistry")
                .withSemester("FALL")
                .create("CHEM", "111", "001");
        Student student = Student.builder()
                .takingCourses(ImmutableSet.of(course))
                .withFirstName("Lana")
                .withLastName("Kane")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .create("lkane");
        QRCodeMapping dbQRCodeMapping = QRCodeMapping.builder()
                .withSequenceNumber(1)
                .byStudent(student)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Defeat ODIN";
        PageDAO pageDAO = new MockPageDAOImpl();
        AssignmentDAO assignmentDAO = new MockAssignmentDAOImpl();
        pageDAO.insert(dbQRCodeMapping);

        Collection<Assignment> processed = PDFProcessor.process(
                pageDAO,
                assignmentDAO,
                testDocPath,
                course,
                assignmentName
        );

        Assert.assertTrue(processed.size() == 1);
        Assignment assignment = Iterators.getOnlyElement(processed.iterator());
        Assert.assertEquals(assignmentName, assignment.getName());
        Assert.assertTrue(assignment.getQRCodeMappings().size() == 1);
    }

    @Test
    public void mergePDF() throws Exception {
        PDDocument testDoc = PDDocument.load(new File(IOUtil.convertToLocal(SINGLE_TEST)));
        Course testCourse = Course.builder()
                .withName("COURSE_NAME")
                .create("CHEM", "111", "001");
        Student test_student = new Student("sarcher");
        test_student.setSchoolId("schoolid");
        PDFProcessor p = new PDFProcessor(new MockPageDAOImpl(), new MockAssignmentDAOImpl(), Collections.singletonList(SINGLE_TEST), testCourse, "testAssignment");
        Set<Assignment> test_assignments = new HashSet<>();
        QRCodeMapping test_QRCodeMapping = QRCodeMapping.builder().create("testuuid");
        QRCodeMapping test_QRCodeMapping2 = QRCodeMapping.builder().create("testuuid2");
        test_QRCodeMapping.setDocument(testDoc);
        test_QRCodeMapping2.setDocument(testDoc);
        Assignment a = new Assignment("src/test/resources/split/tests.pdf", "assignment_name", testCourse, ImmutableSet.of(test_QRCodeMapping, test_QRCodeMapping2), test_student);
        test_assignments.add(a);
        p.mergeAssignmentPages(test_assignments);
        Assert.assertTrue(new File("src/test/resources/split/tests.pdf").exists());
    }
}