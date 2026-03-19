insert into AUTHORITIES (USERNAME, AUTHORITY)
VALUES ('john', 'ROLE_USER');

insert into AUTHORITIES (USERNAME, AUTHORITY)
VALUES ('sam', 'ROLE_USER');

insert into USERS (USERNAME, password, enabled)
VALUES ('john', '$2a$12$DR6tEpV3T9lVNwPunSksE.o8gmA121GOo/fwoGxJs.Wp6FotU2KRe', true);

insert into USERS (USERNAME, password, enabled)
VALUES ('sam', '$2a$12$8CJikRVwuLhea2Oo0QdLIuoHPVhFPgcmZGy69U4ins4ukLqKLedcS', true);

insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, remove_at, description)
VALUES ('21ed8d01-5247-4af3-adb0-b359391ddbc2', 'https://www.baeldung.com/spring-boot-h2-console-error',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), null, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 2, CURRENT_TIMESTAMP),
        null, 'baeldung h2 error');

insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, remove_at, description)
VALUES ('072ca373-000f-4ad7-a53e-799cef540db8', 'https://support.mozilla.org/en-US/kb/xframe-neterror-page',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), null, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 2, CURRENT_TIMESTAMP),
        null, '');

insert into link (short_link_id, original_url, created_at, updated_at, active_from, active_until, remove_at, description)
VALUES ('106eee13-66d2-4800-a8d1-542f67f108fd', 'https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400',
        DATEADD('DAY', -5, CURRENT_TIMESTAMP), null, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP),
        null, '');


insert into Link_User (short_link_id, user_name)
Values ('21ed8d01-5247-4af3-adb0-b359391ddbc2', 'john');

insert into Link_User (short_link_id, user_name)
Values ('072ca373-000f-4ad7-a53e-799cef540db8', 'john');

insert into Link_User (short_link_id, user_name)
Values ('106eee13-66d2-4800-a8d1-542f67f108fd', 'john');