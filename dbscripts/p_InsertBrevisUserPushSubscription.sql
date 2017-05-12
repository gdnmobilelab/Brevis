create or replace function p_InsertBrevisUserPushSubscription(
  IN p_userId VARCHAR(36),
  IN p_pushSubscriptionId TEXT,
  IN p_pushSubscriptionType VARCHAR(255)
)
  returns table(
    brevis_user_push_subscription_id BIGINT,
    brevis_user_push_subscription_user_id VARCHAR(36),
    brevis_user_push_subscription_push_id TEXT,
    brevis_user_push_subscription_type VARCHAR(255)
  ) AS
$BODY$

  insert into
    brevis_user_push_subscriptions (
      userId,
      pushSubscriptionId,
      pushSubscriptionType
    ) values (
      p_userId,
      p_pushSubscriptionId,
    p_pushSubscriptionType
    ) on conflict (pushSubscriptionId, pushSubscriptionType) do update
        set id = EXCLUDED.id,
            lastUpdatedOn = current_timestamp
    returning
      id,
      userId,
      pushSubscriptionId,
      pushSubscriptionType;

$BODY$
LANGUAGE sql;