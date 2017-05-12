create or replace function p_GetBrevisContent(
  IN p_id VARCHAR(36)
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
    id,
    path,
    contentType,
    headline,
    sectionId,
    sectionName,
    webPublicationDateISO,
    webPublicationDateTimestamp,
    tags::TEXT,
    webUrl,
    standfirst,
    authors::TEXT,
    creatorName,
    byline,
    main,
    bodyText,
    bodyHtml,
    wordCount,
    productionOffice
  from brevis_content
  where id = p_id;

$BODY$
LANGUAGE sql;