package com.garretwilson.swing;

/**A toolbar with capabilities especially useful to applications.
<p>This toolbar defaults to rollover buttons with no text.</p>
@author Garret Wilson
*/
public class ApplicationToolBar extends BasicToolBar
{

	/**Default constructor with no name and horizontal orientation.*/
	public ApplicationToolBar()
	{
		this(true);	//construct a toolbar and initialize it	
	}

	/**Constructor with horizontal orientation and optional initialization.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationToolBar(final boolean initialize)
	{
		this(null, initialize);	//construct and initialize the toolbar
	}

	/**Name constructor with horizontal orientation.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	*/
	public ApplicationToolBar(final String name)
	{
		this(name, true);	//construct and initialize the toolbar
	}

	/**Orientation constructor with no name.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public ApplicationToolBar(final int orientation)
	{
		this(orientation, true);	//construct and initialize the toolbar with the given orientation
	}

	/**Name constructor with optional initialization.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public ApplicationToolBar(final String name, final boolean initialize)
	{
		this(name, HORIZONTAL, initialize);	//construct the toolbar with horizontal orientation
	}

	/**Orientation constructor with optional initialization.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public ApplicationToolBar(final int orientation, final boolean initialize)
	{
		this(null, orientation, initialize);	//construct the toolbar with no name
	}

	/**Name and orientation constructor.
	@param name The name of the toolbar, used as the title of an undocked toolbar.
	@param orientation The orientation of the toolbar, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	*/
	public ApplicationToolBar(final String name, final int orientation)
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
	public ApplicationToolBar(final String name, final int orientation, final boolean initialize)
	{
		super(name, orientation);	//construct the parent class
		setRollover(true);	//default to a rollover toolbar
		setButtonTextVisible(false);	//don't show text by default
		if(initialize)  //if we should initialize
			initialize();   //initialize the toolbar
	}

}
