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

package com.globalmentor.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.globalmentor.awt.*;
import com.globalmentor.java.*;
import com.globalmentor.java.Objects;
import com.globalmentor.model.Verifiable;
import com.globalmentor.swing.event.*;

import static com.globalmentor.awt.Containers.*;
import static com.globalmentor.java.Arrays.*;

/**
 * An extended panel that has extra features beyond those in {@link JPanel}.
 * <p>
 * The panel stores properties and fires property change events when a property is modified.
 * </p>
 * <p>
 * The panel is verifiable, and automatically verifies all child components that are likewise verifiable.
 * </p>
 * <p>
 * The panel can store a preferences node to use for preference, or use the default preferences node for the panel class.
 * </p>
 * <p>
 * The panel keeps a lazily-created manager that manages menu and tool actions.
 * </p>
 * <p>
 * The panel keeps track of its current user mode and view of data.
 * </p>
 * <p>
 * The panel can indicate whether it can close.
 * </p>
 * <p>
 * The panel can recognize when it is embedded in a <code>JOptionPane</code> and can set certain option pane values accordingly.
 * </p>
 * <p>
 * The panel can keep track of a title, and if the panel has a titled border, it will automatically update the border's title when the title changes.
 * </p>
 * <p>
 * The panel can keep track of which child component should get the default focus. An extended focus traversal policy is installed so that, if this panel
 * because a root focus traversal cycle, the correct default focus component will be selected. This implementation recognizes tabbed panes and automatically
 * delegates to the selected tab if the tabbed pane has been specified as the default focus component.
 * </p>
 * <p>
 * The panel can create default listeners, such as <code>ActionListener</code> and <code>DocumentListener</code>, that do nothing but update the status.
 * </p>
 * <p>
 * The panel is scrollable, and by default takes care of its own width and/or height rather than allowing any parent scroll width and height. This behavior can
 * be modified.
 * </p>
 * <p>
 * When this panel is enabled or disabled, it updates its status through <code>updateStatus()</code>.
 * </p>
 * <p>
 * The panel keeps an event listener list for convenient adding and removal of specific event listener types.
 * </p>
 * <p>
 * Bound properties:
 * </p>
 * <dl>
 * <dt>BasicPanel{@link #ICON_PROPERTY} (<code>Icon</code>)</dt>
 * <dd>Indicates the icon has been changed.</dd>
 * <dt>BasicPanel{@link #TITLE_PROPERTY} (<code>String</code>)</dt>
 * <dd>Indicates the title has been changed.</dd>
 * <dt>BasicPanel{@link #USER_MODE_PROPERTY} (<code>Integer</code>)</dt>
 * <dd>Indicates the user mode has been changed.</dd>
 * </dl>
 * @author Garret Wilson
 * @see Container#setFocusCycleRoot(boolean)
 * @see PropertyChangeListener
 * @see JOptionPane
 * @see GridBagLayout
 */
public class BasicPanel extends JPanel implements Scrollable, CanClosable, DefaultFocusable, Verifiable, ActionManaged {

	/** The name of the bound icon property. */
	public static final String ICON_PROPERTY = BasicPanel.class.getName() + Java.PACKAGE_SEPARATOR + "icon";
	/** The name of the bound title property. */
	public static final String TITLE_PROPERTY = BasicPanel.class.getName() + Java.PACKAGE_SEPARATOR + "title"; //TODO maybe later move this to a titleable interface
	/** The name of the bound user mode property. */
	public static final String USER_MODE_PROPERTY = BasicPanel.class.getName() + Java.PACKAGE_SEPARATOR + "userMode";

	/**
	 * The preferences that should be used for this panel, or <code>null</code> if the default preferences for this class should be used.
	 */
	private Preferences preferences;

	/**
	 * @return The preferences that should be used for this panel, or the default preferences for this class if no preferences are specifically set.
	 * @throws SecurityException Thrown if a security manager is present and it denies <code>RuntimePermission("preferences")</code>.
	 */
	public Preferences getPreferences() throws SecurityException {
		return preferences != null ? preferences : Preferences.userNodeForPackage(getClass()); //return the user preferences node for whatever class extends this one 
	}

