package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;

/**A panel that can contain a toolbar and a status. The panel uses a basic grid
	bag layout with the border paradigm, and content can be added by a component
	being placed in the panel center.
<p>Accelerators will be added to the component for all actions added to the
	toolbar inside <code>initializeToolBar()</code>.</p> 
@see BasicGridBagLayout 
@author Garret Wilson
*/
public class ToolStatusPanel extends ContentPanel
{

  /**The toolbar, or <code>null</code> if there is no toolbar.*/
	private final BasicToolBar toolBar;

		/**@return The toolbar, or <code>null</code> if there is no toolbar.*/
		public BasicToolBar getToolBar() {return toolBar;}

		/**Sets the toolbar.
		@param newToolBar The new toolbar to use, or <code>null</code> if there should
			be no toolbar.
		*/
/*G***del if not needed
		protected void setToolBar(final JToolBar newToolBar)
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
			  	add(newToolBar, toolBarPosition);  //put the toolbar in the correct position
				}
			}
		}
*/

	/**The position of the toolbar, one of the <code>BorderLayout</code> constants
		such as <code>BorderLayout.NORTH</code>.
	*/
	private String toolBarPosition;

	/**The status bar, or <code>null</code> if there is no toolbar.*/
	private final StatusBar statusBar;

		/**@return The application status bar.*/
		public StatusBar getStatusBar() {return statusBar;}

		/**Sets the status bar.
		@param newStatusBar The new status bar to use, or <code>null</code> if there should
			be no status bar.
		*/
	/*G***del if not needed
		protected void setStatusBar(final StatusBar newStatusBar)
		{
			if(statusBar!=newStatusBar)	//if the status bar is really changing
			{
				if(statusBar!=null)	//if we currently have a status bar
				{
					remove(statusBar);	//remove the old status bar
				}
				statusBar=newStatusBar;	//save the toolbar
				if(newStatusBar!=null)	//if we were given a new status bar
				{
					add(newStatusBar, statusBarPosition);  //put the status bar in the correct position
				}
			}
		}
*/

	/**The position of the status bar, one of the <code>BorderLayout</code>
		constants such as <code>BorderLayout.NORTH</code>.
	*/
	protected String statusBarPosition;

	/**Default constructor.*/
	public ToolStatusPanel()
	{
		this(true, true); //default to having a toolbar and a status bar
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public ToolStatusPanel(final boolean hasToolBar, final boolean hasStatusBar)
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
	public ToolStatusPanel(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		this(null, hasToolBar, hasStatusBar, initialize);	//construct the panel with no content component
	}

	/**Application component constructor.
	@param applicationComponent The new component for the center of the panel.
	*/
	public ToolStatusPanel(final Component applicationComponent)
	{
		this(applicationComponent, true, true); //do the default construction with a toolbar and a status bar
	}

	/**Application component constructor that allows options to be set.
	@param applicationComponent The new component for the center of the panel.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public ToolStatusPanel(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar)
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
	public ToolStatusPanel(final Component applicationComponent, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(applicationComponent!=null ? applicationComponent : new BasicPanel(), false);  //construct the parent class without intializing TODO create a method to create a default center panel
		toolBarPosition=BorderLayout.NORTH;	//default to the toolbar in the north
		statusBarPosition=BorderLayout.SOUTH;	//default to the status bar in the south
		toolBar=hasToolBar ? createToolBar() : null;	//create a toolbar if we should have one
		statusBar=hasStatusBar ? createStatusBar() : null;	//create a status bar if we should have one
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything
	}

	/**Initializes the panel. Should only be called once per instance.*/
/*G***del if not needed
	public void initialize()	//TODO set a flag that will only allow initialization once per instance
	{
		super.initialize();	//do the default initialization
	}
*/

	/**Initializes the user interface.
	<p>After initializing the toolbar, accelerators are added for any actions
		added to the toolbar.</p>
	<p>Any derived class that overrides this method should call this version.</p>
	@see #initializeToolBar(JToolBar)
	@see #initializeStatusBar(StatusBar)
	*/
  protected void initializeUI()
  {
		super.initializeUI(); //do the default UI initialization
		final JToolBar toolbar=getToolBar();	//get the toolbar
		if(toolbar!=null) //if we have a toolbar
		{
			initializeToolBar(toolbar);  //initialize the toolbar
			add(toolBar, toolBarPosition);  //put the toolbar in the correct position
		}
		final StatusBar statusBar=getStatusBar();	//get the status bar
		if(statusBar!=null) //if we have a status bar
		{
			initializeStatusBar(statusBar);  //initialize the status bar
			add(statusBar, statusBarPosition);  //put the status bar in the correct position
		}
  }

	/**@return A new toolbar.*/
	protected BasicToolBar createToolBar()
	{
		return new ApplicationToolBar();	//create and return a default application toolbar
	}

	/**Initializes the toolbar components.
	<p>This version adds a toolbar component for each tool action in the action manager.</p>
	@param toolBar The toolbar to be initialized.
	@see BasicPanel#getActionManager()
	*/
	public void initializeToolBar(final JToolBar toolBar)
	{
		ToolBarUtilities.setupToolBar(toolBar, getActionManager());	//setup the toolbar from the actions in the action manager
	}

	/**@return A new status bar.*/
	protected StatusBar createStatusBar()
	{
		return new StatusBar();  //create and return the status bar
	}

	/**Initializes the status bar components.
	@param statusBar The status bar to be initialized.
	*/
	protected void initializeStatusBar(final StatusBar statusBar)
	{
	}

	/**Sets the position of the toolbar. If there is no toolbar, no action occurs.
	@param position The new position, one of the <code>BorderLayout</code> constants
		such as <code>BorderLayout.NORTH</code>.
	*/
	public void setToolBarPosition(final String position)
	{
		if(toolBarPosition!=position)	//if the position is really changing
		{
			//TODO verify the position is not BorderLayout.CENTER
			toolBarPosition=position;	//update the position
			if(toolBar!=null) //if we have a toolbar
			{
				remove(toolBar);  //remove the toolbar
				add(toolBar, toolBarPosition);  //add the toolbar in the correct position
			}
		}
	}

	/**Sets the position of the status bar. If there is no status bar, no action
		occurs.
	@param position The new position, one of the <code>BorderLayout</code> constants
		such as <code>BorderLayout.NORTH</code>.
	*/
	public void setStatusBarPosition(final String position)
	{
		if(statusBarPosition!=position)	//if the position is really changing
		{
			//TODO verify the position is not BorderLayout.CENTER
			statusBarPosition=position;	//update the position
			if(statusBar!=null) //if we have a status bar
			{
				remove(statusBar);  //remove the status bar
				add(statusBar, statusBarPosition);  //add the status bar in the correct position
			}
		}
	}
	
}
