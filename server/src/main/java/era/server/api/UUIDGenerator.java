package era.server.api;

import java.util.UUID;

public class UUIDGenerator {
    private UUIDGenerator() {
        // no op
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
