CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(128) NOT NULL
);

CREATE UNIQUE INDEX users_email_idx ON users(email);

INSERT INTO users VALUES ('5ddec22f-3c7c-4fea-a47f-b4a6c9bb4473', 'admin', '$2a$10$RVa2zotFQJ5bv/RplVfvOOmoG/2l/ajto.m6vnKiSy1f2AkEtn2Ni', 'ADMINISTRATOR');