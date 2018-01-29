package era.uploader.communication;

import era.uploader.data.model.Assignment;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class AssignmentUploader {


    public static void uploadAssignments(Collection<Assignment> assignments, String host) throws IOException {

        for (Assignment current: assignments) {

            HttpClient client = HttpClientBuilder.create().build();

            String courseId = current.getCourse().getUniqueId() + "";

            host += "/api/course/" + courseId + "/assignment";

            HttpPost post = new HttpPost(host);

            File file = new File(current.getImageFilePath());
            String assignmentName = current.getName();
            int assignmentId = current.getUniqueId();
            String studentId = Integer.toString(current.getStudent().getUniqueId());

            post.addHeader("X-Assignment-Id", assignmentId + "" );
            post.addHeader("X-Assignment-Name", assignmentName);
            post.addHeader("X-Student-Id", studentId);
            post.addHeader("X-Assignment-File-Name", current.getImageFilePath());


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addBinaryBody("pdf", file, ContentType.DEFAULT_BINARY,
                    current.getImageFilePath());

            HttpEntity entity = builder.build();

            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            if(response.getStatusLine().getStatusCode() != 201){

                throw new RuntimeException("Entity was not created due to " + response.getStatusLine().getReasonPhrase());

            }

        }
    }

}
