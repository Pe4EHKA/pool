CREATE TABLE IF NOT EXISTS clients
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name       VARCHAR(256)                        NOT NULL,
    phone      VARCHAR(50)                         NOT NULL,
    email      VARCHAR(256)                        NOT NULL,
    created_at TIMESTAMP                         NOT NULL DEFAULT now(),

    CONSTRAINT pk_client PRIMARY KEY (id),
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT unique_phone UNIQUE (phone)
);
CREATE INDEX IF NOT EXISTS idx_client_name ON clients (name);

CREATE TABLE IF NOT EXISTS working_schedule
(
    day_of_week SMALLINT NOT NULL CHECK ( day_of_week BETWEEN 1 AND 7),
    start_time  TIME     NOT NULL,
    end_time    TIME     NOT NULL,

    CONSTRAINT pk_working_schedule PRIMARY KEY (day_of_week),
    CONSTRAINT chk_working_schedule CHECK ( end_time > start_time )
);

CREATE TABLE IF NOT EXISTS schedule_exception
(
    exception_date DATE    NOT NULL,
    is_working     BOOLEAN NOT NULL DEFAULT FALSE,
    start_time     TIME,
    end_time       TIME,

    CONSTRAINT pk_schedule_exception PRIMARY KEY (exception_date),
    CONSTRAINT chk_schedule_exception CHECK ( end_time > start_time ),
    CONSTRAINT chk_schedule_is_working CHECK (
        (is_working = TRUE AND start_time IS NOT NULL AND end_time IS NOT NULL)
            OR
        (is_working = FALSE AND start_time IS NULL AND end_time IS NULL)
        )
);

CREATE TABLE IF NOT EXISTS reservation
(
    id         BIGSERIAL   NOT NULL,
    client_id  BIGINT      NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL,
    order_id   UUID        NOT NULL DEFAULT RANDOM_UUID(),
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_reservation PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE,
    CONSTRAINT unique_order_id UNIQUE (order_id),
    CONSTRAINT chk_reservation CHECK ( end_time > start_time )
);
CREATE INDEX IF NOT EXISTS idx_res_time ON reservation (start_time);
CREATE INDEX IF NOT EXISTS idx_res_client_day ON reservation (client_id, start_time);


