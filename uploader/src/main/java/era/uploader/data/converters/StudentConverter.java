package era.uploader.data.converters;

import com.google.common.base.Converter;
import era.uploader.data.database.jooq.tables.records.StudentRecord;
import era.uploader.data.model.Student;
import org.jooq.RecordMapper;

import javax.annotation.Nonnull;

public class StudentConverter
        extends Converter<StudentRecord, Student>
        implements RecordMapper<StudentRecord, Student> {
    public static final StudentConverter INSTANCE = new StudentConverter();

    private StudentConverter() {
        // no op
    }

    @Override
    protected final Student doForward(@Nonnull StudentRecord studentRecord) {
        return Student.builder()
                .withSchoolId(studentRecord.getSchoolId())
                .withFirstName(studentRecord.getFirstName())
                .withLastName(studentRecord.getLastName())
                .withUniqueId(studentRecord.getUniqueId())
                .create(studentRecord.getUsername(), studentRecord.getUuid());
    }

    @Override
    protected final StudentRecord doBackward(@Nonnull Student student) {
        StudentRecord record = new StudentRecord();
        record.setEmail(student.getEmail());
        record.setFirstName(student.getFirstName());
        record.setLastName(student.getLastName());
        record.setUsername(student.getUserName());
        record.setUniqueId(student.getUniqueId() < 1 ? null : student.getUniqueId());
        record.setUuid(student.getUuid());
        return record;
    }

    @Override
    public Student map(StudentRecord record) {
        return convert(record);
    }
}
