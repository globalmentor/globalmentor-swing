package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import com.garretwilson.awt.*;
import com.garretwilson.lang.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.util.*;

/**An extended panel that has extra features beyond those in <code>JPanel</code>.
<p>Unlike <code>JPanel</code>, the constructors use a <code>GridBagLayout</code>
	by default if no layout manager is specified.</p>
<p>The component keeps track of the layout constraints used by the child
	components.</p>
<p>The panel stores properties and fires property change events when a
	property is modified.</p>
<p>The panel can keep track of whether its contents have been modified.</p>
<p>The panel can store a preferences node to use for preference, or use the
	default preferences node for the panel class.</p>
<p>The panel can indicate whether it can close.</p>
<p>The panel can recognize when it is embedded in a <code>JOptionPane</code>
	and can set certain option pane values accordingly.</p>
<p>The panel can keep track of a title, and if the panel has a titled border,
	it will automatically update the border's title when the title changes.</p> 
<p>The panel can keep track of which child component should get the default
	focus. An extended focus traversal policy is installed so that, if this
	panel because a root focus traversal cycle, the correct default focus
	component will be selected.</p>
<p>The panel can create default listeners, such as <code>ActionListener</code>
	and <code>DocumentListener</code>, that do nothing but update the status.</p>
<p>The panel can create a default <code>DocumentListener</code> that
	automatically sets the modified status to <code>true</code>.</p>
<p>The panel provides a shared constant inset object specifying no insets.</p>
<p>A basic panel can be used in place of a horizontal or vertical
	<code>Box</code>, with the added benefit that weights can be assigned to
	each component, using <code>createNextBoxConstraints()</code> for layout
	constraints when adding components. For example, a vertical layout might
	add a <code>Box.createGlue()</code> at the end using constraints of
	<code>createNextBoxConstraints(Box.X_AXIS, 1.0)</code>.</p>  
<p>Bound properties:</p>
<dl>
	<dt><code>BasicPanel.TITLE_PROPERTY_NAME</code> (<code>String</code>)</dt>
	<dd>Indicates the title has been changed.</dd>
	<dt><code>BasicPanel.ICON_PROPERTY_NAME</code> (<code>Icon</code>)</dt>
	<dd>Indicates the icon has been changed.</dd>
	<dt><code>Modifiable.MODIFIED_PROPERTY_NAME</code> (<code>Boolean</code>)</dt>
	<dd>Indicates that the boolean modified property has been changed.</dd>
</dl>
@author Garret Wilson
@see java.awt.Container#setFocusCycleRoot
@see java.beans.PropertyChangeListener
@see javax.swing.JOptionPane
@see java.awt.GridBagLayout
*/
public class BasicPanel extends JPanel implements CanClosable, DefaultFocusable, Modifiable
{

	/**An object specifying no insets.*/
	public final static Insets NO_INSETS=new Insets(0, 0, 0, 0);

	/**The name of the bound title property.*/
	public final String TITLE_PROPERTY_NAME=BasicPanel.class.getName()+JavaConstants.PACKAGE_SEPARATOR+"title";	//G***maybe later move this to a titleable interface
	/**The name of the bound icon property.*/
	public final String ICON_PROPERTY_NAME=BasicPanel.class.getName()+JavaConstants.PACKAGE_SEPARATOR+"icon";


	/**The weak map associating layout constraints with components.*/ 
	private final Map constraintsMap;

		/**Associates the given layout constraints with the specified child component.
		@param component The child component being added.
		@param constraints An object expressing layout constraints for this component.
		*/
		protected void putConstraints(final Component component, final Object constraints)
		{
			constraintsMap.put(component, constraints);	//associate the constraints with the component 
		}

		/**Determines which layout constraints are associated with the given child component
		@param component The child component with which constraints are associated.
		@return The constraints associated with the component, or <code>null</code>
			if there are no constraints associated with the component.
		*/
		public Object getConstraints(final Component component)
		{
			return constraintsMap.get(component);	//get any constraints associated with the component 
		}

