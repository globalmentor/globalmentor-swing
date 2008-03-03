package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.java.*;
import com.globalmentor.rdf.*;
import com.globalmentor.rdf.dublincore.RDFDublinCore;
import com.globalmentor.util.*;

/**Main frame parent class for an application. This frame expects to contain
	a <code>ToolStatusPanel</code>.
<p>If an <code>Application</code> is set for the frame, the close
	operation changes to <code>EXIT_ON_CLOSE</code>. (This is essential to work
	around a JDK bug that under certain instances runs a daemon thread that
	prevents the JVM from exiting, even if the main program thread has finished
	and the sole frame has closed.) If there is no <code>Application</code> set,
	the close operation remains the default, <code>DISPOSE_ON_CLOSE</code>.</p>
This class maintains the default close operation of
	<code>DO_NOTHING_ON_CLOSE</code> . This allows the class listen to window
	events and perform consistent <code>canClose()</code> checks for all methods
	of closing. This is all handled transparently&mdash;once closing should occur,
	the local default close operation setting is honored as normal.</p>
<p>The class provides a method for displaying error messages.</p>
@author Garret Wilson
@see ToolStatusPanel
@see Application
@see #displayError
*/
public class ObsoleteApplicationFrame extends BasicFrame	//TODO delete class when dependent classes are modified to use the new ApplicationFrame
{

	//file menu identifiers
	public final static long MENU_FILE_NONE=0;
	public final static long MENU_FILE_NEW=1;
	public final static long MENU_FILE_OPEN=2;
	public final static long MENU_FILE_CLOSE=4;
	public final static long MENU_FILE_SAVE=8;
	public final static long MENU_FILE_SAVE_AS=16;
	public final static long MENU_FILE_EXIT=32;

	//help menu identifiers
	public final static long MENU_HELP_NONE=0;
	public final static long MENU_HELP_CONTENTS=1;
	public final static long MENU_HELP_ABOUT=2;

	/**The application this frame represents, or <code>null</code> if there is no
		application information.
	*/
	private final SwingApplication application;

		/**@return The application this frame represents, or <code>null</code> if
			there is no application information.
		*/
		public final SwingApplication getApplication() {return application;}

	/**The description used for the currently opened document.*/
//G***del	private DocumentDescribable description=null;

	/**The description of the currently opened document.*/
	private ObjectState<RDFResource> documentDescription=null;

		/**@return The description of the currently opened document.*/
		protected ObjectState<RDFResource> getDocumentDescription() {return documentDescription;}

		/**Sets the description of the currently opened document.
		@param description The description of the currently opened document.
		*/
		protected void setDocumentDescription(final ObjectState<RDFResource> description) {documentDescription=description;}

	/**The action for creating a new file.*/
	private final Action fileNewAction;

		/**@return The action for creating a new file.*/
		public Action getFileNewAction() {return fileNewAction;}

	/**An optional array of actions that, if present, will appear as a submenu of
		File|New.
	*/
	private Action[] fileNewActions=null;

		/**@return An array of actions that that will appear as a submenu of
		  File|New, or <code>null</code> if the normal File|New menu item should
			be used.
		*/
		public Action[] getFileNewActions() {return fileNewActions;}

		/**Sets the file new actions.
		@param newFileNewActions The new array of actions that will appear as a
			submenu of File|New, or <code>null</code> if the normal File|New menu item
			should be used.
		*/
		public void setFileNewActions(final Action[] newFileNewActions) {fileNewActions=newFileNewActions;}

	/**The action for opening a file.*/
	private final Action fileOpenAction;

		/**@return The action for opening a file.*/
		public Action getFileOpenAction() {return fileOpenAction;}

	/**The action for closing a file.*/
	private final Action fileCloseAction;

		/**@return The action for closing a file.*/
		public Action getFileCloseAction() {return fileCloseAction;}

	/**The action for saving a file.*/
	private final Action fileSaveAction;

		/**@return The action for saving the file.*/
		public Action getFileSaveAction() {return fileSaveAction;}

