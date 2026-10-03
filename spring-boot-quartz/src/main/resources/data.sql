insert into loan (id, member, book, due_date, returned_on) values
  (1, 'anna',  'Dune',    dateadd('DAY', 1, current_date),  null),
  (2, 'ben',   'Emma',    dateadd('DAY', 3, current_date),  null),
  (3, 'carla', 'Ulysses', dateadd('DAY', -4, current_date), null),
  (4, 'dev',   'Hamlet',  dateadd('DAY', -10, current_date), null),
  (5, 'eva',   'Walden',  dateadd('DAY', -2, current_date), dateadd('DAY', -1, current_date));
