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

package com.garretwilson.swing;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.model.Modifiable;

/**A plug-in strategy class for editing items from a list component.
@param <E> The type of each element in the list.
@author Garret Wilson
*/
public abstract class ListEditStrategy<E> extends ListModelEditStrategy<E>
{

	/**The action for selecting all items.*/
	private final Action selectAllAction;

		/**@return The action for selecting all items.*/
		public Action getSelectAllAction() {return selectAllAction;}

	/**The action for selecting no items.*/
	private final Action selectNoneAction;

		/**@return The action for selecting no items.*/
		public Action getSelectNoneAction() {return selectNoneAction;}

	/**The list component.*/
	private final JList list;

		/**@return The list component.*/
		protected JList getList() {return list;}

	/**List constructor.
	@param list The list component that contains the list model.
	*/
	public ListEditStrategy(final JList list)
	{
		this(list, null);	//construct the strategy with no modifiable object
	}

	/**List and modifiable constructor.
	@param list The list component that contains the list model.
	@param modifiable The object that represents the list model modification
		status, or <code>null</code> if modification status should not be indicated.
	*/
	public ListEditStrategy(final JList list, final Modifiable modifiable)
	{
		super(list.getModel(), list, modifiable);	//use the list as the parent component
		this.list=list;	//save the list
		selectAllAction=new SelectAllAction();
		selectNoneAction=new SelectNoneAction();
				//if the list changes models, notify this class
		list.addPropertyChangeListener("model", new PropertyChangeListener()	//TODO use a constant
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
					{
						setListModel((ListModel)propertyChangeEvent.getNewValue());	//change to the new list model
						updateStatus();	//update our status, as we just got a new model
					}					
				});
		list.addPropertyChangeListener("enabled", new PropertyChangeListener()	//create a new property change listener that will do nothing but update the status when the list is enabled or disabled TODO use a constant
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {updateStatus();}	//if a property is modified, update the status
				});
		list.addListSelectionListener(new ListSelectionListener()	//create a new list selection listener that will do nothing but update the status when the list selection changes
				{
					public void valueChanged(final ListSelectionEvent listSelectionEvent) {updateStatus();}	//if the list selection changes, update the status
				});
		updateStatus();	//update the initial status
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		if(getList()!=null)	//if we've initialized this class (this method can be called from the parent class constructor)
		{
			final JList list=getList();	//get a reference to the list
			final boolean isListEnabled=list.isEnabled();	//see if the list is enabled
			final int selectionMode=list.getSelectionMode();	//get the list selection mode
			final boolean isMultipleSelectionAllowed=selectionMode!=ListSelectionModel.SINGLE_SELECTION;	//see if multiple selections are allowed, either single intervals or multiple intervals
			final int listModelSize=list.getModel().getSize();	//see how long the list is
	//TODO maybe just go with the way things work now, even though the lead selection may not be visible		final int selectedIndex=list.getSelectedIndex();	//get the first selected index 
			final int selectedIndex=getLeadSelectionIndex();	//get the selected index
	//TODO del if not needed		final boolean isLeadSelected=leadSelectionIndex>=0 && list.isSelectedIndex(leadSelectionIndex);	//see whether the lead index is selected
			getSelectAllAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow all items to be selected if there are items
			getSelectNoneAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow no items to be selected if there are items
			getAddAction().setEnabled(isListEnabled);	//only enable the add action if the list is enabled
			getDeleteAction().setEnabled(isListEnabled && selectedIndex>=0);	//only enable the delete action if there is something selected to be deleted
			getEditAction().setEnabled(isListEnabled && selectedIndex>=0);	//only enable the edit action if there is something selected to be edited
			getMoveUpAction().setEnabled(isListEnabled && selectedIndex>0);	//only enable the move up action if there is something selected that can be moved up
		}
	}

	/**@return The currently selected index of the list model.
	This implementation return the lead selection index of the list selection model.
	@see JList#getLeadSelectionIndex()
	*/
	public int getLeadSelectionIndex()
	{
		return getList()!=null ? getList().getSelectionModel().getLeadSelectionIndex() : -1;	//return the list's lead selection index, keeping in mind that this method can be called from the parent class constructor
	}

	/**Sets the current main seletion index of the list model.
	This implementation sets the lead selection index of the list selection model.
	@param index The new index to become the main selection.
	*/
	public void setLeadSelectionIndex(final int index)
	{
		if(getList()!=null)	//if there is a list
		{
			getList().getSelectionModel().setLeadSelectionIndex(index);	//update the lead selection index
		}
	}

	/**Moves an item in the list.
	 This implementation ensures the item retains its selection state.
	 @param oldIndex The index of the item to move. 
	 @param newIndex The new index to which to move the item.
	 */
	protected void move(final int oldIndex, final int newIndex)
	{
		final boolean isSelected=getList().getSelectionModel().isSelectedIndex(oldIndex);	//see if the old index is selected
		super.move(oldIndex, newIndex);	//move the item
		if(isSelected)	//if the item was selected
		{
			getList().getSelectionModel().addSelectionInterval(newIndex, newIndex);	//make sure the item is selected
		}
		else	//if the item was not selected
		{
			getList().getSelectionModel().removeSelectionInterval(newIndex, newIndex);	//make unselect the item at its new index
		}
	}
		
	/**Selects all list entries.*/
	public void selectAll()
	{
		getList().getSelectionModel().setSelectionInterval(0, getList().getModel().getSize()-1);	//select an interval containing all items
		if(getModifiable()!=null)	//if we have a modifiable object
		{					
			getModifiable().setModified(true);	//selecting all modifies the object
		}
	}

	/**Selects no list entries.*/
	public void selectNone()
	{
		getList().getSelectionModel().clearSelection();	//clear the selection
		if(getModifiable()!=null)	//if we have a modifiable object
		{					
			getModifiable().setModified(true);	//unselecting all modifies the object
		}
	}

	/**Action for selecting all items in the list.*/
	class SelectAllAction extends AbstractAction
	{
		/**Default constructor.*/
		public SelectAllAction()
		{
			super("All");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Select all");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Select all items in the list.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.CHECK_MULTIPLE_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			selectAll();	//select all items
		}
	}

	/**Action for selecting no items in the list.*/
	class SelectNoneAction extends AbstractAction
	{
		/**Default constructor.*/
		public SelectNoneAction()
		{
			super("None");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Select none");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Select no items in the list.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.UNCHECK_MULTIPLE_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			selectNone();	//select no items
		}
	}

}
