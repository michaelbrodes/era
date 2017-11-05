package era.uploader.processing;

import era.uploader.common.IOUtil;
import era.uploader.controller.StatusChangeBus;
import era.uploader.view.StatusChangeObserver;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TASKalfaConverterTest {
    @Test
    public void convert_300DPIValidFile() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/300dpi.pdf"));
        StatusChangeBus bus = StatusChangeBus.instance();
        MockStatusObserver observer = new MockStatusObserver();
        bus.register(observer);

        List<String> pages = TASKalfaConverter.convertFile(pdf);

        assertEquals(5, pages.size());
        Thread.sleep(500);
        assertTrue(observer.sentMessages.isEmpty());
    }

    @Test
    public void convert_200DPIValidFile() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/200dpi.pdf"));
        StatusChangeBus bus = StatusChangeBus.instance();
        MockStatusObserver observer = new MockStatusObserver();
        bus.register(observer);

        List<String> pages = TASKalfaConverter.convertFile(pdf);

        assertEquals(5, pages.size());
        assertTrue(observer.sentMessages.isEmpty());
    }

    @Test
    public void convert_FileNotFound() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/100dpi.pdf"));
        StatusChangeBus bus = StatusChangeBus.instance();
        MockStatusObserver observer = new MockStatusObserver();
        bus.register(observer);
        boolean notFound = false;
        List<String> pages = null;

        try {
            pages = TASKalfaConverter.convertFile(pdf);
        } catch (IOException e) {
            notFound = true;
        }

        assertTrue(notFound);
        assertNull(pages);
        assertNotNull(observer.checkMessage());
    }

    @Test
    @Ignore
    public void convert_OutputSplit() throws Exception {
        Path pdf = Paths.get(IOUtil.convertToLocal("src/test/resources/test-pdfs/300dpi.pdf"));
        List<String> pages = TASKalfaConverter.convertFile(pdf);
        for (int i = 0; i < pages.size(); i++) {
            File file = new File(IOUtil.convertToLocal("src/test/resources/split/" + i + ".pdf"));
            try {
                PDDocument.load(new File(pages.get(i))).save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MockStatusObserver implements StatusChangeObserver {
        private final ConcurrentLinkedQueue<StatusChangeEvent> sentMessages =
                new ConcurrentLinkedQueue<>();
        private final Semaphore sem = new Semaphore(0);

        StatusChangeEvent checkMessage() {
            try {
                // Messages are fired asynchronously; make sure we actually
                // have a message before trying to give it to the caller
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            return sentMessages.poll();
        }


        @Override
        public void onMessage(StatusChangeEvent message) {
            sem.release();
            sentMessages.offer(message);
        }
    }

}