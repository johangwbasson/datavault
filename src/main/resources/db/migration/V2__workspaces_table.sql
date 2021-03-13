CREATE TABLE workspaces
(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    created TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT false
);

CREATE INDEX workspaces_user_idx ON workspaces(user_id);