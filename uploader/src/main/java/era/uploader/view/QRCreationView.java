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
import javafx.scene.control.Label;


public class QRCreationView extends Application {

    private UIManager uManage;
    private Stage mainStage;

    public QRCreationView() {
        UIManager uManage = new UIManager(mainStage);
        mainStage = uManage.getPrimaryStage();
    }

    public QRCreationView(UIManager uim) {
        uManage = uim;
        mainStage = uManage.getPrimaryStage();
    }


    @Override
    public void start(Stage stage) {

        //Initializing Button
        Button createQRButton = new Button("Create");

        //Initializing Temporary Text Fields Right Now
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField eID = new TextField();


        //Initializing Labels
        Label fNameLabel = new Label("First Name: ");
        Label lNameLabel = new Label("Last Name: ");
        Label eIDLabel = new Label("e-ID: ");


        //Initializing the GridPane (Easy for organizing objects on a screen)
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(800, 600);

        gridPane.setPadding(new Insets(10,10,10,10));

        gridPane.setVgap(10);
        gridPane.setHgap(10);

        gridPane.add(fNameLabel, 4, 5);
        gridPane.add(firstName, 5, 5);
        gridPane.add(lNameLabel, 4, 7);
        gridPane.add(lastName, 5, 7);
        gridPane.add(eIDLabel, 4, 9);
        gridPane.add(eID, 5, 9);
        gridPane.add(createQRButton, 5, 11);



        Scene scene = new Scene(gridPane);

        stage.setTitle("QR-Code Generation");

        stage.setScene(scene);

        stage.show();


    }



}
