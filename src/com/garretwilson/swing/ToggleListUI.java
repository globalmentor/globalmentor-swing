package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicListUI;

/**User interface class that allows list items to be toggled on and off, as if
	the control key were always pressed. Range selections with the shift key
	function normally.
	<p>This class is derived in part from sections of <code>BasicListUI</code>
	1.54 00/02/02, Copyright 1997-2000 Sun Microsystems, Inc.</p>
@author Garret Wilson
*/
public class ToggleListUI extends BasicListUI
{

	/**Register keyboard actions for the up and down arrow keys.
		The keys registered here move the lead selection instead of changing the
		selection.
	@see #installUI
	*/
	protected void installKeyboardActions()
	{
		super.installKeyboardActions(); //install the default keyboard actions
/*G***del
	  final InputMap inputMap=getInputMap(JComponent.WHEN_FOCUSED); //get the current input map
		SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, inputMap);
		ActionMap map=getActionMap(); //get the input map
		if(map!=null)
		{
	    SwingUtilities.replaceUIActionMap(list, map);
		}
*/
		final ActionMap map=createToggleActionMap();  //create our custom action map TODO later reimplement this to check to see if a map has already been created and stored in the UI, as does BasicListUI
				//G***find a way to merge these actions with the ones we're adding; maybe just
		SwingUtilities.replaceUIActionMap(list, map); //replace the list's map with our custom one
	}

	protected ActionMap createToggleActionMap()
	{
		final ActionMap map=new ActionMapUIResource();
		map.put("selectPreviousRow",
					new IncrementLeadSelectionAction("selectPreviousRow",
									 CHANGE_LEAD, -1));
		map.put("selectNextRow",
					new IncrementLeadSelectionAction("selectNextRow",
									 CHANGE_LEAD, 1));
		map.put("selectFirstRow",
					new HomeAction("selectFirstRow", CHANGE_LEAD));
		map.put("selectLastRow",
					new EndAction("selctLastRow", CHANGE_LEAD));  //G***fix spelling
		map.put("scrollUp",
					new PageUpAction("scrollUp", CHANGE_LEAD));
		map.put("scrollDown",
					new PageDownAction("scrollDown", CHANGE_LEAD));
		return map;
	}




    // Keyboard navigation actions.
    // NOTE: DefaultListSelectionModel.setAnchorSelectionIndex and
    // DefaultListSelectionModel.setLeadSelectionIndex both force the
    // new index to be selected. Because of this not all the bindings
    // could be appropriately implemented. Specifically those that
    // change the lead/anchor without selecting are not enabled.
    // Once this has been fixed the following actions will appropriately
    // work with selectionType == CHANGE_LEAD.

    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * change the lead, and not select it. */
    private static final int CHANGE_LEAD = 0;
    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * change the selection and lead. */
    private static final int CHANGE_SELECTION = 1;
    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * extend the selection from the anchor to the next index. */
    private static final int EXTEND_SELECTION = 2;


    /**
     * Action to increment the selection in the list up/down a row at
     * a type. This also has the option to extend the selection, or
     * only move the lead.
     */
    private static class IncrementLeadSelectionAction extends AbstractAction {
	/** Amount to offset, subclasses will define what this means. */
	protected int amount;
	/** One of CHANGE_LEAD, CHANGE_SELECTION or EXTEND_SELECTION. */
	protected int selectionType;

	protected IncrementLeadSelectionAction(String name, int type) {
	    this(name, type, -1);
	}

	protected IncrementLeadSelectionAction(String name, int type,
					       int amount) {
	    super(name);
	    this.amount = amount;
	    this.selectionType = type;
	}

	/**
	 * Returns the next index to select. This is based on the lead
	 * selected index and the <code>amount</code> ivar.
	 */
	protected int getNextIndex(JList list) {
	    int index = list.getLeadSelectionIndex();
	    int size = list.getModel().getSize();

	    if (index == -1) {
		if (size > 0) {
		    if (amount > 0) {
			index = 0;
		    }
		    else {
			index = size - 1;
		    }
		}
	    }
	    else {
		index += amount;
	    }
	    return index;
	}

	/**
	 * Ensures the particular index is visible. This simply forwards
	 * the method to list.
	 */
	protected void ensureIndexIsVisible(JList list, int index) {
	    list.ensureIndexIsVisible(index);
	}

