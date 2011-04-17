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

import static java.util.Arrays.asList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import com.globalmentor.swing.*;
import com.globalmentor.swing.border.*;
import com.globalmentor.text.directory.vcard.*;

/**A panel allowing entry of one or more addresses of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressesPanel extends ContentPanel
{

	/**The action for adding a new address.*/
	private final Action addAddressAction;

		/**@return The action for adding a new address.*/
		public Action getAddAddressAction() {return addAddressAction;}

	/**The action for adding a new label.*/
	private final Action addLabelAction;

		/**@return The action for adding a new label.*/
		public Action getAddLabelAction() {return addLabelAction;}

	/**The action for removing an address or label.*/
	private final Action removeAddressAction;

		/**@return The action for removing an address or label.*/
		public Action getRemoveAddressAction() {return removeAddressAction;}

	/**The tabbed pane containing the address panels.*/
//TODO fix	private final JTabbedPane tabbedPane;

		/**@return The tabbed pane containing the address panels.*/
//TODO fix		protected JTabbedPane getTabbedPane() {return tabbedPane;}

	/**@return The tabbed pane containing the address panels.*/
	protected JTabbedPane getTabbedPane() {return (JTabbedPane)getContentComponent();}

	/**The stored array of labels we keep so that they won't be lost.*/ 
//TODO del	private LocaleText[] labels=new LocaleText[]{};	//TODO allow these to be edited in the panel
	
	/**Places the labels into the tabs.
	@param labels The labels to place in the tabs.
	*/
/*TODO del
	public void setLabels(LocaleText[] labels)
	{
		this.labels=labels;	//save the labels for later
	}
*/
	
	/**@return An array of entered labels.*/
