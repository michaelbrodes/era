package era.uploader.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.QRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.Student;
import era.uploader.service.QRCreationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AssignmentCreationController {

    private static final int MAX_PAGES = 15;
    private static final QRCodeMappingDAO QR_CODE_MAPPING_DAO = QRCodeMappingDAOImpl.instance();

    @FXML
    private TextField assignmentName;

    @FXML
    private ComboBox<String> courseNamesComboBox;

    @FXML
    private ComboBox<Integer> numPagesComboBox;

    @FXML
    private Button createAssignmentButton;

    @FXML
    void initialize() {

        CourseDAO courseDAO;
        courseDAO = CourseDAOImpl.instance();

        List<Course> courses = courseDAO.getAllCourses();

        final Map<String,Course> nameToCourse = Maps.uniqueIndex(courses, (course )->{

            Preconditions.checkNotNull(course, "Course can never be null when trying to get a name");

            return course.getName() + " " + course.getSemester();

        });

        ObservableList<String> courseKeys = FXCollections.observableArrayList(nameToCourse.keySet());

        courseNamesComboBox.setItems(courseKeys);

        ObservableList<Integer> pageNums = FXCollections.observableArrayList();
        for (int i = 1; i <= MAX_PAGES; i++) {
            pageNums.add(i);
        }

        numPagesComboBox.setItems(pageNums);

        // When the user clicks 'create assignment'
        createAssignmentButton.setOnAction(event -> {

            // If the user does not enter values for each field
            if (courseNamesComboBox.getValue() == null ||
                    numPagesComboBox.getValue() == null ||
                    assignmentName.getCharacters().length() == 0) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Assignment Creation Error");
                errorAlert.setContentText("Please make sure you have filled in each field on this page.");
                errorAlert.showAndWait();
                return;
            }

            String currentCourseName = courseNamesComboBox.getValue();
            Course currentCourse = nameToCourse.get(currentCourseName);

            QRCreationService qrs = new QRCreationService(QR_CODE_MAPPING_DAO);
            Multimap<Student, QRCodeMapping> mmap = qrs.createQRs(
                    currentCourse,
                    assignmentName.getText(),
                    numPagesComboBox.getValue()
            );

            for (QRCodeMapping qrCodeMapping : mmap.values()) {
                try {
                    qrs.saveQRCodeMapping(qrCodeMapping);
                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("QRCode Save Error");
                    errorAlert.setContentText("The QRCode failed to save");
                    errorAlert.showAndWait();
                }
            };

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setHeaderText("QRCodes Saved Successfully");
            infoAlert.setContentText(
                    mmap.values().size()
                    + " QR Codes have been saved to "
                    + qrs.assignmentFileName(assignmentName.getText())
            );
            infoAlert.showAndWait();

        });

    }

    public void home() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToHome();
    }

    public void course() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToCreateCourse();
    }
    public void scanPDF() throws IOException {
        UINavigator nav = new UINavigator(assignmentName.getScene());
        nav.changeToScan();
    }

}
