create or replace function p_GetBrevisUserPushSubscriptions()
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
LANGUAGE sql;