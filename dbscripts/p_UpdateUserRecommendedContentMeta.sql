create or replace function p_UpdateUserRecommendedContentMeta(
  IN p_userId VARCHAR(36),
  IN p_contentId VARCHAR(36),
  IN p_read BOOLEAN
)
  returns boolean AS
$BODY$

  update brevis_user_content_recommendations bucc
    set read = p_read
  from brevis_briefs bb
  where bucc.userId = p_userId
  and bucc.contentId = p_contentId
  and bucc.active = true
  returning true;

$BODY$
LANGUAGE sql;