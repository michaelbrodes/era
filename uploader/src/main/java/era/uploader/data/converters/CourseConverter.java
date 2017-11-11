package era.uploader.data.converters;

import com.google.common.base.Converter;
import era.uploader.data.database.jooq.tables.records.CourseRecord;
import era.uploader.data.model.Course;

import javax.annotation.Nonnull;

/**
 * A Guava converter to convert between a {@link CourseRecord} and {@link Course}
 */
public class CourseConverter extends Converter<CourseRecord, Course> {
    public static final CourseConverter INSTANCE = new CourseConverter();

    private CourseConverter() {
        // no op
    }

    @Override
    protected Course doForward(@Nonnull CourseRecord courseRecord) {
        return Course.builder()
                .withDatabaseId(courseRecord.getUniqueId())
                .withName(courseRecord.getName())
                .create(
                        courseRecord.getDepartment(),
                        courseRecord.getCourseNumber(),
                        courseRecord.getSectionNumber()
                );
    }

    @Override
    protected CourseRecord doBackward(@Nonnull Course course) {
        CourseRecord courseRecord = new CourseRecord();
        courseRecord.setUniqueId(course.getUniqueId() < 1 ? null : course.getUniqueId());
        courseRecord.setCourseNumber(course.getCourseNumber());
        courseRecord.setDepartment(course.getDepartment());
        courseRecord.setSemesterId(
                course.getSemesterObj().getUniqueId() < 1 ?
                        null :
                        course.getSemesterObj().getUniqueId()
        );
        courseRecord.setSectionNumber(course.getSectionNumber());

        return courseRecord;
    }
}
