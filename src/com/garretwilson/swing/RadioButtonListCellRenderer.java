package com.garretwilson.swing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.io.Serializable;

/**Renders an item in a list as a radio button.
	<p>Based on code from <code>DefaultListCellRenderer</code> version 1.17
	02/02/00 by Philip Milne and Hans Muller, copyright 1998-2000 Sun
	Microsystems, Inc.</p>
@author Garret Wilson
*/
public class RadioButtonListCellRenderer extends JRadioButton implements ListCellRenderer, Serializable
{
	protected static Border noFocusBorder;

	/**Default constructor.*/
	public RadioButtonListCellRenderer()
	{
	  super();  //do the default construction
		noFocusBorder=new EmptyBorder(1, 1, 1, 1);
	  setOpaque(true);
	  setBorder(noFocusBorder);
	}

	/**@return A renderer with the correct text for this choice.
	@param list The list component.
	@param value The value of this list item.
	@param index The index of this list item.
	@param isSelected Whether the list item is selected.
	@param cellHasFocus Whether the list item has the focus.
	*/
  public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
  {
		setComponentOrientation(list.getComponentOrientation());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setSelected(isSelected);  //show whether the item is selected
/*G***fix
	if (value instanceof Icon) {
	    setIcon((Icon)value);
	    setText("");
	}
	else {
	    setIcon(null);
	    setText((value == null) ? "" : value.toString());
	}
		final String string=getListCellRendererString(list, value, index, isSelected, cellHasFocus);  //get the string to display G***we have a new version of this method now
//G***assert the string is not null
		Debug.assert(component instanceof JLabel, "List cell renderer component not a JLabel");
		((JLabel)component).setText(string);  //set the correct text, assuming that the component is a JLabel (which is likely the implementation used by the default
		return component; //return the component, now using our custom text
  }
*/
		final String text=getListCellRendererString(list, value, index, isSelected, cellHasFocus);  //get the string to display
//G***assert the string is not null
		setText(text);  //set the correct text
		setEnabled(list.isEnabled());
	  setFont(list.getFont());
	  setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
		return this;  //return ourselves as the component
	}

	/**Retrieves text for a list item. This can be overridden in a derived class
		to return custom text for the specified value. This default version
		delegates to the one-parameter version.
	@param list The list component.
	@param value The value of this list item.
	@param index The index of this list item.
	@param isSelected Whether the list item is selected.
	@param cellHasFocus Whether the list item has the focus.
	@return The correct text for this list item.
	*/
  protected String getListCellRendererString(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
	{
		return getListCellRendererString(value);  //delegate to the simple version
	}

	/**Retrieves text for a list item. This can be overridden in a derived class
		to return custom text for the specified value. This default version
		is called from the multi-parameter version, and returns the object's default
		<code>toString()</code> value.
	@param value The value of this list item.
	@return The correct text for this list item.
	*/
  protected String getListCellRendererString(final Object value)
	{
		return value.toString();  //return the string value of the object by default
	}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void validate() {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void revalidate() {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void repaint(long tm, int x, int y, int width, int height) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void repaint(Rectangle r) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
	  // Strings get interned...
		if(propertyName=="text")
	    super.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

	/**Overridden for performance reasons. See the <code>DefaultListCellRenderer</code> <a href="DefaultListCellRenderer#override">Implementation Note</a>.*/
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

	/**A subclass of <code>RadioButtonListCellRenderer</code> that implements
		UIResource.
		This class does not implement <code>UIResource</code> directly so that
		applications can safely override the cellRenderer property with
		<code>RadioButtonListCellRenderer</code> subclasses.
	*/
	public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource
	{
	}
}
