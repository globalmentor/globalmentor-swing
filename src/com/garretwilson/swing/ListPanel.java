package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;

/**Panel that contains a list and buttons for selecting and unselecting all items.
@author Garret Wilson
*/
public class ListPanel extends ContentPanel
{

	private BasicPanel buttonPanel;
	private final JButton selectAllButton;
	private final JButton selectNoneButton;

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

	/**List constructor.
	@param list The list component, which will be wrapped in a scroll pane.
	@see JScrollPane
	*/
	public ListPanel(final JList list)
	{
		super(new JScrollPane(list), false);	//construct the panel with the list as the content component, but don't initialize the panel
		buttonPanel=new BasicPanel();

		selectAllAction=new SelectAllAction();
		selectAllButton=new JButton(selectAllAction);

		selectNoneAction=new SelectNoneAction();
		selectNoneButton=new JButton(selectNoneAction);
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final BasicGridBagLayout buttonPanelLayout=new BasicGridBagLayout();
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanel.add(selectAllButton, buttonPanelLayout.createNextBoxConstraints(BasicGridBagLayout.Y_AXIS));
		buttonPanel.add(selectNoneButton, buttonPanelLayout.createNextBoxConstraints(BasicGridBagLayout.Y_AXIS));
		buttonPanel.add(Box.createGlue(), buttonPanelLayout.createNextBoxConstraints(BasicGridBagLayout.Y_AXIS, 1.0));

		add(buttonPanel, BorderLayout.LINE_END);
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default updating
		final int listModelSize=getList().getModel().getSize();	//see how long the list is
		getSelectAllAction().setEnabled(listModelSize>0);	//only allow all items to be selected if there are items
		getSelectNoneAction().setEnabled(listModelSize>0);	//only allow no items to be selected if there are items
	}

	/**Selects all list entries.*/
	public void selectAll()
	{
		final JList list=getList();	//get the list
		final ListModel listModel=list.getModel();	//get the list model
		for(int i=listModel.getSize()-1; i>=0; i--)	//look at each item in the model
		{
			list.setSelectedValue(listModel.getElementAt(i), false);	//select this value without scrolling
		}
	}

	/**Selects no list entries.*/
	public void selectNone()
	{
		getList().setSelectedIndices(new int[]{});	//select an empty array of indices
	}

	/**Action for selecting all items in the list.*/
	class SelectAllAction extends AbstractAction
	{
		/**Default constructor.*/
		public SelectAllAction()
		{
			super("Select All");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Select all");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Select all items in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('a'));  //set the mnemonic key G***i18n
//TODO add action icon			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SELECT_ITEM_ICON_FILENAME)); //load the correct icon
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
			super("Select None");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Select none");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Select no items in the list.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('n'));  //set the mnemonic key G***i18n
//TODO add action icon			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SELECT_ITEM_ICON_FILENAME)); //load the correct icon
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
