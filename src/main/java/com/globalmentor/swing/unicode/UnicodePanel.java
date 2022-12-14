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

package com.globalmentor.swing.unicode;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumnModel;

import static com.globalmentor.swing.Components.*;

import com.globalmentor.awt.Fonts;
import com.globalmentor.swing.*;
import com.globalmentor.swing.event.ListDataAdapter;
import com.globalmentor.unicode.*;

/**
 * A panel that shows the set of Unicode code points and related information. This panel fires an action when a Unicode code point has been selected.
 * @author Garret Wilson
 */
public class UnicodePanel extends ToolStatusPanel {

	/** The table containing the Unicode code points. */
	private final ActionTable unicodeTable;

	/** @return The table containing the Unicode code points. */
	protected ActionTable getUnicodeTable() {
		return unicodeTable;
	}

	/** The table model for the Unicode code points. */
	private final UnicodeTableModel unicodeTableModel;

	/** @return The table model for the Unicode code points. */
	protected UnicodeTableModel getUnicodeTableModel() {
		return unicodeTableModel;
	}

	/** The model of Unicode blocks for use in a combo box. */
	private final ComboBoxModel unicodeBlockComboBoxModel;

	/** @return The model of Unicode blocks for use in a combo box. */
	protected ComboBoxModel getUnicodeBlockComboBoxModel() {
		return unicodeBlockComboBoxModel;
	}

	/** The status bar showing information about the current Unicode character. */
	private final UnicodeStatusBar unicodeStatusBar;

	/** @return The status bar showing information about the current Unicode character. */
	protected final UnicodeStatusBar getUnicodeStatusBar() {
		return unicodeStatusBar;
	}

	/** Default constructor. */
	public UnicodePanel() {
		super(true, true, false); //construct the parent class with a toolbar and status bar but don't initialize the panel
		unicodeTableModel = new UnicodeTableModel(); //create a new Unicode table model
		unicodeTable = new ActionTable(unicodeTableModel); //create the Unicode table with a Unicode table model
		final SortedSet<UnicodeBlock> unicodeBlockSet = UnicodeBlocks.getUnicodeBlocks(); //load the set of Unicode blocks
		unicodeBlockComboBoxModel = new DefaultComboBoxModel(unicodeBlockSet.toArray(new UnicodeBlock[unicodeBlockSet.size()])); //create a model from the set of Unicode blocks
		unicodeStatusBar = new UnicodeStatusBar(); //create a new Unicode status bar
		initialize(); //initialize the panel
	}

	/** The dimension of each square code point cell. */
	private static final int CELL_DIMENSION = 32; //TODO use a calculated value

	/** Initializes the user interface. */
	protected void initializeUI() {
		super.initializeUI(); //do the default initialization
		unicodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //allow horizontal scrolling
		unicodeTable.setCellSelectionEnabled(true);
		unicodeTable.setRowSelectionAllowed(false);
		unicodeTable.setColumnSelectionAllowed(false);
		/*TODO del if not needed; doesn't work as implemented
				final Font baseTableHeaderFont=unicodeTable.getTableHeader().getFont();	//get the original table header font
				unicodeTable.getTableHeader().setFont(baseTableHeaderFont.deriveFont((float)Math.round(baseTableHeaderFont.getSize()*0.8f)));	//reduce the size of the header
		*/
		final Font baseTableFont = unicodeTable.getFont(); //get the original table font
		final Font cellFont = Fonts.getFont("Lucida Sans Regular", baseTableFont.getStyle(), //TODO use a constant; see file:///d:/reference/Java/jdk-1_5_0-doc/guide/intl/font.html
				Math.round(baseTableFont.getSize() * 2)); //derive the cell font size from the table font size
		//TODO fix		final Font cellFont=baseTableFont.deriveFont((float)Math.round(baseTableFont.getSize()*2));	//derive the cell font size from the table font size
		unicodeTable.setFont(cellFont); //change the table font
		unicodeTable.setRowHeight(CELL_DIMENSION); //TODO use a calculated value
		final TableColumnModel unicodeTableColumModel = unicodeTable.getColumnModel(); //get the column model for the table
		for(int i = unicodeTableColumModel.getColumnCount() - 1; i >= 0; --i) { //look at each column in the model
			unicodeTableColumModel.getColumn(i).setPreferredWidth(CELL_DIMENSION); //set the preferred width of the column TODO use a calculated value 
		}
		unicodeTable.setPreferredScrollableViewportSize(new Dimension(16 * CELL_DIMENSION, unicodeTable.getRowCount() * CELL_DIMENSION)); //TODO testing
		unicodeTable.setDefaultRenderer(Integer.class, new UnicodeTableCellRenderer()); //use a Unicode table cell renderer for the code points
		setContentComponent(new JScrollPane(unicodeTable)); //put the Unicode table in the center of the panel
		setDefaultFocusComponent(unicodeTable);
		unicodeTable.changeSelection(0, 0, false, false); //select the origin cell
		final ListSelectionListener updateStatusListSelectionListener = createUpdateStatusListSelectionListener(); //create a list selection listener to update the status when the selection changes
		unicodeTable.getSelectionModel().addListSelectionListener(updateStatusListSelectionListener); //update the status when the row selection changes
		unicodeTable.getColumnModel().getSelectionModel().addListSelectionListener(updateStatusListSelectionListener); //update the status when the column selection changes
		unicodeBlockComboBoxModel.addListDataListener(new ListDataAdapter() { //synchronize the table with the combo box when the combo box changes

					public void contentsChanged(final ListDataEvent listDataEvent) {
						synchronizeUnicodeTableToUnicodeBlockComboBox();
					}
				});
		unicodeTable.addActionListener(new ActionListener() { //listen for action events on the Unicode table, and forward the event to our listeners

					public void actionPerformed(final ActionEvent actionEvent) { //when an action is performed
						for(final ActionListener listener : getEventListeners(ActionListener.class)) { //for each action listener
							listener.actionPerformed(actionEvent); //forward the event to the listener
						}
					}
				});
		add(unicodeStatusBar, BorderLayout.SOUTH); //put the Unicode status bar in the south
		//TODO fix		unicodeStatusBar.attach(textPane);	//set the Unicode status bar to track the XML text pane 
	}

