package era.uploader.communication;

import com.google.common.collect.Lists;
import era.uploader.common.UploaderProperties;
import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AssignmentUploader {
    // below are HTTP status codes sent back from the server with variable names for what they mean.
    private static final int CONFLICT = 409;
    private static final int CREATED = 201;


    public static List<FailedAssignment> uploadAssignments(Collection<Assignment> assignments, String host) throws IOException {
        List<FailedAssignment> failedAssignments = Lists.newArrayList();

        UploaderProperties properties = UploaderProperties.instance();

        for (Assignment current: assignments) {

            CloseableHttpClient client = HttpClientBuilder.create().build();

            String courseName = URLEncoder.encode(current.getCourse().getName(), "UTF-8");
            String semesterName = URLEncoder.encode(current.getCourse().getSemester().apiToString(), "UTF-8");
            String studentPath = host + "/api/course/" + courseName + "/semester/" + semesterName + "/assignment";

            HttpPost post = new HttpPost(studentPath);

            File file = new File(current.getImageFilePath());
            String assignmentName = current.getName();
            String studentName = current.getStudent().getUserName();
            String credentials = properties.getUser() + "/" + properties.getPassword();

            post.addHeader("X-Assignment-Name", assignmentName);
            post.addHeader("X-Student-Name", studentName);
            post.addHeader("X-Assignment-File-Name", current.getImageFilePath());
            post.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes()));


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addBinaryBody("pdf", file, ContentType.DEFAULT_BINARY,
                    current.getImageFilePath());

            HttpEntity entity = builder.build();

            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            String humanSemester = current.getCourse().getSemester().toString();
            if (response.getStatusLine().getStatusCode() == CONFLICT) {
                Course conflictCourse = current.getCourse();
                // we don't want to upload the entire course worth of students, but just the one student
                Course courseCopy = Course.builder()
                        .withDatabaseId(conflictCourse.getUniqueId())
                        .withName(conflictCourse.getName())
                        .withSemester(conflictCourse.getSemester())
                        .withTeacher(conflictCourse.getTeacher())
                        .withTeacherId(conflictCourse.getTeacherId())
                        .withSemesterId(conflictCourse.getSemesterId())
                        .withStudents(Collections.singleton(current.getStudent()))
                        .create(conflictCourse.getDepartment(), conflictCourse.getCourseNumber(), conflictCourse.getSectionNumber(), conflictCourse.getUuid());
                CourseUploader.uploadCourses(Collections.singletonList(courseCopy), host);
                failedAssignments.add(new FailedAssignment(
                        assignmentName,
                        studentName,
                        courseName,
                        humanSemester,
                        "please make sure you have the course for that student uploaded"
                ));
            }else if(response.getStatusLine().getStatusCode() == 403){
                throw new RESTException("Invalid Credentials");
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
