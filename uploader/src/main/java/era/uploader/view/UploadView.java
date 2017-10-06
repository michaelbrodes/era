package era.uploader.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import era.uploader.view.UIManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class UploadView extends Application {

    private UIManager uManage;
    private Stage mainStage;

    public UploadView(UIManager uiManager) {
        uManage = uiManager;
        mainStage = uManage.getPrimaryStage();
    }

    public UploadView() {
        UIManager uManage = new UIManager(mainStage);
        mainStage = uManage.getPrimaryStage();
    }

    @Override
    public void start(Stage stage) {

    }
}
