/*
 * This file is generated by jOOQ.
*/
package era.server.data.database.tables;


import era.server.data.database.Dev;
import era.server.data.database.Indexes;
import era.server.data.database.Keys;
import era.server.data.database.tables.records.SemesterRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class Semester extends TableImpl<SemesterRecord> {

    private static final long serialVersionUID = 1917522165;

    /**
     * The reference instance of <code>dev.semester</code>
     */
    public static final Semester SEMESTER = new Semester();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SemesterRecord> getRecordType() {
        return SemesterRecord.class;
    }

    /**
     * The column <code>dev.semester.uuid</code>.
     */
    public final TableField<SemesterRecord, String> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARCHAR(36).nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid()", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>dev.semester.term</code>.
     */
    public final TableField<SemesterRecord, String> TERM = createField("term", org.jooq.impl.SQLDataType.VARCHAR(6).nullable(false), this, "");

    /**
     * The column <code>dev.semester.year</code>.
     */
    public final TableField<SemesterRecord, Integer> YEAR = createField("year", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>dev.semester</code> table reference
     */
    public Semester() {
        this(DSL.name("semester"), null);
    }

    /**
     * Create an aliased <code>dev.semester</code> table reference
     */
    public Semester(String alias) {
        this(DSL.name(alias), SEMESTER);
    }

    /**
     * Create an aliased <code>dev.semester</code> table reference
     */
    public Semester(Name alias) {
        this(alias, SEMESTER);
    }

    private Semester(Name alias, Table<SemesterRecord> aliased) {
        this(alias, aliased, null);
    }

    private Semester(Name alias, Table<SemesterRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SEMESTER_PRIMARY, Indexes.SEMESTER_SEMESTER_TERM_YEAR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SemesterRecord> getPrimaryKey() {
        return Keys.KEY_SEMESTER_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SemesterRecord>> getKeys() {
        return Arrays.<UniqueKey<SemesterRecord>>asList(Keys.KEY_SEMESTER_PRIMARY, Keys.KEY_SEMESTER_SEMESTER_TERM_YEAR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Semester as(String alias) {
        return new Semester(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Semester as(Name alias) {
        return new Semester(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Semester rename(String name) {
        return new Semester(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Semester rename(Name name) {
        return new Semester(name, null);
    }
}
