package era.uploader.view;

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


    public PDFScanView(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    @Override
    public void start(Stage stage){

        //initializing
        final FileChooser fileChooser = new FileChooser();
        Button fileBrowserButton = new Button();

        TextField pdfFileName = new TextField();


        //Clearing the view
        gridPane.getChildren().clear();

        gridPane.add(pdfFileName, 2, 2);
        gridPane.add(fileBrowserButton, 3, 2);

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

    }

}
