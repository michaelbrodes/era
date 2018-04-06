package era.server.web;

import era.server.common.UnauthorizedException;
import era.server.data.AdminDAO;
import spark.Request;
import spark.Response;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An immutable class that carries the state of a student's session when
 * viewing their assignments. This is backed by Spark's {@link Request#session()}
 */
public class UserContext {
    private final String studentUsername;
    private final String assignmentId;

    private UserContext(@Nullable String studentUsername, @Nullable String assignmentId) {
        this.studentUsername = studentUsername;
        this.assignmentId = assignmentId;
    }

    public static UserContext initialize(Request request, Response response, AdminDAO adminDAO) throws UnauthorizedException {
        request.session(true);
        if (CASAuth.assertAuthenticated(request, response, adminDAO)) {
            String assignmentId = request.params(":assignmentId");
            String student = request.params(":userName");
            return new UserContext(student, assignmentId);
        } else {
            throw new UnauthorizedException();
        }

    }

    public Optional<String> getStudentUsername() {
        return Optional.ofNullable(studentUsername);
    }

    public Optional<String> getAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }
}
