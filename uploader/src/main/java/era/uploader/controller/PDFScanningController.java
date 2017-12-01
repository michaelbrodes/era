package era.uploader.controller;

import com.google.common.collect.Maps;
import era.uploader.data.model.Course;
import era.uploader.processing.ScanningProgress;
import era.uploader.service.PDFScanningService;
import era.uploader.view.UINavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javafx.stage.Window;

public class PDFScanningController {

    @FXML
    private RadioButton offline;
    @FXML
    private RadioButton online;
    @FXML
    private Button scanButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private ComboBox<String> courseNames;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Button browseButton;
    @FXML
    private TextField assignmentName;


    //TODO update this to something not hardcoded you schlum
    private static final String HOST_NAME = "http://localhost:3000";

    private final PDFScanningService pdfServ = new PDFScanningService();

    private Path fullPath;
    private String fullFileName;
    //private String currentAssignment;
   //private Course currentCourse;


    @FXML
    public void initialize() {

        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );



        List<Course> courses = pdfServ.getAllCourses();

        final Map<String,Course> nameToCourse = Maps.uniqueIndex(courses, Course::getName);

        ObservableList<String> courseKeys = FXCollections.observableArrayList(nameToCourse.keySet());

        courseNames.setItems(courseKeys);



        //Typical file browser logic to click on the file and display it
        //  to the textInput Field.
        browseButton.setOnAction((event) -> {
            Window currWindow = browseButton.getScene().getWindow();
            File file = fileChooser.showOpenDialog(currWindow);
            if (file != null) {
                fullPath = file.toPath();
                fullFileName = fullPath.toString();
                String[] splitFile = fullFileName.split(File.separator);
                String fName = splitFile[splitFile.length - 1];
                fileNameLabel.setText(fName);
            }
        });


        scanButton.setOnAction(event -> {

            String currentAssignment = assignmentName.getText();
            String currCourseName = courseNames.getValue();
            Course currentCourse = nameToCourse.get(currCourseName);


            if (fullFileName != null)
                try {

                if(currentCourse != null && currentAssignment != null) {
                    final ScanningProgress scanningProgress = pdfServ.scanPDF(fullPath, currentCourse, currentAssignment, HOST_NAME);
                    URL url = getClass().getResource("/gui/pdf-progress.fxml");

                    FXMLLoader fxmlloader = new FXMLLoader();
                    fxmlloader.setLocation(url);
                    fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());

                    // here we gOOOOOOOOOOOOOOOOOoooooooooooooooooo -Mario
                    ((PDFProgressController)fxmlloader.getController()).setScanningProgress(scanningProgress);
                }

                }
                catch (IOException | IllegalArgumentException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File Not Found");
                    errorAlert.setContentText("The File that you specified cannot be found");
                    errorAlert.showAndWait();
                }
        });

    }
}
