package com.garretwilson.swing.unicode;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.*;
import java.net.URI;
import java.util.SortedSet;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.garretwilson.awt.FontUtilities;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import static com.garretwilson.swing.ComponentUtilities.*;
import com.garretwilson.swing.event.ListDataAdapter;
import com.garretwilson.text.unicode.*;

/**A panel that shows the set of Unicode code points and related information.
@author Garret Wilson
*/
public class UnicodePanel extends ToolStatusPanel
{

	/**The table containing the Unicode code points.*/
	private final JTable unicodeTable;

		/**@retuirn The table containing the Unicode code points.*/
		protected JTable getUnicodeTable() {return unicodeTable;}

	/**The table model for the Unicode code points.*/
	private final UnicodeTableModel unicodeTableModel;
	
		/**@return The table model for the Unicode code points.*/
		protected UnicodeTableModel getUnicodeTableModel() {return unicodeTableModel;}

	/**The model of Unicode blocks for use in a combo box.*/
	private final ComboBoxModel unicodeBlockComboBoxModel;

		/**@return The model of Unicode blocks for use in a combo box.*/
		protected ComboBoxModel getUnicodeBlockComboBoxModel() {return unicodeBlockComboBoxModel;}

	/**Default constructor.*/
	public UnicodePanel()
	{
		super(true, true, false);  //construct the parent class with a toolbar and status bar but don't initialize the panel
		unicodeTableModel=new UnicodeTableModel();	//create a new Unicode table model
		unicodeTable=new JTable(unicodeTableModel);	//create the Unicode table with a Unicode table model
		final SortedSet<UnicodeBlock> unicodeBlockSet=UnicodeBlocks.getUnicodeBlocks();	//load the set of Unicode blocks
		unicodeBlockComboBoxModel=new DefaultComboBoxModel(unicodeBlockSet.toArray(new UnicodeBlock[unicodeBlockSet.size()]));	//create a model from the set of Unicode blocks
		initialize(); //initialize the panel
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI();	//do the default initialization
		unicodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);	//allow horizontal scrolling
		unicodeTable.setCellSelectionEnabled(true);
		unicodeTable.setRowSelectionAllowed(false);
		unicodeTable.setColumnSelectionAllowed(false);
		final Font baseTableFont=unicodeTable.getFont();	//get the original table font
		final Font cellFont=FontUtilities.getFont("Lucida Sans Regular", baseTableFont.getStyle(),	//TODO use a constant; see file:///d:/reference/Java/jdk-1_5_0-doc/guide/intl/font.html
				Math.round(baseTableFont.getSize()*2));	//derive the cell font size from the table font size
//TODO fix		final Font cellFont=baseTableFont.deriveFont((float)Math.round(baseTableFont.getSize()*2));	//derive the cell font size from the table font size
		unicodeTable.setFont(cellFont);	//change the table font
		unicodeTable.setRowHeight(24);	//TODO testing
		unicodeTable.setDefaultRenderer(Integer.class, new UnicodeTableCellRenderer());	//use a Unicode table cell renderer for the code points
		setContentComponent(new JScrollPane(unicodeTable));	//put the Unicode table in the center of the panel
		setDefaultFocusComponent(unicodeTable);
		unicodeTable.changeSelection(0, 0, false, false);	//select the origin cell
		unicodeBlockComboBoxModel.addListDataListener(new ListDataAdapter()	//synchronize the table with the combo box when the combo box changes
				{
					public void contentsChanged(final ListDataEvent listDataEvent) {synchronizeUnicodeTableToUnicodeBlockComboBox();}
				});
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
		actionManager.addToolAction(new ComboBoxAction(getUnicodeBlockComboBoxModel()));	//create and add a combo box action with the Unicode block information
/*TODO fix
		final Action fileMenuAction=actionManager.addMenuAction(ActionManager.getFileMenuAction());	//file
		actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getOpenAction());	//file|open
		actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getSaveAction());	//file|save
			//set up the tool actions
		actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getOpenAction());	//open
		actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getSaveAction());	//save
*/
	}

	protected void synchronizeUnicodeTableToUnicodeBlockComboBox()
	{
		final UnicodeBlock block=(UnicodeBlock)getUnicodeBlockComboBoxModel().getSelectedItem();	//see which block is selected
		final JTable table=getUnicodeTable();	//get the Unicode table
		final int row=getUnicodeTableModel().getRow(block.getStartCode());
		final int column=getUnicodeTableModel().getColumn(block.getStartCode());
		table.changeSelection(row, column, false, false);	//select the new cell
		final Rectangle rectangle=table.getCellRect(row, column, true);
		scrollRectToOrigin(table, rectangle);	//scroll the cell to the origin
	}
}