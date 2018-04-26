package era.uploader.controller;

import com.google.common.base.Preconditions;
import era.uploader.common.GUIUtil;
import era.uploader.common.UploaderProperties;
import era.uploader.data.AssignmentDAO;
import era.uploader.data.database.AssignmentDAOImpl;
import era.uploader.data.viewmodel.AssignmentMetaData;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    private Label modeLabel;

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

        GUIUtil.displayConnectionStatus(modeLabel);

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
    public void scanPDF() throws IOException {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        nav.changeToScan();
    }

    /**
     * Switch to the class qrcreation view.
     */
    public void createCourse() throws IOException {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        nav.changeToCreateCourse();
    }

    /**
     * Switch to the assignment creation view.
     */
    public void createAssignment() throws IOException {
        UINavigator nav = new UINavigator(allAssignments.getScene());
        nav.changeToCreateAssignment();
    }

    /**
     * Switch to the online version of the application
     */
    public void changeToOnline() {
        UploaderProperties.instance().setUploadingEnabled(true);
        if (UploaderProperties.instance().isUploadingEnabled()) {
            modeLabel.setText("Online");
            modeLabel.setTextFill(Color.web("#228b22"));
        }
    }

    /**
     * Switch to the offline version of the application
     */
    public void changeToOffline() {
        UploaderProperties.instance().setUploadingEnabled(false);
        if (!UploaderProperties.instance().isUploadingEnabled()) {
            modeLabel.setText("Offline");
            modeLabel.setTextFill(Color.web("#ff0000"));
        }
    }

    public void enterCredentials(MouseEvent mouseEvent) {
// Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Enter server Credentials");

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            UploaderProperties uploaderProperties = UploaderProperties.instance();
            uploaderProperties.setUser(usernamePassword.getKey());
            uploaderProperties.setPassword(usernamePassword.getValue());
        });
    }
}
