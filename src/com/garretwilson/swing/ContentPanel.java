package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.garretwilson.lang.StringUtilities;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A generic panel that constructs a border layout and allows easy setup of
	its center content component.
@author Garret Wilson
*/
public class ContentPanel extends JPanel
{

	/**A set of flag objects representing options.*/
//G***del	private final Set flagSet=new HashSet();

		/**@return A set of flag objects representing options.*/
//G***del		protected Set getFlagSet() {return flagSet;}

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

	/**Application component and flags constructor.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel, or
		<code>null</code> if the default content component should be used.
	@param flags A non-<code>null</code> array of flags specifying options.
	*/
/*G***del
	public ContentPanel(final Component contentComponent, final Object[] flags)
	{
		CollectionUtilities.addAll(getFlagSet(), flags);  //add the flags to the set of flags
		initializeData();  //create the application actions and other properties
    final BorderLayout borderLayout=new BorderLayout(); //create a border layout
    setLayout(borderLayout);  //use a border layout
		if(contentComponent!=null)  //if we were given a content component
		  setContentComponent(contentComponent);  //set the content component
		initializeUI(); //initialize the user interface
	}
*/

	/**Application component constructor.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel, or
		<code>null</code> if the default content component should be used.
	*/
/*G***del when works
	public ContentPanel(final Component contentComponent)
	{
		this(contentPanel, ArrayUtilities.EMPTY_OBJECT_ARRAY);  //create a content panel with the given content component and no flags
	}
*/

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

	/**Default constructor.*/
/*G***del when works
	public ContentPanel()
	{
		this(null); //create a content panel with the default content panel
	}
*/

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

	/**Creates any application objects and initializes data.
		Any class that overrides this method must call this version.
	*/
/*G***del
	protected void initializeData()
	{
	}
*/

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
	@see ApplicationFrame#canClose
	*/
	public boolean canClose()
	{
		return true;  //default to allowing the panel to be closed
	}

}
