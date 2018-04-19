package era.uploader.controller;

import era.uploader.communication.RESTException;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Teacher;
import era.uploader.service.CourseCreationService;
import javafx.event.EventHandler;

import java.nio.file.Path;
import java.util.Collection;

public class CourseCreationTask extends BackendTask<Collection<Course>> {
    private final Path rosterFilePath;
    private final Teacher teacher;
    private final Semester semester;
    private final boolean isUploadingEnabled;
    private final CourseCreationService courseCreationService;

    private CourseCreationTask(Builder builder) {
        super(builder);
        this.rosterFilePath = builder.rosterFilePath;
        this.teacher = builder.teacher;
        this.semester = builder.semester;
        this.isUploadingEnabled = builder.isUploadingEnabled;
        this.courseCreationService = builder.courseCreationService;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected Collection<Course> call() throws Exception {

        Collection<Course> createdCourses = courseCreationService
                .createCourses(this.rosterFilePath, this.semester, this.teacher);

        if (isUploadingEnabled) {
            try{
                courseCreationService.upload(createdCourses);
            } catch (RESTException e){
                onWarning(e.getMessage());
            }
        } else {
            onWarning("The Uploader is in \"Offline\" mode so these courses were not uploaded to the server. "
                    + "If you would like their assignments to be posted to the server, resubmit the roster file with \"Online\" mode enabled.");
        }

        return createdCourses;
    }


    public static final class Builder extends AbstractBuilder<Collection<Course>> {
        private Path rosterFilePath;
        private Teacher teacher;
        private Semester semester;
        private boolean isUploadingEnabled;
        private CourseCreationService courseCreationService;

        private Builder() {
        }

        public CourseCreationTask create() {
            return new CourseCreationTask(this);
        }

        public Builder rosterFilePath(Path rosterFilePath) {
            this.rosterFilePath = rosterFilePath;
            return this;
        }

        public Builder teacher(Teacher teacher) {
            this.teacher = teacher;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder isUploadingEnabled(boolean isUploadingEnabled) {
            this.isUploadingEnabled = isUploadingEnabled;
            return this;
        }

        public Builder courseCreationService(CourseCreationService courseCreationService) {
            this.courseCreationService = courseCreationService;
            return this;
        }

        public Builder warningHandler(EventHandler<TaskWarningEvent> warningHandler) {
            return (Builder) super.warningHandler(warningHandler);
        }

        public Builder errorHandler(EventHandler<TaskErrorEvent> errorHandler) {
            return (Builder) super.errorHandler(errorHandler);
        }

        public Builder successHandler(EventHandler<TaskSuccessEvent<Collection<Course>>> successHandler) {
            return (Builder) super.successHandler(successHandler);
        }
    }
}
