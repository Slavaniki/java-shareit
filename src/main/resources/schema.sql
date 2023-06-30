DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    request_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(200),
    requester_id INTEGER NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE
    CONSTRAINT requests_requester_id_fk
    FOREIGN KEY (requester_id) references users(user_id)
    );

CREATE TABLE IF NOT EXISTS items (
    item_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(200),
    available BOOLEAN,
    owner_id INTEGER NOT NULL,
    request_id INTEGER
    CONSTRAINT items_owner_id_fk
    FOREIGN KEY (owner_id) references users(user_id)
    CONSTRAINT items_request_id_fk
    FOREIGN KEY (request_id) references requests(request_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id INTEGER NOT NULL,
    booker_id INTEGER NOT NULL,
    status VARCHAR NOT NULL
    CONSTRAINT bookings_item_id_fk
    FOREIGN KEY (item_id) references items(item_id)
    CONSTRAINT bookings_booker_id_fk
    FOREIGN KEY (booker_id) references users(user_id)
);