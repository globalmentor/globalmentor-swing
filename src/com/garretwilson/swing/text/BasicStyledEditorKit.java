package com.garretwilson.swing.text;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


import static com.globalmentor.java.Objects.*;

import com.garretwilson.sun.demo.jfc.notepad.ElementTreePanel;
import com.garretwilson.swing.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.text.ViewUtilities;
import com.garretwilson.swing.unicode.UnicodePanel;
import com.garretwilson.swing.unicode.UnicodeTableModel;

import com.globalmentor.io.*;
import com.globalmentor.net.ContentType;

import com.globalmentor.text.Text;
import com.globalmentor.util.*;

/**A styled editor kit with basic functionality, including:
<ul>
	<li>Knowing how to retrieve streams to URIs.</li>
	<li>Supporting progress listeners.</li>
</ul>
@author Garret Wilson
*/
public class BasicStyledEditorKit extends StyledEditorKit implements URIInputStreamable
{

	/**The XML media type this editor kit supports, defaulting to <code>text/plain</code>.*/
	private ContentType mediaType=ContentType.getInstance(ContentType.TEXT_PRIMARY_TYPE, Text.PLAIN_SUBTYPE);

		/**@return The XML media type this editor kit supports.*/
		public ContentType getMediaType() {return mediaType;}

		/**Sets the media type this editor kit supports.
		@param newMediaType The new XML media type.
		*/
		protected void setMediaType(final ContentType newMediaType) {mediaType=newMediaType;}

	/**The default default cursor.*/
	private static final Cursor DEFAULT_CURSOR=Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	/**The default cursor to be used when moving items.*/
	private static final Cursor DEFAULT_MOVE_CURSOR=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	/**The default cursor to be used when over links.*/
	private static final Cursor DEFAULT_LINK_CURSOR=DEFAULT_MOVE_CURSOR;

	/**The default cursor.*/
	private Cursor defaultCursor=DEFAULT_CURSOR;

		/**@return The default cursor.*/
		public Cursor getDefaultCursor() {return defaultCursor;}

		/**Sets the default cursor.
		@param newDefaultCursor The cursor to use as the default.
		*/
		public void setDefaultCursor(final Cursor newDefaultCursor) {defaultCursor=newDefaultCursor;}

	/**The default cursor to show when over hyperlinks.*/
	private Cursor defaultLinkCursor=DEFAULT_LINK_CURSOR;

		/**@return The default cursor to display when the mouse is over a link.*/
		public Cursor getDefaultLinkCursor() {return defaultLinkCursor;}

		/**Sets the default cursor used for hyperlink.
		@param newDefaultCursor The cursor to display by default when the mouse is over a link.
		*/
		public void setDefaultLinkCursor(final Cursor newLinkCursor) {defaultLinkCursor=newLinkCursor;}

		/**Determines the cursor to use when over a given link in a particular document.
		This version return the default link cursor.
		@param xmlDocument The document in which the link appears.
		@param uri The link for which a cursor should be obtained.
		@return The cursor to display when the mouse is over the given link.
		*/
		public Cursor getLinkCursor(final Document Document, final URI uri)
		{
			return getDefaultLinkCursor();	//return the default link cursor
		}

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**The identifier for the action to display the element tree.*/
	public static final String DISPLAY_ELEMENT_TREE_ACTION_NAME="display-element-tree-action";

	/**The identifier for the action to display the Unicode table.*/
	public static final String DISPLAY_UNICODE_TABLE_ACTION_NAME="display-unicode-table-action";

	/**The identifier for the previous page action.*/
	public static final String PREVIOUS_PAGE_ACTION_NAME="previous-page-action";

	/**The identifier for the next page action.*/
	public static final String NEXT_PAGE_ACTION_NAME="next-page-action";

	/**Default actions used by this editor kit to augment the super class default actions.*/
	private static final Action[] DEFAULT_ACTIONS=
	{
		new DisplayElementTreeAction(DISPLAY_ELEMENT_TREE_ACTION_NAME),
		new DisplayUnicodeTableAction(DISPLAY_UNICODE_TABLE_ACTION_NAME),
		new PreviousPageAction(PREVIOUS_PAGE_ACTION_NAME),
		new NextPageAction(NEXT_PAGE_ACTION_NAME),
		new BeginAction(beginAction, false),
		new EndAction(endAction, false)
	};

	/**The access to input streams via URIs.*/
	private final URIInputStreamable uriInputStreamable;

