package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.util.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.swing.border.*;

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

	/**The action for removing an address.*/
	private final Action removeAddressAction;

		/**@return The action for removing an address.*/
		public Action getRemoveAddressAction() {return removeAddressAction;}

	/**A property change listener to change the modified status when the
		"modified" property is set to <code>true</code>.
	*/
	private final PropertyChangeListener modifyModifiedPropertyChangeListener;

	/**The tabbed pane containing the address panels.*/
//G***fix	private final JTabbedPane tabbedPane;

		/**@return The tabbed pane containing the address panels.*/
//G***fix		protected JTabbedPane getTabbedPane() {return tabbedPane;}

	/**@return The tabbed pane containing the address panels.*/
	protected JTabbedPane getTabbedPane() {return (JTabbedPane)getContentComponent();}

	/**The stored array of labels we keep so that they won't be lost.*/ 
	private LocaleText[] labels=new LocaleText[]{};	//TODO allow these to be edited in the panel
	
	/**Places the labels into the tabs.
	@param labels The labels to place in the tabs.
	*/
	public void setLabels(LocaleText[] labels)
	{
		this.labels=labels;	//save the labels for later
	}
	
	/**@return An array of entered labels.*/
	public LocaleText[] getLabels()
	{
		return labels;	//return the labels we stored
	}
	
	/**Places the addresses into the tabs.
	@param addresses The addresses to place in the tabs. If the array is empty,
		a default address will be placed in the first tab.
	*/
	public void setAddresses(Address[] addresses)
	{
		if(addresses.length<1)	//if there isn't at least one address
			addresses=new Address[]{new Address()};	//create a new array containing a single default address
		getTabbedPane().removeAll();	//remove all tabs
		for(int i=0; i<addresses.length; ++i)	//look at each of the addresses
		{
			addAddress(addresses[i]);	//add this address to the tabbed pane
		}
		setDefaultFocusComponent(getTabbedPane().getComponentAt(0));	//set the default focus component to be the first tab (we'll always have at least one tab)
	}
	
	/**@return An array of entered addresses.*/
	public Address[] getAddresses()
	{
		final List addressList=new ArrayList(getTabbedPane().getTabCount());	//create a list in which to store the addresses, making room for the maximum amout of addresses we could get 
//G***del		final Address[] addresses=new Address[getTabbedPane().getTabCount()];	//create an array of addresses, based upon the number of tabs
		for(int i=0; i<getTabbedPane().getTabCount(); ++i)	//look at each tab
		{
			final AddressPanel addressPanel=(AddressPanel)getTabbedPane().getComponentAt(i);	//get this address panel
			final Address address=addressPanel.getAddress();	//get this address
			if(address!=null)	//if an address was entered in this panel
			{
				addressList.add(address);	//add this address to our list
			}
//G***del			addresses[i]=addressPanel.getAddress();	//get this address
		}
//G***del		return addresses;	//return the addresses we collected
		return (Address[])addressList.toArray(new Address[addressList.size()]);	//return an array of the addresses we collected
	}

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
			for(int i=getTabbedPane().getTabCount()-1; i>=0; --i)	//look at each tab
			{
				final Component component=getTabbedPane().getComponentAt(i);	//get this address panel
				if(component instanceof Modifiable)	//if this component is modifiable
				{
					((Modifiable)component).setModified(newModified);	//tell the component it is no longer modified
				}
			}
		}
	}

	/**Default constructor.*/
	public AddressesPanel()
	{
		this(new Address[]{new Address()});	//construct an address panel with one default address
	}

	/**Addresses constructor.
	@param addresses The addresses to place in the tabs. If the array is empty,
		a default address will be placed in the first tab.
	*/
	public AddressesPanel(final Address[] addresses)
	{
		super(new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//G***fix		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//G***fix		tabbedPane=new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		addAddressAction=new AddAddressAction();
		removeAddressAction=new RemoveAddressAction();
		modifyModifiedPropertyChangeListener=createModifyModifiedChangeListener();	//create a property change listener to change the modified status when the modified property is set to true
		initialize();	//initialize the panel
		setAddresses(addresses);	//set the addresses to those given
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderUtilities.createDefaultTitledBorder());	//set a titled border
		setTitle("Addresses");	//G***i18n
		final JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(getAddAddressAction()));
		buttonPanel.add(new JButton(getRemoveAddressAction()));
		add(buttonPanel, BorderLayout.SOUTH);
/*G***fix gridbag
		add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tabbedPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
*/
	}

	/**Updates the user interface.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
		getRemoveAddressAction().setEnabled(getTabbedPane().getTabCount()>1);	//don't allow all the tabs to be removed
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
		addressPanel.addPropertyChangeListener(modifyModifiedPropertyChangeListener);	//listen for changes to the address and update the modified status in response
		final String title="Address";	//get a title for the address G***i18n
//G***del when works		final String title=getTabTitle(address);	//get an title for the address
			//TODO add an icon to each tab
		getTabbedPane().addTab(title, addressPanel);	//add the panel
		setModified(true);	//show that we've been modified
	}

	/**Removes the currently selected address.
	@return <code>true</code> if the address was moved, or <code>false</code> if
		the action was cancelled or if there is only one address remaining.
	*/
	public boolean removeAddress()
	{
		if(getTabbedPane().getTabCount()>1)	//if we have more than one address
		{
			final Component selectedComponent=getTabbedPane().getSelectedComponent();	//get the component selected in the tabbed pane
			if(selectedComponent!=null)	//if a component is selected
			{
					//if they really want to delete the address
				if(JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently remove this address?", "Remove Address", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)	//G***i18n
				{
					selectedComponent.removePropertyChangeListener(modifyModifiedPropertyChangeListener);	//stop listening for changes
					getTabbedPane().remove(selectedComponent);	//remove the selected component
					setModified(true);	//show that we've been modified
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
/*G***del if not needed
	protected String getTabTitle(final Address address)	//G***i18n
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
			super("Add Address");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Add an address");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Add a new address.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('a'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.ADD_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_MASK)); //add the accelerator
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final AddressPanel addressPanel=new AddressPanel(new Address());	//create a default address panel
			if(addressPanel.editTelephoneType())	//ask the user for the address type; if they accept the changes
			{
				addAddressPanel(addressPanel);	//add the address panel
				getTabbedPane().setSelectedComponent(addressPanel);	//select the new address panel
				addressPanel.requestDefaultFocusComponentFocus();	//focus on the default panel TODO add this functionality into a listener to the tabbed pane, maybe, probably not, because we don't want to lose the old focus when changing tabs
//G***del if not needed				updateStatus();	//update the status
			}
		}
	}

	/**Action for removing an address.*/
	class RemoveAddressAction extends AbstractAction
	{
		/**Default constructor.*/
		public RemoveAddressAction()
		{
			super("Remove Address");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Remove this address");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Remove the selected address.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('e'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SUBTRACT_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_MASK)); //add the accelerator
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			removeAddress();	//remove the selected address
			updateStatus();	//update the status
		}
	}

}
