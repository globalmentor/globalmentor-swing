package com.garretwilson.swing.unicode;

import java.awt.Component;
import java.io.*;
import java.net.URI;

import javax.swing.*;

import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
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

	/**Default constructor.*/
	public UnicodePanel()
	{
		super(true, true, false);  //construct the parent class with a toolbar and status bar but don't initialize the panel
		unicodeTable=new JTable(new UnicodeTableModel());	//create the Unicode table with a Unicode table model
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
		setContentComponent(new JScrollPane(unicodeTable));	//put the Unicode table in the center of the panel
		setDefaultFocusComponent(unicodeTable);
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
		try
		{
			final UnicodeBlockSet unicodeBlockSet=UnicodeBlockSet.load();	//load the set of Unicode blocks
			final ComboBoxModel unicodeBlockModel=new DefaultComboBoxModel(unicodeBlockSet.toArray(new UnicodeBlock[unicodeBlockSet.size()]));
			actionManager.addToolAction(new ComboBoxAction(unicodeBlockModel));	//create and add a combo box action with the Unicode block information
		}
		catch(final IOException ioException)	//if there is a problem loading the Unicode blocks
		{
			SwingApplication.displayApplicationError(this, ioException);
		}
/*TODO fix
		final Action fileMenuAction=actionManager.addMenuAction(ActionManager.getFileMenuAction());	//file
		actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getOpenAction());	//file|open
		actionManager.addMenuAction(fileMenuAction, sdiDecorator.getResourceComponentManager().getSaveAction());	//file|save
			//set up the tool actions
		actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getOpenAction());	//open
		actionManager.addToolAction(sdiDecorator.getResourceComponentManager().getSaveAction());	//save
*/
	}
}