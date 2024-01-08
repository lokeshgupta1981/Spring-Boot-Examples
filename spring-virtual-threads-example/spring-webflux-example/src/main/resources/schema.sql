create table product
(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    product_name  varchar(50),
    price integer
);