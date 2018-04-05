package era.server.data.model;

import java.util.Map;

public interface ViewableModel {
    Map<String, Object> toViewModel();
}
