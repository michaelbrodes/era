-- This migration is the companion to V7__Add_UUIDs from the uploader side. It
-- removes uniqueId columns from tables and makes their primary keys uuids.
-- This is so the server can have many uploading clients uploading to it.

-- undo all foreign key constraints
ALTER TABLE course_student
  DROP FOREIGN KEY course_student_course_fk;
ALTER TABLE course_student
  DROP FOREIGN KEY course_student_student_fk;
ALTER TABLE assignment
  DROP FOREIGN KEY assignment_course_fk;
ALTER TABLE assignment
  DROP FOREIGN KEY assignment_student_fk;

-- start dropping primary keys
ALTER TABLE course_student
  DROP PRIMARY KEY;
ALTER TABLE course
  DROP PRIMARY KEY;
ALTER TABLE student
  DROP PRIMARY KEY;
ALTER TABLE assignment
  DROP PRIMARY KEY;
ALTER TABLE semester
  DROP PRIMARY KEY;

-- drop that random ass index on course_student.
ALTER TABLE course_student
  DROP INDEX course_student_student_fk;

-- drop columns that are bigint sequence numbers
ALTER TABLE course
  DROP COLUMN unique_id;
ALTER TABLE course
  DROP COLUMN semester_id;
ALTER TABLE student
  DROP COLUMN unique_id;
ALTER TABLE assignment
  DROP COLUMN unique_id;
ALTER TABLE assignment
  DROP COLUMN course_id;
ALTER TABLE assignment
  DROP COLUMN student_id;
ALTER TABLE semester
  DROP COLUMN unique_id;
DROP TABLE IF EXISTS course_student;

-- Add UUID columns for foreign key relationships. All VARCHARs are of size 36
-- because that is how big a UUID is when it is encoded as a string.
ALTER TABLE course
  ADD COLUMN semester_id VARCHAR(36) NOT NULL;
ALTER TABLE assignment
  ADD COLUMN student_id VARCHAR(36) NOT NULL;
ALTER TABLE assignment
  ADD COLUMN course_id VARCHAR(36) NOT NULL;

-- Make the primary keys the uuid columns of each table.
ALTER TABLE course
  ADD PRIMARY KEY (uuid);
ALTER TABLE student
  ADD PRIMARY KEY (uuid);
ALTER TABLE assignment
  ADD PRIMARY KEY (uuid);
ALTER TABLE semester
  ADD PRIMARY KEY (uuid);

CREATE TABLE IF NOT EXISTS course_student (
  student_id VARCHAR(36) NOT NULL,
  course_id VARCHAR (36) NOT NULL,
  CONSTRAINT course_student_course_fk
  FOREIGN KEY (course_id)
  REFERENCES course(uuid)
    ON DELETE CASCADE,
  CONSTRAINT course_student_student_fk
  FOREIGN KEY (student_id)
  REFERENCES student(uuid)
    ON DELETE CASCADE,
  PRIMARY KEY (course_id, student_id)
);

-- Recreate foreign key relations
ALTER TABLE course
  ADD CONSTRAINT course_semester_fk
FOREIGN KEY (semester_id)
REFERENCES semester (uuid)
  ON DELETE CASCADE;
ALTER TABLE assignment
  ADD CONSTRAINT assignment_student_fk
FOREIGN KEY (student_id)
REFERENCES student (uuid);
ALTER TABLE assignment
  ADD CONSTRAINT assignment_course_fk
FOREIGN KEY (course_id)
REFERENCES course (uuid);

-- Add an index on the student_id of assignment as that is what we will use for
-- when students are viewing their assignments
CREATE INDEX assignment_student_idx
  ON assignment (student_id);

