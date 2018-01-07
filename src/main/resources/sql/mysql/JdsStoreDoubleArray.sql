CREATE TABLE JdsStoreDoubleArray(
    FieldId     BIGINT,
    Uuid  VARCHAR(96),
    Sequence    INT,
    Value       DOUBLE,
    PRIMARY KEY (FieldId,Uuid,Sequence),
    FOREIGN KEY (Uuid) REFERENCES JdsEntityOverview(Uuid) ON DELETE CASCADE
);