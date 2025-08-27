--liquibase formatted sql
--changeset antoxakon:5  -- Формат: author:id
--comment: create index in table animals

create index if not exists animal_uuid_index
    on animals (animal_id);

create index if not exists animal_owner_index
    on animals (owner_id);