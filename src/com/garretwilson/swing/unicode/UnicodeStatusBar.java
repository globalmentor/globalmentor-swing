package com.garretwilson.swing.unicode;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import com.garretwilson.swing.*;
import com.garretwilson.text.unicode.*;

/**A status bar showing Unicode code point value, name, and category.
@author Garret Wilson
*/
public class UnicodeStatusBar extends StatusBar implements CaretListener
{

	/**The label for a Unicode character value.*/
	protected final JLabel unicodeCharacterValueLabel;

	/**The label for a Unicode character name.*/
	protected final JLabel unicodeCharacterNameLabel;

	/**Default constructor.*/
	public UnicodeStatusBar()
	{
		this(true);	//construct a panel and initialize it	
	}

	/**Constructor with optional initialization.
	@param initialize <code>true</code> if the toolbar should initialize itself by
		calling the initialization methods.
	*/
	public UnicodeStatusBar(final boolean initialize)
	{
		super("Unicode Character Information", false);	//construct the parent class without intitializing it	//TODO i18n
		unicodeCharacterValueLabel=createStatusLabel();	//create a label for the Unicode character value
		unicodeCharacterNameLabel=createStatusLabel();	//create a label for the Unicode character name
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		addStatusComponent(unicodeCharacterValueLabel);	//add the Unicode character value label
		addStatusComponent(unicodeCharacterNameLabel);	//add the Unicode character name label
	}

	/**Updates the status, such as the Unicode character information labels.*/
/*G***del if not needed
	protected void updateStatus()
	{
		super.updateStatus(); //update the default actions
		UnicodeCharacter unicodeCharacter;	//see if we can find a Unicode character under the caret
		try
		{
			final int caretDot=getXMLTextPane().getCaret().getDot();	//get the current caret position
			final String caretCharacterString=getXMLTextPane().getText(caretDot, 1);	//get the single character under the caret
			unicodeCharacter=UnicodeData.getUnicodeCharacter(caretCharacterString.charAt(0));	//get Unicode character data for the character under the cursor
		}
		catch(BadLocationException e)	//if the caret isn't at a valid position
		{
			unicodeCharacter=null;	//show that we don't have a Unicode character
		}
		if(unicodeCharacter!=null)	//if we found Unicode character information
		{
			unicodeCharacterValueLabel.setText(unicodeCharacter.toString());	//show the Unicode character value
			unicodeCharacterNameLabel.setText(unicodeCharacter.getUniqueCharacterName());	//show the Unicode character name
		}
		else	//if we didn't find a Unicode character under the caret
		{
			unicodeCharacterValueLabel.setText("");	//show no Unicode character value
			unicodeCharacterNameLabel.setText("");	//show no Unicode character name			
		}
	}
*/

	/**Updates the status, such as the Unicode character information labels, for
		the text component character at the caret position.
		@param textComponent The text component that holds Unicode characters.
	*/
	public void updateStatus(final JTextComponent textComponent)
	{
		updateStatus(textComponent, textComponent.getCaretPosition());	//update the status for the text component for the character at the given position
	}

	/**Updates the status, such as the Unicode character information labels, for
		the text component character at the given position.
	@param textComponent The text component that holds Unicode characters.
	@param position The position for which the status should be shown.
	*/
	public void updateStatus(final JTextComponent textComponent, final int position)
	{
		UnicodeCharacter unicodeCharacter;	//see if we can find a Unicode character under the caret
		try
		{
			final String unicodeString=textComponent.getText(position, 1);	//get the single character at the given position
			unicodeCharacter=UnicodeData.getUnicodeCharacter(unicodeString.charAt(0));	//get Unicode character data for the character at the position
		}
		catch(BadLocationException e)	//if the caret isn't at a valid position
		{
			unicodeCharacter=null;	//show that we don't have a Unicode character
		}
		if(unicodeCharacter!=null)	//if we found Unicode character information
		{
			unicodeCharacterValueLabel.setText(unicodeCharacter.toString());	//show the Unicode character value
			unicodeCharacterNameLabel.setText(unicodeCharacter.getUniqueCharacterName());	//show the Unicode character name
		}
		else	//if we didn't find a Unicode character under the caret
		{
			unicodeCharacterValueLabel.setText("");	//show no Unicode character value
			unicodeCharacterNameLabel.setText("");	//show no Unicode character name			
		}		
	}

	/**Called when a text component caret is updated and updates the Unicode
		status labels to represent the character under the caret. 
	@param caretEvent The event that holds information about the caret change.
	@see #updateStatus(JTextComponent, int)
	*/
	public void caretUpdate(final CaretEvent caretEvent)
	{
		final Object source=caretEvent.getSource();	//get the source of the event
		if(source instanceof JTextComponent)	//if a text component made the change
		{
			updateStatus((JTextComponent)source, caretEvent.getDot());	//update the status for the character under the new cursor position
		}
	}
}
