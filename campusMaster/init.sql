CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ENUMS --------------------------
CREATE TYPE user_role AS ENUM ('student','teacher','admin');
CREATE TYPE message_tag AS ENUM ('urgent','announcement','project');
CREATE TYPE notification_type AS ENUM (
  'new_assignment',
  'grade_published',
  'deadline',
  'message',
  'announcement'
);

-- TABLES -------------------------
-- Reprends exactement ce que tu avais, mais remplace ENUM par les types créés :
-- Exemple :
-- role user_role NOT NULL,
-- tags message_tag,
-- type notification_type NOT NULL,

-- ⚠️ Je suppose que tu gardes exactement la même structure


-- =================== INSERT DATA =====================
-- Assurer l'extension UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-----------------------------------------------------
-- USERS
-----------------------------------------------------
INSERT INTO "USER"(email,password_hash,prenom,nom,role,is_active)
VALUES
('admin1@campus.sn','hashedpass','Mamadou','Diop','admin',true),
('admin2@campus.sn','hashedpass','Fatou','Ba','admin',true),
('admin3@campus.sn','hashedpass','Ousmane','Sow','admin',true),

('teacher1@campus.sn','hashedpass','Awa','Ndiaye','teacher',true),
('teacher2@campus.sn','hashedpass','Serigne','Fall','teacher',true),
('teacher3@campus.sn','hashedpass','Ibrahima','Sy','teacher',true),
('teacher4@campus.sn','hashedpass','Mame','Diagne','teacher',true),
('teacher5@campus.sn','hashedpass','Cheikh','Lo','teacher',true),
('teacher6@campus.sn','hashedpass','Khady','Niang','teacher',true),
('teacher7@campus.sn','hashedpass','Moussa','Gueye','teacher',true),
('teacher8@campus.sn','hashedpass','Aminata','Diallo','teacher',true),
('teacher9@campus.sn','hashedpass','Adama','Kane','teacher',true),
('teacher10@campus.sn','hashedpass','Saliou','Camara','teacher',true);

-- 40 étudiants réalistes
DO $$
DECLARE i INT;
BEGIN
  FOR i IN 1..40 LOOP
     INSERT INTO "USER"(email,password_hash,first_name,last_name,role,is_active)
     VALUES(
        'student'||i||'@campus.sn',
        'hashedpass',
        'Student'||i,
        'Lastname'||i,
        'student',
        TRUE
     );
  END LOOP;
END$$;

-----------------------------------------------------
-- MODULES
-----------------------------------------------------
INSERT INTO MODULE(nom,description,semester,department)
VALUES
('Programmation Java','Base et avancé Java','S5','Informatique'),
('Base de Données','Modélisation et SQL','S5','Informatique'),
('IoT & Capteurs','Arduino, ESP32, MQTT','S6','Électronique'),
('Réseaux','TCP/IP, Sécurité','S6','Télécom'),
('Cloud Computing','Docker, Kubernetes','S6','Informatique'),
('Mathématiques Appliquées','Stats & proba','S5','Sciences'),
('IA & Machine Learning','Notions ML','S6','Informatique'),
('Développement Web','Frontend + Backend','S5','Informatique');

-----------------------------------------------------
-- COURSES (liés aux teachers + modules)
-----------------------------------------------------
WITH t AS (SELECT id_user,email FROM "USER" WHERE role='teacher'),
     m AS (SELECT id_module,name FROM MODULE)
INSERT INTO COURSE(title,description,code,id_teacher,id_module)
VALUES
('Java Avancé','POO, Spring Boot','JAVA501',(SELECT id_user FROM t LIMIT 1),(SELECT id_module FROM m WHERE name='Programmation Java')),
('Introduction SQL','Requêtes SQL','DB301',(SELECT id_user FROM t OFFSET 1 LIMIT 1),(SELECT id_module FROM m WHERE name='Base de Données')),
('Web FullStack','Angular + Spring','WEB402',(SELECT id_user FROM t OFFSET 2 LIMIT 1),(SELECT id_module FROM m WHERE name='Développement Web')),
('IoT Smart System','Projets IoT réels','IOT601',(SELECT id_user FROM t OFFSET 3 LIMIT 1),(SELECT id_module FROM m WHERE name='IoT & Capteurs')),
('Cloud & DevOps','CI/CD Docker','CLOUD700',(SELECT id_user FROM t OFFSET 4 LIMIT 1),(SELECT id_module FROM m WHERE name='Cloud Computing')),
('Réseaux Sécurité','Firewall & VPN','NET520',(SELECT id_user FROM t OFFSET 5 LIMIT 1),(SELECT id_module FROM m WHERE name='Réseaux')),
('Math Stats','Probabilité','MATH330',(SELECT id_user FROM t OFFSET 6 LIMIT 1),(SELECT id_module FROM m WHERE name='Mathématiques Appliquées')),
('Machine Learning Intro','Regression, Classification','ML300',(SELECT id_user FROM t OFFSET 7 LIMIT 1),(SELECT id_module FROM m WHERE name='IA & Machine Learning'));

