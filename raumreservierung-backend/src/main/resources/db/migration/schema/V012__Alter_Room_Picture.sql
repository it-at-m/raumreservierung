create table file_attachment (
    id uuid not null,
    data bytea not null,
    file_name varchar(255) not null,
    content_type varchar(255) not null,
    file_size bigint not null,
    created_at timestamp with time zone not null,
    is_attached boolean not null default false,

    constraint pk_file_attachment primary key (id)
);

alter table room drop column if exists picture;

alter table room add column picture_id uuid;

alter table room
    add constraint fk_room_picture
        foreign key (picture_id)
            references file_attachment (id)
            on delete set null;
