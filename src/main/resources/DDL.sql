CREATE TABLE IF NOT EXISTS fridge (
  id integer AUTO_INCREMENT PRIMARY KEY,
  name varchar(255)
);

CREATE TABLE IF NOT EXISTS item (
  id integer AUTO_INCREMENT PRIMARY KEY,
  fridge_id bigint NOT NULL REFERENCES fridge (id) ON DELETE CASCADE,
  name varchar(255),
  type varchar(255),
  CONSTRAINT fridge_fk foreign key(fridge_id) references fridge(id) ON DELETE CASCADE
);
