package era.uploader.data.database;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.CourseDAO;
import era.uploader.data.database.jooq.tables.records.AllAssignmentsRecord;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Term;
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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static era.uploader.data.database.jooq.Tables.ASSIGNMENT;
import static era.uploader.data.database.jooq.Tables.COURSE;
import static era.uploader.data.database.jooq.Tables.COURSE_STUDENT;
import static era.uploader.data.database.jooq.Tables.SEMESTER;
import static era.uploader.data.database.jooq.Tables.STUDENT;

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
 * This class is ignored because we shouldn't run it each time we compile,
 * instead it should be ran <strong>only</strong> when we are testing the
 * behavior of the database directly
 */
@Ignore
public class AssignmentDAOImplIT {
    private static Connection dbConnection;
    private static final List<String> TABLE_LIST = Arrays.asList(
            COURSE_STUDENT.getName(),
            COURSE.getName(),
            STUDENT.getName(),
            ASSIGNMENT.getName(),
            SEMESTER.getName()

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
    public void getAllAssignments_emptySet() throws Exception {
        AssignmentDAO assignmentDAO = AssignmentDAOImpl.instance();
        Collection<AllAssignmentsRecord> records = assignmentDAO.getAllAssignments();

        Assert.assertNotNull(records);
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void getAllAssignments_multipleRows() throws Exception {
        AssignmentDAO assignmentDAO = AssignmentDAOImpl.instance();
        Student student1 = Student.builder()
                .withFirstName("Mallory")
                .withLastName("Archer")
                .withSchoolId("800111222")
                .createUnique("marcher");

        Student student2 = Student.builder()
                .withFirstName("Fun person")
                .withLastName("Archer")
                .withSchoolId("800111999")
                .createUnique("farcher");

        Student student3 = Student.builder()
                .withFirstName("I am a bird")
                .withLastName("Archer")
                .withSchoolId("800111555")
                .createUnique("iarcher");

        Student student4 = Student.builder()
                .withFirstName("Sterling")
                .withLastName("Archer")
                .withSchoolId("800111444")
                .createUnique("sarcher");

        Course course1 = Course.builder()
                .withName("cells and stuff")
                .withSemester(Semester.of(Term.FALL, Year.now()))
                .withStudents(ImmutableSet.of(
                        student1,
                        student2,
                        student3,
                        student4
                ))
                .createUnique("BIO", "234", "002");

        Course course2 = Course.builder()
                .withName("cells and stuff")
                .withStudents(ImmutableSet.of(
                        student4
                ))
                .withSemester(Semester.of(Term.FALL, Year.now()))
                .createUnique("BIO", "234", "003");

        Course course3 = Course.builder()
                .withName("cells and stuff")
                .withSemester(Semester.of(Term.FALL, Year.now()))
                .withStudents(ImmutableSet.of(
                        student1,
                        student2,
                        student3
                ))
                .createUnique("BIO", "234", "001");

        Set<Course> courses = ImmutableSet.of(
                course1,
                course2,
                course3
        );
        Assignment assignment1 = Assignment.builder()
                .withCourse(course1)
                .withStudent(student1)
                .withImageFilePath("i am a unique file path")
                .createUnique("do cells exist?");
        Assignment assignment2 = Assignment.builder()
                .withCourse(course1)
                .withStudent(student2)
                .withImageFilePath("i am a unique file path")
                .createUnique("do cells exist?");
        Assignment assignment3 = Assignment.builder()
                .withCourse(course1)
                .withStudent(student3)
                .withImageFilePath("i am a unique file path")
                .createUnique("do cells exist?");
        Assignment assignment4 = Assignment.builder()
                .withCourse(course1)
                .withStudent(student4)
                .withImageFilePath("i am a unique file path")
                .createUnique("do cells exist?");
        Assignment assignment5 = Assignment.builder()
                .withCourse(course2)
                .withStudent(student4)
                .withImageFilePath("i am a unique file path")
                .createUnique("I don't know");

        Set<Assignment> assignments = ImmutableSet.of(
                assignment1,
                assignment2,
                assignment3,
                assignment4,
                assignment5
        );

        CourseDAO courseDAO = CourseDAOImpl.instance();
        for (Course course : courses) {
            courseDAO.insert(course);
        }

        for (Assignment assignment : assignments) {
            assignmentDAO.insert(assignment);
        }

        Collection<AllAssignmentsRecord> records = assignmentDAO.getAllAssignments();

        Assert.assertEquals(5, records.size());
        Assert.assertEquals("do cells exist?", Iterables.get(records, 1).getAssignment().trim());
    }

    @Test
    public void convertToModel() throws Exception {
    }

    @Test
    public void convertToRecord() throws Exception {
    }

}