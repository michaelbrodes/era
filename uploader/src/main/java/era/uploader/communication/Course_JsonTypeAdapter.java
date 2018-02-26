package era.uploader.communication;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import era.uploader.data.model.Course;
import era.uploader.data.model.Semester;
import era.uploader.data.model.Student;
import era.uploader.data.model.Term;

import java.io.IOException;
import java.time.Year;
import java.util.Set;

public class Course_JsonTypeAdapter extends TypeAdapter <Course>{

    @Override
    public void write(JsonWriter out, Course value) throws IOException {
        out.beginObject();
            out.name("name").value(value.getName());
            out.name("studentsEnrolled").beginArray();
                for (Student student : value.getStudentsEnrolled()){
                    out.beginObject();
                    out.name("userName").value(student.getUserName());
                    out.name("email").value(student.getEmail());
                    out.endObject();
                }
            out.endArray();
            out.name("semester").beginObject();
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
        String uuid = null, name = null;
        Semester semester = null;
        in.beginObject();
        while (in.hasNext()) {
            String nextName = in.nextName();
            switch(nextName) {
                case "uuid":
                    uuid = in.nextString();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "studentsEnrolled":
                    courseBuilder.withStudents(parseStudentsEnrolled(in));
                    break;
                case "semester":
                    semester = parseSemester(in);
                    break;
                default:
                    throw JSONParseException.unexpectedField(Course.class, nextName);
            }
        }
        in.endObject();

        if (uuid == null) {
            throw JSONParseException.fieldNotSupplied(Course.class, "uuid");
        }

        if (name == null) {
            throw JSONParseException.fieldNotSupplied(Course.class, "name");
        }

        if (semester == null) {
            throw JSONParseException.fieldNotSupplied(Course.class, "semester");
        }

        return courseBuilder.create(name, uuid, semester);
    }

    private Semester parseSemester(JsonReader in) throws IOException {
        in.beginObject();
        String uuid = null, term = null;
        Integer year = null;
        while (in.hasNext()) {
            String nextName = in.nextName();
            switch(nextName) {
                case "uuid":
                    uuid = in.nextString();
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
                    throw JSONParseException.unexpectedField(Semester.class, nextName);
            }
        }
        in.endObject();

        if (uuid == null) {
            throw JSONParseException.fieldNotSupplied(Semester.class, "uuid");
        }

        if (term == null) {
            throw JSONParseException.fieldNotSupplied(Semester.class, "term");
        }

        return new Semester(uuid, Term.valueOf(term), year != null ? Year.of(year) : null);
    }

    private Set<Student> parseStudentsEnrolled(JsonReader in) throws IOException {
        Set<Student> studentsEnrolled = Sets.newHashSet();
        in.beginArray();
        while (in.hasNext()) {
            in.beginObject();
            Student.Builder studentBuilder = Student.builder();
            String username = null, uuid = null;
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
                        uuid = in.nextString();
                        break;
                    default:
                        throw JSONParseException.unexpectedField(Student.class, nextField);
                }
            }
            in.endObject();

            if (uuid == null) {
                throw JSONParseException.fieldNotSupplied(Student.class, "uuid");
            }

            if (username == null) {
                throw JSONParseException.fieldNotSupplied(Student.class, "username");
            }

            studentsEnrolled.add(studentBuilder.create(username, uuid));
        }
        in.endArray();

        return studentsEnrolled;
    }
}