	/**The action for saving a file under a different name.*/
	private final Action fileSaveAsAction;

		/**@return The action for saving a file under a different name.*/
		public Action getFileSaveAsAction() {return fileSaveAsAction;}

	/**The action for file|exit; defaults to <code>getExitAction()</code>.*/
	private Action fileExitAction;

		/**@return The action for exiting, either <code>getCloseAction()</code> or
			<code>getExitAction()</code>. The default is <code>getExitAction()</code>.
		@see #getCloseAction
		@see #getExitAction
		*/
		public Action getFileExitAction() {return fileExitAction;}
		
		
		/**Sets the action to use for file|exit.
		The default is <code>getExitAction()</code>.
		@param action The action to use for file|exit.
		@see #getCloseAction
		@see #getExitAction
		*/
		public void setFileExitAction(final Action action) {fileExitAction=action;}

	/**The action for showing help contents.*/
	private final Action helpContentsAction;

		/**@return The action for showing help contents.*/
		public Action getHelpContentsAction() {return helpContentsAction;}

	/**The action for showing an about box.*/
	private final Action aboutAction;

		/**@return The action for showing an about box.*/
		public Action getAboutAction() {return aboutAction;}

	/**The action for exiting the application.*/
	private final Action exitAction;

		/**@return The action for exiting the application.*/
		public Action getExitAction() {return exitAction;}

	/**The action for closing the application frame.*/
	private final Action closeAction;

		/**@return The action for closing the application frame.*/
		public Action getCloseAction() {return closeAction;}

	/**The application menu bar.*/
	private final JMenuBar menuBar;
	
	/**The file menu items shown.*/
	private long fileMenuInclusions=MENU_FILE_OPEN | MENU_FILE_CLOSE | MENU_FILE_EXIT;

		/**@return The file menu items shown.*/
		public long getFileMenuInclusions() {return fileMenuInclusions;}

		/**Sets the file menu items shown.
		@param inclusions An indication of the menu items that should be included
			one or more of the <code>MENU_FILE_</code> constants logically ORed together.
		*/
		public void setFileMenuInclusions(final long inclusions) {fileMenuInclusions=inclusions;}

	/**The help menu items shown.*/
	private long helpMenuInclusions=MENU_HELP_ABOUT;

		/**@return The help menu items shown.*/
		public long getHelpMenuInclusions() {return helpMenuInclusions;}

		/**Sets the help menu items shown.
		@param inclusions An indication of the menu items that should be included,
			one or more of the <code>MENU_HELP_</code> constants logically ORed together.
		*/
		public void setHelpMenuInclusions(final long inclusions) {helpMenuInclusions=inclusions;}

	/**The copyright text.*/
	private String copyright=null;

		/**@return The copyright text, or <code>null</code> if there is no text.*/
		public String getCopyright() {return copyright;}

		/**Sets the copyright text.
		@param newCopyright The new copyright text, or <code>null</code> if there
			should be no copyright text.
		*/
		public void setCopyright(final String newCopyright) {copyright=newCopyright;}

	/**The application name.*/
	private String applicationName=null;

		/**@return The application name, or <code>null</code> if there is no name.*/
		public String getApplicationName() {return applicationName;}

		/**Sets the application name.
		@param newApplicationName The new application name, or
			<code>null</code> if there should be no application name.
		*/
		public void setApplicationName(final String newApplicationName) {applicationName=newApplicationName;}

	/**The application version.*/ //G***maybe rename this to applicationVersion
	private String version=null;

		/**@return The version text, or <code>null</code> if there is no text.*/
		public String getVersion() {return version;}

		/**Sets the version text.
		@param newVersion The new version text, or <code>null</code> if there
			should be no version text.
		*/
		public void setVersion(final String newVersion) {version=newVersion;}

