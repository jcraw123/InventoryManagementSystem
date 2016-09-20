package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import security.ABACPolicy;
import security.Authenticator;
import security.CryptoHash;
import security.SecurityException;
import security.Session;
import security.User;

public class MDIMenu extends JMenuBar {

	private MDIParent parent;
	
	private int id = 0;
	ABACPolicy policy = new ABACPolicy();
	Authenticator authenticate = new Authenticator();
	
	
	
		
	public MDIMenu(MDIParent p) {
			super();
			
			this.parent = p;
			
			JMenu menu = new JMenu("File");
			JMenuItem menuItem = new JMenuItem("Quit");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.APP_QUIT, null);
				}
			});
			menu.add(menuItem);
			this.add(menu);
			
			menu = new JMenu("Login"); 
			menuItem = new JMenuItem("Login as Bob Roberts");
			menuItem.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.LOGIN_AS_BOB_ROBERTS, null);
									
				}
				
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			menuItem = new JMenuItem("Login As Sue Williams");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.LOGIN_AS_SUE_WILLIAMS, null);	
					
				}
				
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			menuItem = new JMenuItem("Login As Ranger Jones");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.LOGIN_AS_RAGNAR_JONES, null);					
					
				}
				
			});
			

			menu.add(menuItem);
			this.add(menu); 
			
			menuItem = new JMenuItem("Login as Grace Kelly");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.LOGIN_AS_GRACE_KELLY, null);
				}
				
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			
			
			menuItem = new JMenuItem("Logout");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//parent.doCommand(MenuCommands.LOGOUT, null);

					authenticate.logout(id);
					parent.displayChildMessage("Logged Out");
					parent.getUserName("guest");
					parent.doCommand(MenuCommands.LOGOUT, null);
				}
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			
			
			menu = new JMenu("Warehouse");
			menuItem = new JMenuItem("Warehouse List");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.SHOW_LIST_WAREHOUSE, null);
				}
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			menu.add(menuItem);
			menuItem = new JMenuItem("Add Warehouse");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.SHOW_ADD_WAREHOUSE, null);
				}
			});
			menu.add(menuItem);
			this.add(menu);		

			menu = new JMenu("Parts");
			menuItem = new JMenuItem("Parts List");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.SHOW_LIST_PARTS, null);
					
				}
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			menu.add(menuItem);
			menuItem = new JMenuItem("Add Part");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.SHOW_ADD_PART, null);
				}
			});
			
			menu.add(menuItem);
			this.add(menu);
			
			
			menu = new JMenu("Inventory");
			menuItem = new JMenuItem("Inventory List");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.SHOW_LIST_INVENTORY, null);
				}
			});
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Add Inventory Item");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					parent.doCommand(MenuCommands.ADD_INVENTORY,null);
				}
			});
			menu.add(menuItem);
			this.add(menu);
			
			menu = new JMenu("Reports");
			menuItem = new JMenuItem("PDF Report");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.WAREHOUSE_INVENTORY_REPORT_PDF, null);
				}
			});
			menu.add(menuItem);
			this.add(menu);

			menuItem = new JMenuItem("Excel Report");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.doCommand(MenuCommands.WAREHOUSE_INVENTORY_REPORT_EXCEL, null);
				}
			});
			menu.add(menuItem);

			this.add(menu);
		
		
		}

	
	public boolean verifyAccess(String user, String verify) {
		if(policy.canUserAccessFunction(user, verify) == true )
			return true;
		return false; 
	}
	
	
	
	
}
