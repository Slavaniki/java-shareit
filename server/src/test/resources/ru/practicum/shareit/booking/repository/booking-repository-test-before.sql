INSERT INTO users (user_id, name, email)
VALUES (1, 'Adam', 'adam@paradise.comm');

INSERT INTO users (user_id, name, email)
VALUES (2, 'Eva', 'eva@paradise.com');

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (4, 'great garden without people', 2, '2022-10-10 12:00:00');

INSERT INTO items (item_id, name, description, available, owner_id, request_id)
VALUES (3, 'Paradise', 'great garden without people', true, 1, 4);

INSERT INTO items (item_id, name, description, available, owner_id, request_id)
VALUES (5, 'Apple', 'very tasty fruit', true, 2, null);

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (4, '2023-10-20 12:30:00', '2023-10-21 13:35:00', 3, 2, 'WAITING');

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (6, '2023-10-27 12:35:00', '2023-10-28 13:00:00', 5, 1, 'WAITING');

INSERT INTO bookings (booking_id, start_date, end_date, item_id, booker_id, status)
VALUES (7, '2022-10-25 12:30:00', '2023-10-30 13:35:00', 5, 1, 'WAITING');