	/**Constructs a string appropriate for showing on the title of the frame.
	<p>This version returns the application name.</p>
	@return A title to display on the frame.
	*/
	protected String constructTitle()
	{
		final String title;
		if(getApplication()!=null)	//if we have an application
		{
			final RDFObject titleObject=RDFDublinCore.getTitle(getApplication());	//get the application's title object
			title=titleObject!=null ? titleObject.toString() : null;	//use the title
		}
		else	//if we have no application
		{
				//TODO maybe eventually remove all these legacy non-RDF stuff
			title=getApplicationName();  //use the application name, if available
		}
		return title;	//return the title we discovered
	}
	
	/**Default constructor.
	Enables window events.
	*/
	public ObsoleteApplicationFrame()
	{
		this((SwingApplication)null);	//construct the frame with no application	
	}

	/**Application constructor.
	Enables window events.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	*/
	public ObsoleteApplicationFrame(final SwingApplication application)
	{
		this(application, true); //create an application frame with a default application panel and initialize
	}

	/**Constructor with a default panel and optional initialization.
	Enables window events.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final boolean initialize)
	{
		this((SwingApplication)null, initialize);	//construct the frame with no application	
	}

	/**Application constructor with a default panel and optional initialization.
	Enables window events.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final SwingApplication application, final boolean initialize)
	{
		this(application, new ToolStatusPanel(), initialize); //create an application frame with a default application panel
	}

	/**Application panel constructor.
	Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	*/
	public ObsoleteApplicationFrame(final Container contentPane)
	{
		this(null, contentPane);	//construct the frame with no application	
	}

	/**Application panel and application constructor.
  Enables window events.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	*/
	public ObsoleteApplicationFrame(final SwingApplication application, final Container contentPane)
	{
		this(application, contentPane, true);  //construct and initialize the frame
	}

	/**Application panel constructor with optional initialization.
	Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final Container contentPane, final boolean initialize)
	{
		this(null, contentPane, initialize);	//construct the frame with no application	
	}

	/**Application panel and application constructor with optional initialization.
	Enables window events.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final SwingApplication application, final Container contentPane, final boolean initialize)
	{
		this(application, contentPane, true, initialize);	//construct the application frame with a menubar 
	}

	/**Application panel constructor with optional initialization.
	Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	@param hasMenuBar Whether this frame should have a menu bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final Container contentPane, final boolean hasMenuBar, final boolean initialize)
	{
		this(null, contentPane, hasMenuBar, initialize);	//construct the frame with no application	
	}
	
	/**Application panel and application constructor with optional initialization.
  <p>Enables window events.</p>
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param contentPane The container to be used as the content pane; usually
		an <code>ToolStatusPanel</code>.
	@param hasMenuBar Whether this frame should have a menu bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ObsoleteApplicationFrame(final SwingApplication application, final Container contentPane, final boolean hasMenuBar, final boolean initialize)
	{
		super(contentPane, false);	//construct the parent class with the given content pane, but don't initialize it
		this.application=application;	//store the application
		if(application!=null)	//if this frame represents an application
			setDefaultCloseOperation(EXIT_ON_CLOSE);	//exit when the frame closes
		fileNewAction=new FileNewAction();  //create the new action G***maybe lazily create these
		fileOpenAction=new FileOpenAction();  //create the open action
		fileCloseAction=new FileCloseAction();  //create the close action
//G***del		fileCloseAction.setEnabled(false); //default to nothing to close
		fileSaveAction=new FileSaveAction();  //create the save action
		fileSaveAsAction=new FileSaveAsAction();  //create the save as action
//G***del		fileSaveAction.setEnabled(false); //default to nothing to save
		closeAction=new CloseAction();  //create the close action
		exitAction=new ExitAction();  //create the exit action
		setFileExitAction(exitAction);	//default to using the exit action for file|exit
		helpContentsAction=new HelpContentsAction();
		aboutAction=new AboutAction();
		if(hasMenuBar)  //if we should have a menu bar
		{
			menuBar=createMenuBar();  //create the menu bar
			setJMenuBar(menuBar); //set the menu bar
		}
		else  //if we shouldn't have a menu bar
		{
			menuBar=null; //show that we don't have a menu bar
		}
		if(initialize)  //if we should initialize
		  initialize(); //initialize the frame
	}


	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
  	super.initializeUI();	//do the default UI initialization
		initializeMenuBar(getJMenuBar());	//initialize the menu bar
  }

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
/*G***fix if neeed
	protected void updateStatus()
	{
		super.updateStatus();	//update the status normally
//G***fix this; this isn't good, because if a child class uses another save action, such as a proxied action, this will screw things up
//G***fix		final RDFResourceState description=getDocumentDescription();	//see what document is being described
//G***fix		getFileSaveAction().setEnabled(description!=null && description.isModified());	//only enable saving when there is a document that's modified
	}
*/

