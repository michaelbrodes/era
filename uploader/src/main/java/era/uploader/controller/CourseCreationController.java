package era.uploader.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import era.uploader.common.GUIUtil;
import era.uploader.common.UploaderProperties;
import era.uploader.communication.RESTException;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.TeacherDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Teacher;
import era.uploader.data.model.Term;
import era.uploader.data.viewmodel.StudentMetaData;
import era.uploader.service.CourseCreationService;
import era.uploader.service.coursecreation.RosterFileException;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * This class needs to have a GUI that will take in a CSV file. Generate a
 * bunch of students from that CSV file using
 * {@link era.uploader.service.CourseCreationService}, and then output the
 * courses created with the students created from the courses
 */
public class CourseCreationController {
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Button browseFiles;
    @FXML
    private Button createCourse;
    @FXML
    private Button homeButton;
    @FXML
    private TableView<StudentMetaData> output;
    @FXML
    private Label chosenFile;
    @FXML
    private Label createCourseLabel;
    @FXML
    private ComboBox<String> termDropdown;
    @FXML
    private ComboBox<Integer> yearDropdown;
    @FXML
    private ComboBox<String> instructorDropdown;
    @FXML
    private Label offlineWarningLabel;
    @FXML
    private Label modeLabel;


    private final FileChooser chooser = new FileChooser();
    private final CourseCreationService service = new CourseCreationService(CourseDAOImpl.instance(), TeacherDAOImpl.instance());

    private String absoluteChosenFile;
    private Map<String, Teacher> nameToTeacher = Collections.emptyMap();

    @FXML
    void initialize() {
        chooser.setTitle("Choose roster File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Comma Separated Value (CSV)", "*.csv"),
                new FileChooser.ExtensionFilter("Plain Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        ObservableList<String> availableTerms = FXCollections.observableArrayList();
        EnumSet.allOf(Term.class)
                .stream()
                .map(Term::humanReadable)
                .forEach(availableTerms::add);
        termDropdown.setPromptText("Term...");
        termDropdown.setItems(availableTerms);

        int currentYear = Year.now().getValue();
        ObservableList<Integer> availableYears = FXCollections.observableArrayList();
        IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .forEach(availableYears::add);
        yearDropdown.setPromptText("Year...");
        yearDropdown.setItems(availableYears);

        nameToTeacher = Maps.uniqueIndex(service.getAllTeachers(), Teacher::getName);
        instructorDropdown.setPromptText("Instructor...");
        instructorDropdown.getItems().addAll(nameToTeacher.keySet());
        instructorDropdown.setEditable(true);

        loadingSpinner.setVisible(false);

        GUIUtil.displayConnectionStatus(modeLabel, offlineWarningLabel);
    }

    /**
     * When {@link #browseFiles} is clicked, an file explorer will show to
     * allow the user to input their files.
     *
     * @param event the mouse click that triggered this handler
     */
    public void findrosterFile(MouseEvent event) {
        final Window buttonScope = browseFiles.getScene().getWindow();
        File possibleCSVfile = chooser.showOpenDialog(buttonScope);

        if (possibleCSVfile == null ) return; // no more processing needed.

        if (possibleCSVfile.exists()
                && !possibleCSVfile.isDirectory()) {
            // we need the absolute path to the chosen csv file so that we can
            // read it in later in #createCourses. The name is for human reading
            absoluteChosenFile = possibleCSVfile.getAbsolutePath();
            chosenFile.setText(possibleCSVfile.getName());
        } else if (possibleCSVfile.isDirectory()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid roster File!");
            errorAlert.setContentText("The roster file cannot be a directory!");
            errorAlert.showAndWait();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid roster File");
            errorAlert.setContentText("The roster file must exist");
            errorAlert.showAndWait();
        }
    }

    /**
     * IF there is a valid roster file and a semester chosen, we will start
     * parsing that roster file and output the courses we created via that file
     * to the screen.
     *
     * @param event the mouse event that triggered this handler
     */
    public void createCourses(ActionEvent event) {
        Optional<Semester> semester = grabCurrentSemester();
        Optional<String> chosenFile = grabChosenFile();
        Optional<String> instructor = grabInstructor();

        Path inputPath = chosenFile
                .map(Paths::get)
                .orElse(null);

        Optional<Teacher> teacher = instructor
                .map(this::resolveInstructor);

        if (!teacher.isPresent()) {
            Alert noInstructor = new Alert(Alert.AlertType.WARNING);
            noInstructor.setHeaderText("No instructor chosen");
            noInstructor.setContentText("No instructor has been chosen. Please either select an existing instructor or type in a new one.");
            noInstructor.showAndWait();
            // short circuit because cannot process anymore
            return;
        }

        if (inputPath == null) {
            Alert noFile = new Alert(Alert.AlertType.WARNING);
            noFile.setHeaderText("No file chosen");
            noFile.setContentText("No roster file has been chosen.");
            noFile.showAndWait();
            // short circuit because cannot process anymore
            return;
        }

        if (!semester.isPresent()) {
            Alert noSemester = new Alert(Alert.AlertType.WARNING);
            noSemester.setHeaderText("No semester chosen");
            noSemester.setContentText("No semester has been chosen. Please select a year and a term.");
            noSemester.showAndWait();
            // short circuit because cannot process anymore
            return;
        }

        final boolean uploading;
        if (UploaderProperties.instance().isUploadingEnabled() != null) {
            uploading = UploaderProperties.instance().isUploadingEnabled();
        } else {
            uploading = false;
        }

        // course creation can take some time since it involves inserting a
        // bunch of student records into two databases. Because we don't want
        // our users closing the windowing, thinking the application is frozen,
        // we do a buffering spinner why we are waiting
        CourseCreationTask backgroundTask = CourseCreationTask.builder()
                .isUploadingEnabled(uploading)
                .courseCreationService(service)
                .rosterFilePath(inputPath)
                .semester(semester.get())
                .teacher(teacher.get())
                .warningHandler(warningEvent -> {
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setHeaderText("Course Creation Warning");
                    warningAlert.setContentText(warningEvent.getWarningMessage());
                    warningAlert.show();
                })
                .errorHandler((errorEvent) -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Course Creation Error");
                    if (errorEvent.getThrowable() instanceof RESTException) {
                        errorAlert.setContentText("There was an error connecting " +
                    "to the remote server. Please make sure that server is up " +
                    "and that the right URL is shown in uploader.properties");
                    } else if (errorEvent.getThrowable() instanceof RosterFileException) {
                        errorAlert.setContentText(
                                "There was an error trying to open the roster file. " +
                                "Make sure you have permission to access the file's directory.");
                    } else {
                        errorEvent.getThrowable().printStackTrace();
                    }
                })
                .successHandler((successEvent) -> {
                    Collection<Course> courses = successEvent.getResult();

                    ObservableList<StudentMetaData> students = StudentMetaData.fromCourses(courses);
                    output.setItems(students);

                    enableForm(true);
                    Alert uploadSuccessful = new Alert(Alert.AlertType.INFORMATION);
                    uploadSuccessful.setHeaderText("Courses Uploaded");
                    uploadSuccessful.setContentText(courses.size() + " courses uploaded to the server");
                    uploadSuccessful.showAndWait();
                })
                .create();

        enableForm(false);
        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();


//        Collection<Course> courses;
//        try {
//            courses = service.createCourses(inputPath, semester.get(), teacher.get());
//        } catch (IOException e) {
//            // technically shouldn't be possible but we need to be defensive
//            Alert fileOperationError = new Alert(Alert.AlertType.ERROR);
//            fileOperationError.setHeaderText("Error trying to open roster file.");
//            fileOperationError.setContentText("There was an error trying to open the roster file." +
//                    " Make sure you have permission to access the file's directory.");
//            fileOperationError.showAndWait();
//            // short circuit because cannot process anymore
//            return;
//        }
//
//        try {
//            if (uploading) {
//                service.upload(courses);
//                Alert uploadSuccessful = new Alert(Alert.AlertType.INFORMATION);
//                uploadSuccessful.setHeaderText("Courses Uploaded");
//                uploadSuccessful.setContentText(courses.size() + " courses uploaded to the server");
//                uploadSuccessful.show();
//            } else {
//                Alert notUploaded = new Alert(Alert.AlertType.WARNING);
//                notUploaded.setHeaderText("Offline Mode Enabled");
//                notUploaded.setContentText(
//                        "The Uploader is in \"Offline\" mode so these courses were not uploaded to the server. "
//                                + "If you would like their assignments to be posted to the server, resubmit the roster file with \"Online\" mode enabled.");
//                notUploaded.show();
//            }
//        } catch (IOException e) {
//            Alert fileOperationError = new Alert(Alert.AlertType.ERROR);
//            fileOperationError.setHeaderText("Cannot connect to Server.");
//            fileOperationError.setContentText("There was an error connecting " +
//                    "to the remote server. Please make sure that server is up " +
//                    "and that the right URL is shown in uploader.properties");
//            fileOperationError.showAndWait();
//        }
//        ObservableList<StudentMetaData> students = StudentMetaData.fromCourses(courses);
//        output.setItems(students);
    }

