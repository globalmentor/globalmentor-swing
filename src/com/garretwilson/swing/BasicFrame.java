/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import com.garretwilson.awt.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.java.*;
import com.globalmentor.log.Log;
import com.globalmentor.model.Modifiable;

import static com.globalmentor.util.prefs.PreferencesUtilities.*;

/**An extended frame that has extra features beyond those in <code>JFrame</code>.
<p>The default close operation in this class by default is
	<code>DISPOSE_ON_CLOSE</code>. This class maintains its own local default
	close operation setting, and sets the parent class default close operation to
	<code>DO_NOTHING_ON_CLOSE</code>. This allows the class listen to window
	events and perform consistent <code>canClose()</code> checks for all methods
	of closing. This is all handled transparently&mdash;once closing should occur,
	the local default close operation setting is honored as normal.</p>
<p>If the content pane implements <code>Modifiable</code>, the frame's
	<code>updateStatus()</code> method will be called when the content pane is
	modified.</p>
<p>This class can keep track of which component should get the focus by default,
	and will focus that component when the frame is initially shown.</p>
<p>The frame can store a preferences node to use for preference, or use the
	default preferences node for the frame class.</p>
<p>The class automatically remembers and restores its bounds by storing them
	in preferences.</p>
<p>The panel keeps a lazily-created manager that manages menu and tool actions.
	These actions will automatically be merged with whatever actions are provided
	by the content pane before creating a menu bar.</p>
<p>The frame does not actually update its title unless the title text is
	actually changing.</p>
<p>The class maintains a lazily-created property change listener that knows
	how to update the status when something has been modified.</p>
@author Garret Wilson
*/
public class BasicFrame extends JFrame implements DefaultFocusable, CanClosable, ActionManaged
{

		//the bounds preferences
	/**The preference for storing the horizontal position.*/
	protected final String BOUNDS_X_PREFERENCE=getPreferenceName(getClass(), "bounds.x");
	/**The preference for storing the vertical position.*/
	protected final String BOUNDS_Y_PREFERENCE=getPreferenceName(getClass(), "bounds.y");
	/**The preference for storing the width.*/
	protected final String BOUNDS_WIDTH_PREFERENCE=getPreferenceName(getClass(), "bounds.width");
	/**The preference for storing the height.*/
	protected final String BOUNDS_HEIGHT_PREFERENCE=getPreferenceName(getClass(), "bounds.height");
	/**The preference for storing the extended state.*/
	protected final String EXTENDED_STATE_PREFERENCE=getPreferenceName(getClass(), "extended.state");

	/**The preferences that should be used for this frame, or <code>null</code>
		if the default preferences for this class should be used.
	*/
	private Preferences preferences=null;

		/**@return The preferences that should be used for this frame, or the default
			preferences for this class if no preferences are specifically set.
		@exception SecurityException Thrown if a security manager is present and
			it denies <code>RuntimePermission("preferences")</code>.
		*/
		public Preferences getPreferences() throws SecurityException
		{
//TODO del; stay with the old method			return preferences!=null ? preferences: PreferencesUtilities.getUserNodeForClass(getClass());	//return the user preferences node for whatever class extends this one 
			return preferences!=null ? preferences: Preferences.userNodeForPackage(getClass());	//return the user preferences node for whatever class extends this one 
		}
		
		/**Sets the preferences to be used for this panel.
		@param preferences The preferences that should be used for this panel, or
			<code>null</code> if the default preferences for this class should be used
		*/
		public void setPreferences(final Preferences preferences)
		{
			this.preferences=preferences;	//store the preferences
		}

	/**The lazily-created manager of menu and tool actions.*/
	private ActionManager actionManager;

		/**@return The lazily-created manager of menu and tool actions.*/
		public ActionManager getActionManager()
		{
			if(actionManager==null)	//if we haven't yet created an action manager
			{
				actionManager=new ActionManager();	//create a new action manager
			}
			return actionManager;	//return the action manager
		}

