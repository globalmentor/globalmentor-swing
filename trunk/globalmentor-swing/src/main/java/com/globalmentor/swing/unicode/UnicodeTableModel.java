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

package com.globalmentor.swing.unicode;

import javax.swing.table.*;
import com.globalmentor.java.Integers;

/**Table model for displaying Unicode code points.
@author Garret Wilson
*/
public class UnicodeTableModel extends AbstractTableModel
{

	/**The number of Unicode code points.*/ //TODO fix for extended Unicode code points
	protected final static int UNICODE_CODE_POINT_COUNT=0x10000;	  //TODO maybe put this in UnicodeConstants

	/**The number of rows of Unicode code points to show at a time.*/
	protected final static int ROW_COUNT=0x10;

	/**The number of columns of Unicode code points to show at a time.*/
	protected final static int COLUMN_COUNT=UNICODE_CODE_POINT_COUNT/ROW_COUNT;

	/**Default constructor.*/
  public UnicodeTableModel()
	{
	}

	/**@return The number of rows to display.*/
  public int getRowCount()
	{
		return ROW_COUNT; //return the number of rows to display
	}

	/**@return The number of columns to display.*/
	public int getColumnCount()
	{
		return COLUMN_COUNT; //return the number of columns to display
	}

	/**Determines the row of a particular code point.
	@param codePoint A Unicode code point.
	@return The table row in which the code point is shown.
	*/
	public int getRow(final int codePoint)
	{
		return codePoint%ROW_COUNT;
	}

	/**Determines the column of a particular code point.
	@param codePoint A Unicode code point.
	@return The table column in which the code point is shown.
	*/
	public int getColumn(final int codePoint)
	{
		return codePoint/ROW_COUNT;
	}

	/**Returns the class of objects being displayed in a given column.
	This version always returns the <code>Integer</code> class.
	@param columnIndex The column being queried.
	@return The <code>Integer</code> class.
	*/
	public Class<?> getColumnClass(int columnIndex)
	{
		return Integer.class;	//return the Integer class 
	}

	/**Returns the Unicode data to display in the given cell.
	@param rowIndex The row of the cell.
	@param columnIndex The column of the cell.
	@return The value to display in the given cell.
	*/
  public Object getValueAt(int rowIndex, int columnIndex)
	{
		final int codePoint=(ROW_COUNT*columnIndex)+rowIndex; //find out which Unicode code point should appear in this cell
		return Integer.valueOf(codePoint);  //return an integer representing this code point
	}

	/**Returns the name of the column.
	@param columnIndex The column to name.
	@return The starting Unicode code point value of this row in hex.
	*/
	public String getColumnName(int columnIndex)
	{
//TODO fix		return Integer.toHexString(columnIndex%0x10).toUpperCase();  //show the hex values 0...F repeatedly
		return Integers.toHexString(ROW_COUNT*columnIndex, 4).toUpperCase(); //show the starting Unicode code point value of this row in hex
//TODO del 		return Integer.toHexString(ROW_COUNT*columnIndex).toUpperCase();
	}

/*TODO fix
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col) {return true;}
            public void setValueAt(Object aValue, int row, int column) {
                System.out.println("Setting value to: " + aValue);
                data[row][column] = aValue;
*/
}