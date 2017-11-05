package era.uploader.processing;

import era.uploader.data.model.Assignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
//import org.apache.http.client.fluent.Form;
//import org.apache.http.client.fluent.Request;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.HttpClientBuilder;


public class AssignmentUploader {

    static final String SERVER_ADDRESS = "localhost:3000";

    public void uploadAssignments(Collection<Assignment> assignments) throws IOException {

        for (Assignment current: assignments) {

            HttpClient client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost(SERVER_ADDRESS);

            File file = new File(current.getImageFilePath());
            String courseId = current.getCourse().getId() + "";
            String assignmentName = current.getName();
            String studentId = current.getStudent().getSchoolId();


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addTextBody(":courseId", courseId, ContentType.TEXT_PLAIN );

            builder.addTextBody("X-Assignment-Name", assignmentName, ContentType.TEXT_PLAIN);

            builder.addTextBody("X-Student-Id", studentId, ContentType.TEXT_PLAIN);

            builder.addBinaryBody("X-Assignment-File-Name", file, ContentType.DEFAULT_BINARY,
                    current.getImageFilePath());

            HttpEntity entity = builder.build();

            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            
        }
    }

}
