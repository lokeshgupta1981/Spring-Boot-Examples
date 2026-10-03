drop table if exists sent_message;
drop table if exists fine;
drop table if exists loan;

create table loan (
  id          bigint primary key,
  member      varchar(50)  not null,
  book        varchar(100) not null,
  due_date    date         not null,
  returned_on date
);

create table fine (
  loan_id       bigint primary key references loan (id),
  days_late     int            not null,
  amount        decimal(10, 2) not null,
  calculated_on date           not null
);

create table sent_message (
  id        bigint auto_increment primary key,
  loan_id   bigint       not null,
  kind      varchar(20)  not null,
  recipient varchar(50)  not null,
  text      varchar(200) not null,
  sent_at   timestamp    default current_timestamp
);
