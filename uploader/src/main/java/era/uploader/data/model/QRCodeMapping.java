package era.uploader.data.model;

import com.google.common.base.Preconditions;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.awt.image.BufferedImage;

/**
 * An individual page for an assignment. A page has a QR code that encodes a
 * unique uuid of the student's schoolId and a sequence number.
 */
@ParametersAreNullableByDefault
public class QRCodeMapping {
    private Student student;
    private int sequenceNumber;
    // transient means that the BitMatrix should not be serialized over rest
    // calls and it should not be stored in the database
    private transient BufferedImage qrCode;
    private String uuid;
    private String tempDocumentName;
    private transient PDDocument document;
    private Integer studentId;

    private QRCodeMapping(@Nonnull String uuid, Builder builder) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
        this.student = builder.student;
        this.studentId = builder.studentId;
        this.sequenceNumber = builder.sequenceNumber;
        this.qrCode = builder.qrCode;
        this.document = builder.document;
        this.tempDocumentName = builder.tempDocumentName;
    }

    public QRCodeMapping(String uuid, int sequenceNumber) {
        this.uuid = uuid;
        this.sequenceNumber = sequenceNumber;
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


    public BufferedImage getQrCode() {
        return qrCode;
    }

    public void setQrCode( BufferedImage qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QRCodeMapping)) return false;

        QRCodeMapping QRCodeMapping = (QRCodeMapping) o;

        return uuid.equals(QRCodeMapping.uuid);
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

    public static Builder builder() {
        return new Builder();
    }

    public String getTempDocumentName() {
        return tempDocumentName;
    }

    public void setTempDocumentName(String tempDocumentName) {
        this.tempDocumentName = tempDocumentName;
    }

    public Integer getStudentId() {
        return studentId;
    }


    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. We use a builder here because the
     * {@link QRCodeMapping} class has a great deal of potentially Nullable
     * fields. For each nullable field an exponential amount of constructor
     * overloads could be required. If you would like to do those constructor
     * overloads then more power to you, but I am too lazy for that.
     *
     * Our convention is that each field that can be null will have a Builder
     * setter prefixed by the word <code>with</code> and suffixed by the field
     * name in camel case. All nonnull fields will be specified in
     * {@link #create}.
     */
    public static class Builder {
        private Student student;
        private Integer studentId;
        private int sequenceNumber;
        private BufferedImage qrCode;
        private PDDocument document;
        private String tempDocumentName;

        public Builder withDocument(PDDocument document) {
            this.document = document;
            return this;
        }

        public Builder withStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder withStudentId(Integer studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder withSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder withQRCodeImage(BufferedImage qrCode) {
            this.qrCode = qrCode;
            return this;
        }
        public Builder withTempDocumentName(String name) {
            this.tempDocumentName = name;
            return this;
        }

        public QRCodeMapping create(@Nonnull String uuid) {
            Preconditions.checkNotNull(uuid, "Cannot create a QRCodeMapping without a uuid");
            return new QRCodeMapping(uuid, this);
        }
    }
}
