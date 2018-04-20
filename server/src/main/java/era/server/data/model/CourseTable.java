package era.server.data.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.*;

public class CourseTable implements ViewableModel {
    private final String courseName;
    private final List<AssignmentTable> assignmentsInCourse;

    public CourseTable(String courseName, List<AssignmentTable> assignmentsInCourse) {
        this.courseName = courseName;
        this.assignmentsInCourse = assignmentsInCourse;
    }

    public static List<CourseTable> fromAssignmentGroupings(Map<String, List<Assignment>> assignmentsByCourse) {
        List<CourseTable> tables = Lists.newArrayList();

        for (Map.Entry<String, List<Assignment>> course : assignmentsByCourse.entrySet()) {
            CourseTable table = new CourseTable(course.getKey(), Lists.newArrayList());
            for (Assignment assignment : course.getValue()) {
                table.addDistinctAssignment(assignment);
            }
            tables.add(table);
        }

        return tables;
    }

    private void addDistinctAssignment(Assignment assignment) {
        AssignmentTable foundTable = findAssignmentTable(assignment);

        if (foundTable != null) {
            foundTable.getAssignmentsSubmitted().add(assignment);
        } else {
            AssignmentTable newTable = new AssignmentTable(assignment.getName(), Lists.newArrayList(assignment));
            this.assignmentsInCourse.add(newTable);
        }
    }

    @Nullable
    private AssignmentTable findAssignmentTable(Assignment assignment) {
        for (AssignmentTable assignmentGrouping : this.assignmentsInCourse) {
            if (assignment.getName().equals(assignmentGrouping.getAssignmentName())) {
                return assignmentGrouping;
            }
        }

        return null;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<AssignmentTable> getAssignmentsInCourse() {
        return assignmentsInCourse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseTable that = (CourseTable) o;
        return Objects.equals(getCourseName(), that.getCourseName()) &&
                Objects.equals(getAssignmentsInCourse(), that.getAssignmentsInCourse());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCourseName(), getAssignmentsInCourse());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CourseTable{");
        sb.append("courseName='").append(courseName).append('\'');
        sb.append(", assignmentsInCourse=").append(assignmentsInCourse);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Map<String, Object> toViewModel() {
        List<Map<String, Object>> assignmentTableViewModels = Lists.newArrayList();
        for (AssignmentTable table : assignmentsInCourse) {
            assignmentTableViewModels.add(table.toViewModel());
        }

        return ImmutableMap.of("course", courseName, "assignmentTables", assignmentTableViewModels);
    }
}
