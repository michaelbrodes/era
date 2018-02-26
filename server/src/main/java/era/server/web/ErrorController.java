package era.server.web;

import com.google.common.collect.Maps;
import era.server.common.PageRenderer;
import spark.Request;
import spark.Response;

import java.util.Map;

// TODO move this to CASAuth when it is merged in
public class ErrorController {
    private final PageRenderer renderer;

    public ErrorController(PageRenderer renderer) {
        this.renderer = renderer;
    }
    public String unauthorized(Exception exception, Request request, Response response) {
        request.session(true);
        response.status(403);

        Map<String, Object> message = Maps.newHashMap();
        String storedUsername = request.session().attribute("user");

        if (storedUsername != null) {
            message.put("user", storedUsername);
        }
        message.put("reason", "You are not authorized to view this page.");

        return renderer.render(message, "unauthorized.hbs");
    }
}
