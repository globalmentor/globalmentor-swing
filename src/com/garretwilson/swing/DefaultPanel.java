package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.awt.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.util.*;

/**An extended panel that has extra features beyond those in <code>JPanel</code>.
<p>The panel stores properties and fires property change events when a
	property is modified.</p>
<p>The panel can indicate whether it can close.</p>
<p>The panel can recognize when it is embedded in a <code>JOptionPane</code>
	and can set certain option pane values accordingly.</p>
<p>The panel can keep track of which child component should get the default
	focus. An extended focus traversal policy is installed so that, if this
	panel because a root focus traversal cycle, the correct default focus
	component will be selected.</p>
<p>The panel can create default listeners, such as <code>ActionListener</code>
	and <code>DocumentListener</code>, that do nothing but update the status.</p> 
@author Garret Wilson
@see java.awt.Container#setFocusCycleRoot
@see java.beans.PropertyChangeListener
@see javax.swing.JOptionPane
*/
public class DefaultPanel extends JPanel implements CanClosable, DefaultFocusable
{

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

	/**Default constructor.*/
	public DefaultPanel()
	{
		this(true); //initialize the panel
	}

	/**Constructor with optional initialization.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public DefaultPanel(final boolean initialize)
	{
		this(new FlowLayout(), initialize);	//construct the panel with a flow layout by default, as does JPanel
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	*/
	public DefaultPanel(final LayoutManager layout)
	{
		this(layout, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public DefaultPanel(final LayoutManager layout, final boolean initialize)
	{
		super(layout);	//construct the parent class
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

	/**Requests that the default focus component should get the default.
	If the default focus comonent is itself <code>DefaultFocusable</code>, that
		component is asked to request focus for its default focus component, and
		so on.
	@return <code>false</code> if the focus change request is guaranteed to
		fail; <code>true</code> if it is likely to succeed.
	@see Component#requestFocusInWindow
	*/
	public boolean requestDefaultFocusComponentFocus()
	{
		final Component defaultFocusComponent=getDefaultFocusComponent();	//get the default focus component
		if(defaultFocusComponent instanceof DefaultFocusable	//if the component is itself default focusable
				&& ((DefaultFocusable)defaultFocusComponent).getDefaultFocusComponent()!=defaultFocusComponent)	//and the default focus component does not reference itself (which would create an endless loop)
		{
			return ((DefaultFocusable)defaultFocusComponent).requestDefaultFocusComponentFocus();	//pass the request on to the default focus component
		}
		else	//if the default focus component doesn't itself know about default focus components
		{
			return defaultFocusComponent.requestFocusInWindow();	//tell the default focus component to request the focus
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

	/**Creates an action listener that, when an action occurs, updates
		the status.
	@see #updateStatus
	*/
	public ActionListener createUpdateStatusActionListener()
	{
		return new ActionListener()	//create a new document listener that will do nothing but update the status
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
