create table if not exists visit (
  id         bigserial primary key,
  pod        varchar(100) not null,
  visited_at timestamp    not null default now()
);
