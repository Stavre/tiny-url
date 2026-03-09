# Tiny URL

Inspired by [this challenge](https://codingchallenges.fyi/challenges/challenge-url-shortener/)

## User types

The app has two types of users: anonymous and authenticated.
Anonymous users don't log in. They can only create 3-day valid links.

Authenticated users have accounts they use to log in. They have a dashboard with their links.
When creating links they can set the validity interval, as well as a short description.
They can delete or edit their own links.

## Link generation

The shortened version isn't really that short. It's basically an UUID generated 
by the app. Smarter approaches could have been used, but then I would have had to deal with collisions.

### Idempotency

The challenge specifies creating links should be idempotent. There are some practical issues with that.
Two different authenticated users must always get different short links even for the same long url.
Otherwise, both users would have access to the same link. Additionally, this would create issues in
logging link usage.

Idempotency would definitely work for anonymous links if they would not have a fixed 3-day validity period.
It would be strange to create a link and have it expire in one day just because a stranger generated the same link two days ago.

## General architecture

This app is a simple spring boot app that uses Thymeleaf for template rendering and H2 as an in-memory database.
Services, Controllers, Repositories and html templates are split between user types. This makes it easier to work
only on one user-type feature, or to add a new type of user (maybe corporate user).

### API

There are three main paths: /anonymous, /user, and /redirect.

/anonymous and /redirect do not require authentication.
/user does require authentication.

/redirect is used for redirecting short links to the original link.


## Differences between this solution and the challenge

The challenge does not mention users or accounts, making the problem simpler than my approach
(It hints at this possibility in the Going Further section).

By having two different types of users we need to somehow differentiate between the links.
For example, authenticated users might be interested in some sort of analytics regarding the link access.
That means we would need to log usage of authenticated users' links.


