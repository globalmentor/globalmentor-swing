/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;

import com.globalmentor.model.Model;
import com.globalmentor.model.ModelView;

/**Panel that provides one or more views to a data model.
<p>Bound properties:</p>
<dl>
	<dt>{@link Model#MODEL_PROPERTY} ({@link Model})</dt>
	<dd>Indicates the data model has been changed.</dd>
</dl>
@author Garret Wilson
*/
public class ModelPanel<M> extends ModifiablePanel implements ModelView<M>
{

	/**The data model for which this component provides a view.*/
	private M model;

		/**Returns the component data model.
		<p>A calling program should first call <code>verify()</code> to ensure
			the data is valid and that the model reflects the currently entered data.
		@return The data model for which this component provides a view.
		@see #verify()
		*/
		public M getModel() {return model;}

		/**Sets the data model.
		This is a bound property.
		@param newModel The data model for which this component provides a view.
		*/
		public void setModel(final M newModel)
		{
			final M oldModel=model; //get the old value
			if(oldModel!=newModel)  //if the value is really changing
			{
				model=newModel; //update the value
				try
				{
					loadModel();	//try to load the model, as we've changed models
					setModified(false);	//show that we are no longer modified
				}
				catch(IOException ioException)	//if there were any problems saving the model
				{
					SwingApplication.displayApplicationError(this, ioException);	//display the error
				}
					//show that the property has changed
				firePropertyChange(Model.MODEL_PROPERTY, oldModel, newModel);
			}
		}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public ModelPanel(final M model)
	{
		this(model, true); //initialize the panel
	}

	/**Constructor with optional initialization that uses a <code>FlowLayout</code>.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	@see #FlowLayout
	*/
	public ModelPanel(final M model, final boolean initialize)
	{
		this(new FlowLayout(), model, initialize);	//construct the panel with a flow layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	*/
	public ModelPanel(final LayoutManager layout, final M model)
	{
		this(layout, model, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ModelPanel(final LayoutManager layout, final M model, final boolean initialize)
	{
		super(layout, false);	//construct the parent but don't initialize the panel
		this.model=model;	//save the model
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initializes the data.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeData()
	{
		super.initializeData(); //do the default initialization
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
	public void loadModel() throws IOException
	{
	}

	/**Stores the current data being edited to the model, if necessary.
	@exception IOException Thrown if there was an error saving the model.
	*/
	public void saveModel() throws IOException
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