	/**
	 * Sets the preferences to be used for this panel.
	 * @param preferences The preferences that should be used for this panel, or <code>null</code> if the default preferences for this class should be used
	 */
	public void setPreferences(final Preferences preferences) {
		this.preferences = preferences; //store the preferences
	}

	/** The map of properties. */
	private final Map<Object, Object> propertyMap = new HashMap<Object, Object>();

	/**
	 * Gets a property of the panel.
	 * @param key The key to the property.
	 * @return The value of the panel's property, or <code>null</code> if that property does not exist.
	 */
	public Object getProperty(final Object key) {
		return propertyMap.get(key); //return the property from the property map
	}

	/**
	 * Sets the value of a panel property, and fires a property changed event if the key is a string. If the property represented by the key already exists, it
	 * will be replaced.
	 * @param key The non-<code>null</code> property key.
	 * @param value The property value.
	 * @return The old property value associated with the key, or <code>null</code> if no value was associated with the key previously.
	 * @see PropertyChangeEvent
	 */
	public Object setProperty(final Object key, final Object value) {
		final Object oldValue = propertyMap.put(key, value); //put the value in the map keyed to the key and save the old value
		if(key instanceof String) { //if they key was a string					
			firePropertyChange((String)key, oldValue, value); //show that the property value has changed
		}
		return oldValue; //return the old property value, if there was one
	}

	/**
	 * Removes a property of the panel. If the property represented by the key does not exist, no action is taken.
	 * @param key The non-<code>null</code> property key.
	 * @return The removed property value, or <code>null</code> if there was no property.
	 */
	public Object removeProperty(final Object key) {
		return propertyMap.remove(key); //remove and return the property value keyed to the key
	}

	/** The lazily-created manager of menu and tool actions. */
	private ActionManager actionManager;

	/** @return The lazily-created manager of menu and tool actions. */
	public ActionManager getActionManager() {
		if(actionManager == null) { //if we haven't yet created an action manager
			actionManager = new ActionManager(); //create a new action manager
		}
		return actionManager; //return the action manager
	}

	/** The title of the panel, or <code>null</code> if there is no title. */
	private String title = null;

	/** @return The title of the panel, or <code>null</code> if there is no title. */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the panel. If the panel border is a <code>TitledBorder</code>, its title is updated. This is a bound property.
	 * @param newTitle The new title of the panel, or <code>null</code> for no title.
	 */
	public void setTitle(final String newTitle) {
		final String oldTitle = title; //get the old title value
		final Border border = getBorder(); //get our current border
		if(border instanceof TitledBorder) { //if the border is a titled border
			final TitledBorder titledBorder = (TitledBorder)border; //cast the border to a titled border
			if(!Objects.equals(titledBorder.getTitle(), newTitle)) { //if the new title is different than the one currently on the border
				titledBorder.setTitle(newTitle); //update the title on the border
			}
		}
		if(!Objects.equals(oldTitle, newTitle)) { //if the value is really changing
			title = newTitle; //update the value					
			firePropertyChange(TITLE_PROPERTY, oldTitle, newTitle); //show that the property has changed
		}
	}

	/** The icon of the panel, or <code>null</code> if there is no icon. */
	private Icon icon = null;

	/** @return The icon of the panel, or <code>null</code> if there is no icon. */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Sets the icon of the panel. This is a bound property.
	 * @param newIcon The new icon of the panel, or <code>null</code> for no icon.
	 */
	public void setIcon(final Icon newIcon) {
		final Icon oldIcon = icon; //get the old title value
		if(oldIcon != newIcon) { //if the value is really changing
			icon = newIcon; //update the value					
			firePropertyChange(ICON_PROPERTY, oldIcon, newIcon); //show that the property has changed
		}
	}

	/** The component that should get the default focus, or <code>null</code> if unknown. */
	private Component defaultFocusComponent;

