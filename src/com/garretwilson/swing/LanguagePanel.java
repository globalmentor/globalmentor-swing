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
public class LanguagePanel extends GridBagPanel
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
		super(false);	//construct the panel using a grid bag layout, but don't initialize the panel
		languageLabel=new JLabel();
		languageComboBox=new JComboBox();
		setDefaultFocusComponent(languageComboBox);	//set the default focus component
		initialize();	//initialize the panel
		setLanguage(locale);	//set the given language
	}

	private final String NO_LANGUAGE="No Language Specified";	//G***i18n

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
//G***fix		languageList.add(0, null);	//TODO add some non-null object that can be compared to allow proper UI functionality
		languageList.add(0, NO_LANGUAGE);	//TODO add some non-null object that can be compared to allow proper UI functionality
//G***del		final Locale[] languages=(Locale[])languageList.toArray(new Locale[languageList.size()]);
		final Object[] languages=languageList.toArray();	//G***fix
		languageComboBox.setModel(new DefaultComboBoxModel(languages));	//set up the available languages G***testing; create renderer
//G***del		languageComboBox.setModel(new DefaultComboBoxModel(Locale.getAvailableLocales()));	//set up the available languages G***testing; create renderer
		languageComboBox.setRenderer(new SimpleListCellRenderer()
				{
					protected String getListCellRendererString(final Object value)	//return the display language of each localez
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
							return NO_LANGUAGE;	//G***fix
//G***fix							return value.toString();	//G***fix
//G***fix							return String.valueOf(CharacterConstants.EM_DASH_CHAR);	//G***use a better character here
						}
					}					
				});
//G***del		honorificSuffixComboBox.setPrototypeDisplayValue("Sr.");
		add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(languageComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
	}

}