/*TODO del
	public LocaleText[] getLabels()
	{
		return labels;	//return the labels we stored
	}
*/

	protected final JToolBar buttonToolBar;

	/**Places the addresses and labels into the tabs.
	If both arrays are empty, a default address will be placed in the first tab.
	@param addresses The addresses to place in the tabs.
	@param labels The labels to place in the tabs.
	*/
	public void setAddresses(final Collection<Address> addresses, final Collection<Label> labels)
	{
		getTabbedPane().removeAll();	//remove all tabs
		if(!addresses.isEmpty() || !labels.isEmpty())	//if there is at least one address or label
		{
			addAddresses(addresses);	//add the addresses
			addLabels(labels);	//add the labels
		}
		else	//if there are no addresses or labels
		{
			addAddresses(asList(new Address()));	//add a single default address
		}
		setDefaultFocusComponent(getTabbedPane().getComponentAt(0));	//set the default focus component to be the first tab (we'll always have at least one tab)
	}
	
	/**Places the addresses into the tabs.
	@param addresses The addresses to place in the tabs.
	*/
	protected void addAddresses(final Collection<Address> addresses)
	{
		for(final Address address:addresses)	//look at each of the addresses
		{
			addAddress(address);	//add this address to the tabbed pane
		}
	}
	
	/**@return An array of entered addresses.*/
	public Address[] getAddresses()
	{
		final List addressList=new ArrayList(getTabbedPane().getTabCount());	//create a list in which to store the addresses, making room for the maximum amout of addresses we could get 
		for(int i=0; i<getTabbedPane().getTabCount(); ++i)	//look at each tab
		{
			final Component component=getTabbedPane().getComponentAt(i);	//get this component
			if(component instanceof AddressPanel)	//if this is an address panel
			{
				final AddressPanel addressPanel=(AddressPanel)component;	//get this address panel
				final Address address=addressPanel.getAddress();	//get this address
				if(address!=null)	//if an address was entered in this panel
				{
					addressList.add(address);	//add this address to our list
				}
			}
		}
		return (Address[])addressList.toArray(new Address[addressList.size()]);	//return an array of the addresses we collected
	}

	/**Places the labels into the tabs.
	@param labels The labels to place in the tabs.
	*/
	protected void addLabels(final Collection<Label> labels)
	{
		for(final Label label:labels)	//look at each of the labels
		{
			addLabel(label);	//add this label to the tabbed pane
		}
	}
	
	/**@return An array of entered labels.*/
	public Label[] getLabels()
	{
		final List labelList=new ArrayList(getTabbedPane().getTabCount());	//create a list in which to store the labels, making room for the maximum amout of labels we could get 
		for(int i=0; i<getTabbedPane().getTabCount(); ++i)	//look at each tab
		{
			final Component component=getTabbedPane().getComponentAt(i);	//get this component
			if(component instanceof LabelPanel)	//if this is an label panel
			{
				final LabelPanel labelPanel=(LabelPanel)component;	//get this label panel
				final Label label=labelPanel.getLabel();	//get this label
				if(label!=null)	//if a label was entered in this panel
				{
					labelList.add(label);	//add this address to our list
				}
			}
		}
		return (Label[])labelList.toArray(new Label[labelList.size()]);	//return an array of the labels we collected
	}

	/**Default constructor.*/
	public AddressesPanel()
	{
		this(asList(new Address()), Collections.<Label>emptyList());	//construct an address panel with one default address
	}

	/**Addresses constructor.
	If both arrays are empty, a default address will be placed in the first tab.
	@param addresses The addresses to place in the tabs.
	@param labels The labels to place in the tabs.
	*/
	public AddressesPanel(final Collection<Address> addresses, final Collection<Label> labels)
	{
		super(new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//TODO fix		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//TODO fix		tabbedPane=new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		addAddressAction=new AddAddressAction();
		addLabelAction=new AddLabelAction();
		removeAddressAction=new RemoveAddressAction();
		buttonToolBar=new JToolBar(JToolBar.HORIZONTAL);
		initialize();	//initialize the panel
		setAddresses(addresses, labels);	//set the addresses to those given
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(Borders.createDefaultTitledBorder());	//set a titled border
		setTitle("Addresses");	//TODO i18n
		buttonToolBar.setFloatable(false);
		buttonToolBar.add(Box.createGlue());	//put glue before and after the button to center them 
		buttonToolBar.add(new JButton(getAddAddressAction()));
		buttonToolBar.add(new JButton(getAddLabelAction()));
		buttonToolBar.add(new JButton(getRemoveAddressAction()));
		buttonToolBar.add(Box.createGlue());	//put glue before and after the button to center them 
		add(buttonToolBar, BorderLayout.SOUTH);
/*TODO fix gridbag
		add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tabbedPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
*/
	}

	/**Updates the user interface.*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default status updating
//TODO del when works		getRemoveAddressAction().setEnabled(getTabbedPane().getTabCount()>1);	//don't allow all the tabs to be removed
		getRemoveAddressAction().setEnabled(getTabbedPane().getTabCount()>0);	//only allow removal if there are tabs to be removed
	}

	/**Adds an address to the tabbed pane.
	@param address The address to add.
	@return The address panel that represents the added address.
	*/
	public AddressPanel addAddress(final Address address)
	{
		final AddressPanel addressPanel=new AddressPanel(address);	//create a new address panel for this address
		addAddressPanel(addressPanel);	//add the address panel
		return addressPanel;	//return the panel we creatd for the address		
	}

	/**Adds an address panel to the tabbed pane.
	@param address The address to add.
	@return The address panel that represents the added address.
	*/
	protected void addAddressPanel(final AddressPanel addressPanel)
	{
		addressPanel.addPropertyChangeListener(getModifyModifiedPropertyChangeListener());	//listen for changes to the address and update the modified status in response TODO remove when we have a modifiable tabbed pane
		final String title="Address";	//get a title for the address TODO i18n
//TODO del when works		final String title=getTabTitle(address);	//get an title for the address
		getTabbedPane().addTab(title, addressPanel.getIcon(), addressPanel);	//add the panel
		setModified(true);	//show that we've been modified
		updateStatus();	//update the status
	}

	/**Adds a label to the tabbed pane.
	@param label The label to add.
	@return The label panel that represents the added label.
	*/
	public LabelPanel addLabel(final Label label)
	{
		final LabelPanel labelPanel=new LabelPanel(label);	//create a new label panel for this label
		addLabelPanel(labelPanel);	//add the label panel
		return labelPanel;	//return the panel we creatd for the label		
	}

	/**Adds a label panel to the tabbed pane.
	@param label The label to add.
	@return The label panel that represents the added label.
	*/
	protected void addLabelPanel(final LabelPanel labelPanel)
	{
		labelPanel.addPropertyChangeListener(getModifyModifiedPropertyChangeListener());	//listen for changes to the label and update the modified status in response TODO remove when we have a modifiable tabbed pane
		final String title="Label";	//get a title for the address TODO i18n
		getTabbedPane().addTab(title, labelPanel.getIcon(), labelPanel);	//add the panel
		setModified(true);	//show that we've been modified
		updateStatus();	//update the status
	}

	/**Removes the currently selected address or label.
	@return <code>true</code> if the address was moved, or <code>false</code> if
		the action was cancelled or if there is only one address remaining.
	*/
	public boolean removeAddress()
	{
//TODO del if not needed		if(getTabbedPane().getTabCount()>1)	//if we have more than one address
		{
			final Component selectedComponent=getTabbedPane().getSelectedComponent();	//get the component selected in the tabbed pane
			if(selectedComponent!=null)	//if a component is selected
			{
					//determine the name of the thing we're deleting
				final String objectName=selectedComponent instanceof LabelPanel ? "label" : "address";	//TODO i18n				
					//if they really want to delete the address or label
				if(JOptionPane.showConfirmDialog(this, MessageFormat.format("Are you sure you want to permanently remove this {0}?", new Object[]{objectName}),
						MessageFormat.format("Remove {0}", new Object[]{objectName}), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)	//TODO i18n
				{
					selectedComponent.removePropertyChangeListener(getModifyModifiedPropertyChangeListener());	//stop listening for changes TODO remove when we have a modifiable tabbed pane
					getTabbedPane().remove(selectedComponent);	//remove the selected component
					setModified(true);	//show that we've been modified
					updateStatus();	//update the status
					return true;	//show that we removed the address
				}
			}
		}
		return false;	//show that we didn't remove the address
	}

	/**Determines a title to use for the given address tab.
	@param address The address for which a title should be returned.
	@return An appropriate title for the address tab.
	*/
/*TODO del if not needed
	protected String getTabTitle(final Address address)	//TODO i18n
	{
		final String title;	//TODO fix with a better tab label algorithm
		if((address.getAddressType()&Address.PREFERRED_ADDRESS_TYPE)!=0)	
		{
			title="Preferred";
		}
		else if((address.getAddressType()&Address.WORK_ADDRESS_TYPE)!=0 && (address.getAddressType()&Address.HOME_ADDRESS_TYPE)==0)	
		{
			title="Work";
		}
		else if((address.getAddressType()&Address.HOME_ADDRESS_TYPE)!=0 && (address.getAddressType()&Address.WORK_ADDRESS_TYPE)==0)	
		{
			title="Home";
		}
		else if((address.getAddressType()&Address.WORK_ADDRESS_TYPE)!=0 && (address.getAddressType()&Address.HOME_ADDRESS_TYPE)!=0)	
		{
			title="Work/Home";
		}
		else	
		{
			title="Address";
		}
		return title;	//return the title
	}
*/

	/**Action for adding an address.*/
	class AddAddressAction extends AbstractAction
	{
		/**Default constructor.*/
		public AddAddressAction()
		{
			super("Add Address");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Add an address");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Add a new address.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.MAIL_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK)); //add the accelerator
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final AddressPanel addressPanel=new AddressPanel(new Address());	//create a default address panel
			if(addressPanel.editAddressType())	//ask the user for the address type; if they accept the changes
			{
				addAddressPanel(addressPanel);	//add the address panel
				getTabbedPane().setSelectedComponent(addressPanel);	//select the new address panel
				addressPanel.requestDefaultFocusComponentFocus();	//focus on the default panel TODO add this functionality into a listener to the tabbed pane, maybe, probably not, because we don't want to lose the old focus when changing tabs
			}
		}
	}

	/**Action for adding a label.*/
	class AddLabelAction extends AbstractAction
	{
		/**Default constructor.*/
		public AddLabelAction()
		{
			super("Add Label");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Add a label");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Add a new label.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.NOTE_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK)); //add the accelerator
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final LabelPanel labelPanel=new LabelPanel(new Label());	//create a default label panel
			if(labelPanel.editAddressType())	//ask the user for the address type; if they accept the changes
			{
				addLabelPanel(labelPanel);	//add the label panel
				getTabbedPane().setSelectedComponent(labelPanel);	//select the new label panel
				labelPanel.requestDefaultFocusComponentFocus();	//focus on the default panel TODO add this functionality into a listener to the tabbed pane, maybe, probably not, because we don't want to lose the old focus when changing tabs
			}
		}
	}

	/**Action for removing an address.*/
	class RemoveAddressAction extends AbstractAction
	{
		/**Default constructor.*/
		public RemoveAddressAction()
		{
			super("Remove");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Remove this address or label");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Remove the selected address or label.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));  //set the mnemonic key TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.DELETE_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_MASK)); //add the accelerator
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			removeAddress();	//remove the selected address
		}
	}

}