	/**
	 * @return The component that should get the default focus, or <code>null</code> if no component should get the default focus or it is unknown which component
	 *         should get the default focus.
	 */
	public Component getDefaultFocusComponent() {
		return defaultFocusComponent;
	}

	/**
	 * Sets the component to get the focus by default. If this panel becomes a root focus traversal cycle, the default installed focus traversal policy will
	 * automatically allow this component to get the default focus.
	 * @param component The component to get the default focus.
	 */
	public void setDefaultFocusComponent(final Component component) {
		defaultFocusComponent = component;
	}

	/** The mode in which the user can best view the contents of the panel. */
	public static final int VIEW_MODE = 1 << 0;
	/** The mode in which the user can best modify the contents of the panel. */
	public static final int EDIT_MODE = 1 << 1;
	/** The mode in which the user can interact with the contents of the panel. */
	public static final int INTERACT_MODE = 1 << 2;

	/** The mode of interaction with the user, such as <code>EDIT_MODE</code>. */
	private int userMode;

	/** @return The mode of interaction with the user, such as <code>EDIT_MODE</code>. */
	public int getUserMode() {
		return userMode;
	}

	/**
	 * Sets the mode of user interaction.
	 * @param newUserMode The mode of interaction with the user, such as <code>EDIT_MODE</code>.
	 */
	public void setUserMode(final int newUserMode) {
		final int oldUserMode = userMode; //get the old value
		if(oldUserMode != newUserMode) { //if the value is really changing
			userMode = newUserMode; //update the value					
			firePropertyChange(USER_MODE_PROPERTY, new Integer(oldUserMode), new Integer(newUserMode)); //show that the property has changed
		}
	}

	/**
	 * Sets whether or not this component is enabled. This implementation enables calls <code>updateStatus()</code> if the enabled status changes.
	 * @param enabled <code>true</code> if this component should be enabled, <code>false</code> otherwise.
	 * @see #updateStatus()
	 */
	public void setEnabled(final boolean enabled) {
		final boolean oldEnabled = isEnabled(); //see whether we're currently enabled
		super.setEnabled(enabled); //do the default enabling
		if(oldEnabled != enabled) { //if the enabled state changed
			updateStatus(); //update the status
		}
	}

	/** The lazily-created list of event listeners. */
	private EventListenerList eventListenerList = null;

	/** @return The lazily-created list of event listeners. */
	private EventListenerList getEventListenerList() {
		if(eventListenerList == null) { //if there is yet no event listener list
			eventListenerList = new EventListenerList(); //create a new event listener list
		}
		return eventListenerList; //return the event listener list
	}

	/**
	 * Adds the listener as a listener of the specified type.
	 * @param <T> The type of the event listener.
	 * @param eventListenerType The type of the listener to be added.
	 * @param eventListener the listener to be added
	 */
	protected <T extends EventListener> void addEventListener(final Class<T> eventListenerType, T eventListener) {
		getEventListenerList().add(eventListenerType, eventListener); //add the listener to the list of event listeners
	}

	/**
	 * Removes the listener as a listener of the specified type.
	 * @param <T> The type of the event listener.
	 * @param eventListenerType the type of the listener to be removed.
	 * @param eventListener the listener to be removed.
	 */
	public <T extends EventListener> void removeEventListener(final Class<T> eventListenerType, T eventListener) {
		getEventListenerList().remove(eventListenerType, eventListener); //remove the listener from the list of event listeners
	}

	/**
	 * @param <T> The type of the event listener.
	 * @param eventListenerType the type of the listener to be removed.
	 * @return An array of all the listeners of the given type.
	 * @throws ClassCastException if the supplied class is not assignable to <code>EventListener</code>.
	 */
	protected <T extends EventListener> T[] getEventListeners(final Class<T> eventListenerType) {
		if(eventListenerList != null) { //if we have a list of event listeners
			return eventListenerList.getListeners(eventListenerType); //ask the list of event listeners to send back all event listeners of the given type
		} else { //if we have no list of event listeners
			return createArray(eventListenerType, 0); //send back an empty array
		}
	}

