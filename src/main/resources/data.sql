-- data.sql

-- Inserindo papéis (roles)
INSERT INTO role (name) VALUES
('ADMIN'),
('PUBLICO'),
('ADMIN2'),
('ADMIN3'),
('ADMIN4');

INSERT INTO users (username, password) VALUES
('admin', '$2a$10$XgTR9fHHzB0IQi3xV0W0g.mS2INXDr5L7.OaOkHcdwhHZPuhzoQu.'),  -- senha: adminpassword
('public_user', '$2a$10$7Y2Iub7PUl8gA8E0FJ8TYC1ePjpmTTW1zcoLOy.N7sAvz7QVVjsr2'),  -- senha: publicpassword
('admin2', '$2a$10$7oOuGj8eqnvmvS.Z9d3iH7gxhI3y3hK51I7XzOxShhFcChvN95cT2'),  -- senha: admin2password
('admin3', '$2a$10$rgSkQ2vS5Fl5D4KfC28Fsu6r7pTyH28jc5pYfrQe5LzH5Xcm7tS4m'),  -- senha: admin3password
('admin4', '$2a$10$tnLtECM0hrh.sHGELzptbW9wToY7au42oi2s6pz7f7.PYTt2c9TOO');  -- senha: admin4password

-- Associando usuários com papéis (roles)
-- Associa o usuário "admin" com todos os papéis
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM role WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM role WHERE name = 'ADMIN2')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM role WHERE name = 'ADMIN3')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM role WHERE name = 'ADMIN4'));

-- Associa o usuário "public_user" com o papel "PUBLICO"
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'public_user'), (SELECT id FROM role WHERE name = 'PUBLICO'));

-- Associa o usuário "admin2" com o papel "ADMIN2"
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin2'), (SELECT id FROM role WHERE name = 'ADMIN2'));

-- Associa o usuário "admin3" com o papel "ADMIN3"
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin3'), (SELECT id FROM role WHERE name = 'ADMIN3'));

-- Associa o usuário "admin4" com o papel "ADMIN4"
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin4'), (SELECT id FROM role WHERE name = 'ADMIN4'));
