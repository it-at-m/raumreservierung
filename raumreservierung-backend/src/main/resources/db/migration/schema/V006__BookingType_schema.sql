create table booking_status (
                        id uuid not null,
                        booking_state varchar(255),
                        booking_substatus varchar(255),
                        primary key (id)
);