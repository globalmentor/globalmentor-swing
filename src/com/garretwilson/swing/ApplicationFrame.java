package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import com.garretwilson.awt.*;
import com.garretwilson.lang.StringUtilities;
import com.garretwilson.rdf.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.util.Debug;

/**Main frame parent class for an application. This frame expects to contain
	an <code>ApplicationPanel</code>.
<p>This class maintains its own local default close operation setting, and
	sets the parent class default close operation to
	<code>DO_NOTHING_ON_CLOSE</code>. This allows the class listen to window
	events and perform consistent <code>canClose()</code> checks for all methods
	of closing. This is all handled transparently&mdash;once closing should occur,
	the local default close operation setting is honored as normal.</p>
<p>The default close operation in this class by default is
	<code>DISPOSE_ON_CLOSE</code>. The action returned by
	<code>getFileExitAction()</code> will reflect the default close operation
	in effect.</p>
@author Garret Wilson
@see ApplicationPanel
*/
public abstract class ApplicationFrame extends JFrame
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

	/**The default close operation, which defaults to <code>DISPOSE_ON_CLOSE</code>.*/
	private int defaultCloseOperation=DISPOSE_ON_CLOSE;

		/**@return An integer representing the operation that occurs when the user
			closes the frame.
		@see #setDefaultCloseOperation
		*/
		public int getDefaultCloseOperation() {return defaultCloseOperation;}

		/**Sets the operation that will happen by default when the user closes the
			frame.
		The value is set to <code>DISPOSE_ON_CLOSE</code> by default.
		@param operation The operation which should be performed when the user closes
			the frame
		@exception IllegalArgumentException Thrown if defaultCloseOperation value 
			isn't a valid value.
		@see JFrame#setDefaultCloseOperation
		@throws SecurityException Thrown if <code>EXIT_ON_CLOSE</code> has been
			specified and the SecurityManager will not allow the caller to invoke
			<code>System.exit()</code>.
		*/
		public void setDefaultCloseOperation(final int operation)
		{
			if(operation!=DO_NOTHING_ON_CLOSE && operation!=HIDE_ON_CLOSE && operation!=DISPOSE_ON_CLOSE && operation!=EXIT_ON_CLOSE)
			{
				throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
			}
			if(defaultCloseOperation!=operation)	//if the operation is really changing
			{
				switch(operation)	//see which operation they want
				{
					case EXIT_ON_CLOSE:	//if they want to exit on close
						{
							final SecurityManager securityManager=System.getSecurityManager();	//get the security manager
							if(securityManager!=null)	//if there is a security manager
								securityManager.checkExit(0);	//see if we have the rights to exit
						}
						break;
				}
				final int oldOperation=defaultCloseOperation;	//save the old operation
				defaultCloseOperation=operation;	//actually update the default close operation
				firePropertyChange("defaultCloseOperation", oldOperation, operation);	//notify listeners that the value changed
			}
		}
	
	/**The description used for the currently opened document.*/
