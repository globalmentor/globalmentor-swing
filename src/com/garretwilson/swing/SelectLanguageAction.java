package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

/**Action for selecting a language from a dialog.
@author Garret Wilson
@see LanguagePanel
@see Locale
@see Modifiable
*/
public class SelectLanguageAction extends AbstractAction
{

	/**The locale that represents the language, or <code>null</code>
		if no language is indicated.
	*/
	private Locale locale;

		/**@return The locale that represents the language, or
			<code>null</code> if no language is indicated.
		*/
		public Locale getLanguage() {return locale;}

		/**Sets the language.
		@param locale The locale that represents the language, or
			<code>null</code> if no language should be indicated.
		*/
		public void setLanguage(final Locale locale) {this.locale=locale;}

	/**Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	*/
	private Component parentComponent;

	/**The object that should be notified when the locale is modified, or
		<code>null</code> if no object should be notified.
	*/
	private final Modifiable modifiable;

	/**Default constructor with no locale.*/
	public SelectLanguageAction()
	{
		this(null);	//construct an action with no locale
	}

	/**Locale constructor.
	@param locale The locale that represents the language, or
		<code>null</code> if no language should be initially indicated.
	*/
	public SelectLanguageAction(final Locale locale)
	{
		this(locale, null);	//create an action with no parent component
	}

	/**Locale and parent component constructor.
	@param locale The locale that represents the language, or
		<code>null</code> if no language should be initially indicated.
	@param parentComponent Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	*/
	public SelectLanguageAction(final Locale locale, final Component parentComponent)
	{
		this(locale, parentComponent, null);	//create an action with no object to be notified
	}
	
	/**Full constructor.
	@param locale The locale that represents the language, or
		<code>null</code> if no language should be initially indicated.
	@param parentComponent Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	@param modifiable The object that should be notified when the locale is
		modified, or <code>null</code> if no object should be notified.
	*/
	public SelectLanguageAction(final Locale locale, final Component parentComponent, final Modifiable modifiable)
	{
		super("Language");	//create the base class G***i18n
		setLanguage(locale);	//set the language
		this.parentComponent=parentComponent;	//set the parent component
		this.modifiable=modifiable;	//set the modifiable object G***maybe find a better method of notification
		putValue(SHORT_DESCRIPTION, "Select language");	//set the short description G***i18n
		putValue(LONG_DESCRIPTION, "Select the language.");	//set the long description G***i18n
		putValue(MNEMONIC_KEY, new Integer('l'));  //set the mnemonic key G***i18n
		putValue(SMALL_ICON, IconResources.getIcon(IconResources.GLOBE_ICON_FILENAME)); //load the correct icon G***use a better icon
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK)); //add the accelerator
	}
	
	/**Called when the action should be performed.
	@param actionEvent The event causing the action.
	*/
	public void actionPerformed(final ActionEvent actionEvent)
	{
		final LanguagePanel languagePanel=new LanguagePanel(locale);	//create a new language panel
			//ask for a new language; if the user accepts the changes
		if(OptionPane.showConfirmDialog(parentComponent, languagePanel, "Select language", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//G***i18n
		{
			if(!ObjectUtilities.equals(getLanguage(), languagePanel.getLanguage()))	//if the language has changed
			{
				setLanguage(languagePanel.getLanguage());	//update the language
				if(modifiable!=null)	//if we have a modifiable object
				{
					modifiable.setModified(true);	//show that the value has been modified G***probably replace this with a property change listener or something
				}
			}		
		}
	}
}
