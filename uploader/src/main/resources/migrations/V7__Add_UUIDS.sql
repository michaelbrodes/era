-- DATE: 2/6/2017
-- DESCRIPTION: This migration adds Universally Unique ID columns to all tables
--  that are also persisted server side. This is to facilitate multiple clients
DROP TABLE IF EXISTS semester;
DROP TABLE IF EXISTS course_student;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS assignment;

-- corresponds to era.uploader.data.model.Semester
CREATE TABLE IF NOT EXISTS semester (
  unique_id INTEGER PRIMARY KEY AUTOINCREMENT,
  -- term and year are separated so we can sort by them
  -- term can be either FALL or SPRING
  term      TEXT        NOT NULL,
  -- YYYY
  year      INTEGER     NOT NULL,
  uuid      TEXT UNIQUE NOT NULL
);

-- corresponds to era.uploader.data.model.Course
CREATE TABLE IF NOT EXISTS course (
  unique_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name           TEXT        NULL,
  -- the following are not nullable because they are generated from the roster file
  department     TEXT        NOT NULL,
  course_number  TEXT        NOT NULL,
  section_number TEXT        NOT NULL,
  semester_id    INTEGER     NOT NULL,
  uuid           TEXT UNIQUE NOT NULL,
  FOREIGN KEY (semester_id) REFERENCES semester (unique_id),
  CONSTRAINT name_semester_id_unique UNIQUE (name, semester_id)
    ON CONFLICT REPLACE
);

-- corresponds to era.uploader.data.model.Student
CREATE TABLE IF NOT EXISTS student (
  unique_id  INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name TEXT        NULL,
  last_name  TEXT        NULL,
  -- a schoolId is the external integer id that the school uses for a student
  school_id  TEXT        NULL UNIQUE,
  username   TEXT        NOT NULL,
  -- for the most part this field is generated from the username when importing
  -- the roster and defaults to <username>@siue.edu, but to keep it generic
  -- we are also storing it here.
  email      TEXT        NOT NULL,
  uuid       TEXT UNIQUE NOT NULL,
  -- should always be unique, but this is taken from an external source so
  -- ¯\_(ツ)_/¯
  CONSTRAINT school_id_unique UNIQUE (school_id)
    ON CONFLICT REPLACE
);

-- a junction table for the many to many relationship of "course" and
-- "student". It allows us to have two sets of Courses on a student and sets
-- of students on courses.
CREATE TABLE IF NOT EXISTS course_student (
  -- server-side this link is based off of UUID, but client side this is
  -- a uniqueId because most of our code is already written for uniqueId
  course_id  INTEGER NOT NULL,
  student_id INTEGER NOT NULL,
  FOREIGN KEY (course_id) REFERENCES course (unique_id),
  FOREIGN KEY (student_id) REFERENCES student (unique_id),
  PRIMARY KEY (course_id, student_id)
);

-- corresponds to era.uploader.data.model.Assignment
CREATE TABLE IF NOT EXISTS assignment (
  unique_id         INTEGER PRIMARY KEY AUTOINCREMENT,
  name              TEXT        NOT NULL,
  -- the whole purpose of an assignment is the file that it stores so this
  -- cannot be null
  image_file_path   TEXT        NOT NULL,
  course_id         INTEGER     NOT NULL,
  student_id        INTEGER     NOT NULL,
  -- stored as YYYY-MM-DD HH:MM:SS.SSS. SQLite doesn't have a timestamp type
  created_date_time TEXT        NOT NULL,
  uuid              TEXT UNIQUE NOT NULL,
  FOREIGN KEY (course_id) REFERENCES course (unique_id),
  FOREIGN KEY (student_id) REFERENCES student (unique_id)
);


