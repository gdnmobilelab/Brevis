alter table brevis_content add constraint unique_path UNIQUE(path);

alter table brevis_user_push_subscriptions add column active boolean;

alter table brevis_user_push_subscriptions alter column active set default TRUE;