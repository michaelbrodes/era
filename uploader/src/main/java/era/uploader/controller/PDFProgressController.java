package era.uploader.controller;

import era.uploader.processing.ScanningProgress;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.util.Duration;


public class PDFProgressController {
    @FXML
    private TableView<ErrorMetaData> errorList;
    @FXML
    private ProgressBar errorProgress;

    private ScanningProgress scanningProgress;

    @FXML
    void intialize(){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2500),
                ae -> checkForSuccessfulProcesses()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    void setScanningProgress(ScanningProgress scanningProgress) {
        this.scanningProgress = scanningProgress;
    }

    void checkForSuccessfulProcesses(){
        if(scanningProgress == null){
            return;
        }
        else{
            double successFulProcD = (double)scanningProgress.getSuccessfulProcesses();
            double totalNumFilesD = (double)scanningProgress.getPdfFileSize();
            errorProgress.setProgress(successFulProcD/totalNumFilesD);
        }
    }
}
