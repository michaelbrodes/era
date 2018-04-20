package era.server.api;

import era.server.communication.UploaderAuthentication;
import era.server.data.AdminDAO;
import era.server.data.model.Admin;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Base64;
import java.util.Optional;

public class RequestFilter implements Filter{

    private final AdminDAO adminDAO;
    public RequestFilter(AdminDAO adminDAO){
        this.adminDAO = adminDAO;
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        String authorization = request.headers("Authorization");
        String authCredentails = authorization.replace("Basic ", "");
        byte[] byteArrayCredentials = Base64.getDecoder().decode(authCredentails);
        String decodedCredentials = new String(byteArrayCredentials, "UTF-8");
        int seperator = decodedCredentials.indexOf('/');
        String username = decodedCredentials.substring( 0, seperator);
        String password = decodedCredentials.substring(seperator + 1, decodedCredentials.length());

        Optional<Admin> user =  adminDAO.fetchByUsername(username);

        if (!user.isPresent() || !UploaderAuthentication.validatePassword( password , user.get().getPassword()))
        {
            throw Spark.halt(403);
        }
    }
}
