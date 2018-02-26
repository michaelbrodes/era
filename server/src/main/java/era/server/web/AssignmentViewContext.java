package era.server.web;

import spark.Request;

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

    public static AssignmentViewContext initialize(Request request) {
        // TODO wrap CASAuth#assertAuthed in this method
        request.session(true);
        String student = request.session().attribute("user");
        // TODO remove this once authentication is in
        if (student == null) {
            student = request.params(":userName");
        }

        String assignmentId = request.params(":assignmentId");

        return new AssignmentViewContext(student, assignmentId);
    }

    public Optional<String> getStudentUsername() {
        return Optional.ofNullable(studentUsername);
    }

    public Optional<String> getAssignmentId() {
        return Optional.ofNullable(assignmentId);
    }
}
