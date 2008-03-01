package com.garretwilson.swing;

import java.awt.Component;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.util.Modifiable;

/**A plug-in strategy class for editing items from a list model.
<p>Actual modification of the list will only be performed if the list model
	implements either <code>javax.swing.DefaultListModel</code> or
	<code>java.util.List</code>.</p>
<p>If a <code>Modifiable</code> object is given, its modified
	status will be set when an item is added, edited, or deleted.</p>
<p>For best results, derived classes should override
	<code>getParentComponent()</code>, along with <code>getModifiable()</code>
	if needed.</p>
@param <E> The type of element in the list model.
@author Garret Wilson
*/
public abstract class ListModelEditStrategy<E>
{

	/**The action for adding an item.*/
	private final Action addAction;

		/**@return The action for adding an item.*/
		public Action getAddAction() {return addAction;}

	/**The action for deleting an item.*/
	private final Action deleteAction;

		/**@return The action for deleting an item.*/
		public Action getDeleteAction() {return deleteAction;}

	/**The action for editing an item.*/
	private final Action editAction;

		/**@return The action for editing an item.*/
		public Action getEditAction() {return editAction;}

	/**The action for moving an item up.*/
	private final Action moveUpAction;

		/**@return The action for moving an item up.*/
		public Action getMoveUpAction() {return moveUpAction;}

	/**The action for moving an item down.*/
	private final Action moveDownAction;

		/**@return The action for moving an item down.*/
		public Action getMoveDownAction() {return moveDownAction;}
		
	/**The listener that updates the status in response to list data changes.*/
	protected final ListDataListener updateStatusListDataListener;

	/**The list model this strategy edits.*/
	private ListModel listModel;

		/**@return The list model this strategy edits.*/
		protected ListModel getListModel() {return listModel;}

		/**Sets the list model this strategy edits.
		@param newListModel The list model this strategy edits.
		*/
		protected void setListModel(final ListModel newListModel)
		{
			final ListModel oldListModel=getListModel();	//get our old list model
			if(oldListModel!=newListModel)	//if the list model is really changing
			{
				if(oldListModel!=null)	//if we had an old list model
				{
					oldListModel.removeListDataListener(updateStatusListDataListener);	//remove our list data listener
				}
				listModel=newListModel;	//actually change the list model
				if(newListModel!=null)	//if we have a new list model
				{
					newListModel.addListDataListener(updateStatusListDataListener);	//add our list data listener
				}
			}
		}

	/**The component acting as the parent for windows.*/
	private final Component parentComponent;

		/**@return The component acting as the parent for windows.*/
		protected Component getParentComponent() {return parentComponent;}

	/**The object that represents the list model modification status,
		or <code>null</code> if modification status should not be indicated.
	*/
	private final Modifiable modifiable;

		/**@return The object that represents the list model modification status,
			or <code>null</code> if modification status should not be indicated.
		*/
		protected Modifiable getModifiable() {return modifiable;}

	/**List model constructor.
	@param listModel The list model this strategy edits.
	*/
	public ListModelEditStrategy(final ListModel listModel)
	{
		this(listModel, null);	//construct the strategy with no parent component
	}

	/**List model and parent component constructor.
	@param listModel The list model this strategy edits.
	@param parentComponent The component acting as the parent for windows.
	*/
	public ListModelEditStrategy(final ListModel listModel, final Component parentComponent)
	{
		this(listModel, parentComponent, null);	//construct the strategy with no modifiable object
	}

	/**Parent component , and modifiable constructor.
	@param parentComponent The component acting as the parent for windows.
	@param modifiable The object that represents the list model modification
		status, or <code>null</code> if modification status should not be indicated.
	*/
	public ListModelEditStrategy(final Component parentComponent, final Modifiable modifiable)
	{
		this(null, parentComponent, modifiable);	//construct the strategy with no list model
	}

