package era.uploader.data.converters;

import com.google.common.base.Converter;
import era.uploader.data.database.jooq.tables.records.AssignmentRecord;
import era.uploader.data.model.Assignment;

import javax.annotation.Nonnull;

public class AssignmentConverter extends Converter<AssignmentRecord, Assignment> {
    @Override
    protected Assignment doForward(@Nonnull AssignmentRecord assignmentRecord) {
        return Assignment.builder()
                .withCourse_id(assignmentRecord.getCourseId())
                .withStudent_id(assignmentRecord.getStudentId())
                .withImageFilePath(assignmentRecord.getImageFilePath())
                .withUniqueId(assignmentRecord.getUniqueId())
                .create(assignmentRecord.getName());
    }

    @Override
    protected AssignmentRecord doBackward(@Nonnull Assignment assignment) {
        AssignmentRecord assignmentRecord = new AssignmentRecord();
        assignmentRecord.setCourseId(assignment.getCourse_id());
        assignmentRecord.setImageFilePath(assignment.getImageFilePath());
        assignmentRecord.setName(assignment.getName());
        assignmentRecord.setStudentId(assignment.getStudent_id());

        if (assignment.getUniqueId() != 0) {
            assignment.setUniqueId(assignment.getUniqueId());
        }
        return assignmentRecord;
    }
}
