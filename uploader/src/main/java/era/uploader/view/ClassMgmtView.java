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
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ClassMgmtView extends Application {

    private UIManager uManage;
    private Stage mainStage;
    private Multimap<Course, Student> courseStudentMultimap;

    public ClassMgmtView() {
        UIManager uManage = new UIManager(mainStage);
        mainStage = uManage.getPrimaryStage();
    }

    public ClassMgmtView(UIManager uim) {
        uManage = uim;
        mainStage = uManage.getPrimaryStage();
    }

    public ClassMgmtView(UIManager uim, Multimap<Course, Student> csm){
        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        courseStudentMultimap = csm;
    }

    public void start(Stage stage, GridPane gridPane) {


        gridPane.getChildren().clear();

        gridPane.setMinSize(800, 600);
        gridPane.setPadding(new Insets(10,10,10,10));

        gridPane.setVgap(10);
        gridPane.setHgap(20);

        Label studentColumnLabel = new Label("Student");
        Label eIDColumnLabel = new Label("eID");
        Label studentIDLabel = new Label("Student ID");
        Label classIDLabel = new Label("Class");


//        gridPane.add(studentColumnLabel,4, 3);
//        gridPane.add(eIDColumnLabel, 5, 3);
//        gridPane.add(studentColumnLabel, 6, 3);

        int numOfStudents = courseStudentMultimap.size();

        System.out.println(numOfStudents);

        gridPane.add(studentColumnLabel, 3, 3);
        gridPane.add(eIDColumnLabel, 4,3);
        gridPane.add(studentIDLabel, 5,3);
        gridPane.add(classIDLabel, 2, 3);

        int count = 0;
            for (Student student : courseStudentMultimap.values()) {
               String studentName =  student.getFirstName() + " " + student.getLastName();
               String schoolId = student.getSchoolId();
               String userName = student.getUserName();

               gridPane.add(new Label(studentName), 3, (4 + count));
               gridPane.add(new Label(userName), 4, (4 + count));
               gridPane.add(new Label(schoolId), 5, (4 + count));

               count++;
            }

            Iterator item = courseStudentMultimap.asMap().values().iterator();

            while(item.hasNext()){
                item.next();
            }
        }



    @Override
    public void start(Stage stage) {

        GridPane gridPane = new GridPane();

        gridPane.setMinSize(800, 600);
        gridPane.setPadding(new Insets(10,10,10,10));

        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label studentColumnLabel = new Label("Student");
        Label eIDColumnLabel = new Label("eID");
        Label studentIDLabel = new Label("Student ID");


//        gridPane.add(studentColumnLabel,4, 3);
//        gridPane.add(eIDColumnLabel, 5, 3);
//        gridPane.add(studentColumnLabel, 6, 3);

        gridPane.getChildren().clear();

        int numStudents = courseStudentMultimap.size();

        gridPane.add(studentColumnLabel, 3, 3);
        gridPane.add(eIDColumnLabel, 4,3);
        gridPane.add(studentIDLabel, 5,3);


    }


}
