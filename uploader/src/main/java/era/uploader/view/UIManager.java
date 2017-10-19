package era.uploader.view;

import com.google.common.collect.Multimap;
import era.uploader.UploaderApp;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UIManager extends Application {

    //initializing the Manager's instances of the GUI Classes
    private QRCreationView createView;
    private UploadView uploadView;
    private UploaderApp mainWindow;
    private ClassMgmtView classView;
    private PDFScanView pdfScanView;
    private Stage primaryStage;
    public GridPane root;
    public Scene mainScene;

    public UIManager(Stage pStage, GridPane gridPane) {

        createView = new QRCreationView(this);
        uploadView = new UploadView(this);
        primaryStage = pStage;

        root = gridPane;
        //mainScene = new Scene(root, 800, 600);
        //primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    public Scene getScene() {
        return this.mainScene;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;
        mainWindow.start(primaryStage);

    }

    public void changeToCreateView(GridPane gridPane) {

        //this is changing to the QRCreationView
        createView.start(primaryStage, gridPane);
        primaryStage.show();
    }

    public void changeToClassMgmtView(Multimap<Course, Student> courseStudentMultimap, GridPane gridPane) {

        //Changing to the ClassMgmtView()
//        classView = new ClassMgmtView(this, courseStudentMultimap);
//        GridPane secGrid = new GridPane();
//        Scene secondScene = new Scene(secGrid, 600, 800);
//        Stage secStage = new Stage();
//        classView.start(secStage);

        classView = new ClassMgmtView(this, courseStudentMultimap);
        classView.start(primaryStage, gridPane);
        primaryStage.show();
    }

    public void changeToUploadPDFView(GridPane gridPane) {

        pdfScanView = new PDFScanView(this, gridPane);
        pdfScanView.start(primaryStage);
        primaryStage.show();

    }

    public void changeToHomeView(GridPane gridPane) {
        mainWindow = new UploaderApp();
        mainWindow.start(primaryStage);
        primaryStage.show();
    }

}
