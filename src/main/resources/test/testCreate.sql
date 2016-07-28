CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(256),
  password VARCHAR(256),
  enabled  BOOLEAN DEFAULT TRUE
);

CREATE TABLE authorities (
  username  VARCHAR(256),
  authority VARCHAR(256) DEFAULT 'USER'
);