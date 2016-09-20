package security;

import java.util.HashMap;

public class ABACPolicy {
	
	
		
		//fine-grained functional access
		
		public static final String CAN_VIEW_PART = "part.view";
		public static final String CAN_ADD_PART= "part.add";
		public static final String CAN_EDIT_PART = "part.edit";
		public static final String CAN_DELETE_PART = "part.delete";
		
		/**
		 * access control list for users and functions
		 */
		
		private HashMap<String, HashMap<String, Boolean>> acl;
		
		public ABACPolicy(){
			
			//create default fall-through policy where person has no access

			acl = new HashMap<String, HashMap<String, Boolean>>();
			
			createSimpleUserACLEntry("default",true,false,false,false);
			createSimpleUserACLEntry("bob",true,true,true,false);
			createSimpleUserACLEntry("sue",true,false,true,false);
			createSimpleUserACLEntry("ragnar",true,false,false,true);
		}
		
		
		
		public void createSimpleUserACLEntry(String login, boolean ... entries){
			HashMap<String, Boolean>userTable = new HashMap<String, Boolean>();
			userTable.put(CAN_VIEW_PART, entries[0]);
			userTable.put(CAN_ADD_PART, entries[1]);
			userTable.put(CAN_EDIT_PART, entries[2]);
			userTable.put(CAN_DELETE_PART, entries[3]);
			
			acl.put(login, userTable);
		}
		
		/**
		 * Change user's permission for function f
		 * @param uName user whose permission will change
		 * @param f the application function
		 * @param val new permission 
		 */
		
		public void setUserACLEntry(String uName, String f, boolean val)throws SecurityException{
		
			if(!acl.containsKey(uName))
				throw new SecurityException(uName + " does not exsit in ACL");
			HashMap<String, Boolean>userTable = acl.get(uName);
			if(!userTable.containsKey(f))
				throw new SecurityException(f + " does not exist in user table");
			//change the permission
			userTable.put(f, val);
			 
		}
		
		public boolean canUserAccessFunction(String userName, String functionName){
			//get default
			if(!acl.containsKey("default")){
				return false;
			}
			HashMap<String, Boolean> userTable = acl.get("default");
			
			//get user's table if it is in acl. otherwise, use default
			if(acl.containsKey(userName))
				userTable = acl.get(userName);
			//is permission for function in table? if so return that permission
			if(userTable.containsKey(functionName))
				return userTable.get(functionName);
			
			
			//otherwise return false (permission does not exist)
			return false;
		}

}