		/**@return The access to input streams via URIs.*/
		protected URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}

	/**Constructor.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public BasicStyledEditorKit(final URIInputStreamable uriInputStreamable)
	{
		this.uriInputStreamable=checkInstance(uriInputStreamable, "Missing URIInputStreamable");	//store the URIInputStreamable
	}

	/**Constructor that specifies the specific media type supported.
	@param mediaType The media type supported. In some instances, such as
		<code>text/html</code>, this indicates a default namespace even in the
		absence of a document namespace identfication.
	@param uriInputStreamable The source of input streams for resources.
	*/
	public BasicStyledEditorKit(final ContentType mediaType, final URIInputStreamable uriInputStreamable)
	{
		this(uriInputStreamable);	//do the default construction
		setMediaType(mediaType);  //set the requested media type
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new BasicStyledEditorKit(getMediaType(), getURIInputStreamable());}  //G***why do we need this?; make a real clone, or make sure XHTMLEditorKit overrides this

	/**Returns the MIME type of the data the XML editor kit supports, such as
		<code>text/plain</code>.
	@return The MIME type this editor kit supports.
	*/
	public String getContentType() {return getMediaType().toString();}

	/**Create an uninitialized text storage model that is appropriate for this type of editor.
	This version returns a <code>BasicStyledDocument</code>.
	@return The model.
	*/
	public Document createDefaultDocument()
	{
		return new BasicStyledDocument(getURIInputStreamable());	//create a basic styled document, passing along our source of input streams
	}

	/**Returns an input stream from a given URI.
	This implementation delegates to the editor kit's <code>URIInputStreamable</code>.
	@param uri A complete URI to a resource.
	@return An input stream to the contents of the resource represented by the given URI.
	@exception IOException Thrown if an I/O error occurred.
	*/
	public final InputStream getInputStream(final URI uri) throws IOException
	{
		return getURIInputStreamable().getInputStream(uri);	//delegate to our own URIInputStreamable
	}

	/**Fetches the command list for the editor. This is the list of commands
		supported by the superclass augmented by the collection of commands defined
		locally for such things as page operations.
	@return The command list
	*/
	public Action[] getActions()
	{
		return TextAction.augmentList(super.getActions(), DEFAULT_ACTIONS);
	}

	/**Action to display the document element hierarchy.*/
	protected static class DisplayElementTreeAction extends TextAction
	{

		/**Creates an element tree action with the appropriate name.
		@param name The name of the action.
		*/
		public DisplayElementTreeAction(final String name)
		{
			super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
//G***del Debug.notify("XMLEditorKit.PreviousPageAction.actionPerformed()");
			final JTextComponent textComponent=getTextComponent(actionEvent);	//get the text component
			if(Debug.isDebug())	//if debugging is turned on
			{
				Debug.log(ViewUtilities.toString(textComponent));	//log the views to the debug output
			}
			new BasicFrame("Elements", new ElementTreePanel(textComponent)).setVisible(true);	//show a new frame showing elements G***i18n
		}
	}

	/**Action to display the Unicode table.*/
	protected static class DisplayUnicodeTableAction extends TextAction
	{
		/**The text component that last displayed the Unicode frame.*/
		private JTextComponent textComponent=null;

			/**@return The text component that last displayed the Unicode frame.*/
			protected JTextComponent getTextComponent() {return textComponent;}

		/**The frame used for displaying the Unicode, or <code>null</code> if the frame has not yet been created.*/
		private BasicFrame frame=null;
	
		/**The Unicode panel.*/
		private UnicodePanel unicodePanel=null;

		/**Creates a Unicode table action with the appropriate name.
		@param name The name of the action.
		*/
		public DisplayUnicodeTableAction(final String name)
		{
			super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			textComponent=getTextComponent(actionEvent);	//save the text component that generated this event
			if(frame==null)	//if no frame has been created
			{
				unicodePanel=new UnicodePanel();	//create a new Unicode panel
				unicodePanel.addActionListener(new ActionListener()	//add an action listener to insert characters when selected in the panel
						{
							public void actionPerformed(final ActionEvent actionEvent)	//when an action is performed
							{
								final int codePoint=unicodePanel.getSelectedCodePoint();	//get the selected code point
								final JTextComponent textComponent=getTextComponent();	//get the last-known text component
								if(textComponent!=null && codePoint>=0)	//if we have a text component (we should always have one)
								{
									if(codePoint<=Character.MAX_VALUE)	//if this code point isn't over the Java character limit TODO fix for extended Unicode code points
									{
										textComponent.replaceSelection(String.valueOf((char)codePoint));	//insert the character value of the code point
									}
								}
//TODO del when works					JOptionPane.showMessageDialog(UnicodePanel.this, e.getActionCommand());
							}
						});
				frame=new BasicFrame("Unicode", unicodePanel);	//create a new frame showing Unicode G***i18n
/*TODO fix; we need to either create a new Unicode panel class, or make a way to change the base preferences name
				try
				{
					frame.setPreferences(Preferences.userNodeForPackage(getClass()));	//give the frame unique preferences
				}
				catch(final SecurityException securityException)	//if we can't get preferences
				{
					Debug.warn(securityException);	//don't do anything drastic
				}
*/
				frame.setAlwaysOnTop(true);	//make the frame appear always on top
			}
			frame.setVisible(true);	//show the frame
			frame.pack();	//pack the contents of the frame
//TODO del when works			BasicOptionPane.showMessageDialog(null, new JScrollPane(new UnicodePanel()), "Unicode", BasicOptionPane.INFORMATION_MESSAGE);	//G***i18n
		}
	}

	/**Action to go to the previous available page(es).*/
	protected static class PreviousPageAction extends TextAction
	{

		/**Creates a previous page action with the appropriate name.
		@param name The name of the action.
		*/
		public PreviousPageAction(final String name)
		{
	    super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(ActionEvent actionEvent)
		{
		  final JTextComponent textComponent=getTextComponent(actionEvent); //get the associated text component
		  if(textComponent instanceof XMLTextPane) //if the text pane is an XMLTextPane
		  {
				final XMLTextPane xmlTextPane=(XMLTextPane)textComponent;  //cast the text component to an XML editor pane
				if(xmlTextPane.isPaged())	//if the text pane is paged
				{
					xmlTextPane.goPreviousPage(); //go to the previous page
				}
		  }
		}
	}

	/**Action to go to the next available page(es).*/
	protected static class NextPageAction extends TextAction
	{

		/**Creates a next page action with the appropriate name.
		@param name The name of the action.
		*/
		public NextPageAction(final String name)
		{
	    super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(ActionEvent actionEvent)
		{
		  final JTextComponent textComponent=getTextComponent(actionEvent); //get the associated text component
		  if(textComponent instanceof XMLTextPane) //if the text pane is an XMLTextPane
		  {
				final XMLTextPane xmlTextPane=(XMLTextPane)textComponent;  //cast the text component to an XML editor pane
				if(xmlTextPane.isPaged())	//if the text pane is paged
				{
					xmlTextPane.goNextPage(); //go to the next page
				}
		  }
		}
	}

	/**Action that moves the caret to the beginning of the document.
	<p>This version ensures that the caret is not placed on a hidden view.</p>
	*/
	static class BeginAction extends TextAction
	{
		/* Create this object with the appropriate identifier. */
		BeginAction(String nm, boolean select)
		{
			super(nm);
			this.select= select;
		}

		/** The operation to perform when this action is triggered. */
		public void actionPerformed(ActionEvent e)
		{
			JTextComponent target= getTextComponent(e);
			if (target != null)
			{
				try
				{
					final int beginOffset=SwingTextUtilities.getBegin(target);	//get the beginning offset; if this is an invalid position, don't worry---the corresponding exception will be caught below  
					if(select)
					{
						target.moveCaretPosition(beginOffset);
					}
					else
					{
						target.setCaretPosition(beginOffset);
					}
				}
				catch (BadLocationException badLocationException)
				{
					UIManager.getLookAndFeel().provideErrorFeedback(target);
				}
			}
		}
		private boolean select;
	}

	/**Action that moves the caret to the end of the document.
	<p>This version ensures that the caret is not placed on a hidden view.</p>
	*/
	static class EndAction extends TextAction
	{
		/* Create this object with the appropriate identifier. */
		EndAction(String nm, boolean select)
		{
			super(nm);
			this.select= select;
		}

		/** The operation to perform when this action is triggered. */
		public void actionPerformed(ActionEvent e)
		{
			JTextComponent target= getTextComponent(e);
			if (target != null)
			{
				try
				{
					final int endOffset=SwingTextUtilities.getEnd(target);	//get the ending offset; if this is an invalid position, don't worry---the corresponding exception will be caught below  
					if(select)
					{
						target.moveCaretPosition(endOffset);
					}
					else
					{
						target.setCaretPosition(endOffset);
					}
				}
				catch (BadLocationException badLocationException)
				{
					UIManager.getLookAndFeel().provideErrorFeedback(target);
				}
			}
		}
		private boolean select;
	}

	/**Adds a progress listener.
	@param listener The listener to be notified of progress.
	*/
	public void addProgressListener(ProgressListener listener)
	{
		progressListenerList.add(ProgressListener.class, listener);	//add this listener
	}

	/**Removes a progress listener.
	@param listener The listener that should no longer be notified of progress.
	*/
	public void removeProgressListener(ProgressListener listener)
	{
		progressListenerList.remove(ProgressListener.class, listener);
	}

	/**Notifies all listeners that have registered interest for progress that
		progress has been made.
	@param status The status to display.
	*/
	protected void fireMadeProgress(final ProgressEvent progressEvent)
	{
//G***del if not needed		final ProgressEvent progressEvent=new ProgressEvent(this, status);	//create a new progress event
		final Object[] listeners=progressListenerList.getListenerList();	//get the non-null array of listeners
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each listener, from last to first
		{
			if(listeners[i]==ProgressListener.class)	//if this is a progress listener (it should always be)
				((ProgressListener)listeners[i+1]).madeProgress(progressEvent);
     }
	}

	/**Invoked when progress has been made by, for example, the document.
	@param e The event object representing the progress made.
	*/
/*G***del if no needed
	public void madeProgress(final ProgressEvent e)
	{
		fireMadeProgress(e);	//refire that event to our listeners G***perhaps do some manipulation here
	}
*/

}
