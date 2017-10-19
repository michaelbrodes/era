package era.uploader.view;

import com.google.common.collect.Multimap;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.Map;


//TODO add functionality for modifying the data that is displayed on this screen.

public class ClassMgmtView extends Application {

    private UIManager uManage;
    private Stage mainStage;
    private Multimap<Course, Student> courseStudentMultimap;

//    public ClassMgmtView() {
//        UIManager uManage = new UIManager(mainStage);
//        mainStage = uManage.getPrimaryStage();
//    }

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
        Label classIDLabel = new Label("Course");


//        gridPane.add(studentColumnLabel,4, 3);
//        gridPane.add(eIDColumnLabel, 5, 3);
//        gridPane.add(studentColumnLabel, 6, 3);

        int numOfStudents = courseStudentMultimap.size();

        System.out.println(numOfStudents);

        gridPane.add(studentColumnLabel, 3, 3);
        gridPane.add(eIDColumnLabel, 4,3);
        gridPane.add(studentIDLabel, 5,3);
        gridPane.add(classIDLabel, 2, 3);

        int count = 4;
        for (Map.Entry<Course, Collection<Student>> courseToStudents :
                courseStudentMultimap.asMap().entrySet()) {
            String course = courseToStudents.getKey().getDepartment() + ""
                    + courseToStudents.getKey().getCourseNumber() + "-"
                    + courseToStudents.getKey().getSectionNumber();
            gridPane.add(new Label(course), 2, count);
            for (Student student : courseToStudents.getValue()) {
                //System.out.println("\tStudent: " + student.getUserName());
                gridPane.add(new Label(student.getFirstName() + " "
                            + student.getLastName()), 3, count);
                gridPane.add(new Label(student.getUserName()), 4, count);
                gridPane.add(new Label(student.getSchoolId()), 5, count);
                count++;
            }
            count += 2;
        }

//        for (Student student : courseStudentMultimap.values()) {
//            String studentName =  student.getFirstName() + " " + student.getLastName();
//            String schoolId = student.getSchoolId();
//            String userName = student.getUserName();
//            Set<Course> courses = student.getCourses();
//            String studentCourse = "";
//
//            if (courses != null) {
//                while (courses.iterator().hasNext()) {
//                    for (Course course : courses) {
//                        studentCourse += course.getDepartment() + "-" +
//                                course.getCourseNumber() + "-" +
//                                course.getSectionNumber();
//                        if (courses.iterator().hasNext()) {
//                            studentCourse += ", ";
//                        }
//                    }
//                }
//                gridPane.add(new Label(studentCourse), 2, (4 + count));
//
//            }
//            gridPane.add(new Label(studentName), 3, (4 + count));
//            gridPane.add(new Label(userName), 4, (4 + count));
//            gridPane.add(new Label(schoolId), 5, (4 + count));
//
//            count++;
//        }

        Button homeButton = new Button("Back to Home");

        gridPane.add(homeButton, 2, (++count));

        homeButton.setOnAction((event) -> {
            uManage.changeToCreateView(gridPane);
        });

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
