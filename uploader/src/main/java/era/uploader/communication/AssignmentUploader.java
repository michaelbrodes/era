package era.uploader.communication;

import com.google.common.collect.Lists;
import era.uploader.data.model.Assignment;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

public class AssignmentUploader {
    // below are HTTP status codes sent back from the server with variable names for what they mean.
    private static final int CONFLICT = 409;
    private static final int CREATED = 201;


    public static List<FailedAssignment> uploadAssignments(Collection<Assignment> assignments, String host) throws IOException {
        List<FailedAssignment> failedAssignments = Lists.newArrayList();

        for (Assignment current: assignments) {

            CloseableHttpClient client = HttpClientBuilder.create().build();

            String courseName = URLEncoder.encode(current.getCourse().getName(), "UTF-8");
            String semesterName = URLEncoder.encode(current.getCourse().getSemester().apiToString(), "UTF-8");
            String studentPath = host + "/api/course/" + courseName + "/semester/" + semesterName + "/assignment";

            HttpPost post = new HttpPost(studentPath);

            File file = new File(current.getImageFilePath());
            String assignmentName = current.getName();
            String studentName = current.getStudent().getUserName();

            post.addHeader("X-Assignment-Name", assignmentName);
            post.addHeader("X-Student-Name", studentName);
            post.addHeader("X-Assignment-File-Name", current.getImageFilePath());


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addBinaryBody("pdf", file, ContentType.DEFAULT_BINARY,
                    current.getImageFilePath());

            HttpEntity entity = builder.build();

            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            String humanSemester = current.getCourse().getSemester().toString();
            if (response.getStatusLine().getStatusCode() == CONFLICT) {
                failedAssignments.add(new FailedAssignment(
                        assignmentName,
                        studentName,
                        courseName,
                        humanSemester,
                        "please make sure you have the course for that student uploaded"
                ));
            } else if(response.getStatusLine().getStatusCode() != CREATED){
                failedAssignments.add(new FailedAssignment(
                        assignmentName,
                        studentName,
                        courseName,
                        humanSemester,
                        "unexpected error. Please notify a developer so they can check the logs."
                ));
                throw new RuntimeException("Entity was not created due to " + response.getStatusLine().getReasonPhrase());
            }
            client.close();
        }

        return failedAssignments;
    }

}
