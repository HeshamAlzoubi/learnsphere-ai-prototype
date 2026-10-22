-- Replace password and keep credentials outside source control.
CREATE DATABASE IF NOT EXISTS learnsphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'learnsphere_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_STRONG_LOCAL_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON learnsphere.* TO 'learnsphere_app'@'localhost';
-- This is a local development-only account; narrow migration permissions in production.
