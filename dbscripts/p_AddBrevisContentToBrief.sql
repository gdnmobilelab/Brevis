create or replace function p_AddBrevisContentToBrief(
  IN p_contentId VARCHAR(36),
  IN p_briefId VARCHAR(36)
)
  returns table(
    brevis_content_brief_id BIGINT,
    brevis_content_brief_contentId VARCHAR(36),
    brevis_content_brief_briefid VARCHAR(36)
  ) AS
$BODY$

  insert into
    brevis_content_briefs (
      contentId,
      briefId
    ) values (
      p_contentId,
      p_briefId
    )
    returning
      id,
      contentId,
      briefId;

$BODY$
LANGUAGE sql;