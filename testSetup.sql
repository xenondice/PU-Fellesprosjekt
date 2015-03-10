INSERT INTO Room (roomID, size)
VALUES ("K5-208", 8);

INSERT INTO User (username, name, password, salt, email)
VALUES ("lukasap", "Lukas Pestalozzi", "1234", "ABC", "lukasap@stud.ntnu.no");

INSERT INTO CalendarEntry (startTime, endTime, location, description, roomID, creator)
VALUES ('2015-03-03 10:33:17', '2015-03-03 13:33:17', "Gloeshaugen", "Database fellesprosjekt", "K5-208", "lukasap");

