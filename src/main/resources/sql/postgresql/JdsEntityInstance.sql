CREATE TABLE JdsEntityInstance
(
    Uuid       VARCHAR(48),
    EntityId   BIGINT,
    CONSTRAINT unique_entity_instance UNIQUE (Uuid,EntityId)
);