	/**The action for closing the frame.*/
	private final Action closeAction;

		/**@return The action for closing the frame.*/
		public Action getCloseAction() {return closeAction;}

	/**The component that hsould get the default focus, or <code>null</code> if unknown.*/
	private Component defaultFocusComponent;

		/**@return The component that should get the default focus, or
			<code>null</code> if no component should get the default focus or it is
			unknown which component should get the default focus.
		*/
		public Component getDefaultFocusComponent() {return defaultFocusComponent;}
		
		/**Sets the component to get the focus by default.
			If this panel becomes a root focus traversal cycle, the default installed
			focus traversal policy will automatically allow this component to get
			the default focus.
		@param component The component to get the default focus.
		*/
		public void setDefaultFocusComponent(final Component component) {defaultFocusComponent=component;}

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
		public void setDefaultCloseOperation(final int operation) throws SecurityException
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

	/**The lazily-created object that listens for resource modifications and updates the status.*/
	private PropertyChangeListener modifiedUpdateStatusPropertyChangeListener=null;

		/**@return The lazily-created object that listens for resource modifications and updates the status.*/
		protected PropertyChangeListener getModifiedUpdateStatusPropertyChangeListener()
		{
			if(modifiedUpdateStatusPropertyChangeListener==null)	//if we haven't created a property change listener, yet
			{
				modifiedUpdateStatusPropertyChangeListener=new PropertyChangeListener()	//create a property chnage listener to listen for resource modifications
					{
						public void propertyChange(final PropertyChangeEvent propertyChangeEvent) //if the "modified" property changes in the explore panel
						{
							updateStatus();  //update the status of our actions
						}
					};
			}
			return modifiedUpdateStatusPropertyChangeListener;	//return the listener
		}

	/**Sets the title for this frame to the specified string.
	This version does not change the title unless the specified title is actually
	different than the current title.
	@param title The title to be displayed in the frame's border.
		A <code>null</code> value is treated as an empty string, "".
	@see JFrame#getTitle
	@see JFrame#setTitle
	*/
	public void setTitle(final String title)
	{
		if(!Objects.equals(getTitle(), title))	//if the title is really changing
			super.setTitle(title);	//actually update the title
	}
	
	/**Constructs a string appropriate for showing on the title of the frame.
	<p>This version returns the current title.</p>
	@return A title to display on the frame.
	*/
	protected String constructTitle()
	{
		return getTitle();	//return the current title
	}

