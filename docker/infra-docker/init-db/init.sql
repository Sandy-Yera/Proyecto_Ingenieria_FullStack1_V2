-- I. Núcleo de Identidad y Seguridad
CREATE DATABASE IF NOT EXISTS db_service_auth;
CREATE DATABASE IF NOT EXISTS db_service_security;
CREATE DATABASE IF NOT EXISTS db_service_users;
CREATE DATABASE IF NOT EXISTS db_service_buildings;

-- II. Ciclo de Vida (Cotizaciones y Precios)
CREATE DATABASE IF NOT EXISTS db_service_quotes;
CREATE DATABASE IF NOT EXISTS db_service_price_engine;

-- III. Logística y Staff
CREATE DATABASE IF NOT EXISTS db_service_staff;
CREATE DATABASE IF NOT EXISTS db_service_fleet;
CREATE DATABASE IF NOT EXISTS db_service_schedule;
CREATE DATABASE IF NOT EXISTS db_service_inventory;
CREATE DATABASE IF NOT EXISTS db_service_purchases;

-- IV. Operación (Órdenes de Trabajo)
CREATE DATABASE IF NOT EXISTS db_service_workorders;
CREATE DATABASE IF NOT EXISTS db_service_logistics;

-- V. Cierre y Notificaciones
CREATE DATABASE IF NOT EXISTS db_service_payments;
CREATE DATABASE IF NOT EXISTS db_service_billing;
CREATE DATABASE IF NOT EXISTS db_service_notifications;

-- Logs
CREATE DATABASE IF NOT EXISTS db_service_logs;

-- Crear un usuario único para todos los microservicios (para acelerar la integración, )
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'app_user'@'%';
FLUSH PRIVILEGES;

/*
Futuramente borrar y mejorar para cada microservicio, optando así por independencia y seguridad. Ejemplo:
CREATE USER IF NOT EXISTS 'user_auth'@'%' IDENTIFIED BY 'password'; --Ojo con la contraseña
GRANT ALL PRIVILEGES ON db_service_auth.* TO 'user_auth'@'%';
*/