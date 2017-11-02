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
public class QRCodeMapping {
    private Student student;
    private int sequenceNumber;
    // transient means that the BitMatrix should not be serialized over rest
    // calls and it should not be stored in the database
    private transient BitMatrix qrCode;
    private String uuid;
    private String tempDocumentName;
    private transient PDDocument document;

    private QRCodeMapping(@Nonnull String uuid, Builder builder) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
        this.student = builder.student;
        this.sequenceNumber = builder.sequenceNumber;
        this.qrCode = builder.qrCode;
        this.document = builder.document;
        this.tempDocumentName = builder.tempDocumentName;
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

    /**
     * A Builder is a <em>design pattern</em> that allows you to specify constructor
     * arguments with just plain setters. The reason why a builder is used on
     * this class is that there is a plethora of potentially nullable fields in
     * this class, which would require an exponentially large amount of
     * constructor overloads. You are welcome to write those constructor
     * overloads but I am too lazy for it.
     *
     * @return a new {@link QRCodeMapping.Builder} used to construct a new QRCodeMapping
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getTempDocumentName() {
        return tempDocumentName;
    }

    public void setTempDocumentName(String tempDocumentName) {
        this.tempDocumentName = tempDocumentName;
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
        private PDDocument document;
        private String tempDocumentName;

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