	/**
	 * Initializes actions in the action manager.
	 * @param actionManager The implementation that manages actions.
	 */
	protected void initializeActions(final ActionManager actionManager) {
		super.initializeActions(actionManager); //do the default initialization
		actionManager.addToolAction(new ComboBoxAction(getUnicodeBlockComboBoxModel())); //create and add a combo box action with the Unicode block information
		/*TODO fix
				final Action fileMenuAction=actionManager.addMenuAction(ActionManager.getFileMenuAction());	//file
				actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getOpenAction());	//file|open
				actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getSaveAction());	//file|save
					//set up the tool actions
				actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getOpenAction());	//open
				actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getSaveAction());	//save
		*/
	}

	/** Whether the status is being updated. */
	//TODO fix	protected boolean updatingStatus=false;

	/** Updates the states of the user interface, including enabled/disabled status, proxied actions, etc. */
	public void updateStatus() {
		//TODO fix		updatingStatus=true;	//show that we're updating the status, so that changing the combo box won't reposition the 
		final int selectedCodePoint = getSelectedCodePoint(); //get the selected code point
		getUnicodeStatusBar().setCodePoint(selectedCodePoint); //set the selected code point
		final UnicodeBlock selectedComboBoxUnicodeBlock = (UnicodeBlock)unicodeBlockComboBoxModel.getSelectedItem(); //see which block the combo box has selected
		//if no block is currently selected, or the new selected code point is not within the current block's range
		if(selectedComboBoxUnicodeBlock == null || !selectedComboBoxUnicodeBlock.contains(selectedCodePoint)) {
			final UnicodeBlock unicodeBlock = UnicodeBlocks.getUnicodeBlockByCodePoint(selectedCodePoint); //get the block in which the selected code point lies
			unicodeBlockComboBoxModel.setSelectedItem(unicodeBlock); //select the block that includes the selected code point
		}
	}

	/** @return The value of the Unicode code point selected, or <code>-1</code> if no code point is selected. */
	public int getSelectedCodePoint() {
		final JTable unicodeTable = getUnicodeTable(); //get the Unicode table
		final int row = unicodeTable.getSelectedRow(); //get the selected row
		final int column = unicodeTable.getSelectedColumn(); //get the selected column
		if(row >= 0 && column >= 0) { //if a valid cell is selected
			final Integer value = (Integer)unicodeTable.getValueAt(row, column); //get the value in this cell
			return value.intValue(); //return the integer code point selected
		} else { //if no valid cell is selected
			return -1; //show that no valid code point is selected
		}
	}

	/**
	 * Synchronizes the Unicode table to match the currently selected Unicode code block. If no block is selected, no action occurs.
	 */
	protected void synchronizeUnicodeTableToUnicodeBlockComboBox() {
		final UnicodeBlock block = (UnicodeBlock)getUnicodeBlockComboBoxModel().getSelectedItem(); //see which block is selected
		if(block != null) { //if a known block is selected
			if(!block.contains(getSelectedCodePoint())) { //if the selected code point is not within the new block range
				final JTable table = getUnicodeTable(); //get the Unicode table
				final int blockStartCode = block.getStartCode(); //see where this block starts 
				final int row = getUnicodeTableModel().getRow(blockStartCode); //get the row of this block's start
				final int column = getUnicodeTableModel().getColumn(block.getStartCode()); //get the column of this block's start
				table.changeSelection(row, column, false, false); //select the start of the block
				final Rectangle rectangle = table.getCellRect(row, column, true); //get the rectangle representing the start of the block
				scrollRectToOrigin(table, rectangle); //scroll the cell representing the start of the block to the origin
			}
		}
	}

	/**
	 * Adds an <code>ActionListener</code> to the list.
	 * @param actionListener The <code>ActionListener</code> to be added.
	 */
	public void addActionListener(final ActionListener actionListener) {
		addEventListener(ActionListener.class, actionListener);
	}

	/**
	 * Removes an <code>ActionListener</code> from the list.
	 * @param actionListener the listener to be removed
	 */
	public void removeActionListener(ActionListener actionListener) {
		removeEventListener(ActionListener.class, actionListener);
	}

}