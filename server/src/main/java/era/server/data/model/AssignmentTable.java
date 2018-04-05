package era.server.data.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssignmentTable implements ViewableModel {
    private final String assignmentName;
    private final Collection<Assignment> assignmentsSubmitted;

    public AssignmentTable(String assignmentName) {
        this.assignmentName = assignmentName;
        this.assignmentsSubmitted = Lists.newArrayList();
    }
    public AssignmentTable(String assignmentName, Collection<Assignment> assignmentsSubmitted) {
        this.assignmentName = assignmentName;
        this.assignmentsSubmitted = assignmentsSubmitted;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public Collection<Assignment> getAssignmentsSubmitted() {
        return assignmentsSubmitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentTable that = (AssignmentTable) o;
        return Objects.equals(getAssignmentName(), that.getAssignmentName()) &&
                Objects.equals(getAssignmentsSubmitted(), that.getAssignmentsSubmitted());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getAssignmentName(), getAssignmentsSubmitted());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AssignmentTable{");
        sb.append("assignmentName='").append(assignmentName).append('\'');
        sb.append(", assignmentsSubmitted=").append(assignmentsSubmitted);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Map<String, Object> toViewModel() {
        List<Map<String, Object>> assignmentViewModels = Lists.newArrayList();

        for (Assignment assignment : assignmentsSubmitted) {
            assignmentViewModels.add(assignment.toViewModel());
        }

        return ImmutableMap.of(
                "assignmentName", assignmentName,
                "assignments", assignmentViewModels);
    }
}
