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

package com.globalmentor.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.globalmentor.java.*;
import com.globalmentor.model.Localeable;

/**Action for selecting a language from a dialog.
<p>Bound properties:</p>
<dl>
	<dt>{@link Localeable#LOCALE_PROPERTY_NAME} ({@link Locale})</dt>
	<dd>Indicates that the boolean locale property has been changed.</dd>
</dl>
@author Garret Wilson
@see LanguagePanel
@see Locale
*/
public class SelectLanguageAction extends AbstractAction implements Localeable
{

	/**The locale that represents the language, or <code>null</code>
		if no language is indicated.
	*/
	private Locale locale;

		/**@return The locale that represents the language, or
			<code>null</code> if no language is indicated.
		*/
		public Locale getLocale() {return locale;}

		/**Sets the language.
		@param newLocale The locale that represents the language, or
			<code>null</code> if no language should be indicated.
		*/
		public void setLocale(final Locale newLocale)
		{
			final Locale oldLocale=locale; //get the old locale
			if(!Objects.equals(oldLocale, newLocale))  //if the value is really changing
			{
				locale=newLocale; //update the value
				firePropertyChange(LOCALE_PROPERTY_NAME, oldLocale, newLocale);	//show that the locale property has changed
			}
		}

	/**Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	*/
	private Component parentComponent;

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

	/**Full constructor.
	@param locale The locale that represents the language, or
		<code>null</code> if no language should be initially indicated.
	@param parentComponent Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	*/
	public SelectLanguageAction(final Locale locale, final Component parentComponent)
	{
		super("Language");	//create the base class TODO i18n
		this.locale=locale;	//set the language (don't call the setter method, as superclasses might want to access other locale variables from inside a nested class from the outer class' constructor, which would result in a null-pointer exception even though the outer class' variable had been initialized) 
		this.parentComponent=parentComponent;	//set the parent component
		putValue(SHORT_DESCRIPTION, "Select language");	//set the short description TODO i18n
		putValue(LONG_DESCRIPTION, "Select the language.");	//set the long description TODO i18n
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));  //set the mnemonic key TODO i18n
		putValue(SMALL_ICON, IconResources.getIcon(IconResources.GLOBE_ICON_FILENAME)); //load the correct icon TODO use a better icon
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK)); //add the accelerator
	}
	
	/**Called when the action should be performed.
	@param actionEvent The event causing the action.
	*/
	public void actionPerformed(final ActionEvent actionEvent)
	{
		final LanguagePanel languagePanel=new LanguagePanel(getLocale());	//create a new language panel
			//ask for a new language; if the user accepts the changes
		if(BasicOptionPane.showConfirmDialog(parentComponent, languagePanel, "Select language", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//TODO i18n
		{
			setLocale(languagePanel.getLanguage());	//update the language
		}
	}
}
