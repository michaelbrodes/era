package era.server.web;


import era.server.common.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.sql.DriverManager;
import java.sql.SQLException;

public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

    public String checkHealth(Request request, Response response) {
        String connectionString = AppConfig.instance().getConnectionString();
        try {
            DriverManager.getConnection(connectionString);
            LOGGER.info("Connected to database at " + connectionString);
        } catch (SQLException e) {
            LOGGER.error("Couldn't connect to database " + e.getMessage());
            LOGGER.error("Connection string was " + connectionString);
            response.status(500);
            return "Not here!";
        }

        return "Is it me you are looking for?";
    }
}
