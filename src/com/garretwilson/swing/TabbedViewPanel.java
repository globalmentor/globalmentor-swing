package com.garretwilson.swing;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.DataViewable;

/**Panel that allows multiple views of data to be displayed in separate tabs.
When the view value is changed, the appropriate tab is selected; when a new tab
	is selected, the view value is changed accordingly.
<p>Subclasses should set the correct default view after initializing the tabs.</p>
<p>Bound properties:</p>
<dl>
	<dt><code>DataViewable.DATA_VIEW_PROPERTY</code> (<code>Integer</code>)</dt>
	<dd>Indicates the data view has been changed.</dd>
</dl>
@author Garret Wilson
@see BasicPanel#getView
@see BasicPanel#setView
*/
public abstract class TabbedViewPanel extends ContentPanel implements DataViewable
{

	/**@return The center tabbed pane.*/
	protected JTabbedPane getTabbedPane() {return (JTabbedPane)getContentComponent();} 

	/**@return A value representing the supported data views ORed together.*/
	public abstract int getSupportedDataViews();

	/**Determines whether this object supports the data views.
	@param dataViews One or more <code>XXX_DATA_VIEW</code> constants
		ORed together.
	@return <code>true</code> if and only if this object kit supports all the
		indicated data views.
	*/
	public boolean isDataViewsSupported(final int dataViews)
	{
		return (getSupportedDataViews()&dataViews)==dataViews;	//see whether all the data views are supported
	}	

	/**The view of the data, such as <code>SUMMARY_DATA_VIEW</code>.*/
	private int dataView;

		/**@return The view of the data, such as <code>SUMMARY_DATA_VIEW</code>.*/
		public int getDataView() {return dataView;}

		/**Sets the view of the data.
		@param newView The view of the data, such as <code>SUMMARY_DATA_VIEW</code>.
		@exception IllegalArgumentException Thrown if the given view is not supported.
		*/
		public void setDataView(final int newView)
		{
			final int oldView=dataView; //get the old value
			if(oldView!=newView)  //if the value is really changing
			{
				if(!isDataViewsSupported(newView))	//if the new data view isn't supported
					throw new IllegalArgumentException("Unsupported data view "+newView);
				dataView=newView; //update the value					
				updateComponent(newView);	//make sure the selected component matches the view; this will eventually re-enter this method before it returns, but as we've already changed the data view no action will occur
				firePropertyChange(DATA_VIEW_PROPERTY, new Integer(oldView), new Integer(newView));	//show that the property has changed
			}
		}

	/**The map of components keyed to views.*/
	protected final Map viewComponentMap;

		/**Retrieves a component tab for a given view.
		@param view The view for which a tab should be returned; one of the
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		<code>DataViewable.XXX_DATA_VIEW</code> values), or <code>DataViewable.NO_DATA_VIEW</code>
			if there is no view associated with the given component
		*/
		protected int getComponentView(final Component component)
		{
			final Integer viewInteger=(Integer)componentViewMap.get(component);	//get the view, if there is one, associated with the component
			return viewInteger!=null ? viewInteger.intValue() : NO_DATA_VIEW;	//return the view value, or NO_DATA_VIEW if there is no view
		}

	/**Associates a component with a particular view. The component should be
		one that is to appear on the tabbed pane, although this method does not
		itself add the component to the tabbed pane.
	<p>The view and component have a one-to-one relationship. Associating
		multiple components with a view or multiples views with a component will
		likely result in errant functionality.</p>
	@param view The view to associate with a component; one of the
		<code>DataViewable.XXX_DATA_VIEW</code> values.
	@param component The component in the tabbed pane that represents the given
		view.
	*/
	private void setViewComponent(final int view, final Component component)
	{
		final Integer viewObject=new Integer(view);	//create an integer object to represent the view
		viewComponentMap.put(viewObject, component);	//associate the component with the view TODO maybe create a ReverseLookupHashMap that updates internally its own reverse lookup table
		componentViewMap.put(component, viewObject);	//associate the view with the component		
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	<p>An appropriate default icon is used for the given view if possible.
		@param view The view to associate with a component; one of the
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
			case TREE_DATA_VIEW:
				icon=IconResources.getIcon(IconResources.TREE_ICON_FILENAME);
				break;
			case GRAPH_DATA_VIEW:
				//TODO add icon for graph data view
				break;
			case WYSIWYG_DATA_VIEW:
				icon=IconResources.getIcon(IconResources.DOCUMENT_RICH_CONTENT_ICON_FILENAME);
				break;
			case SUMMARY_DATA_VIEW:
				//TODO add icon for summary data view
				break;
			case SOURCE_DATA_VIEW:
				icon=IconResources.getIcon(IconResources.MARKUP_ICON_FILENAME);
				break;
		}
		addView(view, title, icon, component, tip, index);	//add the view with the default icon, if we found one
	}

	/**Adds a tab to the tabbed pane representing a view of the data.
	@param view The view to associate with a component; one of the
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		<code>DataViewable.XXX_DATA_VIEW</code> values.
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
		dataView=NO_DATA_VIEW;	//default to no valid view
		viewComponentMap=new HashMap();	//create the map of components
		componentViewMap=new HashMap();	//create teh map of views
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.
	<p>This version sets the data view to the default.</p>
	*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		addPropertyChangeListener(DATA_VIEW_PROPERTY, new ViewChangeListener());	//listen for changes in the view and call the view change method in response
		getTabbedPane().addChangeListener(new ChangeListener()	//listen for tab changes
				{
					public void stateChanged(final ChangeEvent changeEvent)	//if the selected tab changes
					{
						updateDataView(getTabbedPane().getSelectedComponent());	//update the view accordingly
					}
				});
		setDataView(getDefaultDataView());	//set the default data view
		updateComponent(getDataView());	//make sure the selected component matches the view
	}

	/**Updates the view to reflect the currently selected tab.
	@param component The selected component in the tabbed pane.
	*/
	protected void updateDataView(final Component component)
	{
		final int view=getComponentView(component);	//get the view associated with this component
		if(view!=NO_DATA_VIEW)	//if there is a view associated with this component
		{
			setDataView(view);	//update the view to match the selected tab
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

	/**Indicates that the view of the data has changed.
	<p>This method is guaranteed to only be caused once
		for each view change sequence; the view can therefore safely be changed
		from within this method without re-entry problems.
		This is useful for undoing a view change by changing the view back to
		its old value.</p>
	<p>Derived classes should call this version if it is overridden.
		Derived classes must not call this method from other methods unless they
		guarantee non-re-entrancy.</p>
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onDataViewChanged(final int oldView, final int newView)
	{
/*G***del; testing
		for(int i=getTabbedPane().getTabCount()-1; i>=0; --i)	//G***testing
		{
			getTabbedPane().setTitleAt(i, "");	//G***testing
			getTabbedPane().setIconAt(i, null);	//G***testing
		}
*/
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
					onDataViewChanged(((Integer)viewChangeEvent.getOldValue()).intValue(), ((Integer)viewChangeEvent.getNewValue()).intValue());
				}
				finally
				{
					isViewChanging=false;	//show that we're finished changing the tab
				}
			}
		}										
	}

}