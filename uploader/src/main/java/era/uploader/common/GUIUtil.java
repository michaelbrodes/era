package era.uploader.common;

import com.google.common.collect.Maps;
import era.uploader.data.model.Course;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GUIUtil {
    private static final Color GREEN = Color.web("#228b22");
    private static final Color RED = Color.web("#ff0000");
    private GUIUtil() {
        // no op
    }

    public static void displayConnectionStatus(Label modeLabel) {
        displayConnectionStatus(modeLabel, null);
    }

    public static void displayConnectionStatus(Label modeLabel, @Nullable Label offlineWarning) {
        if (UploaderProperties.instance().isUploadingEnabled()) {
            modeLabel.setText("Online");
            modeLabel.setTextFill(GREEN);

            if (offlineWarning != null) {
                offlineWarning.setVisible(false);
            }
        } else {
            modeLabel.setText("Offline");
            modeLabel.setTextFill(RED);

            if (offlineWarning != null) {
                offlineWarning.setVisible(true);
                offlineWarning.setTextFill(RED);
            }
        }
    }

    /**
     * Dr. Jones would like to have each professor's version of a course
     * displayed as a separate item in the courses dropdown. For example, if he
     * is teaching a CHEM-121a course this semester and Dr. Holovics is also
     * teaching that same course, we would have two drop down items, one
     * stating "Dr. Jones's CHEM-121a" and one stating
     * "Dr. Holovics's CHEM-121a"
     *
     * @param courses every section of every course in the database
     * @return a mapping of a teacher and a course to the course's many
     * sections
     */
    public static Map<String, List<Course>> groupSectionsByTeacher(Collection<Course> courses) {
        Map<String, List<Course>> groupings = Maps.newHashMap();
        for (Course course : courses) {
            String teacherAndCourse = course.getTeacher().getName()
                    + "'s "
                    + course.getDepartment()
                    + "-"
                    + course.getCourseNumber()
                    + " "
                    + course.getSemester().getTerm().humanReadable()
                    + " "
                    + course.getSemester().getYear();

            groupings.computeIfAbsent(teacherAndCourse, (k) -> new ArrayList<>())
                    .add(course);
        }

        return groupings;
    }
}
