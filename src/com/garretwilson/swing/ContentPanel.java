package com.garretwilson.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A generic panel that constructs a border layout and allows easy setup of
	its center content component.
@author Garret Wilson
*/
public class ContentPanel extends JPanel implements CanClosable
{

	/**The map of properties.*/
	private final Map propertyMap=new HashMap();
	
		/**Gets a property of the panel.
		@param key The key to the property.
		@return The value of the property, or <code>null</code> if that property
			does not exist.
		*/
		public Object getProperty(final Object key)
		{
			return propertyMap.get(key);	//return the property from the property map
		}
	
		/**Sets the value of a panel property.
		If <var>value</var> is <code>null</code>, the property will be removed. 
		@param key The non-<code>null</code> property key.
		@param value The property value.
		*/
		public void setProperty(final Object key, final Object value)
		{
			MapUtilities.putRemoveNull(propertyMap, key, value);	//put the value in the map keyed to the key, removing the keyed object if the value is null
		}

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
		@param newContentComponent The new component for the center of the panel.
		*/
		public void setContentComponent(final Component newContentComponent)
		{
			if(contentComponent!=newContentComponent) //if the content component is really changing
			{
			  if(contentComponent!=null)  //if we already have an content component
					remove(contentComponent);   //remove the current one
				contentComponent=newContentComponent; //store the content component
		  	add(newContentComponent, BorderLayout.CENTER);  //put the content component in the center of the panel
			}
		}

	/**Application component constructor with optional initialization.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	protected ContentPanel(final Component contentComponent, final boolean initialize)
	{
		this(initialize); //construct the default panel, initializing if requested
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

	/**Initializes constructor.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	protected ContentPanel(final boolean initialize)
	{
    final BorderLayout borderLayout=new BorderLayout(); //create a border layout
    setLayout(borderLayout);  //use the border layout
		if(initialize)  //if we should initialize
		  initialize(); //initialize the panel
	}

	/**Default constructor.*/
	public ContentPanel()
	{
		this(true); //initialize the panel
	}

	/**Initializes the content panel. Should only be called once per instance.
	@see #initializeUI
	*/
	protected void initialize()
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

	/**@return <code>true</code> if the panel can close.
	This request is delegated to the content pane if possible, and therefore any
	class that overrides this one should call this version. 
	@see ApplicationFrame#canClose
	*/
	public boolean canClose()
	{
		if(getContentComponent() instanceof CanClosable)	//if the content component knows how to ask about closing
			return ((CanClosable)getContentComponent()).canClose();	//return whether the content component can close
		else	//if the content component doesn't know anything about closing		
			return true;  //default to allowing the panel to be closed
	}

}
