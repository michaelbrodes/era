package era.server.web;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import era.server.common.PageRenderer;
import era.server.common.UnauthorizedException;
import era.server.communication.UploaderAuthentication;
import era.server.data.AdminDAO;
import era.server.data.AssignmentDAO;
import era.server.data.model.Admin;
import era.server.data.model.Assignment;
import era.server.data.model.CourseTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The spark controller for the admin related pages.
 */
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final PageRenderer renderer;
    private final AdminDAO adminDAO;
    private final AssignmentDAO assignmentDAO;

    public AdminController(PageRenderer renderer, AdminDAO adminDAO, AssignmentDAO assignmentDAO) {
        this.renderer = renderer;
        this.adminDAO = adminDAO;
        this.assignmentDAO = assignmentDAO;
    }

    /**
     * The admin's index page it will display all assignments in the system
     * divided up by course
     */
    public String viewAllAssignments(Request request, Response response) {
        try {
            UserContext userContext = UserContext.initialize(request, response, adminDAO);
            Optional<Admin> maybeAdmin = userContext.getStudentUsername()
                    .flatMap(adminDAO::fetchByUsername);
            if (maybeAdmin.isPresent()) {
                Map<String, List<Assignment>> assignmentsByCourse = assignmentDAO.fetchAllAssignmentsGroupedByCourse();
                Collection<CourseTable> courseTables = CourseTable.fromAssignmentGroupings(assignmentsByCourse);
                List<Map<String, Object>> courseTableViewModels = courseTables.stream()
                        .sorted(courseTableComparator())
                        .map(CourseTable::toViewModel)
                        .collect(Collectors.toList());

                String generatedPassword = request.session().attribute("password");
                Map<String, Object> viewModel;

                if (generatedPassword != null) {
                    viewModel = ImmutableMap.of("courses", courseTableViewModels, "password", generatedPassword);
                } else {
                    viewModel = ImmutableMap.of("courses", courseTableViewModels);
                }


                return renderer.render(viewModel, "admin.hbs");
            } else {
                throw Spark.halt(403);
            }
        } catch (UnauthorizedException e) {
            throw Spark.halt(403);
        }
    }

    private Comparator<CourseTable> courseTableComparator() {
        return (left, right) -> {
            if (right.getAssignmentsInCourse().isEmpty() && left.getAssignmentsInCourse().isEmpty()) {
                return 0;
            } else if (right.getAssignmentsInCourse().isEmpty() && !left.getAssignmentsInCourse().isEmpty()) {
                return 1;
            } else if (!right.getAssignmentsInCourse().isEmpty() && left.getAssignmentsInCourse().isEmpty()) {
                return -1;
            } else {
                // won't IndexOutOfBounds because a CourseTable will only have a AssignmentsTable if the assignment table has an assignment
                return right.getAssignmentsInCourse()
                        .get(0)
                        .getAssignmentsSubmitted()
                        .get(0)
                        .getCreatedDateTime()
                        .compareTo(left.getAssignmentsInCourse()
                                .get(0)
                                .getAssignmentsSubmitted()
                                .get(0)
                                .getCreatedDateTime()
                        );
            }
        };
    }

    /**
     * The {@link #viewAllAssignments(Request, Response)} page has a form that
     * allows the admin to create a new admin. This is where it posts to.
     */
    public String createNewAdmin(Request request, Response response) {
        request.session(true);

        String username = request.queryParams("username");
        String user = request.session().attribute("user");


        if (user == null || !adminDAO.fetchByUsername(user).isPresent()) {
            //Not an Admin
            throw Spark.halt(403);
        }

        boolean success = false;

        String password = UploaderAuthentication.generatePassword();
        String hashedPassword = "";

        if (!Strings.isNullOrEmpty(username)) {
            try {
                hashedPassword = UploaderAuthentication.generateStrongPasswordHash(password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            success = adminDAO.storeAsAdmin(username, hashedPassword);
        }

        if (success && request.session().attribute("user") != null) {
            request.session().attribute("password", password);
            response.redirect("/admin/" + request.session().attribute("user"));
        } else {
            throw Spark.halt(500, "Couldn't create Admin. See server log for details.");
        }

        return "";

    }

}
