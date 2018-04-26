package era.server.data.model;

public class CourseExistence {
    private final String courseId;
    private final boolean exists;

    public CourseExistence(String courseId, boolean exists) {
        this.courseId = courseId;
        this.exists = exists;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isExists() {
        return exists;
    }
}
