CREATE PROCEDURE proc_store_blob(IN PUUID VARCHAR(96), IN PFIELD_ID BIGINT, IN PVALUE BLOB)
  BEGIN
    INSERT INTO jds_store_blob (uuid, field_id, value)
    VALUES (puuid, pfield_id, pvalue)
    ON DUPLICATE KEY UPDATE value = pvalue;
  END