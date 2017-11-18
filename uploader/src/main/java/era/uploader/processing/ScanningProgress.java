package era.uploader.processing;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class ScanningProgress {
    private AtomicInteger count = new AtomicInteger();
    private int pdfFileSize;


    public void setPdfFileSize(int pdfFileSize) {
        this.pdfFileSize = pdfFileSize;
    }

    public void incrementCount(){
        this.count.incrementAndGet();
    }
}
