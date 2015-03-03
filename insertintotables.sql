INSERT INTO Room (roomID, size)
VALUES ("K5-208", 8)
;

INSERT INTO Entry (EventID, startTime, endTime, location, description, roomID)
VALUES (1,'2015-03-03 10:33:17', '2015-03-03 13:33:17', "Gloeshaugen", "Database fellesprosjekt", "K5-208")
;


INSERT INTO User (username, name, password, salt, email)
VALUES ("xXx69xXx", "Albert Aaberg", "Skybert", "", "albert@stud.ntnu.no")
;

INSERT INTO User (username, name, password, salt, email)
VALUES ("MaMo", "Magnus Moan", "Kombinert", "", "mamo@stud.ntnu.no")
;

INSERT INTO Invitation (username, entryID)
VALUES ("MaMo", 1)
;

INSERT INTO Notification (description, username, entryID)
VALUES ("Invitasjon til fellesprosjekt", "MaMo", 1)
;

INSERT INTO IsAdmin (entryID, username)
VALUES (1, "xXx69xXx")
;

INSERT INTO Gruppe (groupID)
VALUES ("gruppe1")
;

INSERT INTO MemberOf (groupID, username)
VALUES ("gruppe1", "xXx69xXx")
;

INSERT INTO MemberOf (groupID, username)
VALUES ("gruppe1", "MaMo")
;