package com.garretwilson.swing.unicode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import static com.garretwilson.lang.Objects.*;
import com.garretwilson.swing.*;
import static com.garretwilson.swing.text.TextComponentConstants.*;
import com.garretwilson.text.unicode.*;

/**A status bar showing Unicode code point value, name, and category.
@author Garret Wilson
*/
public class UnicodeStatusBar extends StatusBar
{

	/**The last known position in a document, or <code>-1</code> if not known.*/
	private int position=-1;

		/**@return The last known position in a document, or <code>-1</code> if not known.*/
		protected int getPosition() {return position;};

	/**The label for a Unicode character value.*/
	protected final JLabel unicodeCharacterValueLabel;

	/**The label for a Unicode character name.*/
	protected final JLabel unicodeCharacterNameLabel;

	/**The caret listener that updates the status bar in response to a caret event.
	@see #updateStatus(CaretEvent)
	*/
	protected final CaretListener caretListener=new CaretListener()
			{
				public void caretUpdate(final CaretEvent caretEvent) {updateStatus(caretEvent);}
			};

	/**The document listener that updates the status bar in response to a document event.
	@see #updateStatus(DocumentEvent)
	*/
	protected final DocumentListener documentListener=new DocumentListener()
			{
				public void insertUpdate(final DocumentEvent documentEvent) {updateStatus(documentEvent);}
				public void removeUpdate(final DocumentEvent documentEvent) {updateStatus(documentEvent);}
				public void changedUpdate(final DocumentEvent documentEvent) {updateStatus(documentEvent);}
			};

	/**The document change listener that updates the document listener in response to a document change.
	@see #onDocumentChange(Document, Document)
	*/
	protected final PropertyChangeListener documentPropertyChangeListener=new PropertyChangeListener()
			{
				public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
				{
					onDocumentChange(asInstance(propertyChangeEvent.getOldValue(), Document.class), asInstance(propertyChangeEvent.getNewValue(), Document.class)); 
				}
			};
			
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
		super("Unicode Character Information", false);	//construct the parent class without initializing it	//TODO i18n
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

	/**The currently displayed Unicode character, or <code>null</code> if no character is currently being displayed.*/
	private UnicodeCharacter unicodeCharacter=null;

		/**@return The currently displayed Unicode character, or <code>null</code> if no character is currently being displayed.*/
		public UnicodeCharacter getUnicodeCharacter() {return unicodeCharacter;}

	/**The currently displayed Unicode code point, or <code>-1</code> if no code point is currently being displayed.*/
	private int codePoint=-1;

		/**@return The currently displayed Unicode code point, or <code>-1</code> if no code point is currently being displayed.*/
		public int getCodePoint() {return codePoint;}

	/**Sets the Unicode character status by changing the displayed code point.
	@param codePoint The Unicode code point, or <code>-1</code> for no code point.
	*/
	public void setCodePoint(final int codePoint)
	{
		if(getCodePoint()!=codePoint)	//if the code point is really changing
		{
			this.codePoint=codePoint;	//save the code point
			unicodeCharacter=codePoint>=0 ? UnicodeData.getUnicodeCharacter(codePoint) : null;	//if a code point was indicated, try to get a character corresponding to the code point
			if(codePoint>=0)	//if a valid code point was indicated
			{
				unicodeCharacterValueLabel.setText(UnicodeCharacter.getCodePointString(codePoint));	//show the Unicode character value
				if(unicodeCharacter!=null)	//if we found Unicode character information
				{
					unicodeCharacterNameLabel.setText(unicodeCharacter.getUniqueCharacterName());	//show the Unicode character name
				}
				else	//if we didn't find a Unicode character
				{
					unicodeCharacterNameLabel.setText("");	//show no Unicode character name			
				}
			}
			else	//if we don't have a valid Unicode code point
			{
				unicodeCharacterValueLabel.setText("");	//show no Unicode character value
				unicodeCharacterNameLabel.setText("");	//show no Unicode character name			
			}
		}
	}
	
