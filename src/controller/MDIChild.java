package controller;

import java.awt.Container;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class MDIChild extends JPanel {
	
	protected Container myFrame;
	
	/**
	 * MDI parent
	 */
	protected MDIParent parent;

	/**
	 * title to used by JInternalFrame for this panel
	 */
	private String myTitle;

	/**
	 * if true and this MDI child is already open, 
	 * then repeated attempts to open will restore and show the child that is already open
	 */
	private boolean singleOpenOnly;
	
	private boolean changed;
	
	public MDIChild(String title, MDIParent parent) {
		this(title);
		setMDIParent(parent);
		myFrame = null;
		singleOpenOnly = false;
	}
	
	public MDIChild(String title) {
		myTitle = title;
	}
	
	public void setTitle(String title) {
		myTitle = title;
		setInternalFrameTitle(myTitle);
	}
	
	public MDIParent getMasterParent() {
		return parent;
	}
	
	/**
	 * Restores MDIChild's frame from minimized state and sets focus to it
	 */
	public void restoreWindowState() throws PropertyVetoException {
		JInternalFrame c = (JInternalFrame) getMDIChildFrame();
		c.setIcon(false);
		c.moveToFront();
	}
	
	/**
	 * MDIParent calls this to set title of the View's containing frame (i.e., JInternalFrame)
	 * @return title of View (should be set in View's constructor)
	 */
	public String getTitle() {
		return myTitle;
	}

	/**
	 * sets the MDI parent instance variable 
	 * @param mf
	 */
	public void setMDIParent(MDIParent mf) {
		parent = mf;
	}

	public boolean isSingleOpenOnly() {
		return singleOpenOnly;
	}

	public void setSingleOpenOnly(boolean singleOpen) {
		this.singleOpenOnly = singleOpen;
	}

	//get MDI child's local frame container (i.e., JInternalFrame) 
	//This should only do this once
	protected JInternalFrame getMDIChildFrame() {
		Container tempContainer = this;
		//get climbing parent hierarchy until we find the JInnerFrame
		while(!(tempContainer instanceof JInternalFrame) && tempContainer != null) 
			tempContainer = tempContainer.getParent();
		if(tempContainer != null)
			return (JInternalFrame) tempContainer;
		return null;
	}

	//set the title of the containing JInternalFrame
	protected void setInternalFrameTitle(String t) {
		if(myFrame == null)
			myFrame = getMDIChildFrame();
		if(myFrame != null)
			((JInternalFrame) myFrame).setTitle(t);
	}
	
	/**
	 * clean up when the MDI child is closing
	 * subclasses should override, call this as super()
	 * and then unregister as observers from any models
	 */
	protected void cleanup() {
		parent.removeFromOpenViews(this);
		//TEST: this should always print as the MDI Child closes, no matter how the child is closed
		//e.g., click close on the JInternalFrame, click Quit on menu, kill JVM, click close on MDI Parent
		System.err.println("MDIChild is closing...");
	}

	//set internal frame visible to false
	protected void setInternalFrameVisible(boolean v) {
		if(myFrame == null)
			myFrame = getMDIChildFrame();
		if(myFrame != null)
			((JInternalFrame) myFrame).setVisible(v);
	}

	/**
	 * clean up when the MDI child is closing
	 * subclasses should override, call this as super()
	 * and then unregister as observers from any models
	 */
	
	public Container getMyFrame() {
		return myFrame;
	}

	public void setMyFrame(Container myFrame) {
		this.myFrame = myFrame;
	}
	
	/**
	 * tell the enclosing JInternalFrame to close
	 */
	public void closeFrame() {
		try {
			((JInternalFrame) myFrame).setClosed(true);
		} catch (PropertyVetoException e) {
			parent.displayChildMessage("Error trying to close child window!");
		}
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * stub method for saving changes to model
	 * Details will override this. list views will ignore it
	 * @return if save succeeds or fails
	 */
	public boolean saveModel() {
		return true;
	}

}
