package com.garretwilson.swing;

import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.util.DefaultOrderComparator;
import com.garretwilson.util.IteratorUtilities;

/**Manages a set of actions for an application or a component.
The actions are categorized into groups, and action managers may merge or
	unmerge with actions from other action managers.
<p>For consistency and merging, the provided actions should be used if possible
	to represent top-level menus.</p>
<p>To indicate that actions should be visibly separated in any menu or toolbar,
	an instance of <code>SeparatorAction</code> should be used.</p>
<p>Each menu action may have an action manager order property that will
	determine its order within its group. Several default orders are provided and
	should be used for consistency and to facility proper merging. The predefined
	menu actions use the predefined orders automatically. Actions with no order
	specified will be sorted in front of those that do, and two menu actions
	with the same order or no specified order will be sorted in the order in
	which they were added to the manager.</p>
<p>Toolbar actions are not sorted.</p>
@author Garret Wilson
*/
public class ActionManager implements Cloneable
{

	/**The <code>Integer</code> property that specifies the order of a menu action when returned in an iterator.*/
	public final static String MENU_ORDER_PROPERTY=ActionManager.class.getName()+JavaConstants.PACKAGE_SEPARATOR+"order";

		//top-level menu order
	public final static int FILE_MENU_ACTION_ORDER=100;
	public final static int HELP_MENU_ACTION_ORDER=1000;

		//file menu order
	public final static int FILE_NEW_MENU_ACTION_ORDER=100;
	public final static int FILE_OPEN_MENU_ACTION_ORDER=200;
	public final static int FILE_CLOSE_MENU_ACTION_ORDER=300;
	public final static int FILE_SAVE_MENU_ACTION_ORDER=400;
	public final static int FILE_SAVE_AS_MENU_ACTION_ORDER=500;
	public final static int FILE_EXIT_MENU_ACTION_ORDER=9999;

		//help menu order
	public final static int HELP_ABOUT_MENU_ACTION_ORDER=9999;

	/**The lazily-created action representing the top-level file menu.*/
	private static Action fileMenuAction=null;

	/**@return The lazily-created action representing the top-level file menu.*/
	public static Action getFileMenuAction()
	{
		if(fileMenuAction==null)	//if there is no action, yet
		{
			fileMenuAction=new FileMenuAction();	//create a new action 
		}
		return fileMenuAction;	//return the action
	}

	/**The lazily-created action representing the top-level help menu.*/
	private static Action helpMenuAction=null;

	/**@return The lazily-created action representing the top-level help menu.*/
	public static Action getHelpMenuAction()
	{
		if(helpMenuAction==null)	//if there is no action, yet
		{
			helpMenuAction=new HelpMenuAction();	//create a new action 
		}
		return helpMenuAction;	//return the action
	}

	/**The map of lists of menu actions, keyed to parent actions.
	Top-level menu actions are keyed to <code>null</code>.
	*/
	private Map menuActionListMap=new HashMap();	//G***this would be better using a tree

	/**Retrieves a list of menu actions for the given parent menu action.
	If the map has no record of such parent action, it will be added to the map
		along with a corresponding list.
	@param parentAction The action that serves as the parent of the action, or
		<code>null</code> if the action is a top-level menu action.
	@return A list of child menu actions of the given parent.
	*/
	protected List getMenuActionList(final Action parentAction)
	{
		List actionList=(List)menuActionListMap.get(parentAction);	//see if there is a list of children for this parent
		if(actionList==null)	//if no children have been stored for this parent action
		{
			actionList=new ArrayList();	//create a new list for child actions
			menuActionListMap.put(parentAction, actionList);	//store the list in the map
		}
		return actionList;	//return the list of actions
	}

	/**The lazily-created list of tool actions.*/
	private ArrayList toolActionList;


	/**Default constructor.*/
	public ActionManager()
	{
		toolActionList=null;	//we don't have a list of tool actions until we need one
	}

	/**Removes all actions from the action manager.*/ 
	public void clear()
	{
		menuActionListMap.clear();	//clear the map of menu action lists
		toolActionList.clear();	//clear the list of tool actions
	}

	/**Adds to the manager an action representing a top-level menu.
	@param action The action to add as a top-level menu action.
	*/
	public void addMenuAction(final Action action)
	{
		addMenuAction(null, action);	//add the action without any parent
	}

	/**Adds to the manager an action representing a menu.
	If no parent action is specified, the action will be stored as a top-level
		action.
	@param parentAction The action that serves as the parent of the action, or
		<code>null</code> if the action is a top-level menu action.
	@param action The action to add to the parent's list of child actions.
	*/
	public void addMenuAction(final Action parentAction, final Action action)
	{
		getMenuActionList(parentAction).add(action);	//add the action to the parent's list of child actions
	}

