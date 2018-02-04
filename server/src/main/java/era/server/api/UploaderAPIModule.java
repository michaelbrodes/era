package era.server.api;

import era.server.ServerModule;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.model.Course;
import spark.Spark;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The Uploader API <em>module</em> is a module of the "Server" application
 * that handles requests coming from the "Uploader" desktop application.
 * Therefore, it allows for one way communication from the uploader to the
 * server.
 *
 * This particular package will have REST Controllers that facilitate this
 * communication, such as the {@link UploadController} for assignment uploads.
 *
 * @see ServerModule
 */
@ThreadSafe
public class UploaderAPIModule implements ServerModule {
    private static final String API_PREFIX = "/api";
    private static UploaderAPIModule INSTANCE;

    private final UploadController upc;

    /**
     * Creates all the controllers in this module using the DAOs provided.
     */
    private UploaderAPIModule(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO) {
        this.upc = new UploadController(courseDAO, assignmentDAO, studentDAO);
    }

    @Override
    public void setupRoutes() {
        Spark.post(API_PREFIX + Course.ENDPOINT, upc::uploadCourses);
        Spark.post(API_PREFIX + Course.ENDPOINT + "/:courseId/assignment", upc::uploadAssignment);
    }

    /**
     * Threadsafe singleton static factory method. It constructs the one
     * instance, and only, instance of {@link UploaderAPIModule}, using the
     * provided Data Access Objects
     */
    public static UploaderAPIModule instance(
            StudentDAO studentDAO,
            CourseDAO courseDAO,
            AssignmentDAO assignmentDAO) {
        if (INSTANCE == null) {
            synchronized (UploaderAPIModule.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UploaderAPIModule(
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