	/**List model, parent component, and modifiable constructor.
	@param listModel The list model this strategy edits.
	@param parentComponent The component acting as the parent for windows.
	@param modifiable The object that represents the list model modification
		status, or <code>null</code> if modification status should not be indicated.
	*/
	public ListModelEditStrategy(final ListModel listModel, final Component parentComponent, final Modifiable modifiable)
	{
		updateStatusListDataListener=new ListDataListener()	//create a new list data listener that will do nothing but update the status in response to changes
				{
					public void intervalAdded(final ListDataEvent listDataEvent) {updateStatus();}
					public void intervalRemoved(final ListDataEvent listDataEvent) {updateStatus();}
					public void contentsChanged(final ListDataEvent listDataEvent) {updateStatus();}
				};
		this.listModel=null;	//start out with no list model
		setListModel(listModel);	//set the list model
		this.parentComponent=parentComponent;	//save the parent component
		this.modifiable=modifiable;	//save the modifiable object, if any
		addAction=new AddAction();
		deleteAction=new DeleteAction();
		editAction=new EditAction();
		moveUpAction=new MoveUpAction();
		moveDownAction=new MoveDownAction();
		updateStatus();	//update the initial status
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		final int selectedIndex=getLeadSelectionIndex();	//get the selected index
		final int length=getListModel()!=null ? getListModel().getSize() : 0;	//see how many items are in the list model
		getDeleteAction().setEnabled(selectedIndex>=0);	//only enable the delete action if there is something selected to be deleted
		getEditAction().setEnabled(selectedIndex>=0);	//only enable the edit action if there is something selected to be edited
		getMoveUpAction().setEnabled(selectedIndex>0);	//only enable the move up action if there is something selected that can be moved up
		getMoveDownAction().setEnabled(selectedIndex>=0 && selectedIndex<length-1);	//only enable the move down action if there is something selected that can be moved down
	}

