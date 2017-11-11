/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq.tables;


import era.uploader.data.database.jooq.DefaultSchema;
import era.uploader.data.database.jooq.tables.records.AllAssignmentsRecord;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

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
public class AllAssignments extends TableImpl<AllAssignmentsRecord> {

    private static final long serialVersionUID = -1551897862;

    /**
     * The reference instance of <code>all_assignments</code>
     */
    public static final AllAssignments ALL_ASSIGNMENTS = new AllAssignments();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AllAssignmentsRecord> getRecordType() {
        return AllAssignmentsRecord.class;
    }

    /**
     * The column <code>all_assignments.Assignment</code>.
     */
    public final TableField<AllAssignmentsRecord, String> ASSIGNMENT = createField("Assignment", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<AllAssignmentsRecord, Object> STUDENT = createField("Student", org.jooq.impl.DefaultDataType.getDefaultDataType(""), this, "");

    /**
     * The column <code>all_assignments.800 Number</code>.
     */
    public final TableField<AllAssignmentsRecord, String> _800_NUMBER = createField("800 Number", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>all_assignments.Course</code>.
     */
    public final TableField<AllAssignmentsRecord, String> COURSE = createField("Course", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<AllAssignmentsRecord, Object> CHILD_COURSE_ID = createField("Child Course ID", org.jooq.impl.DefaultDataType.getDefaultDataType(""), this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<AllAssignmentsRecord, Object> SEMESTER = createField("Semester", org.jooq.impl.DefaultDataType.getDefaultDataType(""), this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled.
     */
    @java.lang.Deprecated
    public final TableField<AllAssignmentsRecord, Object> CREATED = createField("Created", org.jooq.impl.DefaultDataType.getDefaultDataType(""), this, "");

    /**
     * The column <code>all_assignments.File Location</code>.
     */
    public final TableField<AllAssignmentsRecord, String> FILE_LOCATION = createField("File Location", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>all_assignments</code> table reference
     */
    public AllAssignments() {
        this(DSL.name("all_assignments"), null);
    }

    /**
     * Create an aliased <code>all_assignments</code> table reference
     */
    public AllAssignments(String alias) {
        this(DSL.name(alias), ALL_ASSIGNMENTS);
    }

    /**
     * Create an aliased <code>all_assignments</code> table reference
     */
    public AllAssignments(Name alias) {
        this(alias, ALL_ASSIGNMENTS);
    }

    private AllAssignments(Name alias, Table<AllAssignmentsRecord> aliased) {
        this(alias, aliased, null);
    }

    private AllAssignments(Name alias, Table<AllAssignmentsRecord> aliased, Field<?>[] parameters) {
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
    public AllAssignments as(String alias) {
        return new AllAssignments(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllAssignments as(Name alias) {
        return new AllAssignments(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AllAssignments rename(String name) {
        return new AllAssignments(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AllAssignments rename(Name name) {
        return new AllAssignments(name, null);
    }
}
