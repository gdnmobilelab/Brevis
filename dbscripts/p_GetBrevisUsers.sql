create or replace function p_GetBrevisUsers()
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

select
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
from brevis_users;


$BODY$
LANGUAGE sql;