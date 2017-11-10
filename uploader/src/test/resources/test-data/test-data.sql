-- This sql script will reset the below tables and then insert some new test
-- data on those tables.

-- RUNNING THIS SQL FILE AGAINST A PRODUCTION ENVIRONMENT IS A BAD IDEA!!!!!!!!
UPDATE sqlite_sequence SET seq = 0;
DELETE FROM student;
DELETE FROM assignment;
DELETE FROM course;
DELETE FROM semester;
DELETE FROM course_student;

INSERT INTO semester(term, year) VALUES ('FALL', 2017), ('SPRING', 2018), ('FALL', 2018);
INSERT INTO
  course(name, department, course_number, section_number, semester_id)
VALUES
  ('Intro to CS', 'CS', '111', '001', 1),
  ('Intro to CS', 'CS', '111', '002', 1),
  ('Intro to CS', 'CS', '111', '001', 2),
  ('Intro to CS', 'CS', '111', '001', 3),
  ('Intro to Chemistry', 'CHEM', '111', '001', 1),
  ('Intro to Biology', 'BIO', '111', '001', 1);
INSERT INTO
  student(first_name, last_name, school_id, username, email)
VALUES
  ('Sterling', 'Archer', '800999999', 'sarcher', 'sarcher@siue.edu'),
  ('Lana', 'Kane', '800111111', 'lkane', 'lkane@siue.edu'),
  ('Cheryl', 'Tunt', '800222222', 'ctunt', 'ctunt@siue.edu'),
  ('Figgis', 'Cyril', '800333333', 'cfiggis', 'cfiggis@siue.edu'),
  ('Malory', 'Archer', '800444444', 'marcher', 'marcher@siue.edu'),
  ('Poovey', 'Pam', '800555555', 'ppoovey', 'ppoovey@siue.edu'),
  ('Gillette', 'Ray', '800666666', 'rgillette', 'rgillette@siue.edu');

INSERT INTO
  course_student(course_id, student_id)
VALUES
  (1, 1),
  (5, 1),
  (6, 1),
  (2, 2),
  (5, 2),
  (6, 2),
  (3, 3),
  (4, 4),
  (1, 5),
  (5, 5);
INSERT INTO assignment(name, image_file_path, course_id, student_id, created_date_time)
VALUES
  ('cells and stuff', 'lana-stuff.pdf', 6, 2, DATETIME('now')),
  ('cells and stuff', 'archer-stuff.pdf', 6, 1, DATETIME('now')),
  ('count in binary', 'archer-binary.pdf', 1, 1, DATETIME('now')),
  ('count in binary', 'marcher-binary.pdf', 1, 5, DATETIME('now')),
  ('count in binary', 'marcher-binary.pdf', 1, 5, DATETIME('2017-11-08 23:47:45')),
  ('count in hex', 'marcher-hex.pdf', 1, 5, DATETIME('2017-11-08 23:47:45'));
