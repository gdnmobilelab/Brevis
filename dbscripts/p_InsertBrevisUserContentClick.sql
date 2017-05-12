create or replace function p_InsertBrevisUserContentClick(
  IN p_userId VARCHAR(36),
  IN p_contentId VARCHAR(36),
  IN p_userLocationId BIGINT,
  IN p_dateISO VARCHAR(255),
  IN p_dateTimestamp BIGINT
)
  returns table(
    brevis_user_content_click_id BIGINT,
    brevis_user_content_click_userId VARCHAR(36),
    brevis_user_content_click_contentId VARCHAR(36),
    brevis_user_content_click_locationId BIGINT,
    brevis_user_content_click_dateISO VARCHAR(255),
    brevis_user_content_click_dateTimestamp BIGINT,
    brevis_user_location_id BIGINT,
    brevis_user_location_userId VARCHAR(36),
    brevis_user_location_location TEXT,
    brevis_user_location_latitude DOUBLE PRECISION,
    brevis_user_location_longitude DOUBLE PRECISION,
    brevis_user_location_dateISO VARCHAR(255),
    brevis_user_location_dateTimestamp BIGINT
  ) AS
$BODY$

  with new_brevis_content_click as (
    insert into
    brevis_user_content_clicks (
      userId,
      contentId,
      userLocationId,
      dateISO,
      dateTimestamp
    ) values (
      p_userId,
      p_contentId,
      p_userLocationId,
      p_dateISO,
      p_dateTimestamp
    ) returning
      id,
      userId,
      contentId,
      userLocationId,
      dateISO,
      dateTimestamp
  ) select
      nbcc.id,
      nbcc.userId,
      nbcc.contentId,
      nbcc.userLocationId,
      nbcc.dateISO,
      nbcc.dateTimestamp,
      bul.id,
      bul.userId,
      bul.location::TEXT,
      ST_Y(bul.latlong::geometry),
      ST_X(bul.latlong::geometry),
      bul.dateISO,
      bul.dateTimestamp
  from new_brevis_content_click nbcc
  left join brevis_user_locations bul on nbcc.userLocationId = bul.id;

$BODY$
LANGUAGE sql;