-- Производители
CREATE TABLE IF NOT EXISTS manufacturers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    contact_phone VARCHAR(20)
);

-- Поставщики
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(255),
    manufacturer_id BIGINT,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
);

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    discount DOUBLE
);
-- Товары
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE,
    manufacturer_id BIGINT,
    supplier_id BIGINT,
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);
);

CREATE TABLE finances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(10) CHECK (type IN ('income', 'expense')),
    amount DOUBLE NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(200)
);