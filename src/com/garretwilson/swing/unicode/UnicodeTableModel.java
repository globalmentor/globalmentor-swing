package com.garretwilson.swing.unicode;

import javax.swing.table.*;
import com.garretwilson.lang.IntegerUtilities;
import com.garretwilson.text.unicode.*;

/**Table model for displaying Unicode code points.
@author Garret Wilson
*/
public class UnicodeTableModel extends AbstractTableModel
{

	/**The number of Unicode code points.*/ //G***fix for extended Unicode code points
	protected final static int UNICODE_CODE_POINT_COUNT=0x10000;	  //G***maybe put this in UnicodeConstants

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

	/**Returns the Unicode data to display in the given cell.
	@param rowIndex The row of the cell.
	@param columnIndex The column of the cell.
	@return The value to display in the given cell.
	*/
  public Object getValueAt(int rowIndex, int columnIndex)
	{
		final char codePoint=(char)((ROW_COUNT*columnIndex)+rowIndex); //find out which Unicode code point should appear in this cell
		return String.valueOf(codePoint);  //G***testing
	}

	/**Returns the name of the column.
	@param columnIndex The column to name.
	@return The starting Unicode code point value of this row in hex.
	*/
	public String getColumnName(int columnIndex)
	{
//G***fix		return Integer.toHexString(columnIndex%0x10).toUpperCase();  //show the hex values 0...F repeatedly
		return IntegerUtilities.toHexString(ROW_COUNT*columnIndex, 4); //show the starting Unicode code point value of this row in hex
//G***del 		return Integer.toHexString(ROW_COUNT*columnIndex).toUpperCase();
	}

/*G***fix
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col) {return true;}
            public void setValueAt(Object aValue, int row, int column) {
                System.out.println("Setting value to: " + aValue);
                data[row][column] = aValue;
*/
}