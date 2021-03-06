/*
 * This file is generated by jOOQ.
*/
package era.server.data.database.tables.records;


import era.server.data.database.tables.Admin;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


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
public class AdminRecord extends UpdatableRecordImpl<AdminRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1081274532;

    /**
     * Setter for <code>dev.admin.student_id</code>.
     */
    public AdminRecord setStudentId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>dev.admin.student_id</code>.
     */
    public String getStudentId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>dev.admin.uploader_password</code>.
     */
    public AdminRecord setUploaderPassword(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>dev.admin.uploader_password</code>.
     */
    public String getUploaderPassword() {
        return (String) get(1);
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
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Admin.ADMIN.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Admin.ADMIN.UPLOADER_PASSWORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getUploaderPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getUploaderPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminRecord value1(String value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminRecord value2(String value) {
        setUploaderPassword(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AdminRecord
     */
    public AdminRecord() {
        super(Admin.ADMIN);
    }

    /**
     * Create a detached, initialised AdminRecord
     */
    public AdminRecord(String studentId, String uploaderPassword) {
        super(Admin.ADMIN);

        set(0, studentId);
        set(1, uploaderPassword);
    }
}