	/**@return A new menu bar with appropriate menus.*/
	protected JMenuBar createMenuBar()
	{
	  final JMenuBar menuBar=new JMenuBar();  //create the menu bar
		return menuBar; //return the menu bar we created
	}
	
	/**Initializes the menu bar. This will be called before the UI is updated.
	@param menuBar The menu bar to be initialized.
	@see #initializeUI
	*/
	protected void initializeMenuBar(final JMenuBar menuBar)
	{
		if(getFileMenuInclusions()!=MENU_FILE_NONE)  //if we should have a file menu
			menuBar.add(createFileMenu());  //create and add a file menu
		if(getHelpMenuInclusions()!=MENU_HELP_NONE)  //if we should have a help menu
			menuBar.add(createHelpMenu());  //create and add a help menu		
	}

	/**@return A new file menu that can be added to a menubar.*/
	protected JMenu createFileMenu()
	{
		final long inclusions=getFileMenuInclusions();  //see which file menu items should be included
		final JMenu fileMenu=new JMenu(); //create the menu
		fileMenu.setText("File");	//G***i18n
		fileMenu.setMnemonic('f');	//G***i18n
		if((inclusions & MENU_FILE_NEW)!=0) //file|new
		{
			final Action[] fileNewActions=getFileNewActions();  //get the array of multiple new actions
			if(fileNewActions!=null)  //if we have multiple file new actions
			{
				final JMenu fileNewMenu=new JMenu(getFileNewAction());  //add the main File|New menu G***testing
				for(int i=0; i<fileNewActions.length; ++i) //look at each of the file new actions to create submenus
				{
				  fileNewMenu.add(fileNewActions[i]);  //add this action to the submenu
				}
				fileMenu.add(fileNewMenu);  //add the File|New menu and its submenus to the File menu
			}
			else  //if we don't have multiple file new actions
			{
				final JMenuItem fileNewMenuItem=fileMenu.add(getFileNewAction());
			}
			fileMenu.addSeparator();
		}
		if((inclusions & MENU_FILE_OPEN)!=0) //file|open
		{
			final JMenuItem fileOpenMenuItem=fileMenu.add(getFileOpenAction());
		}
		if((inclusions & MENU_FILE_CLOSE)!=0) //file|close
		{
			JMenuItem fileCloseMenuItem=fileMenu.add(getFileCloseAction());
//G***del			fileCloseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));	//add Ctrl+F4 as an accelerator G***why do we need this twice?
		}
		if((inclusions & MENU_FILE_SAVE)!=0) //file|save
		{
			JMenuItem fileSaveMenuItem=fileMenu.add(getFileSaveAction());
//G***del			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));	//add Ctrl+S as an accelerator G***i18n G***why do we need this twice?
		}
		if((inclusions & MENU_FILE_SAVE_AS)!=0) //file|save as
		{
			JMenuItem fileSaveAsMenuItem=fileMenu.add(getFileSaveAsAction());
		}
		if((inclusions & (MENU_FILE_OPEN | MENU_FILE_CLOSE | MENU_FILE_SAVE))!=0) //--
		{
			fileMenu.addSeparator();
		}
		if((inclusions & MENU_FILE_EXIT)!=0) //file|exit
		{
			JMenuItem fileExitMenuItem=fileMenu.add(getFileExitAction());
		}
		return fileMenu;  //return the menu we created
	}

