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
  semester.term || ' ' || semester.year AS 'semester',
  course.name AS 'course',
  course.department || '-' || course.course_number || '-' || course.section_number AS course_alt,
  student.last_name || ', ' || student.first_name AS 'student',
  assignment.name AS 'assignment',
  assignment.image_file_path AS 'file location'
FROM semester
INNER JOIN course
  ON semester.unique_id = course.semester_id
INNER JOIN course_student
  ON course_id = course.unique_id
INNER JOIN student
  ON student.unique_id = course_student.student_id
INNER JOIN assignment
  ON assignment.unique_id IN (
    -- there could be more than one assignment submission (e.g. a grader
    -- submits twice) thus we need the most recently created
    SELECT assignment.unique_id
    FROM assignment
    WHERE assignment.course_id = student.unique_id AND assignment.course_id = course.unique_id
    GROUP BY assignment.name
    ORDER BY DATE(assignment.created_date_time) DESC
    LIMIT 1
  )
ORDER BY year DESC, term ASC, course ASC, course_alt ASC, assignment ASC, student ASC;
