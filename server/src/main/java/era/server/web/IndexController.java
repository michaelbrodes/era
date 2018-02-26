package era.server.web;

import era.server.common.PageRenderer;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;



public class IndexController {

    private static final int SESSION_TIMEOUT_SECONDS = 600;
    private final PageRenderer renderer;

    public IndexController(PageRenderer renderer) {
        this.renderer = renderer;
    }

    public String checkIndex(Request request, Response response) {

        request.session(true);
        request.session().maxInactiveInterval(SESSION_TIMEOUT_SECONDS);

        if (request.session().attribute("user") != null) {

            response.redirect("/student/" + request.session().attribute("user"));

        }

        request.session().attribute("user","testUsername");

        Map<String, Object> model = new HashMap<>();

        return renderer.render(model, "index.hbs");

    }


}
