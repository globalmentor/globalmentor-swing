package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.util.Editable;
import com.garretwilson.util.Modifiable;

/**A component sequence panel that produces its sequence components from items
	in a list model. (The list items are not necessarily components.)
<p>In order to create and edit items, an editor must be set (besides setting
	the list to be editable using <code>setEditable(true)</code>). Actual
	modification of the list will only be performed if the list uses a list model
	that implements either <code>javax.swing.DefaultListModel</code> or
	<code>java.util.List</code>.</p>
<p>Bound properties:</p>
<dl>
	<dt><code>Editable.EDITABLE_PROPERTY</code> (<code>Boolean</code>)</dt>
	<dd>Indicates the editable status has changed.</dd>
</dl>
@param <E> The type of elements contained in the list model.
@author Garret Wilson
*/
public abstract class AbstractListModelComponentSequencePanel<E> extends AbstractComponentSequencePanel implements Editable
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
				if(getEditStrategy()!=null)	//if we have an edit strategy
				{
					getEditStrategy().setListModel(newListModel);	//update the edit strategy's reference to the model
				}
				goStart();	//go to the beginning which, if we don't have a list, will switch to the default component
			}
		}

	/**The edit strategy for editing items in the list model, or <code>null</code> if there is no edit strategy.*/
	private ListModelEditStrategy editStrategy;

		/**@return The strategy for editing items in the list model, or <code>null</code> if there is no edit strategy.*/
		public ListModelEditStrategy getEditStrategy() {return editStrategy;}

		/**Sets the edit strategy object
		@param editStrategy The edit strategy for editing items in the list, or
			<code>null</code> if there is no edit strategy.
		*/
		public void setEditStrategy(final ListModelEditStrategy editStrategy) {this.editStrategy=editStrategy;}	//TODO make sure we unregister the old list strategy first by removing all listeners to the list

	/**Whether the items in the list can be edited.*/ 
	private boolean editable;

		/**@return Whether the items in the list can be edited.*/ 
		public boolean isEditable() {return editable;}

		/**Sets whether the items in the list can be edited.
		This defaults to <code>false</code>.
		This is a bound property.
		@param newEditable <code>true</code> if the list can be edited.
		*/
		public void setEditable(final boolean newEditable)
		{
			final boolean oldEditable=editable; //get the old value
			if(oldEditable!=newEditable)  //if the value is really changing
			{
				editable=newEditable; //update the value				
				firePropertyChange(EDITABLE_PROPERTY, new Boolean(oldEditable), new Boolean(newEditable));	//show that the property has changed
				updateStatus();	//update the status
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
		this(listModel, null, hasToolBar, hasStatusBar, initialize);	//construct the panel with no edit strategy
	}
	
	/**Constructor that allows optional initialization.
	@param listModel The list the items of which the components in this
		sequence panel represent.
	@param editStrategy The edit strategy for editing items in the list model,
		or <code>null</code> if this panel does not allow editing the list model.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractListModelComponentSequencePanel(final ListModel listModel, final ListModelEditStrategy editStrategy, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel, but don't initialize
		this.editStrategy=editStrategy;	//set the edit strategy
		listDataListener=new ListDataListener()	//create an anonymous nested list data listener that forwards events to our local methods 
				{
					public void intervalAdded(ListDataEvent e) {onIntervalAdded(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
					public void intervalRemoved(ListDataEvent e) {onIntervalRemoved(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
					public void contentsChanged(ListDataEvent e) {onContentsChanged(e.getIndex0(), e.getIndex1());}	//forward the event to our local method
				};
		this.listModel=null;	//start out with no list model
		setListModel(listModel);	//set the list model we were given
		setDistinctAdvance(true);	//use distinct buttons for advancing
		editable=false;	//default to not being editable
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
		actionManager.removeToolAction(getStartAction());	//remove the start action
		if(isEditable() && getEditStrategy()!=null)	//if this sequence panel is editable and there is an edit strategy TODO allow the actions to be changed if the panel becomes uneditable
		{
			actionManager.addToolAction(new ActionManager.SeparatorAction());
			actionManager.addToolAction(getEditStrategy().getAddAction());
			actionManager.addToolAction(getEditStrategy().getDeleteAction());
		}
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus(); //update the default actions
		getStartAction().setEnabled(getIndex()<0); //only allow starting if we haven't started, yet
		final boolean isEditable=isEditable();	//see whether the list can be edited
/*G***fix with edit strategy
		editSeparator.setVisible(isEditable);	//only show the edit separator if editing is allowed
		addButton.setVisible(isEditable);	//only show the add button if editing is allowed
		deleteButton.setVisible(isEditable);	//only show the delete button if editing is allowed
		deleteButton.setEnabled(getIndex()>=0);	//only enable the delete button if there is something selected to be deleted
		editButton.setVisible(isEditable);	//only show the edit button if editing is allowed
		editButton.setEnabled(getIndex()>=0);	//only enable the edit button if there is something selected to be edited
*/
	}

	/**Goes to the indicated step in the sequence.
	@see #setIndex
	*/
	public void go(final int index)
	{
		if(verifyCurrentComponent())	//if the current component verifies TODO check to see if we're actually changing, and that the given index is correct
		{
			final Component component=getIndexedComponent(index);	//get the component for this index
			setContentComponent(component!=null ? component : getDefaultComponent());	//change to the component, if there is one
			setIndex(index);	//show that we're at the given index TODO make sure the index is within our range				
			updateStatus();	//update the status
		}
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
	@exception ClassCastException if the element as the given index is not of the correct generic type <code>E</code>.
	*/
	protected Component getIndexedComponent(final int index)
	{
		//TODO maybe bring some of this index stuff up the hierarchy
		//TODO maybe call this method from other methods to avoid redundancy
		//TODO decide whether we should throw an ArrayOutOfBounds exception
		return getListModel()!=null && index>=0 && index<getListModel().getSize() ? getComponent((E)getListModel().getElementAt(index)) : null;	//get the component for the requested item
	}


	/**@return The first component to be displayed in the sequence.
	@exception ClassCastException if the element as the given index is not of the correct generic type <code>E</code>.
	*/
	protected Component getFirstComponent()
	{
		return getListModel()!=null && getListModel().getSize()>0 ? getComponent((E)getListModel().getElementAt(0)) : null;	//get the component for the first item
	}

	/**@return <code>true</code> if there is a next component after the current one.*/
	protected boolean hasNext()
	{
		return getListModel()!=null ? getIndex()<getListModel().getSize()-1 : false;	//we have another item if we're not at the last item in the list
	}

	/**@return The next component to be displayed in the sequence.
	@exception ClassCastException if the element as the given index is not of the correct generic type <code>E</code>.
	*/
	protected Component getNextComponent()
	{
		return getListModel()!=null ? getComponent((E)getListModel().getElementAt(getIndex()+1)) : null;	//get the component for the next item		
	}

	/**@return <code>true</code> if there is a previous component before the current one.*/
	protected boolean hasPrevious()
	{
		return getIndex()>0;	//we have another item if we're not at the first item in the list		
	}

	/**@return The previous component to be displayed in the sequence.
	@exception ClassCastException if the element as the given index is not of the correct generic type <code>E</code>.
	*/
	protected Component getPreviousComponent()
	{
		return getListModel()!=null ? getComponent((E)getListModel().getElementAt(getIndex()-1)) : null;	//get the component for the previous item		
	}

	/**Returns a component appropriate for representing the given object from
		the list.
	@param object An object in the list.
	@return A component appropriate for representing the object.
	*/
	protected abstract Component getComponent(final E object);

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
		if(index0<=getIndex())	//if the removed index affects our index
		{
			final int index;	//see which index to go to, now
			if(getIndex()<getListModel().getSize())	//if our current index is still OK
			{
				index=getIndex();	//keep our current index
			}
			else	//if our current index is now out of bounds
			{
				index=getListModel().getSize()-1;	//go to the last available index
			}
			go(index);	//go to appropriate index TODO switch to an updateComponent method, and probably make go(int) check to make sure the index is changing
		}
		else	//if the removed index didn't affect our index
		{
			updateStatus();	//update the status
		}
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
		if(getIndex()>=index0 && getIndex()<=index1)	//if the component at our current index was affected
		{
			go(getIndex());	//update the component at our index TODO switch to an updateComponent method, and probably make go(int) check to make sure the index is changing
		}
		else	//if nothing changed at our index 
		{
			updateStatus();	//update the status
		}
	}

	/**An abstract edit strategy that allows editing of the items in the list model.
	<p>This edit strategy correctly uses the sequence panel as a parent component
		and as a modifiable object.</p>
	@author Garret Wilson
	*/
	protected abstract class EditStrategy extends ListModelEditStrategy
	{
		/**Default constructor.*/
		public EditStrategy()
		{
			super(AbstractListModelComponentSequencePanel.this.getListModel(), AbstractListModelComponentSequencePanel.this, AbstractListModelComponentSequencePanel.this);	//construct the parent class with the list model
		}

		/**@return The currently selected index of the list model.*/
		protected int getLeadSelectionIndex()
		{
			return getIndex();	//return the current index
		}

		/**Sets the current main seletion index of the list model.
		@param index The new index to become the main selection.
		*/
		public void setLeadSelectionIndex(final int index)
		{
			go(index);	//go to the indicated index
		}
		
		/**@return The component acting as the parent for windows.*/
//G***del if not needed		protected Component getParentComponent() {return AbstractListModelComponentSequencePanel.this;}

		/**@return The object that represents the list model modification status.*/
//	G***del if not needed		protected Modifiable getModifiable() {return AbstractListModelComponentSequencePanel.this;}

		/**Edits an object from the list.
		<p>This version does nothing and simply returns the item, as it is assumed
			that the panel itself contains the edit view of the item.</p> 
		@param item The item to edit in the list.
		@return The object with the modifications from the edit, or
			<code>null</code> if the edits should not be accepted.
		*/
		protected Object editItem(final Object item)
		{
			return item;	//return the item so that it can be added
		}

	}

}
