-- =============================================================
-- schema.sql  –  Database: artigiani_webapp-DB  (H2)
-- Caricato automaticamente da Spring Boot all'avvio
-- (spring.sql.init.mode=always).
-- Corrisponde alle entità JPA definite nel package entities/.
-- =============================================================

-- -------------------------------------------------------------
-- 1. ARTISANS
--    Mappa: ArtisanEntity
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ARTISANS (
    id        BIGINT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(128)   NOT NULL,
    biography VARCHAR(1024),
    photo_url VARCHAR(512),
    location  VARCHAR(256),
    PRIMARY KEY (id)
);

-- Aggiunge le colonne se la tabella esisteva già prima della migrazione
ALTER TABLE ARTISANS ADD COLUMN IF NOT EXISTS photo_url VARCHAR(512);
ALTER TABLE ARTISANS ADD COLUMN IF NOT EXISTS location  VARCHAR(256);
ALTER TABLE ARTISANS ADD COLUMN IF NOT EXISTS region    VARCHAR(128);


-- -------------------------------------------------------------
-- 2. USERS
--    Mappa: UserEntity
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS USERS (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    username         VARCHAR(64)  NOT NULL,
    password         VARCHAR(256) NOT NULL,
    email            VARCHAR(256) NOT NULL,
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    authority        VARCHAR(64)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email)
);

-- -------------------------------------------------------------
-- 2b. SHIPPING_ADDRESSES
--     Mappa: ShippingAddressEntity
--     FK -> USERS.id  (Many-to-One)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS SHIPPING_ADDRESSES (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      BIGINT        NOT NULL,
    full_name    VARCHAR(128)  NOT NULL,
    country      VARCHAR(64)   NOT NULL DEFAULT 'Italia',
    street       VARCHAR(256)  NOT NULL,
    street_extra VARCHAR(256),
    postal_code  VARCHAR(16)   NOT NULL,
    city         VARCHAR(128)  NOT NULL,
    province     VARCHAR(64)   NOT NULL,
    is_default   BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_sa_user FOREIGN KEY (user_id) REFERENCES USERS (id)
);

-- -------------------------------------------------------------
-- 3. PRODUCTS
--    Mappa: ProductEntity
--    FK -> ARTISANS.id  (Many-to-One)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS PRODUCTS (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(128)    NOT NULL,
    description VARCHAR(2048),
    price       DECIMAL(10, 2)  NOT NULL,
    category    VARCHAR(64)     NOT NULL,
    image_url   VARCHAR(512),
    model3d_url VARCHAR(512),
    artisan_id  BIGINT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_products_artisan
        FOREIGN KEY (artisan_id) REFERENCES ARTISANS (id)
);

-- -------------------------------------------------------------
-- 4. CARTS
--    Mappa: CartEntity
--    FK -> USERS.id  (One-to-One, nullable per carrelli ospite)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CARTS (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user
        FOREIGN KEY (user_id) REFERENCES USERS (id)
);

-- -------------------------------------------------------------
-- 5. CART_ITEMS
--    Mappa: CartItemEntity
--    FK -> CARTS.id, PRODUCTS.id  (Many-to-One)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CART_ITEMS (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    cart_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)    REFERENCES CARTS    (id),
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id) REFERENCES PRODUCTS (id)
);

-- -------------------------------------------------------------
-- 6. ORDERS
--    Mappa: OrderEntity
--    FK -> USERS.id  (Many-to-One)
--    Status: PENDING | PAID | FAILED
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ORDERS (
    id                       BIGINT         NOT NULL AUTO_INCREMENT,
    user_id                  BIGINT         NOT NULL,
    stripe_payment_intent_id VARCHAR(256),
    total_amount             DECIMAL(10, 2) NOT NULL,
    status                   VARCHAR(16)    NOT NULL DEFAULT 'PENDING',
    created_at               TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES USERS (id),
    CONSTRAINT chk_orders_status
        CHECK (status IN ('PENDING', 'PAID', 'FAILED'))
);

-- -------------------------------------------------------------
-- 7. VIDEOS
--    Mappa: VideoEntity
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS VIDEOS (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    title       VARCHAR(256)  NOT NULL,
    description VARCHAR(1024),
    video_url   VARCHAR(512)  NOT NULL,
    PRIMARY KEY (id)
);

-- -------------------------------------------------------------
-- 8. PASSWORD_RESET_TOKENS
--    Mappa: PasswordResetTokenEntity
--    Token UUID con scadenza 1h, collegato a un utente
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS PASSWORD_RESET_TOKENS (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    token      VARCHAR(64)   NOT NULL,
    user_id    BIGINT        NOT NULL,
    expires_at TIMESTAMP     NOT NULL,
    used       BOOLEAN       NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_prt_token UNIQUE (token),
    CONSTRAINT fk_prt_user  FOREIGN KEY (user_id) REFERENCES USERS (id)
);
