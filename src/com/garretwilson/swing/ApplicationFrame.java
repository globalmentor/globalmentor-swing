package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.garretwilson.lang.StringUtilities;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.util.Debug;

/**Main frame parent class for  an application.
@author Garret Wilson
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


	/**The file used for the currently opened document.*/
	private File file=null;

	/**The description of the currently opened document.*/
//G***del if not needed	private DocumentDescribable documentDescription=new DocumentDescription();


	/**The action for creating a new file.*/
	private Action fileNewAction;

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
	private Action fileOpenAction;

		/**@return The action for opening a file.*/
		public Action getFileOpenAction() {return fileOpenAction;}

	/**The action for closing a file.*/
	private Action fileCloseAction;

		/**@return The action for closing a file.*/
		public Action getFileCloseAction() {return fileCloseAction;}

	/**The action for saving a file.*/
	private Action fileSaveAction;

		/**@return The action for saving a file.*/
		public Action getFileSaveAction() {return fileSaveAction;}

	/**The action for saving a file under a different name.*/
	private Action fileSaveAsAction;

		/**@return The action for saving a file under a different name.*/
		public Action getFileSaveAsAction() {return fileSaveAsAction;}

	/**The action for showing help contents.*/
	private Action helpContentsAction;

		/**@return The action for showing help contents.*/
		public Action getHelpContentsAction() {return helpContentsAction;}

	/**The action for showing an about box.*/
	private Action aboutAction;

		/**@return The action for showing an about box.*/
		public Action getAboutAction() {return aboutAction;}

	/**The action for exiting the application.*/
	private Action exitAction;

		/**@return The action for exiting the application.*/
		public Action getExitAction() {return exitAction;}

  /**The application toolbar.*/
	private final JToolBar toolBar;

		/**@return The application toolbar.*/
		protected JToolBar getToolBar() {return toolBar;}

	/**The application status bar.*/
	private final JPanel statusBar;

	/**The label to display the status.*/
	private final JLabel statusStatusLabel;

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

		/**Sets the copyright text. Should be called from <code>initializeData()</code>.
		@param newCopyright The new copyright text, or <code>null</code> if there
			should be no copyright text.
		*/
		public void setCopyright(final String newCopyright) {copyright=newCopyright;}

	/**The application name.*/
	private String applicationName=null;

		/**@return The application name, or <code>null</code> if there is no name.*/
		public String getApplicationName() {return applicationName;}

		/**Sets the application name. Should be called from <code>initializeData()</code>.
		@param newApplicationName The new application name, or
			<code>null</code> if there should be no application name.
		*/
		public void setApplicationName(final String newApplicationName) {applicationName=newApplicationName;}

	/**The application version.*/ //G***maybe rename this to applicationVersion
	private String version=null;

		/**@return The version text, or <code>null</code> if there is no text.*/
		public String getVersion() {return version;}

		/**Sets the version text. Should be called from <code>initializeData()</code>.
		@param newVersion The new version text, or <code>null</code> if there
			should be no version text.
		*/
		public void setVersion(final String newVersion) {version=newVersion;}

