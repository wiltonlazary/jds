CREATE TABLE JdsStoreIntegerArray(
    FieldId         BIGINT,
    EntityGuid      NVARCHAR(48) NOT NULL,
    Sequence        INTEGER,
    Value           INTEGER,
    PRIMARY KEY (FieldId,EntityGuid,Sequence),
    CONSTRAINT fk_JdsStoreIntegerArray_ParentEntityGuid FOREIGN KEY (EntityGuid) REFERENCES JdsStoreEntityOverview(EntityGuid) ON DELETE CASCADE
);