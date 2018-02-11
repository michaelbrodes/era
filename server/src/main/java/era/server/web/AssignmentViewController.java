package era.server.web;

import spark.Request;
import spark.Response;

public class AssignmentViewController {

    public String showAssignments(Request request, Response response) {

        request.session(true);

        request.session().attribute("user", "user");

        return "It worked. Username = " + request.params(":userName");

    }

}
