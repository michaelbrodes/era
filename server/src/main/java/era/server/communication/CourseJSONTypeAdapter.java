package era.server.communication;

import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import era.server.data.model.Course;
import era.server.data.model.Semester;
import era.server.data.model.Student;
import era.server.data.model.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Year;
import java.util.Set;

public class CourseJSONTypeAdapter extends TypeAdapter<Course>{

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseJSONTypeAdapter.class);
    @Override
    public void write(JsonWriter out, Course value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
            out.name("uuid").value(value.getUuid());
            out.name("name").value(value.getName());
            out.name("studentsEnrolled").beginArray();
            for (Student student : value.getStudentsEnrolled()){
                out.beginObject();
                out.name("userName").value(student.getUserName());
                out.name("email").value(student.getEmail());
                out.name("uuid").value(student.getUuid());
                out.endObject();
            }
            out.endArray();
            out.name("semester").beginObject();
                out.name("uuid").value(value.getSemester().getUuid());
                out.name("term").value(value.getSemester().getTerm().name());
                out.name("year").beginObject();
                    out.name("year").value(value.getSemester().getYear().getValue());
                out.endObject();
            out.endObject();
        out.endObject();
    }

    @Override
    public Course read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Course.Builder courseBuilder = Course.builder();
        // a uuid is required to make a course
        String name = null;
        Semester semester = null;
        in.beginObject();
        while (in.hasNext()) {
            String nextName = in.nextName();
            switch(nextName) {
                case "uuid":
                    // they might send it over, ignore it
                    in.nextString();
                    LOGGER.warn("course.uuid sent over as part of Uploader JSON payload. Ignoring...");
                    break;
                case "name":
                    name = in.nextString();
                    courseBuilder.withName(name);
                    break;
                case "studentsEnrolled":
                    courseBuilder.withStudents(parseStudentsEnrolled(in));
                    break;
                case "semester":
                    semester = parseSemester(in);
                    courseBuilder.withSemester(semester);
                    break;
                default:
                    throw new JsonSyntaxException("Unexpected " + nextName + " field in course");
            }
        }
        in.endObject();

        if (name == null) {
            throw new JsonSyntaxException("A name wasn't supplied as part of the Course entity");
        }

        if (semester == null) {
            throw new JsonSyntaxException("A name wasn't supplied as part of the Semester entity");
        }

        return courseBuilder.create();
    }

    private Semester parseSemester(JsonReader in) throws IOException {
        in.beginObject();
        String term = null;
        Integer year = null;

        while (in.hasNext()) {
            String nextName = in.nextName();
            switch(nextName) {
                case "uuid":
                    // they might send it in
                    in.nextString();
                    LOGGER.warn("semester.uuid sent over as part of Uploader JSON payload. Ignoring...");
                    break;
                case "term" :
                    term = in.nextString();
                    break;
                case "year":
                    in.beginObject();
                    if (in.nextName().equals("year")) {
                        year = in.nextInt();
                    }
                    in.endObject();
                    break;
                default:
                    throw new JsonSyntaxException("Unexpected " + nextName + " field in semester");
            }
        }
        in.endObject();

        if (term == null) {
            throw new JsonSyntaxException("A term wasn't supplied as part of the Semester entity.");
        }

        return new Semester(Term.valueOf(term), year != null ? Year.of(year) : null);
    }

    private Set<Student> parseStudentsEnrolled(JsonReader in) throws IOException {
        Set<Student> studentsEnrolled = Sets.newHashSet();
        in.beginArray();
        while (in.hasNext()) {
            in.beginObject();
            Student.Builder studentBuilder = Student.builder();
            String username = null;
            while(in.hasNext()) {
                String nextField = in.nextName();
                switch(nextField) {
                    case "userName":
                        username = in.nextString();
                        break;
                    case "email":
                        studentBuilder.withEmail(in.nextString());
                        break;
                    case "uuid":
                        // they might send it over.
                        in.nextString();
                        LOGGER.warn("student.uuid sent over as part of Uploader JSON payload. Ignoring...");
                        break;
                    default:
                        throw new JsonSyntaxException("Unexpected " + nextField + " field in student");
                }
            }
            in.endObject();


            if (username == null) {
                throw new JsonSyntaxException("A username wasn't supplied as part of the Student entity.");
            }

            studentsEnrolled.add(studentBuilder.create(username));
        }
        in.endArray();

        return studentsEnrolled;
    }

}
