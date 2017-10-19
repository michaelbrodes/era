package era.uploader.view;

import era.uploader.controller.QRCreationController;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.processing.StatusChangeEvent;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;

public class PDFScanView extends Application {

    private GridPane gridPane;
    private UIManager uManage;
    private Stage mainStage;


    public PDFScanView(UIManager uim, GridPane gridPane) {

        uManage = uim;
        mainStage = uManage.getPrimaryStage();
        this.gridPane = gridPane;
    }

    @Override
    public void start(Stage stage){

        //initializing
        final FileChooser fileChooser = new FileChooser();
        Button fileBrowserButton = new Button("Browse");
        Button returnHomeButton = new Button("Home");

        TextField pdfFileName = new TextField();


        //Clearing the view
        gridPane.getChildren().clear();

        gridPane.add(pdfFileName, 2, 4);
        gridPane.add(fileBrowserButton, 3, 4);
        gridPane.add(returnHomeButton, 2, 2);

        pdfFileName.setPromptText("PDF File");



        fileBrowserButton.setOnAction((event) -> {
            File file = fileChooser.showOpenDialog(stage);
            Path fPath = file.toPath();
            String fName = fPath.toString();
            String fullFileName = fName;
            String[] splitFile = fName.split(File.separator);
            fName = splitFile[splitFile.length-1];
            pdfFileName.setText(fName);
        });

        returnHomeButton.setOnAction(event -> {
            uManage.changeToHomeView(gridPane);
        });

    }

}
