package era.uploader.controller;

import com.google.common.collect.Multimap;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.viewmodel.StudentMetaData;
import era.uploader.service.CourseCreationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * TODO implement this class over break
 * This class needs to have a GUI that will take in a CSV file. Generate a
 * bunch of students from that CSV file using
 * {@link era.uploader.service.CourseCreationService}, and then output the
 * courses created with the students created from the courses
 */
public class CourseCreationController {
    @FXML
    private Button browseFiles;
    @FXML
    private Button createCourse;
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


    private final FileChooser chooser = new FileChooser();
    private final CourseCreationService service = new CourseCreationService(CourseDAOImpl.instance());

    private String absoluteChosenFile;

    @FXML
    void initialize() {
        chooser.setTitle("Choose roster File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Comma Separated Value (CSV)", "*.csv"),
                new FileChooser.ExtensionFilter("Plain Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        ObservableList<String> availableTerms = FXCollections.observableArrayList();
        EnumSet.allOf(Semester.Term.class)
                .stream()
                .map(Semester.Term::humanReadable)
                .forEach(availableTerms::add);
        termDropdown.setPromptText("Term...");
        termDropdown.setItems(availableTerms);

        int currentYear = Year.now().getValue();
        ObservableList<Integer> availableYears = FXCollections.observableArrayList();
        IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .forEach(availableYears::add);
        yearDropdown.setPromptText("Year...");
        yearDropdown.setItems(availableYears);
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

        Path inputPath = chosenFile
                .map(Paths::get)
                .orElse(null);

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
        Multimap<Course, Student> coursesToStudents = null;
        try {
            coursesToStudents = service.createCourses(inputPath, semester.get());
        } catch (IOException e) {
            // technically shouldn't be possible but we need to be defensive
            Alert fileOperationError = new Alert(Alert.AlertType.ERROR);
            fileOperationError.setHeaderText("Error trying to open roster file.");
            fileOperationError.setContentText("There was an error trying to open the roster file." +
                    " Make sure you have permission to access the file's directory.");
            fileOperationError.showAndWait();
            // short circuit because cannot process anymore
            return;
        }

        ObservableList<StudentMetaData> students = StudentMetaData.fromMultimap(coursesToStudents);
        output.setItems(students);
    }

    private Optional<String> grabChosenFile() {
        return Optional.ofNullable(absoluteChosenFile);
    }


    private Optional<Semester> grabCurrentSemester() {
        String chosenTerm = termDropdown.getValue();
        Integer chosenYear = yearDropdown.getValue();
        Optional<Semester> chosenSemester = Optional.empty();

        if (chosenTerm != null && chosenYear != null) {
            Semester.Term term = Semester.Term.humanValueOf(chosenTerm);
            Year year = Year.of(chosenYear);
            chosenSemester = Optional.of(Semester.of(term, year));
        }

        return chosenSemester;
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
