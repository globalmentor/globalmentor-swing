package com.garretwilson.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import com.garretwilson.lang.JavaConstants;
import com.garretwilson.model.Model;

/**Panel that provides one or more views to a data model.
<p>Bound properties:</p>
<dl>
	<dt><code>ModelPanel.MODEL_PROPERTY</code> (<code>Model</code>)</dt>
	<dd>Indicates the data model has been changed.</dd>
</dl>
@author Garret Wilson
*/
public class ModelPanel extends BasicPanel
{

	/**The data model for which this component provides a view.*/
	public final String MODEL_PROPERTY=ModelPanel.class.getName()+JavaConstants.PACKAGE_SEPARATOR+"model";

	/**The data model for which this component provides a view.*/
	private Model model;

		/**@return The data model for which this component provides a view.*/
		protected Model getModel() {return model;}

		/**Sets the data model.
		This is a bound property.
		@param newModel The data model for which this component provides a view.
		*/
		protected void setModel(final Model newModel)
		{
			final Model oldModel=model; //get the old value
			if(oldModel!=newModel)  //if the value is really changing
			{
				model=newModel; //update the value
					//show that the property has changed
				firePropertyChange(MODEL_PROPERTY, oldModel, newModel);
			}
		}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public ModelPanel(final Model model)
	{
		this(model, true); //initialize the panel
	}

	/**Constructor with optional initialization that uses a <code>FlowLayout</code>.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	@see #FlowLayout
	*/
	public ModelPanel(final Model model, final boolean initialize)
	{
		this(new FlowLayout(), model, initialize);	//construct the panel with a flow layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	*/
	public ModelPanel(final LayoutManager layout, final Model model)
	{
		this(layout, model, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ModelPanel(final LayoutManager layout, final Model model, final boolean initialize)
	{
		super(layout, false);	//construct the parent but don't initialize the panel
		this.model=model;	//save the model
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

}