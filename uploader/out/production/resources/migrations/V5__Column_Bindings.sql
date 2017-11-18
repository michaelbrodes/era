DROP VIEW IF EXISTS all_assignments;
CREATE VIEW all_assignments AS
  -- || is concat in SQLite which is stupid
  SELECT
    assignment.name  AS 'Assignment',
    CAST(student.last_name || ', ' || student.first_name AS TEXT) AS 'Student',
    student.school_id AS 'Eight Hundred Number',
    course.name AS 'Course',
    CAST(course.department || '-' || course.course_number || '-' || course.section_number AS TEXT) AS 'Child Course ID',
    CAST(semester.term || ' ' || semester.year AS TEXT) AS 'Semester',
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
