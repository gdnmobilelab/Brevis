create or replace function p_InsertBrevisUser(
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
LANGUAGE sql;