	/**Sets the <code>contentPane</code> property. 
	This version installs a property listener to listen for the content pane's
		"modified" property being changed, if the content pane is
		<code>Modifiable</code>.
	@param contentPane the <code>contentPane</code> object for this frame
	@exception IllegalComponentStateException (a runtime
		exception) if the content pane parameter is <code>null</code>.
	@see JFrame#getContentPane
	@see Modifiable
	@see Modifiable#MODIFIED_PROPERTY
	@see #getModifiedPropertyChangeListener
	*/
	public void setContentPane(final Container contentPane)
	{
		final Container oldContentPane=getContentPane();	//get the current content pane
		if(oldContentPane instanceof Modifiable)	//if the old content pane is modifiable
		{
			oldContentPane.removePropertyChangeListener(Modifiable.MODIFIED_PROPERTY, getModifiedUpdateStatusPropertyChangeListener());	//remove the modified property change listener from the old content pane
		}
		super.setContentPane(contentPane);	//set the content pane normally
		setDefaultFocusComponent(contentPane);	//default to focusing on the content pane, if one was passed
		if(contentPane instanceof Modifiable)	//if the new content pane is modifiable
		{
			contentPane.addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY, getModifiedUpdateStatusPropertyChangeListener());	//add a listener to update the status when the "modified" property changes
		}
	}

	/**Default constructor.
	<p>Enables window events.</p>
	*/
	public BasicFrame()
	{
		this(true);	//construct the frame and initialize it	
	}

	/**Initialization constructor.
  <p>Enables window events.</p>
	@param initialize <code>true</code> if the frame should initialize itself by
		calling the initialization methods.
	*/
	public BasicFrame(final boolean initialize)
	{
		this("", initialize);	//construct the frame with no title
	}

	/**Title constructor.
  <p>Enables window events.</p>
	@param title The title of the frame, or <code>null</code> for no title (which
		will be convertd to a title of "").
	*/
	public BasicFrame(final String title)
	{
		this(title, true);	//construct and initialize the frame
	}

	/**Title constructor with optional initialization.
  <p>Enables window events.</p>
	@param title The title of the frame, or <code>null</code> for no title (which
		will be convertd to a title of "").
	@param initialize <code>true</code> if the frame should initialize itself by
		calling the initialization methods.
	*/
	public BasicFrame(final String title, final boolean initialize)
	{
		this(title, null, initialize);	//construct the class with the default content pane
	}

	/**Content pane and title constructor.
  <p>Enables window events.</p>
	@param contentPane The container to be used as the content pane, or
		<code>null</code> if the default content pane should be used.
	@param title The title of the frame, or <code>null</code> for no title (which
		will be convertd to a title of "").
	*/
	public BasicFrame(final String title, final Container contentPane)
	{
		this(title, contentPane, true);	//construct the frame with the given title and content pane, and initialize the frame
	}

	/**Content pane constructor with optional initialization.
  <p>Enables window events.</p>
	@param contentPane The container to be used as the content pane, or
		<code>null</code> if the default content pane should be used.
	@param initialize <code>true</code> if the frame should initialize itself by
		calling the initialization methods.
	*/
	public BasicFrame(final Container contentPane, final boolean initialize)
	{
		this("", contentPane, initialize);	//construct the frame with no title, initializing if requested
	}

	/**Content pane and title constructor with optional initialization.
  <p>Enables window events.</p>
	@param contentPane The container to be used as the content pane, or
		<code>null</code> if the default content pane should be used.
	@param title The title of the frame, or <code>null</code> for no title (which
		will be convertd to a title of "").
	@param initialize <code>true</code> if the frame should initialize itself by
		calling the initialization methods.
	*/
	public BasicFrame(final String title, final Container contentPane, final boolean initialize)
	{
		super(title);	//construct the parent class with this title, making sure we don't pass null (JFrame sometimes allows null titles, other times it converts them to the empty string)
		preferences=null;	//show that we should use the default preferences for this class
		actionManager=null;	//default to no action manager until one is asked for
		closeAction=new CloseAction();  //create the close action
		  //don't do anything automatically on close; we'll handle responding to close events
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	//tell the parent to set its default close operation TODO this implementation depends on the fact that the super class doesn't use the accessor methods---that's probably dangerous
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); //enable window events, so that we can respond to close events
		if(contentPane!=null)	//if we have a content pane
			setContentPane(contentPane); //set the given container as the content pane
		addComponentListener(new ComponentAdapter()	//listen for the window bounds changes and save or restore the bounds in the preferences
				{
					public void componentMoved(ComponentEvent e) {if(isVisible()) saveBoundsPreferences();}	//save the new bounds if we are visible
					public void componentResized(ComponentEvent e) {if(isVisible()) saveBoundsPreferences();}	//save the new bounds if we are visible
					public void componentShown(ComponentEvent e) {
						if(isVisible())
							requestDefaultFocusComponentFocus();	//TODO fix; put back in windowGainedFocus() and find out why it is never called
						if(isVisible()) restoreBoundsPreferences();}	//restore the new bounds if we are visible
				});
//TODO actually, this window state change needs to be fixed---it isn't working correctly
//TODO del		addWindowStateListener(new WindowStateListener()	//listen for the window state changing so that we can save it in the preferences
//TODO del				{
//TODO del public void windowStateChanged(WindowEvent e) {/*TODO fix saveStatePreferences();*/}	//save the new state
//TODO del				});
//TODO del		defaultFocusComponent=null;	//default to no default focus component
			//create and install a new layout focus traversal policy that will
			//automatically use the default focus component, if available
