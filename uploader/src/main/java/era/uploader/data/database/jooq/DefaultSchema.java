/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq;


import era.uploader.data.database.jooq.tables.AllAssignments;
import era.uploader.data.database.jooq.tables.Assignment;
import era.uploader.data.database.jooq.tables.Course;
import era.uploader.data.database.jooq.tables.CourseStudent;
import era.uploader.data.database.jooq.tables.QrCodeMapping;
import era.uploader.data.database.jooq.tables.SchemaVersion;
import era.uploader.data.database.jooq.tables.Semester;
import era.uploader.data.database.jooq.tables.SqliteSequence;
import era.uploader.data.database.jooq.tables.Student;
import era.uploader.data.database.jooq.tables.Teacher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1788423674;

    /**
     * The reference instance of <code></code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>all_assignments</code>.
     */
    public final AllAssignments ALL_ASSIGNMENTS = era.uploader.data.database.jooq.tables.AllAssignments.ALL_ASSIGNMENTS;

    /**
     * The table <code>assignment</code>.
     */
    public final Assignment ASSIGNMENT = era.uploader.data.database.jooq.tables.Assignment.ASSIGNMENT;

    /**
     * The table <code>course</code>.
     */
    public final Course COURSE = era.uploader.data.database.jooq.tables.Course.COURSE;

    /**
     * The table <code>course_student</code>.
     */
    public final CourseStudent COURSE_STUDENT = era.uploader.data.database.jooq.tables.CourseStudent.COURSE_STUDENT;

    /**
     * The table <code>qr_code_mapping</code>.
     */
    public final QrCodeMapping QR_CODE_MAPPING = era.uploader.data.database.jooq.tables.QrCodeMapping.QR_CODE_MAPPING;

    /**
     * The table <code>schema_version</code>.
     */
    public final SchemaVersion SCHEMA_VERSION = era.uploader.data.database.jooq.tables.SchemaVersion.SCHEMA_VERSION;

    /**
     * The table <code>semester</code>.
     */
    public final Semester SEMESTER = era.uploader.data.database.jooq.tables.Semester.SEMESTER;

    /**
     * The table <code>sqlite_sequence</code>.
     */
    public final SqliteSequence SQLITE_SEQUENCE = era.uploader.data.database.jooq.tables.SqliteSequence.SQLITE_SEQUENCE;

    /**
     * The table <code>student</code>.
     */
    public final Student STUDENT = era.uploader.data.database.jooq.tables.Student.STUDENT;

    /**
     * The table <code>teacher</code>.
     */
    public final Teacher TEACHER = era.uploader.data.database.jooq.tables.Teacher.TEACHER;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            AllAssignments.ALL_ASSIGNMENTS,
            Assignment.ASSIGNMENT,
            Course.COURSE,
            CourseStudent.COURSE_STUDENT,
            QrCodeMapping.QR_CODE_MAPPING,
            SchemaVersion.SCHEMA_VERSION,
            Semester.SEMESTER,
            SqliteSequence.SQLITE_SEQUENCE,
            Student.STUDENT,
            Teacher.TEACHER);
    }
}
