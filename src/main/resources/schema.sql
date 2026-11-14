-- Class Organizer tables. The same file sets up MySQL (compose.yaml loads it the first time the
-- database starts) and the embedded H2 database that local runs and tests use (Database.createTables).

CREATE TABLE IF NOT EXISTS usertable (
    id            INT          NOT NULL AUTO_INCREMENT,
    firstname     VARCHAR(50)  NOT NULL,
    lastname      VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    age           INT          NOT NULL,
    username      VARCHAR(30)  NOT NULL,
    -- A salted PBKDF2 hash (see Passwords.java), never the password itself.
    password_hash VARCHAR(200) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT username_is_unique UNIQUE (username)
);

-- One row per class a student takes. idUserClasses is the id of that student in usertable.
CREATE TABLE IF NOT EXISTS userclasses (
    id            INT          NOT NULL AUTO_INCREMENT,
    idUserClasses INT          NOT NULL,
    ClassName     VARCHAR(50)  NOT NULL,
    Subject       VARCHAR(30)  NOT NULL,
    Time          TIME         NOT NULL,
    Monday        BOOLEAN      NOT NULL DEFAULT FALSE,
    Tuesday       BOOLEAN      NOT NULL DEFAULT FALSE,
    Wednesday     BOOLEAN      NOT NULL DEFAULT FALSE,
    Thursday      BOOLEAN      NOT NULL DEFAULT FALSE,
    Friday        BOOLEAN      NOT NULL DEFAULT FALSE,
    Saturday      BOOLEAN      NOT NULL DEFAULT FALSE,
    Professor     VARCHAR(50)  NOT NULL,
    CRN           INT          NOT NULL,
    Grade         INT          NOT NULL,
    Credits       INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT one_crn_per_student UNIQUE (idUserClasses, CRN),
    CONSTRAINT class_belongs_to_student FOREIGN KEY (idUserClasses) REFERENCES usertable (id) ON DELETE CASCADE
);
