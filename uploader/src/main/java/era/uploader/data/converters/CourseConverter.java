package era.uploader.data.converters;

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.model.Course;
import org.jooq.RecordMapper;

import javax.annotation.Nonnull;

/**
 * A Guava converter to convert between a {@link CourseRecord} and {@link Course}
 */
public class CourseConverter
        extends Converter<CourseRecord, Course>
        implements RecordMapper<CourseRecord, Course> {
    public static final CourseConverter INSTANCE = new CourseConverter();

    private CourseConverter() {
        // no op
    }

    @Override
    protected final Course doForward(@Nonnull CourseRecord courseRecord) {
        Preconditions.checkNotNull(courseRecord);
        return Course.builder()
                .withDatabaseId(courseRecord.getUniqueId())
                .withName(courseRecord.getName())
                .withSemesterId(courseRecord.getSemesterId())
                .withTeacherId(courseRecord.getTeacherId())
                .create(
                        courseRecord.getDepartment(),
                        courseRecord.getCourseNumber(),
                        courseRecord.getSectionNumber(),
                        courseRecord.getUuid()
                );
    }

    @Override
    protected final CourseRecord doBackward(@Nonnull Course course) {
        Preconditions.checkNotNull(course);
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setUniqueId(course.getUniqueId() < 1 ? null : course.getUniqueId());
        courseRecord.setCourseNumber(course.getCourseNumber());
        courseRecord.setDepartment(course.getDepartment());

        if (course.getSemester() == null) {
            courseRecord.setSemesterId(course.getSemesterId());
        } else {
            courseRecord.setSemesterId(
                    course.getSemester().getUniqueId() < 1 ?
                            null :
                            course.getSemester().getUniqueId()
            );
        }

        if (course.getTeacher() == null) {
            courseRecord.setTeacherId(course.getTeacherId());
        } else {
            courseRecord.setTeacherId(course.getTeacher().getUniqueId());
        }
        courseRecord.setSectionNumber(course.getSectionNumber());
        courseRecord.setUuid(course.getUuid());

        return courseRecord;
    }

    @Override
    public Course map(CourseRecord record) {
        return convert(record);
    }
}