	/**Updates the status, such as the Unicode character information labels, for
		the text component character at the caret position.
		@param textComponent The text component that holds Unicode characters.
	*/
	public void updateStatus(final JTextComponent textComponent)
	{
		updateStatus(textComponent.getDocument(), textComponent.getCaretPosition());	//update the status for the text component for the character at the given position
	}

	/**Updates the status, such as the Unicode character information labels, for
		the text component character at the given position.
	The given position is saved. 
	@param document The document that holds Unicode characters.
	@param position The position for which the status should be shown.
	*/
	public void updateStatus(final Document document, final int position)
	{
		try
		{
			final String unicodeString=document.getText(position, 1);	//get the single character at the given position
			setCodePoint(unicodeString.charAt(0));	//set the code point to the character at the given position
		}
		catch(BadLocationException e)	//if the caret isn't at a valid position
		{
			unicodeCharacter=null;	//show that we don't have a Unicode character
		}
		this.position=position;	//save the position
	}

	/**Updates the status when a text component caret is updated by updating the Unicode
		status labels to represent the character under the caret. 
	@param caretEvent The event that holds information about the caret change.
	@see #updateStatus(JTextComponent, int)
	*/
	protected void updateStatus(final CaretEvent caretEvent)
	{
		final Object source=caretEvent.getSource();	//get the source of the event
		if(source instanceof JTextComponent)	//if a text component made the change
		{
			updateStatus(((JTextComponent)source).getDocument(), caretEvent.getDot());	//update the status for the character under the new cursor position
		}
	}

	/**Updates the status when a text component document is changed if that change appears under the cursor. 
	@param documentEvent The event that holds information about the document change.
	@see #updateStatus(JTextComponent, int)
	*/
	protected void updateStatus(final DocumentEvent documentEvent)
	{
		final int position=getPosition();	//get our current position
		final int eventOffset=documentEvent.getOffset();	//get the event's offset
		if(position>=eventOffset && position<eventOffset+documentEvent.getLength())	//if our position is within the event range
		{			
			updateStatus(documentEvent.getDocument(), position);	//update the status for the current character under the position
		}
	}

	/**Updates the document changes listeners based upon the change of document.
	@param oldDocument The old document, or <code>null</code> if there was no previous document.
	@param newDocument The new document, or <code>null</code> if there is no new document.
	*/
	protected void onDocumentChange(final Document oldDocument, final Document newDocument)
	{
		if(oldDocument!=null)	//if there was a previous document
		{
			oldDocument.removeDocumentListener(documentListener);	//stop listening for document changes from the old document
		}
		if(newDocument!=null)	//if there is a new document
		{
			newDocument.addDocumentListener(documentListener);	//listen for document changes in the new document
		}
	}

	/**Attaches this status bar to a text component so that it will be updated when the text component changes.
	@param textComponent The text component to which the status bar should be attached.
	*/ 
	public void attach(final JTextComponent textComponent)
	{
		textComponent.addCaretListener(caretListener);	//listen for caret events
		textComponent.getDocument().addDocumentListener(documentListener);	//listen for document changes in the document
		textComponent.addPropertyChangeListener(DOCUMENT_PROPERTY, documentPropertyChangeListener);	//listen for document changes
		updateStatus(textComponent);	//update the status for the text component, which will save the current caret position
	}

	/**Detaches this status bar from a text component so that it will no longer be be updated when the text component changes.
	@param textComponent The text component from which the status bar should be detached.
	*/ 
	public void detach(final JTextComponent textComponent)
	{
		textComponent.removeCaretListener(caretListener);	//stop listening for caret events
		textComponent.getDocument().removeDocumentListener(documentListener);	//stop listening for document changes in the document
		textComponent.removePropertyChangeListener(DOCUMENT_PROPERTY, documentPropertyChangeListener);	//listen for document changes
		position=-1;	//show that we no longer represent a valid position
	}
}
