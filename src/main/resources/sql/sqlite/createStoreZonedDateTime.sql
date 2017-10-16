CREATE TABLE JdsStoreZonedDateTime(
	FieldId         BIGINT,
	EntityGuid      TEXT,
	Value           BIGINT,
	PRIMARY KEY (FieldId,EntityGuid),
	FOREIGN KEY (EntityGuid) REFERENCES JdsStoreEntityOverview(EntityGuid) ON DELETE CASCADE
);