package era.uploader;

import era.uploader.view.UIManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

        primaryStage = new Stage();
        root = new GridPane();
        mainScene = new Scene(root, 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        root.setMinSize(800, 600);
        root.setPadding(new Insets(10,10,10,10));

        root.setVgap(10);
        root.setHgap(10);

        UIManager uManage = new UIManager(primaryStage, root);

        Button qrCreateButton = new Button("Create Class");
        Button pdfUploadButton = new Button("Scan PDF");


        Label eraLabel = new Label();
        eraLabel.setText("Electronically Returned Assignments");

        root.add(eraLabel, 2, 2);
        root.add(qrCreateButton, 2, 4);
        root.add(pdfUploadButton, 3, 4);


        qrCreateButton.setOnAction(event -> {
            uManage.changeToCreateView(root);
        });

        pdfUploadButton.setOnAction(event -> {
            uManage.changeToUploadPDFView(root);
        });


    }
}
