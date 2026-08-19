INSERT INTO employees (
    id,
    first_name,
    last_name,
    email,
    password_hash,
    role,
    active
)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'System',
           'Administrator',
           'admin@lending.local',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'ADMIN',
           TRUE
       );