		/**Removes any layout constraints that are associated with the given component
		@param component The child component with which constraints are associated.
		@return Any constraints that were associated with the component, or
			<code>null</code> if there were no constraints associated with the
			component.
		*/
		protected Object removeConstraints(final Component component)
		{
			return constraintsMap.remove(component);	//remove any constraints associated with the component 
		}

	/**Determines the largest x or y coordinate of all components that were added
		using a <code>GridBagConstraint</code>.
	@param axis The axis on which to determine the maximum coordinate, either
		<code>BoxLayout.X_AXIS</code> or <code>BoxLayout.Y_AXIS</code>
	@return The largest coordinate on the given axis, or <code>-1</code> if
		no components were added using a <code>GridBagConstraint</code>.
	@see BoxLayout#X_AXIS
	@see BoxLayout#Y_AXIS
	*/
	protected int getMaxGrid(final int axis) 
	{
		int max=-1;	//start out not finding any coordinate 
		for(int i=getComponentCount()-1; i>=0; --i)	//look at each child component
		{
			final Component component=getComponent(i);	//get a reference to this component
			final Object constraints=getConstraints(component);	//get any layout constraints associated with this component
			if(constraints instanceof GridBagConstraints)	//if these are grid bag constraints
			{
				final GridBagConstraints gridBagConstraints=(GridBagConstraints)constraints;	//cast the constraints to the appropriate type
				switch(axis)	//see which axis we're looking at
				{
					case BoxLayout.X_AXIS:
						max=Math.max(max, gridBagConstraints.gridx);	//see if we need to update the largest x coordinate
						break;
					case BoxLayout.Y_AXIS:
						max=Math.max(max, gridBagConstraints.gridy);	//see if we need to update the largest y coordinate
						break;
				}
			}
		}
		return max;	//return whatever max value we found
	}

	/**Creates constraints appropriate for laying out components in a row in a
		single column or row on the horizontal or vertial axis.
	The constraints will have a weight of 0.0.
	@param axis The axis along which components are being laid out, either
		<code>BoxLayout.X_AXIS</code> or <code>BoxLayout.Y_AXIS</code>
	@return A grid bag constraint object for adding a new component in single
		file along the horizontal or vertical axis.
	@see BoxLayout#X_AXIS
	@see BoxLayout#Y_AXIS
	*/
	public GridBagConstraints createNextBoxConstraints(final int axis) 
	{
		return createNextBoxConstraints(axis, 0.0);	//return box constraints with no weight
	}

	/**Creates constraints appropriate for laying out components in a row in a
		single column or row on the horizontal or vertial axis.
	@param axis The axis along which components are being laid out, either
		<code>BoxLayout.X_AXIS</code> or <code>BoxLayout.Y_AXIS</code>
	@param weight An amount specifying how to distribute the extra space along the
		axis.
	@return A grid bag constraint object for adding a new component in single
		file along the horizontal or vertical axis.
	@see BoxLayout#X_AXIS
	@see BoxLayout#Y_AXIS
	*/
	public GridBagConstraints createNextBoxConstraints(final int axis, double weight) 
	{
		final int nextGrid=getMaxGrid(axis)+1;	//determine the next coordinate on the grid
		return new GridBagConstraints(
				axis==BoxLayout.X_AXIS ? nextGrid : 0,	//use the next coordinate for the appropriate axis
				axis==BoxLayout.Y_AXIS ? nextGrid : 0,
				1,
				1,
				axis==BoxLayout.X_AXIS ? weight : 1.0,	//use the weight for the appropriate axis
				axis==BoxLayout.Y_AXIS ? weight : 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, NO_INSETS, 0, 0);
}

	/**The preferences that should be used for this panel, or <code>null</code>
		if the default preferences for this class should be used.
	*/
	private Preferences preferences;

