Drop table IF EXISTS assignment;
CREATE TABLE IF NOT EXISTS assignment (
  unique_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  name            TEXT    NOT NULL,
  -- the whole purpose of an assignment is the file that it stores so this
  -- cannot be null
  image_file_path TEXT    NOT NULL,
  course_id       INTEGER NOT NULL,
  student_id      INTEGER NOT NULL,
  FOREIGN KEY (course_id) REFERENCES course(unique_id),
  FOREIGN KEY (student_id) REFERENCES student(unique_id)
);

DROP TABLE IF EXISTS  QRCodeMapping;

CREATE TABLE IF NOT EXISTS qr_code_mapping (
  -- not null because sqlite data types are dumb
  uuid            TEXT PRIMARY KEY NOT NULL,
  sequence_number INTEGER NULL,
  -- QRCodeMappings where we just generated a QR code will not be part of an assignment
  student_id      INTEGER NULL,
  FOREIGN KEY (student_id)    REFERENCES student(unique_id)
);