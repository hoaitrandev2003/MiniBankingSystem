CREATE DATABASE mini_banking;
USE mini_banking;

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description NVARCHAR(100)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    phone VARCHAR(20),
    gender VARCHAR(10),
    date_of_birth DATE,
    identity_number VARCHAR(20),
    address VARCHAR(255),
    failed_login_attempt INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    role_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE bank_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    account_number VARCHAR(20) UNIQUE,
    balance DECIMAL(19,2) DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'VND',
    account_type VARCHAR(20),
    daily_transfer_limit DECIMAL(19,2),
    version BIGINT DEFAULT 0,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50),
    name NVARCHAR(100),
    description NVARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(50) UNIQUE,
    from_account_id INT,
    to_account_id INT,
    amount DECIMAL(19,2),
    transaction_type VARCHAR(30),
    transaction_categories_id INT,
    status VARCHAR(30),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT,
    action VARCHAR(100),
    old_balance DECIMAL(19,2),
    new_balance DECIMAL(19,2),
    performed_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    token VARCHAR(255),
    expiry_date TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE
);


ALTER TABLE users ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id)REFERENCES roles(id);
ALTER TABLE bank_accounts ADD CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)REFERENCES users(id);
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_from_account FOREIGN KEY (from_account_id)REFERENCES bank_accounts(id);
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_to_account FOREIGN KEY (to_account_id)REFERENCES bank_accounts(id);
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_category FOREIGN KEY (transaction_categories_id)REFERENCES transaction_categories(id);
ALTER TABLE transaction_audit_logs ADD CONSTRAINT fk_audit_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id);
ALTER TABLE transaction_audit_logs ADD CONSTRAINT fk_audit_user FOREIGN KEY (performed_by) REFERENCES users(id);
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_token_user FOREIGN KEY (user_id)REFERENCES users(id);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_accounts_user ON bank_accounts(user_id);
CREATE INDEX idx_transactions_from ON transactions(from_account_id);
CREATE INDEX idx_transactions_to ON transactions(to_account_id);
CREATE INDEX idx_transaction_code ON transactions(transaction_code);