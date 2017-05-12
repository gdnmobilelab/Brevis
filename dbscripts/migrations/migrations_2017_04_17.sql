alter table brevis_users add column email TEXT;

create or replace function p_InsertBrevisUser(
  IN p_userId VARCHAR(36),
  IN p_externalId VARCHAR(255),
  IN p_email TEXT,
  IN p_accountType VARCHAR(255),
  IN p_commuteLength INT,
  IN p_morningCommuteStart TIME,
  IN p_eveningCommuteStart TIME
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_commuteLength INT,
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
      commuteLength,
      morningCommuteStart,
      eveningCommuteStart
    ) values (
      p_userId,
      p_externalId,
      p_email,
      p_accountType::ACCOUNT_TYPE,
      p_commuteLength,
      p_morningCommuteStart,
      p_eveningCommuteStart
    )
    returning
      id,
      externalId,
      email,
      accountType,
      commuteLength,
      morningCommuteStart,
      eveningCommuteStart;

$BODY$
LANGUAGE sql;


DROP FUNCTION p_getbrevisuser(character varying);
create or replace function p_GetBrevisUser(
  IN p_id VARCHAR(36)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_commuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  select
    id,
    externalId,
    email,
    accountType,
    commuteLength,
    morningCommuteStart,
    eveningCommuteStart
  from brevis_users where id = p_id;


$BODY$
LANGUAGE sql;


DROP FUNCTION p_getbrevisuserbyexternalid(character varying);
create or replace function p_GetBrevisUserByExternalId(
  IN p_externalId VARCHAR(255)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_commuteLength INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  select
    id,
    externalId,
    email,
    accountType,
    commuteLength,
    morningCommuteStart,
    eveningCommuteStart
  from brevis_users where externalId = p_externalId;


$BODY$
LANGUAGE sql;

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