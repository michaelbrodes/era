package era.uploader.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import era.uploader.common.UploaderProperties;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.QRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.viewmodel.AssignmentPrintoutMetaData;
import era.uploader.service.QRCreationService;
import era.uploader.service.assignment.AveryTemplate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.EnumSet;
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
    private ComboBox<String> averyTemplateComboBox;

    @FXML
    private Button createAssignmentButton;

    @FXML
    private Label modeLabel;
    @FXML
    private TableView<AssignmentPrintoutMetaData> assignmentList;

    @FXML
    private Button createQRCodePageButton;

    @FXML
    void initialize() {

        CourseDAO courseDAO;
        courseDAO = CourseDAOImpl.instance();

        List<Course> courses = courseDAO.getAllCourses();

        final Map<String, Course> nameToCourse = Maps.uniqueIndex(courses, (course) -> {

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

        EnumSet<AveryTemplate> templates = EnumSet.allOf(AveryTemplate.class);
        final Map<String, AveryTemplate> templateDescriptions = Maps.uniqueIndex(templates, AveryTemplate::description);
        ObservableList<String> descriptions = FXCollections.observableArrayList(templateDescriptions.keySet());
        averyTemplateComboBox.setItems(descriptions);

        QRCreationService qrs = new QRCreationService(QR_CODE_MAPPING_DAO);


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

            AssignmentPrintoutMetaData apmd = new AssignmentPrintoutMetaData(assignmentName.getText(), numPagesComboBox.getValue(), currentCourse);
            assignmentList.getItems().add(apmd);

        });

        createQRCodePageButton.setOnAction(event -> {

            // If the user does not enter values for each field
            if (assignmentList.getItems().size() == 0) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Assignment Creation Error");
                errorAlert.setContentText("Please make sure you have created at least one assignment.");
                errorAlert.showAndWait();
                return;
            }
            AveryTemplate template = templateDescriptions.get(averyTemplateComboBox.getValue());
            qrs.printAndSaveQRCodes(assignmentList.getItems(), template);
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setHeaderText("QRCodes Saved Successfully");
            infoAlert.setContentText("QRCode PDFs saved to " + qrs.ASSIGNMENTS_DIR);
            infoAlert.showAndWait();
        });
//
//            String currentCourseName = courseNamesComboBox.getValue();
//            Course currentCourse = nameToCourse.get(currentCourseName);
//            AveryTemplate template = templateDescriptions.get(averyTemplateComboBox.getValue());
//
//            QRCreationService qrs = new QRCreationService(QR_CODE_MAPPING_DAO);
//            Collection<QRCodeMapping> qrCodesForStudents = qrs.createQRs(
//                    currentCourse,
//                    assignmentName.getText(),
//                    numPagesComboBox.getValue(),
//                    template
//            );
//
//            for (QRCodeMapping qrCodeMapping : qrCodesForStudents) {
//                try {
//                    // this is a deprecated method but is still used to make it easier to create test documents.
//                    // in the future this will no longer be here.
//                    qrs.saveQRCodeMapping(qrCodeMapping);
//                } catch (IOException e) {
//                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//                    errorAlert.setHeaderText("QRCode Save Error");
//                    errorAlert.setContentText("The QRCode failed to save");
//                    errorAlert.showAndWait();
//                }
//            }
//
//            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
//            infoAlert.setHeaderText("QRCodes Saved Successfully");
//            infoAlert.setContentText(
//                    qrCodesForStudents.size()
//                            + " QR Codes have been saved to "
//                            + qrs.studentFileName(assignmentName.getText())
//            );
//            infoAlert.showAndWait();
//
//        });
//

        if (UploaderProperties.instance().isUploadingEnabled()) {
            modeLabel.setText("Online");
            modeLabel.setTextFill(Color.web("#228b22"));
        } else {
            modeLabel.setText("Offline");
            modeLabel.setTextFill(Color.web("#ff0000"));
        }
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
