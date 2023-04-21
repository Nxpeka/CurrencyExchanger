CREATE TABLE IF NOT EXISTS Exchange_Rates(
    id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    base_currency_id int NOT NULL REFERENCES Currencies (id) UNIQUE,
    target_currency_id int NOT NULL  REFERENCES Currencies (id) UNIQUE,
    rate double precision NOT NULL
);