package era.uploader.data.database;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import era.uploader.data.CourseDAO;
import era.uploader.data.DAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.STUDENT;
import static era.uploader.data.database.jooq.tables.Semester.SEMESTER;

/**
 * An <em>integration test</em> to insure that the CRUD functionality of the
 * DAO is working correctly with an actual database. It connects to
 * uploader.db, deletes all it's contents, and then runs the unit tests in this
 * class.
 *
 * <strong>NEVER</strong> run this test on a production database. That means
 * <strong>DR JONES DON'T RUN THIS TEST</strong> unless the database you are
 * using doesn't have useful information
 *
 * TODO: make sure to let Dr. Jones know not to run this test or just don't ship this test at all
 *
 * This class is ignored because we shouldn't run it each time we compile,
 * instead it should be ran <strong>only</strong> when we are testing the
 * behavior of the database directly
 */
@Ignore
public class CourseDAOImplIT {
    private static Connection dbConnection;
    private static final List<String> TABLE_LIST = Arrays.asList(
            SEMESTER.getName(),
            STUDENT.getName(),
            COURSE.getName(),
            COURSE_STUDENT.getName()
    );

    @BeforeClass
    public static void setUpClass() throws Exception {
        dbConnection = IntegrationTests.clearAndConnect(TABLE_LIST);
    }

    @Before
    public void setUp() {
        IntegrationTests.clearTables(dbConnection, TABLE_LIST);
    }

    @After
    public void tearDown() {
        // in case we error out we don't want our test state in the database.
        IntegrationTests.clearTables(dbConnection, TABLE_LIST);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        dbConnection.close();
    }

    @Test
    public void insert_SingleRow() throws Exception {
        CourseDAO courseDAO = CourseDAOImpl.instance();
        String courseName = "Intro to chemistry";
        String sectionNumber = "001";
        String archerName = "Sterling";
        String lanaName = "Lana";

        Course testCourse = Course.builder()
                .withName(courseName)
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .withStudents(ImmutableSet.of(
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
                .withAssignments(ImmutableSet.of())
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


    public void insert_AnotherRow() throws Exception {
        CourseDAO courseDAO = CourseDAOImpl.instance();
        String courseName = "Second Intro to chemistry";
        String sectionNumber = "002";
        String archerName = "Student 1";
        String lanaName = "Student 2";

        Course testCourse = Course.builder()
                .withName(courseName)
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .withStudents(ImmutableSet.of(
                        Student.builder()
                                .withFirstName(archerName)
                                .withLastName("Archer")
                                .withSchoolId("800999990")
                                .create("student1"),
                        Student.builder()
                                .withFirstName(lanaName)
                                .withLastName("Kane")
                                .withSchoolId("800888880")
                                .create("student2")
                ))
                .withAssignments(ImmutableSet.of())
                .create("CHEM", "120", sectionNumber);

        courseDAO.insert(testCourse);
    }

    @Test
    public void insertCourseAndStudents() throws Exception {
        CourseDAO courseDAO = CourseDAOImpl.instance();
        Course testCourse = Course.builder()
                .withName("Intro to Chemistry")
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .withAssignments(ImmutableSet.of())
                .create("CHEM", "120", "001");

        Course testCourse2 = Course.builder()
                .withName("Second Intro to Chemistry")
                .withSemester(Semester.of(Semester.Term.FALL, Year.now()))
                .withAssignments(ImmutableSet.of())
                .create("CHEM", "150", "002");

        Student s1 = Student.builder()
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withSchoolId("800999999")
                .create("sarcher");
        Student s2 = Student.builder()
                .withFirstName("Lana")
                .withLastName("Kane")
                .withSchoolId("800888888")
                .create("lkane");

        ImmutableMultimap<Course, Student> mmap = ImmutableMultimap.of(
                testCourse, s1,
                testCourse, s2,
                testCourse2, s1
        );
        courseDAO.insertCourseAndStudents(mmap, Semester.of(Semester.Term.FALL, Year.now()));

        List<Course> courses = courseDAO.getAllCourses();
        Assert.assertEquals(2, courses.size());
        Assert.assertEquals(courses.get(0).getName(), "Intro to Chemistry");
        Assert.assertEquals(courses.get(1).getName(), "Second Intro to Chemistry");

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
        CourseDAO courseDAO = CourseDAOImpl.instance();
        insert_SingleRow();
        insert_AnotherRow();
        List<Course> courses = courseDAO.getAllCourses();

        for (Course course : courses
             ) {
            Assert.assertTrue(course.getName().equals("Second Intro to chemistry") ||
                    course.getName().equals("Intro to chemistry"));
        }
    }

    @Test
    public void convertToModel() throws Exception {
    }

    @Test
    public void convertToRecord() throws Exception {
    }

}