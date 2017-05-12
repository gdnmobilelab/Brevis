create or replace function p_GetActiveBrief()
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  select
    id,
    dateISO,
    dateTimestamp
  from brevis_briefs
  where active = true;

$BODY$
LANGUAGE sql;