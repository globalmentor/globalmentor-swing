package com.garretwilson.swing.unicode;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.garretwilson.awt.FontUtilities;
import com.garretwilson.text.unicode.*;

/**A renderer that displays Unicode code points in a table.
@author Garret Wilson
*/
public class UnicodeTableCellRenderer extends DefaultTableCellRenderer
{

	/**Returns the renderer component.
	This implementation assumes that the parent implementation always returns <code>this</code>.
	@param table The <code>JTable</code>.
	@param value The value to assign to the cell at <code>[row, column]</code>.
	@param isSelected <code>true</code> if cell is selected.
	@param hasFocus <code>true</code> if cell has focus.
	@param row The row of the cell to render.
	@param column The column of the cell to render.
	@return The table cell renderer.
	*/
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		final Component component=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	//do the default configuration of the render component
		if(value instanceof Integer)	//the value should be an integer object
		{
			final String text;	//we'll determine the value to display
			final Integer codePoint=((Integer)value).intValue();	//get the integer value of the code point
			final UnicodeCharacter unicodeCharacter=UnicodeData.getUnicodeCharacter(codePoint);	//get the character information for this code point			
			if(unicodeCharacter!=null)	//if we have a description of this code point
			{
				if(unicodeCharacter.isControl())	//if this is a control character
				{
					text=unicodeCharacter.getUniqueCharacterName();	//use the unique character name of the character TODO update the font, maybe
				}
				else	//if this is not a control character
				{
					final char character=(char)unicodeCharacter.getCodeValue();	//cast the Unicode code point to a character
					text=String.valueOf(character);	//get a string representing that character TODO fix to work with extended Unicode code points
					setFont(FontUtilities.getFont(character, table.getFont()));	//make sure the font supports the character
				}
			}
			else	//if we don't know anything about this code point
			{
				text=UnicodeCharacter.getCodePointString(codePoint);	//just list the code point value TODO use an "unassigned" icon
			}
			setValue(text);	//update the value
		}
		return component;	//return the render component
	}
	
}
