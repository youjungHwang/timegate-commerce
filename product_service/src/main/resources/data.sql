-- Regular products
INSERT INTO product (product_name, price, product_type, available_from, available_until)
VALUES
    ('Regular Product 1', 1000, 'REGULAR', NULL, NULL),
    ('Regular Product 2', 1500, 'REGULAR', NULL, NULL),
    ('Regular Product 3', 2000, 'REGULAR', NULL, NULL),
    ('Regular Product 4', 2500, 'REGULAR', NULL, NULL),
    ('Regular Product 5', 3000, 'REGULAR', NULL, NULL);

-- Reserved products
INSERT INTO product (product_name, price, product_type, available_from, available_until)
VALUES
    ('Reserved Product 1', 100, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 2', 150, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 3', 130, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 4', 160, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 5', 250, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 6', 266, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 7', 300, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 8', 300, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 9', 447, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00'),
    ('Reserved Product 10', 567, 'RESERVED', '2024-02-21T18:00:00', '2024-02-21T19:00:00');

-- Stock for regular and reserved products
INSERT INTO stock (product_id, stock)
VALUES
    (1, 100),  -- Regular Product 1
    (2, 200),  -- Regular Product 2
    (3, 300),  -- Regular Product 3
    (4, 400),  -- Regular Product 4
    (5, 500),  -- Regular Product 5
    (6, 600),  -- Reserved Product 1
    (7, 700),  -- Reserved Product 2
    (8, 800),  -- Reserved Product 3
    (9, 900),  -- Reserved Product 4
    (10, 1000), -- Reserved Product 5
    (11, 1100), -- Reserved Product 6
    (12, 1200), -- Reserved Product 7
    (13, 1300), -- Reserved Product 8
    (14, 1400), -- Reserved Product 9
    (15, 1500); -- Reserved Product 10
