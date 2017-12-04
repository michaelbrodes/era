package era.server.data;

public interface Model {
    String ENDPOINT = "";
    long getUniqueId();
    default String getEndpoint() {
        return ENDPOINT;
    }
}
