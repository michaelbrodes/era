package era.server.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import era.server.communication.CourseJSONTypeAdapter;
import era.server.data.model.Course;

import java.lang.reflect.Type;
import java.util.List;

public final class JSONUtil {
    // Gson instances are threadsafe so it is okay to have one for the entire application.
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Course.class, new CourseJSONTypeAdapter())
            .create();

    private JSONUtil() {
        // no op
    }

    public static Gson gson() {
        return GSON;
    }

    public static <T> Type getListType(Class<T> type) {
        return new TypeToken<List<T>>() {
        }.getType();
    }
}
