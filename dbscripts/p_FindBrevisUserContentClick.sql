create or replace function p_FindBrevisUserContentClick(
  IN p_id VARCHAR(36),
  IN p_limit INT
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
    brevis_content_productionOffice VARCHAR(255)
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
    bc.productionOffice
  from brevis_user_content_clicks bucc
    join brevis_content bc on bucc.contentId = bc.id
    where bucc.userId = p_id
  order by webPublicationDateTimestamp DESC LIMIT p_limit;

$BODY$
LANGUAGE sql;