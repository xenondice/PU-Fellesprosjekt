INSERT INTO Room (roomID, size)
VALUES ("K5-208", 8);


INSERT INTO User (username, name, password, salt, email)
VALUES ("lukasap", "Lukas Pestalozzi", "1234", "ABC", "lukasap@stud.ntnu.no");

INSERT INTO CalendarEntry (startTime, endTime, location, description, roomID, creator)
VALUES ('2015-03-03 10:33:17', '2015-03-03 13:33:17', "Gloeshaugen", "Database fellesprosjekt", "K5-208", "lukasap");

INSERT INTO Alarm (alarmTime, username, entryID)
VALUES ('2015-05-05 10:30:00','lukasap', 1);


INSERT INTO User (username, name, password, salt, email)
VALUES ("xXx69xXx", "Albert Aaberg", "Skybert", "", "albert@stud.ntnu.no");

INSERT INTO User (username, name, password, salt, email)
VALUES ("MaMo", "Magnus Moan", "Kombinert", "", "mamo@stud.ntnu.no");

INSERT INTO Invitation (username, entryID)
VALUES ("MaMo", 1);

INSERT INTO Notification (description, username)
VALUES ("Invitasjon til fellesprosjekt", "MaMo");

INSERT INTO IsAdmin (entryID, username)
VALUES (1, "xXx69xXx");

INSERT INTO Gruppe (groupname)
VALUES ("gruppe1");

INSERT INTO MemberOf (groupname, username)
VALUES ("gruppe1", "xXx69xXx");

INSERT INTO MemberOf (groupname, username)
VALUES ("gruppe1", "MaMo");


