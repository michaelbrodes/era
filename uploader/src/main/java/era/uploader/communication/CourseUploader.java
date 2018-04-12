package era.uploader.communication;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import era.uploader.common.JSONUtil;
import era.uploader.data.model.Course;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CourseUploader {

    public static void uploadCourses(Collection<Course> courses, String host) throws RESTException {

        try{
            CloseableHttpClient client = HttpClients.createDefault();
            Gson gson = JSONUtil.gson();
            uploadCoursesUsingClient(courses, host, client, gson);
            client.close();
        } catch (IOException e)  {
            throw new RESTException(e);
        }
    }

    public static void uploadBadCourses(List<Course> currentCourses, String host) throws RESTException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URI existenceAPIURI = new URIBuilder(host)
                    .setPath("/api/course")
                    .build();
            StringBuilder existenceAPI = new StringBuilder(existenceAPIURI.toString());
            boolean first = true;

            for (Course course : currentCourses) {
                if (first) {
                    existenceAPI.append("?courses[]=").append(course.getName());
                    first = false;
                } else {
                    existenceAPI.append("&courses[]=").append(course.getName());
                }
            }

            HttpGet existenceRequest = new HttpGet(existenceAPI.toString());
            CloseableHttpResponse existenceResponse = client.execute(existenceRequest);

            BufferedReader content = new BufferedReader(new InputStreamReader(existenceResponse.getEntity().getContent()));
            Type courseListType = new TypeToken<List<String>>() {}.getType();
            Gson gson = JSONUtil.gson();
            List<String> badCourseNames = gson.fromJson(content, courseListType);

            List<Course> badCourses = currentCourses.stream()
                    .filter((course) -> badCourseNames.contains(course.getName()))
                    .collect(Collectors.toList());
            existenceResponse.close();
            uploadCoursesUsingClient(badCourses, host, client, gson);
        } catch (IOException e) {
            throw new RESTException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void uploadCoursesUsingClient(Collection<Course> courses, String host, CloseableHttpClient client, Gson gson) throws IOException {
        HttpPost httpPost = new HttpPost(host + "/api/course");

        String json = gson.toJson(courses);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 400)
        {
            throw new RuntimeException("Server unable to parse Json sent\njson:" + json);
        } else if (response.getStatusLine().getStatusCode() == 500) {
            throw new RuntimeException("Exception server side, please see logs.");
        } else if (response.getStatusLine().getStatusCode() != 201) {
            throw new RuntimeException("Unexpected status code from Course Upload endpoint.\ncode:"
                    + response.getStatusLine().getStatusCode());
        }

        response.close();
    }
}

