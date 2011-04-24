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

package com.globalmentor.swing.text.directory.vcard;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.JTabbedPane;

import com.globalmentor.model.LocaledText;
import com.globalmentor.model.Verifiable;
import com.globalmentor.swing.*;
import com.globalmentor.text.directory.vcard.*;

/**A panel containing panels to edit fields for a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class VCardPanel extends TabbedViewPanel<VCard> implements Verifiable
{

	/**The view in which the VCard name and address information is shown.*/
	public final static int NAME_ADDRESS_MODEL_VIEW=-1;
	/**The view in which the explanation information is shown.*/
	public final static int EXPLANATORY_MODEL_VIEW=-2;

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

	/**Default constructor.*/
	public VCardPanel()
	{
		super(new VCard(), false);  //create a content panel with a default vCard, but don't initializing it
		nameAddressPanel=new NameAddressPanel();
		identificationPanel=nameAddressPanel.getIdentificationPanel();
		addressesPanel=nameAddressPanel.getAddressesPanel();
		organizationPanel=nameAddressPanel.getOrganizationPanel();
		telecommunicationsPanel=nameAddressPanel.getTelecommunicationsPanel();
		explanatoryPanel=new ExplanatoryPanel();
		addSupportedModelViews(new int[]{NAME_ADDRESS_MODEL_VIEW, EXPLANATORY_MODEL_VIEW});	//show which views we support
//G***del when works		setDefaultFocusComponent(nameAddressPanel);
		initialize(); //initialize the panel
//G***del when works		setModified(false);	//show that the information has not yet been modified
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
			//TODO maybe create a ModifiableTabbedPane that won't force us to add these listeners here
		final PropertyChangeListener modifyModifiedPropertyChangeListener=getModifyModifiedPropertyChangeListener();	//create a property change listener to change the modified status when the modified property is set to true
		identificationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		addressesPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		organizationPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		telecommunicationsPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		explanatoryPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);
		addView(NAME_ADDRESS_MODEL_VIEW, "Name/Address", nameAddressPanel);	//G***i18n
		addView(EXPLANATORY_MODEL_VIEW, "Explanatory", explanatoryPanel);	//G***i18n
		setDefaultDataView(NAME_ADDRESS_MODEL_VIEW);	//set the name/address view as the default view
		super.initializeUI(); //do the default UI initialization
		getTabbedPane().setTabPlacement(JTabbedPane.TOP);	//place the tabs at the top of the panel
	}

//TODO---make the new ContainerUtilities.verifyDescendants() method select the tab if it desn't verify

	/**Loads the data from the model to the view, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void loadModel() throws IOException
	{
		super.loadModel();	//do the default loading
		final VCard vcard=getModel();	//get the data model
		getIdentificationPanel().setVCardName(vcard.getName());
		getIdentificationPanel().setFormattedName(vcard.getFormattedName());
		getIdentificationPanel().setNicknames(vcard.getNicknames().toArray(new LocaledText[vcard.getNicknames().size()]));
		getAddressesPanel().setAddresses(vcard.getAddresses(), vcard.getLabels());
		getOrganizationPanel().setOrganizationName(vcard.getOrganizationName());
		getOrganizationPanel().setUnits(vcard.getOrganizationUnits());
		getOrganizationPanel().setJobTitle(vcard.getTitle());
		getOrganizationPanel().setRole(vcard.getRole());
			//TODO create getTelephones() and getEmails() that return arrays
		final Telephone[] telephones=(Telephone[])vcard.getTelephones().toArray(new Telephone[vcard.getTelephones().size()]);
		final Email[] emails=(Email[])vcard.getEmails().toArray(new Email[vcard.getEmails().size()]);
		getTelecommunicationsPanel().setTelecommunications(telephones, emails);
		getExplanatoryPanel().setCategories(vcard.getCategories().toArray(new LocaledText[vcard.getCategories().size()]));
		getExplanatoryPanel().setNote(vcard.getNote());
		getExplanatoryPanel().setURL(vcard.getURL());
	}

	/**Stores the current data being edited to the model, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void saveModel() throws IOException
	{
		super.saveModel();	//do the default saving
		final VCard vcard=getModel();	//get the data model
		final LocaledText displayName=getIdentificationPanel().getFormattedName()!=null
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
		vcard.setNotes(getExplanatoryPanel().getNote());
		vcard.setURL(getExplanatoryPanel().getURL());
	}

	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
/*G***fix or del
	protected void onModelViewChange(final int oldView, final int newView)
	{
		super.onModelViewChange(oldView, newView);	//perform the default functionality
		switch(oldView)	//see what view we're changing from
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing from the WYSIWYG view
				book.close();	//to conserve memory, remove the content from the book
				break;
		}
	}
*/

}