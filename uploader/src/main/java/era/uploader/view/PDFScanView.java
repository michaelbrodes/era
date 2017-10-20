package era.uploader.view;

import com.google.common.collect.Multimap;
import era.uploader.controller.PDFScanningController;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PDFScanView extends Application {

    private GridPane gridPane;
    private UIManager uManage;
    private PDFScanningController pdfCtrl;
    private Stage mainStage;
    private String fullFileName;
    private Path fullPath;
    private Course currentCourse;
    private String currentAssignment;
    private boolean buttonPressed;
    private Button returnHomeButton = new Button("Home");



    public PDFScanView(UIManager uim, GridPane gridPane) {

        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        this.gridPane = gridPane;
        pdfCtrl = new PDFScanningController();

    }

    @Override
    public void start(Stage stage) {

        //Getting the course, and then the assignment or creating the assignment

        while(!buttonPressed)
            getCourse();

        getAssignment();

        //initializing
        final FileChooser fileChooser = new FileChooser();
        Button fileBrowserButton = new Button("Browse");
        Button submitButton = new Button("Submit");
        TextField pdfFileName = new TextField();


        //Clearing the view
        gridPane.getChildren().clear();
        //Adding the elements to the GridPane
        gridPane.add(pdfFileName, 4, 5);
        gridPane.add(fileBrowserButton, 5, 5);
        gridPane.add(returnHomeButton, 4, 2);
        gridPane.add(submitButton, 4, 7);

        //Sets the prompt for the TextInput Field
        pdfFileName.setPromptText("PDF File");


        //Typical file browser logic to click on the file and display it
        //  to the textInput Field.
        fileBrowserButton.setOnAction((event) -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                fullPath = file.toPath();
                String fName = fullPath.toString();
                fullFileName = fName;
                String[] splitFile = fName.split(File.separator);
                fName = splitFile[splitFile.length - 1];
                pdfFileName.setText(fName);
            }
        });

        returnHomeButton.setOnAction(event -> {
            uManage.changeToHomeView(gridPane);
        });


        //This is the logic for the submit button when pressed. It checks to make sure that the file
        //  type is valid, and then passes it along to the scanPDF function in the controller.
        submitButton.setOnAction(event -> {

            //This pulls out the extension at the end of the file to ensure that it is the correct file type.
            String[] splitFile = fullFileName.split("\\.");
            String shortFile = splitFile[splitFile.length - 1];

            if (fullFileName != null)
                try {
                    //Switch statement is so that if we have different file types, add cases here
                    switch (shortFile) {
                        case "pdf":
                            //TODO still have to attach this to a class and assignment
                            if(currentCourse != null && currentAssignment != null)
                                pdfCtrl.scanPDF(fullPath, currentCourse, currentAssignment);
                            break;
                        default:
                            //This lets the user know that the file type that they have selected was not compatible with our
                            //  software.
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setHeaderText("File Not Supported");
                            errorAlert.setContentText("We're sorry, we do not support ." + shortFile + " file type at this time. " +
                                    "Please make sure to have .pdf as the file extension.");
                            errorAlert.showAndWait();
                            break;
                    }
                }
                catch (IOException e) {
                    //TODO FILE NOT FOUND. How to display to user?
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File Not Found");
                    errorAlert.setContentText("The File that you specified cannot be found");
                    errorAlert.showAndWait();
                }
        });

    }

    public void getCourse(){
            gridPane.getChildren().clear();
            Set<Course> courses = pdfCtrl.getAllCourses();
            gridPane.add(returnHomeButton, 4, 2);

            final ToggleGroup group = new ToggleGroup();
            Button choiceButton = new Button("Select");

            RadioButton[] radioButtons = new RadioButton[courses.size()];

            int count = 0;

            for (Course curr : courses) {

                radioButtons[count] = new RadioButton(curr.getDepartment() + "-"
                        + curr.getCourseNumber() + "-"
                        + curr.getSectionNumber()
                );
                radioButtons[count].setToggleGroup(group);

                gridPane.add(radioButtons[count], 4,  3 + 2 * count);
                count++;
            }

            gridPane.add(choiceButton, 4, 3 + count);

            buttonPressed = false;



            choiceButton.setOnAction(event -> {
                buttonPressed = true;
                Toggle selectedCourse = group.getSelectedToggle();
                String courseName = selectedCourse.toString();
                String[] splitName = courseName.split("-");

                for (Course curr : courses) {
                    if (curr.getDepartment().equals(splitName[0]) &&
                            curr.getCourseNumber().equals(splitName[1]) &&
                            curr.getSectionNumber().equals(splitName[2])) {
                        currentCourse = curr;
                    }
                }
            });
        
    }

    public void getAssignment(){
        gridPane.getChildren().clear();
    }

}
