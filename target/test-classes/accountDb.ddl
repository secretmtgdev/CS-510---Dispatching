CREATE TABLE state_lookup (
  state_code CHAR(2) NOT NULL,
  state_name VARCHAR(30) NOT NULL,
  PRIMARY KEY (state_code)
);

CREATE TABLE postal_address (
  id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
  street_address VARCHAR(30) NOT NULL,
  city VARCHAR(30) NOT NULL,
  state_code CHAR(2) NOT NULL,
  zip_code VARCHAR(9) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT CONSULTANT_UNIQUE UNIQUE (street_address, city, state_code, zip_code),
  FOREIGN KEY (state_code) references state_lookup(state_code)
);

CREATE TABLE bank (
  routing_number VARCHAR(10) NOT NULL,
  name VARCHAR(10) NOT NULL,
  PRIMARY KEY (routing_number),
  CONSTRAINT BANK_UNIQUE UNIQUE (name)
);

CREATE TABLE account_type_lookup (
  type VARCHAR(10) NOT NULL,
  PRIMARY KEY (type)
);

CREATE TABLE bank_account (
  id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
  account_type VARCHAR(10) NOT NULL,
  account_number VARCHAR(10) NOT NULL,
  holder_name VARCHAR(30) NOT NULL,
  bank_routing VARCHAR(10) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT BANK_ACCOUNT_UNIQUE UNIQUE (account_number, bank_routing),
  FOREIGN KEY (account_type) references account_type_lookup(type)
);

CREATE TABLE account (
  id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(30) NOT NULL,
  password_hash VARCHAR(30) FOR BIT DATA NOT NULL,
  balance INTEGER NOT NULL,
  holder_name VARCHAR(30) NOT NULL,
  postal_address_id INTEGER NOT NULL,
  bank_account_id INTEGER NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT ACCOUNT_UNIQUE UNIQUE (name),
  FOREIGN KEY (postal_address_id) references postal_address(id),
  FOREIGN KEY (bank_account_id) references bank_account(id)
);
      
INSERT INTO account_type_lookup (type) VALUES ('CHECKING');
INSERT INTO account_type_lookup (type) VALUES ('SAVINGS');

INSERT INTO state_lookup (state_code, state_name) VALUES ('AL', 'Alabama');
INSERT INTO state_lookup (state_code, state_name) VALUES ('AK', 'Alaska');
INSERT INTO state_lookup (state_code, state_name) VALUES ('AS', 'American Samoa');
INSERT INTO state_lookup (state_code, state_name) VALUES ('AZ', 'Arizona');
INSERT INTO state_lookup (state_code, state_name) VALUES ('AR', 'Arkansas');
INSERT INTO state_lookup (state_code, state_name) VALUES ('CA', 'California');
INSERT INTO state_lookup (state_code, state_name) VALUES ('CO', 'Colorado');
INSERT INTO state_lookup (state_code, state_name) VALUES ('CT', 'Connecticut');
INSERT INTO state_lookup (state_code, state_name) VALUES ('DE', 'Delaware');
INSERT INTO state_lookup (state_code, state_name) VALUES ('DC', 'District of Columbia');
INSERT INTO state_lookup (state_code, state_name) VALUES ('FM', 'Federated States of Micronesia');
INSERT INTO state_lookup (state_code, state_name) VALUES ('FL', 'Florida');
INSERT INTO state_lookup (state_code, state_name) VALUES ('GA', 'Georgia');
INSERT INTO state_lookup (state_code, state_name) VALUES ('GU', 'Guam');
INSERT INTO state_lookup (state_code, state_name) VALUES ('HI', 'Hawaii');
INSERT INTO state_lookup (state_code, state_name) VALUES ('ID', 'Idaho');
INSERT INTO state_lookup (state_code, state_name) VALUES ('IL', 'Illinois');
INSERT INTO state_lookup (state_code, state_name) VALUES ('IN', 'Indiana');
INSERT INTO state_lookup (state_code, state_name) VALUES ('IA', 'Iowa');
INSERT INTO state_lookup (state_code, state_name) VALUES ('KS', 'Kansas');
INSERT INTO state_lookup (state_code, state_name) VALUES ('KY', 'Kentucky');
INSERT INTO state_lookup (state_code, state_name) VALUES ('LA', 'Louisiana');
INSERT INTO state_lookup (state_code, state_name) VALUES ('ME', 'Maine');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MH', 'Marshall Islands');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MD', 'Maryland');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MA', 'Massachusetts');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MI', 'Michigan');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MN', 'Minnesota');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MS', 'Mississippi');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MO', 'Missouri');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MT', 'Montana');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NE', 'Nebraska');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NV', 'Nevada');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NH', 'New Hampshire');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NJ', 'New Jersey');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NM', 'New Mexico');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NY', 'New York');
INSERT INTO state_lookup (state_code, state_name) VALUES ('NC', 'North Carolina');
INSERT INTO state_lookup (state_code, state_name) VALUES ('ND', 'North Dakota');
INSERT INTO state_lookup (state_code, state_name) VALUES ('MP', 'Northern Mariana Islands');
INSERT INTO state_lookup (state_code, state_name) VALUES ('OH', 'Ohio');
INSERT INTO state_lookup (state_code, state_name) VALUES ('OK', 'Oklahoma');
INSERT INTO state_lookup (state_code, state_name) VALUES ('OR', 'Oregon');
INSERT INTO state_lookup (state_code, state_name) VALUES ('PW', 'Palau');
INSERT INTO state_lookup (state_code, state_name) VALUES ('PA', 'Pennsylvania');
INSERT INTO state_lookup (state_code, state_name) VALUES ('PR', 'Puerto Rico');
INSERT INTO state_lookup (state_code, state_name) VALUES ('RI', 'Rhode Island');
INSERT INTO state_lookup (state_code, state_name) VALUES ('SC', 'South Carolina');
INSERT INTO state_lookup (state_code, state_name) VALUES ('SD', 'South Dakota');
INSERT INTO state_lookup (state_code, state_name) VALUES ('TN', 'Tennessee');
INSERT INTO state_lookup (state_code, state_name) VALUES ('TX', 'Texas');
INSERT INTO state_lookup (state_code, state_name) VALUES ('UT', 'Utah');
INSERT INTO state_lookup (state_code, state_name) VALUES ('VT', 'Vermont');
INSERT INTO state_lookup (state_code, state_name) VALUES ('VI', 'Virgin ISlands');
INSERT INTO state_lookup (state_code, state_name) VALUES ('VA', 'Virginia');
INSERT INTO state_lookup (state_code, state_name) VALUES ('WA', 'Washington');
INSERT INTO state_lookup (state_code, state_name) VALUES ('WV', 'West Virginia');
INSERT INTO state_lookup (state_code, state_name) VALUES ('WI', 'Wisconsin');
INSERT INTO state_lookup (state_code, state_name) VALUES ('WY', 'Wyoming');
