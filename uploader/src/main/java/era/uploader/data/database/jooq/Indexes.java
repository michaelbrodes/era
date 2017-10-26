/*
 * This file is generated by jOOQ.
*/
package era.uploader.data.database.jooq;


import era.uploader.data.database.jooq.tables.CourseStudent;
import era.uploader.data.database.jooq.tables.Page;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling indexes of tables of the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SQLITE_AUTOINDEX_COURSE_STUDENT_1 = Indexes0.SQLITE_AUTOINDEX_COURSE_STUDENT_1;
    public static final Index SQLITE_AUTOINDEX_PAGE_1 = Indexes0.SQLITE_AUTOINDEX_PAGE_1;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 extends AbstractKeys {
        public static Index SQLITE_AUTOINDEX_COURSE_STUDENT_1 = createIndex("sqlite_autoindex_course_student_1", CourseStudent.COURSE_STUDENT, new OrderField[] { CourseStudent.COURSE_STUDENT.COURSE_ID, CourseStudent.COURSE_STUDENT.STUDENT_ID }, true);
        public static Index SQLITE_AUTOINDEX_PAGE_1 = createIndex("sqlite_autoindex_page_1", Page.PAGE, new OrderField[] { Page.PAGE.UUID }, true);
    }
}
