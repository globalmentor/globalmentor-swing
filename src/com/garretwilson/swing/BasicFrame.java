package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.prefs.*;
import javax.swing.*;
import com.garretwilson.awt.*;
import com.garretwilson.lang.*;
import com.garretwilson.util.*;
import com.garretwilson.util.prefs.*;

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
<p>This class allows a default set of frame preferences to be accessed.</p> 
<p>The class automatically remembers and restores its bounds by storing them
	in preferences.</p>
<p>The frame does not actually update its title unless the title text is
	actually changing.</p>
<p>The class maintains a lazily-created property change listener that knows
	how to update the status when something has been modified.</p>
@author Garret Wilson
*/
public class BasicFrame extends JFrame implements DefaultFocusable, CanClosable
{

		//the bounds preferences
	/**The preference for storing the horizontal position.*/
	protected final String BOUNDS_X_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "bounds.x");
	/**The preference for storing the vertical position.*/
	protected final String BOUNDS_Y_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "bounds.y");
	/**The preference for storing the width.*/
	protected final String BOUNDS_WIDTH_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "bounds.width");
	/**The preference for storing the height.*/
	protected final String BOUNDS_HEIGHT_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "bounds.height");
	/**The preference for storing the extended state.*/
	protected final String EXTENDED_STATE_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "extended.state");

	/**@return The default user preferences for this frame.*/
	public Preferences getPreferences()
	{
		return Preferences.userNodeForPackage(getClass());	//return the user preferences node for whatever class extends this one 
	}

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
		if(!ObjectUtilities.equals(getTitle(), title))	//if the title is really changing
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
	@exception java.awt.IllegalComponentStateException (a runtime
		exception) if the content pane parameter is <code>null</code>
	@see JFrame#getContentPane
	@see Modifiable
	@see Modifiable#MODIFIED_PROPERTY_NAME
	@see #getModifiedPropertyChangeListener
	*/
	public void setContentPane(final Container contentPane)
	{
		final Container oldContentPane=getContentPane();	//get the current content pane
		if(oldContentPane instanceof Modifiable)	//if the old content pane is modifiable
		{
			oldContentPane.removePropertyChangeListener(Modifiable.MODIFIED_PROPERTY_NAME, getModifiedUpdateStatusPropertyChangeListener());	//remove the modified property change listener from the old content pane
		}
		super.setContentPane(contentPane);	//set the content pane normally
		if(contentPane instanceof Modifiable)	//if the new content pane is modifiable
		{
			contentPane.addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY_NAME, getModifiedUpdateStatusPropertyChangeListener());	//add a listener to update the status when the "modified" property changes
		}
	}

	/**Default constructor.
	<p>Enables window events.</p>
	*/
	public BasicFrame()
	{
		this("");	//construct the frame with no title	
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
		  //don't do anything automatically on close; we'll handle responding to close events
		super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	//tell the parent to set its default close operation G***this implementation depends on the fact that the super class doesn't use the accessor methods---that's probably dangerous
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); //enable window events, so that we can respond to close events
		if(contentPane!=null)	//if we have a content pane
			setContentPane(contentPane); //set the given container as the content pane
		setDefaultFocusComponent(getContentPane());	//default to focusing on the content pane, if one was passed
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
//G***del		addWindowStateListener(new WindowStateListener()	//listen for the window state changing so that we can save it in the preferences
//G***del				{
//G***del public void windowStateChanged(WindowEvent e) {/*G***fix saveStatePreferences();*/}	//save the new state
//G***del				});
//G***del		defaultFocusComponent=null;	//default to no default focus component
			//create and install a new layout focus traversal policy that will
			//automatically use the default focus component, if available
/*G***fix
		setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()
				{
					public Component getDefaultComponent(final Container focusCycleRoot)	//if the default component is requested
					{
							//if we have a default focus component, return it; otherwise, use the value given by the parent traversal policy class
						return getDefaultFocusComponent()!=null ? getDefaultFocusComponent() : super.getDefaultComponent(focusCycleRoot);
					}
				});
*/
/*G***fix; move from componentShown() to here and find out why this doesn't ever get called
		addWindowListener(new WindowAdapter() {	//G***testing; tidy; comment
				private boolean gotFocus = false;
				public void windowGainedFocus(WindowEvent we) {
						// Once window gets focus, set initial focus
						if (!gotFocus) {
							gotFocus=requestDefaultFocusComponentFocus();	//G***testing
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
		initializeUI(); //initialize the user interface
		pack();	//set the initial size to its default
		updateStatus();  //update the actions
//G***bring back		setSize(800, 600);	//default to 800X600; the window can be maximized after it's shown G***determine the initial size based upon the resolution
//G***fix		setExtendedState(MAXIMIZED_BOTH);	//maximize the frame G***get this from preferences
//G***transfer this to WindowUtilities, maybe		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
//G***fix		WindowUtilities.maximize(this); //maximize the frame G***remove this, as JDK 1.4 has a programmatic maximization
//G***del; doesn't fix the problem		getContentPane().requestFocus();	//focus on the content pane
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
		setTitle(constructTitle());  //update the title
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
/*G***del when works
	public void setVisible(final boolean newVisible)
	{
		super.setVisible(newVisible);	//update the visible status normally
		if(newVisible)	//if the frame is now visible
		{
//G***del			final Rectangle bounds=getBounds();	//get the current bounds
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

	/**Restores the bounds from the preferences.*/
	public void restoreBoundsPreferences()
	{
		final Preferences preferences=getPreferences();	//get the preferences
		final int extendedState=preferences.getInt(EXTENDED_STATE_PREFERENCE, NORMAL);	//get the stored extended state
		final int x=preferences.getInt(BOUNDS_X_PREFERENCE, 0);	//get the stored bounds, using invalid dimensions for defaults
		final int y=preferences.getInt(BOUNDS_Y_PREFERENCE, 0);
		final int width=preferences.getInt(BOUNDS_WIDTH_PREFERENCE, -1);
		final int height=preferences.getInt(BOUNDS_HEIGHT_PREFERENCE, -1);

//TODO maybe the stuff gets changed around here---the height and width seem to be changed, but the x and y seem to be lost
//G***there's some sort of timing issue: when debugging, the things get changed correctly, but in real time often the x and y coordinates get set before the extended state is updated to maximized

		if(width>=0 && height>=0)	//if we had valid dimensions stored
		{
			setBounds(x, y, width, height);	//restore the bounds we had saved in preferences
			setExtendedState(extendedState);	//update the extended state to match that stored
		}
		else	//if no bounds are stored in preferences
		{
			setSize(800, 600);	//set a default size, which will be saved
			WindowUtilities.center(this);	//center the window, which will save the new location
			setExtendedState(MAXIMIZED_BOTH);	//maximize the window 
		}
		validate();	//make sure the components are all laid out correctly after was changed the size
	}

	public void setExtendedState(int state)
	{
		super.setExtendedState(state);	//G***testing	
	}

}
