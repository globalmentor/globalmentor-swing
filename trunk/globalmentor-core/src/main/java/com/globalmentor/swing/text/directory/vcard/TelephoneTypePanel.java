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
import java.util.EnumSet;
import java.util.Set;

import javax.swing.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.awt.Containers;
import com.globalmentor.swing.*;
import com.globalmentor.text.directory.vcard.*;

/**
 * A panel allowing specification of the types of telephone of the <code>TEL</code> type of a vCard <code>text/directory</code> profile as defined in <a
 * href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>, "vCard MIME Directory Profile".
 * @author Garret Wilson
 */
public class TelephoneTypePanel extends BasicPanel
{

	/** The checkbox for a telephone number associated with a residence. */
	private final JCheckBox homeCheckBox;

	/** @return The checkbox for a telephone number associated with a residence. */
	public JCheckBox getHomeCheckBox()
	{
		return homeCheckBox;
	}

	/** The checkbox for a telephone number that has voice messaging support. */
	private final JCheckBox messageCheckBox;

	/** @return The checkbox for a telephone number that has voice messaging support. */
	public JCheckBox getMessageCheckBox()
	{
		return messageCheckBox;
	}

	/** The checkbox for a telephone number associated with a place of work. */
	private final JCheckBox workCheckBox;

	/** @return The checkbox for a telephone number associated with a place of work. */
	public JCheckBox getWorkCheckBox()
	{
		return workCheckBox;
	}

	/** The checkbox for a preferred-use telephone number. */
	private final JCheckBox preferredCheckBox;

	/** @return The checkbox for a preferred-use telephone number. */
	public JCheckBox getPreferredCheckBox()
	{
		return preferredCheckBox;
	}

	/** The checkbox for a voice telephone number. */
	private final JCheckBox voiceCheckBox;

	/** @return The checkbox for a voice telephone number. */
	public JCheckBox getVoiceCheckBox()
	{
		return voiceCheckBox;
	}

	/** The checkbox for a facsimile telephone number. */
	private final JCheckBox faxCheckBox;

	/** @return The checkbox for a facsimile telephone number. */
	public JCheckBox getFaxCheckBox()
	{
		return faxCheckBox;
	}

	/** The checkbox for a cellular telephone number. */
	private final JCheckBox cellCheckBox;

	/** @return The checkbox for a cellular telephone number. */
	public JCheckBox getCellCheckBox()
	{
		return cellCheckBox;
	}

	/** The checkbox for a video conferencing telephone number. */
	private final JCheckBox videoCheckBox;

	/** @return The checkbox for a video conferencing telephone number. */
	public JCheckBox getVideoCheckBox()
	{
		return videoCheckBox;
	}

	/** The checkbox for a paging device telephone number. */
	private final JCheckBox pagerCheckBox;

	/** @return The checkbox for a paging device telephone number. */
	public JCheckBox getPagerCheckBox()
	{
		return pagerCheckBox;
	}

	/** The checkbox for a bulletin board system telephone number. */
	private final JCheckBox bbsCheckBox;

	/** @return The checkbox for a bulletin board system telephone number. */
	public JCheckBox getBBSCheckBox()
	{
		return bbsCheckBox;
	}

	/** The checkbox for a modem-connected telephone number. */
	private final JCheckBox modemCheckBox;

	/** @return The checkbox for a modem-connected telephone number. */
	public JCheckBox getModemCheckBox()
	{
		return modemCheckBox;
	}

	/** The checkbox for a car-phone telephone number. */
	private final JCheckBox carCheckBox;

	/** @return The checkbox for a car-phone telephone number. */
	public JCheckBox getCarCheckBox()
	{
		return carCheckBox;
	}

	/** The checkbox for an ISDN service telephone number. */
	private final JCheckBox isdnCheckBox;

	/** @return The checkbox for an ISDN service telephone number. */
	public final JCheckBox getISDNCheckBox()
	{
		return isdnCheckBox;
	}

	/** The checkbox for a personal communication services telephone number. */
	private final JCheckBox pcsCheckBox;

	/** @return The checkbox for a personal communication services telephone number. */
	public JCheckBox getPCSCheckBox()
	{
		return pcsCheckBox;
	}

