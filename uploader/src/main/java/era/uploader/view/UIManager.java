package era.uploader.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import era.uploader.UploaderApp;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class UIManager extends Application {

    //initializing the Manager's instances of the GUI Classes
    private QRCreationView createView;
    private UploadView uploadView;
    private UploaderApp mainWindow;
    private Stage primaryStage;
    public GridPane root;
    public Scene mainScene;

    public UIManager(Stage pStage) {

        createView = new QRCreationView(this);
        uploadView = new UploadView(this);
        primaryStage = pStage;

        root = new GridPane();
        mainScene = new Scene(root, 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    public Scene getScene() {
        return this.mainScene;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;
        mainWindow.start(primaryStage);

    }

    public void changeToCreateView() {

        //this is changing to the QRCreationView
        createView.start(primaryStage);
        primaryStage.show();
    }

}
