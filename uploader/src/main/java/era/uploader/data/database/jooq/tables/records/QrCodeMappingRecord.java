/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables.records;


import era.uploader.data.database.jooq.tables.QrCodeMapping;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class QrCodeMappingRecord extends UpdatableRecordImpl<QrCodeMappingRecord> implements Record3<String, Integer, Integer> {

    private static final long serialVersionUID = -49031961;

    /**
     * Setter for <code>qr_code_mapping.uuid</code>.
     */
    public QrCodeMappingRecord setUuid(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>qr_code_mapping.uuid</code>.
     */
    public String getUuid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>qr_code_mapping.sequence_number</code>.
     */
    public QrCodeMappingRecord setSequenceNumber(Integer value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>qr_code_mapping.sequence_number</code>.
     */
    public Integer getSequenceNumber() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>qr_code_mapping.student_id</code>.
     */
    public QrCodeMappingRecord setStudentId(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>qr_code_mapping.student_id</code>.
     */
    public Integer getStudentId() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, Integer, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, Integer, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return QrCodeMapping.QR_CODE_MAPPING.UUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return QrCodeMapping.QR_CODE_MAPPING.SEQUENCE_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return QrCodeMapping.QR_CODE_MAPPING.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getUuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getSequenceNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getSequenceNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrCodeMappingRecord value1(String value) {
        setUuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrCodeMappingRecord value2(Integer value) {
        setSequenceNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrCodeMappingRecord value3(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrCodeMappingRecord values(String value1, Integer value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached QrCodeMappingRecord
     */
    public QrCodeMappingRecord() {
        super(QrCodeMapping.QR_CODE_MAPPING);
    }

    /**
     * Create a detached, initialised QrCodeMappingRecord
     */
    public QrCodeMappingRecord(String uuid, Integer sequenceNumber, Integer studentId) {
        super(QrCodeMapping.QR_CODE_MAPPING);

        set(0, uuid);
        set(1, sequenceNumber);
        set(2, studentId);
    }
}
