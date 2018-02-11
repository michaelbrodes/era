package era.server.web;

import era.server.common.PageRenderer;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;



public class IndexController {

    public static final int SESSION_TIMEOUT_SECONDS = 600;

    public String checkIndex(Request request, Response response) {

        request.session(true);
        request.session().maxInactiveInterval(SESSION_TIMEOUT_SECONDS);

        if (request.session().attribute("user") != null) {

            response.redirect("/student/" + request.session().attribute("user"));

        }



        Map<String, Object> model = new HashMap<>();

        return PageRenderer.render(model, "index.hbs");

    }


}
