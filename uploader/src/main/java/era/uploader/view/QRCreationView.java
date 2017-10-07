package era.uploader.view;

import com.google.common.collect.Multimap;
import era.uploader.controller.QRCreationController;
import era.uploader.creation.QRCreator;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import era.uploader.view.UIManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class QRCreationView extends Application {

    private UIManager uManage;
    private Stage mainStage;
    private QRCreationController qrCtrl;
    private String fullFileName;

    public QRCreationView() {
        UIManager uManage = new UIManager(mainStage);
        mainStage = uManage.getPrimaryStage();
        qrCtrl = new QRCreationController(new PageDAOImpl(), new CourseDAOImpl());
    }

    public QRCreationView(UIManager uim) {
        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        qrCtrl = new QRCreationController(new PageDAOImpl(), new CourseDAOImpl());
    }


    @Override
    public void start(Stage stage) {

        //Initializing Button
        Button createQRButton = new Button("Create");
        Button createClassButton = new Button("Add Class");
        Button fileBrowserButton = new Button("Browse");

        //Initializing Temporary Text Fields Right Now
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField eID = new TextField();
        TextField classFileName = new TextField();
        TextField className = new TextField();
        TextField sectionNumber = new TextField();
        TextField studentNumber = new TextField();


        //Initializing Labels
        Label studentSectionLabel = new Label("Single Student QR Code Generation");
        Label fNameLabel = new Label("First Name: ");
        Label lNameLabel = new Label("Last Name: ");
        Label eIDLabel = new Label("e-ID: ");
        Label classNameLabel = new Label("Class Name");
        Label sectionNumberLabel = new Label("Section Number");

        Label orLabel = new Label("Or");
        Label classFileNameLabel = new Label("Class File Name: ");


        //Initializing the GridPane (Easy for organizing objects on a screen)
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(800, 600);
        gridPane.setPadding(new Insets(10,10,10,10));

        gridPane.setVgap(10);
        gridPane.setHgap(10);

        gridPane.add(studentSectionLabel, 4, 3);
        gridPane.add(firstName, 4, 5);
        gridPane.add(lastName, 4, 7);
        gridPane.add(eID, 4, 9);
        gridPane.add(studentNumber, 4, 11);
        gridPane.add(className, 4, 13);
        gridPane.add(sectionNumber, 4, 15);
        gridPane.add(createQRButton, 4, 17);

        firstName.setPromptText("First Name");
        lastName.setPromptText("Last Name");
        eID.setPromptText("eID");
        studentNumber.setPromptText("800 Number");
        className.setPromptText("Course Number");
        sectionNumber.setPromptText("Section Number");
        classFileName.setPromptText("Class Roster File Name");

        gridPane.add(orLabel, 4, 19);
        gridPane.add(classFileName, 4, 21);
        gridPane.add(fileBrowserButton, 5, 21);
        gridPane.add(createClassButton, 4, 23);

        createQRButton.requestFocus();

        final FileChooser fileChooser = new FileChooser();


        fileBrowserButton.setOnAction((event) -> {
           File file = fileChooser.showOpenDialog(mainStage);
           Path fPath = file.toPath();
           String fName = fPath.toString();
           fullFileName = fName;
           String[] splitFile = fName.split(File.separator);
           fName = splitFile[splitFile.length-1];

            if (fPath != null)
                try {
                    qrCtrl.generateStudents(fPath);
                    classFileName.setText(fName);
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO FILE NOT FOUND. How to display to user?
                }

            //TODO work on creating table to display students that have been added to roster
        });



        createClassButton.setOnAction((event) -> {
            String fName;
            if (fullFileName == null) {
                fName = classFileName.getText();
            } else {
                fName = fullFileName;
            }

            Path fPath = Paths.get(fName);
            if (fPath != null)
                try {
                    Multimap<Course, Student> courseStudentMultimap = qrCtrl.generateStudents(fPath);
                    System.out.println(courseStudentMultimap.values());
                } catch (IOException e) {
                //TODO FILE NOT FOUND. How to display to user?
                }
                //TODO work on creating table to display students that have been added to roster
        });


        Scene scene = new Scene(gridPane);

        stage.setTitle("QR-Code Generation");

        stage.setScene(scene);

        stage.show();


    }



}