-----------------------------------------------------
-- ENROLLMENTS (inscrire beaucoup d'étudiants
-----------------------------------------------------
DO $$
DECLARE c UUID;
DECLARE s UUID;
BEGIN
  FOR c IN (SELECT id_course FROM COURSE) LOOP
    FOR s IN (SELECT id_user FROM "USER" WHERE role='student' ORDER BY random() LIMIT 10) LOOP
        INSERT INTO ENROLLMENT(id_course,id_student)
        VALUES(c,s) ON CONFLICT DO NOTHING;
    END LOOP;
  END LOOP;
END $$;

-----------------------------------------------------
-- ASSIGNMENTS
-----------------------------------------------------
INSERT INTO ASSIGNMENT(title,instructions,due_date,id_course)
SELECT 
 'Devoir 1 - '||title,
 'Réaliser le travail demandé',
 NOW() + INTERVAL '7 days',
 id_course
FROM COURSE;

-----------------------------------------------------
-- SUBMISSIONS + GRADES
-----------------------------------------------------
DO $$
DECLARE a UUID;
DECLARE s UUID;
BEGIN
 FOR a IN (SELECT id_assignment FROM ASSIGNMENT) LOOP
  FOR s IN (SELECT id_user FROM "USER" WHERE role='student' ORDER BY random() LIMIT 6) LOOP
    INSERT INTO SUBMISSION(id_assignment,id_student,file_path)
    VALUES(a,s,'/uploads/homework.pdf');

    INSERT INTO GRADE(id_submission,score,feedback,id_teacher)
    SELECT id_submission,(50 + random()*50),'Bon travail',
           (SELECT id_user FROM "USER" WHERE role='teacher' ORDER BY random() LIMIT 1)
    FROM SUBMISSION
    WHERE id_assignment=a AND id_student=s
    LIMIT 1;
  END LOOP;
 END LOOP;
END $$;

-----------------------------------------------------
-- ANNOUNCEMENTS
-----------------------------------------------------
INSERT INTO ANNOUNCEMENT(title,content,id_teacher,id_course)
SELECT 'Information importante',
       'Veuillez consulter vos devoirs.',
       (SELECT id_user FROM "USER" WHERE role='teacher' ORDER BY random() LIMIT 1),
       id_course
FROM COURSE
LIMIT 10;

-----------------------------------------------------
-- NOTIFICATIONS
-----------------------------------------------------
INSERT INTO NOTIFICATION(type,content,id_user)
SELECT 'announcement','Nouvelle annonce disponible',id_user
FROM "USER"
WHERE role='student'
LIMIT 50;

-----------------------------------------------------
-- COMPLETE DATA INSERTION SCRIPT
-----------------------------------------------------

-- ADMINS + TEACHERS + STUDENTS
INSERT INTO "USER"(email,password_hash,first_name,last_name,role,is_active)
VALUES
('admin1@campus.sn','hash','Mamadou','Diop','admin',true),
('admin2@campus.sn','hash','Fatou','Ba','admin',true),
('admin3@campus.sn','hash','Ousmane','Sow','admin',true),

('teacher1@campus.sn','hash','Awa','Ndiaye','teacher',true),
('teacher2@campus.sn','hash','Serigne','Fall','teacher',true),
('teacher3@campus.sn','hash','Ibrahima','Sy','teacher',true),
('teacher4@campus.sn','hash','Mame','Diagne','teacher',true),
('teacher5@campus.sn','hash','Cheikh','Lo','teacher',true),
('teacher6@campus.sn','hash','Khady','Niang','teacher',true),
('teacher7@campus.sn','hash','Moussa','Gueye','teacher',true),
('teacher8@campus.sn','hash','Aminata','Diallo','teacher',true),
('teacher9@campus.sn','hash','Adama','Kane','teacher',true),
('teacher10@campus.sn','hash','Saliou','Camara','teacher',true);

DO $$
DECLARE i INT;
BEGIN
  FOR i IN 1..40 LOOP
     INSERT INTO "USER"(email,password_hash,first_name,last_name,role,is_active)
     VALUES('student'||i||'@campus.sn','hash','Student'||i,'Lastname'||i,'student',true);
  END LOOP;
END$$;

-- MODULES
INSERT INTO MODULE(name,description,semester,department)
VALUES
('Programmation Java','Base et avancé Java','S5','Informatique'),
('Base de Données','SQL et concepts','S5','Informatique'),
('IoT & Capteurs','Arduino, ESP32','S6','Électronique'),
('Réseaux','TCP/IP, Sécurité','S6','Télécom'),
('Cloud Computing','Docker, CI/CD','S6','Informatique'),
('Mathématiques','Stats','S5','Sciences'),
('IA & ML','Intro ML','S6','Informatique'),
('Web FullStack','Frontend + Backend','S5','Informatique');

-- COURSES
WITH t AS (SELECT id_user FROM "USER" WHERE role='teacher'),
     m AS (SELECT id_module,name FROM MODULE)
INSERT INTO COURSE(title,description,code,id_teacher,id_module)
VALUES
('Java Avancé','Spring Boot','JAVA501',(SELECT id_user FROM t LIMIT 1),(SELECT id_module FROM m WHERE name='Programmation Java')),
('SQL','Requêtes','DB301',(SELECT id_user FROM t OFFSET 1 LIMIT 1),(SELECT id_module FROM m WHERE name='Base de Données')),
('FullStack','Angular + Spring','WEB402',(SELECT id_user FROM t OFFSET 2 LIMIT 1),(SELECT id_module FROM m WHERE name='Web FullStack')),
('IoT','Projets connectés','IOT601',(SELECT id_user FROM t OFFSET 3 LIMIT 1),(SELECT id_module FROM m WHERE name='IoT & Capteurs'));

-- ENROLLMENTS
DO $$
DECLARE c UUID;
DECLARE s UUID;
BEGIN
  FOR c IN (SELECT id_course FROM COURSE) LOOP
    FOR s IN (SELECT id_user FROM "USER" WHERE role='student' ORDER BY random() LIMIT 15) LOOP
        INSERT INTO ENROLLMENT(id_course,id_student)
        VALUES(c,s) ON CONFLICT DO NOTHING;
    END LOOP;
  END LOOP;
END $$;

-- ASSIGNMENTS
INSERT INTO ASSIGNMENT(title,instructions,due_date,id_course)
SELECT 'Devoir '||title,'Réaliser le travail',NOW()+INTERVAL '7 days',id_course
FROM COURSE;

-- SUBMISSIONS & GRADES
DO $$
DECLARE a UUID;
DECLARE s UUID;
BEGIN
 FOR a IN (SELECT id_assignment FROM ASSIGNMENT) LOOP
  FOR s IN (SELECT id_user FROM "USER" WHERE role='student' ORDER BY random() LIMIT 8) LOOP
    INSERT INTO SUBMISSION(id_assignment,id_student,file_path)
    VALUES(a,s,'/uploads/homework.pdf');

    INSERT INTO GRADE(id_submission,score,feedback,id_teacher)
    SELECT id_submission,(50 + random()*50),'Bon devoir',
           (SELECT id_user FROM "USER" WHERE role='teacher' ORDER BY random() LIMIT 1)
    FROM SUBMISSION
    WHERE id_assignment=a AND id_student=s
    LIMIT 1;
  END LOOP;
 END LOOP;
END $$;

-- ANNOUNCEMENTS
INSERT INTO ANNOUNCEMENT(title,content,id_teacher,id_course)
SELECT 'Info Importante','Veuillez vérifier vos devoirs.',
       (SELECT id_user FROM "USER" WHERE role='teacher' ORDER BY random() LIMIT 1),
       id_course
FROM COURSE
LIMIT 10;

-- NOTIFICATIONS
INSERT INTO NOTIFICATION(type,content,id_user)
SELECT 'announcement','Nouvelle annonce',id_user
FROM "USER"
WHERE role='student'
LIMIT 50;
