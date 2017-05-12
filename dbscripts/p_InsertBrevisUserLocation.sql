create or replace function p_InsertBrevisUserLocation(
  IN p_userId VARCHAR(36),
  IN p_location TEXT,
  IN p_lat DOUBLE PRECISION,
  IN p_lng DOUBLE PRECISION,
  IN p_dateISO VARCHAR(255),
  IN p_dateTimestamp BIGINT
)
  returns table(
    brevis_user_location_id BIGINT,
    brevis_user_location_userId VARCHAR(36),
    brevis_user_location_location TEXT,
    brevis_user_location_latitude DOUBLE PRECISION,
    brevis_user_location_longitude DOUBLE PRECISION,
    brevis_user_location_dateISO VARCHAR(255),
    brevis_user_location_dateTimestamp BIGINT
  ) AS
$BODY$

  insert into
    brevis_user_locations (
      userId,
      location,
      latlong,
      dateISO,
      dateTimestamp
    ) values (
      p_userId,
      p_location::JSON,
      ST_SetSRID(ST_MakePoint(p_lng, p_lat),4326),
      p_dateISO,
      p_dateTimestamp
    )
    returning
      id,
      userId,
      location::TEXT,
      ST_Y(latlong::geometry),
      ST_X(latlong::geometry),
      dateISO,
      dateTimestamp;

$BODY$
LANGUAGE sql;