# Accounts schema

# --- !Ups

CREATE TABLE Account (
  id        BIGINT(20)    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email     VARCHAR(255)  NOT NULL CONSTRAINT UQ_Account_Email UNIQUE,
  password  VARCHAR(255)  NOT NULL,
  createdOn TIMESTAMP     NOT NULL DEFAULT now()
);

-- test
INSERT INTO Account(email, password) VALUES('kyle@test.com', '$2a$10$Iw0veTcNrcfVLFJAcifk4eZpoJ0I3X5clebnHm.nSFT3GdqgF/v9K');

CREATE TABLE ApiAccessor (
  id          BIGINT(20)    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_id  BIGINT(20)    NOT NULL REFERENCES Account,
  name        VARCHAR(55)   NOT NULL,
  key         VARCHAR(500)  NOT NULL,
  secret      VARCHAR(500)  NULL,
  createdOn   TIMESTAMP     NOT NULL DEFAULT now()
)

# --- !Downs

DROP TABLE Account;
DROP TABLE ApiAccessor;