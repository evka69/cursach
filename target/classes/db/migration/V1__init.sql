
insert into roles (name)
values
('ROLE_USER')


ALTER TABLE products ADD COLUMN max_stock INTEGER DEFAULT 10 NOT NULL;
ALTER TABLE products ADD COLUMN stock INTEGER DEFAULT 10 NOT NULL;

INSERT INTO products (name, price, category, image_path, stock, max_stock) VALUES
('Бананы', 89.00, 'Фрукты', '/catalog_img/banana.jpg', 10, 10),
('Ананасы', 81.48, 'Фрукты', '/catalog_img/ananas.jpg', 10, 10),
('Свекла', 259.22, 'Овощи', '/catalog_img/svekkla.jpg', 10, 10),
('Лук', 175.00, 'Овощи', '/catalog_img/luk.jpg', 10, 10),
('Чеснок', 5759.80, 'Овощи', '/catalog_img/garlic.jpg', 10, 10),
('Молоко', 414.53, 'Молочные', '/catalog_img/milk.jpg', 10, 10),
('Йогурт', 406.20, 'Молочные', '/catalog_img/yogurt.jpg', 10, 10),
('Творог', 79.90, 'Молочные', '/catalog_img/tvorog.jpg', 10, 10),
('Печенье', 216.50, 'К чаю', '/catalog_img/pechenki.jpg', 10, 10),
('Пирожное', 32.00, 'К чаю', '/catalog_img/pir.jpg', 10, 10),
('Торт', 307.58, 'К чаю', '/catalog_img/cake.jpg', 10, 10),
('Пельмени', 92.26, 'Замороженные', '/catalog_img/pelmen.jpg', 10, 10),
('Оладьи', 142.90, 'Замороженные', '/catalog_img/oladyi.jpg', 10, 10),
('Овощной микс', 2730.60, 'Замороженные', '/catalog_img/veget.jpg', 10, 10);


select * from "public".roles;




START TRANSACTION;
INSERT INTO roles (name) VALUES ('ROLE_USER');
SELECT * FROM roles WHERE name = 'ROLE_USER';
COMMIT;

SELECT id, name, stock, max_stock FROM products;

UPDATE products SET stock = 10;