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
LANGUAGE sql;create or replace function p_CleanupBrevisSessions()
  returns void AS
$BODY$

delete from brevis_sessions
where lastAccessTime + maxInactiveTime < extract(epoch from now())

$BODY$
LANGUAGE sql;create or replace function p_CreateBrevisSession(
  IN p_id VARCHAR(255),
  IN p_creationTime BIGINT,
  IN p_lastAccessTime BIGINT,
  IN p_maxInactiveTime INT,
  IN p_sessionData bytea
)
  returns table(
    brevis_session_id VARCHAR(255),
    brevis_session_creation_time BIGINT,
    brevis_session_last_access_time BIGINT,
    brevis_session_max_inactive_time INT,
    brevis_session_data bytea
  ) AS
$BODY$

  insert into
    brevis_sessions (
      id,
      creationTime,
      lastAccessTime,
      maxInactiveTime,
      sessionData
    ) values (
      p_id,
      p_creationTime,
      p_lastAccessTime,
      p_maxInactiveTime,
      p_sessionData
    ) on conflict (id) do update
      set
        lastAccessTime = p_lastAccessTime,
        sessionData = p_sessionData
    returning
      id,
      creationTime,
      lastAccessTime,
      maxInactiveTime,
      sessionData;

$BODY$
LANGUAGE sql;create or replace function p_DeleteBrevisContentRecommendationForUser(
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
LANGUAGE sql;create or replace function p_FindBrevisUserContentClick(
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
LANGUAGE sql;create or replace function p_GetActiveBrief()
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  select
    id,
    dateISO,
    dateTimestamp
  from brevis_briefs
  where active = true;

$BODY$
LANGUAGE sql;create or replace function p_GetBrevisContent(
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
LANGUAGE sql;create or replace function p_GetBrevisContentFromActiveBrief()
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
  from brevis_content bc
  join brevis_content_briefs bcb on bc.id =  bcb.contentId
  join brevis_briefs bf on bcb.briefId = bf.id
  where bf.active = TRUE

$BODY$
LANGUAGE sql;create or replace function p_GetBrevisSession(
  IN p_id VARCHAR(255)
)
  returns table(
    brevis_session_id VARCHAR(255),
    brevis_session_creation_time BIGINT,
    brevis_session_last_access_time BIGINT,
    brevis_session_max_inactive_time INT,
    brevis_session_data bytea
  ) AS
$BODY$

  select
    id,
    creationTime,
    lastAccesstime,
    maxInactiveTime,
    sessionData
  from brevis_sessions
  where id = p_id;

$BODY$
LANGUAGE sql;create or replace function p_GetBrevisUser(
  IN p_id VARCHAR(36)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  select
    id,
    externalId,
    email,
    accountType,
    morningCommuteLength,
    eveningCommuteLength,
    morningCommuteStart,
    eveningCommuteStart
  from brevis_users where id = p_id;


$BODY$
LANGUAGE sql;create or replace function p_GetBrevisUserByExternalId(
  IN p_externalId VARCHAR(255)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  select
    id,
    externalId,
    email,
    accountType,
    morningCommuteLength,
    eveningCommuteLength,
    morningCommuteStart,
    eveningCommuteStart
  from brevis_users where externalId = p_externalId;


$BODY$
LANGUAGE sql;create or replace function p_GetBrevisUserPushSubscriptions()
  returns table(
    brevis_user_push_subscription_id BIGINT,
    brevis_user_push_subscription_user_id VARCHAR(36),
    brevis_user_push_subscription_push_id TEXT,
    brevis_user_push_subscription_type VARCHAR(255)
  ) AS
$BODY$

  select
      id,
      userId,
      pushSubscriptionId,
      pushSubscriptionType
  from brevis_user_push_subscriptions
  where active = true;

$BODY$
LANGUAGE sql;create or replace function p_GetBrevisUserPushSubscriptionsForUser(
  IN p_userId VARCHAR(36)
)
  returns table(
    brevis_user_push_subscription_id BIGINT,
    brevis_user_push_subscription_user_id VARCHAR(36),
    brevis_user_push_subscription_push_id TEXT,
    brevis_user_push_subscription_type VARCHAR(255)
  ) AS
$BODY$

  select
      id,
      userId,
      pushSubscriptionId,
      pushSubscriptionType
  from brevis_user_push_subscriptions
  where userId = p_userId
  and active = true;

$BODY$
LANGUAGE sql;create or replace function p_GetBrevisUsers()
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  select
    id,
    externalId,
    email,
    accountType,
    morningCommuteLength,
    eveningCommuteLength,
    morningCommuteStart,
    eveningCommuteStart
  from brevis_users;


$BODY$
LANGUAGE sql;

DROP FUNCTION p_getbrevisusers();
create or replace function p_GetBrevisUsers()
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

select
  id,
  externalId,
  email,
  accountType,
  morningCommuteLength,
  eveningCommuteLength,
  morningCommuteStart,
  eveningCommuteStart
from brevis_users;


$BODY$
LANGUAGE sql;create or replace function p_GetRecommendedContentForUser(
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
LANGUAGE sql;create or replace function p_InsertBrevisBrief(
  IN p_id VARCHAR(36),
  IN p_dateISO VARCHAR(255),
  IN p_dateTimestamp BIGINT
)
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  insert into
    brevis_briefs (
      id,
      dateISO,
      dateTimestamp
    ) values (
      p_id,
      p_dateISO,
      p_dateTimestamp
    )
    returning
      id,
      dateISO,
      dateTimestamp;

$BODY$
LANGUAGE sql;create or replace function p_InsertBrevisContent(
  IN p_id VARCHAR(36),
  IN p_path VARCHAR(4096),
  IN p_contentType VARCHAR(255),
  IN p_headline TEXT,
  IN p_sectionId VARCHAR(255),
  IN p_sectionName VARCHAR(255),
  IN p_webPublicationDateISO VARCHAR(255),
  IN p_webPublicationDateTimestamp BIGINT,
  IN p_tags TEXT,
  IN p_webUrl VARCHAR(4096),
  IN p_standfirst VARCHAR(1024),
  IN p_authors TEXT,
  IN p_creatorName VARCHAR(255),
  IN p_byline VARCHAR(255),
  IN p_main TEXT,
  IN p_bodyText TEXT,
  IN p_bodyHtml TEXT,
  IN p_wordCount INT,
  IN p_productionOffice VARCHAR(255)
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

  insert into
    brevis_content (
      id,
      path,
      contentType,
      headline,
      sectionId,
      sectionName,
      webPublicationDateISO,
      webPublicationDateTimestamp,
      tags,
      webUrl,
      standfirst,
      authors,
      creatorName,
      byline,
      main,
      bodyText,
      bodyHtml,
      wordCount,
      productionOffice
    ) values (
      p_id,
      p_path,
      p_contentType,
      p_headline,
      p_sectionId,
      p_sectionName,
      p_webPublicationDateISO,
      p_webPublicationDateTimestamp,
      p_tags::JSON,
      p_webUrl,
      p_standfirst,
      p_authors::JSON,
      p_creatorName,
      p_byline,
      p_main,
      p_bodyText,
      p_bodyHtml,
      p_wordCount,
      p_productionOffice
    ) on conflict (path) do update
      set
        path = p_path,
        contentType = p_contentType,
        headline = p_headline,
        sectionId = p_sectionId,
        sectionName = p_sectionName,
        webPublicationDateISO = p_webPublicationDateISO,
        webPublicationDateTimestamp = p_webPublicationDateTimestamp,
        tags = p_tags::JSON,
        webUrl = p_webUrl,
        standfirst = p_standfirst,
        authors = p_authors::JSON,
        creatorName = p_creatorName,
        byline = p_byline,
        main = p_main,
        bodyText = p_bodyText,
        bodyHtml = p_bodyHtml,
        wordCount = p_wordCount,
        productionOffice = p_productionOffice
    returning
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
      productionOffice;

$BODY$
LANGUAGE sql;create or replace function p_InsertBrevisUser(
  IN p_userId VARCHAR(36),
  IN p_externalId VARCHAR(255),
  IN p_email TEXT,
  IN p_accountType VARCHAR(255),
  IN p_morningCommuteLength INT,
  IN p_eveningCommuteLength INT,
  IN p_morningCommuteStart TIME,
  IN p_eveningCommuteStart TIME
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

insert into
  brevis_users (
    id,
    externalId,
    email,
    accountType,
    morningCommuteLength,
    eveningCommuteLength,
    morningCommuteStart,
    eveningCommuteStart
  ) values (
  p_userId,
  p_externalId,
  p_email,
  p_accountType::ACCOUNT_TYPE,
  p_morningCommuteLength,
  p_eveningCommuteLength,
  p_morningCommuteStart,
  p_eveningCommuteStart
)
returning
  id,
  externalId,
  email,
  accountType,
  morningCommuteLength,
  eveningCommuteLength,
  morningCommuteStart,
  eveningCommuteStart;

$BODY$
LANGUAGE sql;create or replace function p_InsertBrevisUserContentClick(
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
LANGUAGE sql;create or replace function p_InsertBrevisUserContentRecommendation(
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
LANGUAGE sql;create or replace function p_InsertBrevisUserLocation(
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
LANGUAGE sql;create or replace function p_InsertBrevisUserPushSubscription(
  IN p_userId VARCHAR(36),
  IN p_pushSubscriptionId TEXT,
  IN p_pushSubscriptionType VARCHAR(255)
)
  returns table(
    brevis_user_push_subscription_id BIGINT,
    brevis_user_push_subscription_user_id VARCHAR(36),
    brevis_user_push_subscription_push_id TEXT,
    brevis_user_push_subscription_type VARCHAR(255)
  ) AS
$BODY$

  insert into
    brevis_user_push_subscriptions (
      userId,
      pushSubscriptionId,
      pushSubscriptionType
    ) values (
      p_userId,
      p_pushSubscriptionId,
    p_pushSubscriptionType
    ) on conflict (pushSubscriptionId, pushSubscriptionType) do update
        set id = EXCLUDED.id,
            lastUpdatedOn = current_timestamp
    returning
      id,
      userId,
      pushSubscriptionId,
      pushSubscriptionType;

$BODY$
LANGUAGE sql;create or replace function p_MakeBrevisBriefActive(
  IN p_id VARCHAR(36)
)
  returns table(
    brevis_brief_id VARCHAR(36),
    brevis_brief_dateISO VARCHAR(255),
    brevis_brief_dateTimestamp BIGINT
  ) AS
$BODY$

  update brevis_briefs
    set active = FALSE
    where id != p_id;

  update brevis_briefs
    set active = TRUE
    where id = p_id
    returning
      id,
      dateISO,
      dateTimestamp;

$BODY$
LANGUAGE sql;create or replace function p_UpdateBrevisUser(
  IN p_userId VARCHAR(36),
  IN p_morningCommuteLength INT,
  IN p_eveningCommuteLength INT,
  IN p_morningCommuteStart TIME,
  IN p_eveningCommuteStart TIME
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  update brevis_users
    set
      morningCommuteLength = p_morningCommuteLength,
      eveningCommuteLength = p_eveningCommuteLength,
      morningCommuteStart = p_morningCommuteStart,
      eveningCommuteStart = p_eveningCommuteStart
    where id = p_userId
    returning
      id,
      externalId,
      email,
      accountType,
      morningCommuteLength,
      eveningCommuteLength,
      morningCommuteStart,
      eveningCommuteStart;

$BODY$
LANGUAGE sql;create or replace function p_UpdateUserRecommendedContentMeta(
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