package com.garretwilson.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import com.garretwilson.model.*;

/**Panel that allows multiple views of a data model to be displayed.
<p>Bound properties:</p>
<dl>
	<dt><code>ModelViewable.MODEL_VIEW_PROPERTY</code> (<code>Integer</code>)</dt>
	<dd>Indicates the data view has been changed.</dd>
</dl>
@author Garret Wilson
*/
public abstract class ModelViewablePanel extends ModelPanel implements ModelViewable
{

	/**@return A value representing the supported data views ORed together.*/
	public abstract int getSupportedModelViews();

	/**Determines whether this object supports the data views.
	@param dataViews One or more <code>XXX_DATA_VIEW</code> constants
		ORed together.
	@return <code>true</code> if and only if this object kit supports all the
		indicated data views.
	*/
	public boolean isModelViewsSupported(final int dataViews)
	{
		return (getSupportedModelViews()&dataViews)==dataViews;	//see whether all the data views are supported
	}	

	/**The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
	private int dataView;

		/**@return The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
		public int getModelView() {return dataView;}

		/**Sets the view of the data.
		@param newView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
		@exception IllegalArgumentException Thrown if the given view is not supported.
		*/
		public void setModelView(final int newView)
		{
			final int oldView=dataView; //get the old value
			if(oldView!=newView)  //if the value is really changing
			{
				if(!isModelViewsSupported(newView))	//if the new data view isn't supported
					throw new IllegalArgumentException("Unsupported model view "+newView);
				try
				{
					if(isModified())	//if the data has been modified
					{
						saveModel(oldView);	//store any model data that was being edited in the old view, if any
					}
				}
				catch(IOException ioException)	//if there were any problems saving the model
				{
					cancelModelViewChange(oldView, newView);	//cancel the change
					SwingApplication.displayApplicationError(this, ioException);	//display the error
					return;	//don't change the view
				}		
				dataView=newView; //update the value
				try
				{
					loadModel(newView);	//load the model into the new view
				}
				catch(IOException ioException)	//if there were any problems saving the model
				{
					SwingApplication.displayApplicationError(this, ioException);	//display the error
				}		
				onModelViewChange(oldView, newView);	//show this class that the model is changing
				firePropertyChange(MODEL_VIEW_PROPERTY, new Integer(oldView), new Integer(newView));	//show that the property has changed
			}
		}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public ModelViewablePanel(final Model model)
	{
		this(model, true); //initialize the panel
	}

	/**Constructor with optional initialization that uses a <code>FlowLayout</code>.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	@see #FlowLayout
	*/
	public ModelViewablePanel(final Model model, final boolean initialize)
	{
		this(new FlowLayout(), model, initialize);	//construct the panel with a flow layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	*/
	public ModelViewablePanel(final LayoutManager layout, final Model model)
	{
		this(layout, model, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ModelViewablePanel(final LayoutManager layout, final Model model, final boolean initialize)
	{
		super(layout, model, false);	//construct the parent but don't initialize the panel
		dataView=NO_MODEL_VIEW;	//default to no valid view
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		setModelView(getDefaultModelView());	//set the default data view
	}

	/**@return The data model for which this component provides a view.*/
	protected Model getModel()
	{
/*G***del if not needed
		try
		{
			saveModel(getModelView());	//store the data that is being edited, if any data is being edited
		}
		catch(IOException ioException)	//if there were any problems saving the model TODO fix the way all this works
		{
			OptionPane.showMessageDialog(this, ioException.getMessage(), ioException.getClass().getName(), JOptionPane.ERROR_MESSAGE);	//G***i18n; TODO fix in a common routine
		}
*/
		return super.getModel();	//return the data that was just stored or was already stored
	}

	/**Sets the data model.
	This is a bound property.
	@param newModel The data model for which this component provides a view.
	*/
	protected void setModel(final Model newModel)
	{
		super.setModel(newModel);	//save the model normally
		try
		{
			loadModel(getModelView());	//try to load the model into our current data view
			newModel.setModified(false);	//show that the information has not been modified
		}
		catch(IOException ioException)	//if there were any problems saving the model
		{
			SwingApplication.displayApplicationError(this, ioException);	//display the error
		}		
	}

	/**Loads the data from the model to the given view.
	@param modelView The view of the data that should be loaded.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected abstract void loadModel(final int modelView) throws IOException;

	/**Stores the current data being edited to the model.
	If no model is being edited or there is no valid view, no action occurs.
	@param modelView The view of the model that should be stored.
	@exception IOException Thrown if there was an error saving the model.
	*/
	protected abstract void saveModel(final int modelView) throws IOException;

	/**Called when the change in model view should be canceled.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void cancelModelViewChange(final int oldView, final int newView)
	{
	}

	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onModelViewChange(final int oldView, final int newView)
	{
	}


}