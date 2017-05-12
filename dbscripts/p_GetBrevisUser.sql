create or replace function p_GetBrevisUser(
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
LANGUAGE sql;