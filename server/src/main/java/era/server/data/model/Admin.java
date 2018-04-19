package era.server.data.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;

public class Admin extends Student {
    private final String password;

    public Admin(Student student) {
        super(student.getUserName(), student.getEmail());
        super.setUuid(student.getUuid());
        super.getCourses().addAll(student.getCourses());
        super.getAssignments().addAll(student.getAssignments());
        password = null;
    }

    private Admin(@Nonnull String username, AdminBuilder builder) {
        super(username, builder);
        this.password = builder.password;
    }

    public String getPassword() {
        return password;
    }

    public static AdminBuilder builder() {
        return new AdminBuilder();
    }

    public static class AdminBuilder extends Student.Builder {
        private String password;

        @Override
        public AdminBuilder withEmail(String email) {
            super.withEmail(email);
            return this;
        }

        @Override
        public AdminBuilder withCourses(@Nonnull Set<Course> courses) {
            super.withCourses(courses);
            return this;
        }

        @Override
        public AdminBuilder withCourse(Course course) {
            super.withCourse(course);
            return this;
        }

        @Override
        public AdminBuilder withAssignments(@Nonnull Collection<Assignment> assignments) {
            super.withAssignments(assignments);
            return this;
        }

        @Override
        public AdminBuilder withUUID(String uuid) {
            super.withUUID(uuid);
            return this;
        }

        @Override
        public Admin create(@Nonnull String userName) {
            return new Admin(userName, this);
        }

        public AdminBuilder withPassword(String password) {
            this.password = password;
            return this;
        }
    }
}
