package era.uploader.service.assignment;

import com.google.common.collect.Lists;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import era.uploader.service.QRCreationService;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AddressLabelSaverTest {
    private static final String ADDRESS_LABELS =
            "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "test-pdfs"
                    + File.separator
                    + "Avery5160EasyPeelAddressLabels.pdf";

    @Test
    public void writeHeader() throws IOException {
        Course course = Course.builder()
                .withName("ebrbrbrbrbrbrbrbrbrbr")
                .create("CS", "111", "001");
        Student student = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Assignment assignment = Assignment.builder()
                .create("header test");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("header test file: " + qrFileName);
        QRCodePDF addressLabelPDF = new QRCodePDF(ADDRESS_LABELS, AveryConstants.Address.CELLS_PER_PAGE);
        AddressLabelSaver addressLabelSaver = new AddressLabelSaver(addressLabelPDF, student, course);
        PDPageContentStream pageEditor
                = addressLabelPDF.getPageEditor();

        addressLabelSaver.writeHeader(pageEditor, addressLabelPDF.getHeight(), addressLabelPDF.getWidth());
        pageEditor.close();
        addressLabelSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_oneImage() throws Exception {
        Course course = Course.builder()
                .withName("ebrbrbrbrbrbrbrbrbrbr")
                .create("CS", "111", "001");
        Student student = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Assignment assignment = Assignment.builder()
                .create("test one image");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("one image pdf: " + qrFileName);

        QRCodePDF addressLabelPDF = new QRCodePDF(ADDRESS_LABELS, AveryConstants.Address.CELLS_PER_PAGE);
        AddressLabelSaver qrSaver = new AddressLabelSaver(addressLabelPDF, student, course);
        QRCreator creator = new QRCreator(
                course,
                Collections.singletonList(student),
                assignment.getName(),
                1,
                AveryConstants.Address.QR_HEIGHT,
                AveryConstants.Address.QR_WIDTH
        );
        QRCode qrcode = creator.generateQRCode(course,student, assignment, 1);

        qrSaver.onSuccess(Collections.singletonList(qrcode));
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(1, addressLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_fourImagse() throws Exception {
        Course course = Course.builder()
                .create("CHEM", "365B", "001");
        Student student = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Assignment assignment = Assignment.builder()
                .create("test one image");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("one image pdf: " + qrFileName);

        QRCodePDF addressLabelPDF = new QRCodePDF(ADDRESS_LABELS, AveryConstants.Address.CELLS_PER_PAGE);
        AddressLabelSaver qrSaver = new AddressLabelSaver(addressLabelPDF, student, course);
        QRCreator creator = new QRCreator(
                course,
                Collections.singletonList(student),
                assignment.getName(),
                4,
                AveryConstants.Address.QR_HEIGHT,
                AveryConstants.Address.QR_WIDTH
        );
        List<QRCode> qrCodes = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            qrCodes.add(creator.generateQRCode(course,student, assignment, i));
        }

        qrSaver.onSuccess(qrCodes);
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(4, addressLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_twoPages() throws Exception {
        Course course = Course.builder()
                .create("CHEM", "365B", "001");
        Student student = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Assignment assignment = Assignment.builder()
                .create("test one image");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("one image pdf: " + qrFileName);

        QRCodePDF addressLabelPDF = new QRCodePDF(ADDRESS_LABELS, AveryConstants.Address.CELLS_PER_PAGE);
        AddressLabelSaver qrSaver = new AddressLabelSaver(addressLabelPDF, student, course);
        QRCreator creator = new QRCreator(
                course,
                Collections.singletonList(student),
                assignment.getName(),
                31,
                AveryConstants.Address.QR_HEIGHT,
                AveryConstants.Address.QR_WIDTH
        );
        List<QRCode> qrCodes = Lists.newArrayList();
        for (int i = 0; i < 31; i++) {
            qrCodes.add(creator.generateQRCode(course,student, assignment, i));
        }

        qrSaver.onSuccess(qrCodes);
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(1, addressLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }
}