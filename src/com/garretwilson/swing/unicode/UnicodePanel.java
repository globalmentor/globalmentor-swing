package com.garretwilson.swing.unicode;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**A panel for displaying characters based upon Unicode code points.
@author Garret Wilson
*/
public class UnicodePanel extends JPanel
{
	TableModel unicodeTableModel=new UnicodeTableModel();
  BorderLayout borderLayout = new BorderLayout();
  JTable unicodeTable = new JTable();
	JScrollPane scrollPane=new JScrollPane();

	/**Default constructor.*/
	public UnicodePanel()
	{
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
	}

	/**Initializes the UI.*/
  private void jbInit() throws Exception
  {
		unicodeTable.setModel(unicodeTableModel);
		unicodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setLayout(borderLayout);
		scrollPane.getViewport().add(unicodeTable);
//G***fix		scrollPane.setPreferredSize(new Dimension(430, 200));
//G***fix		scrollPane.setPreferredSize(new Dimension(430, unicodeTable.getPreferredScrollableViewportSize().height));
//G***fix		scrollPane.setPreferredSize(unicodeTable.getPreferredScrollableViewportSize()); //G***testing
    this.add(scrollPane, BorderLayout.CENTER);
  }
}