//G***del	private DocumentDescribable description=null;

	/**The description of the currently opened document.*/
	private ResourceDescribable documentDescription=null;

		/**@return The description of the currently opened document.*/
		protected ResourceDescribable getDocumentDescription() {return documentDescription;}

		/**Sets the description of the currently opened document.
		@param description The description of the currently opened document.
		*/
		protected void setDocumentDescription(final ResourceDescribable description) {documentDescription=description;}

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

		/**@return The action for exiting, either <code>getCloseAction()</code> or
			<code>getExitAction()</code>.
		@see #getDefaultCloseOperation
		@see #getCloseAction
		@see #getExitAction
		*/
		public Action getFileExitAction()
		{
			return getDefaultCloseOperation()==EXIT_ON_CLOSE ? getExitAction() : getCloseAction();	//G***maybe check for DO_NOTHING_ON_CLOSE as well 
		}

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


	/**Default constructor.
	Enables window events.
	*/
	public ApplicationFrame()
	{
		this(true); //create an application frame with a default application panel
	}

	/**Constructor with a default panel.
	Enables window events.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final boolean initialize)
	{
		this(new ApplicationPanel(), initialize); //create an application frame with a default application panel
	}

	/**Application panel constructor.
	  Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ApplicationPanel</code>.
	*/
	public ApplicationFrame(final Container contentPane)
	{
		this(contentPane, true);  //construct and initialize the frame
	}

	/**Application panel constructor with optional initialization.
		Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ApplicationPanel</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final Container contentPane, final boolean initialize)
	{
		this(contentPane, true, initialize);	//construct the application frame with a menubar 
	}
	
	/**Application panel constructor with optional initialization.
	  Enables window events.
	@param contentPane The container to be used as the content pane; usually
		an <code>ApplicationPanel</code>.
	@param hasMenuBar Whether this frame should have a toolbar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final Container contentPane, final boolean hasMenuBar, final boolean initialize)
	{
		  //don't do anything automatically on close; we'll handle responding to close events
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	//tell the parent to set its default close operation G***this implementation depends on the fact that the super class doesn't use the accessor methods---that's probably dangerous
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); //enable window events, so that we can respond to close events
		setContentPane(contentPane); //set the container as the content pane
		fileNewAction=new FileNewAction();  //create the new action G***maybe lazily create these
		fileOpenAction=new FileOpenAction();  //create the open action
		fileCloseAction=new FileCloseAction();  //create the close action
//G***del		fileCloseAction.setEnabled(false); //default to nothing to close
		fileSaveAction=new FileSaveAction();  //create the save action
		fileSaveAsAction=new FileSaveAsAction();  //create the save as action
//G***del		fileSaveAction.setEnabled(false); //default to nothing to save
		helpContentsAction=new HelpContentsAction();
		aboutAction=new AboutAction();
		closeAction=new CloseAction();  //create the close action
		exitAction=new ExitAction();  //create the exit action
		if(hasMenuBar)  //if we should have a menu bar
		{
			menuBar=createMenuBar();  //create the menu bar
			setJMenuBar(menuBar); //set the menu bar
		}
		else  //if we shouldn't have a menu bar
		{
			menuBar=null; //show that we don't have a menu bar
		}
/*G***fix
		initializeUI(); //initialize the user interface
		updateActions();  //update the actions
*/
		if(initialize)  //if we should initialize
		  initialize(); //initialize the frame
	}


	/**Initializes the frame. Should only be called once per instance.
	@see #initializeUI
	*/
	protected void initialize()
	{
		initializeUI(); //initialize the user interface
		pack();	//set the initial size to its default
		updateStatus();  //update the actions
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
		setTitle(getApplicationName());  //set the frame to reflect the application name
		initializeMenuBar(getJMenuBar());	//initialize the menu bar
//G***bring back		setSize(800, 600);	//default to 800X600; the window can be maximized after it's shown G***determine the initial size based upon the resolution
//G***fix		setExtendedState(MAXIMIZED_BOTH);	//maximize the frame G***get this from preferences
//G***transfer this to WindowUtilities, maybe		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
//G***fix		WindowUtilities.maximize(this); //maximize the frame G***remove this, as JDK 1.4 has a programmatic maximization
  }

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		final ResourceDescribable description=getDocumentDescription();	//see what document is being described
		getFileSaveAction().setEnabled(description!=null && description.isModified());	//only enable saving when there is a document that's modified
	}

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
	public ResourceDescribable openFile()
	{
		ResourceDescribable description=null;	//assume for now that the operation will be canceled
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
		final ResourceDescribable description=getDocumentDescription(); //get a description of the document
		if(description!=null) //if we have a description of the document
		{
//G**del Debug.trace("found document description");
			if(!description.getResource().isAnonymous())  //if the file location is specified
				return saveFile(description, description.getResource().getReferenceURI()); //save using the URI specified by the description
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
		final ResourceDescribable description=getDocumentDescription(); //get a description of the document
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
	public boolean saveFileAs(final ResourceDescribable description)
	{
		boolean result=false; //start out assuming the file won't be saved
		final URI uri=askSaveFile();  //get the URI to use
		if(uri!=null)  //if a valid URI was returned
		{
			result=saveFile(description, uri); //save the file
			if(result)  //if the file was saved without being canceled
			{
				if(!description.getResource().getReferenceURI().equals(uri))	//if the URI wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
				{
						//create a copy of the resource description, using the new URI
					final RDFResource newResource=new DefaultRDFResource(description.getResource(), uri);	//G***but what if the other resource was a special type?
					documentDescription.setResource(newResource);	//update the resource to the one with the new URI
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
	protected boolean saveFile(final ResourceDescribable description, final URI uri)
	{
		if(!description.getResource().getReferenceURI().equals(uri))	//if the URI should be changed
		{
				//create a copy of the resource description, using the new URI
			final RDFResource newResource=new DefaultRDFResource(description.getResource(), uri);	//G***but what if the other resource was a special type?
			documentDescription.setResource(newResource);	//update the resource to the one with the new URI
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
		final AboutPanel aboutPanel=new AboutPanel();	//create a new about panel
		aboutPanel.setTitle(getApplicationName());  //set the panel title to the application name, if available
		aboutPanel.setVersion(getVersion());  //set the version information, if available
		aboutPanel.setCopyright(getCopyright());  //set the copyright information, if available
			//determine a title for the dialog, based upon the application name
		final String dialogTitle="About"+(getApplicationName()!=null ? " "+getApplicationName() : "");
		//have an option pane create and show a new dialog using our about panel
		new JOptionPane(aboutPanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(this, dialogTitle).show();  //G***fix title
	}


	/**Closes the frame, according to the default close settings.
	@see #getDefaultCloseOperation
	*/
	public void close()
	{
		if(getDefaultCloseOperation()==DO_NOTHING_ON_CLOSE || canClose())	//if we should do something on close, make sure we can close
		{
			switch(getDefaultCloseOperation())	//see how we should close
			{
				case HIDE_ON_CLOSE:	//if we should hide
					setVisible(false);	//hide the frame
					break;
				case DISPOSE_ON_CLOSE:	//if we should dispose the frame
					setVisible(false);			//hide the frame
					dispose();							//dispose the frame
					break;
				case EXIT_ON_CLOSE:	//if we should exit
					exit();			//exit
					break;
				case DO_NOTHING_ON_CLOSE:	//if we should do nothing
				default:
					break;
			}
		}
	}

	/**Exits the application with no status.*/
	protected void exit()
	{
		exit(0);	//exit with no status
	}
	
	/**Exits the application with the given status.
	@param status The exit status.
	*/
	protected void exit(final int status)
	{
//G***del when works		if(canClose())	//if we can close
		System.exit(status);	//close the program with the given exit status
	}

	/**Determines whether the frame and application can close.
	@return <code>true</code> if the application frame and application can close.
	@see ApplicationPanel#canClose
	@see #getDocumentDescription
	@see #canClose(ResourceDescribable)
	*/
	public boolean canClose()
	{
		return canCloseFile();	//return whether or not the current file can be closed
	}

	/**@return <code>true</code> if the currently open file can be closed, else
		<code>false</code> if closing should be cancelled.
	If the content pane is an <code>ApplicationPanel</code>, its
		<code>canClose()</code> method is called.
	If there is a document description, the frame's <code>canClose()</code> method
		is called for the description. 
	*/
	public boolean canCloseFile()
	{
		boolean canClose=true;	//default to being able to be closed
		if(getContentPane() instanceof ApplicationPanel)	//if the content pane is an application panel
		{
			canClose=((ApplicationPanel)getContentPane()).canClose();	//ask if the application panel can be closed
		}
		if(canClose)	//if everything looks like we can close so far
		{
			final ResourceDescribable description=getDocumentDescription();	//get the current document description
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
	public boolean canClose(final ResourceDescribable description)
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
	protected ResourceDescribable openFile(final URI uri)	//G***fix the "call this version afterwards" and delete if not needed
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
		else if(exception instanceof sun.io.MalformedInputException)	//if there was an error converting characters; G***put this elsewhere, fix for non-Sun JVMs
		{
			errorMessage="Invalid character encountered for file encoding.";	//G***comment; i18n
		}
		else  //for any another error
		{
		  final String message=exception.getMessage();  //get the exception message
			errorMessage=message!=null ? message : exception.getClass().getName();  //if the message is null, use the class name of the exception as the message
		}
//G***del Debug.trace("Ready to display error message: "+errorMessage);
		final String displayMessage=StringUtilities.wrap(errorMessage, 100);	//wrap the error message at 100 characters G***probably use a constant here
		JOptionPane.showMessageDialog(this, displayMessage, title, JOptionPane.ERROR_MESSAGE);	//G***i18n; comment
	}

	/**Overrides the windows event method so that we can exit when the window
		closes. Instead of overriding this method, child classes are encouraged
		to simply override <code>exit()</code>, making sure to call the super class
		after doing the pre-exit cleanup.
	@param windowEvent The window event.
	*/
	protected void processWindowEvent(final WindowEvent windowEvent)
	{
		super.processWindowEvent(windowEvent);  //do the default processing
		if(windowEvent.getID()==WindowEvent.WINDOW_CLOSING)  //if this is a window closing event
		{
			close();	//close the window
		}
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
			putValue(MNEMONIC_KEY, new Integer('o'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('x'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('n'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('o'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('c'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('s'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('a'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('h'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('a'));  //set the mnemonic key G***i18n
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
