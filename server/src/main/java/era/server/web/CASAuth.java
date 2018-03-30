package era.server.web;

import com.google.common.base.Strings;
import era.server.common.AppConfig;
import era.server.data.StudentDAO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import spark.Request;
import spark.Response;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * A Controller (I didn't name the class) that handles
 * authentication/authorization for students/admins. It provides authentication
 * using the "Central Authentication Service" protocol. SIUE CAS
 * implementation isn't very sophisticated (shocker) and only supports signing
 * in for a signle user and <strong>service-wide</strong> sign out (it will
 * drop the session on their server for every student currently logged in into
 * my-assignments).
 *
 * HTTP flow diagram for login: https://apereo.github.io/cas/4.2.x/images/cas_flow_diagram.png
 * logout is basically "redirect the user to a CAS logout endpoint"
 */
public class CASAuth {
    private static final Logger LOGGER = LoggerFactory.getLogger(CASAuth.class);
    private StudentDAO studentDAO;

    public CASAuth(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    //Login a student using the CAS server
    public String login(Request request, Response response) throws Exception {
        request.session(true);//create the session

        String encodedURL = URLEncoder.encode("https://my-assignments.isg.siue.edu/student/login", "UTF-8");


        if (request.queryParams("ticket") != null) { //the user is not authenticated
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet("https://cas.isg.siue.edu/itscas/serviceValidate?ticket=" +
                    request.queryParams("ticket") +
                    "&service=" + encodedURL);
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) { //This shouldn't happen either

                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    InputStream httpStream = httpResponse.getEntity().getContent();

                    String username = getUsernameFromXML(httpStream);
                    if (username == null) {
                        response.redirect("/" + encodedURL, 302);
                    } else {

                        assertStudentInDatabase(username);
                        request.session().attribute("user", username);
                        response.redirect("/student/" + username, 302);
                    }
                }
                else {
                    LOGGER.info(
                            "Invalid status code from CAS ticket validation: {}", 
                            httpResponse.getStatusLine().getStatusCode());
                }
            }
        }
        else if (Strings.isNullOrEmpty(request.session().attribute("user"))) { //if they came back from CAS
            response.redirect("https://cas.isg.siue.edu/itscas/login?service=" + encodedURL, 302);
        }
        else { //if the user is already authenticated, and in our session
            String username = request.session().attribute("user");
            response.redirect("/student/" + username, 302);
        }
        return null;
    }

    //Make sure that the current user in our application is authenticated
    public static Boolean assertAuthenticated(Request request, Response response) {
        boolean casEnabled = AppConfig.instance().isCASEnabled();
        
        String student = request.params(":userName");
        String user = request.session().attribute("user");
        if (!casEnabled) {
            return true;
        } else if  (Strings.isNullOrEmpty(user) || Strings.isNullOrEmpty(student) || !user.equals(student)){
            response.redirect("/student/login", 302);
            return false;
        } else {
            return true;
        }
    }

    //Send a request to CAS to log all users out of our application
    public Object logout(Request request, Response response) throws Exception{
        request.session(false);
        request.session().removeAttribute("user");
        String encodedURL = URLEncoder.encode("https://my-assignments.isg.siue.edu/", "UTF-8");
        response.redirect("https://cas.isg.siue.edu/itscas/logout?service=" + encodedURL);
        
        return null;
    }

    //Either return a student based on their username, or create one, if it doesn't exist
    private void assertStudentInDatabase(String username) {
        studentDAO.getOrCreateStudent(username);
    }

    //Retrieve a username from XML
    private String getUsernameFromXML(InputStream is) throws SAXException, ParserConfigurationException {
        try {
            //Create a document we can parse, and parsing it
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize(); //I was told to do this by the internet

            NodeList nList = doc.getElementsByTagName("cas:user"); //Grab the 'cas:user' element from the XML

            //We were unable to find the 'cas:user' tag
            if (nList.getLength() == 0) {
                return null;
            }

            Node node = nList.item(0);
            return node.getTextContent();

        }
        catch (IOException e) {
            LOGGER.error("IOException when reading from cas XML document: ", e);
        }
        return null;
    }
}