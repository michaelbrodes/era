package era.server.web;

import com.google.common.collect.ImmutableMap;
import era.server.common.PageRenderer;
import era.server.common.UnauthorizedException;
import era.server.data.AdminDAO;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.model.Admin;
import era.server.data.model.Assignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AssignmentViewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentViewController.class);
    private final AssignmentDAO assignmentDAO;
    private final CourseDAO courseDAO;
    private final PageRenderer renderer;
    private final AdminDAO adminDAO;

    public AssignmentViewController(PageRenderer renderer, AssignmentDAO assignmentDAO, CourseDAO courseDAO, AdminDAO adminDAO) {
        this.renderer = renderer;
        this.assignmentDAO = assignmentDAO;
        this.courseDAO = courseDAO;
        this.adminDAO = adminDAO;
    }

    public String assignmentList(Request request, Response response) {
        try {
            UserContext context = UserContext.initialize(request, response, adminDAO);

            Optional<String> studentUsername = context.getStudentUsername();
            Optional<Admin> maybeAdmin = studentUsername.flatMap(name -> adminDAO.fetchByUsername(name));
            List<Map<String, Object>> assignments = studentUsername
                    .map(assignmentDAO::fetchAllByStudent)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(Assignment::toViewModel)
                    .collect(Collectors.toList());
            Map<String, Object> viewModel;
            if (studentUsername.isPresent()){
                // view models require the second template parameter to be of type Object
                viewModel = ImmutableMap.of("assignmentList", assignments, "isAdmin", maybeAdmin.isPresent(), "username", studentUsername.get());
            } else {
                viewModel = ImmutableMap.of("assignmentList", assignments, "isAdmin", false);
            }


            return renderer.render(viewModel, "assignment-view.hbs");
            
        } catch (UnauthorizedException e) {
            throw Spark.halt(403);
        }
    }


    /**
     * Given a URL param {@code :assignmentId} read that assignment's filename
     * from the database, then read the file from the filesystem and display it
     * to the user.
     *
     * @return We return null because we have to return a value even though the
     * response is already set when we copy the PDF to {@link Response#raw()}
     */
    public Object assignment(Request request, Response response) {
        try {
            UserContext context = UserContext.initialize(request, response, adminDAO);
            Optional<Assignment> assignment = context
                    .getAssignmentId()
                    .flatMap(assignmentDAO::fetch);

            if (assignment.isPresent()) {
                try (OutputStream out = response.raw().getOutputStream()) {
                    response.raw().setContentType("application/pdf");
                    Path pdf = Paths.get(assignment.get().getImageFilePath());
                    Files.copy(pdf, out);
                } catch (IOException e) {
                    LOGGER.error("Problem reading PDF from filesystem.", e);
                    throw Spark.halt(404);
                }
            } else {
                throw Spark.halt(404);
            }

            return null;
        } catch (UnauthorizedException e) {
            throw Spark.halt(403);
        }


    }

}
