create or replace function p_InsertBrevisUserContentRecommendation(
  IN p_userId VARCHAR(36),
  IN p_contentId VARCHAR(36),
  IN p_briefId VARCHAR(36),
  IN p_score DOUBLE PRECISION,
  IN p_active BOOLEAN
)
  returns table(
    brevis_user_content_recommendation_id BIGINT,
    brevis_user_content_recommendation_userId VARCHAR(36),
    brevis_user_content_recommendation_contentId VARCHAR(36),
    brevis_user_content_recommendation_briefId VARCHAR(36),
    brevis_user_content_recommendation_score DOUBLE PRECISION,
    brevis_user_content_recommendation_active BOOLEAN
  ) AS
$BODY$

  insert into
    brevis_user_content_recommendations (
      userId,
      contentId,
      briefId,
      score,
      active
    ) values (
      p_userId,
      p_contentId,
      p_briefId,
      p_score,
      p_active
    ) on conflict (userId, contentId, briefId) do update
      set active = true
    returning
      id,
      userId,
      contentId,
      briefId,
      score,
      active;

$BODY$
LANGUAGE sql;