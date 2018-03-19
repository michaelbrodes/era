-- This is the SQL Script for the initial schema of the Server database. The
-- Server access will be a MariaDB access that runs on a remote server.

-- If developing locally use flywayMigrate gradle task to update your
-- particular local MariaDB instance. When deploying make sure to scp all
-- SQL scripts in this directory and apply them in order against the remote
-- access.

-- a student that belongs to many courses.
CREATE TABLE student (
  unique_id  BIGINT        NOT NULL,
  -- not actually used in the application but here for future proofing
  uuid       VARCHAR(36) NOT NULL,
  username   VARCHAR (255) NOT NULL,
  /*
  For the most part this field is generated from the username when importing
  the roster and defaults to <username>@siue.edu, but, to keep it generic
  and future proof this application, we are also storing it here
   */
  email      VARCHAR (255) NOT NULL,
  PRIMARY KEY                       (unique_id),
  UNIQUE  KEY `student_username_uk` (username),
  UNIQUE  KEY `student_email_uk`    (email)
) ENGINE = InnoDB;

-- defaults the email to be <username>@siue.edu
CREATE TRIGGER `default_student_email` BEFORE INSERT ON student
  FOR EACH ROW SET NEW.email = CONCAT(NEW.username, '@siue.edu');

CREATE TABLE semester (
  unique_id BIGINT      NOT NULL,
  uuid      VARCHAR(36) NOT NULL,
  -- either FALL or SPRING based off of era.uploader.data.model.Semester.
  term      VARCHAR(6)  NOT NULL,
  year      INT(4)      NOT NULL,
  PRIMARY KEY                     (unique_id),
  UNIQUE KEY `semester_term_year` (term, year)
) ENGINE = InnoDB;

CREATE TABLE course (
  unique_id      BIGINT        NOT NULL,
  uuid           VARCHAR (36)  NOT NULL,
  -- TODO: send the department + course_number + section_number as name if not available
  name           VARCHAR (255) NULL,
  -- a course has to belong to one semester.
  semester_id    BIGINT        NOT NULL,
  PRIMARY KEY (unique_id)
) ENGINE = InnoDB;

/*
This is a junction table for the many-to-many mapping of courses to students.
This will not have a corresponding model for it.
 */
CREATE TABLE course_student (
  course_id  BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  CONSTRAINT `course_student_course_fk`
  FOREIGN KEY (course_id)
    REFERENCES course(unique_id)
    ON DELETE CASCADE,
  CONSTRAINT `course_student_student_fk`
  FOREIGN KEY (student_id)
    REFERENCES student(unique_id)
    ON DELETE CASCADE,
  PRIMARY KEY (course_id, student_id)
) ENGINE = InnoDB;

CREATE TABLE assignment (
  unique_id         BIGINT        NOT NULL,
  uuid              VARCHAR(36)   NOT NULL,
  -- displayed directly in the UI so it cannot be null
  name              VARCHAR (255) NOT NULL,
  -- where this assignment PDF is stored in the filesystem
  image_file_path   VARCHAR (255) NOT NULL,
  course_id         BIGINT        NULL,
  student_id        BIGINT        NULL,
  created_date_time TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  -- a student can have more than one assignment for a course
  CONSTRAINT `assignment_student_fk` FOREIGN KEY (student_id)
    REFERENCES student(unique_id)
    ON DELETE SET NULL,
  CONSTRAINT `assignment_course_fk` FOREIGN KEY (course_id)
    REFERENCES course(unique_id)
    ON DELETE SET NULL,
  PRIMARY KEY (unique_id)
) ENGINE = InnoDB;