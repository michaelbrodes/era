-- This is the SQL Script for the initial schema of the Server database. The
-- Server database will be a MariaDB database that runs on a remote server

-- A prerequisite for running sql file is setting up the era-db docker
-- container. The definition of this container is in the file
-- server/docker/docker-compose.yml. Before spinning up that container it is
-- IMPORTANT to create a ROOT_PASS environment variable on the machine you are
-- planning on run this application on, otherwise the root password for the
-- database will be an empty string which is a huge security venerability. To
-- spin up that docker container just open up a shell in the server/docker
-- directory and run the `docker-compose up` command

-- Each schema change to this database should have an associated "migration"
-- script. That is how we version our database with the rest of the project.
-- The naming convention of these migration files are
-- <target version number>-<camelCase Description>.sql. To apply a migration
-- run the sql script associated with it against the era-db database using
-- either a command line MySQL client or Intellij's database plugin. As an
-- example of using the command line MySQL client here is how I applied this
-- migration against my local docker container:
-- TODO: actually supply the command

CREATE TABLE IF NOT EXISTS student (
  unique_id  BIGINT        NOT NULL AUTO_INCREMENT,
  first_name VARCHAR (255) NULL,
  last_name  VARCHAR (255) NULL,
  /*
  school_id is external id that the school maps to a student. Because we can't
  verify the integrity of this id we don't really use it to uniquely identify
  students (we use unique_id instead)

  The 800 number from SIUE is nine characters exactly, if on a different
  domain this may need to be changed.
   */
  school_id  VARCHAR (9)   NOT NULL,
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
);

CREATE TABLE IF NOT EXISTS course (
  unique_id      BIGINT        NOT NULL AUTO_INCREMENT,
  name           VARCHAR (255) NULL,
  -- F, S, FALL, SPRING
  semester       VARCHAR (6)   NULL,
  /*
  The following are not nullable because they are generated from a roster file
  uploader side.
   */
  department     VARCHAR (50)  NOT NULL,
  course_number  VARCHAR (10)  NOT NULL,
  section_number VARCHAR (10)  NOT NULL,
  PRIMARY KEY (unique_id)
);

/*
This is a junction table for the many-to-many mapping of courses to students.
This will not have a corresponding model for it.
 */
CREATE TABLE IF NOT EXISTS course_student (
  course_id  BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  CONSTRAINT `course_student_course_fk`
  FOREIGN KEY (course_id)
    REFERENCES course(unique_id),
  CONSTRAINT `course_student_student_fk`
  FOREIGN KEY (student_id)
    REFERENCES student(unique_id),
  PRIMARY KEY (course_id, student_id)
);

CREATE TABLE IF NOT EXISTS assignment (
  unique_id       BIGINT        NOT NULL AUTO_INCREMENT,
  -- displayed directly in the UI so it cannot be null
  name            VARCHAR (255) NOT NULL,
  -- where this assignment PDF is stored in the filesystem
  image_file_path VARCHAR (255) NOT NULL,
  course_id       BIGINT        NOT NULL,
  student_id      BIGINT        NOT NULL,
  -- a student can have more than one assignment for a course
  CONSTRAINT `assignment_student_fk` FOREIGN KEY (student_id)
    REFERENCES student(unique_id),
  CONSTRAINT `assignment_course_fk` FOREIGN KEY (course_id)
    REFERENCES course(unique_id),
  PRIMARY KEY (unique_id)
)
