package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.model.ObjectState;
import com.globalmentor.rdf.*;
import com.globalmentor.util.Debug;

/**Main frame parent class for a multiple document interface (MDI) application.
@author Garret Wilson
*/
public abstract class MDIApplicationFrame extends ObsoleteApplicationFrame
{

	//window menu identifiers
/*G***fix
	public final static long MENU_FILE_NONE=0;
	public final static long MENU_FILE_NEW=1;
	public final static long MENU_FILE_OPEN=2;
	public final static long MENU_FILE_CLOSE=4;
	public final static long MENU_FILE_SAVE=8;
	public final static long MENU_FILE_EXIT=16;
*/

	/**The map in which document description are stored, usually keyed to internal frames.*/
	private final Map descriptionMap=new WeakHashMap();

		/**Gets the description for a particular document.
		@param key The key for the document, usually an internal frame.
		@return The description used for the object, or <code>null</code> if the
			object has no associated description.
		*/
		protected ObjectState<RDFResource> getDocumentDescription(final Object key)
		{
			return (ObjectState<RDFResource>)descriptionMap.get(key);  //return the description associated with the kay
		}

		/**Associates a description with an object. The object is stored in a weak
			hash map so that, if there are no other references to the object, the
			object and its description will be removed.
		@param key The object with which the description is being associated, such
			as an internal frame.
		@param description The descrption to associate with the object, or
			<code>null</code> if the file association should be removed.
		*/
		protected void setDocumentDescription(final Object key, final ObjectState<RDFResource> description)
		{
			if(description!=null)  //if a valid description was passed
			  descriptionMap.put(key, description); //put the description in the map, keyed to the key
			else  //if null was passed for a description
				descriptionMap.remove(key);  //remove whatever association is in the map
		}

	/**The MDI internal frame manager.*/
	private MDIManager mdiManager;

		/**@return The MDI internal frame manager.*/
		protected MDIManager getMDIManager() {return mdiManager;}

  /**The main MDI pane in which internal frames will be displayed.*/
	private JDesktopPane desktopPane;

		/**@return The main MDI pane in which internal frames will be displayed.*/
		protected JDesktopPane getDesktopPane() {return desktopPane;}

	/**Default constructor.
	Enables window events.
	*/
	public MDIApplicationFrame()
	{
		this(true, true);  //construct the parent class G***fix
	}
	
	/**Constructor with a default panel.
	Enables window events.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public MDIApplicationFrame(final boolean initialize)
	{
		this(true, true, initialize); //create an application frame with a default application panel G***fix default hasXXXs
	}	

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	  Enables window events.
	@param hasMenuBar Whether this frame should have a menu bar.
	@param hasStatusBar Whether this frame should have a status bar.
	*/
	public MDIApplicationFrame(final boolean hasMenuBar, final boolean hasStatusBar)
	{
		this(hasMenuBar, hasStatusBar, true);	//initialize the class
	}
	
	/**Constructor that allows options to be set, such as the presence of a status
		bar.
		Enables window events.
	@param hasMenuBar Whether this frame should have a menu bar.
	@param hasStatusBar Whether this frame should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public MDIApplicationFrame(final boolean hasMenuBar, final boolean hasStatusBar, final boolean initialize)
	{
//G***fix		super(hasMenuBar, hasStatusBar);  //construct the parent class
		super(false);	//construct the parent class
		desktopPane=new JDesktopPane(); //create the desktop pane
		mdiManager=new MDIManager(/*TODO fix this, */desktopPane); //create a new MDI manager to manage the internal frames G***later create a custom one
		if(initialize)	//if we should initialize the frame
			initialize();	//initialize the frame				
	}

	/**Initializes the user interface.*/
  protected void initializeUI()
  {
		super.initializeUI(); //do the default initialization
    getContentPane().add(desktopPane, BorderLayout.CENTER);
  }

	/**Closes the open file.*/
	public void closeFile()
	{
			//do not call the parent version, because we don't want to disassociate
			//  the file, yet---wait until after we know the window is closing
		final JInternalFrame internalFrame=getMDIManager().getSelectedFrame();  //get the selected frame
		if(internalFrame!=null) //if a frame is selected
		{
			if(internalFrame.isClosable())  //if the frame is closable
				internalFrame.doDefaultCloseAction(); //tell it to close
	//G***del if not needed		getDesktopPane().getDesktopManager().closeFrame(internalFrame); //close the selected frame
		}
	}

	/**Retrieves the description used for the currently opened document.
		The currently open internal frame is used to look up the file.
	@return The description used for the currently opened document, or
		<code>null</code> if no description is available (e.g. there is no document
		opened).
	@see #getDocumentDescription(Object)
	*/
	protected ObjectState<RDFResource> getDocumentDescription()
	{
		final JInternalFrame internalFrame=getDesktopPane().getSelectedFrame();  //get the currently selected internal frame
		return internalFrame!=null ? getDocumentDescription(internalFrame) : null;  //return a file for the frame, if it's available
	}

	/**Sets the description of the currently opened document.
		The currently open internal frame is used as a key to store the description.
	@param description The description of the currently opened document.
	@see #setDocumentDescription(Object, RDFResourceState)
	*/
	protected void setDocumentDescription(final ObjectState<RDFResource> description)
	{
Debug.trace("setting description: ", description); //G***del
		final JInternalFrame internalFrame=getMDIManager().getSelectedFrame();  //get the currently selected internal frame
		if(internalFrame!=null) //if there is an internal frame open
		{
		  setDocumentDescription(internalFrame, description);  //store the description, keyed to the internal frame
Debug.trace("setting frame title: ", description.getObject().getURI()); //G***del
		  updateTitle(internalFrame);  //update the internal frame's title
		}
	}

	/**Adds an internal frame to the desktop, adding the appropriate listeners to
		the frame so that appropriate updates can occur when the frame is closed, etc.
	@param internalFrame The frame to add to theh desktop.
	*/
