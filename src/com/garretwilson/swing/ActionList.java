package com.garretwilson.swing;

import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.EventListenerList;

/**Class that fires action events when an item is double-clicked or the Enter
	key is pressed. This class will be superceded if a future version of
	<code>javax.swing.JList</code> handles these key/mouse bindings for the
	correct UI.
	<p>This class was created from consulting the JDK 1.3.1 API documentation for
	<code>javax.swing.JList</code> and <code>javax.swing.JButton</code>, along with
	the sample <code>ActionJList</code> code at
	<a href="http://www.rgagnon.com/javadetails/java-0201.html">http://www.rgagnon.com/javadetails/java-0201.html</a>.</p>
@author Garret Wilson
*/
public class ActionList extends JList
{

	/**The key for mapping the action-firing action in the input and action maps.*/
	public final static String ACTION_KEY="action";

	/**The list of action listeners.*/
	protected final EventListenerList actionListenerList=new EventListenerList();

	/**Constructs a list that displays the elements in the specified, non-null model.
	@param dataModel The data model for this list.
	@exception IllegalArgumentException Thrown if <code>dataModel</code> is
		<code>null</code>.
	*/
	public ActionList(final ListModel dataModel)
	{
		super(dataModel); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a list that displays the elements in the specified array.
	@param listData The array of objects to be loaded into the data model.
	*/
	public ActionList(final Object[] listData)
	{
		super(listData); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a list that displays the elements in the specified
		<code>Vector</code>.
	@param listData The <code>Vector</code> to be loaded into the data model.
	*/
	public ActionList(final Vector listData)
	{
		super(listData); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a list with an empty model.*/
	public ActionList()
	{
		super(); //delegate to the parent class
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
						if(mouseEvent.getSource() instanceof JList)	//if the click originated in a list
						{
							final JList list=(JList)mouseEvent.getSource();	//get the list the click originated in
							final int clickedIndex=list.locationToIndex(mouseEvent.getPoint());	//find the index of the list that was clicked
							if(clickedIndex>=0)	//if they clicked on a valid index
							{
								final int selectedIndex=list.getSelectedIndex();	//see which index is selected
								if(clickedIndex==selectedIndex)	//if they clicked on the selected index (without checking, double-clicking somewhere else on the list would fire the action)
								{
									fireActionPerformed();  //fire an action performed event
									mouseEvent.consume(); //consume the mouse event G***do we need this?
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
		final Object[] selectedValues=getSelectedValues(); //get the selected values
		if(selectedValues.length==1)  //if there is only one value selected G***fix for multiple values
		{
			//create a new action event and fire it to the listeners
			fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, selectedValues[0].toString()));
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
          fireActionEvent=new ActionEvent(ActionList.this,
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