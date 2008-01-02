package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
import com.garretwilson.awt.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.util.*;
import com.globalmentor.java.*;

/**A panel that can be modified, and recognized when its contained components
	have been modified.
<p>The panel can keep track of whether its contents have been modified.</p>
<p>If the <code>Modifiable.MODIFIED_PROPERTY</code> is set to false, all
	all descendant components that implement <code>Modifiable</code> have
	their modified properties set to <code>false</code> as well.</p>
<p>Whenever a component is added that is <code>Modifiable</code> or has
	<code>Modifiable</code> children, listeners are installed that will
	change this panel's modified status to <code>true</code> when one of those
	children are modified. (Setting the modification status of those children
	to <code>false</code> will have no effect.) Similarly, recognized child
	components such as text components, radio buttons, and checkboxes, along
	with children of added non-<code>Modifiable</code> components, will have
	listeners installed that change this panel's modification status to
	<code>true</code> in response to modifications in those components.</p>
<p>Bound properties:</p>
<dl>
	<dt><code>Modifiable.MODIFIED_PROPERTY</code> (<code>Boolean</code>)</dt>
	<dd>Indicates that the boolean modified property has been changed.</dd>
</dl>
@author Garret Wilson
@see java.beans.PropertyChangeListener
*/
public class ModifiablePanel extends BasicPanel implements Modifiable
{

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
				firePropertyChange(MODIFIED_PROPERTY, Boolean.valueOf(oldModified), Boolean.valueOf(newModified));
				if(!modified)	//if we're now not modified
				{
					ContainerUtilities.setModifiableDescendants(this, false);	//tell all of our child components that they are not modified, either
				}
			}
		}

	/**The lazily-created listener that modifies this panel when an action occurs.*/
	private ActionListener modifyActionListener;

		/**@return The lazily-created listener that modifies this panel when an action occurs.*/
		protected ActionListener getModifyActionListener()
		{
			if(modifyActionListener==null)	//if we haven't created the listener, yet
			{
				modifyActionListener=createModifyActionListener();	//create the listener
			}
			return modifyActionListener;	//return the listener
		}

	/**The lazily-created listener that modifies this panel when a document is modified.*/
	private DocumentListener modifyDocumentListener;

		/**@return The lazily-created listener that modifies this panel when a document is modified.*/
		protected DocumentListener getModifyDocumentListener()
		{
			if(modifyDocumentListener==null)	//if we haven't created the listener, yet
			{
				modifyDocumentListener=createModifyDocumentListener();	//create the listener
			}
			return modifyDocumentListener;	//return the listener
		}

	/**The lazily-created listener that modifies this panel when an item state changes.*/
	private ItemListener modifyItemListener;

		/**@return The lazily-created listener that modifies this panel when an item state changes.*/
		protected ItemListener getModifyItemListener()
		{
			if(modifyItemListener==null)	//if we haven't created the listener, yet
			{
				modifyItemListener=createModifyItemListener();	//create the listener
			}
			return modifyItemListener;	//return the listener
		}

	/**The lazily-created listener that modifies this panel when a list selection changes.*/
	private ListSelectionListener modifyListSelectionListener;

		/**@return The lazily-created listener that modifies this panel when a list selection.*/
		protected ListSelectionListener getModifyListSelectionListener()
		{
			if(modifyListSelectionListener==null)	//if we haven't created the listener, yet
			{
				modifyListSelectionListener=createModifyListSelectionListener();	//create the listener
			}
			return modifyListSelectionListener;	//return the listener
		}

	/**The lazily-created listener that modifies this panel when a
		<code>Modifiable</code> is modified. 
	*/
	private PropertyChangeListener modifyModifiedPropertyChangeListener;

		/**@return The lazily-created listener that modifies this panel when a
			<code>Modifiable</code> is modified. 
		*/
		protected PropertyChangeListener getModifyModifiedPropertyChangeListener()
		{
			if(modifyModifiedPropertyChangeListener==null)	//if we haven't created the listener, yet
			{
				modifyModifiedPropertyChangeListener=createModifyModifiedChangeListener();	//create the listener
			}
			return modifyModifiedPropertyChangeListener;	//return the listener
		}

	/**Default constructor that uses a <code>FlowLayout</code>.
	@see #FlowLayout
	*/
	public ModifiablePanel()
	{
		this(true); //initialize the panel
	}

	/**Constructor with optional initialization that uses a <code>FlowLayout</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	@see #FlowLayout
	*/
	public ModifiablePanel(final boolean initialize)
	{
		this(new FlowLayout(), initialize);	//construct the panel with a flow layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	*/
	public ModifiablePanel(final LayoutManager layout)
	{
		this(layout, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ModifiablePanel(final LayoutManager layout, final boolean initialize)
	{
		super(layout, false);	//construct the parent class but don't initialize
		modifyDocumentListener=null;
		modifyItemListener=null;
		modifyListSelectionListener=null;
		modifyModifiedPropertyChangeListener=null;
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initializes the panel. Should only be called once per instance.*/
	public void initialize()	//TODO set a flag that will only allow initialization once per instance
	{
		super.initialize();	//do the default initialization
		setModified(false);	//show that the information has not been modified		
		updateStatus();  //update the status again, because it might have been updated while we were modified G***this means updateStatus() is called twice---try to get around that, or maybe even place updateStatus() in setModified()
	}

	/**Adds the specified component to this container at the specified index.
	<p>This version adds appropriate listeners to added components so that this
		panel's modified status will be appropriately update when descendant
		components are modified.</p>
	@param component The component to be added.
	@param constraints An object expressing layout constraints for this component.
	@param index The position in the container's list at which to insert the
		component, where <code>-1</code> means append to the end.
	@exception IllegalArgumentException Thrown if <code>index</code> is invalid.
	@exception IllegalArgumentException Thrown if adding the container's parent
		to itself.
	@exception IllegalArgumentException Thrown if adding a window to a container.
	@see #addModifyListeners(Component)
	*/
	protected void addImpl(final Component component, final Object constraints, final int index)
	{
		super.addImpl(component, constraints, index);	//do the default adding
		addModifyListeners(component);	//add listeners to modify this panel in response to changes in this component
	}

	/**Removes the component, specified by <code>index</code>, from this container. 
	<p>Any listeners that were added are removed.</p>
	@param index The index of the component to be removed.
	@see #removeModifyListeners(Component)
	*/
	public void remove(final int index)
	{
		final Component component=getComponent(index);	//get a reference to the component to remove
		super.remove(index);	//remove the component
		removeModifyListeners(component);	//remove listeners from the component if everything succeeded
	}

	/**Removes the specified component from this container.
	<p>Any listeners that were added are removed.</p>
	@param component The component to be removed.
	@see #removeModifyListeners(Component)
	*/
	public void remove(final Component component)
	{
		super.remove(component);	//remove the component
		removeModifyListeners(component);	//remove our listeners
	}

	/**Removes all the components from this container.
	*/
	public void removeAll()
	{
		final Component[] components=getComponents();	//get all child components
		super.removeAll();	//remove all the components from the panel
		for(int i=components.length-1; i>=0; --i)	//look at each child component
		{
			removeModifyListeners(components[i]);	//remove our listeners from this child component that is now removed
		}
	}

	/**Adds appropriate listeners to this component or its descendants that will
		update this panel's modified status to <code>true</code> when descendant
		components are modified.
	<p>If the component or any of its descendants is <code>Modifiable</code>,
		a listener is added that changes this panel's modified status to
		<code>true</code> if the <code>Modifiable</code>'s modified status is
		changed to <code>true</code>.</p>
	<p>If the component is not <code>Modifiable</code>, any descendants of
		certain recognized types, such as text components and checkboxes, will
		have listeners registered that will change the modified status of this
		panel to <code>true</code> when those components are modified.</p>
	@param component The component to which listeners will be added, along with
		appropriate descendants.
	@see #removeModifyListeners(Component)
	*/
	protected void addModifyListeners(final Component component)
	{
		if(component instanceof Modifiable)	//if the component is modifiable
		{
			component.addPropertyChangeListener(getModifyModifiedPropertyChangeListener());	//listen for modifications and modify this panel in response
		}
/*G***fix
		else if(component instanceof JTextComponent)	//if the component is a text component
		{
			((JTextComponent)component).getD
		}
*/
		else if(component instanceof Container)	//if this component is a container
		{
			final Component[] components=((Container)component).getComponents();	//get all child components
			for(int i=components.length-1; i>=0; --i)	//look at each child component
			{
				addModifyListeners(components[i]);	//see if we need to add listeners to this child component
			}
		}
	}

	/**Removes all modify listeners that were added within
		<code>addModifyListeners()</code>.
	@param component The component from which listeners will be removed, along with
		appropriate descendants.
	@see #addModifyListeners(Component)
	*/
	private void removeModifyListeners(final Component component)
	{
		if(component instanceof Modifiable)	//if the component is modifiable
		{
			component.removePropertyChangeListener(getModifyModifiedPropertyChangeListener());	//stop listening for modifications
		}
		else if(component instanceof Container)	//if this component is a container
		{
			final Component[] components=((Container)component).getComponents();	//get all child components
			for(int i=components.length-1; i>=0; --i)	//look at each child component
			{
				removeModifyListeners(components[i]);	//see if we need to remove listeners from this child component
			}
		}		
	}

	/**Creates an action listener that, when an action occurs, updates the
		modified status to <code>true</code>.
	@see #setModified
	*/
	private ActionListener createModifyActionListener()
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
	private DocumentListener createModifyDocumentListener()
	{
		return new DocumentModifyAdapter()	//create a new document listener that will do nothing but set modified to true
				{
					public void modifyUpdate(final DocumentEvent documentEvent) {setModified(true);}	//if the document is modified, show that we've been modfied
				};
	}

	/**Creates an item listener that, when an item state changes,
		updates the modified status to <code>true</code>.
	@see #setModified
	*/
	public ItemListener createModifyItemListener()
	{
		return new ItemListener()	//create a new item listener that will do nothing but set modified to true
				{
					public void itemStateChanged(final ItemEvent itemEvent) {setModified(true);}	//if an item state changes, show that we've been modifiefd
				};
	}

	/**Creates a list selection listener that, when the list selection changes,
		updates the modified status to <code>true</code>.
	@see #setModified
	*/
	private ListSelectionListener createModifyListSelectionListener()
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
	protected PropertyChangeListener createModifyPropertyChangeListener(final String propertyName)
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
	private PropertyChangeListener createModifyPropertyChangeListener(final String propertyName, final Object propertyValue)
	{
		return new PropertyChangeListener()	//create a new property change listener that will do nothing but set modified to true
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//if a property is modified
					{
						if(propertyName.equals(propertyChangeEvent.getPropertyName())	//if the property we're concerned about changed
								&& Objects.equals(propertyValue, propertyChangeEvent.getNewValue()))
						{
							setModified(true);	//show that we've been modified
						}
					}
				};
	}

	/**Creates a property change listener that, when the the "modified" property
		changes to <code>true</code>, updates the modified status to <code>true</code>.
	Convenience method.
	@see Modifiable#MODIFIED_PROPERTY
	@see Boolean#TRUE
	@see #setModified
	*/
	private PropertyChangeListener createModifyModifiedChangeListener()
	{
		return createModifyPropertyChangeListener(MODIFIED_PROPERTY, Boolean.TRUE);	//create a property change listener that will set modified to true if the modified property changes to true
	}

	/**Creates an item that, when an item is modified, updates
		the modified status to <code>true</code>.
	@see #setModified
	*/
/*G***fix or del
	protected DocumentListener createModifyDocumentListener()
	{
		return new DocumentModifyAdapter()	//create a new document listener that will do nothing but update the status
				{
					public void modifyUpdate(final DocumentEvent documentEvent) {setModified(true);}	//if the document is modified, show that we've been modfied
				};
	}
*/

}
