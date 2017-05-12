create or replace function p_GetRecommendedContentForUser(
  IN p_userId VARCHAR(36)
)
  returns table(
    brevis_content_id VARCHAR(36),
    brevis_content_path VARCHAR(4096),
    brevis_content_contentType VARCHAR(255),
    brevis_content_headline TEXT,
    brevis_content_sectionId VARCHAR(255),
    brevis_content_sectionName VARCHAR(255),
    brevis_content_webPublicationDateISO VARCHAR(255),
    brevis_content_webPublicationDateTimestamp BIGINT,
    brevis_content_tags TEXT,
    brevis_content_webUrl VARCHAR(4096),
    brevis_content_standfirst VARCHAR(1024),
    brevis_content_authors TEXT,
    brevis_content_creatorName VARCHAR(255),
    brevis_content_byline VARCHAR(255),
    brevis_content_main TEXT,
    brevis_content_bodyText TEXT,
    brevis_content_bodyHtml TEXT,
    brevis_content_wordCount INT,
    brevis_content_productionOffice VARCHAR(255),
    brevis_user_content_recommendation_read BOOLEAN,
    brevis_user_content_recommendation_score DOUBLE PRECISION
  ) AS
$BODY$

  select
    bc.id,
    bc.path,
    bc.contentType,
    bc.headline,
    bc.sectionId,
    bc.sectionName,
    bc.webPublicationDateISO,
    bc.webPublicationDateTimestamp,
    bc.tags::TEXT,
    bc.webUrl,
    bc.standfirst,
    bc.authors::TEXT,
    bc.creatorName,
    bc.byline,
    bc.main,
    bc.bodyText,
    bc.bodyHtml,
    bc.wordCount,
    bc.productionOffice,
    bucr.read,
    bucr.score
  from brevis_user_content_recommendations bucr
  join brevis_users bu on bu.id = bucr.userId
  join brevis_content bc on bucr.contentId = bc.id
  join brevis_briefs bb on bucr.briefId = bb.id
  where bb.active = true
  and bucr.userId = p_userId
  and bucr.active = true;

$BODY$
LANGUAGE sql;