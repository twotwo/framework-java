-- Account
CREATE TABLE account (
  acc_id varchar(255) NOT NULL PRIMARY KEY,
  acc_password varchar(255),
  acc_first_name varchar(255),
  acc_last_name varchar(255),
  acc_email varchar(255)
);
CREATE INDEX account_acc_id ON account(acc_id);

-- Products
CREATE TABLE products (
  id INTEGER NOT NULL PRIMARY KEY,
  description varchar(255),
  price decimal(15,2)
);
CREATE INDEX products_description ON products(description);