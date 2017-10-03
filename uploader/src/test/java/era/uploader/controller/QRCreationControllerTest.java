package era.uploader.controller;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import era.uploader.data.CourseDAO;
import era.uploader.data.PageDAO;
import era.uploader.data.database.CourseDAOImpl;
import era.uploader.data.database.PageDAOImpl;
import era.uploader.data.model.Page;
import era.uploader.data.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QRCreationControllerTest {
    private QRCreationController ctrl;

    @Before
    public void setUp() {
        Set<Page> db = Sets.newHashSet();
        PageDAO pageDAO = mock(PageDAOImpl.class);
        CourseDAO courseDAO = mock(CourseDAOImpl.class);
        when(pageDAO.getDb()).thenReturn(db);
        ctrl = new QRCreationController(pageDAO, courseDAO);
    }

    @Test
    public void createQRs_SingletonSet() throws Exception {
        Student robMcGuy = Student.create()
                .withFirstName("Rob")
                .withLastName("Mcguy")
                .withSchoolId("rmcguy")
                .build();
        robMcGuy.setSchoolId("rmcguy");
        int numberOfAssignments = 2;
        ImmutableSet<Student> students = ImmutableSet.of(robMcGuy);
        QRErrorBus bus = QRErrorBus.instance();

        Multimap<Student, Page> qRs = ctrl.createQRs(students, numberOfAssignments);
        Collection<Page> values = qRs.values();

        assertFalse(values.isEmpty());
        assertTrue(bus.isEmpty());
        assertEquals(2, values.size());
        int i = 0;
        for (Page page : values) {
            assertEquals(i, page.getSequenceNumber());
            assertEquals(robMcGuy, page.getStudent());
            assertNotNull(page.getQrCode());
            i++;
        }
    }

    @Test
    public void createQRs_EmptySet() throws Exception {
        int numberOfAssignments = 2;
        ImmutableSet<Student> students = ImmutableSet.of();

        Multimap<Student, Page> qRs = ctrl.createQRs(students, numberOfAssignments);

        assertEquals(0, qRs.size());
    }
}