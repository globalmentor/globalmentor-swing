package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import com.garretwilson.io.*;
import com.garretwilson.lang.*;
import com.garretwilson.swing.*;
import com.garretwilson.text.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.util.*;

/**A panel containing panels to edit fields for a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class VCardPanel extends ContentPanel
{

	/**@return The tabbed pane component.*/
	protected JTabbedPane getTabbedPane()
	{
		return (JTabbedPane)getContentComponent();  //return the content component cast to a tabbed pane
	}

	/**The panel containing the name and address.*/
	private final NameAddressPanel nameAddressPanel;

	/**The panel containing the name identification information.*/
	private final IdentificationPanel identificationPanel;

		/**@return The panel containing the name identification information.*/
		public IdentificationPanel getIdentificationPanel() {return identificationPanel;}

	/**The panel containing the addresses.*/
	private final AddressesPanel addressesPanel;

		/**@return The panel containing the addresses.*/
		public AddressesPanel getAddressesPanel() {return addressesPanel;}

	/**The panel containing the organization information.*/
	private final OrganizationPanel organizationPanel;

		/**@return The panel containing the organization information.*/
		public OrganizationPanel getOrganizationPanel() {return organizationPanel;}

	/**The panel containing the telecommunications information.*/
	private final TelecommunicationsPanel telecommunicationsPanel;

		/**@return The panel containing the telecommunication information.*/
		public TelecommunicationsPanel getTelecommunicationsPanel() {return telecommunicationsPanel;}

	/**The panel containing the explanatory information.*/
	private final ExplanatoryPanel explanatoryPanel;

		/**The panel containing the explanatory information.*/
		public ExplanatoryPanel getExplanatoryPanel() {return explanatoryPanel;}

	/**Sets whether the object has been modified.
	This version sets the modified status of all contained panels to
		<code>false</code> if the new modified status is <code>false</code>.
	@param newModified The new modification status.
	*/
	public void setModified(final boolean newModified)
	{
		super.setModified(newModified);	//set the modified status
		if(newModified==false)	//if we are no longer modified
		{
			identificationPanel.setModified(newModified);	//tell the identification panel it is no longer modified
			addressesPanel.setModified(newModified);	//tell the addresses panel it is no longer modified
			organizationPanel.setModified(newModified);	//tell the organization panel it is no longer modified
			telecommunicationsPanel.setModified(newModified);	//tell the telecommunications panel it is no longer modified
			explanatoryPanel.setModified(newModified);	//tell the explanatory panel it is no longer modified
		}
	}

	/**Sets the vCard information that appears in the component.
	@param vcard The vCard information to display, or <code>null</code> if
		default information should be displayed.
	*/
	public void setVCard(final VCard vcard)
	{
		nameAddressPanel.getIdentificationPanel().setVCardName(vcard.getName());
		nameAddressPanel.getIdentificationPanel().setFormattedName(vcard.getFormattedName());
		nameAddressPanel.getIdentificationPanel().setNicknames(LocaleText.toLocaleTextArray(vcard.getNicknameList()));
		nameAddressPanel.getOrganizationPanel().setOrganizationName(vcard.getOrganizationName());
		nameAddressPanel.getOrganizationPanel().setUnits(vcard.getOrganizationUnits());
		nameAddressPanel.getOrganizationPanel().setJobTitle(vcard.getTitle());
		nameAddressPanel.getOrganizationPanel().setRole(vcard.getRole());
		final Telephone[] telephones=(Telephone[])vcard.getTelephoneList().toArray(new Telephone[vcard.getTelephoneList().size()]);
		final Email[] emails=(Email[])vcard.getEmailList().toArray(new Email[vcard.getEmailList().size()]);
		nameAddressPanel.getTelecommunicationsPanel().setTelecommunications(telephones, emails);
		explanatoryPanel.setCategories(LocaleText.toLocaleTextArray(vcard.getCategoryList()));
		explanatoryPanel.setNote(vcard.getNote());
	}

	/**Default constructor.*/
	public VCardPanel()
	{
		super(new JTabbedPane(), false);  //create a content panel with a tabbed pane, but don't initializing it
		nameAddressPanel=new NameAddressPanel();
		identificationPanel=nameAddressPanel.getIdentificationPanel();
		addressesPanel=nameAddressPanel.getAddressesPanel();
		organizationPanel=nameAddressPanel.getOrganizationPanel();
		telecommunicationsPanel=nameAddressPanel.getTelecommunicationsPanel();
		explanatoryPanel=new ExplanatoryPanel();
		initialize(); //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		final PropertyChangeListener modifyModifiedPropertyChangeListener=createModifyModifiedChangeListener();	//create a property change listener to change the modified status when the modified property is set to true
		identificationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		addressesPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		organizationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		telecommunicationsPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		explanatoryPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		getTabbedPane().addTab("Name/Address", nameAddressPanel);	//G***i18n
		getTabbedPane().addTab("Explanatory", explanatoryPanel);	//G***i18n
	}

}