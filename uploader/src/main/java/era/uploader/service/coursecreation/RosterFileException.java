package era.uploader.service.coursecreation;

import java.io.IOException;

public class RosterFileException extends IOException {
    public RosterFileException(IOException realException) {
        super(realException);
    }
}
