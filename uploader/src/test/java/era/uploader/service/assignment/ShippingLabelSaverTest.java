package era.uploader.service.assignment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import era.uploader.service.QRCreationService;
import era.uploader.service.assignment.AveryConstants;
import era.uploader.service.assignment.QRCode;
import era.uploader.service.assignment.QRCodePDF;
import era.uploader.service.assignment.QRCreator;
import era.uploader.service.assignment.ShippingLabelSaver;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShippingLabelSaverTest {

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
                .create("how to be cool");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("one image pdf: " + qrFileName);
        QRCodePDF shippingLabelPDF = new QRCodePDF(AveryConstants.Shipping.CELLS_PER_PAGE);
        ShippingLabelSaver qrSaver = new ShippingLabelSaver(shippingLabelPDF);
        QRCreator creator = new QRCreator(
                course,
                Collections.singletonList(student),
                assignment.getName(),
                1,
                AveryConstants.Shipping.QR_HEIGHT,
                AveryConstants.Shipping.QR_WIDTH
        );
        QRCode qrcode = creator.generateQRCode(course,student, assignment, 1);

        qrSaver.onSuccess(Collections.singletonList(qrcode));
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(1, shippingLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_twoImage() throws Exception {
        Course course = Course.builder()
                .withName("ebrbrbrbrbrbrbrbrbrbr")
                .create("CS", "111", "001");
        Student buddy = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Student vic = Student.builder()
                .withFirstName("vic")
                .withLastName("mensa")
                .create("vmensa");

        Assignment assignment = Assignment.builder()
                .create("how to be cool");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("two image pdf: " + qrFileName);
        QRCodePDF shippingLabelPDF = new QRCodePDF(AveryConstants.Shipping.CELLS_PER_PAGE);
        ShippingLabelSaver qrSaver = new ShippingLabelSaver(shippingLabelPDF);
        QRCreator creator = new QRCreator(
                course,
                Arrays.asList(buddy, vic),
                assignment.getName(),
                1,
                AveryConstants.Shipping.QR_HEIGHT,
                AveryConstants.Shipping.QR_WIDTH
        );
        QRCode buddyQR = creator.generateQRCode(course, buddy, assignment, 1);
        QRCode vicQR = creator.generateQRCode(course, vic, assignment, 1);

        qrSaver.onSuccess(Arrays.asList(buddyQR, vicQR));
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(2, shippingLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_threeImage() throws Exception {
        Course course = Course.builder()
                .withName("ebrbrbrbrbrbrbrbrbrbr")
                .create("CS", "111", "001");
        Student buddy = Student.builder()
                .withFirstName("buddy")
                .withLastName("dude")
                .create("bdude");
        Student vic = Student.builder()
                .withFirstName("vic")
                .withLastName("mensa")
                .create("vmensa");
        Student mike = Student.builder()
                .withFirstName("killer")
                .withLastName("mike")
                .create("kmike");

        Assignment assignment = Assignment.builder()
                .create("how to be cool");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("three image pdf: " + qrFileName);
        QRCodePDF shippingLabelPDF = new QRCodePDF(AveryConstants.Shipping.CELLS_PER_PAGE);
        ShippingLabelSaver qrSaver = new ShippingLabelSaver(shippingLabelPDF);
        QRCreator creator = new QRCreator(
                course,
                Arrays.asList(buddy, vic, mike),
                assignment.getName(),
                1,
                AveryConstants.Shipping.QR_HEIGHT,
                AveryConstants.Shipping.QR_WIDTH
        );
        QRCode buddyQR = creator.generateQRCode(course, buddy, assignment, 1);
        QRCode vicQR = creator.generateQRCode(course, vic, assignment, 1);
        QRCode mikeQR = creator.generateQRCode(course, mike, assignment, 1);

        qrSaver.onSuccess(Arrays.asList(buddyQR, vicQR, mikeQR));
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(3, shippingLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }

    @Test
    public void onSuccess_fullPage() throws Exception {
        Course course = Course.builder()
                .withName("ebrbrbrbrbrbrbrbrbrbr")
                .create("CS", "111", "001");
        List<Student> testList = ImmutableList.of(
                Student.builder()
                     .withFirstName("buddy")
                     .withLastName("dude")
                     .create("bdude"),
                Student.builder()
                     .withFirstName("vic")
                     .withLastName("mensa")
                     .create("vmensa"),
                Student.builder()
                     .withFirstName("killer")
                     .withLastName("mike")
                     .create("kmike"),
                Student.builder()
                    .withFirstName("joey")
                    .withLastName("bada$$")
                    .create("jbada$$"),
                Student.builder()
                    .withFirstName("yo")
                    .withLastName("gotti")
                    .create("ygotti"),
                Student.builder()
                    .withFirstName("kanye")
                    .withLastName("west")
                    .create("kwest"),
                Student.builder()
                    .withFirstName("uzi")
                    .withLastName("vert")
                    .create("uvert"),
                Student.builder()
                    .withFirstName("gucci")
                    .withLastName("mane")
                    .create("gmane"),
                Student.builder()
                    .withFirstName("ugly")
                    .withLastName("god")
                    .create("ugod"),
                Student.builder()
                    .withFirstName("based")
                    .withLastName("god")
                    .create("bgod"),
                Student.builder()
                    .withFirstName("two")
                    .withLastName("chainz")
                    .create("tchainz")
        );

        Assignment assignment = Assignment.builder()
                .create("how to be cool");
        String qrFileName = QRCreationService.ASSIGNMENTS_DIR + File.separator + assignment.getName() + " " + UUID.randomUUID().toString() + ".pdf";
        System.out.println("page + 1 image pdf: " + qrFileName);
        QRCodePDF shippingLabelPDF = new QRCodePDF(AveryConstants.Shipping.CELLS_PER_PAGE);
        ShippingLabelSaver qrSaver = new ShippingLabelSaver(shippingLabelPDF);
        QRCreator creator = new QRCreator(
                course,
                testList,
                assignment.getName(),
                1,
                AveryConstants.Shipping.QR_HEIGHT,
                AveryConstants.Shipping.QR_WIDTH
        );
        List<QRCode> toPrint = Lists.newArrayList();
        for (Student testStudent : testList) {
            toPrint.add(creator.generateQRCode(course, testStudent, assignment, 1));
        }

        qrSaver.onSuccess(toPrint);
        qrSaver.save(qrFileName);

        Path qrPath = Paths.get(qrFileName);
        Assert.assertTrue(Files.exists(qrPath));
        Assert.assertEquals(1, shippingLabelPDF.getCurrentCell());
        Files.deleteIfExists(qrPath);
    }
}