/*G***del
	//menu variables
	private JMenuBar menuBar=new JMenuBar();
		//File menu
		private JMenu fileMenu=new JMenu();
		//Edit menu
		private JMenu editMenu=new JMenu();
		//View menu
		private JMenu viewMenu=new JMenu();
		private ButtonGroup displayPageCountButtonGroup=new ButtonGroup();
		private JRadioButtonMenuItem viewNumPagesOnePageMenuItem;
		private JRadioButtonMenuItem viewNumPagesTwoPagesMenuItem;
		private ButtonGroup layoutButtonGroup=new ButtonGroup();
		private JRadioButtonMenuItem viewLayoutRedmondMenuItem;
		private JRadioButtonMenuItem viewLayoutSanFranciscoMenuItem;
		private JRadioButtonMenuItem viewLayoutSanFranciscoSmallMenuItem;
		private JRadioButtonMenuItem viewLayoutCustomMenuItem;
		//View|Zoom menu
		JMenu viewZoomMenu=new JMenu();
		private ButtonGroup zoomButtonGroup=new ButtonGroup();
		private JCheckBoxMenuItem viewAntialiasMenuItem;
		//Insert menu
		private JMenu insertMenu=new JMenu();
		//Go menu
		private JMenu goMenu=new JMenu();
		private JMenu goGuidesMenu=new JMenu();
		private JMenu goBookmarksMenu=new JMenu();
		//Help menu
		private JMenu helpMenu = new JMenu();
		private JCheckBoxMenuItem helpLogDebugMenuItem;
	//toolbar variables
	private JToolBar toolBar = new JToolBar();
	//status bar variables
	private JPanel statusBar=new JPanel();
	GridBagLayout statusGridBagLayout=new GridBagLayout();	//create a layout for the status bar
	JLabel statusStatusLabel=new JLabel();
	JProgressBar statusProgressBar=new JProgressBar();
//G***fix	JSlider statusSlider=new JSlider();
	JScrollBar statusScrollBar=new JScrollBar(JScrollBar.HORIZONTAL);
	JComboBox guideComboBox=new JComboBox();
*/

	/**Default constructor.
	Enables window events.
	*/
	public ApplicationFrame()
	{
		this(true, true); //default to having a menu bar and a status bar
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	  Enables window events.
	@param hasMenuBar Whether this frame should have a menu bar.
	@param hasStatusBar Whether this frame should have a status bar.
	*/
	public ApplicationFrame(final boolean hasMenuBar, final boolean hasStatusBar)
	{
/*G***del
		Debug.assert(newReaderConfig!=null, "Reader configuration is null.");
		setReaderConfig(newReaderConfig);	//save the reader configuration we were passed
*/
		  //don't do anything automatically on close; we'll handle responding to close events
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); //enable window events, so that we can respond to close events
		initializeData();  //create the application actions
    final BorderLayout borderLayout=new BorderLayout();
    this.getContentPane().setLayout(borderLayout);  //use a border layout for the content pane
		if(true/**G***fix hasToolBar*/)  //if we should have a toolbar
		{
			toolBar=createToolBar();  //create the toolbar
	    this.getContentPane().add(toolBar, BorderLayout.NORTH); //put the toolbar in the north of the content pane
		}
		else  //if we shouldn't have a toolbar
		{
			toolBar=null; //show that we don't have a toolbar
		}
		if(hasMenuBar)  //if we should have a menu bar
		{
			menuBar=createMenuBar();  //create the menu bar
			setJMenuBar(menuBar); //set the menu bar
		}
		else  //if we shouldn't have a menu bar
		{
			menuBar=null; //show that we don't have a menu bar
		}
		if(hasStatusBar)  //if we should have a status bar
		{
//G***del Debug.trace("creating status label"); //G***del
			statusStatusLabel=new JLabel();  //create a status label G***this isn't really designed correctly
//G***del Debug.trace("creating status bar"); //G***del
			statusBar=createStatusBar();  //create the menu bar
//G***del Debug.trace("adding status bar"); //G***del
	    this.getContentPane().add(statusBar, BorderLayout.SOUTH); //put the status bar in the south of the content pane
		}
		else  //if we shouldn't have a status bar
		{
			statusBar=null; //show that we don't have a status bar
			statusStatusLabel=null; //show that we don't have a status label
		}
		initializeUI(); //initialize the user interface
		updateActions();  //update the actions
	}

	/**Creates any application objects and initializes data.
		Any class that overrides this method must call this version.
	*/
	protected void initializeData()
	{
		fileNewAction=new FileNewAction();  //create the new action G***maybe lazily create these
		fileOpenAction=new FileOpenAction();  //create the open action
		fileCloseAction=new FileCloseAction();  //create the close action
//G***del		fileCloseAction.setEnabled(false); //default to nothing to close
		fileSaveAction=new FileSaveAction();  //create the save action
		fileSaveAsAction=new FileSaveAsAction();  //create the save as action
//G***del		fileSaveAction.setEnabled(false); //default to nothing to save
		helpContentsAction=new HelpContentsAction();
		aboutAction=new AboutAction();
		exitAction=new ExitAction();  //create the exit action
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
		setTitle(getApplicationName());  //set the frame to reflect the application name
  }

	/**@return A new toolbar.*/
	protected JToolBar createToolBar()
	{
	  final JToolBar toolBar=new JToolBar();  //create the toolbar
		//setup the toolbar
		toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);	//fix; comment; G***testing
		return toolBar; //return the toolbar we created
	}

	/**@return A new status bar.*/
	protected JPanel createStatusBar()
	{
	  final JPanel statusBar=new JPanel();  //create the status bar
		final GridBagLayout statusGridBagLayout=new GridBagLayout();	//create a layout for the status bar
		statusBar.setBorder(BorderFactory.createEtchedBorder());	//set the status border
//G***fix		statusFlowLayout.setAlignment(FlowLayout.LEFT);	//align the status components to the left
		statusBar.setLayout(statusGridBagLayout);	//set the layout of the status bar
		statusStatusLabel.setFont(statusBar.getFont().deriveFont((float)statusBar.getFont().getSize()-1));	//G***testing
		statusBar.add(statusStatusLabel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));	//add the status label to the status bar
		return statusBar; //return the status bar we created
	}

	/**@return A new menu bar with appropriate menus.*/
	protected JMenuBar createMenuBar()
	{
	  final JMenuBar menuBar=new JMenuBar();  //create the menu bar
		if(getFileMenuInclusions()!=MENU_FILE_NONE)  //if we should have a file menu
			menuBar.add(createFileMenu());  //create and add a file menu
		if(getHelpMenuInclusions()!=MENU_HELP_NONE)  //if we should have a help menu
			menuBar.add(createHelpMenu());  //create and add a help menu
		return menuBar; //return the menu bar we created
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
			fileCloseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));	//add Ctrl+F4 as an accelerator G***why do we need this twice?
		}
		if((inclusions & MENU_FILE_SAVE)!=0) //file|save
		{
			JMenuItem fileSaveMenuItem=fileMenu.add(getFileSaveAction());
			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));	//add Ctrl+S as an accelerator G***i18n G***why do we need this twice?
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
			JMenuItem fileExitMenuItem=fileMenu.add(exitAction);
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
		<code>openFile(File)</code> should be overridden. Multiple document
		applications may also override <code>getFile()</code> and
		<code>setFile</code>.</p>
	@return <code>true</code> if the operation was not cancelled
	@see #askOpenFile
	@see #setFile
	*/
	public boolean openFile()
	{
		boolean result=false; //assume for now that the operation will be canceled
/*G***del if not needed
		final DocumentDescribable description=askOpenFile();  //get the file to use
		if(description!=null)  //if a valid file was returned
		{
			result=openFile(description);  //attempt to open the file
			if(result)  //if we opened the file successfully
			{
			  setFile(file);  //update the file, just in case they override openFile() and don't call this version
			}
		}
		return result;  //show whether the operation finished
*/
		final File file=askOpenFile();  //get the file to use
		if(file!=null)  //if a valid file was returned
		{
			result=openFile(file);  //attempt to open the file
			if(result)  //if we opened the file successfully
			{
			  setFile(file);  //update the file, just in case they override openFile() and don't call this version
			}
		}
		return result;  //show whether the operation finished
	}

	/**Closes the open file.*/
	public void closeFile()
	{
		if(canCloseFile())  //if we can close
		{
		  setFile(null);  //show that no file is available
		}
	}

	/**Saves the open file.
		By default the file is checked using <code>getFile()</code> and, if no file
		is available, the <code>saveFileAs</code> method is called.
		<p>For normal operation, this method should not be modified and
		<code>saveFile(File)</code> should be overridden. Multiple document
		applications may also override <code>getFile()</code> and
		<code>setFile</code>.</p>
	@return <code>true</code> if the operation was not cancelled.
	@see #getFile
	@see #saveFileAs
	*/
	public boolean saveFile()
	{
//G***del Debug.trace("saving file");
/*G***del if not needed
		final DocumentDescribably description=getDocumentDescription();  //get the file for saving
//G***del Debug.trace("first file for saving: ", file);
		if(description.getFile()!=null)  //if we have a file
			return saveFile(description); //save using the file
		else  //if we don't have a file
			return saveFileAs(); //call the save as function
*/
		final File file=getFile();  //get the file for saving
//G***del Debug.trace("first file for saving: ", file);
		if(file!=null)  //if we have a file
			return saveFile(file); //save using the file
		else  //if we don't have a file
			return saveFileAs(); //call the save as function
	}

	/**Saves the open file after first asking for a filename.
	@return <code>true</code> if the operation was not cancelled.
	@see #setFile
	*/
	public boolean saveFileAs()
	{
		boolean result=false; //start out assuming the file won't be saved
		final File file=askSaveFile();  //get the file to use
		if(file!=null)  //if a valid file was returned
		{
			result=saveFile(file); //save the file
			if(result)  //if the file was saved without being canceled
			{
			  setFile(file);  //update the file, just in case they override saveFile() and don't call this version
			}
		}
		return result;  //show whether the operation completed
/*G***del
		else  //if a file was not returned to use
			return false; //show that saving did not occur
*/
	}

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

	/**Exits the application with no status.*/
	public void exit()
	{
		exit(0);	//exit with no status
	}
	
	/**Exits the application with the given status.
	@param status The exit status.
	*/
	public void exit(final int status)
	{
		System.exit(status);	//close the program with the given exit status
	}
	

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateActions()
	{
	}

	/**@return <code>true</code> if the currently open file can be closed, else
		<code>false</code> if closing should be cancelled.
	*/
	public boolean canCloseFile()
	{
	  return true;  //default to allowing the file to be closed
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
	@return The file to use for opening the document, or <code>null</code> if the
		file should not be opened or if the user cancels.
	*/
	protected File askOpenFile()
	{
		return null;  //by default we do not ask for a file G***maybe make a default file asking
	}

	/**Opens the file at the given location.
		<p>Any class that overrides this class should call this version <em>after</em>
		the operation so that the file can be updated.</p>
	@param file The file to open.
	@return <code>true</code> if the operation was not cancelled.
	*/
	protected boolean openFile(final File file)
	{
		setFile(file);  //set the file used for this document
		return true;  //this version always succeeds
	}

	/**Asks the user for a file for saving.
	@return The file to use for saving the document, or <code>null</code> if the
		file should not be saved or if the user cancels.
	*/
	protected File askSaveFile()
	{
		return null;  //by default we do not ask for a file G***maybe make a default file asking
	}

	/**Saves the file at the specified location.
		This version only updates the filename for the currently open file.
		<p>Any class that overrides this class should call this version so that the
		file can be updated.</p>
	@param file The location at which the file should be saved.
	@return <code>true</code> if the operation was not cancelled.
	*/
	protected boolean saveFile(final File file)
	{
		setFile(file);  //set the file used for this document
		return true;  //this version always succeeds
	}

	/**Retrieves the file used for the currently opened document.
	@return The file used for the currently opened document, or <code>null</code>
		if no file is available.
	*/
	protected File getFile()
	{
		return file;  //return whatever file we know about
	}

	/**Sets the file used for the currently opened document.
	@param newFile The file to use for saving the document.
	*/
	protected void setFile(final File newFile)
	{
		file=newFile; //store the file
	}



	/**Retrieves the description of the currently opened resource.
	@return A description of the currently opened resource, or <code>null</code>
		if no resource is available.
	*/
/*G***fix
	protected RDFResource getResourceDescription()
	{
		if(file!=null)  //if we have a file
		{


		}


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
//G***del Debug.trace("told to change status"); //G***del
		if(statusStatusLabel!=null) //if we have a status label
		{
//G***del Debug.trace("changing status to: ", status); //G***del
		  statusStatusLabel.setText(status);	//set the status
//G***del 			statusStatusLabel.repaint();  //G***del; testing
//G***del 			statusBar.repaint();  //G***del; testing
		}
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
		if(windowEvent.getID()==WindowEvent.WINDOW_CLOSING/*G***del && exitAction!=null*/)  //if this is a window closing event G***fix with the JDK 1.3 window close stuff
		{
			getExitAction().actionPerformed(null);  //perform the exit action
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
			exit(); //exit the application
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
			openFile(); //open a file
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
			putValue(SHORT_DESCRIPTION, "About Mentoract Reader");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Show more information about Mentoract Reader.");	//set the long description G***Int
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
