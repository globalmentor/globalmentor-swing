package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.util.Debug;

/**An class for simple rendering of custom strings and icons in a list cell.
	A child class should override <code>getListCellRendererString()</code> and
	return the correct string for display. If that method is not overridden,
	the value will be converted to a string by default.
	<p>If it is desired that an icon be displayed, a child class should override
	<code>getListCellRendererIcon()</code> and return the correct icon for display.</p>
@author Garret Wilson
*/
public class SimpleListCellRenderer extends DefaultListCellRenderer
{

	/**Default constructor.*/
	public SimpleListCellRenderer()
	{
	}

	/**@return A renderer with the correct text for this list item.
	@param list The list component.
	@param value The value of this list item.
	@param index The index of this list item.
	@param isSelected Whether the list item is selected.
	@param cellHasFocus Whether the list item has the focus.
	*/
  public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
  {
		final Component component=super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //get the default component
		final String string=getListCellRendererString(list, value, index, isSelected, cellHasFocus);  //get the string to display
		final Icon icon=getListCellRendererIcon(list, value, index, isSelected, cellHasFocus);  //get the icon to display
//G***assert the string is not null
		Debug.assert(component instanceof JLabel, "List cell renderer component not a JLabel");
		final JLabel label=(JLabel)component; //cast the component to a label, assuming that the component is a JLabel (which is likely the implementation used by the default)
		label.setText(string);  //set the correct text
		if(icon!=null)  //if we have an icon
			label.setIcon(icon);  //set the icon
		return component; //return the component, now using our custom text
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

	/**Retrieves an icon for a list item. This can be overridden in a derived
		class to return a custom icon for the specified value. This default version
		delegates to the one-parameter version.
	@param list The list component.
	@param value The value of this list item.
	@param index The index of this list item.
	@param isSelected Whether the list item is selected.
	@param cellHasFocus Whether the list item has the focus.
	@return The correct icon for this list item, or <code>null</code> if no icon
		should be displayed.
	*/
  protected Icon getListCellRendererIcon(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
	{
		return getListCellRendererIcon(value);  //return the result of the simple version
	}

	/**Retrieves an icon for a list item. This can be overridden in a derived
		class to return a custom icon for the specified value. This default version
		is called from the multi-parameter version, and returns <code>null</code>.
	@param value The value of this list item.
	@return The correct icon for this list item, or <code>null</code> if no icon
		should be displayed.
	*/
  protected Icon getListCellRendererIcon(final Object value)
	{
		return null;  //we don't know the icon in this version
	}

}