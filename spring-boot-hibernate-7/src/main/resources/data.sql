insert into mechanic (name) values ('Anna'), ('Ravi');

insert into repair_job (bike_model, problem, status, cost, created_at, mechanic_id) values
  ('Trek', 'flat tire', 'DONE', 15.00, current_timestamp, 1),
  ('Giant', 'brakes', 'IN_PROGRESS', 40.00, current_timestamp, 1),
  ('Brompton', 'gears', 'RECEIVED', 25.00, current_timestamp, 2),
  ('Cube', 'chain', 'DONE', 30.00, current_timestamp, 2);
