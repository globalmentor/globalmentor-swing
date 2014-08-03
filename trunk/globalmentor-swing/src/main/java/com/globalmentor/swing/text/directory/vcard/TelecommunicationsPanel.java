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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.awt.Containers;
import com.globalmentor.swing.*;
import com.globalmentor.swing.border.*;
import com.globalmentor.text.directory.vcard.*;

/**
 * A panel allowing entry of one or more telecommunication types (e.g. <code>TEL</code> and <code>EMAIL</code>) of a vCard <code>text/directory</code> profile
 * as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>, "vCard MIME Directory Profile".
 * @author Garret Wilson
 */
public class TelecommunicationsPanel extends ContentPanel {

	/** The action for adding a new telephone. */
	private final Action addTelephoneAction;

	/** @return The action for adding a new telephone. */
	public Action getAddTelephoneAction() {
		return addTelephoneAction;
	}

	/** The action for adding a new email address. */
	private final Action addEmailAction;

	/** @return The action for adding a new email address. */
	public Action getAddEmailAction() {
		return addEmailAction;
	}

	/** The action for removing an address. */
	//TODO fix	private final Action removeAddressAction;

	/** @return The action for removing an address. */
	//TODO fix		public Action getRemoveAddressAction() {return removeAddressAction;}

	protected final JToolBar buttonToolBar;

	/** @return The content component in which the telecommunications panels will be placed. */
	protected JPanel getContentPanel() {
		return (JPanel)getContentComponent();
	} //TODO why can't we return something else, here--why cast to a JPanel?

	/**
	 * Places the telephones in the panel.
	 * @param telephones The telephone numbers to display, or <code>null</code> if default telephones should be added.
	 */
	public void setTelephones(final Telephone[] telephones) {
		final Email[] emails = getEmails(); //get the emails we currently have
		setTelecommunications(telephones, emails); //set the telephones and add back the emails
	}

	/** @return An array of entered telephones. */
	public Telephone[] getTelephones() {
		final List telephoneList = new ArrayList(); //create a list into which to place the telephones
		final JPanel contentPanel = getContentPanel(); //get our content panel
		final int componentCount = contentPanel.getComponentCount(); //find out how many components there are
		for(int i = 0; i < componentCount; ++i) { //look at each component
			final Component component = contentPanel.getComponent(i); //get this component
			if(component instanceof TelephonePanel) { //if this panel holds a telephone
				final Telephone telephone = ((TelephonePanel)component).getTelephone(); //get this telephone
				if(telephone != null) { //if a telephone was entered
					telephoneList.add(telephone); //add this telephone to our list
				}
			}
		}
		return (Telephone[])telephoneList.toArray(new Telephone[telephoneList.size()]); //return an array of the telephones we collected
	}

	/**
	 * Places the emails in the panel.
	 * @param emails The email addresses to display, or <code>null</code> if default emails should be added.
	 */
	public void setEmails(final Email[] emails) {
		final Telephone[] telephones = getTelephones(); //get the telephones we currently have
		setTelecommunications(telephones, emails); //put the telephones back and add the emails
	}

	/** @return An array of entered emails. */
	public Email[] getEmails() {
		final List emailList = new ArrayList(); //create a list into which to place the emails
		final JPanel contentPanel = getContentPanel(); //get our content panel
		final int componentCount = contentPanel.getComponentCount(); //find out how many components there are
		for(int i = 0; i < componentCount; ++i) { //look at each component
			final Component component = contentPanel.getComponent(i); //get this component
			if(component instanceof EmailPanel) { //if this panel holds an email
				final Email email = ((EmailPanel)component).getEmail(); //get this email
				if(email != null) { //if an email was entered
					emailList.add(email); //add this email to our list
				}
			}
		}
		return (Email[])emailList.toArray(new Email[emailList.size()]); //return an array of the emails we collected
	}

