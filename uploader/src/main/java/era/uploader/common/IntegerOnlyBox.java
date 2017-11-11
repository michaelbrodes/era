package era.uploader.common;

import javafx.scene.control.TextField;

public class IntegerOnlyBox extends TextField {

    public IntegerOnlyBox() {
        this.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d")) {
                this.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));
    }

    public Integer getInt() {
        try {
            return Integer.parseInt(this.getText());
        } catch (NumberFormatException e) {
            // technically only possible if the text is empty, but just in case
            return null;
        }
    }
}
