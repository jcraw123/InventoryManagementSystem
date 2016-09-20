package test_models;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import security.ABACPolicy;
import security.Authenticator;
import security.SecurityException;
import security.Session;
import security.User;

public class test_assignment4 {
	
	private final User USER_BOB = new User("bob", "abcdef", "BobUser");
	private final User USER_SUE = new User("sue","decaef", "SueUser");
	private final User USER_RAGNAR = new User("ragnar","bcdefd", "RagnarUser");
	private final User USER_JILL = new User("jill", "no password", "User doesn't exist");
	
	private static Authenticator auth;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		auth = new Authenticator();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After public void tearDown() throws Exception {
		
	}
	
	//1
	@Test
	public final void test1() { 
		try {
			int sessionId = auth.login(USER_JILL.getLogin(), USER_JILL.getPassword());
			fail("expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("invalid exception message");
		}
	}
	
	//2
	@Test
	public final void test2() { 
		try {
			int sessionId = auth.login(USER_BOB.getLogin(), USER_BOB.getPassword());
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		assertTrue(true);
		
	}
	
	//3
	@Test
	public final void test3() {
		try {
			int sessionId = auth.loginSha256(USER_BOB.getLogin(), "aaaa");
			fail("Expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("Invalid exception message");
		}
	}
	
	//4
	@Test
	public final void test4() {
		String pwHash = "bef57ec7f53a6d40beb640a780a639c83bc29ac8a9816f1fc6c5c6dcd93c4721";
		try {
			int sessionId = auth.loginSha256(USER_BOB.getLogin(), pwHash);
			assertTrue(auth.serverStateHasAccess(sessionId, ABACPolicy.CAN_ADD_PART));
		} catch (SecurityException e) {
			fail("Not Expecting exception");
		}
	}
	
	//5
	@Test 
	public final void test5() {
		try {
			int sessionId = auth.login("random user", USER_BOB.getPassword());
			fail("expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("invalid exception message");
		}
	}
	
	//6
	@Test
	public final void test6() {
		try {
			int sessionId = auth.login(USER_BOB.getLogin(), "random password");
			fail("expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("invalid exception message");
		}
	}
	
	//7
	@Test
	public final void test7() {
		Session s = new Session(USER_JILL);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_ADD_PART));
	
	}
	
	//8
	@Test
	public final void test8() {
		Session s = new Session(USER_BOB);
		assertTrue(auth.clientStateHasAccess(s, ABACPolicy.CAN_ADD_PART));
	}
	
	//9
	@Test
	public final void test9() { 
		Session s = new Session(USER_BOB);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_DELETE_PART));
	}
	
	//10
	@Test 
	public final void test10() { 
		try {
			boolean result = auth.serverStateHasAccess(-1, ABACPolicy.CAN_DELETE_PART);
			fail("Excpected an exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Invalid session"))
				assertTrue(true);
			else
				fail("Invalid exception message");
		}
	}
	
	//11
	@Test
	public final void test11() {
		Session s = new Session(USER_BOB);
		assertTrue(auth.clientStateHasAccess(s, ABACPolicy.CAN_EDIT_PART));
	}
	
	//12 
	@Test
	public final void test12() {
		Session s = new Session(USER_SUE);
		assertTrue(auth.clientStateHasAccess(s, ABACPolicy.CAN_EDIT_PART));
	}
	
	//13 
	@Test
	public final void test13() {
		Session s = new Session(USER_SUE);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_ADD_PART));
	}
	
	//14
	@Test
	public final void test14() { 
		Session s = new Session(USER_SUE);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_DELETE_PART));

	}
	
	//15 
	@Test
	public final void test15() {
		Session s = new Session(USER_RAGNAR);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_ADD_PART));
	}
	
	//16
	@Test
	public final void test16() {
		Session s = new Session(USER_RAGNAR);
		assertTrue(auth.clientStateHasAccess(s, ABACPolicy.CAN_DELETE_PART));
	}
	
	//17
	@Test
	public final void test17() {
		Session s = new Session(USER_RAGNAR);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_EDIT_PART));
	}
	
	//18
	@Test
	public final void test18() {
		try {
			int sessionId = auth.loginSha256(USER_SUE.getLogin(), "bbbb");
			fail("Expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("Invalid exception message");
		}
		
	}
	
	//19 
	@Test 
	public final void test19() {
		try {
			int sessionId = auth.loginSha256(USER_RAGNAR.getLogin(), "cccc");
			fail("Expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("Invalid exception message");
		}
	}
	//20
	@Test
	public final void test20() {
		Session s = new Session(USER_JILL);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_EDIT_PART));
	}
	
	//21
	@Test 
	public final void test21() {
		Session s = new Session(USER_JILL);
		assertFalse(auth.clientStateHasAccess(s, ABACPolicy.CAN_DELETE_PART));
	}
	
	//22
	@Test 
	public final void test22() {
			String pwHash = "77d7a6058bd3a50bd4bea347ace08affa91b11066927ee489d949b2c104d4331";
			try {
				int sessionId = auth.loginSha256(USER_SUE.getLogin(), pwHash);
				assertTrue(auth.serverStateHasAccess(sessionId, ABACPolicy.CAN_EDIT_PART));
			} catch (SecurityException e) {
				fail("Not Expecting exception");
			}
	}
	
	//23
	@Test
	public final void test23() {
		String pwHash = "7ffa962d5568f7124a72b28392f95309a826413131aa12d13b5fe7c3814e0913";
		try {
			int sessionId = auth.loginSha256(USER_RAGNAR.getLogin(), pwHash);
			assertTrue(auth.serverStateHasAccess(sessionId, ABACPolicy.CAN_DELETE_PART));
		} catch (SecurityException e) {
			fail("Not Expecting exception");
		}
	}
	
	//24
	@Test
	public final void test24() {
		try {
			int sessionId = auth.login(USER_SUE.getLogin(), "wrong password");
			fail("expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("invalid exception message");
		}
		
	}
	
	//25 
	@Test
	public final void test25() {
		try {
			int sessionId = auth.login(USER_RAGNAR.getLogin(), "incorrect password");
			fail("expected exception");
		} catch (SecurityException e) {
			if(e.getMessage().equals("Authentication failed"))
				assertTrue(true);
			else
				fail("invalid exception message");
		}
	}
	
}
	
