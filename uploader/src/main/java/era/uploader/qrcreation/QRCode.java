package era.uploader.qrcreation;

import com.google.common.base.Preconditions;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

@ParametersAreNonnullByDefault
public class QRCode {
    // final: starting parameters of the factory
    private final Course course;
    private final Student student;
    private final Assignment assignment;
    private final int pageNumber;
    private String uuid;
    private BufferedImage qrCode;

    QRCode(Course course, Student student, Assignment assignment, int pageNumber) {
        Preconditions.checkNotNull(course);
        Preconditions.checkNotNull(student);
        Preconditions.checkNotNull(assignment);
        // properties that are required for later processing
        Preconditions.checkNotNull(course.getName());
        Preconditions.checkNotNull(assignment.getName());
        Preconditions.checkNotNull(student.getFirstName());
        Preconditions.checkNotNull(student.getLastName());

        this.course = course;
        this.student = student;
        this.pageNumber = pageNumber;
        this.assignment = assignment;
    }

    public QRCodeMapping createQRCodeMapping() {
        return QRCodeMapping.builder()
                .withSequenceNumber(pageNumber)
                .withStudent(student)
                .withQRCodeImage(qrCode)
                .create(uuid);
    }

    public void setUUID(String uuid) {
        Preconditions.checkNotNull(uuid);

        this.uuid = uuid;
    }

    public void setQRCode(BufferedImage qrCode) {
        Preconditions.checkNotNull(qrCode);

        this.qrCode = qrCode;
    }

    public BufferedImage getQrCode() {
        return qrCode;
    }

    public String getStudentName() {
        return student.getFirstName() + " " + student.getLastName();
    }

    public String getCourseName() {
        return course.getName();
    }

    public String getAssignmentName() {
        return assignment.getName();
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