	/**
	 * Places the delivery telephone type into the various fields.
	 * @param telephoneTypes The intended use.
	 * @see Telephone#Type
	 */
	public void setTelephoneTypes(final Set<Telephone.Type> telephoneTypes)
	{
		homeCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.HOME));
		messageCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.MSG));
		workCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.WORK));
		preferredCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.PREF));
		voiceCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.VOICE));
		faxCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.FAX));
		cellCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.CELL));
		videoCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.VIDEO));
		pagerCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.PAGER));
		bbsCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.BBS));
		modemCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.MODEM));
		carCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.CAR));
		isdnCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.ISDN));
		pcsCheckBox.setSelected(telephoneTypes.contains(Telephone.Type.PCS));
	}

	/**
	 * @return The telephone intended use
	 * @see Telephone#Type
	 */
	public Set<Telephone.Type> getTelephoneTypes()
	{
		final Set<Telephone.Type> telephoneTypes = EnumSet.noneOf(Telephone.Type.class); //start out without knowing the telephone type
		if(homeCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.HOME);
		if(messageCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.MSG);
		if(workCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.WORK);
		if(preferredCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.PREF);
		if(voiceCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.VOICE);
		if(faxCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.FAX);
		if(cellCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.CELL);
		if(videoCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.VIDEO);
		if(pagerCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.PAGER);
		if(bbsCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.BBS);
		if(modemCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.MODEM);
		if(carCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.CAR);
		if(isdnCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.ISDN);
		if(pcsCheckBox.isSelected())
			telephoneTypes.add(Telephone.Type.PCS);
		return telephoneTypes; //return the telephone types
	}

	/**
	 * Default constructor.
	 * @see Telephone#DEFAULT_TYPE
	 */
	public TelephoneTypePanel()
	{
		this(EnumSet.of(Telephone.DEFAULT_TYPE)); //construct the panel with the default telephone type
	}

	/**
	 * Telephone type constructor.
	 * @param telephoneTypes The intended use.
	 */
	public TelephoneTypePanel(final Set<Telephone.Type> telephoneTypes)
	{
		super(new BasicGridBagLayout(), false); //construct the panel using a grid bag layout, but don't initialize the panel
		homeCheckBox = new JCheckBox();
		messageCheckBox = new JCheckBox();
		workCheckBox = new JCheckBox();
		preferredCheckBox = new JCheckBox();
		voiceCheckBox = new JCheckBox();
		faxCheckBox = new JCheckBox();
		cellCheckBox = new JCheckBox();
		videoCheckBox = new JCheckBox();
		pagerCheckBox = new JCheckBox();
		bbsCheckBox = new JCheckBox();
		modemCheckBox = new JCheckBox();
		carCheckBox = new JCheckBox();
		isdnCheckBox = new JCheckBox();
		pcsCheckBox = new JCheckBox();
		setDefaultFocusComponent(preferredCheckBox); //set the default focus component
		initialize(); //initialize the panel
		setTelephoneTypes(telephoneTypes); //set the given telephone types
	}

	/** Initializes the user interface. */
	public void initializeUI()
	{
		super.initializeUI(); //do the default user interface initialization
		homeCheckBox.setText("Home"); //TODO i18n
		messageCheckBox.setText("Message"); //TODO i18n
		workCheckBox.setText("Work"); //TODO i18n
		preferredCheckBox.setText("Preferred"); //TODO i18n
		preferredCheckBox.setFont(preferredCheckBox.getFont().deriveFont(Font.BOLD));
		voiceCheckBox.setText("Voice"); //TODO i18n
		faxCheckBox.setText("Fax"); //TODO i18n
		cellCheckBox.setText("Mobile"); //TODO i18n
		videoCheckBox.setText("Video"); //TODO i18n
		pagerCheckBox.setText("Pager"); //TODO i18n
		bbsCheckBox.setText("BBS"); //TODO i18n
		modemCheckBox.setText("Modem"); //TODO i18n
		carCheckBox.setText("Car"); //TODO i18n
		isdnCheckBox.setText("ISDN"); //TODO i18n
		pcsCheckBox.setText("PCS"); //TODO i18n
		add(preferredCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(workCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(homeCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(cellCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(voiceCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(faxCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(pagerCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(modemCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(messageCheckBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(carCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(videoCheckBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(isdnCheckBox, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(bbsCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(pcsCheckBox, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
	}

}
