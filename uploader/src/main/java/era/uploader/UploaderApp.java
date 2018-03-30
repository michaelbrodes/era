package era.uploader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UploaderApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // needed by PDFBox
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        // if Class#getResource cannot resolve the resource it will throw an NPE
        // The resource is guessed to be at /src/main/resources/gui/file-explorer.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/gui/file-explorer.fxml"));
        Scene mainScene = new Scene(root, 997, 597);

        primaryStage.setScene(mainScene);
        primaryStage.show();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutting down...")));
    }
}
