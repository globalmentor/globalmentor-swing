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

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.collections.DecoratorReverseMap;
import com.globalmentor.collections.ReverseMap;
import com.globalmentor.model.Verifiable;

/**Panel that allows multiple views of data to be displayed in separate tabs.
When the view value is changed, the appropriate tab is selected; when a new tab
	is selected, the view value is changed accordingly.
<p>The tabbed view panel uses a {@link BasicGridBagLayout} with a border
	paradigm.</p>
<p>Subclasses should set the correct default view only after initializing the tabs.</p>
@author Garret Wilson
@see BasicGridBagLayout
*/
public abstract class TabbedViewPanel<M> extends ModelViewablePanel<M>
{

	/**The center tabbed pane.*/
	private final JTabbedPane tabbedPane;

		/**@return The center tabbed pane.*/
		protected JTabbedPane getTabbedPane() {return tabbedPane;} 

	/**The reverse lookup map of components keyed to views.*/
	protected final ReverseMap viewComponentMap;

		/**Retrieves a component tab for a given view.
		@param view The view for which a tab should be returned; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
		@return The component that represents the given view, or <code>null</code>
			if no tab component has been associated with the given view.
		*/
		protected Component getViewComponent(final int view)
		{
			return (Component)viewComponentMap.get(new Integer(view));	//return the component, if there is one, associated with the view
		}

		/**Retrieves a view corresponding to a component tab.
		@param component The component that represents a view.
		@return The view represented by the given component (one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values), or <code>ModelViewable.NO_MODEL_VIEW</code>
			if there is no view associated with the given component
		*/
		protected int getComponentView(final Component component)
		{
			final Integer viewInteger=(Integer)viewComponentMap.getKey(component);	//get the view, if there is one, associated with the component
			return viewInteger!=null ? viewInteger.intValue() : NO_MODEL_VIEW;	//return the view value, or NO_MODEL_VIEW if there is no view
		}

		/**Associates a component with a particular view. The component should be
			one that is to appear on the tabbed pane, although this method does not
			itself add the component to the tabbed pane.
		<p>The view and component have a one-to-one relationship. Associating
			multiple components with a view or multiples views with a component will
			likely result in errant functionality.</p>
		@param view The view to associate with a component; one of the
			<code>ModelViewable.XXX_DATA_VIEW</code> values.
		@param component The component in the tabbed pane that represents the given
			view.
		*/
		private void setViewComponent(final int view, final Component component)
		{
			final Integer viewObject=new Integer(view);	//create an integer object to represent the view
			viewComponentMap.put(viewObject, component);	//associate the component with the view
		}