	/**@return A new help menu that can be added to a menubar.*/
	protected JMenu createHelpMenu()
	{
		final long inclusions=getHelpMenuInclusions();  //see which help menu items should be included
		final JMenu helpMenu=new JMenu(); //create the menu
			//help menu
		helpMenu.setText("Help");	//G***i18n
		helpMenu.setMnemonic('h');	//G***i18n
		if((inclusions & MENU_HELP_CONTENTS)!=0) //help|contents
		{
			final JMenuItem helpContentsMenuItem=helpMenu.add(getHelpContentsAction());
		}
		if((inclusions & MENU_HELP_CONTENTS)!=0) //--
		{
		  helpMenu.addSeparator();
		}
		if((inclusions & MENU_HELP_ABOUT)!=0) //help|about
		{
			final JMenuItem helpAboutMenuItem=helpMenu.add(getAboutAction());
		}
		return helpMenu;  //return the menu we created
	}

	/**Creates a new file.*/
	public void newFile()
	{
	}

	/**Opens a file.
		<p>For normal operation, this method should not be modified and
		<code>openFile(URI)</code> should be overridden. Multiple document
		applications may also override <code>getDocumentDescription()</code> and
		<code>setDocumentDescription</code>.</p>
	@return An object describing the file, or <code>null</code> if the operation
		was cancelled.
	@see #askOpenFile
	@see #setFile
	*/
	public ObjectState<RDFResource> openFile()
	{
		ObjectState<RDFResource> description=null;	//assume for now that the operation will be canceled
		final URI uri=askOpenFile();  //get the URI to use
		if(uri!=null)  //if a valid URI was returned
		{
			description=openFile(uri);  //attempt to open the file
			if(description!=null)  //if we opened the file successfully
			{
				setDocumentDescription(description);  //update the file, just in case they override openFile() and don't call this version
			}
		}
		return description;  //return the description
	}

	/**Closes the open file.*/
	public void closeFile()
	{
		if(canCloseFile())  //if we can close
		{
		  setDocumentDescription(null);  //show that no file is available
		}
	}

	/**Saves the open file.
		By default the location is checked using <code>getDocumentDescription</code>
		and, if no file is available, the <code>saveFileAs</code> method is called.
		<p>For normal operation, this method should not be modified and
		<code>saveFile(Description)</code> should be overridden. Multiple document
		applications may also override <code>getDocumentDescription()</code> and
		<code>setDocumentDescription</code>.</p>
	@return <code>true</code> if the operation was not cancelled.
	@see #getDocumentDescription
	@see #saveFileAs
	*/
	public boolean saveFile()
	{
//G***del Debug.trace("saving file");
		final ObjectState<RDFResource> description=getDocumentDescription(); //get a description of the document
		if(description!=null) //if we have a description of the document
		{
//G**del Debug.trace("found document description");
			if(description.getObject().getURI()!=null)  //if the file location is specified
				return saveFile(description, description.getObject().getURI()); //save using the URI specified by the description
			else  //if we don't have a file
				return saveFileAs(description); //call the save as function
		}
		return false; //show that we didn't save the file
	}

	/**Saves the open file after first asking for a filename.
	@return <code>true</code> if the operation was not cancelled.
	*/
	public boolean saveFileAs()
	{
		final ObjectState<RDFResource> description=getDocumentDescription(); //get a description of the document
		if(description!=null) //if we have a description of the document
		{
			return saveFileAs(description); //save the file with the description
		}
		else  //if we have no description
			return false; //show that we couldn't save document
	}

