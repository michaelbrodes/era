package era.server.data;

import era.server.data.model.Admin;
import era.server.data.model.Student;

import java.util.Optional;

/**
 * An admin is a student (our generic user) with just a special privilege to
 * access the admin pages. There for this DAO accesses the {@code admin} table
 * in the backend, but sends back {@link Student} objects
 */
public interface AdminDAO {
    Optional<Admin> fetchByStudentId(String studentId);
    Optional<Admin> fetchByUsername(String username);
    Admin storeAsAdmin(Student student);
}
