package era.uploader.processing;

import era.uploader.data.viewmodel.ErrorMetaData;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class ScanningProgress {
    private final AtomicInteger successfulProcesses = new AtomicInteger();
    private final AtomicInteger pdfFileSize = new AtomicInteger();
    private final CopyOnWriteArrayList<ErrorMetaData> errorList = new CopyOnWriteArrayList<>();

    public int getSuccessfulProcesses() {
        return successfulProcesses.get();
    }

    public int getPdfFileSize() {
        return pdfFileSize.get();
    }

    public void setPdfFileSize(int pdfFileSize) {
        this.pdfFileSize.set(pdfFileSize);
    }

    public void incrementCount() {
        this.successfulProcesses.incrementAndGet();
    }

    public CopyOnWriteArrayList<ErrorMetaData> getErrorList() {
        return errorList;
    }

    public void addError(String message, String pageNum) {
        ErrorMetaData entry = new ErrorMetaData(message, pageNum);
        errorList.add(entry);
    }

    public void addError(String message) {
        ErrorMetaData entry = new ErrorMetaData(message, null);
        errorList.add(entry);
    }

}
