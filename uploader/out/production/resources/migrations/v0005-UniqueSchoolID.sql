DROP TABLE IF EXISTS student;
CREATE TABLE IF NOT EXISTS student (
  unique_id  INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name TEXT NULL,
  last_name  TEXT NULL,
  -- a school uniqueId is the external integer uniqueId that the school uses for a student
  school_id  TEXT NULL UNIQUE,
  username   TEXT NOT NULL,
  -- for the most part this field is generated from the username when importing
  -- the roster and defaults to <username>@siue.edu, but to keep it generic
  -- we are also storing it here.
  email      TEXT NOT NULL
);