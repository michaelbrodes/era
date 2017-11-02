package era.server.data.model;

import com.google.common.base.Preconditions;

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
    private String uuid;
    private String tempDocumentName;
    private Assignment assignment;

    private Page(@Nonnull String uuid, Builder builder) {
        Preconditions.checkNotNull(uuid);
        this.uuid = uuid;
        this.student = builder.student;
        this.sequenceNumber = builder.sequenceNumber;
        this.assignment = builder.assignment;
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
        private Assignment assignment;
        private String tempDocumentName;

        public Builder byStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder withSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder forAssignment(Assignment assignment) {
            this.assignment = assignment;
            return this;
        }

        public Builder withTempDocumentName(String name) {
            this.tempDocumentName = name;
            return this;
        }

        public Page create(@Nonnull String uuid) {
            Preconditions.checkNotNull(uuid);
            return new Page(uuid, this);
        }
    }
}
