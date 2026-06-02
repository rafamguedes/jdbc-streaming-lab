CREATE TABLE supplier (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    cnpj VARCHAR(30),
    email VARCHAR(255),
    phone VARCHAR(50)
);

CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    quantity INTEGER,
    buy_price NUMERIC(19,2),
    sell_price NUMERIC(19,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    supplier_id BIGINT NOT NULL,

    CONSTRAINT fk_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES supplier(id)
);

CREATE INDEX idx_items_created_at ON items(created_at);
CREATE INDEX idx_items_created_at_name ON items(created_at, name);
CREATE INDEX idx_items_supplier_id ON items(supplier_id);