package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.Editable;

/**Panel that contains a list and buttons for selecting and unselecting all items.
<p>Bound properties:</p>
<dl>
	<dt><code>Editable.EDITABLE_PROPERTY</code> (<code>Boolean</code>)</dt>
	<dd>Indicates the editable status has changed.</dd>
</dl>
@author Garret Wilson
*/
public class ListPanel extends ContentPanel implements Editable
{

	protected final JToolBar buttonToolBar;
	protected final JButton selectAllButton;
	protected final JButton selectNoneButton;
	protected final JToolBar.Separator editSeparator;
	protected final JButton editButton;

	/**@return The list in the scroll pane that is the content component as a list; convenience method.*/
	protected JList getList() {return (JList)((JScrollPane)getContentComponent()).getViewport().getView();}

	/**The action for selecting all items.*/
	private final Action selectAllAction;

		/**@return The action for selecting all items.*/
		protected Action getSelectAllAction() {return selectAllAction;}

	/**The action for selecting no items.*/
	private final Action selectNoneAction;

		/**@return The action for selecting no items.*/
		protected Action getSelectNoneAction() {return selectNoneAction;}

	/**The action for editing an item.*/
	private final Action editAction;

		/**@return The action for editing an item.*/
		protected Action getEditAction() {return editAction;}

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

	/**List constructor.
	@param list The list component, which will be wrapped in a scroll pane.
	@see JScrollPane
	*/
	public ListPanel(final JList list)
	{
		super(new JScrollPane(list), false);	//construct the panel with the list as the content component, but don't initialize the panel
		final ListDataListener updateStatusListDataListener=createUpdateStatusListDataListener();	//create a listener to update the status in response to list model changes
		list.getModel().addListDataListener(updateStatusListDataListener);	//listen for changes in the list model
				//if the list changes models, stop listening to the old model and listen to the new one
		list.addPropertyChangeListener("model", new PropertyChangeListener()	//TODO use a constant
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
					{
						if(propertyChangeEvent.getOldValue() instanceof ListModel)	//if there was a list model before
						{
							((ListModel)propertyChangeEvent.getOldValue()).removeListDataListener(updateStatusListDataListener);	//stop listening to the old model
						}
						if(propertyChangeEvent.getNewValue() instanceof ListModel)	//if there is a new list model
						{
							((ListModel)propertyChangeEvent.getNewValue()).addListDataListener(updateStatusListDataListener);	//listen for changes to the new model
						}
						updateStatus();	//update our status, as we just got a new model
					}					
				});
		list.addPropertyChangeListener("enabled", createUpdateStatusPropertyChangeListener());	//update the status when the list's is enabled or disabled TODO use a constant
		list.addListSelectionListener(createUpdateStatusListSelectionListener());	//update the status when the list selection changes
		buttonToolBar=new JToolBar(JToolBar.VERTICAL);
		selectAllAction=new SelectAllAction();	//select all
		selectAllButton=new JButton(selectAllAction);
		selectNoneAction=new SelectNoneAction();	//select none
		selectNoneButton=new JButton(selectNoneAction);
		editSeparator=ToolBarUtilities.createToolBarSeparator(buttonToolBar);
		editAction=new EditAction();	//edit
		editButton=new JButton(editAction);

		editable=false;	//default to not being editable
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final BasicGridBagLayout buttonPanelLayout=new BasicGridBagLayout();
		buttonToolBar.setFloatable(false);
		buttonToolBar.add(selectAllButton);
		buttonToolBar.add(selectNoneButton);
		buttonToolBar.add(editSeparator);
		buttonToolBar.add(editButton);
		add(buttonToolBar, BorderLayout.LINE_END);
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default updating
		final JList list=getList();	//get a reference to the list
		final boolean isListEnabled=list.isEnabled();	//see if the list is enabled
		final int selectionMode=list.getSelectionMode();	//get the list selection mode
		final boolean isMultipleSelectionAllowed=selectionMode!=ListSelectionModel.SINGLE_SELECTION;	//see if multiple selections are allowed, either single intervals or multiple intervals
		final int listModelSize=list.getModel().getSize();	//see how long the list is
		final boolean isEditable=isEditable();	//see whether the list can be edited
//G***maybe just go with the way things work now, even though the lead selection may not be visible		final int selectedIndex=list.getSelectedIndex();	//get the first selected index 
		final int leadSelectionIndex=list.getLeadSelectionIndex();	//get the selection lead
//G***del if not needed		final boolean isLeadSelected=leadSelectionIndex>=0 && list.isSelectedIndex(leadSelectionIndex);	//see whether the lead index is selected	
		selectAllButton.setVisible(isMultipleSelectionAllowed);	//only show the selection buttons if interval selections are allowed
		selectNoneButton.setVisible(isMultipleSelectionAllowed);	//only show the selection buttons if multiple selections are allowed
		getSelectAllAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow all items to be selected if there are items
		getSelectNoneAction().setEnabled(isMultipleSelectionAllowed && isListEnabled && listModelSize>0);	//only allow no items to be selected if there are items
		editSeparator.setVisible(isEditable);	//only show the edit separator if editing is allowed
		editButton.setVisible(isEditable);	//only show the edit button if editing is allowed
		editButton.setEnabled(leadSelectionIndex>=0);	//only enable the edit button if there is something selected to be edited
	}

	/**Selects all list entries.*/
	public void selectAll()
	{
		getList().getSelectionModel().setSelectionInterval(0, getList().getModel().getSize()-1);	//select an interval containing all items
	}

	/**Selects no list entries.*/
	public void selectNone()
	{
		getList().getSelectionModel().clearSelection();	//clear the selection
	}

	/**Edits the currently selected item in the list.
	<p>Derived classes should usually only override <code>edit(Object)</code>.</p>
	@see #edit(Object)
	*/
	public void edit()
	{
		final int leadSelectionIndex=getList().getLeadSelectionIndex();	//get the lead selection
		if(leadSelectionIndex>=0)	//if a valid index is selected
		{
			edit(getList().getModel().getElementAt(leadSelectionIndex));	//edit the selected item
//TODO fix---how do we notify the list of changes in a generic way? maybe we don't need to if the implementing class modifies the list model correctly
//G***fix			((AbstractListModel)getList().getModel())
/*G***fix

			protected void fireContentsChanged(Object source,
																				 int index0,
																				 int index1)
*/
		}
	}

	/**Edits the given item in the list.*/
	public void edit(final Object item)
	{
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
			putValue(MNEMONIC_KEY, new Integer('a'));  //set the mnemonic key G***i18n
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
			putValue(MNEMONIC_KEY, new Integer('n'));  //set the mnemonic key G***i18n
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

	/**Action for editing an item in the list.*/
	class EditAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditAction()
		{
			super("Edit");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit item");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit the selected item.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('e'));  //set the mnemonic key G***i18n
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
