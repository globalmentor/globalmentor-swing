package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**A component sequence panel that produces its sequence components from items
	in a list model. (The list items are not necessarily components.)
@author Garret Wilson
*/
public abstract class AbstractListModelComponentSequencePanel extends AbstractComponentSequencePanel
{

	/**The list model from which components are produced, or <code>null</code> for no list.*/
	private ListModel listModel;

		/**@return The list model from which components are produced, or <code>null</code> for no list.*/
		public ListModel getListModel() {return listModel;}

		/**Changes the list the sequence of components represents.
		@param newListModel The list model from which components are produced, or
			<code>null</code> for no list.
		*/
		public void setListModel(final ListModel newListModel)
		{
			final ListModel oldListModel=listModel;	//get our old list model
			if(oldListModel!=newListModel)	//if the list model is really changing
			{
				if(oldListModel!=null)	//if we had a list model before
				{
					oldListModel.removeListDataListener(listDataListener);	//remove our list data listener
				}
				listModel=newListModel;	//update the list model
				if(newListModel!=null)	//if we have a new list model
				{
					newListModel.addListDataListener(listDataListener);	//listen for changes to the list
				}
				goStart();	//go to the beginning which, if we don't have a list, will switch to the default component
			}
		}

	/**Our anonymous listener that forwards events to our protected listener methods.*/
	protected final ListDataListener listDataListener;

	/**The current index in the list.*/
	private int index;

		/**@return The current index in the list.*/
		public int getIndex() {return index;}

		/**Sets the current index in the list.
		@param newIndex The new list index.
		*/
		protected void setIndex(final int newIndex) {index=newIndex;}

	/**Default constructor.*/
	public AbstractListModelComponentSequencePanel()
	{
		this(null, true); //construct and initialize the panel without a list
	}

	/**List model constructor.
	@param listModel The list the items of which the components in this
		sequence panel represent, or <code>null</code> for no list.
	*/
	public AbstractListModelComponentSequencePanel(final ListModel listModel)
	{
		this(listModel, true); //construct and initialize the panel
	}

	/**Constructor that allows optional initialization.
	@param listModel The list the items of which the components in this
		sequence panel represent, or <code>null</code> for no list
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractListModelComponentSequencePanel(final ListModel listModel, final boolean initialize)
	{
		this(listModel, true, true, initialize);	//consruct the panel with a toolbar and statusbar
	}

	/**Constructor that allows optional initialization.
	@param listModel The list the items of which the components in this
		sequence panel represent.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractListModelComponentSequencePanel(final ListModel listModel, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel, but don't initialize
		listDataListener=new ListDataListener()	//create an anonymous nested list data listener that forwards events to our local methods 
				{
					public void intervalAdded(ListDataEvent e) {onIntervalAdded(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
					public void intervalRemoved(ListDataEvent e) {onIntervalRemoved(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
					public void contentsChanged(ListDataEvent e) {onContentsChanged(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
				};
		this.listModel=null;	//start out with no list model
		setListModel(listModel);	//set the list model we were given
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes the user interface.*/
/*G***del if not needed
	protected void initializeUI()
	{
		super.initializeUI();	//do the default initialization
		setContentComponent(getFirstComponent());	//start with the first component in the sequence
	}
*/

	/**Goes to the indicated step in the sequence.
	@see #setIndex
	*/
	public void go(final int index)
	{
		final Component component=getIndexedComponent(index);	//get the component for this index
		setContentComponent(component!=null ? component : getDefaultComponent());	//change to the component, if there is one
		setIndex(index);	//show that we're at the given index TODO make sure the index is within our range				
		updateStatus();	//update the status
	}

	/**Goes to the first step in the sequence.*/
	protected void first()
	{
		super.first();	//go to the first component
		setIndex(0);	//show that we're at the first index				
	}

	/**Goes to the previous component in the sequence. If there is no previous
		component, no action occurs.
	*/
	protected void previous()
	{
		super.previous();	//go to the previous component
		setIndex(getIndex()-1);	//show that we're at the previous index
	}
	
	/**Goes to the next component in the sequence.*/
	protected void next()
	{
		super.next();	//go to the next component
		setIndex(getIndex()+1);	//show that we're at the next index
	}

	/**Determines the component to be displayed at the given step of the sequence.
	@param index The zero-based index of the step in the sequence.
	@return The component to be displayed at the given step of the sequence. 
	*/
	protected Component getIndexedComponent(final int index)
	{
		//TODO maybe bring some of this index stuff up the hierarchy
		//TODO maybe call this method from other methods to avoid redundancy
		//TODO decide whether we should throw an ArrayOutOfBounds exception
		return getListModel()!=null && index>=0 && index<getListModel().getSize() ? getComponent(getListModel().getElementAt(index)) : null;	//get the component for the requested item
	}


	/**@return The first component to be displayed in the sequence.*/
	protected Component getFirstComponent()
	{
		return getListModel()!=null && getListModel().getSize()>0 ? getComponent(getListModel().getElementAt(0)) : null;	//get the component for the first item
	}

	/**@return <code>true</code> if there is a next component after the current one.*/
	protected boolean hasNext()
	{
		return getListModel()!=null ? getIndex()<getListModel().getSize()-1 : false;	//we have another item if we're not at the last item in the list
	}

	/**@return The next component to be displayed in the sequence.*/
	protected Component getNextComponent()
	{
		return getListModel()!=null ? getComponent(getListModel().getElementAt(getIndex()+1)) : null;	//get the component for the next item		
	}

	/**@return <code>true</code> if there is a previous component before the current one.*/
	protected boolean hasPrevious()
	{
		return getIndex()>0;	//we have another item if we're not at the first item in the list		
	}

	/**@return The previous component to be displayed in the sequence.*/
	protected Component getPreviousComponent()
	{
		return getListModel()!=null ? getComponent(getListModel().getElementAt(getIndex()-1)) : null;	//get the component for the previous item		
	}

	/**Returns a component appropriate for representing the given object from
		the list.
	@param object An object in the list.
	@return A component appropriate for representing the object.
	*/
	protected abstract Component getComponent(final Object object);

	/**Sent after the indices in the index0, index1
		interval have been inserted in the data model.
		The new interval includes both index0 and index1.
	@param index0 The lower index, inclusive, of the range.
	@param index1 The upper index, inclusive, of the range.
	*/
	protected void onIntervalAdded(final int index0, final int index1)
	{
/*G***fix
		if(getIndex()<0 && index0>=0)	//if a valid index was added, and we hadn't started the sequence before TODO only move if we've already started the sequence, or wanted to---but how would we know that?
		{
			goFirst();	//
		}
		else	//if the change doesn't affect our index
*/
		{
			updateStatus();	//update the status
		}
	}
    
	/**Sent after the indices in the index0, index1 interval
		have been removed from the data model.  The interval 
		includes both index0 and index1.
	@param index0 The lower index, inclusive, of the range.
	@param index1 The upper index, inclusive, of the range.
	*/
	protected void onIntervalRemoved(final int index0, final int index1)
	{
		
		updateStatus();	//update the status
	}

	/**Sent when the contents of the list has changed in a way 
		that's too complex to characterize with the previous 
		methods. For example, this is sent when an item has been
		replaced. Index0 and index1 bracket the change.
	@param index0 The lower index, inclusive, of the range.
	@param index1 The upper index, inclusive, of the range.
	*/
	protected void onContentsChanged(final int index0, final int index1)
	{
		
		updateStatus();	//update the status
	}

}
