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

package com.garretwilson.swing;

import java.awt.GridBagConstraints;
import java.util.*;
import static java.util.Collections.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.awt.Containers;

/**A panel allowing selection of a language.
@author Garret Wilson
*/
public class LanguagePanel extends BasicPanel
{
	/**The label of the language combo box.*/
	private final JLabel languageLabel;

	/**The language combo box.*/
	private final JComboBox languageComboBox;

		/**@return The language combo box.*/
		public JComboBox getLanguageComboBox() {return languageComboBox;}

	/**Sets the language displayed.
	@param locale The locale indicating the language, or <code>null</code> for no
		language.
	*/
	public void setLanguage(final Locale locale)
	{
		languageComboBox.setSelectedItem(locale!=null ? (Object)locale : (Object)NO_LANGUAGE);	//select the given language
	}

	/**@return A locale indicating the language selected, or <code>null</code> if
		no language is selected.
	*/
	public Locale getLanguage()
	{
		final Object selectedItem=languageComboBox.getSelectedItem();	//get the selected item
		return selectedItem instanceof Locale ? (Locale)selectedItem : null;	//TODO testing; maybe allow manually entered languages
	}

	/**Default constructor.*/
	public LanguagePanel()
	{
		this(null);	//construct a panel with no language	
	}

	/**Lanaguage constructor.
	@param locale The locale indicating the language, or <code>null</code> for no
		language.
	*/
	public LanguagePanel(final Locale locale)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		languageLabel=new JLabel();
		languageComboBox=new JComboBox();
		setDefaultFocusComponent(languageComboBox);	//set the default focus component
		initialize();	//initialize the panel
		setLanguage(locale);	//set the given language
	}

	private final String NO_LANGUAGE="No Language Specified";	//TODO i18n

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		languageLabel.setText("Language");	//TODO i18n
//TODO testing		languageComboBox.setEditable(true);
		final List languageList=new ArrayList();	//TODO testing
		addAll(languageList, Locale.getAvailableLocales());
		sort(languageList, new Comparator()
				{
					public int compare(Object o1, Object o2) {return ((Locale)o1).toString().compareTo(((Locale)o2).toString());}
				}
		);	//TODO add a comparator here to sort the language display strings
//TODO fix		languageList.add(0, null);	//TODO add some non-null object that can be compared to allow proper UI functionality
		languageList.add(0, NO_LANGUAGE);	//TODO add some non-null object that can be compared to allow proper UI functionality
//TODO del		final Locale[] languages=(Locale[])languageList.toArray(new Locale[languageList.size()]);
		final Object[] languages=languageList.toArray();	//TODO fix
		languageComboBox.setModel(new DefaultComboBoxModel(languages));	//set up the available languages TODO testing; create renderer
//TODO del		languageComboBox.setModel(new DefaultComboBoxModel(Locale.getAvailableLocales()));	//set up the available languages TODO testing; create renderer
		languageComboBox.setRenderer(new SimpleListCellRenderer()
				{
					protected String getListCellRendererString(final Object value)	//return the display language of each locales
					{
						if(value instanceof Locale)	//TODO fix all this better, and use a non-null object for the language
						{
							final Locale locale=(Locale)value;
							final StringBuffer stringBuffer=new StringBuffer(locale.getDisplayLanguage());
							final String displayCountry=locale.getDisplayCountry();
							if(displayCountry!=null && displayCountry.length()>0)
								stringBuffer.append(' ').append('(').append(displayCountry).append(')');
							final String displayVariant=locale.getDisplayVariant();	
							if(displayVariant!=null && displayVariant.length()>0)
								stringBuffer.append(' ').append('(').append(displayVariant).append(')');
							return stringBuffer.toString();
						}
						else
						{
							return NO_LANGUAGE;	//TODO fix
//TODO fix							return value.toString();	//TODO fix
//TODO fix							return String.valueOf(CharacterConstants.EM_DASH_CHAR);	//TODO use a better character here
						}
					}					
				});
		add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(languageComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
	}

}
