package era.uploader.data.model;

/* Class that will represent each individual student and provide a means to
 * create QR codes, and match the information inside of each code to its
 * respective student.
 */

public class Student {
    /* Class Fields */
    private String firstName; /* Student's first name */
    private String lastName;  /* Student's last name */
    private String schoolId;  /* Identifier for each student provided by the school */
    private int uniqueId;     /* Identifier that we generate to uniquely identify each student inside the QR code */

    /* Constructor */
    public Student(String firstName, String lastName, String schoolId, int uniqueId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.uniqueId = uniqueId;
    }

    /* Getters and Setters */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        return uniqueId == student.uniqueId;
    }

    @Override
    public int hashCode() {
        return uniqueId;
    }

}

