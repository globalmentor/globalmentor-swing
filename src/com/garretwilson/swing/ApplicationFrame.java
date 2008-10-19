package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.rdf.*;
import com.globalmentor.rdf.dublincore.RDFDublinCore;
import com.globalmentor.util.Debug;

/**Main frame parent class for an application.
<p>This class requires that the content pane be an instance of
	<code>ApplicationFrame.ApplicationContentPane</code>.</p>
<p>If an <code>Application</code> is set for the frame, the close
	operation changes to <code>EXIT_ON_CLOSE</code>. (This is essential to work
	around a JDK bug that under certain instances runs a daemon thread that
	prevents the JVM from exiting, even if the main program thread has finished
	and the sole frame has closed.) If there is no <code>Application</code> set,
	the close operation remains the default, <code>DISPOSE_ON_CLOSE</code>.</p>
<p>This class maintains the default close operation of
	<code>DO_NOTHING_ON_CLOSE</code> . This allows the class listen to window
	events and perform consistent <code>canClose()</code> checks for all methods
	of closing. This is all handled transparently&mdash;once closing should occur,
	the local default close operation setting is honored as normal.</p>
<p>If an application is provided, the application's preference node is used
	to store user preferences.</p> 
@author Garret Wilson
@see Application
@see SwingApplication
*/
public class ApplicationFrame extends BasicFrame
{

	/**The application this frame represents, or <code>null</code> if there is no
		application information.
	*/
	private final SwingApplication application;

		/**@return The application this frame represents, or <code>null</code> if
			there is no application information.
		*/
		public final SwingApplication getApplication() {return application;}
//
	/**The action for file|exit; defaults to <code>getExitAction()</code>.*/
	private Action fileExitAction;

	/**The action for showing an about box.*/
	private final Action aboutAction;

		/**@return The action for showing an about box.*/
		public Action getAboutAction() {return aboutAction;}

	/**The proxy action for closing the frame or exiting the application.
	<p>Defaults to <code>getExitAction()</code> if there is an application, or
		<code>getCloseAction()</code> if there is no application.</p>
	*/
	private final ProxyAction closeProxyAction;

		/**@return The proxy action for closing the frame and optionally exiting
			the application, depending on whether an application is present.
		@see #getApplication()
		*/
		public Action getCloseProxyAction() {return closeProxyAction;}

	/**The action for exiting the application.*/
	private final Action exitAction;

		/**@return The action for exiting the application.*/
		public Action getExitAction() {return exitAction;}

	/**Constructs a string appropriate for showing on the title of the frame.
	<p>This version returns the application name.</p>
	@return A title to display on the frame.
	*/
	protected String constructTitle()
	{
		final String title;
		if(getApplication()!=null)	//if we have an application
		{
			return getApplication().getTitle();	//get the application's title
		}
		else	//if we have no application
		{
			title=super.constructTitle();  //use the default title
		}
		return title;	//return the title we discovered
	}

	/**Sets the <code>contentPane</code> property. 
	@param contentPane the <code>contentPane</code> object for this frame
	@exception IllegalComponentStateException (a runtime
		exception) if the content pane parameter is <code>null</code>.
	@exception ClassCastException Thrown if the content pane is not an instance
		of <code>ApplicationContentPane</code>.
	@see #ApplicationContentPane
	*/
	public void setContentPane(final Container contentPane)
	{
		super.setContentPane((ApplicationContentPane)contentPane);	//make sure the content pane is of the correct type
	}

	/**@return The content pane as an application content pane; convenience method.*/
	public ApplicationContentPane getApplicationContentPane()
	{
		return (ApplicationContentPane)getContentPane();	//return the content pane cast to an application content pane
	}

	/**@return The toolbar, or <code>null</code> if there is no toolbar.*/
	public BasicToolBar getToolBar() {return getApplicationContentPane().getToolBar();}