	/**
	 * Invokes <code>getNextIndex</code> to determine the next index
	 * to select. If the index is valid (not -1 and < size of the model),
	 * this will either: move the selection to the new index if
	 * the selectionType == CHANGE_SELECTION, move the lead to the
	 * new index if selectionType == CHANGE_LEAD, otherwise the
	 * selection is extend from the anchor to the new index and the
	 * lead is set to the new index.
	 */
	public void actionPerformed(ActionEvent e) {
	    JList list = (JList)e.getSource();
	    int index = getNextIndex(list);
	    if (index >= 0 && index < list.getModel().getSize()) {
		ListSelectionModel lsm = list.getSelectionModel();

		if (selectionType == EXTEND_SELECTION) {
		    int anchor = lsm.getAnchorSelectionIndex();
		    if (anchor == -1) {
			anchor = index;
		    }
		    list.setSelectionInterval(anchor, index);
		    lsm.setAnchorSelectionIndex(anchor);
		    lsm.setLeadSelectionIndex(index);
		}
		else if (selectionType == CHANGE_SELECTION) {
		    list.setSelectedIndex(index);
		}
		else {
		    lsm.setLeadSelectionIndex(index);
		}
		ensureIndexIsVisible(list, index);
	    }
	}
    }


    /**
     * Action to move the selection to the first item in the list.
     */
    private static class HomeAction extends IncrementLeadSelectionAction {
	protected HomeAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    return 0;
	}
    }


    /**
     * Action to move the selection to the last item in the list.
     */
    private static class EndAction extends IncrementLeadSelectionAction {
	protected EndAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    return list.getModel().getSize() - 1;
	}
    }


    /**
     * Action to move up one page.
     */
    private static class PageUpAction extends IncrementLeadSelectionAction {
	protected PageUpAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    int index = list.getFirstVisibleIndex();
	    ListSelectionModel lsm = list.getSelectionModel();

	    if (lsm.getLeadSelectionIndex() == index) {
		Rectangle visRect = list.getVisibleRect();
		visRect.y = Math.max(0, visRect.y - visRect.height);
		index = list.locationToIndex(visRect.getLocation());
	    }
	    return index;
	}

	protected void ensureIndexIsVisible(JList list, int index) {
	    Rectangle visRect = list.getVisibleRect();
	    Rectangle cellBounds = list.getCellBounds(index, index);
	    cellBounds.height = visRect.height;
	    list.scrollRectToVisible(cellBounds);
	}
    }


    /**
     * Action to move down one page.
     */
    private static class PageDownAction extends IncrementLeadSelectionAction {
	protected PageDownAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    int index = list.getLastVisibleIndex();
	    ListSelectionModel lsm = list.getSelectionModel();

	    if (index == -1) {
		// Will happen if size < viewport size.
		index = list.getModel().getSize() - 1;
	    }
	    if (lsm.getLeadSelectionIndex() == index) {
		Rectangle visRect = list.getVisibleRect();
		visRect.y += visRect.height + visRect.height - 1;
		index = list.locationToIndex(visRect.getLocation());
		if (index == -1) {
		    index = list.getModel().getSize() - 1;
		}
	    }
	    return index;
	}

	protected void ensureIndexIsVisible(JList list, int index) {
	    Rectangle visRect = list.getVisibleRect();
	    Rectangle cellBounds = list.getCellBounds(index, index);
	    cellBounds.y = Math.max(0, cellBounds.y + cellBounds.height -
				    visRect.height);
	    cellBounds.height = visRect.height;
	    list.scrollRectToVisible(cellBounds);
	}
    }

	/**Creates a delegate that implements <code>MouseInputListener</code>.
		This version returns a mouse input listener that allows toggling of list
		items.
	*/
	protected MouseInputListener createMouseInputListener()
	{
		return new ToggleMouseInputHandler(); //return our special mouse input handler
	}

	/**Mouse input and focus handling for the list.
		This class extends the default functionality by always allowing toggling,
		not just when the control key is pressed.
	*/
	public class ToggleMouseInputHandler extends BasicListUI.MouseInputHandler
	{

		public void mousePressed(MouseEvent e)
		{
			if(list.isEnabled())  //if the list is enabled
			{
				if(SwingUtilities.isLeftMouseButton(e)) //if the left mouse button was pressed
				{
					/* Request focus before updating the list selection.  This implies
					 * that the current focus owner will see a focusLost() event
					 * before the lists selection is updated IF requestFocus() is
					 * synchronous (it is on Windows).  See bug 4122345
					 */
				  if(!list.hasFocus())  //if the list doesn't have focus
				  {
						list.requestFocus();  //give the list focus
				  }
				  int row = convertYToRow(e.getY());
				  if (row != -1)
					{
						list.setValueIsAdjusting(true);
						int anchorIndex = list.getAnchorSelectionIndex();
						if (e.isShiftDown() && (anchorIndex != -1))
						{
                    list.setSelectionInterval(anchorIndex, row);
						}
						else  //default to toggling
						{
							if (list.isSelectedIndex(row))
							{
								list.removeSelectionInterval(row, row);
							}
							else
							{
								list.addSelectionInterval(row, row);
							}
						}
					}
				}
			}
		}

		public void mouseReleased(MouseEvent e) {}	//TODO fix to replicate the new consumed check in BasicListUI.java	1.91 02/02/15


	}
}