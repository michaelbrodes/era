-- This migration adds a unique constraint on the course.name and
-- course.semester_id combination. This is so that if we have two clients that
-- upload to the same course, we will only have on course in the database. This
-- adds the requirement that you *MUST* UPSERT when you want to insert a new
-- course into the database.

ALTER TABLE course
  ADD CONSTRAINT `course_semester_name_uk` UNIQUE (name, semester_id);