CREATE DATABASE yosuelto WITH ENCODING 'UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8';

CREATE TABLE postulation (
    id  bigserial not null,
    email varchar(255),
    postulation_date timestamp,
    publication_id int8,
    PRIMARY KEY (id)
);

CREATE TABLE publication (
    id  bigserial not null,
    description varchar(255),
    image_url varchar(255),
    publication_date timestamp,
    PRIMARY KEY (id)
);

ALTER TABLE postulation ADD CONSTRAINT FKgrm1t6vgpp0ofw5yt6r9yafdn FOREIGN KEY (publication_id) REFERENCES publication;

GRANT ALL PRIVILEGES ON Publication TO yosuelto;