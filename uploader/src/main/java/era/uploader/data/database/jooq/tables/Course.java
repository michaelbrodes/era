/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables;


import era.uploader.data.database.jooq.DefaultSchema;
import era.uploader.data.database.jooq.Keys;
import era.uploader.data.database.jooq.tables.records.CourseRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class Course extends TableImpl<CourseRecord> {

    private static final long serialVersionUID = -1936678712;

    /**
     * The reference instance of <code>course</code>
     */
    public static final Course COURSE = new Course();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CourseRecord> getRecordType() {
        return CourseRecord.class;
    }

    /**
     * The column <code>course.unique_id</code>.
     */
    public final TableField<CourseRecord, Integer> UNIQUE_ID = createField("unique_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>course.name</code>.
     */
    public final TableField<CourseRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>course.department</code>.
     */
    public final TableField<CourseRecord, String> DEPARTMENT = createField("department", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>course.course_number</code>.
     */
    public final TableField<CourseRecord, String> COURSE_NUMBER = createField("course_number", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>course.section_number</code>.
     */
    public final TableField<CourseRecord, String> SECTION_NUMBER = createField("section_number", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>course.semester_id</code>.
     */
    public final TableField<CourseRecord, Integer> SEMESTER_ID = createField("semester_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>course</code> table reference
     */
    public Course() {
        this(DSL.name("course"), null);
    }

    /**
     * Create an aliased <code>course</code> table reference
     */
    public Course(String alias) {
        this(DSL.name(alias), COURSE);
    }

    /**
     * Create an aliased <code>course</code> table reference
     */
    public Course(Name alias) {
        this(alias, COURSE);
    }

    private Course(Name alias, Table<CourseRecord> aliased) {
        this(alias, aliased, null);
    }

    private Course(Name alias, Table<CourseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CourseRecord, Integer> getIdentity() {
        return Keys.IDENTITY_COURSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CourseRecord> getPrimaryKey() {
        return Keys.PK_COURSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CourseRecord>> getKeys() {
        return Arrays.<UniqueKey<CourseRecord>>asList(Keys.PK_COURSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CourseRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CourseRecord, ?>>asList(Keys.FK_COURSE_SEMESTER_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course as(String alias) {
        return new Course(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course as(Name alias) {
        return new Course(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Course rename(String name) {
        return new Course(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Course rename(Name name) {
        return new Course(name, null);
    }
}
