package com.garretwilson.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;
import javax.swing.*;
import com.garretwilson.text.*;
import com.garretwilson.util.*;

/**A panel allowing selection of a language.
@author Garret Wilson
*/
public class LanguagePanel extends DefaultPanel
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
		languageComboBox.setSelectedItem(locale);	//select the given language
	}

	/**@return A locale indicating the language selected, or <code>null</code> if
		no language is selected.
	*/
	public Locale getLanguage()
	{
		final Object selectedItem=languageComboBox.getSelectedItem();	//get the selected item
		return selectedItem instanceof Locale ? (Locale)selectedItem : null;	//G***testing; maybe allow manually entered languages
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
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		languageLabel=new JLabel();
		languageComboBox=new JComboBox();
		setDefaultFocusComponent(languageComboBox);	//set the default focus component
		initialize();	//initialize the panel
		setLanguage(locale);	//set the given language
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		languageLabel.setText("Language");	//G***i18n
//G***testing		languageComboBox.setEditable(true);
		final List languageList=new ArrayList();	//G***testing
		CollectionUtilities.addAll(languageList, Locale.getAvailableLocales());
		Collections.sort(languageList, new Comparator()
				{
					public int compare(Object o1, Object o2) {return ((Locale)o1).toString().compareTo(((Locale)o2).toString());}
				}
		);	//TODO add a comparator here to sort the language display strings
		languageList.add(0, null);
		final Locale[] languages=(Locale[])languageList.toArray(new Locale[languageList.size()]);
		languageComboBox.setModel(new DefaultComboBoxModel(languages));	//set up the available languages G***testing; create renderer
//G***del		languageComboBox.setModel(new DefaultComboBoxModel(Locale.getAvailableLocales()));	//set up the available languages G***testing; create renderer
		languageComboBox.setRenderer(new SimpleListCellRenderer()
				{
					protected String getListCellRendererString(final Object value)	//return the display language of each localez
					{
						return value instanceof Locale
										//TODO add a LocalUtilities.getDisplayLocale() that includes the variant
							? ((Locale)value).getDisplayLanguage()+" ("+((Locale)value).getDisplayCountry()+")"	//G***i18n fix for a specified locale, if needed
							: ""+CharacterConstants.EM_DASH_CHAR;	//G***use a better character here
					}					
				});
//G***del		honorificSuffixComboBox.setPrototypeDisplayValue("Sr.");
		add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(languageComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
	}

}
