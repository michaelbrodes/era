package era.uploader.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CSVParser {
    /*
    The following constants are positions of specific Student or Course fields
    inside the CSV line
     */
    private static final int STUDENT_RECORD_SIZE = 5;
    private static final int LAST_NAME = 0;
    private static final int FIRST_NAME = 1;
    // the school user name for the student
    private static final int USER_NAME = 2;
    private static final int SCHOOL_ID = 3;
    private static final int COURSE_ID = 4;

    // the sub field COURSE_ID
    private static final int COURSE_ID_SIZE = 4;
    private static final int DEPARTMENT = 0;
    private static final int COURSE_NBR = 1;
    private static final int SECTION_NBR = 2;
    private static final int REFERENCE_NBR = 3;

    private final Semester semester;

    CSVParser(Semester semester) {
        this.semester = semester;
    }

    /**
     * Parses a single line from the CSV file and produces a singleton multimap
     * of a course to a student.
     *
     * @param fields a single line from the CSV file
     * @return Either a singleton multimap of a course to a student or null
     */
    @Nullable
    public Multimap<Course, Student> parseLine(@Nonnull String[] fields) {
        Preconditions.checkNotNull(fields);
        Multimap<Course, Student> ret = null;

        if (fields.length >= STUDENT_RECORD_SIZE) {
            String[] courseRecord = fields[COURSE_ID].split("-");
            if (courseRecord.length >= COURSE_ID_SIZE) {
                ret = ArrayListMultimap.create();
                ret.put(
                        new Course(
                                courseRecord[DEPARTMENT],
                                courseRecord[COURSE_NBR],
                                courseRecord[SECTION_NBR],
                                semester
                        ),
                        Student.builder()
                                .withLastName(fields[LAST_NAME])
                                .withFirstName(fields[FIRST_NAME])
                                .withSchoolId(fields[SCHOOL_ID])
                                .create(fields[USER_NAME])
                );
            } else {
                System.err.println("Error Parsing Course CSV");
            }
        } else {
            System.err.println("Error Parsing Course CSV");
        }

        return ret;
    }
}
