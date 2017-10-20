package era.uploader;

import era.uploader.view.UIManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UploaderApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    public GridPane root;
    public Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        root = new GridPane();
        mainScene = new Scene(root, 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        UIManager uManage = new UIManager(primaryStage);

        uManage.changeToCreateView();


    }
}
