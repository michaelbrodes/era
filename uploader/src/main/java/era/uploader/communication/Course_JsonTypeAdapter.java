package era.uploader.communication;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import era.uploader.data.model.Course;
import era.uploader.data.model.Student;

import java.io.IOException;

public class Course_JsonTypeAdapter extends TypeAdapter <Course>{

    @Override
    public void write(JsonWriter out, Course value) throws IOException {
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
        throw new UnsupportedOperationException();
    }
}
