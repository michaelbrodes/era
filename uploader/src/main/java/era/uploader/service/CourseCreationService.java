package era.uploader.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import era.uploader.common.UploaderProperties;
import era.uploader.service.coursecreation.CSVParser;
import era.uploader.communication.CourseUploader;
import era.uploader.data.CourseDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static era.uploader.common.MultimapCollector.toMultimap;

public class CourseCreationService {
    private final CourseDAO courseDAO;
    private static final String DEFAULT_SERVER_URL = "http://localhost:3001";

    public CourseCreationService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    /**
     * Generates a set of students given an input CSV file. The schema for
     * that CSV file is of Last Name, First Name, Username, Student ID, Child
     * Course ID, *. Anything after Child Course ID is ignored.
     *
     * @param filePath Path to the CSV file we are grabbing the students form
     * @return a map of courses to students.
     * @throws IOException We couldn't find the file you inputted.
     */
    public Multimap<Course, Student> createCourses(@Nonnull Path filePath, Semester semester, boolean isUploadingEnabled) throws IOException {
        Preconditions.checkNotNull(filePath);
        // ignore the title record
        final int firstRecord = 1;
        final CSVParser parser = new CSVParser(semester);

        // The stream needs to be closed after the first reduce is done
        Multimap<Course, Student> courseToStudents;
        try (Stream<String> csvLines = Files.lines(filePath)) {
            courseToStudents = csvLines.skip(firstRecord)
                    .map((inputStudent) -> inputStudent.split(","))
                    .map(parser::parseLine)
                    .filter(Objects::nonNull)
                    .collect(toMultimap());
        }

        courseDAO.insertCourseAndStudents(courseToStudents, semester);
        return courseToStudents;
    }

    public void uploadIfEnabled(Collection<Course> courses) throws IOException {
        UploaderProperties uploaderConfiguration = UploaderProperties.instance();
        boolean isUploadingEnabled = uploaderConfiguration.isUploadingEnabled() == null ?
                false :
                uploaderConfiguration.isUploadingEnabled();
        if (isUploadingEnabled) {
            String serverURL = uploaderConfiguration
                    .getServerURL()
                    .orElse(DEFAULT_SERVER_URL);
            CourseUploader.uploadCourses(courses, serverURL);

        }
    }
}
