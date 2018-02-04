package era.server;

import spark.Request;
import spark.Response;

/**
 * A <em>ServerModule</em> is a distinct component of the server. For
 * instance, a public API that should be consumed by another application and
 * has no direct relationship to showing PDFs would be considered its own
 * component.
 * <p/>
 * Implementers of this interface will group routes that make up a distinct
 * component and pass off request handling of those routes to
 * <em>Controllers</em>. For example, {@link era.server.web.ServerWebModule}
 * sets up the route "/hello" and passes off handling for that route to
 * {@link era.server.web.HealthController#checkHealth(Request, Response)}
 * <p/>
 * As of writing this documentation there are two main components to the
 * application: {@link era.server.api.UploaderAPIModule} and
 * {@link era.server.web.ServerWebModule}. UploaderAPIModule contains routes
 */
public interface ServerModule {
    /**
     * Groups together routes that make up a distinct component of the server.
     */
    void setupRoutes();
}
