package com.garretwilson.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import com.garretwilson.model.*;
import com.garretwilson.util.ArrayUtilities;

/**Panel that allows multiple views of a data model to be displayed.
<p>An implementing class may consider the data in each view to be loaded and
	stored separately, in which case <code>loadModel(int)</code> and
	<code>saveModel(int)</code> should be used for the appropriate views. The
	implementation may instead decide that all data is saved and loaded together,
	in which case <code>loadModel()</code> and <code>saveModel()</code> should
	be used to load or save all data in all views at the same time.
	An implementation may use a hybrid of these paradigms, for example storing
	views of all the data in some views using the view-specific loading and
	saving methods, and using the generic loading and save methods for views that
	contain portions of the data.</p>
<p>Bound properties:</p>
<dl>
	<dt><code>ModelViewable.MODEL_VIEW_PROPERTY</code> (<code>Integer</code>)</dt>
	<dd>Indicates the data view has been changed.</dd>
</dl>
@author Garret Wilson
*/
public abstract class ModelViewablePanel extends ModelPanel implements ModelViewable
{

	/**The default model views supported by this panel.*/
	private final int[] DEFAULT_SUPPORTED_MODEL_VIEWS=new int[]{};

	/**The default default model view of this panel.*/
	private final int DEFAULT_DEFAULT_MODEL_VIEW=NO_MODEL_VIEW;

	/**The model views supported by the panel.*/
	private int[] supportedModelViews;

		/**Determines whether this object supports the given data view.
		@param modelView A model view such as <code>SUMMARY_MODEL_VIEW</code>.
		@return <code>true</code> if and only if this object supports the indicated
			model view.
		*/
		public boolean isModelViewSupported(final int modelView)
		{
			return ArrayUtilities.indexOf(getSupportedModelViews(), modelView)>=0;	//see whether this model view is in our array of supported views
		}	

		/**@return The supported model views.*/
		public int[] getSupportedModelViews() {return supportedModelViews;}
	
		/**Sets the model views supported by this panel. 
		@param modelViews The supported model views.
		*/
		protected void setSupportedModelViews(final int[] modelViews)
		{
			supportedModelViews=modelViews;	//update the supported model views
		}

		/**Adds support for a model view. 
		@param modelView The supported model view to add.
		*/
		protected void addSupportedModelView(final int modelView)
		{
			if(!isModelViewSupported(modelView))	//if this model view isn't already supported
			{
				setSupportedModelViews(ArrayUtilities.append(getSupportedModelViews(), modelView));	//append the new model view to our supported model views
			}
		}

		/**Adds support for model views. 
		@param modelViews The supported model views to add.
		*/
		protected void addSupportedModelViews(final int[] modelViews)
		{
				//TODO make sure the given views aren't already supported
			setSupportedModelViews(ArrayUtilities.append(getSupportedModelViews(), modelViews));	//append the new model views to our supported model views
		}

	/**The default data view of this panel.*/
	private int defaultDataView;

		/**@return The default view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
		public int getDefaultModelView() {return defaultDataView;}

		/**Sets the default data view.
		@param dataView The default view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
		*/
		public void setDefaultDataView(final int dataView) {defaultDataView=dataView;}

	/**The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
	private int modelView;

		/**@return The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
		public int getModelView() {return modelView;}

		/**Sets the view of the data.
		@param newView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
		@exception IllegalArgumentException Thrown if the given view is not supported.
		*/
		public void setModelView(final int newView)
		{
			final int oldView=modelView; //get the old value
			if(oldView!=newView)  //if the value is really changing
			{
				if(!isModelViewSupported(newView))	//if the new data view isn't supported
					throw new IllegalArgumentException("Unsupported model view "+newView);
				if(canChangeModelView(oldView, newView))	//if we can change the view
				{
					modelView=newView; //update the value
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
				else	//if we shouldn't change the view
				{
					cancelModelViewChange(oldView, newView);	//cancel the change
				}
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
		supportedModelViews=DEFAULT_SUPPORTED_MODEL_VIEWS;	//set the model views we support (in this case, none)
		defaultDataView=DEFAULT_DEFAULT_MODEL_VIEW;	//set the default model view (in this case, no model view)
		modelView=NO_MODEL_VIEW;	//default to no valid view
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeUI()
	{
		assert isModelViewSupported(getDefaultModelView()) : "Specified default model view is not supported";
		modelView=getDefaultModelView();	//default to the model view
		super.initializeUI(); //do the default UI initialization, which will load the model into our new view
	}

	/**Loads the data from the model to the view, if necessary.
	<p>This implementation loads the model normally and then loads the model for
		the current view.</p>
	@exception IOException Thrown if there was an error loading the model.
	@see #loadModel(int)
	*/
	protected void loadModel() throws IOException
	{
		super.loadModel();	//do the default loading
		loadModel(getModelView());	//load the model into the current view
	}

	/**Loads the data from the model to the specified view, if necessary.
	@param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel(final int modelView) throws IOException
	{
	}

	/**Stores the current data being edited to the model, if necessary.
	<p>This implementation first saves the model for the current view and then
		saves the model normally.</p>
	@exception IOException Thrown if there was an error saving the model.
	@see #saveModel(int)
	*/
	protected void saveModel() throws IOException
	{
		saveModel(getModelView());	//save the current view
		super.saveModel();	//do the default saving
	}

	/**Stores the current data being edited to the model, if necessary.
	If no model is being edited or there is no valid view, no action occurs.
	@param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	@exception IOException Thrown if there was an error saving the model.
	*/
	protected void saveModel(final int modelView) throws IOException
	{
	}

	/**Determines whether it is appropriate under the circumstances to change
		the model view. This method may display any necessary dialog boxes in
		response to any error conditions before returning.
	<p>This version verifies the panel before allowing the change.</p>
	@param oldView The view before the change.
	@param newView The new view of the data
	@return <code>true</code> if the model view change should be allowed, else
		<code>false</code>.
	*/
	protected boolean canChangeModelView(final int oldView, final int newView)
	{
		return verify();	//we can change views if the panel verifies
	}

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