	/**Sets the toolbar.
	@param newToolBar The new toolbar to use, or <code>null</code> if there should
		be no toolbar.
	*/
	protected void setToolBar(final BasicToolBar newToolBar)
	{
		getApplicationContentPane().setToolBar(newToolBar);
	}

	/**@return The application status bar.*/
	public StatusBar getStatusBar() {return getApplicationContentPane().getStatusBar();}

	/**Sets the status bar.
	@param newStatusBar The new status bar to use, or <code>null</code> if there should
		be no status bar.
	*/
	protected void setStatusBar(final StatusBar newStatusBar)
	{
		getApplicationContentPane().setStatusBar(newStatusBar);
	}

	/**Default constructor.*/
	public ApplicationFrame()
	{
		this((SwingApplication)null);	//construct the frame with no application	
	}

	/**Application constructor.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	*/
	public ApplicationFrame(final SwingApplication application)
	{
		this(application, true); //create an application frame with a default application panel and initialize
	}

	/**Constructor with a default panel and optional initialization.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final boolean initialize)
	{
		this((SwingApplication)null, initialize);	//construct the frame with no application	
	}

	/**Application constructor with a content pane and optional initialization.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final SwingApplication application, final boolean initialize)
	{
		this(application, null, initialize); //create an application frame with the default content pane
	}

	/**Application component constructor.
	@param applicationComponent The component to be used as application component,
		or <code>null</code> if the default application component should be used.
	*/
	public ApplicationFrame(final Component applicationComponent)
	{
		this(null, applicationComponent);	//construct the frame with no application	
	}

	/**Application and component constructor.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param applicationComponent The component to be used as application component,
		or <code>null</code> if the default application component should be used.
	*/
	public ApplicationFrame(final SwingApplication application, final Component applicationComponent)
	{
		this(application, applicationComponent, true);  //construct and initialize the frame
	}

	/**Application component constructor with optional initialization.
	@param applicationComponent The component to be used as application component,
		or <code>null</code> if the default application component should be used.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final Component applicationComponent, final boolean initialize)
	{
		this(null, applicationComponent, initialize);	//construct the frame with no application	
	}

	/**Application, and component constructor with optional initialization.
	@param application The application this frame represents, or
		<code>null</code> if there is no application information available or this
		frame doesn't represent an application.
	@param applicationComponent The component to be used as application component,
		or <code>null</code> if the default application component should be used.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationFrame(final SwingApplication application, final Component applicationComponent, final boolean initialize)
	{
		super(false);	//construct the parent class, but don't initialize it
		setContentPane(new ApplicationContentPane(applicationComponent, false, false));	//create a special application content pane and place the application component inside it		
		this.application=application;	//store the application
		if(application!=null)	//if this frame represents an application
		{
			setDefaultCloseOperation(EXIT_ON_CLOSE);	//exit when the frame closes
			try
			{
				setPreferences(application.getPreferences());	//use the application preference node
			}
			catch(SecurityException securityException)	//if we can't access preferences
			{
				Debug.warn(securityException);	//warn of the security problem			
			}
		}
		exitAction=new ExitAction();  //create the exit action
		closeProxyAction=new ProxyAction(application!=null ? getExitAction() : getCloseAction());	//create the close proxy action, proxying the exit action if we have an application
		aboutAction=new AboutAction();
		if(initialize)  //if we should initialize
		  initialize(); //initialize the frame
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
		final SwingApplication application=getApplication();	//get our application, if there is one
		final Action fileMenuAction=ActionManager.getFileMenuAction();
		actionManager.addMenuAction(fileMenuAction);	//file
		actionManager.addMenuAction(fileMenuAction, getCloseProxyAction());	//file|close/exit
		if(application!=null)	//if we have an application
		{
			final Action helpMenuAction=ActionManager.getHelpMenuAction();
			actionManager.addMenuAction(helpMenuAction);	//help
			actionManager.addMenuAction(helpMenuAction, getAboutAction());	//help|about
		}
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
/*G***del if not needed
  protected void initializeUI()
  {
  	super.initializeUI();	//do the default UI initialization
  }
*/

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
/*G***fix if needed
	protected void updateStatus()
	{
		super.updateStatus();	//update the status normally
//G***fix this; this isn't good, because if a child class uses another save action, such as a proxied action, this will screw things up
//G***fix		final RDFResourceState description=getDocumentDescription();	//see what document is being described
//G***fix		getFileSaveAction().setEnabled(description!=null && description.isModified());	//only enable saving when there is a document that's modified
	}
*/

