create or replace function p_CreateBrevisSession(
  IN p_id VARCHAR(255),
  IN p_creationTime BIGINT,
  IN p_lastAccessTime BIGINT,
  IN p_maxInactiveTime INT,
  IN p_sessionData bytea
)
  returns table(
    brevis_session_id VARCHAR(255),
    brevis_session_creation_time BIGINT,
    brevis_session_last_access_time BIGINT,
    brevis_session_max_inactive_time INT,
    brevis_session_data bytea
  ) AS
$BODY$

  insert into
    brevis_sessions (
      id,
      creationTime,
      lastAccessTime,
      maxInactiveTime,
      sessionData
    ) values (
      p_id,
      p_creationTime,
      p_lastAccessTime,
      p_maxInactiveTime,
      p_sessionData
    ) on conflict (id) do update
      set
        lastAccessTime = p_lastAccessTime,
        sessionData = p_sessionData
    returning
      id,
      creationTime,
      lastAccessTime,
      maxInactiveTime,
      sessionData;

$BODY$
LANGUAGE sql;