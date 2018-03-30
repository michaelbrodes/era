package era.uploader.communication;

import java.io.IOException;

public class RESTException extends IOException {
    public RESTException(IOException e) {
        super(e);
    }
}
