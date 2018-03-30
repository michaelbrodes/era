DROP TABLE IF EXISTS teacher;
CREATE TABLE IF NOT EXISTS teacher (
  unique_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS course;
-- corresponds to era.uploader.data.model.Course
CREATE TABLE IF NOT EXISTS course (
  unique_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name           TEXT        NULL,
  -- the following are not nullable because they are generated from the roster file
  department     TEXT        NOT NULL,
  course_number  TEXT        NOT NULL,
  section_number TEXT        NOT NULL,
  semester_id    INTEGER     NOT NULL,
  teacher_id     INTEGER     NOT NULL,
  uuid           TEXT UNIQUE NOT NULL,
  FOREIGN KEY (teacher_id) REFERENCES teacher (unique_id),
  FOREIGN KEY (semester_id) REFERENCES semester (unique_id),
  CONSTRAINT name_semester_id_unique UNIQUE (department, course_number, section_number, semester_id)
    ON CONFLICT REPLACE
);
