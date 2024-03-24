CREATE TABLE IF NOT EXISTS user_store (
    id                  BIGSERIAL PRIMARY KEY,
    first_name          VARCHAR(255) NOT NULL,
    last_name           VARCHAR(255) NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    password            VARCHAR(255),
    phone_number        VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS product (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    price           DECIMAL(10, 2) NOT NULL,
    quantity        INT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_store (
    id              BIGSERIAL PRIMARY KEY,
    id_user         BIGSERIAL,
    order_date      DATE NOT NULL,
    status          VARCHAR(20) NOT NULL,
    subtotal        DECIMAL(10, 2),

    FOREIGN KEY (id_user) REFERENCES user_store(id)
);

CREATE TABLE IF NOT EXISTS order_product (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGSERIAL,
    product_id     BIGSERIAL,

    FOREIGN KEY (order_id) REFERENCES order_store(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);