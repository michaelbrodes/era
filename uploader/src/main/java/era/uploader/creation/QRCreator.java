package era.uploader.creation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * A multithreaded QRCode creator. It generates the UUIDs to be stored in the
 * QR code and creates an image of those QR codes. It processes QR Codes in
 * batches based off of how many cpu cores are on the machine generating them.
 */
@ParametersAreNonnullByDefault
public class QRCreator implements Callable<List<QRCode>> {
    private final List<Student> students;
    private final String assignment;
    private final Course course;
    private final int pageSize;

    public QRCreator(Course course, List<Student> student, String assignmentName, int pageSize) {
        Preconditions.checkNotNull(student);
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(assignmentName);

        this.students = student;
        this.course = course;
        this.assignment = assignmentName;
        this.pageSize = pageSize;
    }

    /**
     * Creates new QR code for a student. The QR code encodes Universally
     * Unique ID. There should be a QR code for every page of every
     * assignment.
     *
     * @return A page object mapping a QR code to a student.
     * @throws Exception Most probably a QR writing exception. Callable makes
     *                   the class throw a general exception
     */
    @Override
    public List<QRCode> call() throws Exception {
        ImmutableList.Builder<QRCode> qrCodesBuilder = ImmutableList.builder();
        for (Student student : students) {
            Assignment studentAssignment = createAssignmentForStudent(student);
            for (int i = 0; i < pageSize; i++) {
                qrCodesBuilder.add(generateQRCode(course, student, studentAssignment, i));
            }
        }

        return qrCodesBuilder.build();
    }

    /**
     * Creates a new {@link Assignment} object for a student. It bases it off of the given course
     * and assignmentName given to the object during construction.
     */
    @VisibleForTesting
    Assignment createAssignmentForStudent(Student student) {
        Preconditions.checkNotNull(student);

        Assignment.Builder assignmentBuilder = Assignment.builder()
                .withCourse(course)
                .withStudent(student);

        // anything less than 0 is an invalid database id
        if (course.getUniqueId() > 0) {
            assignmentBuilder.withCourse_id(course.getUniqueId());
        }

        if (student.getUniqueId() > 0) {
            assignmentBuilder.withStudent_id(student.getUniqueId());
        }

        return assignmentBuilder.create(assignment);
    }

    /**
     * Generates a new UUID to unique identify a page of an assignment. Then
     * it encodes that UUID into a QR code. We create a new QRCode object off
     * of the generated UUID, encoded image, student, course, and page number
     * to keep track of the meta data related to a QR code.
     */
    @VisibleForTesting
    QRCode generateQRCode(
            Course course,
            Student student,
            Assignment assignment,
            int pageNumber
    ) throws WriterException {
        QRCode qrcode = new QRCode(course, student, assignment, pageNumber);
        String uuid = UUID.randomUUID().toString();
        qrcode.setUUID(uuid);

        try {
            qrcode.setQRCode(createNewQRCodeImage(uuid));
        } catch (WriterException e) {
            System.err.println("Issue writing QR Code");
            throw e;
        }

        return qrcode;
    }

    /**
     * Encodes the uuid into a QR Code and writes that QR Code to an image.
     *
     * @param uuid universally unique id that identifies a student on an
     *             assignment page.
     * @return an image of a generated QR Code
     * @throws WriterException We had a problem either encoding the UUID or
     *                         converting the QR Code's {@link BitMatrix} to a
     *                         BufferedImage.
     */
    private BufferedImage createNewQRCodeImage(String uuid) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix qrBitMatrix = writer.encode(
                uuid,
                BarcodeFormat.QR_CODE,
                AveryConstants.QR_WIDTH,
                AveryConstants.QR_HEIGHT
        );

        return MatrixToImageWriter.toBufferedImage(qrBitMatrix);
    }
}
