package dbms;

import static org.junit.Assert.*;

import java.sql.DriverManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;

import exceptions.UsernameAlreadyExistsException;
import user.User;

public class DbmTest {
	private DataBaseManager dbm;
	private User u1;
	private User u2;
	private Connection connection;

	@Before
	public void setUp() throws Exception {
		dbm = new DataBaseManager();
		u1 = new User("testUser1", "Theo", "1234", "ABCD", "Theo@test.ntnu");
		u2 = new User("testUser2", "Thalia", "5678", "EFGS", "Thalia@test.ntnu");
		Class.forName("com.mysql.jdbc.Driver");
		String[] ci= {"jdbc:mysql://mysql.stud.ntnu.no/mariessa_pu", "mariessa_pu","fellesprosjekt" };
		connection = DriverManager.getConnection(ci[0], ci[1], ci[2]);
	}

	@After
	public void tearDown() throws Exception {
		dbm.close();
		connection.close();
	}

	@Test
	public void testAddUser() {
		
		fail("Not yet implemented");
		
		
	}

	@Test
	public void testEditUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeAdmin() {
		fail("Not yet implemented");
	}

	@Test
	public void testAllowToSee() {
		fail("Not yet implemented");
	}

	@Test
	public void testAllowToSeeGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testHideEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testHideEventGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testGoing() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotGoing() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsActive() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsNotActive() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEntry() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteEntry() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddEntry() {
		fail("Not yet implemented");
	}

	@Test
	public void testEditEntry() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testEditNotification() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddUserToGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveUserFromGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddRoom() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteRoom() {
		fail("Not yet implemented");
	}

	@Test
	public void testEditRoom() {
		fail("Not yet implemented");
	}

	@Test
	public void testInviteUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testInviteGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsAllowedToSee() {
		fail("Not yet implemented");
	}

	@Test
	public void testCanEdit() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateCalendar() {
		fail("Not yet implemented");
	}

}
