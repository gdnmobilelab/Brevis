create or replace function p_MakeBrevisBriefActive(
  IN p_id VARCHAR(36)
)
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  update brevis_briefs
    set active = FALSE
    where id != p_id;

  update brevis_briefs
    set active = TRUE
    where id = p_id
    returning
      id,
      dateISO,
      dateTimestamp;

$BODY$
LANGUAGE sql;