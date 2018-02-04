package era.uploader.service.assignment;

import com.google.common.base.Preconditions;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CountDownLatch;

/**
 * {@link AbstractQRSaver} defines a <em>general</em> strategy for saving QR
 * Codes. {@link AddressLabelSaver} and {@link ShippingLabelSaver} are the
 * specific <em>instances</em> of that strategy. The user can pick the saving
 * strategy of choice using a specific {@link AveryTemplate} which contains
 * the name and description of each strategy. Call
 * {@link #saver(AveryTemplate, CountDownLatch, Course, Student)} to get your
 * strategy of choice.
 */
@ParametersAreNonnullByDefault
public class QRSaverFactory {
    /**
     * Creates a new {@link AbstractQRSaver} based off the provided
     * {@link AveryTemplate}
     */
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

    /**
     * Creates a new {@link AbstractQRSaver} based off the provided
     * {@link AveryTemplate}
     *
     * @deprecated use {@link #saver(AveryTemplate, CountDownLatch, Course, Student)}
     */
    @Deprecated
    public static AbstractQRSaver saver(AveryTemplate template, CountDownLatch finishedLatch) {
        return saver(template, finishedLatch, null, null);
    }
}
