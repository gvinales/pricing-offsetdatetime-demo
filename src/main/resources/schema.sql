CREATE TABLE PRICES
(
    ID         UUID,
    BRAND_ID   INT,
    START_DATE TIMESTAMP,
    END_DATE   TIMESTAMP,
    PRICE_LIST INT,
    PRODUCT_ID INT,
    PRIORITY   INT,
    PRICE      DECIMAL(10, 2),
    CURRENCY   VARCHAR(3),
    PRIMARY KEY (ID)
);

ALTER TABLE PRICES
    ADD COLUMN TIME_ZONE VARCHAR(255);