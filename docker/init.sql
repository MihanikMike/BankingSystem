CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(50) PRIMARY KEY,
    owner VARCHAR(100) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL
    );

INSERT INTO accounts (account_number, owner, balance)
VALUES
    ('ACC1001', 'Mike', 1000.00),
    ('ACC1002', 'John', 500.00)
    ON CONFLICT (account_number) DO NOTHING;