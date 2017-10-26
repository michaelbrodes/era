package era.uploader.data.model;

import com.google.common.base.Preconditions;
import com.google.zxing.common.BitMatrix;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * An individual page for an assignment. A page has a QR code that encodes a
 * unique uuid of the student's schoolId and a sequence number.
 */
@ParametersAreNullableByDefault
public class Page {
    private Student student;
    private int sequenceNumber;
    // transient means that the BitMatrix should not be serialized over rest
    // calls and it should not be stored in the database
    private transient BitMatrix qrCode;
    private String uuid;

    private transient PDDocument document;
    private Assignment assignment;

    private Page(@Nonnull String uuid, Builder builder) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
        this.student = builder.student;
        this.sequenceNumber = builder.sequenceNumber;
        this.qrCode = builder.qrCode;
        this.assignment = builder.assignment;
        this.document = builder.document;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
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
    @Nonnull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@Nonnull String uuid) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
    }


    public BitMatrix getQrCode() {
        return qrCode;
    }

    public void setQrCode( BitMatrix qrCode) {
        this.qrCode = qrCode;
    }


    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment( Assignment assignment) {
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

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. The reason why a builder is used on
     * this class is that there is a plethora of potentially nullable fields in
     * this class, which would require an exponentially large amount of
     * constructor overloads. You are welcome to write those constructor
     * overloads but I am too lazy for it.
     *
     * @return a new {@link Page.Builder} used to construct a new Page
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. The reason why a builder is used on
     * this class is that there is a plethora of potentially nullable fields in
     * this class, which would require an exponentially large amount of
     * constructor overloads. You are welcome to write those constructor
     * overloads but I am too lazy for it.
     */
    public static class Builder {
        private Student student;
        private int sequenceNumber;
        private BitMatrix qrCode;
        private Assignment assignment;
        private PDDocument document;

        public Builder withDocument(PDDocument document) {
            this.document = document;
            return this;
        }

        public Builder byStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder withSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder withQRCode(BitMatrix qrCode) {
            this.qrCode = qrCode;
            return this;
        }

        public Builder forAssignment(Assignment assignment) {
            this.assignment = assignment;
            return this;
        }

        public Page create(@Nonnull String uuid) {
            Preconditions.checkNotNull(uuid);
            return new Page(uuid, this);
        }
    }
}
