INSERT INTO supplier
(
    name,
    description,
    cnpj,
    email,
    phone
)
VALUES
('TechDistribuidora','Fornecedor de tecnologia','00000000000100','tech@email.com','11999990001'),
('InfoComercio','Fornecedor de tecnologia','00000000000200','info@email.com','11999990002'),
('Digital Supplier','Fornecedor de tecnologia','00000000000300','digital@email.com','11999990003'),
('MegaInfo','Fornecedor de tecnologia','00000000000400','mega@email.com','11999990004'),
('GlobalTech','Fornecedor de tecnologia','00000000000500','global@email.com','11999990005');

INSERT INTO items
(
    name,
    description,
    quantity,
    buy_price,
    sell_price,
    supplier_id
)
SELECT
    'Produto ' || gs,
    'Descricao do produto ' || gs,
    FLOOR(RANDOM() * 100 + 1),
    ROUND((RANDOM() * 2000 + 10)::numeric, 2),
    ROUND((RANDOM() * 3000 + 20)::numeric, 2),
    FLOOR(RANDOM() * 5 + 1)
FROM generate_series(1, 500000) gs;