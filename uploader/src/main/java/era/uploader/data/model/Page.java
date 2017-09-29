package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.zxing.common.BitMatrix;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An individual page for an assignment. A page has a QR code that encodes a
 * unique uuid of the student's schoolId and a sequence number.
 */
@ParametersAreNonnullByDefault
public class Page {
    private Student student;
    private int sequenceNumber;
    // transient means that the BitMatrix should not be serialized over rest
    // calls and it should not be stored in the database
    private transient BitMatrix qrCode;
    private String uuid;
    private Assignment assignment;

    private Page(Builder builder) {
        this.student = builder.student;
        this.sequenceNumber = builder.sequenceNumber;
        this.uuid = builder.uuid;
        this.qrCode = builder.qrCode;
        this.assignment = builder.assignment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        Preconditions.checkNotNull(student);
        this.student = student;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return a unique string encoded by a QR code.
     */
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
    }

    @Nullable
    public BitMatrix getQrCode() {
        return qrCode;
    }

    public void setQrCode(@Nullable BitMatrix qrCode) {
        this.qrCode = qrCode;
    }

    @Nullable
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(@Nullable Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;

        Page page = (Page) o;

        return uuid.equals(page.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @ParametersAreNonnullByDefault
    public static class Builder {
        private Student student;
        private int sequenceNumber;
        private BitMatrix qrCode;
        private Assignment assignment;
        private String uuid;

        public Builder byStudent(Student student) {
            Preconditions.checkNotNull(student);
            this.student = student;
            return this;
        }

        public Builder withSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder withUuid(String uuid) {
            Preconditions.checkNotNull(uuid);
            this.uuid = uuid;
            return this;
        }

        public Builder withQRCode(@Nullable BitMatrix qrCode) {
            this.qrCode = qrCode;
            return this;
        }

        public Builder forAssignment(@Nullable Assignment assignment) {
            this.assignment = assignment;
            return this;
        }

        public Page create() {
            return new Page(this);
        }
    }
}
