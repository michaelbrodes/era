package era.uploader.view;

import com.google.common.collect.Multimap;
import era.uploader.controller.PDFScanningController;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    //TODO update this to something not hardcoded you schlum
    private static final String HOST_NAME = "http://localhost:3000";

    public PDFScanView(UIManager uim, GridPane gridPane) {

        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        this.gridPane = gridPane;
        pdfCtrl = new PDFScanningController();

    }

    @Override
    public void start(Stage stage) {

//        uManage.hidePrimaryStage();

        stage.setWidth(500);
        stage.setHeight(400);

        mainStage = stage;

        stage.hide();
        //Getting the course, and then the assignment or creating the assignment
        getCourse();

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
            stage.getScene().getWindow().hide();
            uManage.changeToHomeView();
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
                                pdfCtrl.scanPDF(fullPath, currentCourse, currentAssignment, HOST_NAME);
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

            //Setting up the new window for the additional information required to create assignment
            Stage secondStage = new Stage();
            GridPane gridPane2 = new GridPane();
            secondStage.setScene(new Scene(gridPane2));

            gridPane2.setHgap(20);
            gridPane2.setVgap(20);

            secondStage.setTitle("Choose Class");
            secondStage.setX(mainStage.getX() + 200);
            secondStage.setY(mainStage.getY() + 100);
            secondStage.setAlwaysOnTop(true);

            //Getting all the courses
            //TODO HIDE THE PREVIOUS WINDOW. WHY WILL THIS NOT WORK
            Set<Course> courses = pdfCtrl.getAllCourses();

            //Dynamically will make the window as large as it needs with how many classes there are
            secondStage.setHeight((courses.size() + 2) * 250);
            secondStage.setWidth(300);

            secondStage.show();

            gridPane2.add(returnHomeButton, 4, 2);

            //Setting up the radio button group
            final ToggleGroup group = new ToggleGroup();
            Button choiceButton = new Button("Select");

            RadioButton[] radioButtons = new RadioButton[courses.size()];

            int count = 0;

            //TODO Add field to ask for host

            //Iterates through the courses and displays the course names on the Radio button options
            for (Course curr : courses) {

                radioButtons[count] = new RadioButton(curr.getDepartment() + "-"
                        + curr.getCourseNumber() + "-"
                        + curr.getSectionNumber()
                );
                radioButtons[count].setToggleGroup(group);

                gridPane2.add(radioButtons[count], 4,  3 + 2 * count);
                count++;
            }

            gridPane2.add(choiceButton, 4, 3 + count);

            ToggleGroup locations = new ToggleGroup();
            RadioButton online = new RadioButton("Online Database");
            online.setToggleGroup(locations);
            RadioButton offline = new RadioButton("Offline Database");
            online.setToggleGroup(locations);

            gridPane2.add(online, 4, 3 + count + 3);
            gridPane2.add(offline, 4, 7 + count);

            //buttonPressed = false;


            //Executes whenever the user has selected a choice and has a radio button selected.
            choiceButton.setOnAction(event -> {
                //buttonPressed = true;
                Toggle selectedCourse = group.getSelectedToggle();
                Toggle selectedLocation = locations.getSelectedToggle();
                String courseName = selectedCourse.toString();
                String[] getCourseName = courseName.split("\\'");
                String[] splitName = getCourseName[1].split("-");

                for (Course curr : courses) {
                    if (curr.getDepartment().equals(splitName[0]) &&
                            curr.getCourseNumber().equals(splitName[1]) &&
                            curr.getSectionNumber().equals(splitName[2])) {
                        currentCourse = curr;
                    }
                }

                gridPane2.getChildren().clear();

                getAssignment(secondStage, gridPane2);
            });
        
    }

    public void getAssignment(Stage secondStage, GridPane gridPane2){


        //Resets the size of the Stage for the assignment name prompt text field
        secondStage.setWidth(500);
        secondStage.setHeight(400);

        secondStage.setTitle("Assignment Name");

        Label enterLabel = new Label("Enter the Assignment/Test Name");
        TextField assignmentName = new TextField();
        Button submitButton = new Button("Submit");

        assignmentName.setPromptText("Name of Assignment");

        gridPane2.add(enterLabel, 2, 2);
        gridPane2.add(assignmentName, 2, 3);
        gridPane2.add(submitButton, 3, 3);



        //This executes once the user has entered in an assignment name
        submitButton.setOnAction(event -> {

            //This checks to make sure that the text field is not empty
            if(assignmentName.getText() == null){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Not a Valid Assignment Name");
                errorAlert.setContentText("You must enter a valid Assignment Name.");
                errorAlert.showAndWait();
            } else {
                //Will prep to have the assignment created
                currentAssignment = assignmentName.getText();
                secondStage.hide();

                mainStage.show();
            }



        });


    }

}
