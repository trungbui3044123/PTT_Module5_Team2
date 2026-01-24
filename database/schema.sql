CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL DEFAULT '123456@Abc',
    email VARCHAR(150) NOT NULL UNIQUE,
    name VARCHAR(150),
    age INT CHECK (age > 18 AND age < 60),
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    role ENUM('customers', 'staff','admin','suppliers','users') NOT NULL DEFAULT 'khach',
    status ENUM('block', 'active', 'banned') NOT NULL DEFAULT 'active',
    
    -- Validate SĐT: chỉ số, 9–15 ký tự
    CONSTRAINT chk_phone_format CHECK (phone REGEXP '^[0-9]{9,15}$')
);
