package era.uploader.service.coursecreation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Teacher;

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
    private static final int COURSE_ID_SIZE = 3;
    private static final int DEPARTMENT = 0;
    private static final int COURSE_NBR = 1;
    private static final int SECTION_NBR = 2;

    /**
     * Parses a single line from the CSV file and produces a singleton multimap
     * of a course to a student.
     *
     * @param fields a single line from the CSV file
     * @return Either a singleton multimap of a course to a student or null
     */
    @Nonnull
    public ParsedLine parseLine(@Nonnull String[] fields) {
        Preconditions.checkNotNull(fields);
        ParsedLine.Builder lineBuilder = ParsedLine.builder();

        if (fields.length >= STUDENT_RECORD_SIZE) {
            lineBuilder.firstName(fields[FIRST_NAME])
                    .lastName(fields[LAST_NAME])
                    .studentId(fields[SCHOOL_ID])
                    .userName(fields[USER_NAME]);

            String[] courseRecord = fields[COURSE_ID].split("-");
            if (courseRecord.length >= COURSE_ID_SIZE) {
                lineBuilder.courseDepartment(courseRecord[DEPARTMENT])
                        .courseNumber(courseRecord[COURSE_NBR])
                        .courseSection(courseRecord[SECTION_NBR]);
            } else {
                System.err.println("Malformed Course entry in roster CSV");
                System.err.printf("%s\n", String.join(",", courseRecord));
            }
        } else {
            System.err.println("Malformed student entry in roster CSV");
            System.err.printf("%s\n", String.join(",", String.join(",", fields)));
        }

        return lineBuilder.create();
    }
}
