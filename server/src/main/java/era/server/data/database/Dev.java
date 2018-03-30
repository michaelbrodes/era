/*
 * This file is generated by jOOQ.
*/
package era.server.data.database;


import era.server.data.database.tables.Admin;
import era.server.data.database.tables.Assignment;
import era.server.data.database.tables.Course;
import era.server.data.database.tables.CourseStudent;
import era.server.data.database.tables.SchemaVersion;
import era.server.data.database.tables.Semester;
import era.server.data.database.tables.Student;

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
public class Dev extends SchemaImpl {

    private static final long serialVersionUID = 1199421274;

    /**
     * The reference instance of <code>dev</code>
     */
    public static final Dev DEV = new Dev();

    /**
     * The table <code>dev.admin</code>.
     */
    public final Admin ADMIN = era.server.data.database.tables.Admin.ADMIN;

    /**
     * The table <code>dev.assignment</code>.
     */
    public final Assignment ASSIGNMENT = era.server.data.database.tables.Assignment.ASSIGNMENT;

    /**
     * The table <code>dev.course</code>.
     */
    public final Course COURSE = era.server.data.database.tables.Course.COURSE;

    /**
     * The table <code>dev.course_student</code>.
     */
    public final CourseStudent COURSE_STUDENT = era.server.data.database.tables.CourseStudent.COURSE_STUDENT;

    /**
     * The table <code>dev.schema_version</code>.
     */
    public final SchemaVersion SCHEMA_VERSION = era.server.data.database.tables.SchemaVersion.SCHEMA_VERSION;

    /**
     * The table <code>dev.semester</code>.
     */
    public final Semester SEMESTER = era.server.data.database.tables.Semester.SEMESTER;

    /**
     * The table <code>dev.student</code>.
     */
    public final Student STUDENT = era.server.data.database.tables.Student.STUDENT;

    /**
     * No further instances allowed
     */
    private Dev() {
        super("dev", null);
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
            Admin.ADMIN,
            Assignment.ASSIGNMENT,
            Course.COURSE,
            CourseStudent.COURSE_STUDENT,
            SchemaVersion.SCHEMA_VERSION,
            Semester.SEMESTER,
            Student.STUDENT);
    }
}
