package era.uploader.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class UINavigator {

    private static final int GUI_WIDTH = 1000;
    private static final int GUI_HEIGHT = 600;

    private Scene mainScene;
    private Window primaryWindow;

    public UINavigator(Scene scene){

        this.mainScene = scene;
        this.primaryWindow = scene.getWindow();

        this.primaryWindow.setWidth(GUI_WIDTH);
        this.primaryWindow.setHeight(GUI_HEIGHT);

    }

    public void changeToHome() throws IOException {

        Parent homeView = FXMLLoader.load(getClass().getResource("/gui/file-explorer.fxml"));

        mainScene.setRoot(homeView);

    }

    public void changeToScan() throws IOException {

        Parent scanView = FXMLLoader.load(getClass().getResource("/gui/scan-pdf.fxml"));

        mainScene.setRoot(scanView);

    }

    public void changeToCreateAssignment() throws IOException {

        Parent createAssignmentView = FXMLLoader.load(getClass().getResource("/gui/assignment-creation.fxml"));

        mainScene.setRoot(createAssignmentView);

    }

    public void changeToCreateCourse() throws IOException {

        Parent createCourseView = FXMLLoader.load(getClass().getResource("/gui/course-creation.fxml"));

        mainScene.setRoot(createCourseView);

    }

    public Window getPrimaryStage() {
        return primaryWindow;
    }

    }
}
