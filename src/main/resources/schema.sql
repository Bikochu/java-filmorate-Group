DROP ALL OBJECTS;

create table IF NOT EXISTS RATING
(
    RATING_ID INTEGER not null auto_increment,
    RATING_NAME      CHARACTER VARYING(10),
    constraint "RATING_pk"
        primary key (RATING_ID)
);

create table IF NOT EXISTS GENRE
(
    GENRE_ID INTEGER not null auto_increment,
    GENRE_NAME     CHARACTER VARYING(50),
    constraint "GENRE_pk"
        primary key (GENRE_ID)
);

create table IF NOT EXISTS FILM
(
    FILM_ID      INTEGER not null auto_increment,
    FILM_NAME    CHARACTER VARYING(50),
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    constraint "FILM_pk"
        primary key (FILM_ID)
);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    constraint "FILM_GENRE_FILM_FILM_ID_fk"
        foreign key (FILM_ID) references PUBLIC.FILM,
    constraint "FILM_GENRE_GENRE_GENRE_ID_fk"
        foreign key (GENRE_ID) references PUBLIC.GENRE
);

create table IF NOT EXISTS FILM_RATING
(
    FILM_ID  INTEGER,
    RATING_ID INTEGER,
    constraint "FILM_RATING_FILM_FILM_ID_fk"
        foreign key (FILM_ID) references PUBLIC.FILM,
    constraint "FILM_RATING_RATING_RATING_ID_fk"
        foreign key (RATING_ID) references PUBLIC.RATING
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER not null auto_increment,
    EMAIL    CHARACTER VARYING(50),
    LOGIN    CHARACTER VARYING(50),
    USER_NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE,
    constraint "USERS_pk"
        primary key (USER_ID)
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    constraint "LIKES_FILM_FILM_ID_fk"
        foreign key (FILM_ID) references PUBLIC.FILM,
    constraint "LIKES_USERS_USER_ID_fk"
        foreign key (USER_ID) references PUBLIC.USERS
);

create table IF NOT EXISTS USERS_FRIEND
(
    USER_ID   INTEGER,
    FRIEND_ID INTEGER,
    STATUS    BOOLEAN,
    constraint "USERS_FRIEND_USERS_USER_ID_fk"
        foreign key (USER_ID) references PUBLIC.USERS
);

create table IF NOT EXISTS EVENTS
(
    EVENT_ID   INTEGER not null auto_increment,
    EVENT_TYPE CHARACTER VARYING(50) NOT NULL,
    OPERATION  CHARACTER VARYING(50) NOT NULL,
    USER_ID    INTEGER NOT NULL,
    ENTITY_ID  INTEGER NOT NULL,
    EVENT_TS   TIMESTAMP NOT NULL,
    constraint "EVENTS_pk"
        primary key (EVENT_ID),
    constraint "EVENTS_USERS_USER_ID_fk"
        foreign key (USER_ID) references PUBLIC.USERS ON DELETE CASCADE
);

CREATE TRIGGER IF NOT EXISTS LIKE_ADD AFTER INSERT ON LIKES 
	FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.trigger.LikeTrigger';

CREATE TRIGGER IF NOT EXISTS LIKE_DEL AFTER DELETE ON LIKES 
	FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.trigger.LikeTrigger';

CREATE TRIGGER IF NOT EXISTS FRIEND_ADD AFTER INSERT ON USERS_FRIEND 
	FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.trigger.FriendTrigger';

CREATE TRIGGER IF NOT EXISTS FRIEND_DEL AFTER DELETE ON USERS_FRIEND 
	FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.trigger.FriendTrigger';
