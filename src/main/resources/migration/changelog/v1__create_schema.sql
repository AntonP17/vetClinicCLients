--liquibase formatted sql
--changeset antoxakon:1  -- Формат: author:id
--comment: create schema vet_clients

create schema if not exists vet_clients;

