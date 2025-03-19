-- 1. Inserindo roles
INSERT INTO role (name) VALUES
('POLICIAL'),
('AGENTE_SEGURANCA'),
('INVESTIGADOR'),
('GESTOR_SEGURANCA_PUBLICA'),
('CIDADAO_ANONIMO');

-- 2. Inserindo permissões
INSERT INTO permissions (name) VALUES
('Tela Ocorrencia'),
('Tela Detalhes'),
('Tela Mudar Detalhes Ocorrenciais'),
('Tela de Investigador'),
('Relatórios'),
('Tela Inicial dos Detalhes'),
('Login');

-- 3. Associando permissões às roles
-- POLICIAL e AGENTE_SEGUANCA - Tela ocorrencia, Tela detalhes, Tela mudar detalhes ocorrenciais, login
INSERT INTO role_permissions (role_id, permission_id) VALUES
((SELECT id FROM role WHERE name = 'POLICIAL'), (SELECT id FROM permissions WHERE name = 'Tela Ocorrencia')),
((SELECT id FROM role WHERE name = 'POLICIAL'), (SELECT id FROM permissions WHERE name = 'Tela Detalhes')),
((SELECT id FROM role WHERE name = 'POLICIAL'), (SELECT id FROM permissions WHERE name = 'Tela Mudar Detalhes Ocorrenciais')),
((SELECT id FROM role WHERE name = 'POLICIAL'), (SELECT id FROM permissions WHERE name = 'Login')),
((SELECT id FROM role WHERE name = 'AGENTE_SEGURANCA'), (SELECT id FROM permissions WHERE name = 'Tela Ocorrencia')),
((SELECT id FROM role WHERE name = 'AGENTE_SEGURANCA'), (SELECT id FROM permissions WHERE name = 'Tela Detalhes')),
((SELECT id FROM role WHERE name = 'AGENTE_SEGURANCA'), (SELECT id FROM permissions WHERE name = 'Tela Mudar Detalhes Ocorrenciais')),
((SELECT id FROM role WHERE name = 'AGENTE_SEGURANCA'), (SELECT id FROM permissions WHERE name = 'Login'));

-- INVESTIGADOR - tudo do policial + Tela de investigador
INSERT INTO role_permissions (role_id, permission_id) VALUES
((SELECT id FROM role WHERE name = 'INVESTIGADOR'), (SELECT id FROM permissions WHERE name = 'Tela Ocorrencia')),
((SELECT id FROM role WHERE name = 'INVESTIGADOR'), (SELECT id FROM permissions WHERE name = 'Tela Detalhes')),
((SELECT id FROM role WHERE name = 'INVESTIGADOR'), (SELECT id FROM permissions WHERE name = 'Tela Mudar Detalhes Ocorrenciais')),
((SELECT id FROM role WHERE name = 'INVESTIGADOR'), (SELECT id FROM permissions WHERE name = 'Tela de Investigador')),
((SELECT id FROM role WHERE name = 'INVESTIGADOR'), (SELECT id FROM permissions WHERE name = 'Login'));

-- GESTOR_SEGURANCA_PUBLICA - apenas relatórios e acesso às telas com permissão de leitura
INSERT INTO role_permissions (role_id, permission_id) VALUES
((SELECT id FROM role WHERE name = 'GESTOR_SEGURANCA_PUBLICA'), (SELECT id FROM permissions WHERE name = 'Relatórios'));

-- CIDADAO_ANONIMO - apenas tela inicial dos detalhes e login
INSERT INTO role_permissions (role_id, permission_id) VALUES
((SELECT id FROM role WHERE name = 'CIDADAO_ANONIMO'), (SELECT id FROM permissions WHERE name = 'Tela Inicial dos Detalhes')),
((SELECT id FROM role WHERE name = 'CIDADAO_ANONIMO'), (SELECT id FROM permissions WHERE name = 'Login'));