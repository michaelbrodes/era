package era.server.web;

import era.server.common.UnauthorizedException;
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

    public static UserContext initialize(Request request, Response response) throws UnauthorizedException {
        request.session(true);
        if (CASAuth.assertAuthenticated(request, response)) {
            String student = request.session().attribute("user");
            if (student == null) {
                student = request.params(":userName");
            }
            String assignmentId = request.params(":assignmentId");

            return new UserContext(student, assignmentId);
        }
        else {
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
