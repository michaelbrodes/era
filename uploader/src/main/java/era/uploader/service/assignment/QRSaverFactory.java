package era.uploader.service.assignment;

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
}
