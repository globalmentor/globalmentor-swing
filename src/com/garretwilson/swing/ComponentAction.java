package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.beans.*;
import java.lang.ref.*;

import javax.swing.*;
import javax.swing.event.*;

import static com.garretwilson.lang.Objects.*;
import static com.garretwilson.swing.ComponentConstants.*;
import com.garretwilson.util.Debug;

/**Action that knows how to create its own component.
@author Garret Wilson
*/
public abstract class ComponentAction<C extends Component> extends AbstractAction
{

	/**The property that represents the selected objects of the component, if the component implements <code>ItemSelectable</code>.*/
//TODO del if not needed	public final static String SELECTED_OBJECTS_PROPERTY="selectedObjects";

	/**The listener we use to find out when the proxied action is changing.*/
//G***del if not needed	private PropertyChangeListener actionPropertyChangeListener;

	/**Whether action properties should be updated when the proxied action changes.*/
//G***fix	private boolean shouldUpdateProperties=true;

	/**Defines a proxy action object with a default description string and
		default icon.
	*/
	public ComponentAction()
	{
		super();  //construct the parent
	}

	/**Defines a proxy action object with the specified description string and a
		default icon.
	@param name The name description of the action.
	*/
	public ComponentAction(final String name)
	{
		this();  //do the default construction
		putValue(NAME, name);
	}

	/**Defines a proxy action object with the specified description string and the
		specified icon.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public ComponentAction(final String name, final Icon icon)
	{
		this(name);  //do the default construction
		putValue(Action.SMALL_ICON, icon);
	}

	/**Creates a new component and adds it to the container.
	@param container The container to which the new component should be added.
	@return The component added to the container.
	@see #createConfiguredComponent()
	*/
	public C addComponent(final Container container)
	{
		final C component=createConfiguredComponent();	//create and configure a new component
		container.add(component);	//add the configured component
		return component;	//return the created component
	}

	/**Creates and configures a component for use in adding to a container.
	@return A new component suitable for adding to a container.
	@see #createComponent()
	@see #configureComponent(C)
	*/
	public C createConfiguredComponent()
	{
		final C component=createComponent();	//create the component
		configureComponent(component);	//configure the component
		return component;	//return the configured component
	}

	/**Creates a component for the action.
	@return A new component for the action.
	*/
	protected abstract C createComponent();

	/**Configures a created component.
	@param component The component to configure.
	*/
	protected void configureComponent(final C component)
	{
/*G***fix		
		component.set
		String text = a!=null? (String)a.getValue(Action.NAME) : null;
		Icon icon   = a!=null? (Icon)a.getValue(Action.SMALL_ICON) : null;
	        boolean enabled = a!=null? a.isEnabled() : true;
	        String tooltip = a!=null?
	            (String)a.getValue(Action.SHORT_DESCRIPTION) : null;
	        JButton b = new JButton(text, icon) {
		    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
			PropertyChangeListener pcl = createActionChangeListener(this);
			if (pcl==null) {
			    pcl = super.createActionPropertyChangeListener(a);
			}
			return pcl;
		    }
		};
		if (icon !=null) {
		    b.putClientProperty("hideActionText", Boolean.TRUE);
		}
		b.setHorizontalTextPosition(JButton.CENTER);
		b.setVerticalTextPosition(JButton.BOTTOM);
*/
		final String shortDescription=(String)getValue(SHORT_DESCRIPTION);	//get the short description
		component.setEnabled(isEnabled());	//give the component the same enabled status as the action
		if(component instanceof JComponent)	//if this is a JComponent
		{
			final JComponent jComponent=(JComponent)component;	//get the component as a JComponent
			jComponent.setToolTipText(shortDescription);	//set the tooltip text from the short description
		}
	}

	/**@return A new property change listener for updating the component based on the status of the action.
	@param component The component to change in response to action property changes.
	*/
	protected PropertyChangeListener createActionPropertyChangeListener(final C component)
	{
		return new ActionPropertyChangeListener(component);	//return the default action property change listener
	}

	protected class ActionPropertyChangeListener implements PropertyChangeListener
	{
		
		/**The reference to the component; this implementation uses a weak reference.*/
		final Reference<C> componentReference;

			/**@return The component, or <code>null</code> if we no longer reference a component.*/
			public C getComponent() {return componentReference.get();}

		/**Creates a property change listener to update a component based upon action property changes.
		@param component The component to be updated.
		*/
		public ActionPropertyChangeListener(final C component)
		{
			componentReference=new WeakReference<C>(component);	//createa  weak reference to the component
		}
		
		/**Called when a bound property is changed.
		This version removes this listener from the action if the component has been garbage-collected.
		@param propertyChangeEvent An event object describing the event source and the property that has changed.
		*/
		public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
		{
			final Action action=(Action)propertyChangeEvent.getSource();	//get the source of the event
			final C component=getComponent();	//get the component we represent
			if(component!=null)	//if we have a component
			{
				final JComponent jComponent=asInstance(component, JComponent.class);	//get the component as a JComponent, if it is one
				final String propertyName=propertyChangeEvent.getPropertyName();	//get the property name
				if(ENABLED_PROPERTY.equals(propertyName))  //if the enabled state is changing, we must change the actual property (which will change the property value), rather than just changing the underlying value or the actual property won't change
				{
					component.setEnabled(((Boolean)propertyChangeEvent.getNewValue()).booleanValue());	//update the enabled state
				}
				else if(SHORT_DESCRIPTION.equals(propertyName))	//short description
				{
					if(jComponent!=null)	//if we have a JComponent
					{
						jComponent.setToolTipText((String)propertyChangeEvent.getNewValue());	//set the tooltip text from the new short description
					}
				}
	    }
			else	//if we don't have a component anymore
			{
				action.removePropertyChangeListener(this);	//stop listening for property changes
			}
		}
	}
}
