CREATE TABLE holiday (
    PRIMARY KEY (id),
    id      UUID        NOT NULL,
    name    VARCHAR(100) NOT NULL,
    start_date      DATE        NOT NULL,
    end_date        DATE        NOT NULL,
                    CONSTRAINT start_before_end
                    CHECK (start_date <= end_date)
);
