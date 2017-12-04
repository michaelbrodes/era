package era.server.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import era.server.common.APIMessage;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.model.Assignment;
import era.server.data.model.Course;
import era.server.data.model.Student;
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
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class UploadController {
    private final CourseDAO courseDAO;
    private final AssignmentDAO assignmentDAO;
    private final StudentDAO studentDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);


    public UploadController(
            final CourseDAO courseDAO,
            final AssignmentDAO assignmentDAO,
            final StudentDAO studentDAO
    ) {
        this.courseDAO = courseDAO;
        this.assignmentDAO = assignmentDAO;
        this.studentDAO = studentDAO;
    }


    public String uploadAssignment(Request request, Response response) {
        long courseIdLong = 0;
        long studentIdLong = 0;
        int assignmentId = 0;
        String contentType = request.contentType();
        String courseId = request.params(":courseId");

        if (courseId == null) {
            response.status(400);
            return "";
        }// if :coursId is null malformed request, return 400

        String assignmentIdHeader = request.headers("X-Assignment-Id");
        String assignmentNameHeader = request.headers("X-Assignment-Name");
        String studentIdHeader = request.headers("X-Student-Id");
        String assignmentFileNameHeader = request.headers("X-Assignment-File-Name");

        try {
            if (!contentType.contains("multipart/form-data")) {
                response.status(415);
                return "";
            }

            courseIdLong = Long.valueOf(courseId);
            studentIdLong = Long.valueOf(studentIdHeader);
            assignmentId = Integer.valueOf(assignmentIdHeader);
        } catch (NumberFormatException e) {
            e.getMessage();
            response.status(400);
            return "";
        }

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("temp"));
        try (InputStream is = request.raw().getPart("pdf").getInputStream()) {
            Course course = courseDAO.read(courseIdLong);
            Student student = studentDAO.read(studentIdLong);
            if (course != null && student != null) {
                Assignment assignment = new Assignment(
                        assignmentId,
                        assignmentFileNameHeader,
                        assignmentNameHeader,
                        course,
                        studentDAO.read(studentIdLong),
                        LocalDateTime.now()
                );
                if (storeInFileSystem(is, assignment)) {
                    assignmentDAO.storeAssignment(assignment);
                }
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            response.status(500);
            return "";
        }
        response.status(201);
        return "";
    }

    /**
     * Stores a list of courses into the database. Each course object can have
     * many assignments and students.
     */
    public String uploadCourses(Request request, Response response) {
        // json is the only input type we have
        if (!request.contentType().contains("application/json")) {
            return APIMessage.error(
                    request,
                    response,
                    415,
                    "wrong content type, we require application/json"
            );
        }

        Gson jsonParser = new Gson();
        Type courseList = new TypeToken<List<Course>>() {
        }.getType();
        List<Course> courses = jsonParser.fromJson(request.body(), courseList);

        if (courses == null) {
            return APIMessage.error(
                    request,
                    response,
                    400,
                    "We cannot insert a null collection of courses"
            );
        }

        // we cannot insert a null course or a course that has either a null
        // name, uniqueId, or semester.
        for (Course course : courses) {
            if (course != null
                    && course.getName() != null
                    && course.getSemester() != null
                    && course.getUniqueId() != 0) {
                try {
                    courseDAO.insert(course);
                } catch (SQLException e) {
                    String errorMessage = "Error inserting course "
                            + course.toString() +
                            " into the database "
                            + e.getMessage();
                    LOGGER.error(errorMessage);
                    return APIMessage.error(request, response, 500, errorMessage);
                }
            } else if (course == null) {
                return APIMessage.error(request, response, 400, "Cannot create null course");
            } else {
                return APIMessage.error(request, response, 400, "Course "
                        + course.toString()
                        + " has null information that cannot be inserted");
            }
        }

        return APIMessage.created(response);
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
            System.err.println(e);
            return false;
        }
        return true;
    }
}
