package com.garretwilson.swing;

import java.awt.Component;

import javax.swing.*;

/**A toolbar that has basic convenience methods, such as those to update status.
<p>This toolbar maintains whether contained buttons should display text.
	When a button is added, whether the button has text will depend on the status
	of the toolbar <code>buttonTextVisible</code> property unless the button has
	no icon. When this property is changed using <code>setButtonTextVisible()</code>,
	all buttons on the toolbar change to match the new setting. The default
	value of the <code>buttonTextVisible</code> property is <code>true</code>.</p> 
@author Garret Wilson
*/
public class BasicToolBar extends JToolBar
{

	/**Whether buttons should have text displayed.*/
	private boolean buttonTextVisible=true;

		/**@return Whether buttons should have text displayed.*/
		public boolean isButtonTextVisible() {return buttonTextVisible;}

		/**Sets whether buttons should have text displayed.
		@param newVisible <code>true</code> if buttons should display text, else
			<code>false</code>.
		*/
		public void setButtonTextVisible(final boolean newVisible)
		{
			final boolean oldVisible=buttonTextVisible;	//get the current visibility of button text
			if(oldVisible!=newVisible)	//if the button text visibility is changing
			{
				buttonTextVisible=newVisible;	//change to the new visibility
				final Component[] components=getComponents();	//get all child components
				for(int i=components.length-1; i>=0; --i)	//look at each child component
				{
					if(components[i] instanceof AbstractButton)	//if this child component is a button
					{
						setupButton((AbstractButton)components[i]);	//set up this button to reflect our new button text visibility
					}
				}
			}
		}

	/**Default constructor with no name and horizontal orientation.*/
	public BasicToolBar()
	{
		this(true);	//construct a toolbar and initialize it	
	}

	/**Constructor with horizontal orientation and optional initialization.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public BasicToolBar(final boolean initialize)
	{
		this(null, initialize);	//construct and initialize the toolbar
	}

	/**Name constructor with horizontal orientation.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	*/
	public BasicToolBar(final String name)
	{
		this(name, true);	//construct and initialize the toolbar
	}

	/**Orientation constructor with no name.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public BasicToolBar(final int orientation)
	{
		this(orientation, true);	//construct and initialize the toolbar with the given orientation
	}

	/**Name constructor with optional initialization.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public BasicToolBar(final String name, final boolean initialize)
	{
		this(name, HORIZONTAL, initialize);	//construct the toolbar with horizontal orientation
	}

	/**Orientation constructor with optional initialization.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public BasicToolBar(final int orientation, final boolean initialize)
	{
		this(null, orientation, initialize);	//construct the toolbar with no name
	}

	/**Name and orientation constructor.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public BasicToolBar(final String name, final int orientation)
	{
		this(name, orientation, true);	//construct and initialize the toolbar with the given name and orientation
	}

	/**Name and orientation constructor with optional initialization.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public BasicToolBar(final String name, final int orientation, final boolean initialize)
	{
		super(name, orientation);	//construct the parent class
		if(initialize)  //if we should initialize
			initialize();   //initialize the toolbar
	}

	/**Initializes the toolbar. Should only be called once per instance.
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

	/**Adds the specified component to this container at the specified index.
	<p>If a button is added, whether it has text will depend on the state of the
		<code>buttonTextVisible</code> property.</p>
	@param component The component to be added.
	@param constraints An object expressing layout constraints for this component.
	@param index The position in the container's list at which to insert the
		component, where <code>-1</code> means append to the end.
	@exception IllegalArgumentException Thrown if <code>index</code> is invalid.
	@exception IllegalArgumentException Thrown if adding the container's parent
		to itself.
	@exception IllegalArgumentException Thrown if adding a window to a container.
	@see AbstractButton
	@see #setupButton
	*/
	protected void addImpl(final Component component, final Object constraints, final int index)
	{
		if(component instanceof AbstractButton)	//if this component is a button
		{
			setupButton((AbstractButton)component);	//set up the button
		}
		super.addImpl(component, constraints, index);	//do the default adding
	}

	/**Sets up a button to display text based on whether text should be visible.
	<p>If a button has no icon, its text will always be visible.</p>
	@param button The button to set up.
	@see #isButtonTextVisible
	*/
	protected void setupButton(final AbstractButton button)
	{	
		final Icon icon=button.getIcon();	//see if the button has an icon
		final boolean hideText=icon!=null ? !isButtonTextVisible() : false;	//always show the text if there is no icon; otherwise, show or hide the text appropriately			
		button.putClientProperty(ButtonConstants.HIDE_ACTION_TEXT_PROPERTY, Boolean.valueOf(hideText));	//tell the button whether its text should be hidden
		final String text=button.getText();	//get the button text
		if(hideText)	//if we should hide text
		{
			if(text!=null && text.length()>0)	//if there is text
			{
				button.setText(null);	//clear the button text
//G***fix			button.repaint();	//G***fix
			}
		}
		else if(text==null || text.length()==0)	//if we should show the text, but there is no text
		{
			if(button.getAction()!=null)	//if the button has a corresponding action
			{
				button.setText((String)button.getAction().getValue(Action.NAME));	//set the text to the name of the action				
			}
			//TODO fix for buttons that don't have corresponding actions
		}
	}
}
