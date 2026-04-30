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

-- IV. Operación (Órdenes de Trabajo)
CREATE DATABASE IF NOT EXISTS db_service_workorders;
CREATE DATABASE IF NOT EXISTS db_service_logistics;

-- V. Cierre y Notificaciones
CREATE DATABASE IF NOT EXISTS db_service_payments;
CREATE DATABASE IF NOT EXISTS db_service_billing;
CREATE DATABASE IF NOT EXISTS db_service_notifications;

-- Base de datos extra para métricas o logs si lo deseas
CREATE DATABASE IF NOT EXISTS db_service_history;