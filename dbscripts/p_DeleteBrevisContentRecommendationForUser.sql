create or replace function p_DeleteBrevisContentRecommendationForUser(
  IN p_userId VARCHAR(36)
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

  update brevis_user_content_recommendations bucr set
    active = false
  where
    bucr.userId = p_userId
  returning
    id,
    userId,
    contentId,
    briefId,
    score,
    active;

$BODY$
LANGUAGE sql;