	/**Saves the open file after first asking for a filename.
	@param description The description of the document being saved.
	@return <code>true</code> if the operation was not cancelled.
	@see #setFile
	*/
	public boolean saveFileAs(final ObjectState<RDFResource> description)
	{
		boolean result=false; //start out assuming the file won't be saved
		final URI uri=askSaveFile();  //get the URI to use
		if(uri!=null)  //if a valid URI was returned
		{
			result=saveFile(description, uri); //save the file
			if(result)  //if the file was saved without being canceled
			{
				if(!Objects.equals(description.getObject().getURI(), uri))	//if the URI wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
				{
					description.getObject().setReferenceURI(uri);	//update the resource description's URI
/*G***del when works
						//create a copy of the resource description, using the new URI
					final RDFResource newResource=new DefaultRDFResource(description.getRDFResource(), uri);	//G***but what if the other resource was a special type?
					documentDescription.setRDFResource(newResource);	//update the resource to the one with the new URI
*/
				}
//G***fix or del				setFile(file);  //update the file, just in case they override saveFile() and don't call this version
			}
		}
		return result;  //show whether the operation completed
		
	}

	/**Saves the file at the location specified by the description.
		Methods that override this version should call this version to update
		the URI.
	@param description The description of the document being saved.
	@param uri The URI at which the file should be saved.
	@return <code>true</code> if the operation was not cancelled.
	*/
	protected boolean saveFile(final ObjectState<RDFResource> description, final URI uri)
	{
		if(!Objects.equals(description.getObject().getURI(), uri))	//if the URI should be changed
		{
			description.getObject().setReferenceURI(uri);	//update the resource description's URI
/*G***del when works
				//create a copy of the resource description, using the new URI
			final RDFResource newResource=new DefaultRDFResource(description.getRDFResource(), uri);	//G***but what if the other resource was a special type?
			documentDescription.setRDFResource(newResource);	//update the resource to the one with the new URI
*/
		}
		return true;  //this version always succeeds
	}

	/**Determines if a location has been specified for the given document.
		This version checks to see if a file is defined in the description.
	@param description A description of the opened document.
	@return <code>true</code> if the description contains a specified location.
	*/
/*G***del when works	
	protected boolean isLocationSpecified(final DocumentDescribable description)
	{
//G***del Debug.trace("is location anonymous: ", new Boolean(description.getResource().isAnonymous()));
		return description.getFile()!=null; //see if there is a file specified
	}
*/

	/**Shows the help contents.*/
	public void helpContents()
	{
	}

	/**Shows information about the application.*/
	public void helpAbout()
	{
		final AboutPanel aboutPanel=new AboutPanel(getApplication());	//create a new about panel for the application
		if(getApplication()==null)	//if we have no application
		{
				//TODO maybe eventually remove all these legacy non-RDF stuff
			aboutPanel.setTitle(getApplicationName());  //set the panel title to the application name, if available
			aboutPanel.setVersion(getVersion());  //set the version information, if available
			aboutPanel.setCopyright(getCopyright());  //set the copyright information, if available
		}
		//determine a title for the dialog, based upon the application name
//G***del when works	final String dialogTitle="About"+(getApplicationName()!=null ? " "+getApplicationName() : "");
			//determine a title for the dialog, based upon the application title
		final String dialogTitle="About"+(aboutPanel.getTitle()!=null ? " "+aboutPanel.getTitle() : "");	//G***i18n
		//have an option pane create and show a new dialog using our about panel
		BasicOptionPane.showMessageDialog(this, aboutPanel, dialogTitle, JOptionPane.INFORMATION_MESSAGE);	//G***check and see why we originally had a more complex version
/*G***del if not needed
			//create a new dialog for our about panel
		final JDialog aboutDialog=new OptionPane(aboutPanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(this, dialogTitle);
		aboutDialog.pack();	//pack the dialog
		aboutDialog.setVisible(true);	//show the dialog
*/
//G***del if not needed		new OptionPane(aboutPanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(this, dialogTitle).show();  //G***check
	}

	/**Determines whether the frame and application can close.
	@return <code>true</code> if the application frame and application can close.
	@see ToolStatusPanel#canClose
	@see #getDocumentDescription
	@see #canClose(RDFResourceState)
	*/
	public boolean canClose()
	{
		return canCloseFile();	//return whether or not the current file can be closed
	}