    private void enableForm(boolean enable) {
        loadingSpinner.setVisible(!enable);
        createCourse.setDisable(!enable);
        instructorDropdown.setDisable(!enable);
        termDropdown.setDisable(!enable);
        yearDropdown.setDisable(!enable);
        browseFiles.setDisable(!enable);
        homeButton.setDisable(!enable);
    }

    private Optional<String> grabInstructor() {
        return Optional.ofNullable(instructorDropdown.getValue());
    }

    private Optional<String> grabChosenFile() {
        return Optional.ofNullable(absoluteChosenFile);
    }


    private Optional<Semester> grabCurrentSemester() {
        String chosenTerm = termDropdown.getValue();
        Integer chosenYear = yearDropdown.getValue();
        Optional<Semester> chosenSemester = Optional.empty();

        if (chosenTerm != null && chosenYear != null) {
            Term term = Term.humanValueOf(chosenTerm);
            Year year = Year.of(chosenYear);
            chosenSemester = Optional.of(Semester.of(term, year));
        }

        return chosenSemester;
    }

    private Teacher resolveInstructor(String teacherName) {
        if (nameToTeacher.containsKey(teacherName.trim())) {
            return nameToTeacher.get(teacherName.trim());
        } else {
            return service.createTeacher(teacherName.trim());
        }
    }

    public void home() throws IOException {
        UINavigator nav = new UINavigator(browseFiles.getScene());
        nav.changeToHome();
    }
    public void assignment() throws IOException {
        UINavigator nav = new UINavigator(browseFiles.getScene());
        nav.changeToCreateAssignment();
    }
    public void scanPDF() throws IOException {
        UINavigator nav = new UINavigator(browseFiles.getScene());
        nav.changeToScan();
    }
}
