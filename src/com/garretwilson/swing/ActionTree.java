package com.garretwilson.swing;

import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.tree.*;

/**Class that fires action events when a node is double-clicked or the Enter
	key is pressed. This class will be superceded if a future version of
	<code>javax.swing.JTree</code> handles these key/mouse bindings for the
	correct UI.
@see ActionList
@author Garret Wilson
*/
public class ActionTree extends JTree
{

	/**The list of action listeners.*/
	private final EventListenerList actionListenerList=new EventListenerList();

	/**Constructs a tree that displays the elements in the specified, non-null model.
	@param dataModel The data model for this tree.
	@exception IllegalArgumentException Thrown if <code>dataModel</code> is
		<code>null</code>.
	*/
	public ActionTree(final TreeModel dataModel)
	{
		super(dataModel); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a tree that displays the elements in the specified array.
	@param treeData The array of objects to be loaded into the data model.
	*/
	public ActionTree(final Object[] treeData)
	{
		super(treeData); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a tree that displays the elements in the specified
		<code>Vector</code>.
	@param treeData The <code>Vector</code> to be loaded into the data model.
	*/
	public ActionTree(final Vector treeData)
	{
		super(treeData); //delegate to the parent class
		addListeners(); //add the listeners needed for generating action events in response
	}

	/**Constructs a tree with an empty model.*/
	public ActionTree()
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
						fireActionPerformed();  //fire an action performed event
						mouseEvent.consume(); //consume the mouse event G***do we need this?
					}
				 }
			});
		addKeyListener(new KeyAdapter() //create a new key adapter and add it as a listener
			{
				public void keyPressed(KeyEvent keyEvent)	//listen for a key press; listening for a key release would create actions from residue key releases from other components
				{
				  if(keyEvent.getKeyCode()==KeyEvent.VK_ENTER)  //if this was the enter key
					{
						fireActionPerformed();  //fire an action performed event
						keyEvent.consume(); //consume the event so that it won't cause other effects elsewhere
					}
			  }
		  });
	}

	/**Notifies all listeners that have registered interest for notification on
		this event type.
	@see EventListenerList
	*/
  protected void fireActionPerformed()
	{

		final TreePath selectionPath=getSelectionPath();  //get the selection path
		//create a new action event and fire it to the listeners
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)); //G***fix action command
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
	public void removeActionListener(ActionListener actionListener)
	{
		listenerList.remove(ActionListener.class, actionListener);
	}

	/**Notifies all listeners that have registered interest for notification on
		this event type. The event instance is lazily created using the parameters
		passed into the fire method.
	@param actionEvent The <code>ActionEvent</code> object.
	@see EventListenerList
	*/
  protected void fireActionPerformed(ActionEvent actionEvent)
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
          fireActionEvent=new ActionEvent(this,
						  ActionEvent.ACTION_PERFORMED,
						  actionCommand,
						  actionEvent.getModifiers());  //create a new event for firing, using the parameters of the passed event
				}
				((ActionListener)listeners[i+1]).actionPerformed(fireActionEvent);  //send the event to this listener
		  }
		}
  }

}