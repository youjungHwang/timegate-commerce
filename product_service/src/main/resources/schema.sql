CREATE TABLE IF NOT EXISTS product (
           product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
           product_name VARCHAR(255) NOT NULL,
           price DECIMAL(19,4) NOT NULL,
           product_type ENUM('REGULAR', 'RESERVED') NOT NULL,
           available_from DATETIME,
           available_until DATETIME,
           created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
           modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS stock (
         product_id BIGINT NOT NULL,
         stock BIGINT,
         PRIMARY KEY (product_id),
         CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
