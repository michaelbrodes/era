package era.server.common;

public class UnauthorizedException extends Exception {
    public UnauthorizedException() {
        super("User is not authorized to make that request");
    }
}
