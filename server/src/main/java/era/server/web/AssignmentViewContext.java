package era.server.web;

import spark.Request;
import spark.Response;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An immutable class that carries the state of a student's session when
 * viewing their assignments. This is backed by Spark's {@link Request#session()}
 */
public class AssignmentViewContext {
    private final String studentUsername;
    private final String assignmentId;

    private AssignmentViewContext(@Nullable String studentUsername, @Nullable String assignmentId) {
        this.studentUsername = studentUsername;
        this.assignmentId = assignmentId;
    }

    public static AssignmentViewContext initialize(Request request, Response response) {
        request.session(true);
        if (CASAuth.assertAuthenticated(request, response)) {
            String student = request.session().attribute("user");
            String assignmentId = request.params(":assignmentId");

            return new AssignmentViewContext(student, assignmentId);
        }
        else {
            return null;
        }

    }

    public Optional<String> getStudentUsername() {
        return Optional.ofNullable(studentUsername);
    }

    public Optional<String> getAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }
}