	/**Shows information about the application.*/
	public void about()
	{
		final AboutPanel aboutPanel=new AboutPanel(getApplication());	//create a new about panel for the application
			//determine a title for the dialog, based upon the application title
		final String dialogTitle="About"+(aboutPanel.getTitle()!=null ? " "+aboutPanel.getTitle() : "");	//G***i18n
		//have an option pane create and show a new dialog using our about panel
		BasicOptionPane.showMessageDialog(this, aboutPanel, dialogTitle, JOptionPane.INFORMATION_MESSAGE);	//G***check and see why we originally had a more complex version
	}

	/**Determines whether the frame can close.
	This version attempts to save any application configuration information.
	@return <code>true</code> if the frame can close.
	*/
/*G***del; transferred to Application
	public boolean canClose()
	{
		boolean canClose=super.canClose();	//try to perform the default closing functionality
		if(canClose)	//if we can close, make sure the configuration has been saved
		{
			
				//if there is configuration information and it has been modified
			if(getApplication()!=null && getApplication().getConfiguration()!=null && getApplication().getConfiguration().isModified())
			{
				try
				{
					getApplication().getConfiguration().store();	//try to store the configuration information; if we were unsuccessful
				}
				catch(IOException ioException)	//if there is an error saving the configuration
				{
					getApplication().displayError(this, ioException);	//alert the user of the error
						//ask if we can close even though we can't save the configuration information
					canClose=JOptionPane.showConfirmDialog(this,
						"Unable to save configuration information; are you sure you want to close?",
						"Unable to save configuration", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;	//G***i18n
				}
			}
		}
		return canClose;	//return whether we can close
	}
*/

	/**Determines whether the frame and application can close.
	@return <code>true</code> if the application frame and application can close.
	@see ToolStatusPanel#canClose
	@see #getDocumentDescription
	@see #canClose(RDFResourceState)
	*/
/*G***fix
	public boolean canClose()
	{
		return canCloseFile();	//return whether or not the current file can be closed
	}
*/

	/**@return <code>true</code> if the currently open file can be closed, else
		<code>false</code> if closing should be cancelled.
	If the content pane is an <code>ToolStatusPanel</code>, its
		<code>canClose()</code> method is called.
	If there is a document description, the frame's <code>canClose()</code> method
		is called for the description. 
	*/
/*G***fix
	public boolean canCloseFile()
	{
		boolean canClose=true;	//default to being able to be closed
		if(getContentPane() instanceof CanClosable)	//if the content pane knows how to ask about closing
		{
			canClose=((CanClosable)getContentPane()).canClose();	//ask if the content pane can be closed
		}
		if(canClose)	//if everything looks like we can close so far
		{
			final RDFResourceState description=getDocumentDescription();	//get the current document description
			if(description!=null && description!=getContentPane())	//if we have a document description, and the description isn't the content pane (otherwise we would call canClose() twice)
				canClose=canClose(description);	//see if we can close the description
		}
		return canClose;	//return whether we can close the frame
	}
*/

	/**Exits the application with the given status.
	This version asks the application to exit, if there is an application.
	@param status The exit status.
	@see Application#exit(int)
	*/
	protected void exit(final int status)
	{
		if(getApplication()!=null)	//if there is an application
		{
			getApplication().exit(status);	//ask the application to exit
		}
		else	//if there is no application
		{
			super.exit(status);	//exit in the default way, if there is no application or the application couldn
		}
	}

