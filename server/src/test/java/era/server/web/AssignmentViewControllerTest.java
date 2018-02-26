package era.server.web;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import era.server.common.PageRenderer;
import era.server.data.AssignmentDAO;
import era.server.data.CourseDAO;
import era.server.data.model.Assignment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import spark.Request;
import spark.Response;
import spark.Session;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssignmentViewControllerTest {
    @Mock
    private PageRenderer renderer;
    @Mock
    private AssignmentDAO assignmentDAO;
    @Mock
    private CourseDAO courseDAO;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private Assignment testAss = Assignment.builder()
            .withCreatedDateTime(LocalDateTime.now())
            .withImageFilePath("test.pdf")
            .withCourseName("course")
            .create("name", "uuid");

    @Before
    public void setUp() {
        when(renderer.render(anyMap(),anyString())).thenReturn("");
        when(assignmentDAO.fetchAllByStudent("sarcher")).thenReturn(Sets.newHashSet(testAss));
        // disabled because if it is null then we should fail the test.
        //noinspection ConstantConditions
        when(assignmentDAO.fetchAllByStudent(not(eq("sarcher")))).thenReturn(Collections.emptySet());
    }

    @Test
    public void assignmentList_studentInDatabase() {
        AssignmentViewController controller = new AssignmentViewController(renderer, assignmentDAO, courseDAO);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Session session = Mockito.mock(Session.class);

        when(session.attribute("user")).thenReturn("sarcher");
        when(request.session()).thenReturn(session);
        Map<String, Object> expectedViewModel = ImmutableMap.of("assignmentList", Collections.singletonList(testAss.toViewModel()));

        controller.assignmentList(request, response);

        verify(renderer).render(expectedViewModel, "assignment-view.hbs");
    }

    @Test
    public void assignmentList_studentNotInDatabase() {
        AssignmentViewController controller = new AssignmentViewController(renderer, assignmentDAO, courseDAO);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Session session = Mockito.mock(Session.class);

        when(session.attribute("user")).thenReturn("lkane");
        when(request.session()).thenReturn(session);
        Map<String, Object> expectedViewModel = ImmutableMap.of("assignmentList", Collections.emptyList());

        controller.assignmentList(request, response);

        verify(renderer).render(expectedViewModel, "assignment-view.hbs");
    }
}