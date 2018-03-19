package era.uploader.data;

import era.uploader.data.model.Teacher;

import java.util.Set;

public interface TeacherDAO {
    Set<Teacher> getAllTeachers();

    Teacher insert(Teacher newTeacher);
}
