DROP TABLE IF EXISTS semester;
-- Corresponds to era.uploader.data.model.Semester
-- Organizes courses by the term that were in
CREATE TABLE IF NOT EXISTS semester(
  unique_id INTEGER PRIMARY KEY AUTOINCREMENT,
  -- term and year are separated so we can sort by them
  -- term can be either FALL or SPRING
  term      TEXT NOT NULL,
  -- YYYY
  year      INTEGER NOT NULL
);

-- ALTER table doesn't really work with SQLite so we have to drop it first
DROP TABLE IF EXISTS course;

-- Corresponds to era.uploader.data.model.Course
CREATE TABLE IF NOT EXISTS course (
  unique_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name           TEXT NULL,
  -- the following are not nullable because they are generated from the roster file
  department     TEXT NOT NULL,
  course_number  TEXT NOT NULL,
  section_number TEXT NOT NULL,
  semester_id    INTEGER NOT NULL,
  FOREIGN KEY (semester_id) REFERENCES semester(unique_id)
);

DROP TABLE IF EXISTS assignment;
-- corresponds to era.uploader.data.model.Assignment
CREATE TABLE IF NOT EXISTS assignment (
  unique_id         INTEGER PRIMARY KEY AUTOINCREMENT,
  name              TEXT    NOT NULL,
  -- the whole purpose of an assignment is the file that it stores so this
  -- cannot be null
  image_file_path   TEXT    NOT NULL,
  course_id         INTEGER NOT NULL,
  student_id        INTEGER NOT NULL,
  -- stored as YYYY-MM-DD HH:MM:SS.SSS. SQLite doesn't have a timestamp type
  created_date_time TEXT NOT NULL,
  FOREIGN KEY (course_id) REFERENCES course(unique_id),
  FOREIGN KEY (student_id) REFERENCES student(unique_id)
);

DROP VIEW IF EXISTS all_assignments;

CREATE VIEW all_assignments AS
-- || is concat in SQLite which is stupid
SELECT
  assignment.name AS 'Assignment',
  student.last_name || ', ' || student.first_name AS 'Student',
  student.school_id AS '800 Number',
  course.name AS 'Course',
  course.department || '-' || course.course_number || '-' || course.section_number AS 'Child Course ID',
  semester.term || ' ' || semester.year AS 'Semester',
  DATE(assignment.created_date_time) AS 'Created',
  assignment.image_file_path AS 'File Location'
FROM assignment
  INNER JOIN student
    ON assignment.student_id = student.unique_id
  INNER JOIN course
    ON course.unique_id = assignment.course_id
  INNER JOIN semester
    ON semester.unique_id = course.semester_id
GROUP BY student.unique_id, assignment.name
-- There is no unique constraint on assignments. While we don't officially
-- support it, we don't prevent an assignment from being submitted multiple
-- times. Ideally we want to get the most recent submission if that is the case
HAVING max(assignment.created_date_time) = assignment.created_date_time
ORDER BY created_date_time DESC;

