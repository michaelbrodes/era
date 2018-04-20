package era.server.api;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import era.server.common.JSONUtil;
import era.server.communication.UploaderAuthentication;
import era.server.data.AdminDAO;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class UploadController {
    private final CourseDAO courseDAO;
    private final AssignmentDAO assignmentDAO;
    private final StudentDAO studentDAO;
    private final AdminDAO adminDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    private static final int TERM_INDEX = 0;
    private static final int YEAR_INDEX = 1;
    // below are HTTP status codes that this API returns as well as documentation on how to handle them
    /**
     * Success, we inserted the uploader side object into the database. Don't
     * worry about it.
     */
    private static final int CREATED = 201;
    /**
     * There was a bug on the server side. Look at the logs there should be an
     * exception in there.
     */
    private static final int INTERNAL_SERVER_ERROR = 500;
    /**
     * An course or student that was on the uploader side was not on the server
     * side. Notify the user that they should resubmit there roster csv with
     * online mode turned on.
     */
    private static final int CONFLICT = 409;
    /**
     * This is a specific bug on the uploader side. Either the file that it is
     * uploading isn't what we support or the client didn't properly set the
     * Content-Type HTTP header.
     */
    private static final int BAD_CONTENT = 415;
    /**
     * This is a general bug on the uploader side. This is a bug that should
     * never happen, so fail and log an exception on the Uploader side.
     */
    private static final int BAD_REQUEST = 400;


    UploadController(
            final CourseDAO courseDAO,
            final AssignmentDAO assignmentDAO,
            final StudentDAO studentDAO,
            final AdminDAO adminDAO
    ) {
        this.courseDAO = courseDAO;
        this.assignmentDAO = assignmentDAO;
        this.studentDAO = studentDAO;
        this.adminDAO = adminDAO;
    }

    /**
     * Uploads an Assignment PDF as multipart/form-data and stores it in the
     * file system.
     *
     * This endpoint is located at
     * /api/course/:courseName/semester/:semesterName/assignment. ":courseName"
     * and ":semesterName" are <em>request parameters</em> meaning that they
     * are placeholders for a particular value. So
     * /api/course/CHEM-121-001/semester/FALL-2015/assignment would be handled
     * by this endpoint with ":courseName" -> CHEM-121-001 and
     * ":semesterName" -> FALL-2015.
     *
     * Requests to this endpoint should have the custom HTTP headers
     * X-Assignment-Name, X-Student-Name, and X-Assignment-File-Name, these
     * refer to the assignment's name (e.g. Exam 1), the student's username
     * (e.g. myrjones for Dr. Myron Jones), and the assignment's filename
     * respectively.
     *
     * What is a custom HTTP header: https://www.keycdn.com/support/custom-http-headers/
     */
    public String uploadAssignmentPDF(Request request, Response response) {
        String contentType = request.contentType();
        // course.name uploader-side
        String courseName = null;
        String semesterName = null;
        try {
            courseName = URLDecoder.decode(request.params(":courseName"), "UTF-8");
            semesterName = URLDecoder.decode(request.params(":semesterName"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Cannot decode courseName or semesterName", e);
            return APIMessage.error(request, response, BAD_REQUEST, e.getMessage());
        }
        // semester.term semester.year uploader-side

        if (courseName == null || semesterName == null) {
            response.status(BAD_REQUEST);
            return "";
        }// if :courseId is null malformed request, return 400

        if (!contentType.contains("multipart/form-data")) {
            response.status(BAD_CONTENT);
            return "";
        }

        String[] termAndYear = semesterName.split("-");

        if (termAndYear.length != 2
                || !Term.contains(termAndYear[TERM_INDEX])
                || !canCastToInt(termAndYear[YEAR_INDEX])) {
            response.status(BAD_REQUEST);
            return "";
        }

        String assignmentNameHeader = request.headers("X-Assignment-Name");
        String studentNameHeader = request.headers("X-Student-Name");
        String assignmentFileNameHeader = request.headers("X-Assignment-File-Name");

        Term term = Term.valueOf(termAndYear[TERM_INDEX]);
        Year year = Year.of(Integer.parseInt(termAndYear[YEAR_INDEX]));
        Semester semester = new Semester(term, year);

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("temp"));
        try (InputStream is = request.raw().getPart("pdf").getInputStream()) {
            Course course = courseDAO.readByCourseNameAndSemester(courseName, semester).orElse(null);
            Student student = studentDAO.readByUsername(studentNameHeader).orElse(null);
            if (course != null && student != null) {
                Assignment assignment = Assignment.builder()
                        .withImageFilePath(assignmentFileNameHeader)
                        .withCourse(course)
                        .withStudent(student)
                        .withCourse_id(course.getUuid())
                        .withStudent_id(student.getUuid())
                        .withCreatedDateTime(LocalDateTime.now())
                        .create(assignmentNameHeader, UUIDGenerator.uuid());

                if (storeInFileSystem(is, assignment)) {
                    assignmentDAO.storeAssignment(assignment);
                }
            } else {
                LOGGER.warn(
                        "The student {} or the course {} {} for assignment {} doesn't exist in the server database, therefore we cannot insert",
                        studentNameHeader,
                        courseName,
                        semester,
                        assignmentNameHeader
                );
                response.status(CONFLICT);
                return "";
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            response.status(INTERNAL_SERVER_ERROR);
            return "";
        }

        response.status(CREATED);
        return "";
    }

    /**
     * Upload a single Assignment PDF as multipart/form-data (binary). This method
     * has custom headers meant for identifying students and courses.
     *
     * @deprecated use {@link #uploadAssignmentPDF(Request, Response)} instead
     * as it doesn't require the client to keep track of server ids.
     */
    @Deprecated
    public String uploadAssignment(Request request, Response response) {
        LOGGER.warn("Usage of deprecated uploadAssignment command.");
        String contentType = request.contentType();
        String courseId = request.params(":courseId");

        if (courseId == null) {
            response.status(400);
            return "";
        }// if :coursId is null malformed request, return 400

        if (!contentType.contains("multipart/form-data")) {
            response.status(415);
            return "";
        }

        String assignmentIdHeader = request.headers("X-Assignment-Id");
        String assignmentNameHeader = request.headers("X-Assignment-Name");
        String studentIdHeader = request.headers("X-Student-Id");
        String assignmentFileNameHeader = request.headers("X-Assignment-File-Name");

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("temp"));
        try (InputStream is = request.raw().getPart("pdf").getInputStream()) {
            Course course = courseDAO.read(courseId);
            Student student = studentDAO.read(studentIdHeader);
            if (course != null && student != null) {
                Assignment assignment = new Assignment(
                        assignmentIdHeader,
                        assignmentFileNameHeader,
                        assignmentNameHeader,
                        course,
                        student,
                        LocalDateTime.now()
                );
                if (storeInFileSystem(is, assignment)) {
                    assignmentDAO.storeAssignment(assignment);
                }
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            response.status(INTERNAL_SERVER_ERROR);
            return "";
        }

        response.status(CREATED);
        return "";
    }

    /**
     * Stores a list of courses into the database. Each course object can have
     * many students.
     */
    public String uploadCourses(Request request, Response response) throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException {
        // json is the only input type we have

        if (!request.contentType().contains("application/json")) {
            return APIMessage.error(
                    request,
                    response,
                    BAD_CONTENT,
                    "wrong content type, we require application/json"
            );
        }

        Gson jsonParser = JSONUtil.gson();
        Type courseList = new TypeToken<List<Course>>() {}.getType();
        List<Course> courses;
        try {
             courses = jsonParser.fromJson(request.body(), courseList);
        } catch(JsonSyntaxException e) {
            LOGGER.error("Exception when parsing course JSON \n{}", request.body());
            LOGGER.error("Detailed exception: ", e);
            return APIMessage.error(request, response, BAD_REQUEST, e.getMessage());
        }

        // we cannot insert a null course or a course that has either a null
        // name, uniqueId, or semester.
        for (Course course : courses) {
            try {
                courseDAO.insert(course);
            } catch (SQLException e) {
                String errorMessage = "Error inserting course "
                        + course.toString() +
                        " into the database "
                        + e.getMessage();
                LOGGER.error(errorMessage);
                return APIMessage.error(request, response, INTERNAL_SERVER_ERROR, errorMessage);
            }
        }

        return APIMessage.created(response);
    }

    public String checkCourseExistence(Request request, Response response) {
        if(request.queryParams().contains("courses[]")) {
            String[] courses = request.queryParamsValues("courses[]");
            Gson gson = JSONUtil.gson();
            Set<String> queryCourses = Sets.newHashSet(courses);
            Set<String> matchingCourses = courseDAO.findCoursesInDB(queryCourses);
            // take difference of two sets
            queryCourses.removeAll(matchingCourses);

            return gson.toJson(queryCourses);
        } else {
            return "[]";
        }
    }

    private boolean canCastToInt(String value) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    private boolean storeInFileSystem(InputStream inputStream, Assignment assignment) {
        Path path = Paths.get(assignment.getImageFilePath());

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, CREATE, WRITE))) {
            byte[] buffer = new byte[1024]; // Adjust if you want
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            LOGGER.error("Error storing assignment into the filesystem", e);
            return false;
        }
        return true;
    }
}
