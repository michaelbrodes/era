package era.server.web;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import era.server.common.PageRenderer;
import era.server.common.UnauthorizedException;
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
                        .map(CourseTable::toViewModel)
                        .collect(Collectors.toList());

                Map<String, Object> viewModel = ImmutableMap.of("courses", courseTableViewModels);
                return renderer.render(viewModel, "admin.hbs");
            } else {
                throw Spark.halt(403);
            }
        } catch (UnauthorizedException e) {
            throw Spark.halt(403);
        }
    }

    /**
     * The {@link #viewAllAssignments(Request, Response)} page has a form that
     * allows the admin to create a new admin. This is where it posts to.
     */
    public String createNewAdmin(Request request, Response response) {
        request.session(true);

        String username = request.queryParams("username");
        String user = request.session().attribute("user");


        Optional<Admin> maybeAdmin = adminDAO.fetchByUsername(user);

        if (user == null || !adminDAO.fetchByUsername(user).isPresent()) {
            //Not an Admin
            throw Spark.halt(403);
        }

        boolean success = false;

        if (!Strings.isNullOrEmpty(username)) {
            success = adminDAO.storeAsAdmin(username);
        }

        if (success && request.session().attribute("user") != null) {
            response.redirect("/admin/" + request.session().attribute("user"));
        } else {
            throw Spark.halt(500, "Couldn't create Admin. See server log for details.");
        }

        return "";

    }

}
