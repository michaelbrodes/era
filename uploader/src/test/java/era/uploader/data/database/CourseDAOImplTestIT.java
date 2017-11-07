package era.uploader.data.database;

import com.google.common.collect.ImmutableSet;
import era.uploader.data.CourseDAO;
import era.uploader.data.DAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;

import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;

/**
 * An <em>integration test</em> to insure that the CRUD functionality of the
 * DAO is working correctly with an actual database. It looks for a sample
 * database in the test resources directory, deletes all it's contents, and
 * then runs the unit tests in this class.
 *
 * This class is ignored because we shouldn't run it each time we compile,
 * instead it should be ran <strong>only</strong> when we are testing the
 * behavior of the database directly
 */
@Ignore
public class CourseDAOImplTestIT {
    private static Connection dbConnection;
    @BeforeClass
    public static void setUpClass() throws Exception {
        dbConnection = IntegrationTests.clearAndConnect(Arrays.asList(
                STUDENT.getName(),
                COURSE.getName(),
                COURSE_STUDENT.getName()
        ));
    }

    @Before
    public void setUp() {
        IntegrationTests.clearTables(dbConnection, Arrays.asList(
                STUDENT.getName(),
                COURSE.getName(),
                COURSE_STUDENT.getName()
        ));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        dbConnection.close();
    }

    @Test
    public void insert_SingleRow() throws Exception {
        CourseDAO courseDAO = new CourseDAOImpl();
        String courseName = "Intro to chemistry";
        String sectionNumber = "001";
        String archerName = "Sterling";
        String lanaName = "Lana";

        Course testCourse = Course.builder()
                .withName(courseName)
                .withSemester("FALL")
                .havingStudentsEnrolled(ImmutableSet.of(
                        Student.builder()
                            .withFirstName(archerName)
                            .withLastName("Archer")
                            .withSchoolId("800999999")
                            .create("sarcher"),
                        Student.builder()
                            .withFirstName(lanaName)
                            .withLastName("Kane")
                            .withSchoolId("800888888")
                            .create("lkane")
                ))
                .havingAssignments(ImmutableSet.of())
                .create("CHEM", "111", sectionNumber);

        courseDAO.insert(testCourse);

        try (DSLContext context = DSL.using(DAO.CONNECTION_STR)) {
            context.selectFrom(COURSE)
                    .fetch()
                    .forEach((record) -> {
                        Assert.assertEquals(1, record.getUniqueId().intValue());
                        Assert.assertEquals(courseName, record.getName());
                        Assert.assertEquals(sectionNumber, record.getSectionNumber());
                    });
            context.selectFrom(COURSE_STUDENT)
                    .fetch()
                    .forEach((record) -> {
                        Assert.assertEquals(1, record.getCourseId().intValue());
                        Assert.assertTrue(
                                record.getStudentId() == 1 || record.getStudentId() == 2
                        );
                    });
        }
        Assert.assertNotEquals(0, testCourse.getUniqueId());
    }

    @Test
    public void insertCourseAndStudents() throws Exception {
    }

    @Test
    public void read() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void getAllCourses() throws Exception {
    }

    @Test
    public void convertToModel() throws Exception {
    }

    @Test
    public void convertToRecord() throws Exception {
    }

}