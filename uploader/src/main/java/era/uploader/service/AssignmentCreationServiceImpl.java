package era.uploader.service;

import era.uploader.data.model.Assignment;
import era.uploader.data.model.Course;
import era.uploader.data.model.QRCodeMapping;
import era.uploader.service.assignment.AveryTemplate;

import javax.annotation.Nonnull;
import java.util.List;

public class AssignmentCreationServiceImpl implements AssignmentCreationService {
    @Override
    public List<QRCodeMapping> generateQRsForAssignments(@Nonnull List<Assignment> assignments, @Nonnull Course course, @Nonnull AveryTemplate pdfTemplate) {
        return null;
    }
}
