/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables.records;


import era.uploader.data.database.jooq.tables.Semester;
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
public class SemesterRecord extends UpdatableRecordImpl<SemesterRecord> implements Record3<Integer, String, Integer> {

    private static final long serialVersionUID = -1857263301;

    /**
     * Setter for <code>semester.unique_id</code>.
     */
    public SemesterRecord setUniqueId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>semester.unique_id</code>.
     */
    public Integer getUniqueId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>semester.term</code>.
     */
    public SemesterRecord setTerm(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>semester.term</code>.
     */
    public String getTerm() {
        return (String) get(1);
    }

    /**
     * Setter for <code>semester.year</code>.
     */
    public SemesterRecord setYear(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>semester.year</code>.
     */
    public Integer getYear() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Semester.SEMESTER.UNIQUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Semester.SEMESTER.TERM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Semester.SEMESTER.YEAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getUniqueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getTerm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getYear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getUniqueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getTerm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getYear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SemesterRecord value1(Integer value) {
        setUniqueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SemesterRecord value2(String value) {
        setTerm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SemesterRecord value3(Integer value) {
        setYear(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SemesterRecord values(Integer value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SemesterRecord
     */
    public SemesterRecord() {
        super(Semester.SEMESTER);
    }

    /**
     * Create a detached, initialised SemesterRecord
     */
    public SemesterRecord(Integer uniqueId, String term, Integer year) {
        super(Semester.SEMESTER);

        set(0, uniqueId);
        set(1, term);
        set(2, year);
    }
}
