package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**A content panel that, besides the content component, contains a button.
The button is connected to the content component; whenever the the content
	component is clicked, the button is activated.
<p>The button is placed in the border of the component.</p>
@author Garret Wilson
*/
public class ButtonContentPanel extends ContentPanel
{

	/**The object that listens for mouse events on the content component and
		in response clicks the button.
	*/
	private MouseListener mouseListener=null;

	/**The button to display in the border of the panel.*/
	private AbstractButton button=null;

		/**The button to display in the border of the panel.*/
		public AbstractButton getButton() {return button;}

		/**Sets the button to display in the border of the panel.
		@param newButton The button to connect to the content component and display in
			the border of the panel.
		*/
		public void setButton(final AbstractButton newButton)
		{
			if(button!=newButton) //if the buttonis really changing
			{
				if(button!=null)  //if we already have a button
					remove(button);   //remove the current one
				button=newButton;	//store the button
				if(newButton!=null)	//if we were given a new button
				{
					add(newButton, BorderLayout.WEST);  //put the button in the west of the panel TODO i18n
				}
			}
		}

		/**Sets the main content component in the center of the panel.
		<p>This version adds a listener for clicks to the content component, and in
			response selects the button.</p>
		@param newContentComponent The new component for the center of the panel,
			or <code>null</code> for no content component.
		*/
		public void setContentComponent(final Component newContentComponent)
		{
			final Component oldContentComponent=getContentComponent();	//get the current content component
			super.setContentComponent(newContentComponent);	//set the content component normally
			if(oldContentComponent!=newContentComponent) //if the content component really changed
			{
				if(mouseListener==null)	//if we haven't yet created a mouse listener (we must create it here, because this method is called from the constructor before the variable would be initialized)
				{
					mouseListener=new MouseAdapter()	//create a mouse listener to listen for mouse clicks on the component
							{
								public void mouseClicked(final MouseEvent mouseEvent)
								{
//G***test, maybe									getButton().dispatchEvent(mouseEvent)
									getButton().doClick();	//G***testing
									getButton().requestFocusInWindow();	//request focus for the button
								}									
							};
				}
				if(oldContentComponent!=null)	//if we had a content component earlier
				{
					oldContentComponent.removeMouseListener(mouseListener);	//remove the mouse listener from the old component
				}
				if(newContentComponent!=null)	//if we were given a new content component
				{
					newContentComponent.addMouseListener(mouseListener);	//listen for mouse clicks on the component
				}
			}
		}

	/**Content component and button constructor.
	<p>The content component is guaranteed to be set before
		<code>initializeUI</code> is called.</p>
	@param contentComponent The new component for the center of the panel.
	@param button The button to connect to the content component and display in
		the border of the panel.
	*/
	public ButtonContentPanel(final Component contentComponent, final AbstractButton button)
	{
		this(contentComponent, button, true); //create and initialize the component
	}

	/**Initialize constructor.
	@param contentComponent The new component for the center of the panel.
	@param button The button to connect to the content component and display in
		the border of the panel.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ButtonContentPanel(final Component contentComponent, final AbstractButton button, final boolean initialize)
	{
		super(contentComponent, false);	//construct the panel with the content component, but don't initialize
		setButton(button);	//set the button
		if(initialize)  //if we should initialize
			initialize(); //initialize the panel
	}

}
