package com.garretwilson.swing;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**Panel that allows multiple views of data to be displayed in separate tabs.
When the view value is changed, the appropriate tab is selected; when a new tab
	is selected, the view value is changed accordingly.
@author Garret Wilson
@see BasicPanel#getView
@see BasicPanel#setView
*/
public class TabbedViewPanel extends ContentPanel
{

	/**@return The center tabbed pane.*/
	protected JTabbedPane getTabbedPane() {return (JTabbedPane)getContentComponent();} 

	/**The map of components keyed to views.*/
	protected final Map viewComponentMap;

		/**Retrieves a component tab for a given view.
		@param view The view for which a tab should be returned; one of the
		<code>BasicPanel.XXX_VIEW</code> values.
		@return The component that represents the given view, or <code>null</code>
			if no tab component has been associated with the given view.
		*/
		protected Component getViewComponent(final int view)
		{
			return (Component)viewComponentMap.get(new Integer(view));	//return the component, if there is one, associated with the view
		}

	/**The map of views keyed to components.*/
	protected final Map componentViewMap;

		/**Retrieves a view corresponding to a component tab.
		@param component The component that represents a view.
		@return The view represented by the given component (one of the
		<code>BasicPanel.XXX_VIEW</code> values), or <code>BasicPanel.NO_VIEW</code>
			if there is no view associated with the given component
		*/
		protected int getComponentView(final Component component)
		{
			final Integer viewInteger=(Integer)componentViewMap.get(component);	//get the view, if there is one, associated with the component
			return viewInteger!=null ? viewInteger.intValue() : NO_VIEW;	//return the view value, or NO_VIEW if there is no view
		}

	/**Associates a component with a particular view. The component should be
		one that is to appear on the tabbed pane, although this method does not
		itself add the component to the tabbed pane.
	<p>The view and component have a one-to-one relationship. Associating
		multiple components with a view or multiples views with a component will
		likely result in errant functionality.</p>
	@param view The view to associate with a component; one of the
		<code>BasicPanel.XXX_VIEW</code> values.
	@param component The component in the tabbed pane that represents the given
		view.
	*/
	public void setViewComponent(final int view, final Component component)
	{
		final Integer viewObject=new Integer(view);	//create an integer object to represent the view
		viewComponentMap.put(viewObject, component);	//associate the component with the view TODO maybe create a ReverseLookupHashMap that updates internally its own reverse lookup table
		componentViewMap.put(component, viewObject);	//associate the view with the component		
	}

	/**Default constructor.*/
	public TabbedViewPanel()
	{
		this(true); //initialize the panel
	}

	/**Initialize constructor.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public TabbedViewPanel(final boolean initialize)
	{
		super(new JTabbedPane(), false);	//construct the parent class with a tabbed pane as a content component, but don't initialize the panel
		viewComponentMap=new HashMap();	//create the map of components
		componentViewMap=new HashMap();	//create teh map of views
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		addPropertyChangeListener(VIEW_PROPERTY, new ViewChangeListener());	//listen for changes in the view and call the view change method in response
		getTabbedPane().addChangeListener(new ChangeListener()	//listen for tab changes
				{
					public void stateChanged(final ChangeEvent changeEvent)	//if the selected tab changes
					{
						updateView(getTabbedPane().getSelectedComponent());	//update the view accordingly
					}
				});
		updateComponent(getView());	//make sure the selected component matches the view
	}

	/**Updates the view to reflect the currently selected tab.
	@param component The selected component in the tabbed pane.
	*/
	protected void updateView(final Component component)
	{
		final int view=getComponentView(component);	//get the view associated with this component
		if(view!=NO_VIEW)	//if there is a view associated with this component
		{
			setView(view);	//update the view to match the selected tab
		}
	}

	/**Updates the selected tab to reflect the current view.
	@param view The new view of the data to be represented by one of the tabs.
	*/
	protected void updateComponent(final int view)
	{
		final Component component=getViewComponent(view);	//get the component associated with this view
		if(component!=null)	//if there is a component to show the view
		{
			getTabbedPane().setSelectedComponent(component);	//select the tabbed pane that represents the view 
		}
	}

	/**Indicates that the view of the data has changed.
	<p>This method is guaranteed to only be caused once
		for each view change sequence; the view can therefore safely be changed
		from within this method without re-entry problems.
		This is useful for undoing a view change by changing the view back to
		its old value.</p>
	<p>Derived classes must not call this method unless they guarantee
		non-re-entrancy.</p>
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onViewChanged(final int oldView, final int newView)
	{		
	}

	/**The class that listens for view changes and calls the view change method
		in response.
	@author Garret Wilson
	@see TabbedViewPanel#onViewChanged
	*/
	protected class ViewChangeListener implements PropertyChangeListener
	{
		/**The flag for detecting when we're processing a view change to avoid re-entry.*/
		private boolean isViewChanging=false;

		/**Called when the view is changing.*/
		public void propertyChange(PropertyChangeEvent viewChangeEvent)
		{
			if(!isViewChanging)	//if we're not already processing a view change event
			{
				try
				{
					isViewChanging=true;	//set the flag to avoid reentry
						//show that the view changed
					onViewChanged(((Integer)viewChangeEvent.getOldValue()).intValue(), ((Integer)viewChangeEvent.getNewValue()).intValue());
				}
				finally
				{
					isViewChanging=false;	//show that we're finished changing the tab
				}
			}
		}										
	}

}