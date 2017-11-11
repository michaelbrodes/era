package era.uploader.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class UploadView extends Application {

    private UIManager uManage;
    private Stage mainStage;

    public UploadView(UIManager uiManager) {
        uManage = uiManager;
        mainStage = uManage.getPrimaryStage();
    }

//    public UploadView() {
//        UIManager uManage = new UIManager(mainStage);
//        mainStage = uManage.getPrimaryStage();
//    }

    @Override
    public void start(Stage stage) {

    }
}
