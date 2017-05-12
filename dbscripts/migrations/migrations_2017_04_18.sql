alter table brevis_users rename column commuteLength to morningCommuteLength;
alter table brevis_users add column eveningCommuteLength INT;

alter table brevis_users alter column morningCommuteLength set default 30;
alter table brevis_users alter column eveningCommuteLength set default 30;

DROP FUNCTION p_getbrevisuser(character varying);
create or replace function p_GetBrevisUser(
  IN p_id VARCHAR(36)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLegnth INT,
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

DROP FUNCTION p_getbrevisuserbyexternalid(character varying);
create or replace function p_GetBrevisUserByExternalId(
  IN p_externalId VARCHAR(255)
)
  returns table(
    brevis_user_userId VARCHAR(36),
    brevis_user_externalId VARCHAR(255),
    brevis_user_email TEXT,
    brevis_user_accountType ACCOUNT_TYPE,
    brevis_morningCommuteLength INT,
    brevis_eveningCommuteLegnth INT,
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
LANGUAGE sql;

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

DROP FUNCTION p_updatebrevisuser(character varying,character varying,text,character varying,integer,time without time zone,time without time zone);
create or replace function p_UpdateBrevisUser(
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
    brevis_eveningCommuteLegnth INT,
    brevis_morningCommuteStart TIME,
    brevis_eveningCommuteStart TIME
  ) AS
$BODY$

  update brevis_users
    set
      externalId = p_externalId,
      email = p_email,
      accountType = p_accountType::ACCOUNT_TYPE,
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