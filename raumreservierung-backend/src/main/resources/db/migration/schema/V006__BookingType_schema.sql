create table booking_type (
                        id uuid not null,
                        booking_status varchar(255) not null unique,
                        color varchar(255) not null,
                        is_blocking boolean not null,
                        primary key (id)
);