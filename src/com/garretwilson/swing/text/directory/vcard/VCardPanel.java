package com.garretwilson.swing.text.directory.vcard;

import java.beans.PropertyChangeListener;
import javax.swing.*;
import com.garretwilson.swing.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.util.*;

/**A panel containing panels to edit fields for a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class VCardPanel extends ContentPanel implements Verifiable
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
/*G***del when ModifiablePanel works
	public void setModified(final boolean newModified)	//TODO see why we can't just remove this and use the BasicPanel version (for some reason, name changes no longer will update the status without this)
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
*/
	/**Sets the vCard information that appears in the component.
	@param vcard The vCard information to display, or <code>null</code> if
		default information should be displayed.
	*/
	public void setVCard(final VCard vcard)
	{
		getIdentificationPanel().setVCardName(vcard.getName());
		getIdentificationPanel().setFormattedName(vcard.getFormattedName());
		getIdentificationPanel().setNicknames(LocaleText.toLocaleTextArray(vcard.getNicknameList()));
		getAddressesPanel().setAddresses(vcard.getAddresses(), vcard.getLabels());
		getOrganizationPanel().setOrganizationName(vcard.getOrganizationName());
		getOrganizationPanel().setUnits(vcard.getOrganizationUnits());
		getOrganizationPanel().setJobTitle(vcard.getTitle());
		getOrganizationPanel().setRole(vcard.getRole());
			//TODO create getTelephones() and getEmails() that return arrays
		final Telephone[] telephones=(Telephone[])vcard.getTelephoneList().toArray(new Telephone[vcard.getTelephoneList().size()]);
		final Email[] emails=(Email[])vcard.getEmailList().toArray(new Email[vcard.getEmailList().size()]);
		getTelecommunicationsPanel().setTelecommunications(telephones, emails);
		getExplanatoryPanel().setCategories(LocaleText.toLocaleTextArray(vcard.getCategoryList()));
		getExplanatoryPanel().setNote(vcard.getNote());
		getExplanatoryPanel().setURL(vcard.getURL());
	}

	/**@return A vCard object representing the information being edited.*/
	public VCard getVCard()
	{
		final VCard vcard=new VCard();	//create a new vCard
		final LocaleText displayName=getIdentificationPanel().getFormattedName()!=null
				? getIdentificationPanel().getFormattedName()		//use the formatted name as the display name
				: getOrganizationPanel().getOrganizationName();		//use the company name if there is no formatted name
			//TODO decide how to come up with a display name if there is neither formatted name nor organization name
		vcard.setDisplayName(displayName);
		vcard.setName(getIdentificationPanel().getVCardName());
		vcard.setFormattedName(getIdentificationPanel().getFormattedName());
		vcard.setNicknames(getIdentificationPanel().getNicknames());
		vcard.setAddresses(getAddressesPanel().getAddresses());
		vcard.setLabels(getAddressesPanel().getLabels());
		vcard.setOrganizationName(getOrganizationPanel().getOrganizationName());
		vcard.setOrganizationUnits(getOrganizationPanel().getUnits());
		vcard.setTitle(getOrganizationPanel().getJobTitle());
		vcard.setRole(getOrganizationPanel().getRole());
		vcard.setTelephones(getTelecommunicationsPanel().getTelephones());
		vcard.setEmails(getTelecommunicationsPanel().getEmails());
		vcard.setCategories(getExplanatoryPanel().getCategories());
		vcard.setNote(getExplanatoryPanel().getNote());
		vcard.setURL(getExplanatoryPanel().getURL());
		return vcard;	//return the vcard we constructed
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
		setDefaultFocusComponent(nameAddressPanel);
		initialize(); //initialize the panel
		setModified(false);	//show that the information has not yet been modified
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
			//TODO maybe create a ModifiableTabbedPane that won't force us to add these listeners here
		final PropertyChangeListener modifyModifiedPropertyChangeListener=getModifyModifiedPropertyChangeListener();	//create a property change listener to change the modified status when the modified property is set to true
		identificationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		addressesPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		organizationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		telecommunicationsPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		explanatoryPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		getTabbedPane().addTab("Name/Address", nameAddressPanel);	//G***i18n
		getTabbedPane().addTab("Explanatory", explanatoryPanel);	//G***i18n
	}

//TODO---make the new ContainerUtilities.verifyDescendants() method select the tab if it desn't verify

	/**Verifies the component.
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	*/
/*G***del when works---the new BasicPanel functionality should automatically verify all descendant components, even those on tabs
	public boolean verify()
	{
		if(!nameAddressPanel.verify())	//if the name/address panel doesn't verify
		{
			getTabbedPane().setSelectedComponent(nameAddressPanel);	//select the name/address panel
			return false;	//show that verification failed
		}
		if(!explanatoryPanel.verify())	//if the explanatory panel doesn't verify
		{
			getTabbedPane().setSelectedComponent(explanatoryPanel);	//select the explanatory panel
			return false;	//show that verification failed
		}
		return super.verify();  //if we couldn't find any problems, verify the parent class TODO we duplicate this functionality here to check and change the tabs if necessary---can we put something like this in BasicPanel, too?
	}
*/

}