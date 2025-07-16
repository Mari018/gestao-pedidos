
-- Drop existing tables if they exist to ensure a clean slate
IF OBJECT_ID('order_status_history', 'U') IS NOT NULL DROP TABLE order_status_history;
IF OBJECT_ID('requests', 'U') IS NOT NULL DROP TABLE requests;
IF OBJECT_ID('error_logs', 'U') IS NOT NULL DROP TABLE error_logs;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;

---

-- Table: users
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    is_validated BIT NOT NULL
);

---

-- Table: error_logs
CREATE TABLE error_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    error_type NVARCHAR(255) NOT NULL,
    error_message NVARCHAR(1000) NOT NULL,
    stack_trace NVARCHAR(MAX), -- Use NVARCHAR(MAX) for potentially very long strings
    endpoint NVARCHAR(255),
    http_method NVARCHAR(10),
    user_email NVARCHAR(255),
    occurred_at DATETIME2 NOT NULL, -- DATETIME2 is preferred for precision
    resolved BIT NOT NULL DEFAULT 0,
    resolution_notes NVARCHAR(MAX),
    resolved_at DATETIME2
);

---

-- Table: requests (representing Order entity)
CREATE TABLE requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    client_name NVARCHAR(255) NOT NULL,
    client_email NVARCHAR(255) NOT NULL,
    creation_date DATETIME2 NOT NULL,
    request_state NVARCHAR(50) NOT NULL, -- Storing enum as string
    value REAL NOT NULL, -- REAL is for single-precision float
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2
);

---

-- Table: order_status_history
CREATE TABLE order_status_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status NVARCHAR(50) NOT NULL, -- Storing enum as string
    changed_at DATETIME2 NOT NULL,
    changed_by NVARCHAR(255),
    CONSTRAINT FK_OrderStatusHistory_Order FOREIGN KEY (order_id) REFERENCES requests(id)
);

---

-- Re-enable referential integrity checks
-- EXEC sp_MSforeachtable "ALTER TABLE ? CHECK CONSTRAINT all";