		/**@return The preferences that should be used for this panel, or the default
			preferences for this class if no preferences are specifically set.
		*/
		public Preferences getPreferences()
		{
			return preferences!=null ? preferences: Preferences.userNodeForPackage(getClass());	//return the user preferences node for whatever class extends this one 
		}
		
		/**Sets the preferences to be used for this panel.
		@param preferences The preferences that should be used for this panel, or
			<code>null</code if the default preferences for this class should be used
		*/
		public void setPreferences(final Preferences preferences)
		{
			this.preferences=preferences;	//store the preferences
		}

	/**Whether the object has been modified; the default is not modified.*/
	private boolean modified=false;

		/**@return Whether the object been modified.*/
		public boolean isModified() {return modified;}

		/**Sets whether the object has been modified.
			This is a bound property.
		@param newModified The new modification status.
		*/
		public void setModified(final boolean newModified)
		{
			final boolean oldModified=modified; //get the old modified value
			if(oldModified!=newModified)  //if the value is really changing
			{
				modified=newModified; //update the value
					//show that the modified property has changed
				firePropertyChange(MODIFIED_PROPERTY_NAME, BooleanUtilities.toBoolean(oldModified), BooleanUtilities.toBoolean(newModified));
			}
		}

	/**The map of properties.*/
	private final Map propertyMap=new HashMap();
	
		/**Gets a property of the panel.
		@param key The key to the property.
		@return The value of the panel's property, or <code>null</code> if
			that property does not exist.
		*/
		public Object getProperty(final Object key)
		{
			return propertyMap.get(key);	//return the property from the property map
		}
		
		/**Sets the value of a panel property, and fires a property changed
			event if the key is a string.
		If the property represented by the key already exists, it will be replaced.
		@param key The non-<code>null</code> property key.
		@param value The property value.
		@return The old property value associated with the key, or <code>null</code>
			if no value was associated with the key previously.
		@see PropertyChangeEvent
		*/
		public Object setProperty(final Object key, final Object value)
		{
			final Object oldValue=propertyMap.put(key, value);	//put the value in the map keyed to the key and save the old value
			if(key instanceof String)	//if they key was a string
			{					
				firePropertyChange((String)key, oldValue, value);	//show that the property value has changed
			}
			return oldValue;	//return the old property value, if there was one
		}
	
		/**Removes a property of the panel.
		If the property represented by the key does not exist, no action is taken.
		@param key The non-<code>null</code> property key.
		@return The removed property value, or <code>null</code> if there was no
			property.
		*/
		public Object removeProperty(final Object key)
		{
			return propertyMap.remove(key);	//remove and return the property value keyed to the key
		}
		
	/**The title of the panel, or <code>null</code> if there is no title.*/
	private String title=null;

		/**@return The title of the panel, or <code>null</code> if there is no title.*/
		public String getTitle() {return title;}

		/**Sets the title of the panel. If the panel border is a
			<code>TitledBorder</code>, its title is updated.
		This is a bound property.
		@param newTitle The new title of the panel, or <code>null</code> for no title.
		*/
		public void setTitle(final String newTitle)
		{
			final String oldTitle=title; //get the old title value
			final Border border=getBorder();	//get our current border
			if(border instanceof TitledBorder)	//if the border is a titled border
			{
				final TitledBorder titledBorder=(TitledBorder)border;	//cast the border to a titled border
				if(!ObjectUtilities.equals(titledBorder.getTitle(), newTitle))	//if the new title is different than the one currently on the border
				{
					titledBorder.setTitle(newTitle);	//update the title on the border
				}
			}
			if(!ObjectUtilities.equals(oldTitle, newTitle))  //if the value is really changing
			{
				title=newTitle; //update the value					
				firePropertyChange(TITLE_PROPERTY_NAME, oldTitle, newTitle);	//show that the property has changed
			}
		}

