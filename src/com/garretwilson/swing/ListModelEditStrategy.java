package com.garretwilson.swing;

import java.awt.Component;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.Modifiable;

/**A plug-in strategy class for editing items from a list model.
<p>Actual modification of the list will only be performed if the list model
	implements either <code>javax.swing.DefaultListModel</code> or
	<code>java.util.List</code>.</p>
<p>If a <code>Modifiable</code> object is given, its modified
	status will be set when an item is added, edited, or deleted.</p> 
@author Garret Wilson
*/
public abstract class ListModelEditStrategy //TODO use generics when they are available
{

	/**The action for adding an item.*/
	private final Action addAction;

		/**@return The action for adding an item.*/
		protected Action getAddAction() {return addAction;}

	/**The action for deleting an item.*/
	private final Action deleteAction;

		/**@return The action for deleting an item.*/
		protected Action getDeleteAction() {return deleteAction;}

	/**The action for editing an item.*/
	private final Action editAction;

		/**@return The action for editing an item.*/
		protected Action getEditAction() {return editAction;}

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

	/**List model and parent component constructor.
	@param listModel The list model this strategy edits.
	@param parentComponent The component acting as the parent for windows.
	*/
	public ListModelEditStrategy(final ListModel listModel, final Component parentComponent)
	{
		this(listModel, parentComponent, null);	//construct the strategy with no modifiable object
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
		updateStatus();	//update the initial status
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		final int selectedIndex=getSelectedIndex();	//get the selected index
		getDeleteAction().setEnabled(selectedIndex>=0);	//only enable the delete action if there is something selected to be deleted
		getEditAction().setEnabled(selectedIndex>=0);	//only enable the edit action if there is something selected to be edited
	}

	/**Creates a new item and, if editing is successful, adds it to the list.*/
	public void add()
	{
		try
		{
			final Object item=createItem();	//create a new item
			final Object newItem=editItem(item);	//edit the item
			if(newItem!=null)	//if the user accepted the changes
			{
				final ListModel listModel=getListModel();	//get the list model
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).addElement(newItem);	//add the item to the default list model
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List)listModel).add(newItem);	//add the item to the list
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
		delete(getSelectedIndex());	//delete the selected index
	}

	/**Deletes an item in the list.
	@param index The index of the item to delete. 
	*/
	public void delete(final int index)
	{
		if(index>=0)	//if a valid index is requested
		{
			final ListModel listModel=getListModel();	//get the list model
			final Object item=listModel.getElementAt(index);	//get the currently selected item
				//ask the user for confimation to delete the item
			if(OptionPane.showConfirmDialog(getParentComponent(), "Are you sure you want to delete the item, \""+item+"\"?", "Confirm delete", OptionPane.OK_CANCEL_OPTION)==OptionPane.OK_OPTION)	//G***i18n
			{
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).remove(index);	//remove the item from the default list model
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List)listModel).remove(index);	//remove the item from the list
				}
				if(getModifiable()!=null)	//if we have a modifiable object
				{					
					getModifiable().setModified(true);	//deleting an item modifies the object
				}
			}
		}
	}

	/**Edits the currently selected item in the list.*/
	public void edit()
	{
		edit(getSelectedIndex());	//edit the selected index
	}

	/**Edits the currently selected item in the list.
	@param index The index of the item to delete. 
	*/
	public void edit(final int index)
	{
		if(index>=0)	//if a valid index is selected
		{
			final ListModel listModel=getListModel();	//get the list model
			final Object newItem=editItem(listModel.getElementAt(index));	//edit the selected item
			if(newItem!=null)	//if the edit went successfully
			{
				if(listModel instanceof DefaultListModel)	//if the list model is a default list model
				{
					((DefaultListModel)listModel).set(index, newItem);	//update the default list model item
				}
				else if(listModel instanceof List)	//if the list model implements the list interface
				{
					((List)listModel).set(index, newItem);	//update the list
				}
				if(getModifiable()!=null)	//if we have a modifiable object
				{					
					getModifiable().setModified(true);	//editing an item modifies the object
				}
			}
		}
	}

	/**@return The currently selected index of the list model.*/
	protected abstract int getSelectedIndex();

	/**Creates a new default object to be edited.
	@return The new default object.
	@exception IllegalAccessException Thrown if the class or its nullary 
		constructor is not accessible.
	@exception InstantiationException Thrown if a class represents an abstract
		class, an interface, an array class, a primitive type, or void;
		or if the class has no nullary constructor; or if the instantiation fails
		for some other reason.
	*/
	protected abstract Object createItem() throws InstantiationException, IllegalAccessException;

	/**Edits an object from the list.
	@param parentComponent The component to use as a parent for any editing
		components.
	@param item The item to edit in the list.
	@return The object with the modifications from the edit, or
		<code>null</code> if the edits should not be accepted.
	*/
	protected abstract Object editItem(final Object item);

	/**Action for adding an item to the list.*/
	class AddAction extends AbstractAction
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
	class DeleteAction extends AbstractAction
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

}
