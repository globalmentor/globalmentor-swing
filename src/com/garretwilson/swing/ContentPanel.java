package com.garretwilson.swing;

import java.awt.*;
import com.garretwilson.awt.*;
import com.garretwilson.util.*;

/**A generic panel that allows easy setup of a center content component.
<p>This panel uses a <code>BasicGridBagLayout</code> as its layout manager.</p>
<p>If the panel is inside a <code>JOptionPane</code>, the window containing
	to ensure the component has enough room every time the content
	component changes.</p>  
<p>If the content component implements <code>DefaultFocusable</code> and knows
	which component should get the default focus, any request for the default
	focus component will be delegated to the content component.</p>
@author Garret Wilson
@see BasicGridBagLayout
@see DefaultFocusable
*/
public class ContentPanel extends BasicPanel implements CanClosable
{

	/**The main content component in the center of the panel.*/
	private Component contentComponent=null;

		/**@return The main content component in the center of the panel, or
			<code>null</code> if there is no content component.
		*/
		public Component getContentComponent()
		{
			return contentComponent;  //return the content component
		}
	
		/**Sets the main content component in the center of the panel.
		<p>If the panel is inside a <code>JOptionPane</code>, the window containing
			to ensure the component has enough room.</p>  
		@param newContentComponent The new component for the center of the panel,
			or <code>null</code> for no content component.
		*/
		public void setContentComponent(final Component newContentComponent)
		{
			if(contentComponent!=newContentComponent) //if the content component is really changing
			{
			  if(contentComponent!=null)  //if we already have an content component
					remove(contentComponent);   //remove the current one
				contentComponent=newContentComponent; //store the content component
				if(newContentComponent!=null)	//if we were given a new content component
				{
					add(newContentComponent, BorderLayout.CENTER);  //put the content component in the center of the panel
				}
/*G***del when revalidate() works---maybe even remove WindowUtilities.packWindow()		  	
		  	if(getParentOptionPane()!=null)	//if this panel is inside an option pane
		  	{
		  			//TODO probably change this to only pack the window if the panel wants to be bigger than the window to keep from shrinking the window
		  			//(actually, this window.pack doesn't seem to ever shrink the window, just enlarge it, so this is probably fine
		  			//TODO remove the JOptionPane check when this enlarges but not shrinks the window
					WindowUtilities.packWindow(this);	//pack the window we're inside, if there is one, to ensure there's enough room to view this component
		  	}
		  	newContentComponent.repaint();	//repaint the component (important if we're inside a JOptionPane, for instance)
*/
				revalidate();	//update the layout
				if(newContentComponent!=null)	//if we were given a new content component
				{
					newContentComponent.repaint();	//repaint the component (important if we're inside a JOptionPane, for instance)
				}
				if(newContentComponent instanceof DefaultFocusable)	//if the content component knows what should be focused by default
				{
					((DefaultFocusable)newContentComponent).requestDefaultFocusComponentFocus();	//let the component request the focus for its default focus component
				}
			}
		}

	/**Returns the default focus component.
	If the content component implements <code>DefaultFocusable</code> and knows
		which component should get the default focus, any request for the default
		focus component will be delegated to the content component. Otherwise,
		whichever default focus component is set will be returned.
	@return The component that should get the default focus, or
		<code>null</code> if no component should get the default focus or it is
		unknown which component should get the default focus.
	*/
	public Component getDefaultFocusComponent()
	{
		if(getContentComponent() instanceof DefaultFocusable)	//if the content component knows what should be focused by default
		{
			final DefaultFocusable defaultFocusable=(DefaultFocusable)getContentComponent();	//cast the content component to a default focusable object
			if(defaultFocusable.getDefaultFocusComponent()!=null)	//if the component knows what should get the default focus
			{	 
				return defaultFocusable.getDefaultFocusComponent();	//return the default focus component specified by the content component 
			}
		}
		return super.getDefaultFocusComponent();	//if we can't get the default focus component from the content component, return whatever had been set with this panel
	}

	/**Content component constructor with optional initialization.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final Component contentComponent, final boolean initialize)
	{
		this(false); //construct the default panel without initializing it
		if(contentComponent!=null)  //if we were given a content component
		  setContentComponent(contentComponent);  //set the content component
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Application component constructor.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel.
	*/
	public ContentPanel(final Component contentComponent)
	{
		this(contentComponent, true); //create and initialize the component
	}

	/**Initialize constructor.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final boolean initialize)
	{
		super(new BasicGridBagLayout(), initialize);	//construct the panel with a basic grid bag layout and initialize if we should
/*G***del if not needed
		super(new BasicGridBagLayout(), false);	//construct the panel with a basic grid bag layout, but don't initialize
		if(initialize)  //if we should initialize
		  initialize(); //initialize the panel
*/
	}

	/**Default constructor.*/
	public ContentPanel()
	{
		this(true); //initialize the panel
	}

	/**@return <code>true</code> if the panel can close.
		This request is delegated to the content pane if possible, and therefore
		any class that overrides this one should call this version. 
	@see ApplicationFrame#canClose
	*/
	public boolean canClose()
	{
		if(getContentComponent() instanceof CanClosable)	//if the content component knows how to ask about closing
			return ((CanClosable)getContentComponent()).canClose();	//return whether the content component can close
		else	//if the content component doesn't know anything about closing		
			return super.canClose();  //ask the default implementation if we can close
	}
	
}
