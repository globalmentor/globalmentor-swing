package com.garretwilson.swing;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.Modifiable;

/**A plug-in strategy class for editing items from a list component.
@author Garret Wilson
*/
public abstract class ListEditStrategy extends ListModelEditStrategy
{

	/**The action for selecting all items.*/
	private final Action selectAllAction;

		/**@return The action for selecting all items.*/
		protected Action getSelectAllAction() {return selectAllAction;}

	/**The action for selecting no items.*/
	private final Action selectNoneAction;

		/**@return The action for selecting no items.*/
		protected Action getSelectNoneAction() {return selectNoneAction;}

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
	//G***maybe just go with the way things work now, even though the lead selection may not be visible		final int selectedIndex=list.getSelectedIndex();	//get the first selected index 
			final int selectedIndex=getSelectedIndex();	//get the selected index
	//G***del if not needed		final boolean isLeadSelected=leadSelectionIndex>=0 && list.isSelectedIndex(leadSelectionIndex);	//see whether the lead index is selected
			getSelectAllAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow all items to be selected if there are items
			getSelectNoneAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow no items to be selected if there are items
			getAddAction().setEnabled(isListEnabled);	//only enable the add action if the list is enabled
			getDeleteAction().setEnabled(isListEnabled && selectedIndex>=0);	//only enable the delete action if there is something selected to be deleted
			getEditAction().setEnabled(isListEnabled && selectedIndex>=0);	//only enable the edit action if there is something selected to be edited
		}
	}

	/**@return The currently selected index of the list model.
	This implementation return the lead selection index of the list.
	@see JList#getLeadSelectionIndex()
	*/
	public int getSelectedIndex()
	{
		return getList()!=null ? getList().getLeadSelectionIndex() : -1;	//return the list's lead selection index, keeping in mind that this method can be called from the parent class constructor
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
			super("All");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Select all");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Select all items in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));  //set the mnemonic key G***i18n
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
			super("None");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Select none");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Select no items in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key G***i18n
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
