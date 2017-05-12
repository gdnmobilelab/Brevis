alter table brevis_user_content_recommendations add column read boolean default false;
alter table brevis_user_content_recommendations add column createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp;

create or replace function p_MarkUserRecommendedContentRead(
  IN p_userId VARCHAR(36),
  IN p_contentId VARCHAR(36)
)
  returns boolean AS
$BODY$

update brevis_user_content_recommendations bucc
set read = true
from brevis_briefs bb
where bucc.userId = p_userId
      and bucc.contentId = p_contentId
      and bucc.active = true
returning true;

$BODY$
LANGUAGE sql;