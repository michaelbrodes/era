package era.uploader.controller;

import era.uploader.processing.ScanningProgress;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import org.jooq.True;


public class PDFProgressController {
    @FXML
    private TableView<ErrorMetaData> errorList;
    @FXML
    private ProgressBar errorProgress;

    private ScanningProgress scanningProgress;

    @FXML
    void intialize(){

    }
    void setScanningProgress(ScanningProgress scanningProgress) {
        this.scanningProgress = scanningProgress;
        Task<Void> pipelineTask = new Task() {
            @Override
            protected Void call() throws Exception {
                while(true){
                    updateProgress(scanningProgress.getSuccessfulProcesses(),scanningProgress.getPdfFileSize());
                    Thread.sleep(2500);
                }
            }
        };
        errorProgress.progressProperty().bind(pipelineTask.progressProperty());

        Thread checkingThread = new Thread(pipelineTask);
        checkingThread.setDaemon(false);
        checkingThread.start();
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
