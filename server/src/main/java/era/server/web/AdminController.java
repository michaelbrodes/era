package era.server.web;

import com.google.common.collect.ImmutableMap;
import era.server.common.PageRenderer;
import era.server.common.UnauthorizedException;
import era.server.data.AdminDAO;
import era.server.data.AssignmentDAO;
import era.server.data.Model;
import era.server.data.model.Admin;
import era.server.data.model.Assignment;
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
            UserContext userContext = UserContext.initialize(request, response);
            Optional<Admin> maybeAdmin = userContext.getStudentUsername()
                    .flatMap(adminDAO::fetchByUsername);
            if (maybeAdmin.isPresent()) {
                Map<String, Collection<Assignment>> assignmentsByCourse = assignmentDAO.fetchAllAssignmentsGroupedByCourse();
                List<ImmutableMap<String, Object>> courseTables = assignmentsByCourse
                        .entrySet()
                        .stream()
                        .map(entry -> ImmutableMap.of("course", entry.getKey(), "assignments", transformToViewModel(entry.getValue())))
                        .collect(Collectors.toList());

                Map<String, Object> viewModel = ImmutableMap.of("courses", courseTables);
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

        return "";
    }

    private List<Map<String, Object>> transformToViewModel(Collection<? extends Model> models) {
        return models.stream()
                .map(Model::toViewModel)
                .collect(Collectors.toList());
    }
}
