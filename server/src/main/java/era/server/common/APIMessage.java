package era.server.common;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import era.server.data.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@ParametersAreNonnullByDefault
public class APIMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIMessage.class);

    /**
     * No-Op so it can't be instantiated
     */
    private APIMessage() {

    }

    /**
     * When we have an error caused in an API, we often want to give the user
     * of that API a message of what caused the error and a response code.
     * This static method wraps that functionality into a single method.
     *
     * @param request The user's request.
     * @param response Our response to a request. It will be given the
     *                 responseCode supplied in this method.
     * @param responseCode The response code for this error.
     * @param errorMessage The message we want the user to see when they
     *                     receive this error
     * @return A JSON object wrapping the useful information that the user
     * wants.
     */
    public static String error(Request request, Response response, int responseCode, String errorMessage) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(response);
        Preconditions.checkNotNull(errorMessage);

        String requestedUrl = request.url();
        response.status(responseCode);
        response.type("application/json");

        JsonObject error = new JsonObject();
        error.addProperty("at", requestedUrl);
        error.addProperty("reason", errorMessage);
        error.addProperty("statusCode", responseCode);

        return error.toString();
    }

    public static String created (Response response) {
        return created(response, null);
    }
    /**
     * Returns a 201 status code with a JSON object describing where a model
     * was created
     *
     * @param response our response to a user
     * @param createdModel the model we created
     * @return a JSON object describe what was created and where it was
     * created at
     */
    public static String created(Response response, @Nullable Model createdModel) {
        Preconditions.checkNotNull(response);

        response.status(201);
        if (createdModel != null) {
            response.type("application/json");
            JsonObject createdResponse = new JsonObject();
            createdResponse.addProperty("type", createdModel.getClass().getCanonicalName());
            createdResponse.addProperty("endpoint", createdModel.ENDPOINT);

            return createdResponse.toString();
        } else {
            return "";
        }
    }
}
