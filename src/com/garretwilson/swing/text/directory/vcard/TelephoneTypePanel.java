package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;

/**A panel allowing specification of the types of telephone of the <code>TEL</code>
	type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class TelephoneTypePanel extends BasicPanel
{

	/**The checkbox for a telephone number associated with a residence.*/
	private final JCheckBox homeCheckBox;

		/**@return The checkbox for a telephone number associated with a residence.*/
		public JCheckBox getHomeCheckBox() {return homeCheckBox;}

	/**The checkbox for a telephone number that has voice messaging support.*/
	private final JCheckBox messageCheckBox;

		/**@return The checkbox for a telephone number that has voice messaging support.*/
		public JCheckBox getMessageCheckBox() {return messageCheckBox;}

	/**The checkbox for a telephone number associated with a place of work.*/
	private final JCheckBox workCheckBox;

		/**@return The checkbox for a telephone number associated with a place of work.*/
		public JCheckBox getWorkCheckBox() {return workCheckBox;}

	/**The checkbox for a preferred-use telephone number.*/
	private final JCheckBox preferredCheckBox;

		/**@return The checkbox for a preferred-use telephone number.*/
		public JCheckBox getPreferredCheckBox() {return preferredCheckBox;}

	/**The checkbox for a voice telephone number.*/
	private final JCheckBox voiceCheckBox;

		/**@return The checkbox for a voice telephone number.*/
		public JCheckBox getVoiceCheckBox() {return voiceCheckBox;}

	/**The checkbox for a facsimile telephone number.*/
	private final JCheckBox faxCheckBox;

		/**@return The checkbox for a facsimile telephone number.*/
		public JCheckBox getFaxCheckBox() {return faxCheckBox;}

	/**The checkbox for a cellular telephone number.*/
	private final JCheckBox cellCheckBox;

		/**@return The checkbox for a cellular telephone number.*/
		public JCheckBox getCellCheckBox() {return cellCheckBox;}

	/**The checkbox for a video conferencing telephone number.*/
	private final JCheckBox videoCheckBox;

		/**@return The checkbox for a video conferencing telephone number.*/
		public JCheckBox getVideoCheckBox() {return videoCheckBox;}

	/**The checkbox for a paging device telephone number.*/
	private final JCheckBox pagerCheckBox;

		/**@return The checkbox for a paging device telephone number.*/
		public JCheckBox getPagerCheckBox() {return pagerCheckBox;}

	/**The checkbox for a bulletin board system telephone number.*/
	private final JCheckBox bbsCheckBox;

		/**@return The checkbox for a bulletin board system telephone number.*/
		public JCheckBox getBBSCheckBox() {return bbsCheckBox;}

	/**The checkbox for a modem-connected telephone number.*/
	private final JCheckBox modemCheckBox;

		/**@return The checkbox for a modem-connected telephone number.*/
		public JCheckBox getModemCheckBox() {return modemCheckBox;}

	/**The checkbox for a car-phone telephone number.*/
	private final JCheckBox carCheckBox;

		/**@return The checkbox for a car-phone telephone number.*/
		public JCheckBox getCarCheckBox() {return carCheckBox;}

	/**The checkbox for an ISDN service telephone number.*/
	private final JCheckBox isdnCheckBox;

		/**@return The checkbox for an ISDN service telephone number.*/
		public final JCheckBox getISDNCheckBox() {return isdnCheckBox;}

	/**The checkbox for a personal communication services telephone number.*/
	private final JCheckBox pcsCheckBox;

		/**@return The checkbox for a personal communication services telephone number.*/
		public JCheckBox getPCSCheckBox() {return pcsCheckBox;}

	/**Places the delivery telephone type into the various fields.
	@param telephoneType The intended use, one or more of the
		<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	@see Telephone
	*/
	public void setTelephoneType(final int telephoneType)
	{
		homeCheckBox.setSelected((telephoneType & Telephone.HOME_TELEPHONE_TYPE)!=0);
		messageCheckBox.setSelected((telephoneType & Telephone.MESSAGE_TELEPHONE_TYPE)!=0);
		workCheckBox.setSelected((telephoneType & Telephone.WORK_TELEPHONE_TYPE)!=0);
		preferredCheckBox.setSelected((telephoneType & Telephone.PREFERRED_TELEPHONE_TYPE)!=0);
		voiceCheckBox.setSelected((telephoneType & Telephone.VOICE_TELEPHONE_TYPE)!=0);
		faxCheckBox.setSelected((telephoneType & Telephone.FAX_TELEPHONE_TYPE)!=0);
		cellCheckBox.setSelected((telephoneType & Telephone.CELL_TELEPHONE_TYPE)!=0);
		videoCheckBox.setSelected((telephoneType & Telephone.VIDEO_TELEPHONE_TYPE)!=0);
		pagerCheckBox.setSelected((telephoneType & Telephone.PAGER_TELEPHONE_TYPE)!=0);
		bbsCheckBox.setSelected((telephoneType & Telephone.BBS_TELEPHONE_TYPE)!=0);
		modemCheckBox.setSelected((telephoneType & Telephone.MODEM_TELEPHONE_TYPE)!=0);
		carCheckBox.setSelected((telephoneType & Telephone.CAR_TELEPHONE_TYPE)!=0);
		isdnCheckBox.setSelected((telephoneType & Telephone.ISDN_TELEPHONE_TYPE)!=0);
		pcsCheckBox.setSelected((telephoneType & Telephone.PCS_TELEPHONE_TYPE)!=0);
	}
	
	/**@return The telephone intended use, a combination of
		<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	@see Telephone
	*/
	public int getTelephoneType()
	{
		int telephoneType=Telephone.NO_TELEPHONE_TYPE;	//start out without knowing the telephone type
		if(homeCheckBox.isSelected())
			telephoneType|=Telephone.HOME_TELEPHONE_TYPE;
		if(messageCheckBox.isSelected())
			telephoneType|=Telephone.MESSAGE_TELEPHONE_TYPE;
		if(workCheckBox.isSelected())
			telephoneType|=Telephone.WORK_TELEPHONE_TYPE;
		if(preferredCheckBox.isSelected())
			telephoneType|=Telephone.PREFERRED_TELEPHONE_TYPE;
		if(voiceCheckBox.isSelected())
			telephoneType|=Telephone.VOICE_TELEPHONE_TYPE;
		if(faxCheckBox.isSelected())
			telephoneType|=Telephone.FAX_TELEPHONE_TYPE;
		if(cellCheckBox.isSelected())
			telephoneType|=Telephone.CELL_TELEPHONE_TYPE;
		if(videoCheckBox.isSelected())
			telephoneType|=Telephone.VIDEO_TELEPHONE_TYPE;
		if(pagerCheckBox.isSelected())
			telephoneType|=Telephone.PAGER_TELEPHONE_TYPE;
		if(bbsCheckBox.isSelected())
			telephoneType|=Telephone.BBS_TELEPHONE_TYPE;
		if(modemCheckBox.isSelected())
			telephoneType|=Telephone.MODEM_TELEPHONE_TYPE;
		if(carCheckBox.isSelected())
			telephoneType|=Telephone.CAR_TELEPHONE_TYPE;
		if(isdnCheckBox.isSelected())
			telephoneType|=Telephone.ISDN_TELEPHONE_TYPE;
		if(pcsCheckBox.isSelected())
			telephoneType|=Telephone.PCS_TELEPHONE_TYPE;
		return telephoneType;	//return the address type
	}

	/**Default constructor.
	@see Telephone#DEFAULT_TELEPHONE_TYPE
	*/
	public TelephoneTypePanel()
	{
		this(Telephone.DEFAULT_TELEPHONE_TYPE);	//construct the panel with the default telephone type
	}

	/**Telepone type constructor.
	@param telephoneType The intended use, one or more of the
		<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	*/
	public TelephoneTypePanel(final int telephoneType)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		homeCheckBox=new JCheckBox();
		messageCheckBox=new JCheckBox();
		workCheckBox=new JCheckBox();
		preferredCheckBox=new JCheckBox();
		voiceCheckBox=new JCheckBox();
		faxCheckBox=new JCheckBox();
		cellCheckBox=new JCheckBox();
		videoCheckBox=new JCheckBox();
		pagerCheckBox=new JCheckBox();
		bbsCheckBox=new JCheckBox();
		modemCheckBox=new JCheckBox();
		carCheckBox=new JCheckBox();
		isdnCheckBox=new JCheckBox();
		pcsCheckBox=new JCheckBox();
		setDefaultFocusComponent(preferredCheckBox);	//set the default focus component
		initialize();	//initialize the panel
		setTelephoneType(telephoneType);	//set the given telephone type
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		homeCheckBox.setText("Home");	//G***i18n
		messageCheckBox.setText("Message");	//G***i18n
		workCheckBox.setText("Work");	//G***i18n
		preferredCheckBox.setText("Preferred");	//G***i18n
		preferredCheckBox.setFont(preferredCheckBox.getFont().deriveFont(Font.BOLD));
		voiceCheckBox.setText("Voice");	//G***i18n
		faxCheckBox.setText("Fax");	//G***i18n
		cellCheckBox.setText("Mobile");	//G***i18n
		videoCheckBox.setText("Video");	//G***i18n
		pagerCheckBox.setText("Pager");	//G***i18n
		bbsCheckBox.setText("BBS");	//G***i18n
		modemCheckBox.setText("Modem");	//G***i18n
		carCheckBox.setText("Car");	//G***i18n
		isdnCheckBox.setText("ISDN");	//G***i18n
		pcsCheckBox.setText("PCS");	//G***i18n
		add(preferredCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(workCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(homeCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(cellCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(voiceCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(faxCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(pagerCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(modemCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(messageCheckBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(carCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(videoCheckBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(isdnCheckBox, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(bbsCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(pcsCheckBox, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
	}

}