/*G***fix
	protected void addInternalFrame(final JInternalFrame internalFrame)
	{
		internalFrame.addInternalFrameListener(this);  //show that we want to hear about internal frame events
			//listen for the frame trying to close
		internalFrame.addVetoableChangeListener(new VetoableChangeListener()
    {
      public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException
      {
					//if the internal frame is trying to close
				if(JInternalFrame.IS_CLOSED_PROPERTY.equals(propertyChangeEvent.getPropertyName())
					  && ((Boolean)propertyChangeEvent.getNewValue()).booleanValue()==true)
				{
					if(canCloseFile()) //if this frame can close
					{
						setFile(null);  //disassociate the file from the internal frame G***check; see if we need to explicitly pass a key
					}
					else  //if the frame cannot close
						throw new PropertyVetoException("canceled", propertyChangeEvent); //cancel the closing G***i18n
				}
      }
    });
		getDesktopPane().add(internalFrame, getDesktopPane().DEFAULT_LAYER);  //add the frame to the default layer
		getDesktopPane().getDesktopManager().activateFrame(internalFrame);  //activate the internal frame we just added
		updateTitle(internalFrame);  //update the internal frame's title
	}
*/

	/**Adds an internal frame to the desktop, adding the appropriate listeners to
		the frame so that appropriate updates can occur when the frame is closed, etc.
	@param internalFrame The frame to add to theh desktop.
	*/
	protected void addInternalFrame(final JInternalFrame internalFrame)
	{
		getMDIManager().addInternalFrame(internalFrame);  //add the QTI internal frame to the desktop
		updateTitle(internalFrame);  //update the internal frame's title
	}


	/**Updates the title of the given internal frame.
		Called in response to an internal frame being added or the file being set,
		for example.
		This version updates the title with the filename stored for this internal
		frame, if available.
		<p>Child classes that want to manage titles themselves can override this
		method and manage the title at that point, or override this method to
		do nothing so that titles are not disturbed.</p>
	@param internalFrame The internal frame the title of which should be updated.
	*/
	protected void updateTitle(final JInternalFrame internalFrame)
	{
		String title; //we'll assign a title to this string
		final ObjectState<RDFResource> description=getDocumentDescription(internalFrame); //get the file associated with this frame
		if(description!=null)  //if a description is available
		{
//G***del			try
			{
			  title=description.getObject().getURI().toString(); //show the canonical file path in the title G***maybe show something special for an anonymous resource
//G***del				title=file.getCanonicalPath(); //show the canonical file path in the title
			}
/*G***del			
			catch(IOException ioException)  //if an I/O exception occurss while trying to get the canonical path
			{
				Debug.warn(ioException);  //warn that an error occurred
				title=file.getPath(); //just use the file's normal path in the title
			}
*/
		}
		else  //if no file is availalbe
		  title="(untitled)"; //create a title for unsaved files G***i81n
		internalFrame.setTitle(title); //show the correct title
	}

	/**Determines whether a file can be closed.
	Subclasses should override <code>canCloseInernalFrame()</code>.
	@return <code>true</code> if the currently open file can be closed, else
		<code>false</code> if closing should be canceled.
	*/
	public boolean canCloseFile()
	{
		return canCloseInternalFrame(getMDIManager().getSelectedFrame());  //get the selected frame and see if we can close it
	}

	/**Determines if the internal frame can be closed.
	@param internalFrame The internal frame requesting to be closed.
	@return <code>true</code> if the currently open internal frame can be
		closed, else <code>false</code> if closing should be canceled.
	*/
	public boolean canCloseInternalFrame(final JInternalFrame internalFrame)
	{
		  //ask the MDI manager if we can close the currently selected frame (this will come back and ask us if we can close the document, if a document is present)
		return getMDIManager().canCloseInternalFrame(internalFrame);
	}

	/**Exits the application.*/
	public void exit()  //G***should we add an exit code parameter?
	{
		if(!getMDIManager().canCloseInternalFrames()) //if we can't close all the internal frames
			return; //don't exit
		super.exit(); //everything can be closed; exit the application normally
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		super.updateStatus();  //do the default updating
		final JInternalFrame internalFrame=getMDIManager().getSelectedFrame();  //get the currently selected internal frame
		getFileCloseAction().setEnabled(internalFrame!=null); //enable or disable the close action
	}

}
