create or replace function p_CleanupBrevisSessions()
  returns void AS
$BODY$

delete from brevis_sessions
where lastAccessTime + maxInactiveTime < extract(epoch from now())

$BODY$
LANGUAGE sql;