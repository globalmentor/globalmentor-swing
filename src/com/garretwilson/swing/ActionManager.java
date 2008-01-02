package com.garretwilson.swing;

import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.garretwilson.util.DefaultOrderComparator;
import com.globalmentor.java.*;

import static com.garretwilson.util.IteratorUtilities.*;
import static com.globalmentor.java.Objects.*;

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
	public final static String MENU_ORDER_PROPERTY=ActionManager.class.getName()+Java.PACKAGE_SEPARATOR+"order";

		//top-level menu order
	public final static int FILE_MENU_ACTION_ORDER=100;
	public final static int EDIT_MENU_ACTION_ORDER=200;
	public final static int INSERT_MENU_ACTION_ORDER=300;
	public final static int VIEW_MENU_ACTION_ORDER=400;
	public final static int TOOL_MENU_ACTION_ORDER=500;
	public final static int CONFIGURE_MENU_ACTION_ORDER=600;
	public final static int WINDOW_MENU_ACTION_ORDER=700;
	public final static int HELP_MENU_ACTION_ORDER=1000;

		//file menu order
	public final static int FILE_NEW_MENU_ACTION_ORDER=100;
	public final static int FILE_OPEN_MENU_ACTION_ORDER=200;
	public final static int FILE_CLOSE_MENU_ACTION_ORDER=300;
	public final static int FILE_SAVE_MENU_ACTION_ORDER=400;
	public final static int FILE_SAVE_AS_MENU_ACTION_ORDER=500;
	public final static int FILE_REVERT_MENU_ACTION_ORDER=600;
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

	/**The lazily-created action representing the top-level edit menu.*/
	private static Action editMenuAction=null;

	/**@return The lazily-created action representing the top-level edit menu.*/
	public static Action getEditMenuAction()
	{
		if(editMenuAction==null)	//if there is no action, yet
		{
			editMenuAction=new EditMenuAction();	//create a new action 
		}
		return editMenuAction;	//return the action
	}

	/**The lazily-created action representing the top-level insert menu.*/
	private static Action insertMenuAction=null;

	/**@return The lazily-created action representing the top-level insert menu.*/
	public static Action getInsertMenuAction()
	{
		if(insertMenuAction==null)	//if there is no action, yet
		{
			insertMenuAction=new InsertMenuAction();	//create a new action 
		}
		return insertMenuAction;	//return the action
	}

	/**The lazily-created action representing the top-level view menu.*/
	private static Action viewMenuAction=null;

	/**@return The lazily-created action representing the top-level view menu.*/
	public static Action getViewMenuAction()
	{
		if(viewMenuAction==null)	//if there is no action, yet
		{
			viewMenuAction=new ViewMenuAction();	//create a new action 
		}
		return viewMenuAction;	//return the action
	}

	/**The lazily-created action representing the top-level configure menu.*/
	private static Action configureMenuAction=null;

	/**@return The lazily-created action representing the top-level configure menu.*/
	public static Action getConfigureMenuAction()
	{
		if(configureMenuAction==null)	//if there is no action, yet
		{
			configureMenuAction=new ConfigureMenuAction();	//create a new action 
		}
		return configureMenuAction;	//return the action
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
	private Map<Action, List<Action>> menuActionListMap=new HashMap<Action, List<Action>>();	//TODO would be better using a tree

	/**Retrieves a list of menu actions for the given parent menu action.
	If the map has no record of such parent action, it will be added to the map
		along with a corresponding list.
	@param parentAction The action that serves as the parent of the action, or
		<code>null</code> if the action is a top-level menu action.
	@return A list of child menu actions of the given parent.
	*/
	protected List<Action> getMenuActionList(final Action parentAction)
	{
		List<Action> actionList=menuActionListMap.get(parentAction);	//see if there is a list of children for this parent
		if(actionList==null)	//if no children have been stored for this parent action
		{
			actionList=new ArrayList<Action>();	//create a new list for child actions
			menuActionListMap.put(parentAction, actionList);	//store the list in the map
		}
		return actionList;	//return the list of actions
	}

	/**The lazily-created list of tool actions.*/
	private ArrayList<Action> toolActionList;

	/**Default constructor.*/
	public ActionManager()
	{
		toolActionList=null;	//we don't have a list of tool actions until we need one
	}

	/**Removes all actions from the action manager.*/ 
	public void clear()
	{
		menuActionListMap.clear();	//clear the map of menu action lists
		if(toolActionList!=null)	//if there is a list of tool actions
		{
			toolActionList.clear();	//clear the list of tool actions
		}
	}

	/**Adds to the manager an action representing a top-level menu.
	If the action already exists at the top level, no action occurs.
	@param action The action to add as a top-level menu action.
	@return The added menu action.
	*/
	public Action addMenuAction(final Action action)
	{
		return addMenuAction(null, action);	//add the action without any parent
	}

	/**Adds to the manager an action representing a menu.
	If no parent action is specified, the action will be stored as a top-level
		action.
	If the action already exists as a child of the given parent, no action occurs.
	@param parentAction The action that serves as the parent of the action, or
		<code>null</code> if the action is a top-level menu action.
	@param action The action to add to the parent's list of child actions.
	@return The added menu action.
	*/
	public Action addMenuAction(final Action parentAction, final Action action)
	{
		final List<Action> menuActionList=getMenuActionList(parentAction);	//get the parent's list of child actions
		if(!menuActionList.contains(action))	//if the list doesn't already contain the action
		{
			menuActionList.add(action);	//add the action to the parent's list of child actions
		}
		return action;	//return the added action
	}

	/**@return A read-only iterator to actions representing top-level menus.*/
	public Iterator<Action> getMenuActionIterator()
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
	public Iterator<Action> getMenuActionIterator(final Action parentAction)
	{
		final List<Action> actionList=menuActionListMap.get(parentAction);	//see if there is a list of children for this parent
		final List<Action> sortedActionList=actionList!=null ? new ArrayList<Action>(actionList) : null;	//if there's a list, place it in a separate list for sorting
		if(sortedActionList!=null)	//if there are actions to sort
		{
			Collections.sort(sortedActionList, new ActionMenuOrderComparator(actionList));	//sort the actions by menu order, defaulting to the order they were in before sorting
		}
		return sortedActionList!=null ? Collections.unmodifiableList(sortedActionList).iterator() : (Iterator<Action>)EMPTY_ITERATOR;	//return a read-only iterator to the sorted actions, if there are any
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
			//merge the menu actions
		for(final Map.Entry<Action, List<Action>> actionListEntry:menuActionListMap.entrySet())	//look at all action lists in the merging manager, keyed to actions
		{
			final Action parentAction=actionListEntry.getKey();	//get this parent action
			final List<Action> mergedActionList=mergedActionManager.getMenuActionList(parentAction);	//get the merged action manager's list of actions for this parent, creating one if it isn't present
			final Iterator<Action> actionIterator=actionManager.getMenuActionIterator(parentAction);	//get an iterator to child actions of this action
			if(actionIterator!=null)	//if there is a list of child actions
			{
				while(actionIterator.hasNext())	//while there are more actions
				{
					final Action action=actionIterator.next();	//get the next action
					if(!mergedActionList.contains(action))	//if the action is not already in the merged list
					{
						mergedActionList.add(action);	//add this action to the merged list
					} 
				}
			}
		}
			//merge the tool actions
		if(toolActionList!=null)	//if there is a list of tool actions
		{
			final Iterator<Action> toolActionEntryIterator=actionManager.toolActionList.iterator();	//get an iterator to all tool actions in the merging manager
			while(toolActionEntryIterator.hasNext())	//while there are more tool actions
			{
				final Action action=toolActionEntryIterator.next();	//get the next tool action
				mergedActionManager.addToolAction(action);	//add this action to the merged action manager, creating a list of tool actions if needed
			}
		}
		return mergedActionManager;	//return the merged action manager
	}

	/**Adds to the manager an action representing a tool.
	If the action already exists at the top level, no action occurs.
	@param action The action to add as a tool action.
	*/
	public void addToolAction(final Action action)
	{
		if(toolActionList==null)	//if there is no tool action list
		{
			toolActionList=new ArrayList<Action>();	//create a new list of tool actions
		}
		if(!toolActionList.contains(action))	//if the list doesn't already contain the action
		{
			toolActionList.add(action);	//add the action to the list of tool actions
		}
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
	public Iterator<Action> getToolActionIterator()
	{
		return toolActionList!=null ? Collections.unmodifiableList(toolActionList).iterator() : (Iterator<Action>)EMPTY_ITERATOR;	//return a read-only iterator to the tool actions, if there are any
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

	/**Adds toolbar components to the toolbar from the toolbar actions.
	The components are set not to receive focus.
	@param toolBar The toolbar to set up.
	@return The toolbar that was set up.
	@see #getToolActionIterator()
	*/
	public JToolBar addToolComponents(final JToolBar toolBar)
	{
		Component lastComponent=null;	//keep track of the last component we added
		final Iterator<Action> actionIterator=getToolActionIterator();	//get an iterator to the tool actions
		while(actionIterator.hasNext())	//while there are actions
		{
			final Action action=actionIterator.next();	//get the next action
			if(action instanceof ActionManager.SeparatorAction)		//if this is a separator action
			{
					//don't put two separators in a row, and don't put a separator as the first component 
				if(lastComponent!=null && !(lastComponent instanceof JSeparator))	//if this isn't the first component and it doesn't come before a separator
				{
					lastComponent=ToolBarUtilities.createToolBarSeparator(toolBar);	//create a toolbar separator
					toolBar.add(lastComponent);	//add the separator
				}				
			}
			else if(action instanceof ComponentAction)		//if this is a component action
			{
				final ComponentAction<?> componentAction=(ComponentAction<?>)action;	//get the action as a component action
				lastComponent=componentAction.addComponent(toolBar);	//tell the action to add a component to the toolbar
				lastComponent.setFocusable(false);	//don't allow this component to receive focus TODO see if we want this here or in the action's addComponent() method				
			}
			else	//if this is a normal action
			{
				lastComponent=toolBar.add(action);	//add this action
				lastComponent.setFocusable(false);	//don't allow this component to receive focus
			}			
		}
		return toolBar;	//return the toolbar we initialized
	}

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
			actionManager.menuActionListMap=new HashMap<Action, List<Action>>();	//create a new map, which we'll fill with cloned lists
			for(final Map.Entry<Action, List<Action>> actionListEntry:menuActionListMap.entrySet())	//look at all action lists, keyed to actions
			{
				actionManager.menuActionListMap.put(actionListEntry.getKey(), (ArrayList<Action>)((ArrayList<Action>)actionListEntry.getValue()).clone());	//clone the list and put it in the new map
			}
			if(toolActionList!=null)	//if we have a list of tool actions
			{
				actionManager.toolActionList=(ArrayList<Action>)toolActionList.clone();	//clone our list of tool actions
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
	protected static class ActionMenuOrderComparator extends DefaultOrderComparator<Action>
	{
		
		/**Constructs a comparator to compare actions based upon order or, by
			default, the order in which they appear in another list.
		@param defaultOrderList The list that determines the default order of the
			actions. This must not a different list than any list being sorted.
		*/
		public ActionMenuOrderComparator(final List<Action> defaultOrderList)
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
		public int compare(final Action action1, final Action action2)
		{
				//get the orders, if any
			final Integer orderInteger1=asInstance(action1.getValue(MENU_ORDER_PROPERTY), Integer.class);
			final Integer orderInteger2=asInstance(action2.getValue(MENU_ORDER_PROPERTY), Integer.class);
			if(orderInteger1==null)	//if the first action doesn't have an order
			{
				if(orderInteger2==null)	//if neither action has an order, sort by name
				{
					return super.compare(action1, action2);	//return the default order as specified by the provided default order list
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
					return order!=0 ? order : super.compare(action1, action2);	//return the order; if the order of both objects are the same, return the default order as specified by the provided default order list
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

	/**Action for a top-level edit menu.*/
	protected static class EditMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditMenuAction()
		{
			super("Edit");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));  //set the mnemonic key G***i18n
//G***fix if needed			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
//G***fix			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK)); //add the accelerator G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(EDIT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

	/**Action for a top-level insert menu.*/
	protected static class InsertMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public InsertMenuAction()
		{
			super("Insert");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Insert menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Insert functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));  //set the mnemonic key G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(INSERT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

	/**Action for a top-level view menu.*/
	protected static class ViewMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public ViewMenuAction()
		{
			super("View");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "View menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "View functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));  //set the mnemonic key G***i18n
//G***fix if needed			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
//G***fix			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK)); //add the accelerator G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(VIEW_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

	/**Action for a top-level configure menu.*/
	protected static class ConfigureMenuAction extends AbstractAction
	{
		/**Default constructor.*/
		public ConfigureMenuAction()
		{
			super("Configure");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Configuration menu");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Configuration functions.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
//G***fix if needed			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
//G***fix			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK)); //add the accelerator G***i18n
			putValue(MENU_ORDER_PROPERTY, new Integer(CONFIGURE_MENU_ACTION_ORDER));	//set the order
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
