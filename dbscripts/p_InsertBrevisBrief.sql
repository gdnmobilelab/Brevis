create or replace function p_InsertBrevisBrief(
  IN p_id VARCHAR(36),
  IN p_dateISO VARCHAR(255),
  IN p_dateTimestamp BIGINT
)
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  insert into
    brevis_briefs (
      id,
      dateISO,
      dateTimestamp
    ) values (
      p_id,
      p_dateISO,
      p_dateTimestamp
    )
    returning
      id,
      dateISO,
      dateTimestamp;

$BODY$
LANGUAGE sql;