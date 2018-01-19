/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables;


import era.uploader.data.database.jooq.DefaultSchema;
import era.uploader.data.database.jooq.Keys;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;

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
public class Assignment extends TableImpl<AssignmentRecord> {

    private static final long serialVersionUID = 1947219123;

    /**
     * The reference instance of <code>assignment</code>
     */
    public static final Assignment ASSIGNMENT = new Assignment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AssignmentRecord> getRecordType() {
        return AssignmentRecord.class;
    }

    /**
     * The column <code>assignment.unique_id</code>.
     */
    public final TableField<AssignmentRecord, Integer> UNIQUE_ID = createField("unique_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>assignment.name</code>.
     */
    public final TableField<AssignmentRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>assignment.image_file_path</code>.
     */
    public final TableField<AssignmentRecord, String> IMAGE_FILE_PATH = createField("image_file_path", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>assignment.course_id</code>.
     */
    public final TableField<AssignmentRecord, Integer> COURSE_ID = createField("course_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>assignment.student_id</code>.
     */
    public final TableField<AssignmentRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>assignment.created_date_time</code>.
     */
    public final TableField<AssignmentRecord, String> CREATED_DATE_TIME = createField("created_date_time", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>assignment</code> table reference
     */
    public Assignment() {
        this(DSL.name("assignment"), null);
    }

    /**
     * Create an aliased <code>assignment</code> table reference
     */
    public Assignment(String alias) {
        this(DSL.name(alias), ASSIGNMENT);
    }

    /**
     * Create an aliased <code>assignment</code> table reference
     */
    public Assignment(Name alias) {
        this(alias, ASSIGNMENT);
    }

    private Assignment(Name alias, Table<AssignmentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Assignment(Name alias, Table<AssignmentRecord> aliased, Field<?>[] parameters) {
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
    public Identity<AssignmentRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ASSIGNMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AssignmentRecord> getPrimaryKey() {
        return Keys.PK_ASSIGNMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AssignmentRecord>> getKeys() {
        return Arrays.<UniqueKey<AssignmentRecord>>asList(Keys.PK_ASSIGNMENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<AssignmentRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AssignmentRecord, ?>>asList(Keys.FK_ASSIGNMENT_COURSE_1, Keys.FK_ASSIGNMENT_STUDENT_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assignment as(String alias) {
        return new Assignment(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assignment as(Name alias) {
        return new Assignment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Assignment rename(String name) {
        return new Assignment(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Assignment rename(Name name) {
        return new Assignment(name, null);
    }
}
