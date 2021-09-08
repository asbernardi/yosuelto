CREATE DATABASE yosuelto WITH ENCODING 'UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8';

CREATE TABLE location (
    id  bigserial not null,
    city varchar(255),
    country varchar(255),
    country_code varchar(255),
    isp varchar(255),
    lat varchar(255),
    lon varchar(255),
    org varchar(255),
    query varchar(255),
    region varchar(255),
    region_name varchar(255),
    status varchar(255),
    timezone varchar(255),
    zip varchar(255),
    PRIMARY KEY (id)
);

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
    email varchar(255),
    image_url varchar(255),
    publication_date timestamp,
    location_id int8,
    PRIMARY KEY (id)
);

ALTER TABLE postulation ADD CONSTRAINT FKgrm1t6vgpp0ofw5yt6r9yafdn FOREIGN KEY (publication_id) REFERENCES publication;

ALTER TABLE publication ADD CONSTRAINT FK8up95iuuispfsgox48u1mblol FOREIGN KEY (location_id) REFERENCES location


GRANT ALL PRIVILEGES ON Publication TO yosuelto;