	/**Creates a new item and, if editing is successful, adds it to the list.*/
	public void add()
	{
		try
		{
			final E item=createItem();	//create a new item
			final E newItem=editItem(item);	//edit the item
			if(newItem!=null)	//if the user accepted the changes
			{
				final ListModel listModel=getListModel();	//get the list model
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).addElement(newItem);	//add the item to the default list model
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List<? super E>)listModel).add(newItem);	//add the item to the list
				}
				if(getModifiable()!=null)	//if we have a modifiable object
				{					
					getModifiable().setModified(true);	//adding an item modifies the object
				}
			}
		}
		catch(InstantiationException e)
		{
			SwingApplication.displayApplicationError(getParentComponent(), e);	//show the error
		}
		catch(IllegalAccessException e)
		{
			SwingApplication.displayApplicationError(getParentComponent(), e);	//show the error
		}
	}

	/**Deletes the currently selected item in the list.*/
	public void delete()
	{
		delete(getLeadSelectionIndex());	//delete the selected index
	}

	/**Deletes an item in the list.
	@param index The index of the item to delete.
 	@exception ClassCastException if the element at the given index is not of the correct generic type <code>E</code>.
	*/
	public void delete(final int index)
	{
		final ListModel listModel=getListModel();	//get the list model
		if(index>=0 && index<listModel.getSize())	//if a valid index is requested
		{
			final E item=(E)listModel.getElementAt(index);	//get the currently selected item
				//ask the user for confimation to delete the item
			if(BasicOptionPane.showConfirmDialog(getParentComponent(), "Are you sure you want to delete the item, \""+item+"\"?", "Confirm delete", BasicOptionPane.OK_CANCEL_OPTION)==BasicOptionPane.OK_OPTION)	//G***i18n
			{
				deleteItem(item);	//delete the item before removing it from the list
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).remove(index);	//remove the item from the default list model
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List<? super E>)listModel).remove(index);	//remove the item from the list
				}
				if(getModifiable()!=null)	//if we have a modifiable object
				{					
					getModifiable().setModified(true);	//deleting an item modifies the object
				}
			}
		}
	}

	/**Edits the currently selected item in the list.
	@exception ClassCastException if the element at the current index is not of the correct generic type <code>E</code>.
	*/
	public void edit()
	{
		edit(getLeadSelectionIndex());	//edit the selected index
	}

	/**Edits an item in the list.
	@param index The index of the item to delete. 
	@exception ClassCastException if the element at the given index is not of the correct generic type <code>E</code>.
	*/
	public void edit(final int index)
	{
		final ListModel listModel=getListModel();	//get the list model
		if(index>=0 && index<listModel.getSize())	//if a valid index is indicated
		{
			final E newItem=editItem((E)listModel.getElementAt(index));	//edit the selected item
			if(newItem!=null)	//if the edit went successfully
			{
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).set(index, newItem);	//update the default list model item
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List<? super E>)listModel).set(index, newItem);	//update the list
				}
				if(getModifiable()!=null)	//if we have a modifiable object
				{					
					getModifiable().setModified(true);	//editing an item modifies the object
				}
			}
		}
	}

	/**Moves the currently selected item up one index in the list.*/
	public void moveUp()
	{
		moveUp(getLeadSelectionIndex());	//move up the selected index
	}

	/**Moves an item up in the list.
	 @param index The index of the item to move. 
	*/
	public void moveUp(final int index)
	{
		if(index>0 && index<getListModel().getSize())	//if a valid index is indicated
		{
			move(index, index-1);	//move the item up one index
		}
	}

	/**Moves the currently selected item down one index in the list.*/
	public void moveDown()
	{
		moveDown(getLeadSelectionIndex());	//move down the selected index
	}

	/**Moves an item down in the list.
	 @param index The index of the item to move. 
	 */
	public void moveDown(final int index)
	{
		if(index>=0 && index<getListModel().getSize()-1)	//if a valid index is indicated
		{
			move(index, index+1);	//move the item down one index
		}
	}
	
	/**Moves an item in the list.
	The lead selection is moved along with the item if appropriate.
	@param oldIndex The index of the item to move. 
	@param newIndex The new index to which to move the item.
	@exception ClassCastException if the element at the given index is not of the correct generic type <code>E</code>.
	*/
	protected void move(final int oldIndex, final int newIndex)
	{
		final int oldLeadSelectionIndex=getLeadSelectionIndex();	//get the current lead selection index
		final ListModel listModel=getListModel();	//get the list model
		final E item=(E)listModel.getElementAt(oldIndex);	//get the element at this index
		if(listModel instanceof DefaultListModel)	//if the list model is a default list model
		{
			((DefaultListModel)listModel).remove(oldIndex);	//remove the item from its old index
			((DefaultListModel)listModel).insertElementAt(item, newIndex);	//insert the item at its new index
		}
		else if(listModel instanceof List)	//if the list model implements the list interface
		{
			((List<? super E>)listModel).remove(oldIndex);	//remove the item from its old index
			((List<? super E>)listModel).add(newIndex, item);	//insert the item at its new index
		}
		if(getModifiable()!=null)	//if we have a modifiable object
		{					
			getModifiable().setModified(true);	//editing an item modifies the object
		}
		if(oldLeadSelectionIndex==oldIndex)	//if the old index had thelead selection
		{
			setLeadSelectionIndex(newIndex);	//move the lead selection index along with the item
		}
	}
	
	/**@return The current main selection index of the list model.*/
	protected abstract int getLeadSelectionIndex();

	/**Sets the current main seletion index of the list model.
	@param index The new index to become the main selection.
	*/
	protected abstract void setLeadSelectionIndex(final int index);
	
	/**Creates a new default object to be edited.
	@return The new default object.
	@exception IllegalAccessException Thrown if the class or its nullary 
		constructor is not accessible.
	@exception InstantiationException Thrown if a class represents an abstract
		class, an interface, an array class, a primitive type, or void;
		or if the class has no nullary constructor; or if the instantiation fails
		for some other reason.
	*/
	protected abstract E createItem() throws InstantiationException, IllegalAccessException;

	/**Edits an object from the list.
	@param item The item to edit in the list.
	@return The object with the modifications from the edit, or
		<code>null</code> if the edits should not be accepted.
	*/
	protected abstract E editItem(final E item);

	/**Deletes an object in the list.
	This is called so that any physical object backing may be removed, if
	necessary. This method must not remove the item from the list.
	@param item The item to delete in the list.
	*/
	protected void deleteItem(final E item) {}

	/**Action for adding an item to the list.*/
	protected class AddAction extends AbstractAction
	{
		/**Default constructor.*/
		public AddAction()
		{
			super("Add");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Add item");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Add an item to the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.DOCUMENT_NEW_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			add();	//add an item
		}
	}

	/**Action for deleting an item from the list.*/
	protected class DeleteAction extends AbstractAction
	{
		/**Default constructor.*/
		public DeleteAction()
		{
			super("Delete");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Delete item");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Delete an item from the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.DELETE_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			delete();	//delete the item
		}
	}

	/**Action for editing an item in the list.*/
	class EditAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditAction()
		{
			super("Edit");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit item");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit the selected item.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EDIT_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			edit();	//select all items
		}
	}

	/**Action for moving an item up in the list.*/
	protected class MoveUpAction extends AbstractAction
	{
		/**Default constructor.*/
		public MoveUpAction()
		{
			super("Move Up");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Move up");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Move the selected item up in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.MOVE_UP_ICON_FILENAME)); //load the correct icon
		}
		
		/**Called when the action should be performed.
		 @param actionEvent The event causing the action.
		 */
		public void actionPerformed(final ActionEvent actionEvent)
		{
			moveUp();	//move the item up
		}
	}

	/**Action for moving an item down in the list.*/
	protected class MoveDownAction extends AbstractAction
	{
		/**Default constructor.*/
		public MoveDownAction()
		{
			super("Move Down");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Move down");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Move the selected item down in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.MOVE_DOWN_ICON_FILENAME)); //load the correct icon
		}
		
		/**Called when the action should be performed.
		 @param actionEvent The event causing the action.
		 */
		public void actionPerformed(final ActionEvent actionEvent)
		{
			moveDown();	//move the item down
		}
	}
	
}
