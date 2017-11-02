package era.server;

import spark.Spark;

public class ServerApp {
    public static final String API = "/api";

    public static void main(String[] args) {
        UploadController upc = new UploadController();
        Spark.port(3000);
        Spark.get("/hello", (req, res) -> {
            res.status(200);
            return "Is it me you are looking for?";
        });

        Spark.post(API + "/course/:courseId/assignment", (req, res) -> {
            upc.handleRequest(req, res);
            return "";
        });
    }
}
