package era.server;

import era.server.common.AppConfig;
import era.server.common.ConfigOpts;
import spark.Spark;

import java.util.Map;

public class ServerApp {

    public static void main(String[] args) {
        Map<ConfigOpts, String> optionMap = ConfigOpts.parseArgs(args);
        String host = optionMap.getOrDefault(ConfigOpts.HOST, "localhost");
        String port = optionMap.getOrDefault(ConfigOpts.PORT, "3001");
        String user = optionMap.getOrDefault(ConfigOpts.USER, "root");
        String password = optionMap.getOrDefault(ConfigOpts.PASSWORD, "");

        AppConfig config = AppConfig.instance();
        config.setConnectionString(host, port, user, password);

        Spark.port(3000);

        Spark.get("/hello", (req, res) -> {
            res.status(200);
            return "Is it me you are looking for?";
        });
    }
}
