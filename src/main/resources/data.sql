insert into AUTHORITIES (USERNAME, AUTHORITY) VALUES ('john', 'ROLE_USER');
insert into AUTHORITIES (USERNAME, AUTHORITY) VALUES ('sam',  'ROLE_USER');

insert into USERS (USERNAME, password, enabled)
VALUES ('john', '$2a$12$DR6tEpV3T9lVNwPunSksE.o8gmA121GOo/fwoGxJs.Wp6FotU2KRe', true);

insert into USERS (USERNAME, password, enabled)
VALUES ('sam', '$2a$12$8CJikRVwuLhea2Oo0QdLIuoHPVhFPgcmZGy69U4ins4ukLqKLedcS', true);

-- john: active link (created 2 days ago, expires in 2 days)
insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, description)
VALUES ('aBc123', 'https://www.baeldung.com/spring-boot-h2-console-error',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), null,
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 2, CURRENT_TIMESTAMP),
        'baeldung h2 error');

-- john: active link (created 2 days ago, expires in 2 days)
insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, description)
VALUES ('dEf456', 'https://support.mozilla.org/en-US/kb/xframe-neterror-page',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), null,
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 2, CURRENT_TIMESTAMP),
        '');

-- john: expired link (created 5 days ago, expired 2 days ago)
insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, description)
VALUES ('gHi789', 'https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400',
        DATEADD('DAY', -5, CURRENT_TIMESTAMP), null,
        DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP),
        '');

-- sam: active link (created 1 day ago, expires in 4 days)
insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, description)
VALUES ('jKl321', 'https://spring.io/guides/gs/spring-boot',
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), null,
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 4, CURRENT_TIMESTAMP),
        'Spring Boot guide');

-- sam: expired link (created 10 days ago, expired 3 days ago)
insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, description)
VALUES ('mNo654', 'https://docs.spring.io/spring-framework/reference/',
        DATEADD('DAY', -10, CURRENT_TIMESTAMP), null,
        DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP),
        'Spring docs - expired');

insert into Link_User (short_link_id, user_name) VALUES ('aBc123', 'john');
insert into Link_User (short_link_id, user_name) VALUES ('dEf456', 'john');
insert into Link_User (short_link_id, user_name) VALUES ('gHi789', 'john');
insert into Link_User (short_link_id, user_name) VALUES ('jKl321', 'sam');
insert into Link_User (short_link_id, user_name) VALUES ('mNo654', 'sam');

-- aBc123 (john, active) — 16 usages spread over the last 48 hours
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -2820, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -2700, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -2580, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -2400, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -2160, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -1980, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -1680, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -1440, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -1260, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE', -1080, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',  -900, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',  -720, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',  -480, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',  -240, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',  -120, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('aBc123', DATEADD('MINUTE',   -30, CURRENT_TIMESTAMP));

-- dEf456 (john, active) — 7 usages spread over the last 48 hours
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE', -2760, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE', -2640, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE', -2280, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE', -1800, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE', -1320, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE',  -600, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('dEf456', DATEADD('MINUTE',  -180, CURRENT_TIMESTAMP));

-- gHi789 (john, expired 2 days ago) — 9 usages while it was active (5 days ago → 2 days ago)
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -7080, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -6900, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -6480, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -5760, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -4800, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -4320, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -3900, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -3360, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('gHi789', DATEADD('MINUTE', -3000, CURRENT_TIMESTAMP));

-- jKl321 (sam, active) — 9 usages over the last 24 hours
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE', -1380, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE', -1260, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE', -1140, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',  -960, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',  -780, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',  -600, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',  -420, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',  -240, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('jKl321', DATEADD('MINUTE',   -60, CURRENT_TIMESTAMP));

-- mNo654 (sam, expired 3 days ago) — 9 usages while it was active (10 days ago → 3 days ago)
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE', -14400, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE', -13200, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE', -11760, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE', -10080, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE',  -8640, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE',  -7200, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE',  -6000, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE',  -4800, CURRENT_TIMESTAMP));
insert into link_usage (short_link_id, used_at) VALUES ('mNo654', DATEADD('MINUTE',  -4500, CURRENT_TIMESTAMP));
