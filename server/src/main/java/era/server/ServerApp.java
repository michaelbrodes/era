package era.server;

import era.server.common.AppConfig;
import era.server.common.ConfigOpts;
import era.server.controller.UploadController;
import era.server.data.access.AssignmentDAOImpl;
import era.server.data.access.CourseDAOImpl;
import era.server.data.access.StudentDAOImpl;
import spark.Spark;

import java.util.Map;

public class ServerApp {
    public static final String API = "/api";

    public static void main(String[] args) {
        Map<ConfigOpts, String> optionMap = ConfigOpts.parseArgs(args);
        String host = optionMap.getOrDefault(ConfigOpts.HOST, "localhost");
        String port = optionMap.getOrDefault(ConfigOpts.PORT, "3001");
        String user = optionMap.getOrDefault(ConfigOpts.USER, "root");
        String password = optionMap.getOrDefault(ConfigOpts.PASSWORD, "");

        AppConfig config = AppConfig.instance();
        config.setConnectionString(host, port, user, password);

        // startup all the DAOs so we don't have any duplicated connections
        final StudentDAOImpl studentDAOImpl = new StudentDAOImpl();;
        final AssignmentDAOImpl assignmentDAOImpl = new AssignmentDAOImpl();
        final CourseDAOImpl courseDAOImpl = new CourseDAOImpl();

        UploadController upc = new UploadController(
                courseDAOImpl,
                assignmentDAOImpl,
                studentDAOImpl
        );

        Spark.port(3000);
        Spark.get("/hello", (req, res) -> {
            res.status(200);
            return "Is it me you are looking for?";
        });

        Spark.post(API + "/course/:courseId/assignment", upc::handleRequest);
    }
}
