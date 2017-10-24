-- This is the SQL Script for the initial schema of the Uploader database. The
-- Uploader database will be a SQLite database that is distributed with the
-- uploader executable.

-- Each schema change to this database should have an associated "migration"
-- script. That is how we version our database with the rest of the project.
-- The naming convention of these migration files are
-- <target version number>-<camelCase Description>.sql. To apply a migration
-- either use the command line sqlite3 (https://sqlite.org/download.html)
-- client or intellij's database plugin. An example of applying a migration
-- using the sqlite3 command client is as follows:
-- `sqlite> .read v003-InitialTables.sql`
-- or with the sqlite3 command itself:
-- `sqlite3 uploader.db < v003-InitialTables.sql`

-- Corresponds to era.uploader.data.model.Student
CREATE TABLE IF NOT EXISTS student (
  unique_id  INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name TEXT NULL,
  last_name  TEXT NULL,
  -- a school id is the external integer id that the school uses for a student
  school_id  TEXT NOT NULL,
  username   TEXT NOT NULL,
  -- for the most part this field is generated from the username when importing
  -- the roster and defaults to <username>@siue.edu, but to keep it generic
  -- we are also storing it here.
  email      TEXT NOT NULL
);

-- Corresponds to era.uploader.data.model.Course
CREATE TABLE IF NOT EXISTS course (
  unique_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name           TEXT NULL,
  semester       TEXT NULL,
  -- the following are not nullable because they are generated from the roster file
  department     TEXT NOT NULL COLLATE NOCASE,
  course_number  TEXT NOT NULL,
  section_number TEXT NOT NULL
);

-- a junction table for the many to many relationship of "course" and
-- "student". It allows us to have two sets of Courses on a student and sets
-- of students on courses.
CREATE TABLE IF NOT EXISTS course_student (
  course_id  INTEGER NOT NULL,
  student_id INTEGER NOT NULL,
  FOREIGN KEY (course_id)  REFERENCES course(unique_id),
  FOREIGN KEY (student_id) REFERENCES student(unique_id),
  PRIMARY KEY (course_id, student_id)
);

-- corresponds to era.uploader.data.model.Assignment
CREATE TABLE IF NOT EXISTS assignment (
  unique_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  name            TEXT    NOT NULL,
  -- the whole purpose of an assignment is the file that it stores so this
  -- cannot be null
  image_file_path TEXT    NOT NULL,
  course_id       INTEGER NOT NULL,
  FOREIGN KEY (course_id) REFERENCES course(unique_id)
);

CREATE TABLE IF NOT EXISTS page (
  -- not null because sqlite data types are dumb
  uuid            TEXT PRIMARY KEY NOT NULL,
  sequence_number INTEGER NULL,
  -- pages where we just generated a QR code will not be part of an assignment
  assignment_id   INTEGER NULL,
  student_id      INTEGER NOT NULL,
  FOREIGN KEY (assignment_id) REFERENCES assignment(unique_id),
  FOREIGN KEY (student_id)    REFERENCES student(unique_id)
);
