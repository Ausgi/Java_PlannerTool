CREATE TABLE IF NOT EXISTS courses (
  plateNr varchar(50) NOT NULL,
  colour varchar(16) NOT NULL,
  model varchar(16) NOT NULL,
  year int(11) NOT NULL,
  PRIMARY KEY (courseName)
);

INSERT INTO courses (courseName, courseId, semester, courseStatus, year) VALUES
('Java-OO', 'R0000', 'Autumn', 'Ongoing' 2017);
