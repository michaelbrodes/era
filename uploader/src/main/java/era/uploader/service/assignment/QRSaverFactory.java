package era.uploader.service.assignment;

import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.util.concurrent.CountDownLatch;

public class QRSaverFactory {
    public static AbstractQRSaver saver(AveryTemplate template, CountDownLatch finishedLatch) {
        switch (template) {
            case ADDRESS_LABELS:
                return new AddressLabelSaver(finishedLatch);
            case SHIPPING_LABELS:
                return new ShippingLabelSaver(finishedLatch);
            default:
                throw new IllegalArgumentException("AveryTemplate does not exist");
        }
    }

    public static AbstractQRSaver saver(AveryTemplate template, CountDownLatch finishedLatch, Student student) {
        switch (template) {
            case ADDRESS_LABELS:
                return new AddressLabelSaver(finishedLatch, student, null);
            case SHIPPING_LABELS:
                return new ShippingLabelSaver(finishedLatch, student, null);
            default:
                throw new IllegalArgumentException("AveryTemplate does not exist");
        }
    }
}
