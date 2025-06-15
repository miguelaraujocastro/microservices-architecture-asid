```sql
-- Conteúdo de init-db.sql
CREATE DATABASE IF NOT EXISTS userdb;
CREATE DATABASE IF NOT EXISTS bookdb;
CREATE DATABASE IF NOT EXISTS cartdb;
CREATE DATABASE IF NOT EXISTS orderdb;
CREATE DATABASE IF NOT EXISTS shippingdb;

-- Tabelas para userdb
USE userdb;
CREATE TABLE IF NOT EXISTS user (
    userid INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


USE bookdb;

CREATE TABLE IF NOT EXISTS author (
    authorid INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
    categoryid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS subcategory (
    subcategoryid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(categoryid)
);

CREATE TABLE IF NOT EXISTS book (
    bookid INT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    image VARCHAR(255),
    isbn_number VARCHAR(50) UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author_id INT NOT NULL,
    category_id INT NOT NULL,
    subcategory_id INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES author(authorid),
    FOREIGN KEY (category_id) REFERENCES category(categoryid),
    FOREIGN KEY (subcategory_id) REFERENCES subcategory(subcategoryid)
);


-- Exemplo para cartdb.cart
USE cartdb;
CREATE TABLE IF NOT EXISTS cart (
    cartid INT AUTO_INCREMENT PRIMARY KEY,
    created_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES userdb.user(userid)
);

-- Exemplo para cartdb.cart_item (depende de cart e book)
USE cartdb;
CREATE TABLE IF NOT EXISTS cart_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    sub_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES cart(cartid),
    FOREIGN KEY (book_id) REFERENCES bookdb.book(id)
);



USE orderdb;

CREATE TABLE IF NOT EXISTS orders (
    orderid INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME NOT NULL,
    shippingorder_id INT,
    total_price DECIMAL(10, 2) NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_details (
    order_detailsid INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    sub_total DECIMAL(10, 2) NOT NULL,
    user_id INT NOT NULL,
    order_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(orderid)
);

USE shippingdb;

CREATE TABLE IF NOT EXISTS shipping_order (
    shippingorder_id INT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    order_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orderdb.orders(orderid)
);


-- Agora, insira os dados de teste para author, category, subcategory
-- Use IF NOT EXISTS ou IGNORE para evitar erros se o script for executado várias vezes
INSERT IGNORE INTO author (id, name) VALUES (1, 'José Saramago');
INSERT IGNORE INTO author (id, name) VALUES (2, 'J.K. Rowling');

INSERT IGNORE INTO category (id, name) VALUES (1, 'Ficção');
INSERT IGNORE INTO category (id, name) VALUES (2, 'Fantasia');

INSERT IGNORE INTO subcategory (id, name, category_id) VALUES (1, 'Distopia', 1);
INSERT IGNORE INTO subcategory (id, name, category_id) VALUES (2, 'Magia', 2);
