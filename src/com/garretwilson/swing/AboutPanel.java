package com.garretwilson.swing;

import java.awt.*;
import java.text.MessageFormat;
import javax.swing.*;
import java.awt.event.*;
import com.garretwilson.swing.LinkLabel;
import com.garretwilson.text.CharacterConstants;

/**A generic panel for displaying information about, for example, an application.
@author Garret Wilson
*/
public class AboutPanel extends JPanel implements CharacterConstants
{

	/**@return The copyright text, or <code>null</code> if there is no text.*/
	public String getCopyright()
	{
		return copyrightLabel.isVisible() ? versionLabel.getText() : null;  //if the label isn't visible, there effectively is no text
	}

	/**Sets the copyright text.
	@param newCopyright The new copyright text, or <code>null</code> if there
		should be no copyright text.
	*/
	public void setCopyright(final String newCopyright)
	{
		copyrightLabel.setVisible(newCopyright!=null);  //show or hide the label depending on the presence of text
		if(newCopyright!=null)  //if there is new text
			copyrightLabel.setText(newCopyright); //update the text
	}

	/**@return The title text, such as the application name, or <code>null</code>
		if there is no text.
	*/
	public String getTitle()
	{
		return titleLabel.isVisible() ? titleLabel.getText() : null;  //if the label isn't visible, there effectively is no text
	}

	/**Sets the title text.
	@param newTitle The new title text, such as the application name, or
		<code>null</code> if there should be no title.
	*/
	public void setTitle(final String newTitle)
	{
		titleLabel.setVisible(newTitle!=null);  //show or hide the label depending on the presence of text
		if(newTitle!=null)  //if there is new text
			titleLabel.setText(newTitle); //update the text
	}

	/**@return The version text, or <code>null</code> if there is no text.*/
	public String getVersion()
	{
		return versionLabel.isVisible() ? versionLabel.getText() : null;  //if the label isn't visible, there effectively is no text
	}

	/**Sets the version text.
	@param newVersion The new version text, or <code>null</code> if there
		should be no version text.
	*/
	public void setVersion(final String newVersion)
	{
		versionLabel.setVisible(newVersion!=null);  //show or hide the label depending on the presence of text
		if(newVersion!=null)  //if there is new text
		  versionLabel.setText(newVersion); //update the text
	}

  GridBagLayout backGridBagLayout = new GridBagLayout();
  JTextArea titleLabel = new JTextArea();
  JLabel versionLabel = new JLabel();
  JTextArea copyrightLabel = new JTextArea();
  JLabel infoLabelLabel = new JLabel();
  LinkLabel infoLabel = new LinkLabel();
  JLabel webLabelLabel = new JLabel();
  LinkLabel webLabel = new LinkLabel();

	/**Default constructor.*/
  public AboutPanel()
  {
		jbInit();
//G***fix		versionLabel.setText(MessageFormat.format("Beta Version {0} Build {1}", new Object[]{VERSION, BUILD}));	//show the correct version G***i18n
  }

	/**Initializes the user interface.*/
  void jbInit()
  {
    this.setLayout(backGridBagLayout);
		this.setPreferredSize(new Dimension(400, 200));
    titleLabel.setBackground(getBackground());
    titleLabel.setFont(new java.awt.Font("SansSerif", 1, 26));
    titleLabel.setEditable(false);
    titleLabel.setText("Application Title");
    titleLabel.setColumns(1);
    titleLabel.setLineWrap(true);
    titleLabel.setRows(1);
    titleLabel.setWrapStyleWord(true);
    copyrightLabel.setBackground(getBackground());
    copyrightLabel.setFont(new java.awt.Font("Serif", 0, 12));
    copyrightLabel.setEditable(false);
    copyrightLabel.setText("Copyright "+COPYRIGHT_SIGN+" 1999-2002 GlobalMentor, Inc. All Rights Reserved.");
    copyrightLabel.setColumns(1);
    copyrightLabel.setLineWrap(true);
    copyrightLabel.setRows(1);
    copyrightLabel.setWrapStyleWord(true);
    infoLabelLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    infoLabelLabel.setText("Info:");
    infoLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    infoLabel.setForeground(Color.blue);
		infoLabel.setRolloverColor(Color.red);
    infoLabel.setText("info@globalmentor.com");
		infoLabel.setTarget("mailto:info@globalmentor.com");
    webLabelLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    webLabelLabel.setText("WWW:");
    webLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    webLabel.setForeground(Color.blue);
		webLabel.setRolloverColor(Color.red);
    webLabel.setText("http://www.globalmentor.com");
		webLabel.setTarget("http://www.globalmentor.com");
    versionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
    versionLabel.setText("Version XX Build XX");
    this.add(titleLabel,       new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(versionLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(copyrightLabel,         new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
    this.add(webLabelLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
    this.add(webLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(infoLabelLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
    this.add(infoLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }
}