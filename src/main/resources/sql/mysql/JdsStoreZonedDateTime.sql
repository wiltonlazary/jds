CREATE TABLE JdsStoreZonedDateTime(
	FieldId     BIGINT,
	Uuid  VARCHAR(96),
	Value       TIMESTAMP,
	PRIMARY KEY (FieldId,Uuid),
	FOREIGN KEY (Uuid) REFERENCES JdsEntityOverview(Uuid) ON DELETE CASCADE
);