	/**@return <code>true</code> if the currently open file can be closed, else
		<code>false</code> if closing should be cancelled.
	If the content pane is an <code>ToolStatusPanel</code>, its
		<code>canClose()</code> method is called.
	If there is a document description, the frame's <code>canClose()</code> method
		is called for the description. 
	*/
	public boolean canCloseFile()
	{
		boolean canClose=true;	//default to being able to be closed
		if(getContentPane() instanceof CanClosable)	//if the content pane knows how to ask about closing
		{
			canClose=((CanClosable)getContentPane()).canClose();	//ask if the content pane can be closed
		}
		if(canClose)	//if everything looks like we can close so far
		{
			final ObjectState<RDFResource> description=getDocumentDescription();	//get the current document description
			if(description!=null && description!=getContentPane())	//if we have a document description, and the description isn't the content pane (otherwise we would call canClose() twice)
				canClose=canClose(description);	//see if we can close the description
		}
		return canClose;	//return whether we can close the frame
	}	

	/**Determines if the document with the given description can be closed.
	@param description The description of the document requesting to be closed.
	@return <code>true</code> if the specified document can be closed, else
		<code>false</code> if closing should be canceled.
	*/
	public boolean canClose(final ObjectState<RDFResource> description)
	{
		if(description.isModified()) //if the document has been modified
		{
				//see if they want to remove save the document G***probably put the filename in, if available
			switch(JOptionPane.showConfirmDialog(this, "Document modified; save modifications?", "Save File", JOptionPane.YES_NO_CANCEL_OPTION)) //G***i18n
			{
				case JOptionPane.YES_OPTION: //if they want to save
					return saveFile(); //save the file and return whether it was successful
				case JOptionPane.NO_OPTION: //if they don't want to save
					return true;  //allow the frame to be closed
				default:  //for any other option (including cancel or simply escaping from the dialog box)
					return false; //don't allow the frame to be closed
			}
		}
		return true;  //default to allowing the document to be closed
	}

	/**Asks the user for a file for opening.
	@return The URI to use for opening the document, or <code>null</code> if the
		file should not be opened or if the user cancels.
	*/
	protected URI askOpenFile()
	{
		return null;  //by default we do not ask for a file G***maybe make a default file asking
	}

	/**Opens the file at the given location.
		<p>Any class that overrides this class should call this version <em>after</em>
		the operation so that the description can be updated.</p>
	@param uri The URI of the file to open.
	@return A description of the opened document, or <code>null</code> if the
		document was not opened.
	*/
	protected ObjectState<RDFResource> openFile(final URI uri)	//G***fix the "call this version afterwards" and delete if not needed
	{
/*G***fix; maybe this can't go here in the new architecture
		if(!description.getResource().getReferenceURI().equals(uri))	//if the URI wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
		{
				//create a copy of the resource description, using the new URI
			final RDFResource newResource=new DefaultRDFResource(description.getResource(), uri);	//G***but what if the other resource was a special type?
			documentDescription.setResource(newResource);	//update the resource to the one with the new URI
		}
*/		
/*G***fix
		setFile(file);  //set the file used for this document
		return true;  //this version always succeeds
*/
		return null;	//G***fix
	}

	/**Asks the user for a location for saving.
	@return The location to use for saving the document, or <code>null</code>
		if the file should not be saved or if the user cancels.
	*/
	protected URI askSaveFile()
	{
		return null;  //by default we do not ask for a file G***maybe make a default file asking
	}

	/**Retrieves the file used for the currently opened document.
	@return The file used for the currently opened document, or <code>null</code>
		if no file is available.
	*/
/*G***fix
	protected File getFile()
	{
		return file;  //return whatever file we know about
	}
*/

	/**Sets the file used for the currently opened document.
	@param newFile The file to use for saving the document.
	*/
/*G***fix
	protected void setFile(final File newFile)
	{
		file=newFile; //store the file
	}
*/

