/*
 * This file is generated by jOOQ.
*/
package era.server.data.database.tables.records;


import era.server.data.database.tables.Course;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
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
public class CourseRecord extends UpdatableRecordImpl<CourseRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1522190958;

    /**
     * Setter for <code>dev.course.uuid</code>.
     */
    public CourseRecord setUuid(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>dev.course.uuid</code>.
     */
    public String getUuid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>dev.course.name</code>.
     */
    public CourseRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>dev.course.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>dev.course.semester_id</code>.
     */
    public CourseRecord setSemesterId(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>dev.course.semester_id</code>.
     */
    public String getSemesterId() {
        return (String) get(2);
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
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Course.COURSE.UUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Course.COURSE.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Course.COURSE.SEMESTER_ID;
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
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getSemesterId();
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
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getSemesterId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseRecord value1(String value) {
        setUuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseRecord value3(String value) {
        setSemesterId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CourseRecord
     */
    public CourseRecord() {
        super(Course.COURSE);
    }

    /**
     * Create a detached, initialised CourseRecord
     */
    public CourseRecord(String uuid, String name, String semesterId) {
        super(Course.COURSE);

        set(0, uuid);
        set(1, name);
        set(2, semesterId);
    }
}