	/**@return A read-only iterator to actions representing top-level menus.*/
	public Iterator getMenuActionIterator()
	{
		return getMenuActionIterator(null);	//return an iterator to all menu actions that do not have parents---the top-level menus
	}

	/**Returns an iterator to actions representing menus.
	If no parent action is specified, the actions returned will retpresent
		top-level actions.
	@param parentAction The action that serves as the parent of the actions, or
		<code>null</code> if an iterator to top-level menu actions should be returned.
	@return A read-only iterator to actions representing menus.
	*/
	public Iterator getMenuActionIterator(final Action parentAction)
	{
		final List actionList=(List)menuActionListMap.get(parentAction);	//see if there is a list of children for this parent
		final List sortedActionList=actionList!=null ? new ArrayList(actionList) : null;	//if there's a list, place it in a separate list for sorting
		if(sortedActionList!=null)	//if there are actions to sort
		{
			Collections.sort(sortedActionList, new ActionMenuOrderComparator(actionList));	//sort the actions by menu order, defaulting to the order they were in before sorting
		}
		return sortedActionList!=null ? Collections.unmodifiableList(sortedActionList).iterator() : IteratorUtilities.getEmptyIterator();	//return a read-only iterator to the sorted actions, if there are any
	}

	/**Merges a given action manager with this one, creating a new, merged action
		manager.
	@param actionManager The action manager to merge with this one.
	@return The new action manager result of the merger of this action manager
		and the given action manager.
	*/
	public ActionManager merge(final ActionManager actionManager)
	{
		final ActionManager mergedActionManager=(ActionManager)clone();	//clone this action manager
		final Iterator actionListEntryIterator=actionManager.menuActionListMap.entrySet().iterator();	//get an iterator to all action lists in the merging manager, keyed to actions
		while(actionListEntryIterator.hasNext())	//while there are more entries
		{
			final Map.Entry actionListEntry=(Map.Entry)actionListEntryIterator.next();	//get the next entry
			final Action parentAction=(Action)actionListEntry.getKey();	//get this parent action
			final List mergedActionList=mergedActionManager.getMenuActionList(parentAction);	//get the merged action manager's list of actions for this parent, creating one if it isn't present
			final Iterator actionIterator=actionManager.getMenuActionIterator(parentAction);	//get an iterator to child actions of this action
			if(actionIterator!=null)	//if there is a list of child actions
			{
				while(actionIterator.hasNext())	//while there are more actions
				{
					final Action action=(Action)actionIterator.next();	//get the next action
					if(!mergedActionList.contains(action))	//if the action is not already in the merged list
					{
						mergedActionList.add(action);	//add this action to the merged list
					} 
				}
			}
		}
		return mergedActionManager;	//return the merged action manager
	}

	/**Adds to the manager an action representing a tool.
	@param action The action to add as a tool action.
	*/
	public void addToolAction(final Action action)
	{
		if(toolActionList==null)	//if there is no tool action list
		{
			toolActionList=new ArrayList();	//create a new list of tool actions
		}
		toolActionList.add(action);	//add the action to the list of tool actions
	}

	/**Removes from the manager an action representing a tool.
	@param action The action to remove as a tool action.
	*/
	public void removeToolAction(final Action action)
	{
		if(toolActionList!=null)	//if there is a tool action list
		{
			toolActionList.remove(action);	//remove the action from the list of tool actions
		}
	}

	/**@return A read-only iterator to actions representing tools.*/
	public Iterator getToolActionIterator()
	{
		return toolActionList!=null ? Collections.unmodifiableList(toolActionList).iterator() : IteratorUtilities.getEmptyIterator();	//return a read-only iterator to the tool actions, if there are any
	}

	/**Returns a read-only sorted iterator to the given list of actions.
	@param actionList The list of actions from which the iterator should return
		actions, or <code>null</code> if no actions are available.
	@return A read-only iterator to any actions, or <code>null</code> if there are
		no actions.
	@see ActionMen
	 */
/*G***del; this is only needed for menu items, so there's no point in splitting it out
	protected static Iterator getSortedActionIterator(final List actionList)
	{
	final List actionList=(List)menuActionListMap.get(parentAction);	//see if there is a list of children for this parent
	final List sortedActionList=actionList!=null ? new ArrayList(actionList) : null;	//if there's a list, place it in a separate list for sorting
	if(sortedActionList!=null)	//if there are actions to sort
	{
		Collections.sort(sortedActionList, new ActionMenuOrderComparator(actionList));	//sort the actions by menu order, defaulting to the order they were in before sorting
	}
	return sortedActionList!=null ? Collections.unmodifiableList(sortedActionList).iterator() : null;	//return a read-only iterator to the sorted actions, if there are any
*/

	/**Creates a component, such as a button, to represent the given action.
	<p>Most methods should call <code>MenuUtilities.createComponent(Action)</code>
		or <code>ToolBarUtilities.createComponent(Action)</code>, as those methods
		correctly create separators for those contexts.</p>
	@param action The action for which a component should be created.
	@param The new component to represent the action.
	@see 
	*/
/*G***maybe fix later
	public static Component createComponent(final Action action)
	{
		return new JButton(action);	//return a new button to represent the action
	}
*/

