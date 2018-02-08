package era.uploader.common;

import java.util.UUID;

public class UUIDGenerator {
    private UUIDGenerator() {
        // no op
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
