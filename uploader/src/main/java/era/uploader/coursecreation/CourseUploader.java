package era.uploader.coursecreation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import era.uploader.coursecreation.Course_JsonTypeAdapter;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class CourseUploader {

    public static void uploadCourses(Collection<Course> courses, String host) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(host + "/api/course");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Course.class, new Course_JsonTypeAdapter())
                .create();


        String json = gson.toJson(courses);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 201)
        {
            throw new RuntimeException("Server unable to parse Json sent \n code: " + response.getStatusLine().getStatusCode());
        }
        client.close();
    }

}

