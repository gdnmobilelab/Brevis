create or replace function p_GetBrevisSession(
  IN p_id VARCHAR(255)
)
  returns table(
    brevis_session_id VARCHAR(255),
    brevis_session_creation_time BIGINT,
    brevis_session_last_access_time BIGINT,
    brevis_session_max_inactive_time INT,
    brevis_session_data bytea
  ) AS
$BODY$

  select
    id,
    creationTime,
    lastAccesstime,
    maxInactiveTime,
    sessionData
  from brevis_sessions
  where id = p_id;

$BODY$
LANGUAGE sql;