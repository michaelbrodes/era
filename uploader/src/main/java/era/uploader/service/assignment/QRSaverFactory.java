package era.uploader.service.assignment;

import com.google.common.base.Preconditions;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public class QRSaverFactory {
    public static AbstractQRSaver saver(
            AveryTemplate template,
            CountDownLatch finishedLatch,
            @Nullable Course course,
            @Nullable Student student) {
        Preconditions.checkNotNull(template);
        Preconditions.checkNotNull(finishedLatch);

        switch (template) {
            case ADDRESS_LABELS:
                return new AddressLabelSaver(finishedLatch, student, course);
            case SHIPPING_LABELS:
                return new ShippingLabelSaver(finishedLatch);
            default:
                throw new IllegalArgumentException("AveryTemplate does not exist");
        }
    }

    public static AbstractQRSaver saver(AveryTemplate template, CountDownLatch finishedLatch) {
        return saver(template, finishedLatch, null, null);
    }
}
