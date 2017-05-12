create or replace function p_UpdateBrevisUser(
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
LANGUAGE sql;