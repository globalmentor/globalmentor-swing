package com.garretwilson.swing.text.directory.vcard;

import javax.swing.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;

/**A panel allowing entry of one or more addresses a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressesPanel extends ContentPanel
{

	/**@return The tabbed pane containing the address panels.*/
	protected JTabbedPane getTabbedPane() {return (JTabbedPane)getContentComponent();}

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
			final Address address=addresses[i];	//get this address
			final AddressPanel addressPanel=new AddressPanel(address);	//create a new address panel for this address
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
				//TODO add an icon to each tab
			getTabbedPane().addTab(title, addressPanel);	//add the panel
			setDefaultFocusComponent(getTabbedPane().getComponentAt(0));	//set the default focus component to be the first tab (we'll always have at least one tab)
		}
	}
	
	/**@return An array of entered addresses.*/
	public Address[] getAddresses()
	{
		final Address[] addresses=new Address[getTabbedPane().getTabCount()];	//create an array of addresses, based upon the number of tabs
		for(int i=0; i<addresses.length; ++i)	//look at each tab
		{
			final AddressPanel addressPanel=(AddressPanel)getTabbedPane().getComponentAt(i);	//get this address panel
			addresses[i]=addressPanel.getAddress();	//get this address
		}
		return addresses;	//return the addresses we collected
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
		initialize();	//initialize the panel
		setAddresses(addresses);	//set the addresses to those given
	}

}
