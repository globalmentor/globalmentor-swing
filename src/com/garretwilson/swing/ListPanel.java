package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.util.Editable;

/**Panel that contains a list and buttons for selecting and unselecting all items.
<p>In order to create and edit items, an edit strategy  must be set (besides
	setting the list to be editable using <code>setEditable(true)</code>). Actual
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
@see ListEditStrategy
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
	protected final JToolBar.Separator moveSeparator;
	protected final JButton moveUpButton;
	protected final JButton moveDownButton;
	
	/**The edit strategy for editing items in the list, or <code>null</code> if there is no edit strategy.*/
	private final ListEditStrategy editStrategy;

		/**@return The strategy for editing items in the list, or <code>null</code> if there is no edit strategy.*/
		public ListEditStrategy getEditStrategy() {return editStrategy;}

		/**Sets the edit strategy object
		@param editStrategy The edit strategy for editing items in the list, or
			<code>null</code> if there is no edit strategy.
		*/
//G***del if not needed		public void setEditStrategy(final ListEditStrategy editStrategy) {this.editStrategy=editStrategy;}	//TODO make sure we unregister the old list strategy first by removing all listeners to the list

	/**The list this panel represents.*/
	private final JList list;
	
		/**@return The list this panel represents.*/
		protected JList getList() {return list;}

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

	/**List constructor that only allows selection.
	@param list The list component, which will be wrapped in a scroll pane.
	@see JScrollPane
	*/
	public ListPanel(final JList list)
	{
		this(list, new ListEditStrategy(list)
				{
					protected Object createItem() throws InstantiationException, IllegalAccessException
					{
						throw new AssertionError("This list cannot create items.");	//TODO fix with a better exception
					}
					protected Object editItem(Object item)
					{
						throw new AssertionError("This list cannot edit items.");	//TODO fix with a better exception
					}
				});
	}

	/**List and edit strategy constructor.
	@param list The list component, which will be wrapped in a scroll pane.
	@param editStrategy The edit strategy for editing items in the list.
	@see JScrollPane
	*/
	public ListPanel(final JList list, final ListEditStrategy editStrategy)
	{
		super(new JScrollPane(list), false);	//construct the panel with the list as the content component, but don't initialize the panel
		this.list=list;	//save the list we were given
		this.editStrategy=editStrategy;	//save the edit strategy
		buttonToolBar=new JToolBar(JToolBar.VERTICAL);
		selectAllButton=new JButton(editStrategy.getSelectAllAction());
		selectNoneButton=new JButton(editStrategy.getSelectNoneAction());
		editSeparator=ToolBarUtilities.createToolBarSeparator(buttonToolBar);
		addButton=new JButton(editStrategy.getAddAction());
		deleteButton=new JButton(editStrategy.getDeleteAction());
		editButton=new JButton(editStrategy.getEditAction());
		moveSeparator=ToolBarUtilities.createToolBarSeparator(buttonToolBar);
		moveUpButton=new JButton(editStrategy.getMoveUpAction());
		moveDownButton=new JButton(editStrategy.getMoveDownAction());
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
		buttonToolBar.add(moveSeparator);
		buttonToolBar.add(moveUpButton);
		buttonToolBar.add(moveDownButton);
		add(buttonToolBar, BorderLayout.LINE_END);
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		final int selectionMode=getList().getSelectionMode();	//get the list selection mode
		final boolean isMultipleSelectionAllowed=selectionMode!=ListSelectionModel.SINGLE_SELECTION;	//see if multiple selections are allowed, either single intervals or multiple intervals
		final boolean isEditable=isEditable();	//see whether the list can be edited
//G***maybe just go with the way things work now, even though the lead selection may not be visible		final int selectedIndex=list.getSelectedIndex();	//get the first selected index 
//G***del if not needed		final boolean isLeadSelected=leadSelectionIndex>=0 && list.isSelectedIndex(leadSelectionIndex);	//see whether the lead index is selected	
		selectAllButton.setVisible(isMultipleSelectionAllowed);	//only show the selection buttons if interval selections are allowed
		selectNoneButton.setVisible(isMultipleSelectionAllowed);	//only show the selection buttons if multiple selections are allowed
		editSeparator.setVisible(isMultipleSelectionAllowed && isEditable);	//only show the edit separator if editing is allowed, and we're showing buttons before it
		addButton.setVisible(isEditable);	//only show the add button if editing is allowed
		deleteButton.setVisible(isEditable);	//only show the delete button if editing is allowed
		editButton.setVisible(isEditable);	//only show the edit button if editing is allowed	
		moveSeparator.setVisible(isEditable);	//only show the move separator if editing is allowed
		moveUpButton.setVisible(isEditable);	//only show the move up button if editing is allowed
		moveDownButton.setVisible(isEditable);	//only show the move down button if editing is allowed
	}

}
