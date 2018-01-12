package era.server;

import era.server.api.UploaderAPIModule;
import era.server.common.AppConfig;
import era.server.common.ConfigOpts;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.access.AssignmentDAOImpl;
import era.server.data.access.CourseDAOImpl;
import era.server.data.access.StudentDAOImpl;
import era.server.web.ServerWebModule;
import spark.Spark;

import java.util.Map;

public class ServerApp {
    private static final String API = "/api";

    public static void main(String[] args) {
        Map<ConfigOpts, String> optionMap = ConfigOpts.parseArgs(args);
        String dbName = optionMap.getOrDefault(ConfigOpts.DB_NAME, "dev");
        String host = optionMap.getOrDefault(ConfigOpts.HOST, "localhost");
        String dbPort = optionMap.getOrDefault(ConfigOpts.DB_PORT, "3306");
        String user = optionMap.getOrDefault(ConfigOpts.USER, "s002716");
        String password = optionMap.getOrDefault(ConfigOpts.PASSWORD, "qot42yim");
        String port = optionMap.getOrDefault(ConfigOpts.SERVER_PORT, "80");

        AppConfig config = AppConfig.instance();
        config.setPort(port);
        config.setConnectionString(dbName, host, dbPort, user, password);

        // startup all the DAOs so we don't have any duplicated connections
        final StudentDAO studentDAOImpl = StudentDAOImpl.instance();
        final CourseDAO courseDAOImpl = CourseDAOImpl.instance();
        final AssignmentDAO assignmentDAOImpl = AssignmentDAOImpl.instance();

        ServerModule[] modules = new ServerModule[] {
                UploaderAPIModule.instance(
                        studentDAOImpl,
                        courseDAOImpl,
                        assignmentDAOImpl),
                ServerWebModule.instance(
                        studentDAOImpl,
                        courseDAOImpl,
                        assignmentDAOImpl)
        };

        Spark.staticFiles.location("/static");

        for (ServerModule module : modules) {
            module.setupRoutes();
        }
    }
}
