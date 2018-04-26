package era.uploader.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import era.uploader.common.UploaderProperties;
import era.uploader.communication.CourseUploader;
import era.uploader.communication.RESTException;
import era.uploader.data.CourseDAO;
import era.uploader.data.TeacherDAO;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Teacher;
import era.uploader.service.coursecreation.CSVParser;
import era.uploader.service.coursecreation.ParsedLine;
import era.uploader.service.coursecreation.RosterFileException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourseCreationService {
    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;
    private static final String DEFAULT_SERVER_URL = "http://localhost:3001";

    public CourseCreationService(CourseDAO courseDAO, TeacherDAO teacherDAO) {
        this.courseDAO = courseDAO;
        this.teacherDAO = teacherDAO;
    }

    /**
     * Parses a roster file, creates a list of courses from that roster file,
     * and inserts those courses into the database.
     *
     * @param filePath Path to the CSV file we are grabbing the students form
     * @param semester the semester that these courses should belong to.
     * @param teacher the teacher that teaches this course.
     * @return a map of courses to students.
     * @throws IOException We couldn't find the file you inputted.
     */
    public Collection<Course> createCourses(@Nonnull Path filePath, Semester semester, Teacher teacher) throws RosterFileException {
        Preconditions.checkNotNull(filePath);
        // ignore the title record
        final int firstRecord = 1;
        final CSVParser parser = new CSVParser();

        // The stream needs to be closed after the first reduce is done
        Collection<Course> parsedCourses = Sets.newHashSet();
        try (Stream<String> csvLines = Files.lines(filePath)) {
            Map<Course, List<ParsedLine>> courseToStudents = csvLines.skip(firstRecord)
                    .map((inputStudent) -> inputStudent.split(","))
                    .map(parser::parseLine)
                    .filter(ParsedLine::hasAllFields)
                    .collect(Collectors.groupingBy(
                            (line) -> Course.builder()
                                    .withSemester(semester)
                                    .withTeacher(teacher)
                                    .createUnique(line.getCourseDepartment(), line.getCourseNumber(), line.getCourseSection()))
                    );

            for (Map.Entry<Course, List<ParsedLine>> courseToLine :
                    courseToStudents.entrySet()) {
                Course parsedCourse = courseToLine.getKey();
                for (ParsedLine studentLine :
                        courseToLine.getValue()) {
                    Student parsedStudent = Student.builder()
                            .withFirstName(studentLine.getFirstName())
                            .withLastName(studentLine.getLastName())
                            .withSchoolId(studentLine.getStudentId())
                            .withCourse(parsedCourse)
                            .createUnique(studentLine.getUserName());

                    parsedCourse.getStudentsEnrolled().add(parsedStudent);
                }
                parsedCourses.add(parsedCourse);
            }
        } catch (IOException e) {
            throw new RosterFileException(e);
        }

        courseDAO.insertCourseAndStudents(parsedCourses, semester);
        return parsedCourses;
    }

    public void upload(Collection<Course> courses) throws RESTException {
        UploaderProperties uploaderConfiguration = UploaderProperties.instance();
        String serverURL = uploaderConfiguration
                .getServerURL()
                .orElse(DEFAULT_SERVER_URL);
        CourseUploader.uploadCourses(courses, serverURL);
    }

    public Set<Teacher> getAllTeachers() {
        return ImmutableSet.copyOf(teacherDAO.getAllTeachers());
    }

    public Teacher createTeacher(String teacherName) {
        Teacher newTeacher = new Teacher(teacherName);
        return teacherDAO.insert(newTeacher);
    }
}
