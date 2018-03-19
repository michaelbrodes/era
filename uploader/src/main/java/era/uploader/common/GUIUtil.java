package era.uploader.common;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.annotation.Nullable;

public class GUIUtil {
    private static final Color GREEN = Color.web("#228b22");
    private static final Color RED = Color.web("#ff0000");
    private GUIUtil() {
        // no op
    }

    public static void displayConnectionStatus(Label modeLabel) {
        displayConnectionStatus(modeLabel, null);
    }

    public static void displayConnectionStatus(Label modeLabel, @Nullable Label offlineWarning) {
        if (UploaderProperties.instance().isUploadingEnabled()) {
            modeLabel.setText("Online");
            modeLabel.setTextFill(GREEN);

            if (offlineWarning != null) {
                offlineWarning.setVisible(false);
            }
        } else {
            modeLabel.setText("Offline");
            modeLabel.setTextFill(RED);

            if (offlineWarning != null) {
                offlineWarning.setVisible(true);
                offlineWarning.setTextFill(RED);
            }
        }
    }
}