	/**Sets the application status label.
	@param status The new status to display.
	*/
	public void setStatus(final String status)
	{
/*G***fix		
//G***del Debug.trace("told to change status"); //G***del
		if(statusStatusLabel!=null) //if we have a status label
		{
//G***del Debug.trace("changing status to: ", status); //G***del
		  statusStatusLabel.setText(status);	//set the status
//G***del 			statusStatusLabel.repaint();  //G***del; testing
//G***del 			statusBar.repaint();  //G***del; testing
		}
*/		
	}

	/**Displays an error messages for an exception.
	@param title The error message box title.
	@param exception The exception that caused the error.
	*/
	protected void displayError(final String title, final Exception exception)
	{
		Debug.trace(exception);	//log the error
		final String errorMessage;	//we'll store the error message in this variable
		if(exception instanceof FileNotFoundException)	//if a file was not found
		{
			errorMessage="File not found: "+exception.getMessage();	//G***comment; i18n
		}
		else if(exception instanceof CharConversionException)	//if there was an error converting characters; G***put this elsewhere, fix for non-Sun JVMs
		{
			errorMessage="Invalid character encountered for file encoding.";	//G***comment; i18n
		}
		else  //for any another error
		{
		  final String message=exception.getMessage();  //get the exception message
			errorMessage=message!=null ? message : exception.getClass().getName();  //if the message is null, use the class name of the exception as the message
		}
//G***del Debug.trace("Ready to display error message: "+errorMessage);
		final String displayMessage=Strings.wrap(errorMessage, 100);	//wrap the error message at 100 characters G***probably use a constant here
		JOptionPane.showMessageDialog(this, displayMessage, title, JOptionPane.ERROR_MESSAGE);	//G***i18n; comment
	}

	/**Action for closing the frame.*/
	class CloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public CloseAction()
		{
			super("Close");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Close the window");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Close the window.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EXIT_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			close(); //close the frame
		}
	}

	/**Action for exiting the application.*/
	class ExitAction extends AbstractAction
	{
		/**Default constructor.*/
		public ExitAction()
		{
			super("Exit");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Exit the application");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Exit the application.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EXIT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
//G***del when works			exit(); //exit the application
			close(); //close the frame; this assumes that setDefaultCloseOperation has been set to DISPOSE_ON_CLOSE or EXIT_ON_CLOSE
		}
	}

	/**Action for creating a new file.*/
	class FileNewAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileNewAction()
		{
			super("New...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Create a new file ");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Create a new file.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.NEW_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			newFile(); //create a new file
		}
	}

	/**Action for opening a a file.*/
	class FileOpenAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileOpenAction()
		{
			super("Open...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Open a file ");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Bring up a dialog to select a file, and then load the selected file.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.OPEN_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
//G***fix			openFile(); //open a file
		}
	}

	/**Action for closing a file.*/
	class FileCloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileCloseAction()
		{
			super("Close");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Close the open file.");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Close the currently open file.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.CLOSE_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			closeFile();	//close the file
		}
	}

	/**Action for saving a file.*/
	class FileSaveAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileSaveAction()
		{
			super("Save");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Save the open file");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Save the currently open file.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //add the accelerator G***i18n
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			saveFile();	//save the file
		}
	}

	/**Action for saving a file under a different name.*/
	class FileSaveAsAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileSaveAsAction()
		{
			super("Save As");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Save file as another filename");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Save the currently open file under a differennt filename.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_AS_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			saveFileAs();	//save the file as a different filename
		}
	}

	/**Action for showing the help contents.*/
	class HelpContentsAction extends AbstractAction
	{
		/**Default constructor.*/
		public HelpContentsAction()
		{
			super("Help Topics...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Help Topics");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Bring up a list of topics for finding help.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.HELP_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
		  helpContents(); //show the help contents
		}
	}

	/**Action for showing the help about dialog.*/
	class AboutAction extends AbstractAction
	{
		/**Default constructor.*/
		public AboutAction()
		{
			super("About...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "About the Application");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Show more information about the application.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.INFO_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
		  helpAbout();  //show the help about
		}
	}

}
