# Accounts schema

# --- !Ups

CREATE TABLE Account (
  id        bigint(20)    NOT NULL AUTO_INCREMENT,
  email     varchar(255)  NOT NULL,
  password  varchar(255)  NOT NULL,
  createdOn timestamp     NOT NULL DEFAULT now(),
  PRIMARY KEY (id)
);

ALTER TABLE Account ADD CONSTRAINT UQ_Account_Email UNIQUE (email);

# --- !Downs

DROP TABLE Account;