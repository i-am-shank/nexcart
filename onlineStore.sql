show tables;

select * from users;

select * from files;

desc files;

-- ddl-auto=update doesn't update new added values to ENUM
ALTER TABLE onlinestore.files
MODIFY COLUMN file_type ENUM(
'USER_IMAGE',
'CATEGORY_COVER_IMAGE'
);

select * from products p;

-- drop table products;

-- As ddl-auto=update doesn't update ENUM values in db  -  manually doing so
ALTER TABLE onlinestore.files
MODIFY COLUMN file_type ENUM(
'USER_IMAGE',
'CATEGORY_COVER_IMAGE',
'PRODUCT_IMAGE'
);

select * from cart c;

select * from cart_item ci;

select * from addresses a;

-- drop table addresses;

select * from orders o;

SELECT DISTINCT payment_status
FROM orders;

show create table orders;

-- ALTER TABLE orders
-- DROP CHECK orders_chk_1;


--------------------------------------------------------------------


select * from products;

select * from users;

select * from cart;
-- userId 7  -  empty cart

select * from cart_item ci where ci.cart_id = 1;

select * from addresses a where a.user_id = 2;

select * from addresses;

select * from orders;
-- 2026-06-02 07:49:40.284339

show create table orders;

-- ALTER TABLE orders
--     MODIFY COLUMN order_status ENUM(
--         'CANCELLED',
--         'PENDING',
--         'DISPATCHED',
--         'DELIVERED',
--         'RETURN_STARTED',
--         'RETURNED'
--         ) NOT NULL;

ALTER TABLE orders DROP CHECK orders_chk_2;
