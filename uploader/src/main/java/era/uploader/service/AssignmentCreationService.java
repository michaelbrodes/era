package era.uploader.service;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.service.assignment.AveryTemplate;
import era.uploader.service.assignment.AbstractQRSaver;
import era.uploader.service.assignment.AddressLabelSaver;
import era.uploader.service.assignment.QRCreator;
import era.uploader.service.assignment.QRSaverFactory;
import era.uploader.service.assignment.ShippingLabelSaver;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface AssignmentCreationService {
    /**
     * In order to match students to their assignments we create QR codes that
     * should be attached to those assignments. In order to allow Graders to
     * put in assignment pages out of order, we generate a unique QR code for
     * each page of an assignment.
     *
     * This method takes in a list of assignments that are part of a course
     * and generates qr codes for every page of every assignment for every
     * student in that. These QR codes are then printed out into a PDF in a
     * format that allows them to be printed on Avery Labels. We support
     * templates of 2" by 4" shipping labels and of 1" by 2.625" address
     * labels.
     *
     * Each QR code encodes an "Universally Unique ID" {@link UUID} that is
     * unique per page. UUIDs prevent students from easily reverse engineering
     * who has what QR code. Each QR code will be saved in our database as a
     * {@link QRCodeMapping} row. QRCodeMappings store the QR code's UUID, the
     * student associated with it, and the page number that the QR code was put
     * on.
     *
     * NOTE: Despite what it may seem, we actually don't create Assignments in
     * this method. We will eventually create assignments when we implement the
     * "Multi-page Ordering" feature of this project.
     *
     * @param assignments all assignments (and their number of pages) we have
     *                    to create QR codes for.
     * @param course the course that contains the students we want to map QR
     *               codes to
     * @param pdfTemplate the type of Avery Label template we want to save QR
     *                    codes on. This will allow us to determine if we want
     *                    an {@link AddressLabelSaver}
     *                    or a {@link ShippingLabelSaver}
     *                    strategy at runtime. Strategies are instantiated
     *                    using {@link QRSaverFactory}
     * @return the QR code -> student mappings for each page of every
     * assignment in the "assignments" parameter.
     * @see QRCreator for the QR code creation logic
     * @see AbstractQRSaver for the general strategy
     * of saving QR codes.
     * @see era.uploader.service.assignment for all classes used in this process
     */
    List<QRCodeMapping> generateQRsForAssignments(
            @Nonnull List<Assignment> assignments,
            @Nonnull Course course,
            @Nonnull AveryTemplate pdfTemplate
    );
}
