CREATE TABLE IF NOT EXISTS account
(
    id     int         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    number varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS currency_amount
(
    id         int        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    currency   varchar(3) NOT NULL,
    amount     float      NOT NULL,
    account_id int REFERENCES account (id)
);