	/**@return An independent copy of this action manager with the same action relationships.*/
	public Object clone()
	{
		try
		{ 
			ActionManager actionManager=(ActionManager)super.clone();	//create a cloned copy of this action manager
			actionManager.menuActionListMap=new HashMap();	//create a new map, which we'll fill with cloned lists
			final Iterator actionListEntryIterator=menuActionListMap.entrySet().iterator();	//get an entry to all action lists, keyed to actions
			while(actionListEntryIterator.hasNext())	//while there are more entries
			{
				final Map.Entry actionListEntry=(Map.Entry)actionListEntryIterator.next();	//get the next entry
				actionManager.menuActionListMap.put(actionListEntry.getKey(), ((ArrayList)actionListEntry.getValue()).clone());	//clone the list and put it in the new map
			}
			if(toolActionList!=null)	//if we have a list of tool actions
			{
				actionManager.toolActionList=(ArrayList)toolActionList.clone();	//clone our list of tool actions
			}
			return actionManager;	//return the cloned action manager
		}
		catch(CloneNotSupportedException e)
		{ 
			throw new AssertionError("Cloning is unexpectedly not supported.");
		}
	}

	/**Class to compare actions by menu order.
	@author Garret Wilson
	@see ActionManager#MENU_ORDER_PROPERTY
	*/
	protected static class ActionMenuOrderComparator extends DefaultOrderComparator
	{
		
		/**Constructs a comparator to compare actions based upon order or, by
			default, the order in which they appear in another list.
		@param defaultOrderList The list that determines the default order of the
			actions. This must not a different list than any list being sorted.
		*/
		public ActionMenuOrderComparator(final List defaultOrderList)
		{
			super(defaultOrderList);	//construct the parent class with the default order
		}

		/**Compares actions by menu order.
		Actions with no order specified will be sorted in front of those that do,
			and two menu actions with the same order or no specified order will be
			sorted in the order in which they were added to the manager.
		@param object1 The first object to be compared; must be an <code>Action.</code>.
		@param object2 The second object to be compared; must be an <code>Action.</code>
		@return A negative integer, zero, or a positive integer as the
			first argument is less than, equal to, or greater than the second. 
		@throws ClassCastException Thrown if the arguments' types prevent them from
			being compared by this comparator.
		*/
		public int compare(final Object object1, final Object object2)
		{
			final Action action1=(Action)object1;	//cast the objects to actions
			final Action action2=(Action)object2;
				//get the orders, if any
			final Integer orderInteger1=(Integer)ObjectUtilities.asInstance(action1.getValue(MENU_ORDER_PROPERTY), Integer.class);
			final Integer orderInteger2=(Integer)ObjectUtilities.asInstance(action2.getValue(MENU_ORDER_PROPERTY), Integer.class);
			if(orderInteger1==null)	//if the first action doesn't have an order
			{
				if(orderInteger2==null)	//if neither action has an order, sort by name
				{
					return super.compare(object1, object2);	//return the default order as specified by the provided default order list
				}
				else	//if the first action doesn't have an order, yet the second one does
				{
					return -1;	//put the first action first
				}
			}
			else	//if the first action has an order
			{
				if(orderInteger2==null)	//if the second action has no order
				{
					return 1;	//put the second action first
				}
				else	//if both actions have orders
				{
					final int order=orderInteger1.compareTo(orderInteger2);	//compare orders
					return order!=0 ? order : super.compare(object1, object2);	//return the order; if the order of both objects are the same, return the default order as specified by the provided default order list
				}
			}			
		}
	}

	/**Action for a top-level file menu.*/
	protected static class FileMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public FileMenuAction()
		{
			super("File");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "File menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "File and resource-related functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));  //set the mnemonic key G***i18n
//G***fix if needed			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
//G***fix			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK)); //add the accelerator G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(FILE_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

	/**Action for a top-level help menu.*/
	protected static class HelpMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public HelpMenuAction()
		{
			super("Help");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Help menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Helpful functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));  //set the mnemonic key G***i18n
//G***fix if needed			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
//G***del			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //add the accelerator G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(HELP_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

	/**Action that represents a separator.*/
	public static class SeparatorAction extends AbstractAction
	{
		/**Default constructor.*/
		public SeparatorAction()
		{
			this(-1);	//create action with no order
		}

		/**Order constructor.
		@param order The order of the menu, or -1 if there should be no order
		 */
		public SeparatorAction(final int order)
		{
			super("-");	//create the base class
			if(order>=0)	//if a valid order was given
			{
				putValue(MENU_ORDER_PROPERTY, new Integer(order));	//set the order
			}
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

}
