package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import com.garretwilson.io.*;
import com.garretwilson.lang.StringUtilities;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.util.Debug;

/**A panel that contains a toolbar and a status. The panel uses a border layout,
	and content can be added by a component being placed in the panel center.
@author Garret Wilson
*/
public class ApplicationPanel extends ContentPanel //G***maybe replace the content pane in Application frame with this
{

	/**Whether this panel has a toolbar; defaults to <code>true</code>.*/
//G***del if not needed	private boolean hasToolBar=true;

	/**Whether this panel has a status bar; defaults to <code>true</code>.*/
//G***del if not needed	private boolean hasStatusBar=true;

  /**The application toolbar.*/
	private JToolBar toolBar=null;

		/**@return The application toolbar, or <code>null</code> if there is no toolbar.*/
		public JToolBar getToolBar() {return toolBar;}

		/**Sets the application toolbar.
		@param newToolBar The new toolbar to use, or <code>null</code> if there should
			be no toolbar.
		*/
		public void setToolBar(final JToolBar newToolBar)
		{
			if(toolBar!=newToolBar)	//if the toolbar is really changing
			{
				if(toolBar!=null)	//if we currently have a toolbar
				{
					remove(toolBar);	//remove the old toolbar
				}
				toolBar=newToolBar;	//save the toolbar
				if(newToolBar!=null)	//if we were given a new toolbar
				{
					add(newToolBar, BorderLayout.NORTH); //put the toolbar in the north
				}
			}
		}

	/**The application status bar.*/
	private final JPanel statusBar;	//TODO create a StatusBar panel

		/**@return The application status bar.*/
		public JPanel getStatusBar() {return statusBar;}

	/**The label to display the status.*/
	private final JLabel statusStatusLabel;

	/**The progress bar label that appears on the status bar.*/
	private final JProgressBar statusProgressBar;

	/**Default constructor.*/
	public ApplicationPanel()
	{
		this(true, true); //default to having a toolbar and a status bar
	}

	/**Application component constructor.
	@param applicationComponent The new component for the center of the panel.
	*/
	public ApplicationPanel(final Component applicationComponent)
	{
		this(applicationComponent, true, true); //do the default construction with a toolbar and a status bar
/*G***del
		this(); //do the default construction
		setContentComponent(applicationComponent); //set the content component
*/
	}

	/**Application component constructor that allows options to be set.
	@param applicationComponent The new component for the center of the panel.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public ApplicationPanel(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar)
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
	public ApplicationPanel(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		this(hasToolBar, hasStatusBar, false); //do the default construction without initializing
		setContentComponent(applicationComponent); //set the content component
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public ApplicationPanel(final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(hasToolBar, hasStatusBar, true); //construct and initialize the panel
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationPanel(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(false);  //construct the parent class without intializing
		final JToolBar toolbar=hasToolBar ? createToolBar() : null;	//create a toolbar if we should have one
		setToolBar(toolbar);	//set the toolbar
		if(hasStatusBar)  //if we should have a status bar
		{
//G***del Debug.trace("creating status label"); //G***del
			statusStatusLabel=new JLabel();  //create a status label G***this isn't really designed correctly
		  statusProgressBar=new JProgressBar(); //create a status progress bar
//G***del Debug.trace("creating status bar"); //G***del
			statusBar=createStatusBar();  //create the menu bar
//G***del Debug.trace("adding status bar"); //G***del
	    add(statusBar, BorderLayout.SOUTH); //put the status bar in the south
		}
		else  //if we shouldn't have a status bar
		{
			statusBar=null; //show that we don't have a status bar
			statusStatusLabel=null; //show that we don't have a status label
			statusProgressBar=null; //show that we don't have a status progress bar
		}
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything
	}

	/**Creates any application objects and initializes data.
		Any class that overrides this method must call this version.
	*/
/*G***fix
	protected void initializeData()
	{
		super.initializeData(); //initialize the default data
		toolBar=createToolBar();  //create the toolbar G***fix creation order problem
	}
*/

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
		super.initializeUI(); //do the default UI initialization
		if(getToolBar()!=null) //if we have a toolbar
			initializeToolBar(getToolBar());  //initialize the toolbar
		if(getStatusBar()!=null) //if we have a status bar
			initializeStatusBar(getStatusBar());  //initialize the status bar
  }
	
	/**@return A new toolbar.*/
	protected JToolBar createToolBar()
	{
	  final JToolBar toolBar=new JToolBar();  //create the toolbar
		//setup the toolbar
		toolBar.setRollover(true);	//default to a rollover toolbar
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
		statusProgressBar.setStringPainted(true); //G***fix; comment
		statusProgressBar.setFont(statusProgressBar.getFont().deriveFont((float)statusProgressBar.getFont().getSize()-1));	//G***testing; fix
		statusBar.add(statusProgressBar, new GridBagConstraints(1, 0, 1, 1, 0.5, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));	//G***testing
		return statusBar; //return the status bar we created
	}

	/**Initializes the toolbar components.
	@param toolBar The toolbar to be initialized.
	*/
	protected void initializeToolBar(final JToolBar toolBar)
	{
	}

	/**Initializes the status bar components.
	@param statusBar The status bar to be initialized.
	*/
	protected void initializeStatusBar(final JPanel statusBar)
	{
	}

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
/*G**fix or del
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
*/

	/**Sets the position of the toolbar. If there is no toolbar, no action occurs.
	@param position The new position, one of the <code>BorderLayout</code> constants
		such as <code>BorderLayout.NORTH</code>.
	*/
	public void setToolBarPosition(final String position)
	{
		if(toolBar!=null) //if we have a toolbar
		{
				//G***verify the position is not BorderLayout.CENTER
			remove(toolBar);  //remove the toolbar
			add(toolBar, position); //add the toolbar in the correct position
		}
	}

	/**Sets the position of the status bar. If there is no status bar, no action
		occurs.
	@param position The new position, one of the <code>BorderLayout</code> constants
		such as <code>BorderLayout.NORTH</code>.
	*/
	public void setStatusBarPosition(final String position)
	{
		if(statusBar!=null) //if we have a status bar
		{
				//G***verify the position is not BorderLayout.CENTER
			remove(statusBar);  //remove the status bar
			add(statusBar, position); //add the toolbar in the correct position
		}
	}
	
}
