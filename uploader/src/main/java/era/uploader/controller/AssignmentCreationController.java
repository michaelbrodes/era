package era.uploader.controller;

import com.google.common.collect.Maps;
import era.uploader.common.GUIUtil;
import era.uploader.data.CourseDAO;
import era.uploader.data.QRCodeMappingDAO;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.QRCodeMappingDAOImpl;
import era.uploader.data.model.Course;
import era.uploader.data.viewmodel.AssignmentPrintoutMetaData;
import era.uploader.service.AssignmentCreationService;
import era.uploader.service.assignment.AveryTemplate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private Map<String, List<Course>> sectionsGroupedByTeacher;
    private Map<String, AveryTemplate> descriptionToTemplate;
    private AssignmentCreationService service;

    @FXML
    void initialize() {

        CourseDAO courseDAO;
        courseDAO = CourseDAOImpl.instance();

        List<Course> courses = courseDAO.getAllCourses();
        sectionsGroupedByTeacher = groupSectionsByTeacher(courses);
        ObservableList<String> courseKeys = FXCollections
                .observableArrayList(sectionsGroupedByTeacher.keySet());
        courseNamesComboBox.setItems(courseKeys);

        ObservableList<Integer> pageNums = FXCollections.observableArrayList();
        for (int i = 1; i <= MAX_PAGES; i++) {
            pageNums.add(i);
        }
        numPagesComboBox.setItems(pageNums);

        EnumSet<AveryTemplate> templates = EnumSet.allOf(AveryTemplate.class);
        descriptionToTemplate = Maps.uniqueIndex(templates, AveryTemplate::description);
        ObservableList<String> descriptions = FXCollections
                .observableArrayList(descriptionToTemplate.keySet());
        averyTemplateComboBox.setItems(descriptions);

        service = new AssignmentCreationService(QR_CODE_MAPPING_DAO);

        createAssignmentButton.setOnAction(this::addAssignmentToTable);
        createQRCodePageButton.setOnAction(event -> {

            // If the user does not enter values for each field
            if (assignmentList.getItems().size() == 0) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Assignment Creation Error");
                errorAlert.setContentText("Please make sure you have created at least one assignment.");
                errorAlert.showAndWait();
                return;
            }
            AveryTemplate template = descriptionToTemplate.get(averyTemplateComboBox.getValue());
            service.printAndSaveQRCodes(assignmentList.getItems(), template);

            Path relativeAssignmentDir = Paths.get(AssignmentCreationService.ASSIGNMENTS_DIR);
            Path absoluteAssignmentDir = relativeAssignmentDir.toAbsolutePath();

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setHeaderText("QRCodes Saved Successfully");
            infoAlert.setContentText("QRCode PDFs saved to " + absoluteAssignmentDir.toString());
            infoAlert.showAndWait();
        });
        GUIUtil.displayConnectionStatus(modeLabel);
//
//            String currentCourseName = courseNamesComboBox.getValue();
//            Course currentCourse = nameToCourse.get(currentCourseName);
//            AveryTemplate template = templateDescriptions.get(averyTemplateComboBox.getValue());
//
//            AssignmentCreationService qrs = new AssignmentCreationService(QR_CODE_MAPPING_DAO);
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

    }

    /**
     * Adds assignments to the table at the right hand side of the table.
     *
     * This GUI allows for the creation of multiple assignments across multiple
     * courses, all at one time. We allow for this by dividing the Assignment
     * Creation GUI into two sections: one for inputting the metadata related
     * to a single assignment and one for displaying all assignments created
     * thus far.
     */
    @SuppressWarnings("unused")
    private void addAssignmentToTable(ActionEvent actionEvent) {
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
        List<Course> currentCourses = sectionsGroupedByTeacher.get(currentCourseName);

        for (Course course : currentCourses) {
            AssignmentPrintoutMetaData apmd = new AssignmentPrintoutMetaData(assignmentName.getText(), numPagesComboBox.getValue(), course);
            assignmentList.getItems().add(apmd);
        }
    }


    /**
     * Dr. Jones would like to have each professor's version of a course
     * displayed as a separate item in the courses dropdown. For example, if he
     * is teaching a CHEM-121a course this semester and Dr. Holovics is also
     * teaching that same course, we would have two drop down items, one
     * stating "Dr. Jones's CHEM-121a" and one stating
     * "Dr. Holovics's CHEM-121a"
     *
     * @param courses every section of every course in the database
     * @return a mapping of a teacher and a course to the course's many
     * sections
     */
    private Map<String, List<Course>> groupSectionsByTeacher(List<Course> courses) {
        Map<String, List<Course>> groupings = Maps.newHashMap();
        for (Course course : courses) {
            String teacherAndCourse = course.getTeacher().getName()
                    + "'s "
                    + course.getDepartment()
                    + "-"
                    + course.getCourseNumber();

            groupings.computeIfAbsent(teacherAndCourse, (k) -> new ArrayList<>())
                    .add(course);
        }

        return groupings;
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
