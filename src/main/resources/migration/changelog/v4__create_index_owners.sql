--liquibase formatted sql
--changeset antoxakon:4  -- Формат: author:id
--comment: create index in table owners

create index if not exists animal_owner_uuid_index
    on owners (animal_owner_uuid);

create index if not exists owner_lastname_index
    on owners (last_name);