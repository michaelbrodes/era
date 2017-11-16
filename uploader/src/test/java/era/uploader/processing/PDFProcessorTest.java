package era.uploader.processing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import era.uploader.common.IOUtil;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.database.MockAssignmentDAOImpl;
import era.uploader.data.database.MockQRCodeMappingDAOImpl;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
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
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .create("CHEM", "111", "001");
        Student student = Student.builder()
                .withCourses(ImmutableSet.of(course))
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .create("sarcher");
        QRCodeMapping dbQRCodeMapping = QRCodeMapping.builder()
                .withSequenceNumber(1)
                .withStudent(student)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Infiltrate the Kremlin";
        QRCodeMappingDAO qrCodeMappingDAO = new MockQRCodeMappingDAOImpl();
        AssignmentDAO assignmentDAO = new MockAssignmentDAOImpl();
        qrCodeMappingDAO.insert(dbQRCodeMapping);

        Collection<Assignment> processed = PDFProcessor.process(
                qrCodeMappingDAO,
                assignmentDAO,
                testDocPath,
                course,
                assignmentName,
                null
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
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .create("CHEM", "111", "001");
        Student student = Student.builder()
                .withCourses(ImmutableSet.of(course))
                .withFirstName("Lana")
                .withLastName("Kane")
                .withUniqueId(1)
                .withSchoolId("800888888")
                .create("lkane");
        QRCodeMapping dbQRCodeMapping = QRCodeMapping.builder()
                .withSequenceNumber(1)
                .withStudent(student)
                .create("6ab251a5-6c4e-4688-843f-60aea570c3a6");
        String assignmentName = "Defeat ODIN";
        QRCodeMappingDAO QRCodeMappingDAO = new MockQRCodeMappingDAOImpl();
        AssignmentDAO assignmentDAO = new MockAssignmentDAOImpl();
        QRCodeMappingDAO.insert(dbQRCodeMapping);

        Collection<Assignment> processed = PDFProcessor.process(
                QRCodeMappingDAO,
                assignmentDAO,
                testDocPath,
                course,
                assignmentName,
                null
        );

        Assert.assertTrue(processed.size() == 1);
        Assignment assignment = Iterators.getOnlyElement(processed.iterator());
        Assert.assertEquals(assignmentName, assignment.getName());
        Assert.assertTrue(assignment.getQRCodeMappings().size() == 1);
    }

    @Test
    @Ignore
    public void mergePDF() throws Exception {
        PDDocument testDoc = PDDocument.load(new File(IOUtil.convertToLocal(SINGLE_TEST)));
        Course testCourse = Course.builder()
                .withName("COURSE_NAME")
                .create("CHEM", "111", "001");
        Student test_student = new Student("sarcher");
        test_student.setSchoolId("schoolid");
        PDFProcessor p = new PDFProcessor(new MockQRCodeMappingDAOImpl(), new MockAssignmentDAOImpl(), Collections.singletonList(SINGLE_TEST), testCourse, "testAssignment", "testDomain");
        Set<Assignment> test_assignments = new HashSet<>();
        QRCodeMapping test_QRCodeMapping = QRCodeMapping.builder().create("testuuid");
        QRCodeMapping test_QRCodeMapping2 = QRCodeMapping.builder().create("testuuid2");
        test_QRCodeMapping.setDocument(testDoc);
        test_QRCodeMapping2.setDocument(testDoc);
        Assignment a = new Assignment("src/test/resources/split/tests.pdf", "assignment_name", testCourse, ImmutableSet.of(test_QRCodeMapping, test_QRCodeMapping2), test_student, LocalDateTime.now());
        test_assignments.add(a);
        p.mergeAssignmentPages(test_assignments);
        Assert.assertTrue(new File("src/test/resources/split/tests.pdf").exists());
    }
}