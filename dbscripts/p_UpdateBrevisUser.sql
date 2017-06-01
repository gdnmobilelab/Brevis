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
    brevis_user_firstName TEXT,
    brevis_user_lastName TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_user_morningCommuteLength INT,
    brevis_user_eveningCommuteLength INT,
    brevis_user_morningCommuteStart TIME,
    brevis_user_eveningCommuteStart TIME
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
      firstName,
      lastName,
      accountType,
      morningCommuteLength,
      eveningCommuteLength,
      morningCommuteStart,
      eveningCommuteStart;

$BODY$
LANGUAGE sql;