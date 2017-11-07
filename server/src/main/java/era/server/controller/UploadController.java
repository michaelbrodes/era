package era.server.controller;

import era.server.data.database.AssignmentDAO;
import era.server.data.database.CourseDAO;
import era.server.data.database.StudentDAO;
import era.server.data.model.Assignment;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class UploadController {
    private final CourseDAO courseDAO;
    private final AssignmentDAO assignmentDAO;
    private final StudentDAO studentDAO;

    public UploadController(
            final CourseDAO courseDAO,
            final AssignmentDAO assignmentDAO,
            final StudentDAO studentDAO
    ) {
        this.courseDAO = courseDAO;
        this.assignmentDAO = assignmentDAO;
        this.studentDAO = studentDAO;
    }


    public void handleRequest(Request request, Response response) {
        System.out.println("In request");

        long courseIdLong = 0;
        long studentIdLong = 0;
        int assignmentId = 0;
        String contentType = request.contentType();
        String courseId = request.params(":courseId");

        if (courseId == null) {
            response.status(400);
            return;
        }// if :coursId is null malformed request, return 400

        String assignmentIdHeader = request.headers("X-Assignment-Id");
        String assignmentNameHeader = request.headers("X-Assignment-Name");
        String studentIdHeader = request.headers("X-Student-Id");
        String assignmentFileNameHeader = request.headers("X-Assignment-File-Name");

        try {
            if(!contentType.contains("multipart/form-data")){
                response.status(415);
                return;
            }

            courseIdLong = Long.valueOf(courseId);
            studentIdLong = Long.valueOf(studentIdHeader);
            assignmentId = Integer.valueOf(assignmentIdHeader);
        } catch (NumberFormatException e) {
            e.getMessage();
            response.status(400);
            return;
        }

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("temp"));
        try (InputStream is = request.raw().getPart("pdf").getInputStream()) {
            Assignment assignment = new Assignment(assignmentFileNameHeader, assignmentNameHeader,
                    studentDAO.read(studentIdLong), assignmentId, courseDAO.read(courseIdLong));
            if(storeInFileSystem(is, assignment)){
                assignmentDAO.storeAssignment(assignment);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            response.status(500);
            return;
        }
        response.status(201);
        return;
    }

    public boolean storeInFileSystem(InputStream inputStream, Assignment assignment) {

            Path path = Paths.get(assignment.getImageFilePath());

            try (OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(path, CREATE, WRITE))) {
                byte[] buffer = new byte[1024]; // Adjust if you want
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                }
            }

            catch (IOException e) {
                System.err.println(e);
                return false;
            }
            return true;
        }
    }
