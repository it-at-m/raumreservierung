CREATE TABLE holidays (
    PRIMARY KEY (holiday_id),
    holiday_id      UUID        NOT NULL,
    holiday_name    VARCHAR(30) NOT NULL,
    start_date      DATE        NOT NULL,
    end_date        DATE        NOT NULL,
                    CONSTRAINT start_before_end
                    CHECK (start_date <= end_date)
);
