package era.server;

import era.server.api.UploaderAPIModule;
import era.server.common.AppConfig;
import era.server.common.ConfigOpts;
import era.server.data.AdminDAO;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.StudentDAO;
import era.server.data.access.AdminDAOImpl;
import era.server.data.access.AssignmentDAOImpl;
import era.server.data.access.CourseDAOImpl;
import era.server.data.access.StudentDAOImpl;
import era.server.web.ServerWebModule;
import spark.Spark;

import java.util.Map;

public class ServerApp {

    /**
     * Main entry point into the Server web application. It is responsible
     * configuration and setup of {@link ServerModule}s. It parses commandline
     * arguments for setting up the database connection and networking. See
     * the RUNME doc for details.
     */
    public static void main(String[] args) {
        Map<ConfigOpts, String> optionMap = ConfigOpts.parseArgs(args);
        String dbName = optionMap.getOrDefault(ConfigOpts.DB_NAME, "dev");
        String host = optionMap.getOrDefault(ConfigOpts.HOST, "localhost");
        String dbPort = optionMap.getOrDefault(ConfigOpts.DB_PORT, "3306");
        String user = optionMap.getOrDefault(ConfigOpts.USER, "s002716");
        String password = optionMap.getOrDefault(ConfigOpts.PASSWORD, "qot42yim");
        String port = optionMap.getOrDefault(ConfigOpts.SERVER_PORT, "80");
        Boolean casEnabled = Boolean.valueOf(optionMap.getOrDefault(ConfigOpts.CAS_ENABLED, "false"));

        AppConfig config = AppConfig.instance();
        config.setPort(port);
        config.setConnectionString(dbName, host, dbPort, user, password);
        config.setCASEnabled(casEnabled);

        // startup all the DAOs so we don't have any duplicated connections
        final StudentDAO studentDAOImpl = StudentDAOImpl.instance();
        final CourseDAO courseDAOImpl = CourseDAOImpl.instance();
        final AssignmentDAO assignmentDAOImpl = AssignmentDAOImpl.instance();
        final AdminDAO adminDAO = AdminDAOImpl.instance();

        ServerModule[] modules = new ServerModule[] {
                UploaderAPIModule.instance(
                        studentDAOImpl,
                        courseDAOImpl,
                        adminDAO,
                        assignmentDAOImpl),
                ServerWebModule.instance(
                        studentDAOImpl,
                        courseDAOImpl,
                        assignmentDAOImpl,
                        adminDAO)
        };

        Spark.staticFiles.location("/static");

        for (ServerModule module : modules) {
            module.setupRoutes();
        }
    }
}