	/**
	 * Default constructor that uses a <code>FlowLayout</code>.
	 * @see FlowLayout
	 */
	public BasicPanel() {
		this(true); //initialize the panel
	}

	/**
	 * Constructor with optional initialization that uses a <code>FlowLayout</code>.
	 * @param initialize <code>true</code> if the panel should initialize itself by calling the initialization methods.
	 * @see FlowLayout
	 */
	public BasicPanel(final boolean initialize) {
		this(new FlowLayout(), initialize); //construct the panel with a flow layout by default
	}

	/**
	 * Layout constructor.
	 * @param layout The layout manager to use.
	 */
	public BasicPanel(final LayoutManager layout) {
		this(layout, true); //construct the class with the layout, initializing the panel
	}

	/**
	 * Layout constructor with optional initialization.
	 * @param layout The layout manager to use.
	 * @param initialize <code>true</code> if the panel should initialize itself by calling the initialization methods.
	 */
	public BasicPanel(final LayoutManager layout, final boolean initialize) {
		super(layout, false); //construct the parent class but don't initialize
		preferences = null; //show that we should use the default preferences for this class
		actionManager = null; //default to no action manager until one is asked for
		userMode = VIEW_MODE; //default to viewing the data
		defaultFocusComponent = null; //default to no default focus component
		//create and install a new layout focus traversal policy that will
		//automatically use the default focus component, if available
		setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {

			public Component getDefaultComponent(final Container focusCycleRoot) { //if the default component is requested
				Component defaultFocusComponent = getDefaultFocusComponent(); //see if we have a default focus component
				/*TODO fix
				if(defaultFocusComponent instanceof JTabbedPane) {	//if the default focus component is a tabbed pane	//TODO check; doesn't seem to work
					final Component selectedTab=((JTabbedPane)defaultFocusComponent).getSelectedComponent();	//get the selected tab
					if(selectedTab!=null) {	//if there is a selected tab
						defaultFocusComponent=selectedTab instanceof DefaultFocusable	//if the tab component knows about default focus components
								? ((DefaultFocusable)selectedTab).getDefaultFocusComponent()	//ask the tab for a default focus component
								: selectedTab;	//otherwise, use the selected tab as the default focus component
					}
				}
				*/
				//if we have a default focus component, return it; otherwise, use the value given by the parent traversal policy class
				return defaultFocusComponent != null ? defaultFocusComponent : super.getDefaultComponent(focusCycleRoot);
			}
		});
		if(initialize) //if we should initialize
			initialize(); //initialize the panel
	}

	/**
	 * Initializes the panel. Should only be called once per instance.
	 * @see #initializeUI
	 */
	public void initialize() { //TODO set a flag that will only allow initialization once per instance
		initializeActions(getActionManager()); //initialize actions
		initializeUI(); //initialize the user interface
		initializeData(); //initialize the data
		updateStatus(); //update the actions
	}

	/**
	 * Initializes actions in the action manager. Any derived class that overrides this method should call this version.
	 * @param actionManager The implementation that manages actions.
	 */
	protected void initializeActions(final ActionManager actionManager) {
	}

	/**
	 * Initializes the user interface. Any derived class that overrides this method should call this version.
	 */
	protected void initializeUI() {
	}

	/**
	 * Initializes the data. Any derived class that overrides this method should call this version.
	 */
	protected void initializeData() {
	}

	/** Updates the states of the user interface, including enabled/disabled status, proxied actions, etc. */
	public void updateStatus() {
	}

	/**
	 * Requests that the default focus component should get the default.
	 * <p>
	 * If the component is a tab in a tabbed pane, that tab in the tabbed pane is selected.
	 * </p>
	 * <p>
	 * If the default focus comonent is itself <code>DefaultFocusable</code>, that component is asked to request focus for its default focus component, and so on.
	 * </p>
	 * @return <code>false</code> if the focus change request is guaranteed to fail; <code>true</code> if it is likely to succeed.
	 * @see Component#requestFocusInWindow
	 */
	public boolean requestDefaultFocusComponentFocus() {
		final Component defaultFocusComponent = getDefaultFocusComponent(); //get the default focus component
		if(defaultFocusComponent != null) { //if there is a default focus component, make sure its parent tabs are selected if it's in a tabbed pane
			TabbedPaneUtilities.setSelectedParentTabs(defaultFocusComponent); //select the tabs of any parent tabbed panes
		}
		if(defaultFocusComponent instanceof DefaultFocusable //if the component is itself default focusable
				&& ((DefaultFocusable)defaultFocusComponent).getDefaultFocusComponent() != defaultFocusComponent) { //and the default focus component does not reference itself (which would create an endless loop)
			return ((DefaultFocusable)defaultFocusComponent).requestDefaultFocusComponentFocus(); //pass the request on to the default focus component
		} else if(defaultFocusComponent != null) { //if the default focus component doesn't itself know about default focus components, but there is a default focus component
			return defaultFocusComponent.requestFocusInWindow(); //tell the default focus component to request the focus
		} else { //if there is no default focus component
			return false; //there was nothing to focus
		}
	}

	/** @return <code>true</code> if the panel can close. */
	public boolean canClose() {
		return true; //default to always allowing closing
	}

	/**
	 * Verifies the component.
	 * <p>
	 * This version verifies all desdendant components that implement <code>Verifiable</code>, and returns <code>true</code> if no child component returns
	 * <code>false</code>.
	 * </p>
	 * @return <code>true</code> if the component contents are valid, <code>false</code> if not.
	 */
	public boolean verify() {
		return verifyDescendants(this); //verify all descendant components 
	}

	/** @return The component that should get the initial focus. */
	//TODO fix	public Component getInitialFocusComponent() {return labelTextField;}

	/**
	 * @return The <code>JOptionPane</code> in which this panel is embedded, or <code>null</code> if this panel is not embedded in a <code>JOptionPane</code>.
	 */
	protected JOptionPane getParentOptionPane() {
		Container parent = getParent(); //get the parent container
		while(parent != null && !(parent instanceof JOptionPane)) { //while we're still getting parents, but we haven't found an option pane
			parent = parent.getParent(); //get the parent's parent
		}
		return (JOptionPane)parent; //return the JOptionPane parent, or null if there was no JOptionPane parent
	}

	/**
	 * Sets the value property of the parent container <code>JOptionPane</code>. If this panel is not embedded in a <code>JOptionPane</code>, no action occurs.
	 * <p>
	 * For example, setting a value of <code>new Integer(JOptionPane.OK_OPTION)</code> will close the option pane and return that value.
	 * </p>
	 * @param newValue The chosen value.
	 * @see JOptionPane#setValue
	 * @see JOptionPane#getValue()
	 */
	public void setOptionPaneValue(final Object newValue) {
		final JOptionPane optionPane = getParentOptionPane(); //get the option pane in which we're embedded
		if(optionPane != null) { //if we're embedded in an option pane
			optionPane.setValue(newValue); //set the value of the option pane
		}
	}

	/**
	 * Returns the value the user has selected in the parent container <code>JOptionPane</code>. <code>UNINITIALIZED_VALUE</code> implies the user has not yet
	 * made a choice, <code>null</code> means the user closed the window with out choosing anything or this panel is not embedded in a <code>JOptionPane</code>.
	 * Otherwise the returned value should be one of the options defined in <code>JOptionPane</code>.
	 * @return the <code>Object</code> chosen by the user, <code>UNINITIALIZED_VALUE</code> if the user has not yet made a choice, or <code>null</code> if the
	 *         user closed the window without making a choice or this panel is not embedded in a <code>JOptionPane</code>.
	 * @see JOptionPane#getValue
	 * @see JOptionPane#setValue
	 */
	public Object getOptionPaneValue() {
		final JOptionPane optionPane = getParentOptionPane(); //get the option pane in which we're embedded
		return optionPane != null ? optionPane.getValue() : null; //return the value property of the option pane, or null if we are not embedded in a JOptionPane
	}

	/**
	 * @return A list selection listener that, when the list selection changes, updates the status.
	 * @see #updateStatus
	 */
	public ListSelectionListener createUpdateStatusListSelectionListener() {
		return new ListSelectionListener() { //create a new list selection listener that will do nothing but update the status

			public void valueChanged(final ListSelectionEvent listSelectionEvent) {
				updateStatus();
			} //if the list selection changes, update the status
		};
	}

	/**
	 * @return A property change listener that, when any property changes, updates the modified status to <code>true</code>.
	 * @see #setModified
	 */
	/*TODO bring back
		public PropertyChangeListener createModifyPropertyChangeListener()
		{
			return new PropertyChangeListener() {	//create a new property change listener that will do nothing but set modified to true
						public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {setModified(true);}	//if a property is modified, show that we've been modified
					};
		}
	*/

	/**
	 * @return A property change listener that, when a property chnages, updates the status.
	 * @see #updateStatus
	 */
	public PropertyChangeListener createUpdateStatusPropertyChangeListener() {
		return new PropertyChangeListener() { //create a new property change listener that will do nothing but update the status

			public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
				updateStatus();
			} //if a property is modified, update the status
		};
	}

	/**
	 * @return An action listener that, when an action occurs, updates the status.
	 * @see #updateStatus
	 */
	/*TODO bring back; or maybe we don't need, now that we have createUpdateStatusItemListener()
		public ActionListener createUpdateStatusActionListener()
		{
			return new ActionListener() {	//create a new action listener that will do nothing but update the status
						public void actionPerformed(final ActionEvent actionEvent) {updateStatus();}	//if the action occurs, update the status
					};
		}
	*/

	/**
	 * @return A document listener that, when a document is modified, updates the status.
	 * @see #updateStatus
	 */
	public DocumentListener createUpdateStatusDocumentListener() {
		return new DocumentModifyAdapter() { //create a new document listener that will do nothing but update the status

			public void modifyUpdate(final DocumentEvent documentEvent) {
				updateStatus();
			} //if the document is modified, update the status
		};
	}

	/**
	 * @return An item listener that, when an item state changes, updates the status.
	 * @see #updateStatus
	 */
	public ItemListener createUpdateStatusItemListener() {
		return new ItemListener() { //create a new item listener that will do nothing but update the status

			public void itemStateChanged(final ItemEvent itemEvent) {
				updateStatus();
			} //if an item state changes, update the status
		};
	}

	/**
	 * @return A list data listener that, when a list model is modified, updates the status.
	 * @see #updateStatus
	 */
	public ListDataListener createUpdateStatusListDataListener() {
		return new ListDataListener() { //create a new list data listener that will do nothing but update the status in response to changes

			public void intervalAdded(final ListDataEvent listDataEvent) {
				updateStatus();
			}

			public void intervalRemoved(final ListDataEvent listDataEvent) {
				updateStatus();
			}

			public void contentsChanged(final ListDataEvent listDataEvent) {
				updateStatus();
			}
		};
	}

	//Scrollable methods

	/**
	 * Returns the preferred size of the viewport for a view component.
	 * <p>
	 * This version simply returns the component's preferred size.
	 * </p>
	 * @return The preferred panel size when scrolling.
	 * @see Panel#getPreferredSize
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize(); //return the default preferred size
	}

	/**
	 * Determines the increment for unit scrolling.
	 * @param visibleRect The view area visible within the viewport.
	 * @param orientation The orientation, either <code>SwingConstants.VERTICAL</code> or <code>SwingConstants.HORIZONTAL</code>.
	 * @param direction Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The unit increment for scrolling in the specified direction.
	 */
	public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
		switch(orientation) { //see on which axis we're being scrolled
			case SwingConstants.VERTICAL:
				return visibleRect.height / 10;
			case SwingConstants.HORIZONTAL:
				return visibleRect.width / 10;
			default:
				throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	/**
	 * Determines the increment for block scrolling.
	 * @param visibleRect The view area visible within the viewport.
	 * @param orientation The orientation, either <code>SwingConstants.VERTICAL</code> or <code>SwingConstants.HORIZONTAL</code>.
	 * @param direction Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The block increment for scrolling in the specified direction.
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch(orientation) { //see on which axis we're being scrolled
			case SwingConstants.VERTICAL:
				return visibleRect.height;
			case SwingConstants.HORIZONTAL:
				return visibleRect.width;
			default:
				throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	/** Whether a viewport should always force the width of this panel to match its own. */
	private boolean tracksViewportWidth = false;

	/**
	 * Sets whether a viewport should always force the width of this panel to match its own width, unless the panel at its smallest could not fit inside the
	 * viewport.
	 * @param horizontallySelfManaged <code>true</code> if the panel should <em>not</em> be scrolled horizontally by any parent viewport, allowing this panel to
	 *          manage its own horizontal scrolling and/or wrapping.
	 */
	public void setTracksViewportWidth(final boolean horizontallySelfManaged) {
		tracksViewportWidth = horizontallySelfManaged;
	}

	/**
	 * Returns whether a viewport should always force the width of this panel to match the width of the viewport.
	 * <p>
	 * This implementation defaults to <code>false</code>, allowing the viewport to scroll horizontally as needed.
	 * </p>
	 * @return <code>true</code> if a viewport should force the panel's width to match its own width, allowing this panel to manage its own horizontal scrolling
	 *         and/or wrapping.
	 * @see #setTracksViewportHeight
	 */
	public boolean getScrollableTracksViewportWidth() {
		if(getParent() instanceof JViewport) { //if the panel is inside a viewport
			final JViewport viewport = (JViewport)getParent(); //cast the parent to a viewport
			if(tracksViewportWidth) { //if we track our own width
				return viewport.getWidth() >= getMinimumSize().width; //make sure we can be as small as the viewport wants us to be
			} else { //if we don't track our own width, fill the viewport if we're not big enough
				return getPreferredSize().width < viewport.getWidth(); //if we're smaller than the viewport, let the viewport size us to fill the viewport 
			}
		} else { //if we're not in a viewport
			return tracksViewportWidth; //use whatever value we were assigned
		}
	}

	/** Whether a viewport should always force the height of this panel to match its own. */
	private boolean tracksViewportHeight = false;

	/**
	 * Sets whether a viewport should always force the height of this panel to match its own height, unless the panel at its smallest could not fit inside the
	 * viewport
	 * @param verticallySelfManaged <code>true</code> if the panel should <em>not</em> be scrolled vertically by any parent viewport, allowing this panel to
	 *          manage its own vertical scrolling and/or wrapping.
	 */
	public void setTracksViewportHeight(final boolean verticallySelfManaged) {
		tracksViewportHeight = verticallySelfManaged;
	}

	/**
	 * Returns whether a viewport should always force the height of this panel to match the height of the viewport.
	 * <p>
	 * This implementation defaults to <code>false</code>, allowing the viewport to scroll vertically as needed.
	 * </p>
	 * @return <code>true</code> if a viewport should force the panel's height to match its own height, allowing this panel to manage its own vertical scrolling
	 *         and/or wrapping.
	 * @see #setTracksViewportHeight
	 */
	public boolean getScrollableTracksViewportHeight() {
		if(getParent() instanceof JViewport) { //if the panel is inside a viewport
			final JViewport viewport = (JViewport)getParent(); //cast the parent to a viewport
			if(tracksViewportHeight) { //if we track our own height
				return viewport.getHeight() >= getMinimumSize().height; //make sure we can be as small as the viewport wants us to be
			} else { //if we don't track our own height, fill the viewport if we're not big enough
				return getPreferredSize().height < viewport.getHeight(); //if we're smaller than the viewport, let the viewport size us to fill the viewport 
			}
		} else { //if we're not in a viewport
			return tracksViewportHeight; //use whatever value we were assigned
		}
	}

}
