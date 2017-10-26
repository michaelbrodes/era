package era.server;

import spark.Spark;

public class ServerApp {

    public static void main(String[] args) {
        Spark.port(3000);
        Spark.get("/hello", (req, res) -> {
            res.status(200);
            return "Is it me you are looking for?";
        });
    }
}
