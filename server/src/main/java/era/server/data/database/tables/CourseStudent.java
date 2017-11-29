/*
 * This file is generated by jOOQ.
*/
package era.server.data.database.tables;


import era.server.data.database.Era;
import era.server.data.database.Indexes;
import era.server.data.database.Keys;
import era.server.data.database.tables.records.CourseStudentRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class CourseStudent extends TableImpl<CourseStudentRecord> {

    private static final long serialVersionUID = -1389362464;

    /**
     * The reference instance of <code>era.course_student</code>
     */
    public static final CourseStudent COURSE_STUDENT = new CourseStudent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CourseStudentRecord> getRecordType() {
        return CourseStudentRecord.class;
    }

    /**
     * The column <code>era.course_student.course_id</code>.
     */
    public final TableField<CourseStudentRecord, Long> COURSE_ID = createField("course_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>era.course_student.student_id</code>.
     */
    public final TableField<CourseStudentRecord, Long> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>era.course_student</code> table reference
     */
    public CourseStudent() {
        this(DSL.name("course_student"), null);
    }

    /**
     * Create an aliased <code>era.course_student</code> table reference
     */
    public CourseStudent(String alias) {
        this(DSL.name(alias), COURSE_STUDENT);
    }

    /**
     * Create an aliased <code>era.course_student</code> table reference
     */
    public CourseStudent(Name alias) {
        this(alias, COURSE_STUDENT);
    }

    private CourseStudent(Name alias, Table<CourseStudentRecord> aliased) {
        this(alias, aliased, null);
    }

    private CourseStudent(Name alias, Table<CourseStudentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Era.ERA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.COURSE_STUDENT_COURSE_STUDENT_STUDENT_FK, Indexes.COURSE_STUDENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CourseStudentRecord> getPrimaryKey() {
        return Keys.KEY_COURSE_STUDENT_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CourseStudentRecord>> getKeys() {
        return Arrays.<UniqueKey<CourseStudentRecord>>asList(Keys.KEY_COURSE_STUDENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CourseStudentRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CourseStudentRecord, ?>>asList(Keys.COURSE_STUDENT_COURSE_FK, Keys.COURSE_STUDENT_STUDENT_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseStudent as(String alias) {
        return new CourseStudent(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseStudent as(Name alias) {
        return new CourseStudent(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CourseStudent rename(String name) {
        return new CourseStudent(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CourseStudent rename(Name name) {
        return new CourseStudent(name, null);
    }
}