	/**Adds a tab to the tabbed pane representing a view of the data.
	<p>An appropriate default icon is used for the given view if possible.
		@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param component The component to be displayed on the tab that represents the
		given view.
	*/
	public void addView(final int view, final String title, final Component component)
	{
		addView(view, title, component, null);	//add the view with a default icon and no tooltip
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	<p>An appropriate default icon is used for the given view if possible.
		@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param component The component to be displayed on the tab that represents the
		given view.
	@param tip The tooltip to be displayed on the tab, or <code>null</code> for no
		tip.
	*/
	public void addView(final int view, final String title, final Component component, final String tip)
	{
		addView(view, title, component, tip, getTabbedPane().getTabCount());	//add the view as the last tab
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	<p>An appropriate default icon is used for the given view if possible.
		@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param component The component to be displayed on the tab that represents the
		given view.
	@param tip The tooltip to be displayed on the tab, or <code>null</code> for no
		tip.
	@param index The zero-based tab position at which to insert the new view.
	*/
	public void addView(final int view, final String title, final Component component, final String tip, final int index)
	{
		Icon icon=null;	//we'll try to determine an icon for the view
		switch(view)	//see which view we have
		{
			case TREE_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.TREE_ICON_FILENAME);
				break;
			case GRAPH_MODEL_VIEW:
				//TODO add icon for graph data view
				break;
			case WYSIWYG_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.DOCUMENT_RICH_CONTENT_ICON_FILENAME);
				break;
			case SEQUENCE_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.DOCUMENT_STACK_ICON_FILENAME);
				break;
			case LIST_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.LIST_ICON_FILENAME);
				break;
			case SUMMARY_MODEL_VIEW:
				//TODO add icon for summary data view
				break;
			case SOURCE_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.MARKUP_ICON_FILENAME);
				break;
			case CONFIGURATION_MODEL_VIEW:
				icon=IconResources.getIcon(IconResources.CONFIGURATION_ICON_FILENAME);
				break;
		}
		addView(view, title, icon, component, tip, index);	//add the view with the default icon, if we found one
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param icon The icon to be displayed on the tab, or <code>null</code> for no
		icon.
	@param component The component to be displayed on the tab that represents the
		given view.
	*/
	public void addView(final int view, final String title, final Icon icon, final Component component)
	{
		addView(view, title, icon, component, null);	//add the view with no tooltip
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param icon The icon to be displayed on the tab, or <code>null</code> for no
		icon.
	@param component The component to be displayed on the tab that represents the
		given view.
	@param tip The tooltip to be displayed on the tab, or <code>null</code> for no
		tip.
	*/
	public void addView(final int view, final String title, final Icon icon, final Component component, final String tip)
	{
		addView(view, title, icon, component, tip, getTabbedPane().getTabCount());	//add the view as the last tab		
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	@param view The view to associate with a component; one of the
		<code>ModelViewable.XXX_DATA_VIEW</code> values.
	@param title The title to be displayed on the tab.
	@param icon The icon to be displayed on the tab, or <code>null</code> for no
		icon.
	@param component The component to be displayed on the tab that represents the
		given view.
	@param tip The tooltip to be displayed on the tab, or <code>null</code> for no
		tip.
	@param index The zero-based tab position at which to insert the new view.
	*/
	public void addView(final int view, final String title, final Icon icon, final Component component, final String tip, final int index)
	{
		getTabbedPane().insertTab(title, icon, component, tip, index);	//add the component to the tabbed pane
		setViewComponent(view, component);	//associate that component with the view
//G***del if not needed		addModifyListeners(component);	//listen for any modifications of the view component TODO remove when a modifiable tabbed pane is created
	}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public TabbedViewPanel(final M model)
	{
		this(model, true); //initialize the panel
	}

	/**Initialize constructor.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public TabbedViewPanel(final M model, final boolean initialize)
	{
		super(new BasicGridBagLayout(), model, false);	//construct the parent class with a grid bag layout manager, but don't initialize the panel
		tabbedPane=new JTabbedPane();	//create the center tabbed pane
		viewComponentMap=new DecoratorReverseMap(new HashMap(), new HashMap());	//create the reverse lookup map of components
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeUI()
	{
		getTabbedPane().setTabPlacement(JTabbedPane.BOTTOM);	//put the tabs on the bottom
		add(getTabbedPane(), BorderLayout.CENTER);	//add the tabbed pane to the center of the panel
		getTabbedPane().addChangeListener(new ChangeListener()	//listen for tab changes
				{
					public void stateChanged(final ChangeEvent changeEvent)	//if the selected tab changes
					{
						updateDataView(getTabbedPane().getSelectedComponent());	//update the view accordingly
					}
				});
		super.initializeUI(); //do the default UI initialization, which will set the default model and initialize the default view
		updateComponent(getModelView());	//make sure the selected component matches the view
	}

	/**Determines whether it is appropriate under the circumstances to change
		the model view. This method may display any necessary dialog boxes in
		response to any error conditions before returning.
	<p>This version verifies only the current view component.</p>
	@param oldView The view before the change.
	@param newView The new view of the data
	@return <code>true</code> if the model view change should be allowed, else
		<code>false</code>.
	*/
	protected boolean canChangeModelView(final int oldView, final int newView)
	{
		final Component oldViewComponent=getViewComponent(oldView);	//get the component that represents the view we're changing from
		boolean verified=!(oldViewComponent instanceof Verifiable) || ((Verifiable)oldViewComponent).verify();	//verify the old view, if it can be verified
		if(verified)	//if the default verification succeeded
		{
			try
			{
				saveModel(oldView);	//store the model data that was being edited for this view, if any
			}
			catch(IOException ioException)	//if there were any problems saving the model
			{
				AbstractSwingApplication.displayApplicationError(this, ioException);	//display the error
				verified=false;	//show that the component didn't verify
			}
		}
		return verified;	//show whether or not we successfully verified the component
	}

	/**Called when the change in model view should be canceled.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void cancelModelViewChange(final int oldView, final int newView)
	{
		super.cancelModelViewChange(oldView, newView);	//perform the default functionality
		updateComponent(oldView);	//make sure the correct tab is shown for the old view, since we're canceling changing to the new view
	}

	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onModelViewChange(final int oldView, final int newView)
	{
		updateComponent(newView);	//make sure the correct tab is shown
		super.onModelViewChange(oldView, newView);	//do the default functionality of loading the model into the new view
	}

	/**Updates the view to reflect the currently selected tab.
	@param component The selected component in the tabbed pane.
	*/
	protected void updateDataView(final Component component)
	{
		final int view=getComponentView(component);	//get the view associated with this component
		if(view!=NO_MODEL_VIEW)	//if there is a view associated with this component
		{
			setModelView(view);	//update the view to match the selected tab
		}
	}

	/**Updates the selected tab to reflect the current view.
	If the correct tab is already selected, no action occurs.
	@param view The new view of the data to be represented by one of the tabs.
	*/
	protected void updateComponent(final int view)
	{
		final Component component=getViewComponent(view);	//get the component associated with this view
		if(component!=null && getTabbedPane().getSelectedComponent()!=component)	//if there is a component to show the view, and we're not already showing it
		{
			getTabbedPane().setSelectedComponent(component);	//select the tabbed pane that represents the view 
		}
	}

}