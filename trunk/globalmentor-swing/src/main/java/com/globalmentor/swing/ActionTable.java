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

package com.globalmentor.swing;

import java.awt.Point;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.*;

/**Class that fires action events when a cell is double-clicked or the Enter
	key is pressed. This class will be superceded if a future version of
	{@link JTable} handles these key/mouse bindings for the
	correct UI.
<p>It is unknown whether this implementation correctly handles reordered columns.</p>
@author Garret Wilson
*/
public class ActionTable extends JTable
{

	/**The key for mapping the action-firing action in the input and action maps.*/
	public final static String ACTION_KEY="action";
	
	/**The list of action listeners.*/
	protected final EventListenerList actionListenerList=new EventListenerList();

	/**Constructs a default table that is initialized with a default data model, a default column model, and a default selection model.*/
	public ActionTable()
	{
		this(null, null, null);
	}

	/**Constructs a table that is initialized with <code>dm</code> as the data model, a default column model, and a default selection model.
	@param dm The data model for the table.
	*/
	public ActionTable(final TableModel dm)
	{
		this(dm, null, null);
	}

	/**Constructs a that is initialized with <code>dm</code> as the data model, <code>cm</code> as the column model, and a default selection model.
	@param dm The data model for the table.
	@param cm The column model for the table.
	*/
	public ActionTable(final TableModel dm, final TableColumnModel cm)
	{
		this(dm, cm, null);
	}

	/**Constructs a table that is initialized with <code>dm</code> as the data model, <code>cm</code> as the column model, and <code>sm</code> as the selection model.
	If any of the parameters are <code>null</code> this method will initialize the table with the corresponding default model.
	@param dm The data model for the table.
	@param cm The column model for the table.
	@param sm the row selection model for the table.
	*/
	public ActionTable(final TableModel dm, final TableColumnModel cm, final ListSelectionModel sm)
	{
		super(dm, cm, sm);	//construct the parent class
		addListeners(); //add the listeners needed for generating action events in response
  }

	/**Constructs a table with <code>numRows</code> and <code>numColumns</code> of empty cells using <code>DefaultTableModel</code>.
	@param numRows The number of rows the table holds.
	@param numColumns The number of columns the table holds.
	*/
	public ActionTable(final int numRows, final int numColumns)
	{
		this(new DefaultTableModel(numRows, numColumns));
	}

	/**Constructs a table to display the values in the <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>, with column names, <code>columnNames</code>.
	@param rowData The data for the new table.
	@param columnNames The names of each column.
	*/
	public ActionTable(final Vector rowData, final Vector columnNames)
	{
		this(new DefaultTableModel(rowData, columnNames));
	}

	/**Constructs a table to display the values in the two dimensional array, <code>rowData</code>, with column names, <code>columnNames</code>.
	@param rowData The data for the new table.
	@param columnNames The names of each column.
	*/
	public ActionTable(final Object[][] rowData, final Object[] columnNames)
	{
		super(rowData, columnNames);	//construct the parent class
		addListeners(); //add the listeners needed for generating action events in response		
	}
	
	/**Adds the appropriate listeners for generating actions.*/
	private void addListeners()
	{
		addMouseListener(new MouseAdapter() //create a new mouse adapter and add it as a listener
			{
				public void mouseClicked(final MouseEvent mouseEvent)
				{
					if(mouseEvent.getClickCount()==2) //if this was a double click
					{
						if(mouseEvent.getSource() instanceof JTable)	//if the click originated in a table
						{
							final JTable table=(JTable)mouseEvent.getSource();	//get the table the click originated in
							final Point point=mouseEvent.getPoint();	//get the mouse point
							final int row=table.rowAtPoint(point);	//get the row at the click point
							final int column=table.columnAtPoint(point);	//get the column at the click point
							if(row>=0 && column>=0)	//if they clicked on a valid cell
							{
								if(row==table.getSelectedRow() && column==table.getSelectedColumn())	//if they clicked on the selected cell (without checking, double-clicking somewhere else on the table would fire the action)
								{
									fireActionPerformed();  //fire an action performed event
									mouseEvent.consume(); //consume the mouse event
								}
							}
						}
					}
				 }
			});
		final InputMap inputMap=getInputMap();	//get the input map		
		final ActionMap actionMap=getActionMap();	//get the action map		
		final KeyStroke enterKeyStroke=KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);	//get the keystroke for the Enter key
		inputMap.put(enterKeyStroke, ACTION_KEY);	//map the Enter keystroke to the action key 
		actionMap.put(ACTION_KEY, new ActionAction());	//map the action key to an action-firing action
	}

	/**Notifies all listeners that have registered interest for notification on
		this event type.
	@see EventListenerList
	*/
  protected void fireActionPerformed()
	{
		final int row=getSelectedRow();	//get the selected row
		final int column=getSelectedColumn();	//get the selected column
		if(row>=0 && column>=0)	//if a valid cell is selected
		{
			final Object value=getValueAt(row, column);	//get the value in this cell
			//create a new action event and fire it to the listeners
			fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, value!=null ? value.toString() : null));
		}
	}

	/**Adds an <code>ActionListener</code> to the list.
	@param actionListener The <code>ActionListener</code> to be added.
	*/
	public void addActionListener(final ActionListener actionListener)
	{
		listenerList.add(ActionListener.class, actionListener);
	}

	/**Removes an <code>ActionListener</code> from the list.
	@param actionListener the listener to be removed
	*/
	public void removeActionListener(final ActionListener actionListener)
	{
		listenerList.remove(ActionListener.class, actionListener);
	}

	/**Notifies all listeners that have registered interest for notification on
		this event type. The event instance is lazily created using the parameters
		passed into the fire method.
	@param actionEvent The <code>ActionEvent</code> object.
	@see EventListenerList
	*/
  protected void fireActionPerformed(final ActionEvent actionEvent)
	{
		final Object[] listeners=listenerList.getListenerList();  //get the array of listeners
		ActionEvent fireActionEvent=null; //we won't create an action event
		//process the listeners last to first, notifying those that are interested in this event
		for(int i=listeners.length-2; i>=0; i-=2) //look at all the listener types
		{
		  if(listeners[i]==ActionListener.class)  //if this is an action listener
			{
				if(fireActionEvent==null) //if we haven't yet created an event to fire
				{
				  final String actionCommand=actionEvent.getActionCommand();  //get the passed action command
          fireActionEvent=new ActionEvent(ActionTable.this,
						  ActionEvent.ACTION_PERFORMED,
						  actionCommand,
						  actionEvent.getModifiers());  //create a new event for firing, using the parameters of the passed event
				}
				((ActionListener)listeners[i+1]).actionPerformed(fireActionEvent);  //send the event to this listener
		  }
		}
  }

	/**Action for firing an action event
	@author Garret Wilson
	*/
	protected class ActionAction extends AbstractAction
	{
		public void actionPerformed(final ActionEvent actionEvent) {fireActionPerformed();}	//fire a new action
	}
}