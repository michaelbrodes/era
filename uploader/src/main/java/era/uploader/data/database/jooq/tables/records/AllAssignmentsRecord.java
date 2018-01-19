/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables.records;


import era.uploader.data.database.jooq.tables.AllAssignments;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.TableRecordImpl;


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
public class AllAssignmentsRecord extends TableRecordImpl<AllAssignmentsRecord> implements Record8<String, Object, String, String, Object, Object, Object, String> {

    private static final long serialVersionUID = -1467660433;

    /**
     * Setter for <code>all_assignments.Assignment</code>.
     */
    public AllAssignmentsRecord setAssignment(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Assignment</code>.
     */
    public String getAssignment() {
        return (String) get(0);
    }

    /**
     * Setter for <code>all_assignments.Student</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public AllAssignmentsRecord setStudent(Object value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Student</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public Object getStudent() {
        return (Object) get(1);
    }

    /**
     * Setter for <code>all_assignments.Eight Hundred Number</code>.
     */
    public AllAssignmentsRecord setEightHundredNumber(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Eight Hundred Number</code>.
     */
    public String getEightHundredNumber() {
        return (String) get(2);
    }

    /**
     * Setter for <code>all_assignments.Course</code>.
     */
    public AllAssignmentsRecord setCourse(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Course</code>.
     */
    public String getCourse() {
        return (String) get(3);
    }

    /**
     * Setter for <code>all_assignments.Child Course ID</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public AllAssignmentsRecord setChildCourseId(Object value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Child Course ID</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public Object getChildCourseId() {
        return (Object) get(4);
    }

    /**
     * Setter for <code>all_assignments.Semester</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public AllAssignmentsRecord setSemester(Object value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Semester</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public Object getSemester() {
        return (Object) get(5);
    }

    /**
     * Setter for <code>all_assignments.Created</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public AllAssignmentsRecord setCreated(Object value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.Created</code>.
     */

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public Object getCreated() {
        return (Object) get(6);
    }

    /**
     * Setter for <code>all_assignments.File Location</code>.
     */
    public AllAssignmentsRecord setFileLocation(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>all_assignments.File Location</code>.
     */
    public String getFileLocation() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, Object, String, String, Object, Object, Object, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, Object, String, String, Object, Object, Object, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return AllAssignments.ALL_ASSIGNMENTS.ASSIGNMENT;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field2() {
        return AllAssignments.ALL_ASSIGNMENTS.STUDENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return AllAssignments.ALL_ASSIGNMENTS.EIGHT_HUNDRED_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return AllAssignments.ALL_ASSIGNMENTS.COURSE;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field5() {
        return AllAssignments.ALL_ASSIGNMENTS.CHILD_COURSE_ID;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field6() {
        return AllAssignments.ALL_ASSIGNMENTS.SEMESTER;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field7() {
        return AllAssignments.ALL_ASSIGNMENTS.CREATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return AllAssignments.ALL_ASSIGNMENTS.FILE_LOCATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getAssignment();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object component2() {
        return getStudent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getEightHundredNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getCourse();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object component5() {
        return getChildCourseId();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object component6() {
        return getSemester();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object component7() {
        return getCreated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getFileLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getAssignment();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object value2() {
        return getStudent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getEightHundredNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getCourse();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object value5() {
        return getChildCourseId();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object value6() {
        return getSemester();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public Object value7() {
        return getCreated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getFileLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignmentsRecord value1(String value) {
        setAssignment(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public AllAssignmentsRecord value2(Object value) {
        setStudent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignmentsRecord value3(String value) {
        setEightHundredNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignmentsRecord value4(String value) {
        setCourse(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public AllAssignmentsRecord value5(Object value) {
        setChildCourseId(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public AllAssignmentsRecord value6(Object value) {
        setSemester(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    @Override
    public AllAssignmentsRecord value7(Object value) {
        setCreated(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignmentsRecord value8(String value) {
        setFileLocation(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignmentsRecord values(String value1, Object value2, String value3, String value4, Object value5, Object value6, Object value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AllAssignmentsRecord
     */
    public AllAssignmentsRecord() {
        super(AllAssignments.ALL_ASSIGNMENTS);
    }

    /**
     * Create a detached, initialised AllAssignmentsRecord
     */
    public AllAssignmentsRecord(String assignment, Object student, String eightHundredNumber, String course, Object childCourseId, Object semester, Object created, String fileLocation) {
        super(AllAssignments.ALL_ASSIGNMENTS);

        set(0, assignment);
        set(1, student);
        set(2, eightHundredNumber);
        set(3, course);
        set(4, childCourseId);
        set(5, semester);
        set(6, created);
        set(7, fileLocation);
    }
}
