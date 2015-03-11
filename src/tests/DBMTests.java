package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import calendar.Alarm;
import calendar.CalendarEntry;
import calendar.Invitation;
import room_booking.Room;
import user.Group;
import user.User;
import dbms.DataBaseManager;
import exceptions.AlarmAlreadyExistsException;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupAlreadyExistsException;
import exceptions.InvitationAlreadyExistsException;
import exceptions.RoomAlreadyExistsException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;

public class DBMTests {
	
	private DataBaseManager dbm;
	private Connection connection;
	User u = new User("lukasap", "Lukas Pestalozzi", "1234", "ABC", "lukasap@stud.ntnu.no");
	Room r = new Room("K5-208", 8);
	CalendarEntry e;
	Alarm a = new Alarm(100, "lukasap", 1);
	Invitation inv = new Invitation(true, true, "lukasap", 1);

	@Before
	public void setUp() throws Exception {
		dbm = new DataBaseManager();
		
		// inserts standarddata into DB
		try {
			dbm.addSQL("addtables.sql");
			dbm.addSQL("testSetup.sql");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dbm = new DataBaseManager(); // refresch the connection
		Class.forName("com.mysql.jdbc.Driver");
		String[] ci= {"jdbc:mysql://mysql.stud.ntnu.no/mariessa_pu", "mariessa_pu","fellesprosjekt" };
		connection = DriverManager.getConnection(ci[0], ci[1], ci[2]);

		
		e = new CalendarEntry(getlastEntryID(), 100, 100000, "Gloeshaugen", "Database fellesprosjekt", "K5-208", "lukasap");
	}

	@After
	public void tearDown() throws Exception {
		dbm.close();
		connection.close();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void testAddUser() {
		// remove user if it is there
		User testU = new User("testU", "as", "sdfsad", "asdf", "fsda");
		try {
			connection.createStatement().executeUpdate("DELETE FROM User WHERE username = '"+testU.getUsername()+"';");
		} catch (SQLException e1) {
			
			e1.printStackTrace();
			fail("WTF?");
		}
		
		// check adding a user correctly
		try {
			assertTrue(isUserThere(testU.getUsername()) == false);
			dbm.addUser(testU);
			assertTrue(isUserThere(testU.getUsername()));
			
		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
		
		// check if throws the exception correctly
		try {
			assert(isUserThere(testU.getUsername()));
			dbm.addUser(testU);
			
		} catch (UsernameAlreadyExistsException e) {
			assertTrue(true);
		}
		
		// check what happens with username = null
		try {
			assertTrue(dbm.addUser(new User(null, "", "", "", "")) == false);
			
		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
	}
	
	private boolean isUserThere(String username){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM User WHERE username = '"+username+"';").next();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
	}

	@Test
	public void testAddRoom() {
		// remove room if it is there
		Room testRoom = new Room("X1", 42);
		try {
			connection.createStatement().executeUpdate("DELETE FROM Room WHERE roomID = '"+testRoom.getRoom_id()+"';");
		} catch (SQLException e1) {
			
			e1.printStackTrace();
			fail("WTF?");
		}
		
		// check adding a room correctly
		try {
			assertTrue(isRoomThere(testRoom) == false);
			dbm.addRoom(testRoom);
			assertTrue(isRoomThere(testRoom));
			
		} catch (RoomAlreadyExistsException e) {
			fail("exception thrown");
			e.printStackTrace();
		}
		
		// check if throws the exception correctly
		try {
			assert(isRoomThere(testRoom));
			dbm.addRoom(testRoom);
			
		} catch (RoomAlreadyExistsException e) {
			assertTrue(true);
		}
		
		// check what happens with roomid = null
		
		try {
			assertTrue(dbm.addRoom(new Room(null, 11)) == false);

		} catch (RoomAlreadyExistsException e) {
			fail("wrong exception thrown");
			e.printStackTrace();
		}
	}

	private boolean isRoomThere(Room r){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM Room WHERE roomID = '"+r.getRoom_id()+"';").next();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
	}

	@Test
	@Ignore
	public void testAddAlarm(){
		// remove alarm if it is there
		Alarm testAlarm = new Alarm(12000, u.getUsername(), e.getEntryID());
		try {
			
			PreparedStatement stmt = connection.prepareStatement("DELETE From Alarm where username = ? and entryID = ?");
			int i = 0;
			stmt.setString(++i, testAlarm.getUsername());
			stmt.setLong(++i, testAlarm.getEntry_id());			
			stmt.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail("WTF?");
		}
		
		// check adding an Alarm correctly
		try {
			assertTrue(isAlarmThere(testAlarm) == false);
			dbm.addAlarm(testAlarm);
			assertTrue(isAlarmThere(testAlarm));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown");
			
		}
		
		// check if throws the exception correctly
		// alarm already exists
		try {
			assert(isAlarmThere(testAlarm));
			dbm.addAlarm(testAlarm);
			
		} catch (AlarmAlreadyExistsException e) {
			assertTrue(true);
		}catch (Exception e) {
			fail("wrong exception");
			e.printStackTrace();
		}
		
		// check what happens with wrong inputs (should fail silentry and return false)
		try {
			assertTrue(dbm.addAlarm(new Alarm(100, null, -1)) == false);
			assertTrue(dbm.addAlarm(new Alarm(100, "lukasap", -1)) == false);
			// more testcases...

		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
	}
	
	private boolean isAlarmThere(Alarm a){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM Alarm WHERE username = '"+a.getUsername()+"' and entryID = '"+a.getEntry_id()+"';").next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Test
	@Ignore
	public void testAddInvitation() {
		
		// remove inv if it is there
		Invitation testInv = new Invitation(true, true, u.getUsername(), e.getEntryID());
		try {
			
			PreparedStatement stmt = connection.prepareStatement("DELETE From Invitation where username = ? and entryID = ?");
			int i = 0;
			stmt.setString(++i, testInv.getUsername());
			stmt.setLong(++i, testInv.getEntry_id());			
			stmt.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail("WTF?");
		}
		
		// check adding an Invitation correctly
		try {
			assertTrue(isInvitationThere(testInv) == false);
			dbm.addInvitation(testInv);
			assertTrue(isInvitationThere(testInv));
			
		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
		
		// check if throws the exception correctly
		// invitation already exists
		try {
			assert(isInvitationThere(testInv));
			dbm.addInvitation(testInv);
			
		} catch (InvitationAlreadyExistsException e) {
			assertTrue(true);
		}catch (Exception e) {
			fail("wrong exception");
			e.printStackTrace();
		}
		
		// check what happens with roomid = null
		try {
			assertTrue(dbm.addInvitation(new Invitation(false, false, null, -1)) == false);

		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
	}
	
	private boolean isInvitationThere(Invitation inv){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM Invitation WHERE username = '"+inv.getUsername()+"' and entryID = '"+inv.getEntry_id()+"';").next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private long getlastEntryID(){
		ResultSet rset;
		long lastID = -1;
		try {
			rset = connection.createStatement().executeQuery("SELECT MAX(entryID) FROM CalendarEntry;");
			rset.next();
			lastID = rset.getInt("MAX(entryID)");
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail("problem with intern method");
		}
		assertTrue(lastID > 0);
		return lastID;
	}

	@Test
	public void testAddEntry() {
		// get latest entryID
		long lastID = getlastEntryID();
		
		CalendarEntry testEntry = new CalendarEntry(1, 1000, 100000, "tree", "happy tree friends", null, u.getUsername());
		
		// check adding a entry correctly
		try {
			dbm.addEntry(testEntry, u.getUsername());
			assertTrue(getlastEntryID() == lastID+1);
		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
		
		// check if throws the exception correctly
		try {
			dbm.addEntry(testEntry, "asjkdhfkasdhf"); // username does not exist
			
		} catch (UserDoesNotExistException e) {
			assertTrue(true);
		}
		
		// check what happens with username = null (should throw UserDoesNotExistException)
		try {
			assertTrue(dbm.addEntry(testEntry, null) == false);
			
		} catch (UserDoesNotExistException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testAddGroup() {
		// remove group if it is there
		String gname = "group1";
		try {
			PreparedStatement stmt = connection.prepareStatement("DELETE From Gruppe where groupname = ? ;");
			stmt.setString(1, gname);
			stmt.executeUpdate();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail("WTF?");
		}
		// make some users and add them to DB
		User u1 = new User("u1", "", "", "", "");
		User u2 = new User("u2", "", "", "", "");
		User u3 = new User("u3", "", "", "", "");
		User[] userArray = { u1, u2, u3 };
		for(User user : userArray){
			try {
				dbm.addUser(user);
			} catch (UsernameAlreadyExistsException e) {
				e.printStackTrace();
			}
		}

		Group testGroup = new Group(userArray, gname);

		// check adding an Group correctly
		try {
			assertTrue(isGroupThere(testGroup.getName()) == false);
			dbm.addGroup(testGroup);
			assertTrue(isGroupThere(testGroup.getName()));

		} catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown");
		}

		// check if throws the exception correctly
		// group already exists
		try {
			assert (isGroupThere(testGroup.getName()));
			dbm.addGroup(testGroup);

		} catch (GroupAlreadyExistsException e) {
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail("wrong exception");
		}

		// check what happens with wrong inputs
		try {
			assertTrue(dbm.addGroup(new Group(null, null)) == false);

		} catch (Exception e) {
			fail("exception thrown");
			e.printStackTrace();
		}
	}
	
	private boolean isGroupThere(String groupname){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM Gruppe WHERE groupname = '"+groupname+"';").next();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
	}

	@Test
	public void testAddUserToGroup() {
		User[] userArray = {u};
		User[] emptyArray = {};
		String groupname = "grname1";
		try {
			assertTrue(isInGroup(groupname, u.getUsername()) == false);
			dbm.addGroup(new Group(emptyArray, groupname));
			dbm.addUserToGroup(u.getUsername(), groupname);
			assertTrue(isInGroup(groupname, u.getUsername()));
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		
		try {
			assertTrue(isInGroup(groupname, u.getUsername()));
			assertTrue(dbm.addGroup(new Group(userArray, null)) == false);
			dbm.addGroup(new Group(userArray, "grname1"));
		} catch (GroupAlreadyExistsException e1) {
			assertTrue(true);
		} catch (Exception e2){
			fail("wrong exception thrown");
		}
	}
	
	private boolean isInGroup(String groupname, String username){
		try {
			return connection.createStatement().executeQuery("SELECT * FROM MemberOf WHERE groupname = '"+groupname+"' and username = '"+username+"';").next();
		
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
	}

	@Test
	@Ignore
	public void testAddNotification() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testMakeAdmin() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testIsAllowedToSee() {
		assertTrue(false);
	}

	@Test
	@Ignore
	public void testIsAllowedToEdit() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetAlarm() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testIsMemberOf() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetNotificationsForUser() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetRoom() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetInvitation() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetUser() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetEntry() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditAlarmAlarm() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditAlarmStringLongLong() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditNotification() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditUser() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditEntry() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEditRoom() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAllowToSee() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAllowToSeeGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testHideEvent() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testHideEventGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGoing() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testNotGoing() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteAlarmAlarm() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteAlarmStringLong() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testRevokeAdmin() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteNotification() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteEntry() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testRemoveUserFromGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDeleteRoom() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testInviteUser() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testInviteGroup() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateCalendar() {
		fail("Not yet implemented");
	}

}
