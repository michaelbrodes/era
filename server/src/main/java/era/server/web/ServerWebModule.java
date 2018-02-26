package era.server.web;


import era.server.ServerModule;
import era.server.common.PageRenderer;
import era.server.common.UnauthorizedException;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import spark.Spark;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The Server Web <em>module</em> is a module of the "Server" application
 * that handles requests coming from students to view their assignments. It is
 * responsible for both generating the UI that allows students to see their
 * assignments and authenticating those students.
 *
 * This particular package will have Controllers to handle these requests.
 *
 * @see ServerModule
 */
@ThreadSafe
public class ServerWebModule implements ServerModule {

    private static final PageRenderer RENDERER = new PageRenderer();
    private static ServerWebModule INSTANCE;
    private final HealthController healthController;
    private final IndexController indexController;
    private final AssignmentViewController assignmentViewController;

    /**
     * Creates the controllers contained in this module
     */
    private ServerWebModule(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO) {
        this.healthController = new HealthController();
        this.indexController = new IndexController(RENDERER);
        this.assignmentViewController = new AssignmentViewController(RENDERER, assignmentDAO, courseDAO);
    }

    @Override
    public void setupRoutes() {
        Spark.get("/hello", healthController::checkHealth);
        Spark.get("/", indexController::checkIndex);
        Spark.get("/student/logout", (request, response)->{
            request.session().attributes().clear();
            response.redirect("/");
            return null;
        });
        Spark.get("/student/:userName", assignmentViewController::assignmentList);
        Spark.get("/student/:userName/assignment/:assignmentId", assignmentViewController::assignment);
    }

    /**
     * Threadsafe singleton static factory method. It constructs the one
     * instance, and only, instance of {@link ServerWebModule}, using the
     * provided Data Access Objects
     */
    public static ServerWebModule instance(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO) {
        if (INSTANCE == null) {
            synchronized (ServerWebModule.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServerWebModule(
                            studentDAO,
                            courseDAO,
                            assignmentDAO
                    );
                }
            }
        }

        return INSTANCE;
    }
}
