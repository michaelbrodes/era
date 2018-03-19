package era.uploader.controller;

import era.uploader.common.GUIUtil;
import era.uploader.common.UploaderProperties;
import era.uploader.data.viewmodel.ErrorMetaData;
import era.uploader.service.processing.ScanningProgress;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;


public class PDFProgressController {
    @FXML
    private TableView<ErrorMetaData> errorList;
    @FXML
    private ProgressBar errorProgress;
    @FXML
    private Label numPercent;
    @FXML
    private Label modeLabel;

    private ScanningProgress scanningProgress;
    private Task<Void> pipelineTask;

    @FXML
    void intialize(){
    }

    void setScanningProgress(ScanningProgress scanningProgress) {
        this.scanningProgress = scanningProgress;
        errorList.setPlaceholder(new Label("No Errors Currently Present"));
        final StringProperty percentage = new SimpleStringProperty("0%");
        numPercent.textProperty().bind(percentage);
        pipelineTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while(true){
                    updateProgress(scanningProgress.getSuccessfulProcesses(),scanningProgress.getPdfFileSize());
                    Platform.runLater((new Runnable() {
                        @Override
                        public void run() {
                            double successfulProcesses = scanningProgress.getSuccessfulProcesses();
                            double totalProcesses = scanningProgress.getPdfFileSize();
                            percentage.setValue(Integer.toString((int)(successfulProcesses/totalProcesses * 100)) + "%");
                            final ObservableList<ErrorMetaData> errorMetaData = FXCollections.observableArrayList(scanningProgress.getErrorList());
                            errorList.setItems(errorMetaData);
                        }
                    }));
                    Thread.sleep(200);


                }
            }
        };
        errorProgress.progressProperty().bind(pipelineTask.progressProperty());
        Thread checkingThread = new Thread(pipelineTask);
        checkingThread.setDaemon(false);
        checkingThread.start();

        GUIUtil.displayConnectionStatus(modeLabel);
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

    public void endAndReturn(MouseEvent mouseEvent){
        pipelineTask.cancel();
        UINavigator  ishmael = new UINavigator(errorList.getScene());
        try{
            ishmael.changeToHome();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
