package era.server.web;

import spark.Request;
import spark.Response;

public class AssignmentViewController {

    public String showAssignments(Request request, Response response) {

        request.session(true);

        return "It worked. Username = " + request.params(":userName");

    }

}
