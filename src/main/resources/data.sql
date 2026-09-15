-- =============================================================
-- data.sql  –  Dati iniziali: artigiani_webapp
-- Caricato automaticamente da Spring Boot all'avvio
-- (spring.sql.init.mode=always, dopo schema.sql).
--
-- USA MERGE INTO (H2 upsert) invece di DELETE+INSERT
-- così i dati esistenti vengono preservati tra un riavvio e l'altro.
-- Hash BCrypt generati con BCryptPasswordEncoder (strength 10).
-- Password admin -> ad_id_07
--
-- NOTA: i video NON vengono più inseriti da qui — sono gestiti
-- esclusivamente dal pannello admin (/admin/video/new) e
-- persistono nel DB tra un riavvio e l'altro.
-- =============================================================

-- -------------------------------------------------------------
-- Utente amministratore (inserito solo se non esiste già)
-- username  : admin
-- password  : ad_id_07
-- -------------------------------------------------------------
MERGE INTO USERS (id, username, password, email, delivery_address, enabled, authority)
KEY (username)
VALUES (1, 'admin', '$2a$10$WPQYb2unD5Bu6S.wEZJpnes7jnA/0HS0G8O21vHWkrktDuWzOx6YC',
        'admin@artigiani.it', NULL, TRUE, 'ROLE_ADMIN');
