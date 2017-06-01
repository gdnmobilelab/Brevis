
create extension if not exists postgis;

drop table if exists brevis_user_content_clicks;
drop table if exists brevis_user_locations;
drop table if exists brevis_user_push_subscriptions;
drop table if exists brevis_users;
drop table if exists brevis_content_briefs;
drop table if exists brevis_briefs;
drop table if exists brevis_content;

drop table if exists brevis_sessions;

create table if not exists brevis_content (
  id VARCHAR(36) PRIMARY KEY,
  path VARCHAR(4096),
  contentType VARCHAR(255),
  headline TEXT,
  sectionId VARCHAR(255),
  sectionName VARCHAR(255),
  webPublicationDateISO VARCHAR(255),
  webPublicationDateTimestamp BIGINT,
  tags JSON,
  webUrl VARCHAR(4096),
  standfirst VARCHAR(1024),
  authors JSON,
  creatorName VARCHAR(255),
  byline VARCHAR(255),
  main TEXT,
  bodyText TEXT,
  bodyHtml TEXT,
  wordCount INT,
  productionOffice VARCHAR(255),
  created_on TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  UNIQUE (path)
);

create table if not exists brevis_briefs (
  id VARCHAR(36) PRIMARY KEY,
  active BOOLEAN DEFAULT FALSE,
  dateISO VARCHAR(255),
  dateTimestamp BIGINT,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp
);

create table if not exists brevis_content_briefs (
  id BIGSERIAL PRIMARY KEY,
  contentId VARCHAR(36) REFERENCES brevis_content,
  briefId VARCHAR(36) REFERENCES brevis_briefs,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  UNIQUE (contentId, briefId)
);

do $$
DECLARE
  exists boolean;
BEGIN
  select true into exists from pg_type where typname = 'account_type';

  If exists is null then
    CREATE TYPE ACCOUNT_TYPE AS ENUM ('GOOGLE');
  end if;
END
$$;

create table if not exists brevis_users (
  id VARCHAR(36) PRIMARY KEY,
  externalId VARCHAR(255),
  accountType ACCOUNT_TYPE,
  email TEXT,
  firstName TEXT,
  lastName TEXT,
  morningCommuteLength INT DEFAULT 30 NOT NULL,
  eveningCommuteLength INT DEFAULT 30 NOT NULL,
  morningCommuteStart TIME WITHOUT TIME ZONE,
  eveningCommuteStart TIME WITHOUT TIME ZONE,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  UNIQUE(externalId, accountType)
);

create table if not exists brevis_user_content_recommendations (
  id BIGSERIAL PRIMARY KEY,
  userId VARCHAR(36) REFERENCES brevis_users(id),
  contentId VARCHAR(36) REFERENCES brevis_content(id),
  briefId VARCHAR(36) REFERENCES brevis_briefs(id),
  score DOUBLE PRECISION DEFAULT 0.0,
  read BOOLEAN DEFAULT FALSE,
  active BOOLEAN DEFAULT TRUE,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  UNIQUE(userId, contentId, briefId)
);

create table if not exists brevis_user_push_subscriptions (
  id BIGSERIAL PRIMARY KEY,
  pushSubscriptionId TEXT,
  pushSubscriptionType VARCHAR(255),
  userId VARCHAR(36) REFERENCES brevis_users(id),
  active BOOLEAN DEFAULT TRUE,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  lastUpdatedOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
  check (pushSubscriptionType = 'IOS' or pushSubscriptionType = 'WEB'),
  unique(pushSubscriptionId, pushSubscriptionType)
);

create table if not exists brevis_user_locations (
  id BIGSERIAL PRIMARY KEY,
  userId VARCHAR(36) REFERENCES brevis_users(id),
  location JSON,
  latlong geography(POINT,4326),
  dateISO VARCHAR(255),
  dateTimestamp BIGINT,
  createdOn TIMESTAMPTZ NOT NULL DEFAULT current_timestamp
);

create table if not exists brevis_user_content_clicks (
  id BIGSERIAL PRIMARY KEY,
  userId VARCHAR(36) REFERENCES brevis_users(id),
  contentId VARCHAR(36) REFERENCES brevis_content(id),
  userLocationId BIGINT REFERENCES brevis_user_locations(id),
  dateISO VARCHAR(255),
  dateTimestamp BIGINT,
  created_on TIMESTAMPTZ NOT NULL DEFAULT current_timestamp
);

create table if not exists brevis_sessions (
  id VARCHAR(255) PRIMARY KEY,
  creationTime BIGINT NOT NULL,
  lastAccessTime BIGINT NOT NULL,
  maxInactiveTime INT NOT NULL,
  sessionData bytea
);

-- GRANT ALL PRIVILEGES ON DATABASE __TABLE__ to __USER__;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public to __USER__;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO __USER__;