	/**
	 * Places telephones and emails in the panel. If no telephones or emails are given, default telephone and email components will be added.
	 * @param telephones The telephone numbers to display, or <code>null</code> if default telephones should be added.
	 * @param emails The email addresses to display, or <code>null</code> if default emails should be added.
	 */
	public void setTelecommunications(final Telephone[] telephones, final Email[] emails) {
		getContentPanel().removeAll(); //remove all the components from the content panel TODO maybe remove the listeners first
		if(telephones != null) { //if telephones were given
			for(int i = 0; i < telephones.length; ++i) { //look at each telephone
				addTelephone(telephones[i]); //add this telephone
			}
		}
		if(telephones == null || telephones.length == 0) { //if no telephones were given, add the default telephones
			//add a work voice telephone
			final TelephonePanel workTelephonePanel = new TelephonePanel(null, EnumSet.of(Telephone.Type.WORK, Telephone.Type.VOICE));
			workTelephonePanel.setLabelsVisible(false); //turn off the labels
			addComponent(workTelephonePanel);
			//add a home voice telephone
			final TelephonePanel homeTelephonePanel = new TelephonePanel(null, EnumSet.of(Telephone.Type.HOME, Telephone.Type.VOICE));
			homeTelephonePanel.setLabelsVisible(false); //turn off the labels
			addComponent(homeTelephonePanel);
			//add a mobile voice telephone
			final TelephonePanel mobileTelephonePanel = new TelephonePanel(null, EnumSet.of(Telephone.Type.CELL, Telephone.Type.VOICE));
			mobileTelephonePanel.setLabelsVisible(false); //turn off the labels
			addComponent(mobileTelephonePanel);
		}
		if(emails != null) { //if emails were given
			for(int i = 0; i < emails.length; ++i) { //look at each email
				addEmail(emails[i]); //add this email
			}
		}
		if(emails == null || emails.length == 0) { //if no emails were given, add the default emails
			//add an Internet email address
			final EmailPanel internetEmailPanel = new EmailPanel();
			internetEmailPanel.setLabelsVisible(false); //turn off the labels
			addComponent(internetEmailPanel);
		}
	}

	/** Default constructor. */
	public TelecommunicationsPanel() {
		this(null, null); //construct a telecommunications panel with default telephones and email addresses
	}

	/**
	 * Telephone and email constructor.
	 * @param telephones The telephone numbers to display, or <code>null</code> if default telephones should be added.
	 * @param emails The email addresses to display, or <code>null</code> if default email addresses should be added.
	 */
	public TelecommunicationsPanel(final Telephone[] telephones, final Email[] emails) {
		super(new ModifiablePanel(new BasicGridBagLayout()), false); //construct the panel using a grid bag layout, but don't initialize the panel TODO is this right? double-check construction with a panel
		addTelephoneAction = new AddTelephoneAction();
		addEmailAction = new AddEmailAction();
		//TODO fix		removeAddressAction=new RemoveAddressAction();
		buttonToolBar = new JToolBar(JToolBar.HORIZONTAL);
		initialize(); //initialize the panel
		setTelecommunications(telephones, emails); //set the given telephones and emails
	}

	/** Initializes the user interface. */
	public void initializeUI() {
		super.initializeUI(); //do the default user interface initialization
		setBorder(Borders.createDefaultTitledBorder()); //set a titled border
		setTitle("Telecommunications"); //TODO i18n
		buttonToolBar.setFloatable(false);
		buttonToolBar.add(Box.createGlue()); //put glue before and after the button to center them 
		buttonToolBar.add(new JButton(getAddTelephoneAction()));
		buttonToolBar.add(new JButton(getAddEmailAction()));
		//TODO fix		buttonPanel.add(new JButton(getRemoveAddressAction()));
		buttonToolBar.add(Box.createGlue()); //put glue before and after the button to center them 
		add(buttonToolBar, BorderLayout.SOUTH);
		/*TODO fix gridbag
				add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				add(tabbedPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		*/
	}

	/** Updates the user interface. */
	public void updateStatus() {
		super.updateStatus(); //do the default status updating
		//TODO fix		getRemoveAddressAction().setEnabled(getTabbedPane().getTabCount()>1);	//don't allow all the tabs to be removed
	}

	/**
	 * Adds a telephone to the content panel.
	 * @param telephone The telephone to add.
	 * @return The telephone panel that represents the added telephone.
	 */
	public TelephonePanel addTelephone(final Telephone telephone) {
		final TelephonePanel telephonePanel = new TelephonePanel(telephone); //create a new telephone panel for this telephone
		telephonePanel.setLabelsVisible(false); //turn off the labels
		addComponent(telephonePanel); //add the panel to the content panel
		return telephonePanel; //return the panel we creatd for the telephone		
	}