/*TODO fix
		setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()
				{
					public Component getDefaultComponent(final Container focusCycleRoot)	//if the default component is requested
					{
							//if we have a default focus component, return it; otherwise, use the value given by the parent traversal policy class
						return getDefaultFocusComponent()!=null ? getDefaultFocusComponent() : super.getDefaultComponent(focusCycleRoot);
					}
				});
*/
/*TODO fix; move from componentShown() to here and find out why this doesn't ever get called
		addWindowListener(new WindowAdapter() {	//TODO testing; tidy; comment
				private boolean gotFocus = false;
				public void windowGainedFocus(WindowEvent we) {
						// Once window gets focus, set initial focus
						if (!gotFocus) {
							gotFocus=requestDefaultFocusComponentFocus();	//TODO testing
						}
				}
		});
*/
		if(initialize)  //if we should initialize
		  initialize(); //initialize the frame
	}


	/**Initializes the frame. Should only be called once per instance.
	@see #initializeUI
	*/
	protected void initialize()
	{
		initializeActions(getActionManager()); //initialize actions
		initializeUI(); //initialize the user interface
		pack();	//set the initial size to its default
		updateStatus();  //update the actions
//TODO bring back		setSize(800, 600);	//default to 800X600; the window can be maximized after it's shown TODO determine the initial size based upon the resolution
//TODO fix		setExtendedState(MAXIMIZED_BOTH);	//maximize the frame TODO get this from preferences
//TODO transfer this to WindowUtilities, maybe		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
//TODO fix		WindowUtilities.maximize(this); //maximize the frame TODO remove this, as JDK 1.4 has a programmatic maximization
//TODO del; doesn't fix the problem		getContentPane().requestFocus();	//focus on the content pane
	}

	/**Initializes actions in the action manager.
		Any derived class that overrides this method should call this version.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
		setTitle(constructTitle());  //update the title
		ActionManager actionManager=getActionManager();	//get our action manager
		final Container contentPane=getContentPane();	//get the content pane
		if(contentPane instanceof ActionManaged)	//if the content pane has managed actions
		{
			actionManager=actionManager.merge(((ActionManaged)contentPane).getActionManager());	//merge with the the content pane's managed actions
		}
			//set up the menu bar
				//TODO put this in some convenience method, maybe---or maybe not, if the code is specific to frames
		final Iterator menuActionIterator=actionManager.getMenuActionIterator();	//get the iterator to top-level menu actions
		if(menuActionIterator.hasNext())	//if there are top-level menu actions
		{
			final JMenuBar menuBar=new JMenuBar();  //create a menu bar
			while(menuActionIterator.hasNext())	//while there are more actions
			{
				final Action action=(Action)menuActionIterator.next();	//get the next action
				final JMenuItem menuItem=createMenuItem(action, actionManager, true);	//create a menu item for this top-level menu action
				menuBar.add((JMenu)menuItem);	//add the menu (we know it's a menu, because we specified that this is a top-level menu action) to the menu bar
			}
			setJMenuBar(menuBar);	//set the menu bar
		}
  }

	/**Creates a menu item to represent the given action. If the given action
		manager contains child actions for the given action, a menu will be
		created and the child actions will be recursively added to the menu.
	@param action The action to be represented with a menu or menu item.
	@param actionManager The manager that contains any children actions for the
		given parent.
	@param isTopLevel Whether this action represents the top level in the manu
		hierarchy, and thus a menu should unconditionally be created rather than
		a single menu item.
	@return A complete menu or a menu item to represent the given action and any
		chilren. 
	*/
	protected JMenuItem createMenuItem(final Action action, final ActionManager actionManager, final boolean isTopLevel)
	{
		final Iterator menuActionIterator=actionManager.getMenuActionIterator(action);	//get the iterator to children, if any, of the parent action
		if(isTopLevel || menuActionIterator.hasNext())	//if the parent action has children, or if the action is a top-level action
		{
			final JMenu menu=new JMenu(action);	//create a new menu from the action
			Component lastComponent=null;	//keep track of the last component we added
			while(menuActionIterator.hasNext())	//while there are more actions
			{
				final Action childAction=(Action)menuActionIterator.next();	//get the next child action
				if(childAction instanceof ActionManager.SeparatorAction)		//if this is a separator action
				{
						//don't put two separators in a row, and don't put a separator as the first component 
					if(lastComponent!=null && !(lastComponent instanceof JSeparator))	//if this isn't the first component and it doesn't come before a separator
					{
						lastComponent=new JPopupMenu.Separator();	//create a menu separator
						menu.add(lastComponent);	//add the separator
					}				
				}
				else	//if this is a normal action
				{
					final JMenuItem childMenuItem=createMenuItem(childAction, actionManager, false);	//create a new menu item and/or child menu items for the action, specifying that this is not a top-level action
					lastComponent=menu.add(childMenuItem);	//add the child menu item
				}			
			}
			return menu;	//show that we created a complete menu to represent the action
		}
		else	//if there are no children actions
		{
			return createMenuItem(action);	//just create a normal menu item from the action
		}
	}

	/**The map of button groups keyed to action groups.*/
	private final Map actionGroupButtonGroupMap=new HashMap();

	/**Retrieves a button group for the given action group.
	 If no button group exists for the given action group, one is created.
	*/
	protected ButtonGroup getButtonGroup(final ActionGroup actionGroup)
	{
		ButtonGroup buttonGroup=(ButtonGroup)actionGroupButtonGroupMap.get(actionGroup);	//get a button group group for this action group
		if(buttonGroup==null)	//if no button group has been created
		{
			buttonGroup=new ButtonGroup();	//create a new button group
			actionGroupButtonGroupMap.put(actionGroup, buttonGroup);	//associate the button group with the action group
		}
		return buttonGroup;	//return the button group
	}
	
	/**Creates a single menu item&mdash;not a menu&mdash;from the given action.
	@param action The action for which a menu item should be created.
	@return A new menu item to represent the action.
	*/
	protected JMenuItem createMenuItem(final Action action)
	{
		final JMenuItem menuItem;
		if(action instanceof AbstractToggleAction)	//if this is a toggle action
		{
			final AbstractToggleAction toggleAction=(AbstractToggleAction)action;	//cast the action to a toggle action
			final ActionGroup actionGroup=toggleAction.getGroup();	//get the action's group, if any
			if(actionGroup!=null)	//if there is a group of mutually exclusive actions
			{
				menuItem=new JRadioButtonMenuItem(toggleAction);	//create a radio button menu item
				getButtonGroup(actionGroup).add(menuItem);	//add this menu item to the button group
			}
			else	//if there is no group
			{
				menuItem=new JCheckBoxMenuItem(toggleAction);	//create a checkbox menu item
			}
			menuItem.setSelected(toggleAction.isSelected());	//set the initial selected state of the menu item
				//create a property change listener to update the button in response to the action "selected" state changing
			final PropertyChangeListener selectedPropertyChangeListener=Buttons.createToggleActionSelectedPropertyChangeListener(menuItem);
			toggleAction.addPropertyChangeListener(selectedPropertyChangeListener);	//listen for the action "selected" state changing
		}
		else	//if we should create a normal menu item
		{
			menuItem=new JMenuItem(action);	//create a normal menu item from the action
		}
		return menuItem;	//return the menu item we created
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		setTitle(constructTitle());  //update the title
	}

	/**Requests that the default focus component should get the default.
	<p>If the component is a tab in a tabbed pane, that tab in the tabbed pane
		is selected.</p>
	<p>If the default focus comonent is itself <code>DefaultFocusable</code>, that
		component is asked to request focus for its default focus component, and
		so on.</p>
	@return <code>false</code> if the focus change request is guaranteed to
		fail; <code>true</code> if it is likely to succeed.
	@see Component#requestFocusInWindow
	*/
	public boolean requestDefaultFocusComponentFocus()	//TODO put this is some common class along with the version in BasicPanel
	{
		final Component defaultFocusComponent=getDefaultFocusComponent();	//get the default focus component
		if(defaultFocusComponent!=null)	//if there is a default focus component, make sure its parent tabs are selected if it's in a tabbed pane
		{
			TabbedPaneUtilities.setSelectedParentTabs(defaultFocusComponent);	//select the tabs of any parent tabbed panes
		}
		if(defaultFocusComponent instanceof DefaultFocusable	//if the component is itself default focusable
				&& ((DefaultFocusable)defaultFocusComponent).getDefaultFocusComponent()!=defaultFocusComponent)	//and the default focus component does not reference itself (which would create an endless loop)
		{
			return ((DefaultFocusable)defaultFocusComponent).requestDefaultFocusComponentFocus();	//pass the request on to the default focus component
		}
		else if(defaultFocusComponent!=null)	//if the default focus component doesn't itself know about default focus components, but there is a default focus component
		{
			return defaultFocusComponent.requestFocusInWindow();	//tell the default focus component to request the focus
		}
		else	//if there is no default focus component
		{
			return false;	//there was nothing to focus
		}
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

	/**Exits the application with no status.
	Convenience method which calls <code>exit(int)</code>.
	@see #exit(int)
	*/
	protected void exit()
	{
		exit(0);	//exit with no status
	}
	
	/**Exits the application with the given status.
	@param status The exit status.
	*/
	protected void exit(final int status)
	{
		System.exit(status);	//close the program with the given exit status
	}

	/**Determines whether the frame can close.
	<p>If the content pane is an instance of <code>CanClosable</code>, its
		<code>canClose()</code> method is called.</p>
	@return <code>true</code> if the frame can close.
	@see CanClosable#canClose
	*/
	public boolean canClose()
	{
		if(getContentPane() instanceof CanClosable)	//if the content pane knows how to ask about closing
		{
			return ((CanClosable)getContentPane()).canClose();	//ask if the content pane can be closed
		}
		else	//if the content pane is not an instance of CanClosable
		{
			return true;	//allow closing by default
		}
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

	/**Makes the frame visible or invisible.
		This version resets the bounds of the frame to the last known bounds as
		saved in the preferences if the window is being made visible. 
	@param newVisible <code>true</code> to make the component visible;
		<code>false</code> to make it invisible.
	*/
	public void setVisible(final boolean newVisible)
	{
		if(!isVisible() && newVisible)	//if the frame is becoming visible
		{
			restoreBoundsPreferences();	//restore the bounds (this will be done again after coming visible, because setting the bounds while hidden will not correctly set the window extended state)
		}
		super.setVisible(newVisible);	//set the visibility normally
	}

	/**Makes the component visible or invisible.
		This version resets the bounds of the frame to the last known bounds as
		saved in the preferences. 
	@param newVisible <code>true</code> to make the component visible;
		<code>false</code> to make it invisible.
	*/
/*TODO del when works
	public void setVisible(final boolean newVisible)
	{
		super.setVisible(newVisible);	//update the visible status normally
		if(newVisible)	//if the frame is now visible
		{
//TODO del			final Rectangle bounds=getBounds();	//get the current bounds
			final Preferences preferences=getPreferences();	//get the preferences
			final int x=preferences.getInt(BOUNDS_X_PREFERENCE, -1);	//get the stored bounds, using invalid dimensions for defaults
			final int y=preferences.getInt(BOUNDS_Y_PREFERENCE, -1);
			final int width=preferences.getInt(BOUNDS_WIDTH_PREFERENCE, -1);
			final int height=preferences.getInt(BOUNDS_HEIGHT_PREFERENCE, -1);
			if(x>=0 && y>=0 && width>=0 && height>=0)	//if we had valid bounds stored
			{
				setBounds(x, y, width, height);	//restore the bounds we had saved in preferences
			}
			else	//if no bounds are stored in preferences
			{
				setSize(800, 600);	//set a default size, which will be saved
				WindowUtilities.center(this);	//center the window, which will save the new location 
			}
			validate();	//make sure the components are all laid out correctly after was changed the size
		}
	}
*/

	/**Saves the bounds in the preferences.*/
	protected void saveBoundsPreferences()
	{
		try
		{
			final Preferences preferences=getPreferences();	//get the preferences
			final Rectangle bounds=getBounds();	//get the current bounds
			final int extendedState=getExtendedState();	//get the current extended state
			preferences.putInt(EXTENDED_STATE_PREFERENCE, extendedState);	//store the extended state
			if(extendedState!=MAXIMIZED_HORIZ && extendedState!=MAXIMIZED_BOTH)	//if we aren't maximized horizontally
			{
				preferences.putInt(BOUNDS_X_PREFERENCE, bounds.x);	//save the horizontal bounds
				preferences.putInt(BOUNDS_WIDTH_PREFERENCE, bounds.width);
			}
			if(extendedState!=MAXIMIZED_VERT && extendedState!=MAXIMIZED_BOTH)	//if we aren't maximized vertically
			{
				preferences.putInt(BOUNDS_Y_PREFERENCE, bounds.y);	//save the vertical bounds
				preferences.putInt(BOUNDS_HEIGHT_PREFERENCE, bounds.height);
			}
		}
		catch(SecurityException securityException)	//if we can't access preferences
		{
			Log.warn(securityException);	//warn of the security problem			
		}
	}

	/**Restores the bounds from the preferences.*/
	public void restoreBoundsPreferences()
	{
		boolean useDefault=true;	//we'll use the defaults if we can't load the saved values
		try
		{
			final Preferences preferences=getPreferences();	//get the preferences
			final int extendedState=preferences.getInt(EXTENDED_STATE_PREFERENCE, NORMAL);	//get the stored extended state
			final int x=preferences.getInt(BOUNDS_X_PREFERENCE, 0);	//get the stored bounds, using invalid dimensions for defaults
			final int y=preferences.getInt(BOUNDS_Y_PREFERENCE, 0);
			final int width=preferences.getInt(BOUNDS_WIDTH_PREFERENCE, -1);
			final int height=preferences.getInt(BOUNDS_HEIGHT_PREFERENCE, -1);
//TODO maybe the stuff gets changed around here---the height and width seem to be changed, but the x and y seem to be lost
//TODO there's some sort of timing issue: when debugging, the things get changed correctly, but in real time often the x and y coordinates get set before the extended state is updated to maximized
			useDefault=width<0 || height<0;	//use the default if we didn't get valid dimensions
			if(!useDefault)	//if we had valid dimensions stored
			{
				setBounds(x, y, width, height);	//restore the bounds we had saved in preferences
				setExtendedState(extendedState);	//update the extended state to match that stored
			}
		}
		catch(SecurityException securityException)	//if we can't access preferences
		{
			Log.warn(securityException);	//warn of the security problem			
		}
		if(useDefault)	//if no bounds are stored in preferences
		{
			setSize(800, 600);	//set a default size, which will be saved
			WindowUtilities.center(this);	//center the window, which will save the new location
			setExtendedState(MAXIMIZED_BOTH);	//maximize the window 
		}
		validate();	//make sure the components are all laid out correctly after was changed the size
	}

	public void setExtendedState(int state)
	{
		super.setExtendedState(state);	//TODO testing	
	}

	/**Action for closing the frame.*/
	protected class CloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public CloseAction()
		{
			super("Close");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Close the window");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Close the window.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EXIT_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK)); //add the accelerator
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_EXIT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			close(); //close the frame
		}
	}

}
