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

package com.garretwilson.swing.unicode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.garretwilson.awt.FontUtilities;
import com.globalmentor.text.unicode.*;

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
		setBackground(null);	//remove any cached background color
		final Component component=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	//do the default configuration of the render component
		if(value instanceof Integer)	//the value should be an integer object
		{
			final String text;	//we'll determine the value to display
			final String description;	//we'll determine the description
			final Integer codePoint=((Integer)value).intValue();	//get the integer value of the code point
			final UnicodeCharacter unicodeCharacter=UnicodeData.getUnicodeCharacter(codePoint);	//get the character information for this code point			
			if(unicodeCharacter!=null)	//if we have a description of this code point
			{
				if(unicodeCharacter.isControl())	//if this is a control character
				{
//TODO del					text="<html>"+unicodeCharacter.getUniqueCharacterName()+"</html>";	//use the unique character name of the character TODO update the font, maybe					
//TODO del					text=unicodeCharacter.getCharacterName();	//show "<control>" in place of the character
					text="CTL";	//show "CTL" in place of the character TODO use an icon
					final Font baseFont=getFont();	//get the current font
					setFont(baseFont.deriveFont((float)Math.round(baseFont.getSize()/2)));	//reduce the size of the normal text
				}
				else	//if this is not a control character
				{
					final char character=(char)unicodeCharacter.getCodeValue();	//cast the Unicode code point to a character
					text=String.valueOf(character);	//get a string representing that character TODO fix to work with extended Unicode code points
					setFont(FontUtilities.getFont(character, table.getFont()));	//make sure the font supports the character
				}
				description=unicodeCharacter.getUniqueCharacterName();	//use the character name for the description
			}
			else	//if we don't know anything about this code point
			{
//TODO del				text=UnicodeCharacter.getCodePointString(codePoint);	//just list the code point value TODO use an "unassigned" icon
				text="";	//don't show anything for the unassigned code point
				setBackground(Color.LIGHT_GRAY);	//set the background color to indicate an unassigned code point
				description="?";	//show an unassigned description
			}
			setValue(text);	//update the value
			setToolTipText(description);	//show the description in the tooltip
		}
		setHorizontalAlignment(CENTER);	//center the text horizontally
		setVerticalTextPosition(CENTER);	//center the text vertically
		return component;	//return the render component
	}
	
}
