package com.garretwilson.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;

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

		/**Returns the component data model.
		<p>A calling program should first call <code>verify()</code> to ensure
			the data is valid and that the model reflects the currently entered data.
		@return The data model for which this component provides a view.
		@see #verify()
		*/
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
				try
				{
					loadModel();	//try to load the model, as we've changed models
				}
				catch(IOException ioException)	//if there were any problems saving the model
				{
					SwingApplication.displayApplicationError(this, ioException);	//display the error
				}
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

	/**Initialize the user interface.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		try
		{
			loadModel();	//try to load the model, since we're just now initializing
		}
		catch(IOException ioException)	//if there were any problems saving the model
		{
			SwingApplication.displayApplicationError(this, ioException);	//display the error
		}
	}

	/**Loads the data from the model to the view, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel() throws IOException
	{
	}

	/**Stores the current data being edited to the model, if necessary.
	If no model is being edited or there is no valid view, no action occurs.
	@exception IOException Thrown if there was an error saving the model.
	*/
	protected void saveModel() throws IOException
	{
	}

	/**Verifies the component.
	<p>This version saves any editing changes to the model.</p>
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	@see #saveModel()
	*/
	public boolean verify()
	{
		boolean verified=super.verify();	//perform the default verification---do this first so that any child components that we may need for our model can be verified, saving their models
		if(verified)	//if the default verification succeeded
		{
			try
			{
				saveModel();	//store any model data that was being edited, if any
			}
			catch(IOException ioException)	//if there were any problems saving the model
			{
				SwingApplication.displayApplicationError(this, ioException);	//display the error
				verified=false;	//show that the component didn't verify
			}
		}
		return verified;	//show whether or not we successfully verified the component
	}

}