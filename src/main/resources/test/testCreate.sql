CREATE TABLE users (
  username VARCHAR(256),
  password VARCHAR(256),
  enabled  BOOLEAN DEFAULT TRUE
);

CREATE TABLE authorities (
  username  VARCHAR(256),
  authority VARCHAR(256) DEFAULT 'USER'
);