	/**
	 * Adds an email to the content panel.
	 * @param email The email to add.
	 * @return The email panel that represents the added email.
	 */
	public EmailPanel addEmail(final Email email) {
		final EmailPanel emailPanel = new EmailPanel(email); //create a new email panel for this email
		emailPanel.setLabelsVisible(false); //turn off the labels
		addComponent(emailPanel); //add the panel to the content panel
		return emailPanel; //return the panel we creatd for the email		
	}

	/**
	 * Adds a component to the content panel, positioning it correctly based upon the other components present.
	 * @param component The component to add.
	 */
	protected void addComponent(final Component component) {
		//TODO use the new BasicGridBagLayout methods here
		//add the component to the content panel, in a location based upon the components already present 
		getContentPanel().add(
				component,
				new GridBagConstraints(0, getContentPanel().getComponentCount(), 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
						Containers.NO_INSETS, 0, 0));
		setModified(true); //show that we've been modified by the addition of this component
		/*TODO del; we may not even need this pack method any more, if revalidate() works
				if(getParentOptionPane()!=null) {	//if this panel is inside an option pane
					WindowUtilities.packWindow(this);	//pack the window we're inside, if there is one, to ensure there's enough room to view this component
				}
		*/
		//TODO del		invalidate();	//TODO testing
		//TODO del		repaint();	//repaint ourselves (important if we're inside a JOptionPane, for instance)
		revalidate(); //update the layout
	}

	/**
	 * Removes the currently selected address.
	 * @return <code>true</code> if the address was moved, or <code>false</code> if the action was cancelled or if there is only one address remaining.
	 */
	/*TODO fix
		public boolean removeAddress()
		{
			if(getTabbedPane().getTabCount()>1) {	//if we have more than one address
				final Component selectedComponent=getTabbedPane().getSelectedComponent();	//get the component selected in the tabbed pane
				if(selectedComponent!=null) {	//if a component is selected
						//if they really want to delete the address
					if(JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently remove this address?", "Remove Address", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {	//TODO i18n
						getTabbedPane().remove(selectedComponent);	//remove the selected component
						return true;	//show that we removed the address
					}
				}
			}
			return false;	//show that we didn't remove the address
		}
	*/

	/** Action for adding a telephone. */
	class AddTelephoneAction extends AbstractAction {

		/** Default constructor. */
		public AddTelephoneAction() {
			super("Add Phone"); //create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Add a telephone"); //set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Add a new telephone number."); //set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P)); //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //load the correct icon
		}

		/**
		 * Called when the action should be performed.
		 * @param actionEvent The event causing the action.
		 */
		public void actionPerformed(final ActionEvent actionEvent) {
			final TelephonePanel telephonePanel = new TelephonePanel(); //create a default telephone panel
			if(telephonePanel.editTelephoneType()) { //ask the user for the telephone type; if they accept the changes
				telephonePanel.setLabelsVisible(false); //turn off the labels
				addComponent(telephonePanel); //add the new telephone panel
			}
			telephonePanel.requestDefaultFocusComponentFocus(); //focus on the new telephone panel
		}
	}

	/** Action for adding an email. */
	class AddEmailAction extends AbstractAction {

		/** Default constructor. */
		public AddEmailAction() {
			super("Add Email"); //create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Add an email"); //set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Add a new email address."); //set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E)); //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EMAIL_ICON_FILENAME)); //load the correct icon
		}

		/**
		 * Called when the action should be performed.
		 * @param actionEvent The event causing the action.
		 */
		public void actionPerformed(final ActionEvent actionEvent) {
			final EmailPanel emailPanel = new EmailPanel(); //create a default email panel
			//TODO fix			if(emailPanel.editEmailType())	//ask the user for the email type; if they accept the changes
			{
				emailPanel.setLabelsVisible(false); //turn off the labels
				addComponent(emailPanel); //add the new email panel
			}
			emailPanel.requestDefaultFocusComponentFocus(); //focus on the new email panel
		}
	}

}
