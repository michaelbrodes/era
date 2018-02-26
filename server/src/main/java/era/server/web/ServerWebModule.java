package era.server.web;


import era.server.ServerModule;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.LogoutRoute;
import org.pac4j.sparkjava.SecurityFilter;
import spark.Spark;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The Server Web <em>module</em> is a module of the "Server" application
 * that handles requests coming from students to view their assignments. It is
 * responsible for generating the UI that allows students to see their
 * assignments
 *
 * This particular package will have Controllers to handle these requests.
 */
@ThreadSafe
public class ServerWebModule implements ServerModule {

    private static ServerWebModule INSTANCE;

    private final HealthController healthController;
    private final IndexController indexController;
    private final AssignmentViewController assignmentViewController;
    private final CASAuth casAuth;
    private final Boolean casEnabled;

    /**
     * Creates the controllers contained in this module
     */
    private ServerWebModule(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO,
            Boolean casEnabled) {
        this.healthController = new HealthController();
        this.indexController = new IndexController();
        this.assignmentViewController = new AssignmentViewController();
        this.casAuth = new CASAuth(studentDAO);
        this.casEnabled = casEnabled;
    }

    @Override
    public void setupRoutes() {
        if (casEnabled) {
            Spark.get("/student/login", casAuth::login);
        }
    Spark.get("/hello", healthController::checkHealth);
    Spark.get("/", indexController::checkIndex);
    Spark.get("/student/:userName", assignmentViewController::showAssignments);

    }

    /**
     * Threadsafe singleton static factory method. It constructs the one
     * instance, and only, instance of {@link ServerWebModule}, using the
     * provided Data Access Objects
     */
    public static ServerWebModule instance(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO,
            Boolean casEnabled) {
        if (INSTANCE == null) {
            synchronized (ServerWebModule.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServerWebModule(
                            studentDAO,
                            courseDAO,
                            assignmentDAO,
                            casEnabled
                    );
                }
            }
        }

        return INSTANCE;
    }
}
