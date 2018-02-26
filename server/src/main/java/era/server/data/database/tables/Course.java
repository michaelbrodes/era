/*
 * This file is generated by jOOQ.
*/
package era.server.data.database.tables;


import era.server.data.database.Dev;
import era.server.data.database.Indexes;
import era.server.data.database.Keys;
import era.server.data.database.tables.records.CourseRecord;

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
public class Course extends TableImpl<CourseRecord> {

    private static final long serialVersionUID = 529102738;

    /**
     * The reference instance of <code>dev.course</code>
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
     * The column <code>dev.course.uuid</code>.
     */
    public final TableField<CourseRecord, String> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>dev.course.name</code>.
     */
    public final TableField<CourseRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.field("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>dev.course.semester_id</code>.
     */
    public final TableField<CourseRecord, String> SEMESTER_ID = createField("semester_id", org.jooq.impl.SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * Create a <code>dev.course</code> table reference
     */
    public Course() {
        this(DSL.name("course"), null);
    }

    /**
     * Create an aliased <code>dev.course</code> table reference
     */
    public Course(String alias) {
        this(DSL.name(alias), COURSE);
    }

    /**
     * Create an aliased <code>dev.course</code> table reference
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
        return Dev.DEV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.COURSE_COURSE_SEMESTER_FK, Indexes.COURSE_COURSE_SEMESTER_NAME_UK, Indexes.COURSE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CourseRecord> getPrimaryKey() {
        return Keys.KEY_COURSE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CourseRecord>> getKeys() {
        return Arrays.<UniqueKey<CourseRecord>>asList(Keys.KEY_COURSE_PRIMARY, Keys.KEY_COURSE_COURSE_SEMESTER_NAME_UK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CourseRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CourseRecord, ?>>asList(Keys.COURSE_SEMESTER_FK);
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
