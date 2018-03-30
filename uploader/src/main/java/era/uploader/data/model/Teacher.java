package era.uploader.data.model;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class Teacher {
    private int uniqueId;
    private final String name;
    private final Set<Course> coursesTaught;

    public Teacher(String name) {
        this.name = name;
        this.coursesTaught = Sets.newHashSet();
    }

    public Teacher(int uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.coursesTaught = Sets.newHashSet();
    }


    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public Set<Course> getCoursesTaught() {
        return coursesTaught;
    }

    public void addCourses(Course course) {
        coursesTaught.add(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(getName(), teacher.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName());
    }
}