	/**The content pane for the application frame.
		The content pane allows for a toolbar and status panel. The action manager
		it returns will be the action manager of its contained
		<code>ActionManaged</code>, if any.
	@author Garret Wilson
	*/
	protected class ApplicationContentPane extends ToolStatusPanel
	{

		/**@return The action manager of the content component, if the content
			component is action managed; otherwise, the defualt action manager.
		@see ActionManaged
		 */
		public ActionManager getActionManager()
		{
			final Component contentComponent=getContentComponent();	//get the content component
			return contentComponent instanceof ActionManaged ? ((ActionManaged)contentComponent).getActionManager() : getActionManager();	//return the content component's action manager if it has one
		}

		/**Default constructor.*/
		public ApplicationContentPane()
		{
			this(true, true); //default to having a toolbar and a status bar
		}
	
		/**Constructor that allows options to be set, such as the presence of a status
			bar.
		@param hasToolBar Whether this panel should have a toolbar.
		@param hasStatusBar Whether this panel should have a status bar.
		*/
		public ApplicationContentPane(final boolean hasToolBar, final boolean hasStatusBar)
		{
			this(hasToolBar, hasStatusBar, true); //construct and initialize the panel
		}
	
		/**Initialization constructor that allows options to be set, such as the
			presence of a status bar.
		@param hasToolBar Whether this panel should have a toolbar.
		@param hasStatusBar Whether this panel should have a status bar.
		@param initialize <code>true</code> if the panel should initialize itself by
			calling the initialization methods.
		*/
		public ApplicationContentPane(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
		{
			this(null, hasToolBar, hasStatusBar, initialize);	//construct the panel with no content component
		}
	
		/**Application component constructor.
		@param applicationComponent The new component for the center of the panel.
		*/
		public ApplicationContentPane(final Component applicationComponent)
		{
			this(applicationComponent, true, true); //do the default construction with a toolbar and a status bar
		}
	
		/**Application component constructor that allows options to be set.
		@param applicationComponent The new component for the center of the panel.
		@param hasToolBar Whether this panel should have a toolbar.
		@param hasStatusBar Whether this panel should have a status bar.
		*/
		public ApplicationContentPane(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar)
		{
			this(applicationComponent, hasToolBar, hasStatusBar, true); //construct and automatically initialize the object
		}
	
		/**Application component constructor that allows options to be set.
		@param applicationComponent The new component for the center of the panel.
		@param hasToolBar Whether this panel should have a toolbar.
		@param hasStatusBar Whether this panel should have a status bar.
		@param initialize <code>true</code> if the panel should initialize itself by
			calling the initialization methods.
		*/
		public ApplicationContentPane(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
		{
			super(applicationComponent, hasToolBar, hasStatusBar, initialize);  //construct the parent class without intializing TODO create a method to create a default center panel
/*G***fix
			toolBarPosition=BorderLayout.NORTH;	//default to the toolbar in the north
			statusBarPosition=BorderLayout.SOUTH;	//default to the status bar in the south
			toolBar=hasToolBar ? createToolBar() : null;	//create a toolbar if we should have one
			statusBar=hasStatusBar ? createStatusBar() : null;	//create a status bar if we should have one
			if(initialize)  //if we should initialize the panel
				initialize();   //initialize everything
*/
		}
	}

	/**Action for exiting the application.*/
	protected class ExitAction extends AbstractAction
	{
		/**Default constructor.*/
		public ExitAction()
		{
			super("Exit");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Exit the application");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Exit the application.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EXIT_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK)); //add the accelerator
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_EXIT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			close(); //close the frame; this assumes that setDefaultCloseOperation has been set to DISPOSE_ON_CLOSE or EXIT_ON_CLOSE
		}	
	}

	/**Action for showing the help about dialog.*/
	protected class AboutAction extends AbstractAction
	{
		/**Default constructor.*/
		public AboutAction()
		{
			super("About...");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "About the application");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Show more information about the application.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.INFO_ICON_FILENAME)); //load the correct icon
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.HELP_ABOUT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		  about();  //show the help about
		}
	}

}
