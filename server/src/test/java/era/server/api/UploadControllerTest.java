package era.server.api;

import com.google.common.collect.Sets;
import era.server.common.PageRenderer;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.model.Course;
import era.server.data.model.Semester;
import era.server.data.model.Student;
import era.server.data.model.Term;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import spark.Request;
import spark.Response;

import java.time.Year;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadControllerTest {

    @Mock
    private AssignmentDAO assignmentDAO;
    @Mock
    private CourseDAO courseDAO;
    @Mock
    private StudentDAO studentDAO;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {

    }

    /**
     * Tests that the typical case (uploading a list of courses encoded as
     * JSON) works.
     */
    @Test
    public void uploadCourses_typical() throws Exception {
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        Course actualCourse = Course.builder()
                .withName("another course")
                .withStudents(Sets.newHashSet(
                        Student.builder()
                            .withEmail("ppoovey@siue.edu")
                            .create("ppoovey")
                ))
                .withSemester(new Semester(Term.SPRING, Year.of(2014)))
                .create();
        // look at era.server.communication.CourseJSONTypeAdapter for how I came up with this JSON
        // notice the lone uuid. We do not send UUIDs over from the uploader side
        String payload = "[\n" +
                "  {\n" +
                "    \"uuid\": \"1234\",\n" +
                "    \"name\": \"course\",\n" +
                "    \"studentsEnrolled\": [\n" +
                "      {\n" +
                "        \"userName\": \"sarcher\", \n" +
                "        \"email\": \"sarcher@siue.edu\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"userName\": \"lkane\", \n" +
                "        \"email\": \"lkane@siue.edu\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"semester\": {\n" +
                "      \"term\": \"SPRING\", \n" +
                "      \"year\": {\n" +
                "        \"year\": 2014\n" +
                "      }\n" +
                "    }\n" +
                "  }, \n" +
                "  {\n" +
                "    \"name\": \"another course\",\n" +
                "    \"studentsEnrolled\": [\n" +
                "      {\n" +
                "        \"userName\": \"ppoovey\", \n" +
                "        \"email\": \"ppoovey@siue.edu\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"semester\": {\n" +
                "      \"term\": \"SPRING\", \n" +
                "      \"year\": {\n" +
                "        \"year\": 2014\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "]";

        when(request.contentType()).thenReturn("application/json");
        when(request.body()).thenReturn(payload);

        UploadController controller = new UploadController(courseDAO, assignmentDAO, studentDAO);
        String responseBody = controller.uploadCourses(request, response);

        assertEquals("", responseBody);
        verify(response).status(201);
        verify(courseDAO).insert(actualCourse);
    }

    /**
     * Tests that the client can send an empty list of courses, and we won't
     * have a problem
     */
    @Test
    public void uploadCourses_noCourses() {
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        String payload = "[]";

        when(request.contentType()).thenReturn("application/json");
        when(request.body()).thenReturn(payload);

        UploadController controller = new UploadController(courseDAO, assignmentDAO, studentDAO);
        String responseBody = controller.uploadCourses(request, response);

        assertEquals("", responseBody);
        verify(response).status(201);
    }

    /**
     * This tests that if
     * {@link era.server.communication.CourseJSONTypeAdapter} can't parse the
     * body of the request, we will return a 400 to the client.
     */
    @Test
    public void uploadCourses_invalidJSON() {
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        when(request.contentType()).thenReturn("application/json");

        // removes the term of the semester on the first course
        String payload = "[\n" +
                "  {\n" +
                "    \"name\": \"course\",\n" +
                "    \"studentsEnrolled\": [\n" +
                "      {\n" +
                "        \"userName\": \"sarcher\",\n" +
                "        \"email\": \"sarcher@siue.edu\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"userName\": \"lkane\",\n" +
                "        \"email\": \"lkane@siue.edu\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"semester\": {\n" +
                "      \"year\": {\n" +
                "        \"year\": 2014\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"another course\",\n" +
                "    \"studentsEnrolled\": [\n" +
                "      {\n" +
                "        \"userName\": \"ppoovey\",\n" +
                "        \"email\": \"ppoovey@siue.edu\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"semester\": {\n" +
                "      \"term\": \"SPRING\",\n" +
                "    }\n" +
                "  }\n" +
                "]";
        when(request.body()).thenReturn(payload);

        UploadController uploadController = new UploadController(courseDAO, assignmentDAO, studentDAO);
        String resultBody = uploadController.uploadCourses(request, response);

        assertNotEquals("", resultBody);
        verify(response).status(400);
    }

    @Test
    public void uploadCourses_notJson() {

    }

}