package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.dublincore.*;
import com.garretwilson.rdf.version.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.rdf.RDFPanel;
import com.garretwilson.text.CharacterConstants;

/**A generic panel for displaying information about, for example, an application.
<p>This panel recognizes the following resource properties:</p>
<dl>
	<dt><code>dc:title</code></dt> <dd>The title to display in the panel.</dd>
	<dt><code>ver:version</code></dt> <dd>The vesion to display in the panel.</dd>
	<dt><code>dc:rights</code></dt> <dd>The copyright information to display in the panel.</dd>
</dl>
@author Garret Wilson
*/
public class AboutPanel extends BasicPanel implements CharacterConstants
{

	/**The action for showing the properties.*/
	private final Action propertiesAction;

		/**@return The action for showing the properties.*/
		public Action getPropertiesAction() {return propertiesAction;}

	/**The resource the panel represents, or <code>null</code> if there is no
		resource represented.
	*/
	private RDFResource resource=null;

		/**@return The resource the panel represents, or <code>null</code> if there
			is no resource represented.
		*/
		public RDFResource getResource() {return resource;}

		/**Sets the represented resource and updates the displayed information.
		@param newResource The new resource represented, or <code>null</code> if 
			there is no resource to represent.
		*/
		public void setResource(final RDFResource newResource)
		{
			if(resource!=newResource)	//if the resource is actually changing
			{
				resource=newResource;	//save the resource
				if(resource!=null)	//if there is a new resource
				{
					final RDFObject titleObject=DCUtilities.getTitle(resource);	//get the title object
					setTitle(titleObject!=null ? titleObject.toString() : null);	//set the title
					final RDFObject versionObject=VersionUtilities.getVersion(resource);	//get the version object
					setVersion(versionObject!=null ? versionObject.toString() : null);	//set the version
					final RDFObject rightsObject=DCUtilities.getRights(resource);	//get the rights
					setCopyright(rightsObject!=null ? rightsObject.toString() : null);	//set the copyright
				}
				updateStatus();	//update our status based upon whether we now have a resource
			}
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

	private final JTextArea titleLabel;
	private final JLabel versionLabel;
	private final JTextArea copyrightLabel;
	private final JLabel infoLabelLabel;
	private final LinkLabel infoLabel;
	private final JLabel webLabelLabel;
	private final LinkLabel webLabel;

	/**Default constructor.*/
  public AboutPanel()
  {
  	this(null);	//construct the panel without a resource
  }

	/**Resource constructor.
	@param resource The resource about which information should be displayed, or
		<code>null</code> if information will be supplied via class methods.
	*/
	public AboutPanel(final RDFResource resource)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		propertiesAction=new PropertiesAction();
		titleLabel = new JTextArea();
		versionLabel = new JLabel();
		copyrightLabel = new JTextArea();
		infoLabelLabel = new JLabel();
		infoLabel = new LinkLabel();
		webLabelLabel = new JLabel();
		webLabel = new LinkLabel();		
		initialize();	//initialize the panel
		setResource(resource);	//set the given resource
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setPreferredSize(new Dimension(400, 200));
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
		copyrightLabel.setText("Copyright "+COPYRIGHT_SIGN+" 1999-2003 GlobalMentor, Inc. All Rights Reserved.");
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
		final JButton propertiesButton=new JButton(getPropertiesAction());	//create a button for displaying the properties
		add(titleLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(propertiesButton, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(versionLabel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(copyrightLabel, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
		add(webLabelLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
		add(webLabel, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(infoLabelLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
		add(infoLabel, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**Updates the user interface.*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default status updating
		getPropertiesAction().setEnabled(getResource()!=null);	//only allow access to properties if there is a resource
	}

	/**Action for showing resource properties.*/
	class PropertiesAction extends AbstractAction
	{
		/**Default constructor.*/
		public PropertiesAction()
		{
			super("Properties...");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "View properties.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "View the metadata properties.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('p'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PROPERTY_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final RDFResource resource=getResource();	//get our resource
			if(resource!=null)	//if we have a resource
			{
				final RDFPanel rdfPanel=new RDFPanel(new RDFResourceModel(resource));  //create a new panel in which to show the resource
				rdfPanel.setEditable(false);	//don't allow this RDF to be edited
					//show the properties in an information dialog
				OptionPane.showMessageDialog(AboutPanel.this, rdfPanel, (getTitle()!=null ? getTitle()+' ' : "")+"Properties", OptionPane.INFORMATION_MESSAGE);	//G***i18n
			}
		}
	}

}