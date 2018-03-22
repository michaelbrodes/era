package era.uploader.controller;

import era.uploader.common.GUIUtil;
import era.uploader.common.UploaderProperties;
import era.uploader.data.model.Course;
import era.uploader.service.PDFScanningService;
import era.uploader.service.processing.ScanningProgress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
    @FXML
    private Label modeLabel;

    private static final String NULL_HOST = null;

    private final PDFScanningService pdfServ = new PDFScanningService();

    private Path fullPath;
    private String fullFileName;
    //private Course currentCourse;
    //private String currentAssignment;


    @FXML
    public void initialize() {

        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );


        List<Course> courses = pdfServ.getAllCourses();

        final Map<String, List<Course>> nameToCourses = GUIUtil.groupSectionsByTeacher(courses);
        ObservableList<String> courseKeys = FXCollections.observableArrayList(nameToCourses.keySet());
        courseNames.setItems(courseKeys);


        //Typical file browser logic to click on the file and display it
        //  to the textInput Field.
        browseButton.setOnAction((event) -> {
            Window currWindow = browseButton.getScene().getWindow();
            File file = fileChooser.showOpenDialog(currWindow);
            if (file != null) {
                fullPath = file.toPath();
                fullFileName = fullPath.toString();
                String fName = file.getName();
                fileNameLabel.setText(fName);
            }
        });


        scanButton.setOnAction(event -> {

            String currentAssignment = assignmentName.getText();
            String currCourseName = courseNames.getValue();
            List<Course> currentCourses = nameToCourses.get(currCourseName);



            if (fullFileName == null || currCourseName == null || currentAssignment == null || currentAssignment.trim().equals("")) {

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Must Choose Options");
                errorAlert.setContentText("You must enter an Assignment Name, Choose a course, and Choose a PDF file to Scan.");
                errorAlert.showAndWait();

            }

            else if (fullFileName != null)
                try {
                boolean uploading = false;
                if(currentCourses != null && currentAssignment != null) {
                    if (UploaderProperties.instance().isUploadingEnabled() != null) {
                        uploading = UploaderProperties.instance().isUploadingEnabled();
                    }
                    if(uploading) {

                        String serverURL = UploaderProperties.instance().getServerURL().orElse(null);
                        final ScanningProgress scanningProgress = pdfServ.scanPDF(fullPath, currentCourses, currentAssignment, serverURL);
                        URL url = getClass().getResource("/gui/pdf-progress.fxml");

                        FXMLLoader fxmlloader = new FXMLLoader();
                        fxmlloader.setLocation(url);
                        //fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
                        Parent root = fxmlloader.load();
                        Scene mainScene = scanButton.getScene();
                        // here we gOOOOOOOOOOOOOOOOOoooooooooooooooooo -Mario
                        ((PDFProgressController)fxmlloader.getController()).setScanningProgress(scanningProgress);
                        mainScene.setRoot(root);
                    }
                    else {
                        final ScanningProgress scanningProgress = pdfServ.scanPDF(fullPath, currentCourses, currentAssignment, NULL_HOST);
                        URL url = getClass().getResource("/gui/pdf-progress.fxml");

                        FXMLLoader fxmlloader = new FXMLLoader();
                        fxmlloader.setLocation(url);
                        //fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
                        Parent root = fxmlloader.load();
                        Scene mainScene = scanButton.getScene();
                        // here we gOOOOOOOOOOOOOOOOOoooooooooooooooooo -Mario
                        ((PDFProgressController)fxmlloader.getController()).setScanningProgress(scanningProgress);
                        mainScene.setRoot(root);
                    }

                }

                } catch (IOException | IllegalArgumentException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File Not Found");
                    errorAlert.setContentText("The File that you specified cannot be found");
                    errorAlert.showAndWait();
                }
        });

        GUIUtil.displayConnectionStatus(modeLabel);
    }

    public void home() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToHome();
    }

    public void course() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToCreateCourse();
    }

    public void assignment() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToCreateAssignment();
    }
}
