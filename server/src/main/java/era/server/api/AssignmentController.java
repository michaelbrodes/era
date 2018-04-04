package era.server.api;

import era.server.data.AssignmentDAO;
import spark.Request;
import spark.Response;

public class AssignmentController {
    private final AssignmentDAO assignmentDAO;

    public AssignmentController(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public Object deleteAssignment(Request request, Response response) {
        if (request.params("uuid") != null
                && assignmentDAO.delete(request.params("uuid"))) {
            response.status(204);
        } else {
            response.status(404);
        }

        return "";
    }
}
