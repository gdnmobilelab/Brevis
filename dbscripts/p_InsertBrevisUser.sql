create or replace function p_InsertBrevisUser(
  IN p_userId VARCHAR(36),
  IN p_externalId VARCHAR(255),
  IN p_email TEXT,
  IN p_firstName TEXT,
  IN p_lastName TEXT,
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
    brevis_user_firstName TEXT,
    brevis_user_lastName TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_user_morningCommuteLength INT,
    brevis_user_eveningCommuteLength INT,
    brevis_user_morningCommuteStart TIME,
    brevis_user_eveningCommuteStart TIME
  ) AS
$BODY$

insert into
  brevis_users (
    id,
    externalId,
    email,
    firstName,
    lastName,
    accountType,
    morningCommuteLength,
    eveningCommuteLength,
    morningCommuteStart,
    eveningCommuteStart
  ) values (
  p_userId,
  p_externalId,
  p_email,
  p_firstName,
  p_lastName,
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
  firstName,
  lastName,
  accountType,
  morningCommuteLength,
  eveningCommuteLength,
  morningCommuteStart,
  eveningCommuteStart;

$BODY$
LANGUAGE sql;