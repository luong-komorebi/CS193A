CREATE TABLE courses (
id INT UNSIGNED NOT NULL PRIMARY KEY, 
name VARCHAR(32) DEFAULT NULL, 
teacher_id INT UNSIGNED NOT NULL
);

INSERT INTO courses VALUES (10001, 'Computer Science 142', 1234);
INSERT INTO courses VALUES (10002, 'Computer Science 143', 5678);
INSERT INTO courses VALUES (10003, 'Computer Science 190M', 9012);
INSERT INTO courses VALUES (10004, 'Informatics 100', 1234);

CREATE TABLE grades (
student_id INT UNSIGNED NOT NULL, 
course_id INT UNSIGNED NOT NULL, 
grade varchar(2) DEFAULT NULL
);

INSERT INTO grades VALUES (123, 10001, 'B-');
INSERT INTO grades VALUES (123, 10002, 'C');
INSERT INTO grades VALUES (456, 10001, 'B+');
INSERT INTO grades VALUES (888, 10002, 'A+');
INSERT INTO grades VALUES (888, 10003, 'A+');
INSERT INTO grades VALUES (404, 10004, 'D+');
INSERT INTO grades VALUES (404, 10002, 'B');
INSERT INTO grades VALUES (456, 10002, 'D-');

CREATE TABLE students (
id INT UNSIGNED NOT NULL PRIMARY KEY, 
name VARCHAR(32) DEFAULT NULL,
email VARCHAR(32) DEFAULT NULL,
password VARCHAR(16) DEFAULT NULL
);

INSERT INTO students VALUES (123, 'Bart', 'bart@fox.com', 'bartman');
INSERT INTO students VALUES (404, 'Ralph', 'ralph@fox.com', 'catfood');
INSERT INTO students VALUES (456, 'Milhouse', 'milhouse@fox.com', 'fallout');
INSERT INTO students VALUES (888, 'Lisa', 'lisa@fox.com', 'vegan');

CREATE TABLE teachers (
id INT UNSIGNED NOT NULL PRIMARY KEY, 
name VARCHAR(32) DEFAULT NULL
);

INSERT INTO teachers VALUES (1234, 'Krabappel');
INSERT INTO teachers VALUES (5678, 'Hoover');
INSERT INTO teachers VALUES (9012, 'Stepp');