	/**The icon of the panel, or <code>null</code> if there is no icon.*/
	private Icon icon=null;

		/**@return The icon of the panel, or <code>null</code> if there is no icon.*/
		public Icon getIcon() {return icon;}

		/**Sets the icon of the panel.
		This is a bound property.
		@param newIcon The new icon of the panel, or <code>null</code> for no icon.
		*/
		public void setIcon(final Icon newIcon)
		{
			final Icon oldIcon=icon; //get the old title value
			if(oldIcon!=newIcon)  //if the value is really changing
			{
				icon=newIcon; //update the value					
				firePropertyChange(ICON_PROPERTY_NAME, oldIcon, newIcon);	//show that the property has changed
			}
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

	/**Default constructor that uses a <code>GridBagLayout</code>.
	@see #GridBagLayout
	*/
	public BasicPanel()
	{
		this(true); //initialize the panel
	}

	/**Constructor with optional initialization that uses a <code>GridBagLayout</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	@see #GridBagLayout
	*/
	public BasicPanel(final boolean initialize)
	{
		this(new GridBagLayout(), initialize);	//construct the panel with a grid bag layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	*/
	public BasicPanel(final LayoutManager layout)
	{
		this(layout, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public BasicPanel(final LayoutManager layout, final boolean initialize)
	{
		super(layout, false);	//construct the parent class but don't initialize
		constraintsMap=new WeakHashMap();	//construct a map to associate layout constraints with child components, using a weak map so that we won't keep child components from being claimed by the garbage collector should we get out of synch with the actual child components
		preferences=null;	//show that we should use the default preferences for this class
		defaultFocusComponent=null;	//default to no default focus component
			//create and install a new layout focus traversal policy that will
			//automatically use the default focus component, if available
		setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()
				{
					public Component getDefaultComponent(final Container focusCycleRoot)	//if the default component is requested
					{
							//if we have a default focus component, return it; otherwise, use the value given by the parent traversal policy class
						return getDefaultFocusComponent()!=null ? getDefaultFocusComponent() : super.getDefaultComponent(focusCycleRoot);
					}
				});
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initializes the content panel. Should only be called once per instance.
	@see #initializeUI
	*/
	public void initialize()	//TODO set a flag that will only allow initialization once per instance
	{
		initializeUI(); //initialize the user interface
		updateStatus();  //update the actions
		setModified(false);	//show that the information has not been modified G***maybe don't even update the modified status until initialization has occurred
	}

	/**Initializes the user interface.
		Any derived class that overrides this method should call this version.
	*/
  protected void initializeUI()
  {
  }

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
	}



	/**Adds the specified component to this container at the specified
		index.
	<p>This version stores the layout constraints locally so they may be accessed
		later.</p> 
	@param component The component to be added.
	@param constraints An object expressing layout constraints for this component.
	@param index The position in the container's list at which to
		insert the component, where <code>-1</code> means append to the end.
	@exception IllegalArgumentException Thrown if <code>index</code> is invalid.
	@exception IllegalArgumentException Thrown if adding the container's parent.
		to itself.
	@exception IllegalArgumentException Thrown if adding a window to a container.
	@see #putConstraints
	*/
	protected void addImpl(final Component component, final Object constraints, final int index)
	{
		super.addImpl(component, constraints, index);	//add the component normally
		putConstraints(component, constraints);	//associate the constraints with the component 
	}

	/**Removes the component, specified by <code>index</code>, from this container.
	<p>This version removes any layout constraints locally associated with the
		child component at the given index.</p> 
	@param index The index of the component to be removed.
	@see #removeConstraints
 	*/
	public void remove(final int index)
	{
		super.remove(index);	//remove the component normally; if there were no exceptions, the index was valid
		removeConstraints(getComponent(index));	//remove any constraints associated with the component at the given index
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
	public boolean requestDefaultFocusComponentFocus()
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

	/**@return <code>true</code> if the panel can close.*/
	public boolean canClose()
	{
		return true;	//default to always allowing closing
	}
	
	/**@return The component that should get the initial focus.*/
//G***fix	public Component getInitialFocusComponent() {return labelTextField;}
	
	
	/**@return The <code>JOptionPane</code> in which this panel is embedded, or
		<code>null</code> if this panel is not embedded in a
		<code>JOptionPane</code>.
	*/
	protected JOptionPane getParentOptionPane()
	{
		Container parent=getParent();	//get the parent container
		while(parent!=null && !(parent instanceof JOptionPane))	//while we're still getting parents, but we haven't found an option pane
		{
			parent=parent.getParent();	//get the parent's parent
		}
		return (JOptionPane)parent;	//return the JOptionPane parent, or null if there was no JOptionPane parent
	}
	
	/**Sets the value property of the parent container <code>JOptionPane</code>. 
		If this panel is not embedded in a <code>JOptionPane</code>, no action
		occurs.
	<p>For example, setting a value of
		<code>new Integer(JOptionPane.OK_OPTION)</code> will close the option pane
		and return that value.</p>
	@param newValue The chosen value.
	@see JOptionPane#setValue
	@see #getValue
	*/
	public void setOptionPaneValue(final Object newValue)
	{
		final JOptionPane optionPane=getParentOptionPane();	//get the option pane in which we're embedded
		if(optionPane!=null)	//if we're embedded in an option pane
		{
			optionPane.setValue(newValue);	//set the value of the option pane
		}
	}

	/**Returns the value the user has selected in the parent container 
		<code>JOptionPane</code>. <code>UNINITIALIZED_VALUE</code>
		implies the user has not yet made a choice, <code>null</code> means the
		user closed the window with out choosing anything or this panel is not
		embedded in a <code>JOptionPane</code>. Otherwise the returned value should
		be one of the options defined in <code>JOptionPane</code>.
	@return the <code>Object</code> chosen by the user,
		<code>UNINITIALIZED_VALUE</code> if the user has not yet made a choice, or
		<code>null</code> if the user closed the window without making a choice or
		this panel is not embedded in a <code>JOptionPane</code>.
	@see JOptionPane#getValue
	@see #setValue
	*/
	public Object getOptionPaneValue()
	{
		final JOptionPane optionPane=getParentOptionPane();	//get the option pane in which we're embedded
		return optionPane!=null ? optionPane.getValue() : null;	//return the value property of the option pane, or null if we are not embedded in a JOptionPane
	}

	/**Creates an action listener that, when an action occurs, updates the
		modified status to <code>true</code>.
	@see #setModified
	*/
	public ActionListener createModifyActionListener()
	{
		return new ActionListener()	//create a new action listener that will do nothing but set modified to true
				{
					public void actionPerformed(final ActionEvent actionEvent) {setModified(true);}	//if the action occurs, show that we've been modified
				};
	}

	/**Creates a document listener that, when a document is modified, updates
		the modified status to <code>true</code>.
	@see #setModified
	*/
	public DocumentListener createModifyDocumentListener()
	{
		return new DocumentModifyAdapter()	//create a new document listener that will do nothing but set modified to true
				{
					public void modifyUpdate(final DocumentEvent documentEvent) {setModified(true);}	//if the document is modified, show that we've been modfied
				};
	}

	/**Creates a list selection listener that, when the list selection changes,
		updates the modified status to <code>true</code>.
	@see #setModified
	*/
	public ListSelectionListener createModifyListSelectionListener()
	{
		return new ListSelectionListener()	//create a new list selection listener that will do nothing but set modified to true
				{
					public void valueChanged(final ListSelectionEvent listSelectionEvent) {setModified(true);}	//if the list selection changes, show that we've been modified
				};
	}

	/**Creates a property change listener that, when any property changes,
		updates the modified status to <code>true</code>.
	@see #setModified
	*/
/*G***bring back
	public PropertyChangeListener createModifyPropertyChangeListener()
	{
		return new PropertyChangeListener()	//create a new property change listener that will do nothing but set modified to true
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {setModified(true);}	//if a property is modified, show that we've been modified
				};
	}
*/

	/**Creates a property change listener that, when the given property changes,
		updates the modified status to <code>true</code>.
	@param propertyName The name of the property that, when changed, will set
		the modified status to <code>true</code>. 
	@see #setModified
	*/
	public PropertyChangeListener createModifyPropertyChangeListener(final String propertyName)
	{
		return new PropertyChangeListener()	//create a new property change listener that will do nothing but set modified to true
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//if a property is modified
					{
						if(propertyName.equals(propertyChangeEvent.getPropertyName()))	//if the property we're concerned about changed
						{
							setModified(true);	//show that we've been modified
						}
					}
				};
	}

	/**Creates a property change listener that, when the given property changes
		to the given value, updates the modified status to <code>true</code>.
	@param propertyName The name of the property that, when changed, will set
		the modified status to <code>true</code>. 
	@param propertyValue The value of the property that, when equal to the new
		value, will set the modified status to <code>true</code>. 
	@see #setModified
	*/
	public PropertyChangeListener createModifyPropertyChangeListener(final String propertyName, final Object propertyValue)
	{
		return new PropertyChangeListener()	//create a new property change listener that will do nothing but set modified to true
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//if a property is modified
					{
						if(propertyName.equals(propertyChangeEvent.getPropertyName())	//if the property we're concerned about changed
								&& ObjectUtilities.equals(propertyValue, propertyChangeEvent.getNewValue()))
						{
							setModified(true);	//show that we've been modified
						}
					}
				};
	}

	/**Creates a property change listener that, when the the "modified" property
		changes to <code>true</code>, updates the modified status to <code>true</code>.
	Convenience method.
	@see Modifiable#MODIFIED_PROPERTY_NAME
	@see Boolean#TRUE
	@see #setModified
	*/
	public PropertyChangeListener createModifyModifiedChangeListener()
	{
		return createModifyPropertyChangeListener(MODIFIED_PROPERTY_NAME, Boolean.TRUE);	//create a property change listener that will set modified to true if the modified property changes to true
	}

	/**Creates a property change listener that, when a property chnages,
		updates the status.
	@see #updateStatus
	*/
	public PropertyChangeListener createUpdateStatusPropertyChangeListener()
	{
		return new PropertyChangeListener()	//create a new property change listener that will do nothing but update the status
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {updateStatus();}	//if a property is modified, update the status
				};
	}

	/**Creates an item that, when an item is modified, updates
		the modified status to <code>true</code>.
	@see #setModified
	*/
/*G***fix or del
	public DocumentListener createModifyDocumentListener()
	{
		return new DocumentModifyAdapter()	//create a new document listener that will do nothing but update the status
				{
					public void modifyUpdate(final DocumentEvent documentEvent) {setModified(true);}	//if the document is modified, show that we've been modfied
				};
	}
*/

	/**Creates an action listener that, when an action occurs, updates
		the status.
	@see #updateStatus
	*/
	public ActionListener createUpdateStatusActionListener()
	{
		return new ActionListener()	//create a new action listener that will do nothing but update the status
				{
					public void actionPerformed(final ActionEvent actionEvent) {updateStatus();}	//if the action occurs, update the status
				};
	}

	/**Creates a document listener that, when a document is modified, updates
		the status.
	@see #updateStatus
	*/
	public DocumentListener createUpdateStatusDocumentListener()
	{
		return new DocumentModifyAdapter()	//create a new document listener that will do nothing but update the status
				{
					public void modifyUpdate(final DocumentEvent documentEvent) {updateStatus();}	//if the document is modified, update the status
				};
	}

}
