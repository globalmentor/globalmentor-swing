package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.Editable;

/**Panel that contains a list and buttons for selecting and unselecting all items.
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
@author Garret Wilson
@see java.util.List
@see javax.swing.DefaultListModel
*/
public class ListPanel extends ContentPanel implements Editable
{

	protected final JToolBar buttonToolBar;
	protected final JButton selectAllButton;
	protected final JButton selectNoneButton;
	protected final JToolBar.Separator editSeparator;
	protected final JButton addButton;
	protected final JButton deleteButton;
	protected final JButton editButton;

	/**The editor for editing items in the list, or <code>null</code> if there is no editor.*/
	private Editor editor;

		/**@return The editor for editing items in the list, or <code>null</code> if there is no editor.*/
		public Editor getEditor() {return editor;}

		/**Sets the editor object
		@param editor The editor for editing items in the list, or]
			<code>null</code> if there is no editor.
		*/
		public void setEditor(final Editor editor) {this.editor=editor;}

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
		addAction=new AddAction();	//select all
		addButton=new JButton(addAction);
		deleteAction=new DeleteAction();	//select all
		deleteButton=new JButton(deleteAction);
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
		buttonToolBar.add(addButton);
		buttonToolBar.add(deleteButton);
		buttonToolBar.add(editButton);
		add(buttonToolBar, BorderLayout.LINE_END);
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
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
		addButton.setVisible(isEditable);	//only show the add button if editing is allowed
		addButton.setEnabled(isListEnabled);	//only enable the add button if the list is enabled
		deleteButton.setVisible(isEditable);	//only show the delete button if editing is allowed
		deleteButton.setEnabled(isListEnabled && leadSelectionIndex>=0);	//only enable the delete button if there is something selected to be deleted
		editButton.setVisible(isEditable);	//only show the edit button if editing is allowed
		editButton.setEnabled(isListEnabled && leadSelectionIndex>=0);	//only enable the edit button if there is something selected to be edited
	}

	/**Selects all list entries.*/
	public void selectAll()
	{
		getList().getSelectionModel().setSelectionInterval(0, getList().getModel().getSize()-1);	//select an interval containing all items
		setModified(true);	//selecting all modifies the panel
	}

	/**Selects no list entries.*/
	public void selectNone()
	{
		getList().getSelectionModel().clearSelection();	//clear the selection
		setModified(true);	//unselecting all modifies the panel
	}

	/**Creates a new item and, if editing is successful, adds it to the list.*/
	public void add()
	{
		final Editor editor=getEditor();	//get the current editor
		if(editor!=null)	//if we have an editor
		{
			try
			{
				final Object item=editor.create();	//create a new item
				final Object newItem=editor.edit(this, item);	//edit the item
				if(newItem!=null)	//if the user accepted the changes
				{
					final ListModel listModel=getList().getModel();	//get the list model
					if(listModel instanceof DefaultListModel)	//if the list model is a default list model
					{
						((DefaultListModel)listModel).addElement(newItem);	//add the item to the default list model
					}
					else if(listModel instanceof List)	//if the list model implements the list interface
					{
						((List)listModel).add(newItem);	//add the item to the list
					}					
					setModified(true);	//adding an item modifies the panel
				}
			}
			catch(InstantiationException e)
			{
				SwingApplication.displayApplicationError(this, e);	//show the error
			}
			catch(IllegalAccessException e)
			{
				SwingApplication.displayApplicationError(this, e);	//show the error
			}
		}
	}

	/**Deletes the currently selected item in the list.*/
	public void delete()
	{
		final Editor editor=getEditor();	//get the current editor
		if(editor!=null)	//if we have an editor
		{
			final int leadSelectionIndex=getList().getLeadSelectionIndex();	//get the lead selection
			if(leadSelectionIndex>=0)	//if a valid index is selected
			{
				final ListModel listModel=getList().getModel();	//get the list model
				final Object item=listModel.getElementAt(leadSelectionIndex);	//get the currently selected item
					//ask the user for confimation to delete the item
				if(OptionPane.showConfirmDialog(this, "Are you sure you want to delete the item, \""+item+"\"?", "Confirm delete", OptionPane.OK_CANCEL_OPTION)==OptionPane.OK_OPTION)	//G***i18n
				{
					if(listModel instanceof DefaultListModel)	//if the list model is a default list model
					{
						((DefaultListModel)listModel).remove(leadSelectionIndex);	//remove the item from the default list model
					}
					else if(listModel instanceof List)	//if the list model implements the list interface
					{
						((List)listModel).remove(leadSelectionIndex);	//remove the item from the list
					}
					setModified(true);	//deleting an item modifies the panel
				}
			}
		}
	}

	/**Edits the currently selected item in the list.*/
	public void edit()
	{
		final Editor editor=getEditor();	//get the current editor
		if(editor!=null)	//if we have an editor
		{
			final int leadSelectionIndex=getList().getLeadSelectionIndex();	//get the lead selection
			if(leadSelectionIndex>=0)	//if a valid index is selected
			{
				final ListModel listModel=getList().getModel();	//get the list model
				final Object newItem=editor.edit(this, listModel.getElementAt(leadSelectionIndex));	//edit the selected item
				if(newItem!=null)	//if the edit went successfully
				{
					if(listModel instanceof DefaultListModel)	//if the list model is a default list model
					{
						((DefaultListModel)listModel).set(leadSelectionIndex, newItem);	//update the default list model item
					}
					else if(listModel instanceof List)	//if the list model implements the list interface
					{
						((List)listModel).set(leadSelectionIndex, newItem);	//update the list
					}
					setModified(true);	//editing an item modifies the panel
				}
			}
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

	/**An interface for editing items in a list.
	@author Garret Wilson
	*/
	public interface Editor	//TODO use generics when they are available
	{

		/**Creates a new default object to be edited.
		@return The new default object.
		@exception IllegalAccessException Thrown if the class or its nullary 
			constructor is not accessible.
		@exception InstantiationException Thrown if a class represents an abstract
			class, an interface, an array class, a primitive type, or void;
			or if the class has no nullary constructor; or if the instantiation fails
			for some other reason.
		*/
		public Object create() throws InstantiationException, IllegalAccessException;

		/**Edits an object from the list.
		@param parentComponent The component to use as a parent for any editing
			components.
		@param item The item to edit in the list.
		@return The object with the modifications from the edit, or
			<code>null</code> if the edits should not be accepted.
		*/
		public Object edit(final Component parentComponent, final Object item);

	}

	/**A base class for editing items in a list.
	@author Garret Wilson
	*/
	public static abstract class AbstractEditor implements Editor
	{

		/**The class used for creating new objects.*/
		protected final Class factoryClass;

		/**Creates an editor with a factory class.
		@param factoryClass The class used to create new objects. The class must
			implement a default constructor.
		*/
		public AbstractEditor(final Class factoryClass)
		{
			this.factoryClass=factoryClass;	//save the class
		}

		/**Creates a new default object to be edited.
		@return The new default object.
		@exception IllegalAccessException Thrown if the class or its nullary 
			constructor is not accessible.
		@exception InstantiationException Thrown if a class represents an abstract
			class, an interface, an array class, a primitive type, or void;
			or if the class has no nullary constructor; or if the instantiation fails
			for some other reason.
		*/
		public Object create() throws InstantiationException, IllegalAccessException
		{
			return factoryClass.newInstance();	//create a new instance of the class
		}

	}

}
