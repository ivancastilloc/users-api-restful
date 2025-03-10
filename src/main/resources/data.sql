-- Insertar usuarios
INSERT INTO users (id, name, email, password, is_active, created, modified, last_login, token) 
VALUES 
  (1, 'Juan Rodriguez', 'juan@rodriguez.org', 'hunter2', true, NOW(), NOW(), NOW(), 'aabbccddeeffgghhiijjkkllmmnnoopp'),
  (2, 'Tony Stark', 'tony@starkindustries.com', 'ironman123', true, NOW(), NOW(), NOW(), 'xxxyyyzzz123abc');


-- Insertar teléfonos asociados a los usuarios
INSERT INTO phones (id, number, citycode, countrycode, user_id) 
VALUES 
  (1, '123456789', '1', '52', 1), -- Teléfono de Juan Rodriguez
  (2, '987654321', '2', '52', 1), -- Otro teléfono de Juan Rodriguez
  (3, '5558675309', '212', '1', 2), -- Teléfono de Tony Stark
  (4, '5551234567', '310', '1', 2); -- Otro teléfono de Tony Stark
