package era.uploader.view;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import era.uploader.common.IntegerOnlyBox;
import era.uploader.controller.QRCreationController;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;


public class QRCreationView extends Application {

    private UIManager uManage;
    private Stage mainStage;
    private QRCreationController qrCtrl;
    private String fullFileName;

//    public QRCreationView() {
//        UIManager uManage = new UIManager(mainStage);
//        mainStage = uManage.getPrimaryStage();
//        qrCtrl = new QRCreationController(new PageDAOImpl(), new CourseDAOImpl());
//    }

    public QRCreationView(UIManager uim) {
        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        qrCtrl = new QRCreationController(new PageDAOImpl(), new CourseDAOImpl());
    }


    @Override
    public void start(Stage stage) {

    }


    public void start(Stage stage, GridPane gridPane) {

        gridPane.getChildren().clear();
        ObservableList<String> terms = FXCollections.observableArrayList(
                Semester.Term.FALL.humanReadable(),
                Semester.Term.SPRING.humanReadable()
        );

        //Initializing Button
        ComboBox<String> termCombo = new ComboBox<>(terms);
        Button createQRButton = new Button("Create");
        Button createClassButton = new Button("Add Class");
        Button fileBrowserButton = new Button("Browse");
        Button goHome = new Button("Back");

        //Initializing Temporary Text Fields Right Now
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField eID = new TextField();
        TextField classFileName = new TextField();
        TextField className = new TextField();
        TextField sectionNumber = new TextField();
        TextField studentNumber = new TextField();
        IntegerOnlyBox semesterYear = new IntegerOnlyBox();

        //Initializing Labels
        Label studentSectionLabel = new Label("Single Student QR Code Generation");
        Label fNameLabel = new Label("First Name: ");
        Label lNameLabel = new Label("Last Name: ");
        Label eIDLabel = new Label("e-ID: ");
        Label classNameLabel = new Label("Class Name");
        Label sectionNumberLabel = new Label("Section Number");
        Label semesterLabel = new Label("Semester");

        Label orLabel = new Label("Or");
        Label classFileNameLabel = new Label("Class File Name: ");

//        gridPane.setMinSize(800, 600);
//        gridPane.setPadding(new Insets(10,10,10,10));
//
//        gridPane.setVgap(10);
//        gridPane.setHgap(10);

//        gridPane.add(studentSectionLabel, 4, 3);
//        gridPane.add(firstName, 4, 5);
//        gridPane.add(lastName, 4, 7);
//        gridPane.add(eID, 4, 9);
//        gridPane.add(studentNumber, 4, 11);
//        gridPane.add(className, 4, 13);
//        gridPane.add(sectionNumber, 4, 15);
//        gridPane.add(createQRButton, 4, 17);

        firstName.setPromptText("First Name");
        lastName.setPromptText("Last Name");
        eID.setPromptText("eID");
        studentNumber.setPromptText("800 Number");
        className.setPromptText("Course Number");
        sectionNumber.setPromptText("Section Number");
        classFileName.setPromptText("Class Roster File Name");
        semesterYear.setPromptText("Year");
        termCombo.setPromptText("Term");

//        gridPane.add(orLabel, 4, 19);
        gridPane.add(classFileName, 4, 4);
        gridPane.add(fileBrowserButton, 5, 4);
        gridPane.add(createClassButton, 4, 6);
        gridPane.add(goHome, 4, 2);
        gridPane.add(termCombo, 5, 2);
        gridPane.add(semesterYear, 6, 2);

        createQRButton.requestFocus();

        final FileChooser fileChooser = new FileChooser();


        fileBrowserButton.setOnAction((event) -> {
            File file = null;
            file = fileChooser.showOpenDialog(mainStage);
            if (file != null) {
                Path fPath = file.toPath();
                String fName = fPath.toString();
                fullFileName = fName;
                String[] splitFile = fName.split(File.separator);
                fName = splitFile[splitFile.length - 1];
                classFileName.setText(fName);
            }
        });

        ;
        goHome.setOnAction(event -> {
            uManage.changeToHomeView();
        });


        createClassButton.setOnAction((event) -> {
            String fName;
            Semester.Term term = null;
            Year year = null;
            if (fullFileName == null) {
                fName = classFileName.getText();
            } else {
                fName = fullFileName;
            }

            if (termCombo.getValue() != null
                    && !Strings.isNullOrEmpty(termCombo.getValue())) {
                term = Semester.Term.humanValueOf(termCombo.getValue());
            }

            Integer yearInt = semesterYear.getInt();
            if (yearInt != null) {
                year = Year.of(yearInt);
            }

            Path fPath = Paths.get(fName);
            if (fPath != null && term != null && year != null) {
                try {
                    Semester semester = new Semester(term, year);
                    Multimap<Course, Student> courseStudentMultimap =
                            qrCtrl.generateStudents(fPath, semester);
                    uManage.changeToClassMgmtView(courseStudentMultimap, gridPane);
                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File Not Found");
                    errorAlert.setContentText("The File that you specified cannot be found");
                    errorAlert.showAndWait();
                }
            }
        });


//        Scene scene = new Scene(gridPane);
//
//        stage.setTitle("QR-Code Generation");
//
//        stage.setScene(scene);
//
//        stage.show();


    }



}
