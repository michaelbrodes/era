package era.uploader.processing;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class ScanningProgress {
    private AtomicInteger successfulProcesses = new AtomicInteger();

    public int getSuccessfulProcesses() {
        return successfulProcesses.get();
    }

    public int getPdfFileSize() {
        return pdfFileSize;
    }

    private int pdfFileSize;


    public void setPdfFileSize(int pdfFileSize) {
        this.pdfFileSize = pdfFileSize;
    }

    public void incrementCount(){
        this.successfulProcesses.incrementAndGet();
    }
}
