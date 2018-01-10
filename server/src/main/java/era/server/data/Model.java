package era.server.data;

import java.util.Map;

public interface Model {
    String ENDPOINT = "";
    long getUniqueId();
    default String getEndpoint() {
        return ENDPOINT;
    }
    /**
     * Creates Map from the fields in this object to their values. This allows
     * Models to be used in Handlebar templates.
     *
     * @return A map representing this object. Its keys are the fields of this
     * class and its values are their values.
     */
    Map<String, Object> toViewModel();
}
