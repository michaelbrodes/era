package era.uploader.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import era.uploader.communication.Course_JsonTypeAdapter;
import era.uploader.data.model.Course;

public class JSONUtil {
    private JSONUtil() {
        // cannot instantiate
    }

    public static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Course.class, new Course_JsonTypeAdapter())
                .create();
    }
}
