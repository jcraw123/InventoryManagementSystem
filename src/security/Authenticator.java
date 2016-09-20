package security;

import java.util.ArrayList;
import java.util.List;

public class Authenticator {
	
private ABACPolicy accessPolicy;
	
	private List<Session> sessions;
	
	private List<User> credentials;
	
	//creates valid users
	public Authenticator(){
		
		User u = new User("bob", "abcdef", "BobUser");
		u.setPasswordHash(CryptoHash.hashSha256(u.getPassword()));
		credentials = new ArrayList<User>();
		credentials.add(u);
		
		User u1 = new User("sue","decaef", "SueUser");
		u1.setPasswordHash(CryptoHash.hashSha256(u1.getPassword()));
		credentials.add(u1);
		
		User u2 = new User("ragnar","bcdefd", "RagnarUser");
		u1.setPasswordHash(CryptoHash.hashSha256(u1.getPassword()));
		credentials.add(u2);
		
		//init the session list
		sessions = new ArrayList<Session>();
		
		//create a default ABAC policy and add some permissions for user bob
		accessPolicy = new ABACPolicy();
		accessPolicy.createSimpleUserACLEntry(u.getLogin(),true,true,true,false);
		accessPolicy.createSimpleUserACLEntry(u1.getLogin(),true,false,true,false);
		accessPolicy.createSimpleUserACLEntry(u2.getLogin(),true,false,false,true);
		
	}
	
	/**
	 * look up in ABAC table user.login x function
	 * @param s session containing logged in user
	 * @param f function to which user is requesting access
	 * @return
	 */
	
	public boolean clientStateHasAccess(Session s, String f){
		return accessPolicy.canUserAccessFunction(s.getSessionUser().getLogin(),f );
	}
	
	
	/**
	 * use server state to determine if session user has access to function f 
	 * @param sessionId id of session to lookup in server
	 * @param f function to which permission is being asked
	 * @return
	 * @throws SecurityException
	 */
	
	public boolean serverStateHasAccess(int sessionId, String f) throws SecurityException{
		for(Session s : sessions){
			if(s.getSessionId() == sessionId){
				return accessPolicy.canUserAccessFunction(s.getSessionUser().getLogin(), f);
			}
		}
		throw new SecurityException("Invalid session");
	}
	

	/**
	 * login and create a new session if credentials match
	 * @param l
	 * @param pw
	 * @return id of newly created session
	 * @throws SecurityException
	 */
	public int login(String l, String pw) throws SecurityException{
		
		for(User u : credentials){
			if(u.getLogin().equals(l) && u.getPasswordHash().equals(pw)){
				//creates a session for the user, that isn't server side
				Session s = new Session(u);
				sessions.add(s);
				return s.getSessionId();
			}
		}
		throw new SecurityException("Authentication failed");
	}
	
	/**
	 * login and create a new session if credentials match using Sha-256 hash of user's password
	 * @param l
	 * @param pwHash Sha256 hash of password
	 * @return id of newly created session
	 * @throws SecurityException
	 */
	public int loginSha256(String l, String pwHash)throws SecurityException{
		//iterate through user credentials and see if l and pwHash match. if so, make a session and returns its id
		for(User u : credentials) {
			if(u.getLogin().equals(l) && u.getPasswordHash().equals(pwHash)) {
				
				Session s = new Session(u);
				sessions.add(s);
				return s.getSessionId();
			}
		}
		throw new SecurityException("Authentication failed");
	
	}
	/**
	 * removes the session at the specific index sessionId
	 * @param sessionId
	 */
	public void logout(int sessionId) {
		for(int i = sessions.size() - 1; i >= 0; i--) {
			Session s = sessions.get(i);
			if(s.getSessionId() == sessionId) {
				sessions.remove(i);
			}
		}
	}
}
