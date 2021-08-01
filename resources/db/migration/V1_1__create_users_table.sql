CREATE TABLE USERS (
  id varchar(36) primary key,
  name varchar(30) unique,
  password bytea
)
