CREATE SCHEMA `user`;

CREATE TABLE `user`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(60) NULL,
  `last_name` VARCHAR(60) NULL,
  `email` VARCHAR(60) NULL,
  PRIMARY KEY (`id`)
);


-------------------------------

CREATE SCHEMA `order`;

CREATE TABLE `order`.`order` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NULL,
  `order_amount` INT NULL,
  `user_id` INT NULL,
  PRIMARY KEY (`id`));
