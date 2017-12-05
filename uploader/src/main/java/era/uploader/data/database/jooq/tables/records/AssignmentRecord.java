/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables.records;


import era.uploader.data.database.jooq.tables.Assignment;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class AssignmentRecord extends UpdatableRecordImpl<AssignmentRecord> implements Record6<Integer, String, String, Integer, Integer, String> {

    private static final long serialVersionUID = -2133448416;

    /**
     * Setter for <code>assignment.unique_id</code>.
     */
    public AssignmentRecord setUniqueId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>assignment.unique_id</code>.
     */
    public Integer getUniqueId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>assignment.name</code>.
     */
    public AssignmentRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>assignment.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>assignment.image_file_path</code>.
     */
    public AssignmentRecord setImageFilePath(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>assignment.image_file_path</code>.
     */
    public String getImageFilePath() {
        return (String) get(2);
    }

    /**
     * Setter for <code>assignment.course_id</code>.
     */
    public AssignmentRecord setCourseId(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>assignment.course_id</code>.
     */
    public Integer getCourseId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>assignment.student_id</code>.
     */
    public AssignmentRecord setStudentId(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>assignment.student_id</code>.
     */
    public Integer getStudentId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>assignment.created_date_time</code>.
     */
    public AssignmentRecord setCreatedDateTime(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>assignment.created_date_time</code>.
     */
    public String getCreatedDateTime() {
        return (String) get(5);
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
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, String, String, Integer, Integer, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, String, String, Integer, Integer, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Assignment.ASSIGNMENT.UNIQUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Assignment.ASSIGNMENT.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Assignment.ASSIGNMENT.IMAGE_FILE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Assignment.ASSIGNMENT.COURSE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Assignment.ASSIGNMENT.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Assignment.ASSIGNMENT.CREATED_DATE_TIME;
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getImageFilePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getCourseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getCreatedDateTime();
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getImageFilePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getCourseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getCreatedDateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value1(Integer value) {
        setUniqueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value3(String value) {
        setImageFilePath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value4(Integer value) {
        setCourseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value5(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord value6(String value) {
        setCreatedDateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssignmentRecord values(Integer value1, String value2, String value3, Integer value4, Integer value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AssignmentRecord
     */
    public AssignmentRecord() {
        super(Assignment.ASSIGNMENT);
    }

    /**
     * Create a detached, initialised AssignmentRecord
     */
    public AssignmentRecord(Integer uniqueId, String name, String imageFilePath, Integer courseId, Integer studentId, String createdDateTime) {
        super(Assignment.ASSIGNMENT);

        set(0, uniqueId);
        set(1, name);
        set(2, imageFilePath);
        set(3, courseId);
        set(4, studentId);
        set(5, createdDateTime);
    }
}