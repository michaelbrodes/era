-- an admin is a student (because student is our generic user)
CREATE TABLE IF NOT EXISTS admin (
  student_id VARCHAR(36) PRIMARY KEY,
  CONSTRAINT `admin_student_fk`
    FOREIGN KEY (student_id)
    REFERENCES student(uuid)
    ON DELETE CASCADE
);