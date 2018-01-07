CREATE TABLE JdsStoreDouble(
	FieldId     BIGINT,
	Uuid  VARCHAR(96),
	Value       DOUBLE,
	PRIMARY KEY (FieldId,Uuid),
	FOREIGN KEY (Uuid) REFERENCES JdsEntityOverview(Uuid) ON DELETE CASCADE
);