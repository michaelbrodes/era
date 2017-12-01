package era.uploader.controller;

import javafx.beans.property.SimpleStringProperty;

public class ErrorMetaData {

    private final SimpleStringProperty message = new SimpleStringProperty("");
    private final SimpleStringProperty pageNum = new SimpleStringProperty("");

    public ErrorMetaData(String message, String pageNum){
        setMessage(message);
        setPageNum(pageNum);
    }

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getPageNum() {
        return pageNum.get();
    }

    public SimpleStringProperty pageNumProperty() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum.set(pageNum);
    }


}
