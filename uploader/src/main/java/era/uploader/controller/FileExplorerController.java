package era.uploader.controller;

import com.google.common.base.Preconditions;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.viewmodel.AssignmentMetaData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The FileExplorer is the GUI component that displays all assignments in the
 * system. It basically just executes a select statement against
 * {@link era.uploader.data.database.jooq.tables.AllAssignments} and outputs
 * the result to the screen in the form of a {@link TableView} of
 * {@link AssignmentMetaData}
 */
public class FileExplorerController {
    @FXML
    private TableView<AssignmentMetaData> allAssignments;
    private static final AssignmentDAO ASSIGNMENT_DAO = AssignmentDAOImpl.instance();

    @FXML
    void initialize() {
        Preconditions.checkNotNull(allAssignments, "Table view cannot be null when injected");

        allAssignments.setRowFactory(tv -> {
            TableRow<AssignmentMetaData> row = new TableRow<>();
            // we want the user to double click to open a pdf
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && (!row.isEmpty())) {
                    AssignmentMetaData assignment = row.getItem();
                    openAssignment(assignment);
                }
            });

            row.hoverProperty().addListener((observable -> {
                row.setCursor(Cursor.HAND);
            }));

            return row;
        });
        ObservableList<AssignmentMetaData> assignments = allAssignments.getItems();
        assignments.addAll(loadFromDB());

    }

    /**
     * Instructs the desktop to open a pdf on double click
     */
    private void openAssignment(@Nonnull AssignmentMetaData assignment) {
        Preconditions.checkNotNull(assignment);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(assignment.getLocation()));
        } catch (IllegalArgumentException | IOException e) {
            Alert notFoundAlert = new Alert(Alert.AlertType.ERROR);
            notFoundAlert.setTitle("Assignment Not Found");
            notFoundAlert.setHeaderText("The assignment you were looking for doesn't exist");
            notFoundAlert.setContentText("The likely causes of this are if the file was moved or if the was deleted");
            notFoundAlert.show();
        }
    }

    /**
     * Grabs all the assignments in the system (fetches the first fifty) to be
     * displayed on the {@link #allAssignments} tableview.
     */
    private List<AssignmentMetaData> loadFromDB() {
        return ASSIGNMENT_DAO.getAllAssignments()
                .stream()
                .map(AssignmentMetaData::fromAllAssignments)
                .collect(Collectors.toList());
    }

    /**
     * Switch to the pdf scanning view.
     */
    public void scanPDF(MouseEvent mouseEvent) {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        try {
            nav.changeToScan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Switch to the class qrcreation view.
     */
    public void createCourse(MouseEvent mouseEvent) {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        try {
            nav.changeToCreateCourse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Switch to the assignment creation view.
     */
    public void createAssignment(MouseEvent mouseEvent) {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        try {
            nav.changeToCreateAssignment();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Switch to the file explorer view. Wait what...
     */
    public void home(MouseEvent mouseEvent) {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        try {
            nav.changeToHome();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
