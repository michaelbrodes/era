package era.uploader.data.database;

import era.uploader.data.TeacherDAO;
import era.uploader.data.model.Teacher;

import java.util.HashSet;
import java.util.Set;

public class MockTeacherDAOImpl implements TeacherDAO {
    private final Set<Teacher> teachers;
    private static int idCount = 0;

    public MockTeacherDAOImpl() {
        teachers = new HashSet<>();
    }

    @Override
    public Set<Teacher> getAllTeachers() {
        return teachers;
    }

    @Override
    public Teacher insert(Teacher newTeacher) {
        newTeacher.setUniqueId(idCount++);
        teachers.add(newTeacher);
        return newTeacher;
    }
}
