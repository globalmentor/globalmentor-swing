package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;  //G***fix for image mouse clicks
import java.beans.*;
import java.io.*;
import java.net.*;
import java.net.URI;
import java.text.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;  //G***del when image click code is moved elsewhere
import com.garretwilson.io.*;
import com.garretwilson.net.*;
import com.garretwilson.rdf.*;
import com.garretwilson.resources.icon.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.rdf.*;
import com.garretwilson.swing.text.*;
import com.garretwilson.swing.text.Annotation;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.oeb.OEBDocument; //G***eventually del
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
import com.garretwilson.text.xml.oeb.*;
import com.garretwilson.util.*;
import edu.stanford.ejalbert.*;

/**A component shows information in book form. Has an XMLTextPane as a child.
	<p>Bound properties:</p>
	<ul>
	  <li><code>BOOKMARKS_PROPERTY</code> Indicates the bookmarks have
			chnaged. Returns <code>null</code> for old and new values.</li>
	  <li><code>HISTORY_INDEX_PROPERTY</code> Indicates the history has
			changed. Returns the old and new history index.</li>
	</ul>
@author Garret Wilson
@see javax.swing.JComponent
@see javax.swing.JPanel
@see com.garretwilson.swing.XMLTextPane
*/
public class Book extends JComponent implements PageListener, AdjustmentListener, CaretListener, MouseListener  //G***testing MouseListener for image viewing
{
	/**The property representing bookmark changes.*/
	public final static String BOOKMARKS_PROPERTY="bookmarks";

	/**The property representing the history index.*/
	public final static String HISTORY_INDEX_PROPERTY="historyIndex";

	/**The property which stores the user data <code>File</code> object.*/
	public final static String USER_DATA_FILE_PROPERTY="userData";

	/**The highlight painter used for displaying bookmark locations.*/
	protected final static BookmarkHighlightPainter bookmarkHighlightPainter=new BookmarkHighlightPainter();

	/**Keeps track of whether a mouse press or release was the popup trigger; if
		so, it disables the normal mouse click functionality from occurring.
	*/
	private boolean mousePressReleasePopupTrigger=false;

	/**The text pane used to display information.*/
	private XMLTextPane TextPane; //G***do we want this to just be a text pane? probably not

		/**@return The text pane used to display information.*/
		public XMLTextPane getXMLTextPane() {return TextPane;}	//G***maybe later change this to protected access

		/**Sets the text pane used to display information.
		@param textPane The XML text pane component.
		*/
		private void setXMLTextPane(final XMLTextPane textPane)
		{
			TextPane=textPane;
				//catch all document changes in the text pane, since the document is actually changed in a separate thread
			textPane.addPropertyChangeListener(XMLTextPane.DOCUMENT_PROPERTY, new PropertyChangeListener()
				{
				  //if the document property changes, call documentChange()
				  public void propertyChange(final PropertyChangeEvent event) {documentChange();}
				});
		  textPane.addPageListener(this); //add ourselves as a page listener, so that we can update the forward and backwards actions G***we probably want to get the events directly from the book
		}

	/**The scrollbar for showing the position within the book.*/
	private JScrollBar scrollBar=null;

		/**@return The scrollbar for showing the position within the book, or
		  <code>null</code> if there is no scrollbar.
		*/
		public JScrollBar getScrollBar() {return scrollBar;}

		/**Sets the scrollbar for showing the position within the book.
		@param newScrollBar The scrollbar to use for showing the book position.
		*/
		public void setScrollBar(final JScrollBar newScrollBar)
		{
			if(scrollBar!=newScrollBar) //if we're really changing scrollbars,
		  {
				if(scrollBar!=null)  // and we already had a scrollbar
				{
					scrollBar.removeAdjustmentListener(this); //remove ourselves as an adjustment listener
				}
				scrollBar=newScrollBar; //change scrollbars
				newScrollBar.addAdjustmentListener(this); //tell the scrollbar we want to know when it changes
		  }
		}

	/**The action for navigating to the previous page.*/
	private final Action previousPageAction=new PreviousPageAction();

		/**@return The action for navigating to the previous page.*/
		public Action getPreviousPageAction() {return previousPageAction;}

	/**The action for navigating to the next page.*/
	private final Action nextPageAction=new NextPageAction();

		/**@return The action for navigating to the next page.*/
		public Action getNextPageAction() {return nextPageAction;}

	/**The action for moving backwards in navigation history.*/
	private final Action backAction=new BackAction();

		/**@return The action for moving backwards in navigation history.*/
		public Action getBackAction() {return backAction;}

	/**The action for closing a book.*/
	private final Action closeAction=new CloseAction();

		/**@return The action for closing a book.*/
		public Action getCloseAction() {return closeAction;}

	/**The action for copying text.*/
	private final Action copyAction=new CopyAction();

		/**@return The action for copying text.*/
		public Action getCopyAction() {return copyAction;}

	/**The action for inserting a highlight.*/
	private final Action insertHighlightAction=new InsertHighlightAction();

		/**@return The action for inserting a highlight, which is updated to be
		  enabled or disabled at the appropriate times based upon the selection.
		*/
		public Action getInsertHighlightAction() {return insertHighlightAction;}

	/**The action for displaying the book properties.*/
	private final Action viewPropertiesAction=new ViewPropertiesAction();

		/**@return The action for displaying the book properties.*/
		public Action getViewPropertiesAction() {return viewPropertiesAction;}

	/**The current highlight color (defaults to yellow).*/
	private Color highlightColor=Color.yellow;

		/**@return The current highlight color.*/
		public Color getHighlightColor() {return highlightColor;}

		/**Sets the current highlight color.
		@param color The new highlight color.
		*/
		public void setHighlightColor(final Color color) {highlightColor=color;}

	/**The map of highlight tags for the book, keyed to bookmarks. This map
		therefore serves as both a definitive list of bookmarks and a map of
		highlights which correspond to those bookmarks. This tree map ensures
		that the key set will always be in natural bookmark order, from lowest
		to highest.*/  //G***maybe make this a bound property
	private final Map bookmarkHighlightTagMap=new TreeMap();

		/**@return The list of bookmarks in the book.*/ //G***maybe later just make an Iterator available
//G***del		protected List getBookmarkList() {return bookmarkList;}

		/**Adds an unnamed bookmark at the current location in the document.*/
		public void addBookmark()
		{
			addBookmark((String)null);  //add a bookmark with no name
		}

		/**Adds a bookmark with a specified name at the current location in the
		  document.
		@param name The name to give the bookmark, or <code>null</code> if the
			bookmark should not be given a name.
		*/
		public void addBookmark(final String name)
		{
Debug.trace();  //G***del
			int offset=-1; //we'll store the offset here at which the bookmark should be inserted
				//see if there is a selection
			final boolean isSelection=getXMLTextPane().getSelectionEnd()>getXMLTextPane().getSelectionStart();
			if(isSelection) //if there is a selection
			{
				offset=getXMLTextPane().getSelectionStart(); //we'll put the bookmark at the start of the selection
			}
			else  //if nothing is selected, put the bookmark at the start of the current page
			{
				final int pageIndex=getPageIndex(); //get our current page index
				if(pageIndex!=-1) //if we have a valid page index
				{
				  offset=getXMLTextPane().getPageStartOffset(pageIndex);  //get the starting offset of our page
				}
			}
			if(offset>=0) //if we found a valid offset
			{
				try
				{
					addBookmark(name, offset);  //add a bookmark with this name at this offset
				}
				catch(BadLocationException e) //we should never have a bad location
				{
					Debug.error(e); //report the error
				}
			}
		}

		/**Adds a bookmark with a specified name at the specified location in the
		  document.
		@param name The name to give the bookmark, or <code>null</code> if the
			bookmark should not be given a name.
		@param offset The position in the document at which the bookmark should be added.
		@exception BadLocationException Thrown if the position represents an invalid
			location in the document
		*/
		public void addBookmark(final String name, final int offset) throws BadLocationException
		{
			final Bookmark bookmark=new Bookmark(name, getXMLTextPane().getDocument(), offset); //create a bookmark at the beginning of our first page
			addBookmark(bookmark);  //add the bookmark
		}

		/**Adds a bookmark to the book, attaching it at its specified offset if
		  the bookmark isn't already attached to the document. If the bookmark
			already is added, no action will take place.
		@param bookmark The bookmark to add.
		@exception BadLocationException Thrown if the bookmark represents an invalid
			location in the document
		*/
		public void addBookmark(final Bookmark bookmark) throws BadLocationException
		{
			if(bookmarkHighlightTagMap.get(bookmark)==null) //if this bookmark isn't already added
			{
				if(!bookmark.isAttached())  //if the bookmark isn't attached to the document
					bookmark.attach(getXMLTextPane().getDocument());  //attach the bookmark to the document
				//add a bookmark highlighter to the text pane to show the bookmark at the correct location
				final Object bookmarkHighlight=getXMLTextPane().getHighlighter().addHighlight(bookmark.getOffset(), bookmark.getOffset()+1, bookmarkHighlightPainter);
Debug.trace("bookmark highlight returned is: ", bookmarkHighlight); //G***del
				bookmarkHighlightTagMap.put(bookmark, bookmarkHighlight); //add the bookmark highlight to the map, keyed to the bookmark
Debug.trace("key set: ", bookmarkHighlightTagMap.keySet());
Debug.trace("values: ", bookmarkHighlightTagMap.values());

Debug.trace("after putting bookmark, found tag: ", bookmarkHighlightTagMap.get(bookmark));
Debug.trace("contains bookmark key: ", new Boolean(bookmarkHighlightTagMap.containsKey(bookmark)));
//G***del Debug.trace("contains value key: ", new Boolean(bookmarkHighlightTagMap.containsKey(bookmarkHighlight)));
Debug.trace("contains value: ", new Boolean(bookmarkHighlightTagMap.containsValue(bookmarkHighlight)));
				firePropertyChange(BOOKMARKS_PROPERTY, null, null); //fire an event showing that the bookmarks have changed
			}
		}

		/**Removes a bookmark from the book, if the book currently has the bookmark.
		@param bookmark The bookmark to remove.
		*/
		public void removeBookmark(final Bookmark bookmark)
		{
Debug.trace("removing bookmark: ", bookmark);
			final Object bookmarkHighlightTag=bookmarkHighlightTagMap.get(bookmark);  //get the highlight for this bookmark
Debug.trace("found highlight tag: ", bookmarkHighlightTag);
			if(bookmarkHighlightTag!=null) //if we have a highlight for this bookmark
			{
Debug.trace("ready to remove tag from map");
				bookmarkHighlightTagMap.remove(bookmark);  //remove the bookmark and highlight tag from the map
				getXMLTextPane().getHighlighter().removeHighlight(bookmarkHighlightTag);  //remove the highlight itself
Debug.trace("ready to fire property change");
				firePropertyChange(BOOKMARKS_PROPERTY, null, null); //fire an event showing that the bookmarks have changed
			}
		}

		/**Finds and returns the last bookmark nearest the given position.
		@param offset The position in a document for which a bookmark should be
			returned.
		@return The last bookmark at the given position, or <code>null</code> if
			there are no bookmarks at the given position.
		@see Bookmark#contains
		*/
		public Bookmark getBookmark(final int offset)
		{
			Bookmark bookmark=null; //show that we haven't yet found a bookmark
			final Iterator iterator=getBookmarkIterator();  //get an iterator to search the bookmarks
			while(iterator.hasNext()) //while there are more bookmarks
			{
				final Bookmark currentBookmark=(Bookmark)iterator.next();  //get the next bookmark
//G***del when works				if(currentBookmark.getOffset()==offset)  //if this bookmark starts at the offset
				if(currentBookmark.contains(offset))  //if this bookmark starts at the offset
					bookmark=currentBookmark; //store the bookmark and look for another one
			}
			return bookmark;  //return the last matching bookmark, or null if there was no match
		}

		/**Removes all bookmarks from the book.*/
		public void clearBookmarks()
		{
			bookmarkHighlightTagMap.clear();  //clear the bookmarks and their corresponding highlights
		  TextComponentUtilities.removeHighlights(getXMLTextPane(), bookmarkHighlightPainter);  //remove all bookmark highlights G***it would probably be better to remove them one at a time with the highlight tag
			firePropertyChange(BOOKMARKS_PROPERTY, null, null); //fire an event showing that the bookmarks have changed
		}

		/**@return A read-only iterator of all available bookmarks in natural order.*/
		public Iterator getBookmarkIterator()
		{
		  return Collections.unmodifiableSet(bookmarkHighlightTagMap.keySet()).iterator(); //return a read-only iterator to the bookmarks (the keys are already sorted because they are stored in a TreeMap)
		}

	/**The map of highlight tags for the book, keyed to annotations. This map
		therefore serves as both a definitive list of annotations and a map of
		highlights which correspond to those annotations. This tree map ensures
		that the key set will always be in natural annotation order, from lowest
		to highest.*/
	private final Map annotationHighlightTagMap=new TreeMap();

		/**Adds an annotation with a specified starting and ending offsets in the
		  document.
		@param startOffset The position in the document at which the annotation
			should be added.
		@param endOffset The position in the document which indicates the end of the
			annotation.
		@param color The highlight color of the annotation.
		@exception BadLocationException Thrown if the position represents an invalid
			location in the document
		*/
		public void addAnnotation(final int startOffset, final int endOffset, final Color color) throws BadLocationException
		{
			final Annotation annotation=new Annotation(getXMLTextPane().getDocument(), startOffset, endOffset, color); //create a bookmark at the beginning of our first page
			addAnnotation(annotation);  //add the annotation
		}

		/**Adds an annotation to the book, attaching it at its specified offsets if
		  the annotation isn't already attached to the document. If the annotation
			already is added, no action will take place.
		@param annotation The annotation to add.
		@exception BadLocationException Thrown if the bookmark represents an invalid
			location in the document
		*/
		public void addAnnotation(final Annotation annotation) throws BadLocationException
		{
			if(annotationHighlightTagMap.get(annotation)==null) //if this annotation isn't already added
			{
				if(!annotation.isAttached())  //if the annotation isn't attached to the document
					annotation .attach(getXMLTextPane().getDocument());  //attach the annotation to the document
				final Highlighter.HighlightPainter annotationHighlightPainter=new DefaultHighlighter.DefaultHighlightPainter(annotation.getColor());  //G***testing; comment
				//add an annotation highlighter to the text pane to show the annotation at the correct location
				final Object annotationHighlight=getXMLTextPane().getHighlighter().addHighlight(annotation.getStartOffset(), annotation.getEndOffset(), annotationHighlightPainter);
				annotationHighlightTagMap.put(annotation, annotationHighlight); //add the annotation highlight to the map, keyed to the annotation
//G***fix				firePropertyChange(BOOKMARKS_PROPERTY_NAME, null, null); //fire an event showing that the bookmarks have changed
			}
		}

		/**Removes an annotation from the book, if the book currently has the annotation.
		@param annotation The annotation to remove.
		*/
		public void removeAnnotation(final Annotation annotation)
		{
			final Object annotationHighlightTag=annotationHighlightTagMap.get(annotation);  //get the highlight for this annotation
			if(annotationHighlightTag!=null) //if we have a highlight for this annotation
			{
				annotationHighlightTagMap.remove(annotation);  //remove the annotation and highlight tag from the map
				getXMLTextPane().getHighlighter().removeHighlight(annotationHighlightTag);  //remove the highlight itself
//G***fix				firePropertyChange(BOOKMARKS_PROPERTY_NAME, null, null); //fire an event showing that the bookmarks have changed
			}
		}

		/**Finds and returns the last annotation containing given position.
		@param offset The position in a document for which an annotation should be
			returned.
		@return The last annotation containing the given position, or
			<code>null</code> if no annotations contain at the given position.
		@see Bookmark#contains
		*/
		public Annotation getAnnotation(final int offset)
		{
			Annotation annotation=null; //show that we haven't yet found a annotation
			final Iterator iterator=getAnnotationIterator();  //get an iterator to search the annotations
			while(iterator.hasNext()) //while there are more annotations
			{
				final Annotation currentAnnotation=(Annotation)iterator.next();  //get the next annotation
				if(currentAnnotation.contains(offset))  //if this annotation contains the given offset
					annotation=currentAnnotation; //store the annotation and look for another one
			}
			return annotation;  //return the last matching annotation, or null if there was no match
		}

		/**Removes all annotations from the book.*/
		public void clearAnnotations()
		{
			final Set annotationSet=annotationHighlightTagMap.keySet(); //get the set of annotations
				//convert the annotation set to an array so we will have a local copy as we remove the annotations
		  final Annotation[] annotationArray=(Annotation[])annotationSet.toArray(new Annotation[annotationSet.size()]);
			for(int i=annotationArray.length-1; i>=0; --i)  //look at each annotation
				removeAnnotation(annotationArray[i]); //remove this annotation
		}

		/**@return A read-only iterator of all available annotations in natural order.*/
		public Iterator getAnnotationIterator()
		{
		  return Collections.unmodifiableSet(annotationHighlightTagMap.keySet()).iterator(); //return a read-only iterator to the annotations (the keys are already sorted because they are stored in a TreeMap)
		}

	/**@return The URI of the loaded publication or file, or <code>null</code> if
		there is no file loaded.
	@see XMLTextPane#getBaseURI()
	*/
	public URI getURI()
	{
		return getXMLTextPane().getBaseURI();  //get the base URI property value
	}

	/**@return The RDF data model, if this book's
		text pane has an XML document with RDF metadata.
	@return The RDF data model associated with the book, or <code>null</code> if
		there is no RDF metadata.
//	G***this will eventually probably go somewhere else, when this turns into an XMLReader component or something; we'll probably store the publication in a property
	*/
	public RDF getRDF()
	{
		final Document document=getXMLTextPane().getDocument(); //get the document associated with the text pane
		if(document instanceof XMLDocument) //if the document is an XML document
		  return ((XMLDocument)document).getRDF();  //get the RDF data model from the XML document G***why don't we get the property value directly?
	  return null;  //show that we could not find an RDF data model
	}

	/**@return The OEB publication associated with the document, if this book's
		text pane has an OEB document with an OEB publication.
	@return The OEB publication associated with the book, or <code>null</code> if
		there is none.
//	G***this will eventually probably go somewhere else, when this turns into an XMLReader component or something; we'll probably store the publication in a property
	*/
	public OEBPublication getOEBPublication()
	{
		final Document document=getXMLTextPane().getDocument(); //get the document associated with the text pane
		if(document instanceof OEBDocument) //if the document is an OEB document
		  return ((OEBDocument)document).getPublication();  //get the OEB publication object from the OEB document G***why don't we get the property value directly?
	  return null;  //show that we could not find a publication
	}

	/**@return The file object representing the user data file associated with
		the loaded publication or file, or <code>null</code> if	there is no user
		data file.
	@see #USER_DATA_FILE_PROPERTY
	*/
	public File getUserDataFile()
	{
		final Object userDataFile=getXMLTextPane().getDocument().getProperty(USER_DATA_FILE_PROPERTY); //get the user data file from the document
		return userDataFile instanceof File ? (File)userDataFile : null;  //return the file, if that's really what it is; otherwise, return null
	}

/*G***fix
	/**
	private int DisplayPageCount;
*/

	/**@return The number of pages to display at a time.
	@see XMLTextPane#getDisplayPageCount
	@see XMLPagedView#getDisplayPageCount
	*/
	public int getDisplayPageCount() {return getXMLTextPane().getDisplayPageCount();}

	/**Sets the number of pages to display at a time.
	@param displayPageCount The new number of pages to display at a time.
	@see XMLTextPane#setDisplayPageCount
	@see XMLPagedView#setDisplayPageCount
	*/
	public void setDisplayPageCount(final int displayPageCount)
	{
		getXMLTextPane().setDisplayPageCount(displayPageCount);	//tell the text pane the number of pages do be displayed
	}

		/**@return Whether text is antialiased.*/
		public boolean isAntialias() {return getXMLTextPane().isAntialias();}

		/**Sets whether text is antialiased.
		@param newAntialias Whether text should be antialias.
		*/
		public void setAntialias(final boolean newAntialias) {getXMLTextPane().setAntialias(newAntialias);}

	/**@return The factor by which text should be zoomed, default 1.00.*/
	public float getZoomFactor() {return getXMLTextPane().getZoomFactor();}

	/**Sets the factor by which text should be zoomed.
	@param newZoomFactor The amount by which normal text should be multiplied.
	*/
	public void setZoomFactor(final float newZoomFactor){getXMLTextPane().setZoomFactor(newZoomFactor);}

	//history
	//G***probably make something to limit the size of the history list
	//G***don't forget to clear the history list when a new document is loaded
	//G***tie the history to a property

	/**The list of history positions.*/
	private java.util.List historyList=new ArrayList();

	/**The index of the next history index to populate.*/
	private int historyIndex=0;

		/**@return The index representing the next history index to populate; also
			represents the number of previous history items available.
		*/
		public int getHistoryIndex() {return historyIndex;}

		/**Updates the history index and fires a property changed event.
		@param newHistoryIndex The new history index.
		*/
		private void setHistoryIndex(final int newHistoryIndex)
		{
			final int oldHistoryIndex=getHistoryIndex();  //get the old history index
			if(newHistoryIndex!=oldHistoryIndex)  //if the history index is really changing
			{
				historyIndex=newHistoryIndex; //update the history index
				getBackAction().setEnabled(hasPreviousHistory());  //only enable the back button if there is previous history
				firePropertyChange(HISTORY_INDEX_PROPERTY, oldHistoryIndex, newHistoryIndex); //fire an event showing that the property changed
			}
		}

	/**@return Whether or not there is past history that can be visited.*/
	public boolean hasPreviousHistory()
	{
		return getHistoryIndex()>0;
	}

	/**@return Whether or not there is next history that can be visited.*/
	public boolean hasNextHistory()
	{
		return getHistoryIndex()<historyList.size();
	}

	/**Returns the previous history and moves the history index back one.
	@return The previous history position, or <code>null</code> if not available.
	*/
	protected Position decrementHistory()
	{
		if(hasPreviousHistory())  //if there is previous history
		{
			final int previousHistoryIndex=getHistoryIndex()-1; //get the previous history index
			final Position position=(Position)historyList.get(previousHistoryIndex);  //get the previous history position
		  setHistoryIndex(previousHistoryIndex);  //decrement the history index, firing a property changed event
		  return position;  //return the position
		}
		else  //if there is no previous history
		  return null;  //show that there is no previous history
	}

	/**Returns the next history and moves the history index forward one.
	@return The next history position, or <code>null</code> if not available.
	*/
	protected Position incrementHistory()
	{
		if(hasNextHistory()) //if there is next history
		{
			final int historyIndex=getHistoryIndex(); //get the current history index
			final Position position=(Position)historyList.get(historyIndex); //get the item at the current history index
		  setHistoryIndex(historyIndex+1);  //increment the history index, firing a property changed event
		  return position;  //return the position
		}
		else  //if there is no next history
		  return null;  //show that there is no next history
	}

	/**Adds a history position to the list. If there were future history positions,
		they are removed.
	*/
	protected void addHistory(final Position position)
	{
		//make sure all history positions after and including the current history index are removed
		for(int i=historyList.size(); i>historyIndex; historyList.remove(--i));
		historyList.add(position); //add this position to our history list
		setHistoryIndex(getHistoryIndex()+1); //increment our history index
	}

	/**Stores the current position in the history list.*/
	protected void storePositionHistory()
	{
Debug.trace();  //G***del
		final int pageIndex=getPageIndex(); //get our current page index
		if(pageIndex!=-1) //if we have a valid page index
		{
//G***del Debug.trace("pageIndex: "+pageIndex);
			final int offset=getXMLTextPane().getPageStartOffset(pageIndex);  //get the starting offset of our page
//G***del Debug.trace("offset: "+offset);
//G***del 		  Debug.trace(""+getXMLTextPane().getDocument().getStartPosition());  //G***testing
		  try
			{
//G***del Debug.trace("Before creating position from offset: "+offset+" document length: "+getXMLTextPane().getDocument().getLength());
				final Position position=getXMLTextPane().getDocument().createPosition(offset);  //create a position from our offset
//G***del Debug.trace("After creating position from offset: "+offset);
				addHistory(position); //add this position to our history list
			}
			catch(BadLocationException e) //we should never have a bad location
			{
				Debug.error(e); //report the error
			}
		}
	}

	/**Constructs a new <code>Book</code> with the specified number of pages
		displayed.
	@param displayPageCount The number of pages to display.
	@see com.garretwilson.swing.OEBTextPane
	*/
	public Book(final int displayPageCount)
	{
		super();	//construct the parent class
		backAction.setEnabled(false); //default to no history
		viewPropertiesAction.setEnabled(false); //default to having no properties to view
		closeAction.setEnabled(false); //default to nothing to close
		copyAction.setEnabled(false); //disable all our local actions based on selection state
		insertHighlightAction.setEnabled(false); //disable all our local actions based on selection state
//G***fix		setLayout(new GridBagLayout());	//create a grid bag layout for our book
		setLayout(new BorderLayout());	//create a border layout for our book
//G***del		setLayout(new FlowLayout());	//create a borderlayout for our book
		setDoubleBuffered(false);	//turn off double buffering G***do we want this?
		setOpaque(true);	//show that we aren't transparent
		updateUI();	//update the user interface
		final XMLTextPane xmlTextPane=new XMLTextPane();  //create a new text pane
		xmlTextPane.setAsynchronousLoad(true);	//turn on asynchronous loading TODO fix this better; tidy up throughout the code
//G***del/*G***bring back
		xmlTextPane.setPaged(true); //show that the text pane should page its information
		setXMLTextPane(xmlTextPane);	//store the text pane for use in the future (it will be used by setDisplayPageCount())
		add(getXMLTextPane(), BorderLayout.CENTER);	//add the text pane to the center of our control
		getXMLTextPane().setEditable(false);	//don't let the OEB text pane be edited in this implementation
		setDisplayPageCount(displayPageCount);	//set the number of pages to display
		getXMLTextPane().addHyperlinkListener(  //add a listener for hyperlink events
			new HyperlinkListener()
			{
				public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent)
				{//G***check about the loadingPage flag
					if(hyperlinkEvent.getEventType()==HyperlinkEvent.EventType.ACTIVATED)	//if the cursor is entering the
					{
						activateLink(hyperlinkEvent);	//activate the link
					}
				}
			});
		getXMLTextPane().addCaretListener(this);	//listen for caret events so that we can enable or disable certain actions
		getXMLTextPane().addMouseListener(this);	//G***testing
//G***del*/
	}

	/**Called when the displayed page has changed, so that we can update the page
		indicators
	@param pageEvent The page event.
	*/
	//G***change the page event to pageChange()
	public void pageChanged(PageEvent pageEvent)  //G***change the page event to a bound property
	{
//G***del Debug.trace("page changed event, new page index: {0} count: {1}", new Object[]{pageEvent.getPageIndex(), pageEvent.getPageCount()});  //G***fix
//G***del Debug.traceStack();
		final int pageIndex=pageEvent.getPageIndex(); //get the new page index
		final int pageCount=pageEvent.getPageCount(); //get the page count
		final int displayPageCount=getDisplayPageCount(); //find out how many pages at a time are being displayed
Debug.trace("page index: ", pageIndex);		  //G***del
Debug.trace("page count: ", pageCount);		  //G***del
Debug.trace("display page count: ", displayPageCount);		  //G***del
		final JScrollBar scrollBar=getScrollBar();  //get our scrollbar, if we have one
		if(scrollBar!=null) //if we have a scrollbar
		{
			scrollBar.setMinimum(1); //show that we start with page one G***maybe put this somewhere else
			scrollBar.setMaximum(pageCount);  //show how many pages there are
			scrollBar.setValue(pageIndex+1);  //show which page we're on
/*G***fix
		statusSlider.setMinimum(1); //show that we start with page one G***maybe put this somewhere else
		statusSlider.setMaximum(pageCount);  //show how many pages there are
		statusSlider.setValue(pageIndex+1);  //show which page we're on
*/
			final StringBuffer toolTipTextStringBuffer=new StringBuffer("Page "); //construct a string with the current page number G***i18n
			toolTipTextStringBuffer.append(pageIndex+1); //add the page number to the string
			if(displayPageCount>1) //if there are more than one page being shown
			{
				toolTipTextStringBuffer.append('-'); //a separator
				toolTipTextStringBuffer.append(pageIndex+displayPageCount<=pageCount ? pageIndex+displayPageCount : pageCount); //add the last page number in the range to the string
			}
			scrollBar.setToolTipText(toolTipTextStringBuffer.toString());  //add the tooltip text showing the page number
//G****fix		statusSlider.setToolTipText(toolTipTextStringBuffer.toString());  //add the tooltip text showing the page number
		}
		previousPageAction.setEnabled(pageIndex>0); //we can only go back if the new page index is greater than zero
		nextPageAction.setEnabled(pageIndex+displayPageCount<pageCount); //we can only go forwards if the turning the page would not be over the total number of pages
	}


	/**Called when the book should be adjusted based upon the scrollbar change.
	@param adjustmentEvent The event containing the adjustment information.
	*/
	public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent)
	{
//G***del Debug.trace("Change event: "+changeEvent.toString());		  //G***testing
		if(getPageCount()>0)  //if the book has pages
		{
			final int value=adjustmentEvent.getValue();  //get the new slider value
			final int newPageIndex=value-1; //find out which page index this indicates
//G***fix			if(newPageIndex>=0) //if the new index is a valid page index
//G***del	Debug.trace("Slider change from page: "+book.getPageIndex()+" to "+newPageIndex);		  //G***del
			setPageIndex(newPageIndex); //change to the specified page in the book G***check to see if we should do this in another thread
		}
	}

	/**Called when the caret position is updated.
	@param caretEvent The event holding information about the caret movement.
	*/
	public void caretUpdate(final	CaretEvent caretEvent)
	{
		final boolean isSelection=caretEvent.getMark()!=caretEvent.getDot(); //see if there is a selection
		copyAction.setEnabled(isSelection); //only allow copying if something is selected
		insertHighlightAction.setEnabled(isSelection); //only allow highlighting if something is selected
	}

	/**Called when a mouse click occurs. If the click is a popup trigger, the
		popup-related activities are performed. Otherwise, specific actions take
		place on special objects, such as displaying images.
	@param mouseEvent The mouse event.
	@see #popupTriggered
	@see MouseListener#mouseClicked
	*/
	public void mouseClicked(MouseEvent mouseEvent)
	{
//G***del Debug.trace("Mouse clicked...");
		if(mouseEvent.isPopupTrigger()) //if this is a trigger to popup a context menu
		{
			popupTriggered(mouseEvent); //show that a popup was triggered
		}
		else  //if this is a normal click
		{
		  if(mousePressReleasePopupTrigger) //if a previous press or release triggered a popup
			{
				mousePressReleasePopupTrigger=false; //don't perform any action, but reset the flag for the next click
			}
			else  //if no popup was triggered in any previous press or release
			{
				final JEditorPane editorPane=(JEditorPane)mouseEvent.getSource();	//get the source of the event G***should we really assume the source is the editor pane? probably not
				if(!editorPane.isEditable())	//if the editor pane is read-only
				{
					final Point point=new Point(mouseEvent.getX(), mouseEvent.getY());	//create a point from the mouse click coordinates
					final int pos=editorPane.viewToModel(point);	//get the position of the mouse click

						//get the view closest to the viewer, making sure we have a forward bias or we'll get the wrong view
				  final View view=SwingTextUtilities.getLeafView(editorPane.getUI().getRootView(editorPane), pos, Position.Bias.Forward);
				  final AttributeSet attributeSet=view.getAttributes(); //get the view's attributes
						if(XHTMLSwingTextUtilities.isImage(attributeSet)) //if this is an image
						{
	Debug.trace("Is image element.");
	//G***del						final String src=(String)attributeSet.getAttribute("src");	//G***fix; use a constant
							final String href=XHTMLSwingTextUtilities.getImageHRef(attributeSet); //get a reference to the image file represented by the element
	//G***del Debug.notify("image from: "+src);	//G***fix
							if(href!=null)  //if we found a reference to the image
							{
									//take into account that the href is relative to this file's base URI
								try
								{
									final String baseRelativeHRef=XMLStyleUtilities.getBaseRelativeHRef(attributeSet, href);
		Debug.trace("Image href: ", href);
									viewImage(baseRelativeHRef);  //show the image
								}
								catch(URISyntaxException uriSyntaxException)  //we should never get this error
								{
									Debug.error(uriSyntaxException); //report the error
								}
							}
						}


				  //G***this doesn't work with object fallback images; we really need to get the *view* represented and get the *attribute* set from the view to check for an image

	//G***del Debug.notify("Mouse clicked on position: "+pos);	//G***fix
	/*G***fix
					if(pos>=0)	//if we found a valid position in the document
						activateLink(pos, editorPane, e.getX(), e.getY());	//try activate a link at that position
	*/



/*G***del when we've successfully switched from using elements to using views
					final Document document=editorPane.getDocument();	//get the document in the editor pane
					if(document instanceof XMLDocument)	//if this is an XML document
					{
	//G***del Debug.trace("OEBBook mouse clicked in an OEBDocument");
						XMLDocument xmlDocument=(XMLDocument)document;	//cast the document to an XML document
						Element element=xmlDocument.getCharacterElement(pos);	//get the element this position represents

						element=element!=null ? element.getParentElement() : null; //if the element has a parent, get that parent; this is because, right now, the images have dummy text beneath them G***fix eventually
	Debug.trace("Checking mouse click element.");
	//G***del 					final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
	//G***del					final String elementName=XMLStyleConstants.getXMLElementName(attributeSet); //get the name of this element
	//G***del Debug.trace("mouse clicked on element: "+elementName);  //G***del
	//G***del when works					final String elementName=(String)attributeSet.getAttribute(StyleConstants.NameAttribute);	//get the name of this element
						if(OEBSwingTextUtilities.isImageElement(element)) //if this is an image element
						{
	Debug.trace("Is image element.");
	//G***del						final String src=(String)attributeSet.getAttribute("src");	//G***fix; use a constant
							final String href=OEBSwingTextUtilities.getImageElementHRef(element); //get a reference to the image file represented by the element
	//G***del Debug.notify("image from: "+src);	//G***fix
							if(href!=null)  //if we found a reference to the image
							{
	Debug.trace("Image href: ", href);
								viewImage(href);  //show the image
							}


						}
					}
*/
				}
			}
		}
	}

	/**Invoked when a mouse button has been pressed on a component. Checks to see
		if the event is a popup trigger.
	@param mouseEvent The mouse event.
	@see #popupTriggered
	*/
  public void mousePressed(MouseEvent mouseEvent)
	{
//G***del Debug.trace("mousePressed is trigger: ", new Boolean(mouseEvent.isPopupTrigger()));  //G***del
		if(mouseEvent.isPopupTrigger()) //if this is a trigger to popup a context menu
		{
			mousePressReleasePopupTrigger=true; //show that a mouse press or release triggered a popup
			popupTriggered(mouseEvent); //show that a popup was triggered
		}
	}

	/**Invoked when a mouse button has been released on a component. Checks to see
		if the event is a popup trigger.
	@param mouseEvent The mouse event.
	@see #popupTriggered
	*/
  public void mouseReleased(MouseEvent mouseEvent)
	{
//G***del Debug.trace("mouseReleased is trigger: ", new Boolean(mouseEvent.isPopupTrigger()));  //G***del
		if(mouseEvent.isPopupTrigger()) //if this is a trigger to popup a context menu
		{
			mousePressReleasePopupTrigger=true; //show that a mouse press or release triggered a popup
			popupTriggered(mouseEvent); //show that a popup was triggered
		}
	}

  /**
   * Invoked when the mouse enters a component.
   */
  public void mouseEntered(MouseEvent mouseEvent) {}

  /**
   * Invoked when the mouse exits a component.
   */
  public void mouseExited(MouseEvent mouseEvent) {}

	/**Called when the popup trigger occurred (e.g. the right mouse button on
		the Windows look-and-feel).
	@param mouseEvent The mouse event.
	*/
	public void popupTriggered(final MouseEvent mouseEvent)
	{
		final JPopupMenu popupMenu=new JPopupMenu();  //create a new popup menu
		final Object mouseEventSource=mouseEvent.getSource(); //get the source of the mouse event
		if(mouseEventSource instanceof JEditorPane) //if the mouse was clicked on the editor pane
		{
			final JEditorPane editorPane=(JEditorPane)mouseEventSource;	//get the editor pane
			final Point point=new Point(mouseEvent.getX(), mouseEvent.getY());	//create a point from the mouse click coordinates
			final int pos=editorPane.viewToModel(point);	//get the position of the mouse click
			final Document document=editorPane.getDocument();	//get the document in the editor pane
			if(document instanceof DefaultStyledDocument)	//if this is a default styled document
			{
				DefaultStyledDocument defaultStyledDocument=(DefaultStyledDocument)document;	//cast the document to a default styled document
						  //"View Image"
					//see if we can get an image element at this position
				final Element imageElement=XHTMLSwingTextUtilities.getImageElement(defaultStyledDocument, pos);
				if(imageElement!=null)  //if we found an image element
				{
					final String href=XHTMLSwingTextUtilities.getImageHRef(imageElement.getAttributes()); //get a reference to the image file represented by the element
					if(href!=null)  //if we found a reference to the image
					{
						try
						{
								//G***add something here to extract the name of the image, especially after we can get a URL relative to the file base
								//take into account that the href is relative to this file's base URL
							final String baseRelativeHRef=XMLStyleUtilities.getBaseRelativeHRef(imageElement.getAttributes(), href);
							JMenuItem viewImageMenuItem=popupMenu.add(new ViewImageAction(baseRelativeHRef)); //add an action to view the image
							popupMenu.addSeparator(); //add a separator
						}
						catch(URISyntaxException uriSyntaxException)  //we should never get this error
						{
							Debug.error(uriSyntaxException); //report the error
						}
					}
				}
			}
				//"Delete Bookmark"
		  final Bookmark bookmark=getBookmark(pos); //get the bookmark at this position, if there is one
			if(bookmark!=null)  //if there is a bookmark at this position
			{
				JMenuItem deleteBookmarkMenuItem=popupMenu.add(new DeleteBookmarkAction(bookmark)); //add an action to delete the bookmark
//G***fix or del				deleteBookmarkMenuItem.setMnemonic('i');	//G***i18n
				popupMenu.addSeparator(); //add a separator
			}
				//"Delete Annotation"
		  final Annotation annotation=getAnnotation(pos); //get the annotation at this position, if there is one
			if(annotation!=null)  //if there is an annotation at this position
			{
				JMenuItem deleteAnnotationMenuItem=popupMenu.add(new DeleteAnnotationAction(annotation)); //add an action to delete the annotation
//G***fix or del				deleteBookmarkMenuItem.setMnemonic('i');	//G***i18n
				popupMenu.addSeparator(); //add a separator
			}
				//"Define XXXX"
			try
			{
				final String defineText;  //this will be the text to define
					//see if there is a selection
				final boolean isSelection=getXMLTextPane().getSelectionEnd()>getXMLTextPane().getSelectionStart();  //see if there is a selection
				if(isSelection) //if there is a selection
				{
					final int textOffset=getXMLTextPane().getSelectionStart(); //we'll start with the selection start
					final int textLength=Math.min(getXMLTextPane().getSelectionEnd()-textOffset, 128); //we'll end with the selection end, making sure the selection isn't too large G***use a constant here
					defineText=document.getText(textOffset, textLength).trim(); //get the text they've selected and trim it, in case they accidentally got whitespace along with it
				}
				else  //if there isn't a selection, try to see what word is being clicked on
				{
								//G***check to see if text is highlighted; if so, use the highlighted portion
					final int textOffset=Math.max(pos-64, 0); //find out the start of the text to retrieve; make sure we don't go past the beginning of the document G***use a constant here
					final int textLength=Math.min(document.getLength()-textOffset, 128); //find out how much text to retrieve; make sure we don't go past the end of the document G***use a constant here
					final int relativeOffset=Math.min(pos-textOffset, textLength-1);  //find out where the position will be in the text we retrieve, makeing sure it isn't past the end of the text
/*G***del
Debug.trace("position: ", pos);
Debug.trace("Text offset: ", textOffset);
Debug.trace("Text length: ", textLength);
Debug.trace("Relative offset: ", relativeOffset);
*/
						//G***this ignores different elements, so <h1>Title</h1><p>text</p> give "Titletext"; fix so that only the element text is returned
					final String text=document.getText(textOffset, textLength); //get the text surrounding the position
					final BreakIterator wordBreakIterator=BreakIterator.getWordInstance();  //get a break iterator to find a word based on the appropriate locale G***use the specific locale here
					wordBreakIterator.setText(text);  //set the text of the break iterator
					final int wordEnd=wordBreakIterator.following(relativeOffset); //get the end of the word
					final int wordBegin=wordBreakIterator.previous(); //get the beginning of the word
	//G***do the isLetter thing to make sure this is a word
					defineText=text.substring(wordBegin, wordEnd).trim(); //get the word to define, trimming it in case the selection was whitespace
				}
				if(defineText.length()>0 && defineText.length()<64) //if there is something to define at this location, and it's not too long G***use a constant here
				{
						//G***check the current locale and record that in the action as well
					JMenuItem defineWordMenuItem=popupMenu.add(new DefineAction(defineText)); //add an action to define the word
						//separator
					popupMenu.addSeparator(); //add a separator
				}
			}
			catch(BadLocationException e) //we should never get a bad location, since we test the offsets and lengths
			{
				Debug.error(e);
			}
				//"Insert Bookmark..."
			JMenuItem insertBookmarkMenuItem=popupMenu.add(new InsertBookmarkAction(pos)); //add an action to insert a bookmark at this position
				//"Insert Highlight..."
			JMenuItem insertHighlightMenuItem=popupMenu.add(insertHighlightAction); //add the pre-created action to highlight
				//separator
			popupMenu.addSeparator(); //add a separator
				//"Copy"
			JMenuItem copyMenuItem=popupMenu.add(copyAction); //add the pre-created action to copy text
		}
		if(mouseEventSource instanceof Component) //if the mouse event source was a component
		{
			popupMenu.show((Component)mouseEventSource, mouseEvent.getX(), mouseEvent.getY());  //show the popup menu, using the source component as the invoker
		}
	}

	/**Constructs a new <code>Book</code> defaulting to displaying two pages.
	@see com.garretwilson.swing.OEBTextPane
	*/
	public Book()
	{
		this(2);	//default to showing two pages
	}

	/**Sets the given XML data.
	@param xmlDocument The XML document that contains the data.
	@param baseURI The base URI, corresponding to the XML document.
	@param mediaType The media type of the XML document.
	*/
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final MediaType mediaType)
	{
		setXML(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new MediaType[]{mediaType}, mediaType);	//set the XML using arrays, specifying the media type
	}

	/**Sets the given XML data.
	@param xmlDocumentArray The array of XML documents that contain the data.
	@param baseURIArray The array of base URIs, corresponding to the XML documents.
	@param mediaTypeArray The array of media types of the documents.
	@param mediaType The media type of the book itself.
	*/
	public void setXML(final org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final MediaType[] mediaTypeArray, final MediaType mediaType)
	{
		getXMLTextPane().setContentType(mediaType.toString());	//set the content type of the text pane
		getXMLTextPane().setXML(xmlDocumentArray, baseURIArray, mediaTypeArray);	//tell the XML text pane to set the XML
	}

	/**Reads the book content from a URI.
	@param uri The location of the book.
	@exception IOExeption Thrown if an I/O error occurs.
	@see OEBTextPane#read
	*/
	public void open(final URI uri) throws IOException
	{
		close();  //close whatever book is open

/*G***fix
		final XMLEditorKit.XMLViewFactory xmlViewFactory=(XMLEditorKit.XMLViewFactory)oebEditorKit.getViewFactory();  //get the view factory from the editor kit G***make sure this is an XMLViewFactory
		  //register a QTI view factory with the QTI namespace, with the normal XML view factory as the fallback
//G***if fix, register with the XMLTextPane, not the view factory		xmlViewFactory.registerViewFactory(QTIConstants.QTI_1_1_NAMESPACE_URI, new QTIViewFactory());
*/
		getXMLTextPane().setPage(uri);	//tell the text pane to read from the URI
	}

	/**Closes the book, if one is open.*/
	public void close()
	{
		final File userDataFile=getUserDataFile();  //see if there is a file in which we can store user data
		if(userDataFile!=null)  //if there is a user data file
		{
			try
			{
				final UserData userData=getUserData(); //create a new user data object to represent the data in this book
					//G***check the return value here
			  XMLStorage.store(userData, userDataFile, true); //save the user data to the user data file, making a backup file in the process
/*G***del when works
				BeanUtilities.xmlEncode(userData, userDataFile);  //save the user data to the user data file



				final XMLStorage xmlStorage=new XMLStorage(); //G***testing
				final com.garretwilson.text.xml.XMLDocument xmlDocument=new com.garretwilson.text.xml.XMLDocument();  //G***testing
				xmlStorage.store(userData, xmlDocument);

Debug.trace("saved XML tree:");
com.garretwilson.text.xml.XMLUtilities.printTree(xmlDocument, Debug.getOutput()); //G***del; testing

try
{
	final UserData userData2=(UserData)XMLStorage.retrieve(xmlDocument, UserData.class);

Debug.trace("userdata2: ", userData2);  //G***del
Debug.trace("userdata2 bookmarks: ", userData2.getBookmarks());  //G***del
Debug.trace("userdata2 bookmarks length: ", userData2.getBookmarks().length);  //G***del
Debug.trace("userdata2 bookmarks 0: ", userData2.getBookmarks()[0]);  //G***del

	final com.garretwilson.text.xml.XMLDocument xmlDocument2=new com.garretwilson.text.xml.XMLDocument();  //G***testing
	xmlStorage.store(userData2, xmlDocument2);


	Debug.trace("saved XML tree 2:");
	com.garretwilson.text.xml.XMLUtilities.printTree(xmlDocument2, Debug.getOutput()); //G***del; testing
}
catch(Exception e)
{
	Debug.error(e);
}


		  XMLStorage.store(userData, new File("d:\\temp.xml")); //G***testing


				BeanUtilities.xmlEncode(userData, userDataFile);  //save the user data to the user data file
*/
			}
			catch(IOException e)  //if anything went wrong saving the user data
			{
				Debug.error(e); //G***fix; alert the user, give them the option to abort closing
			}
		}
		historyList.clear();  //clear the history list G***probably put a clearHistory() method instead
		setHistoryIndex(0); //show that we have no history
		clearBookmarks(); //clear the bookmark list
		getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());  //create a default document and assign it to the text pane
	}

	/**Closes and opens the book content from the same location.
		If no file is open, no action is taken.
	@exception IOExeption Thrown if an I/O error occurs.
	@see #getURI
	@see #open
	@see #close
	*/
	public void reload() throws IOException
	{
		final URI uri=getURI(); //get the current URI
		if(uri!=null) //if there is content loaded from some location
		{
			close();  //close the current book
			open(uri);  //open the book from the same location
		}
	}

	/**Reads the book content from a reader.
	@param in The stream to read from.
	@param desc An object describing the stream.
	@exception IOExeption Thrown if an I/O error occurs.
	@see OEBTextPane#read
	*/
/*G***del
	public void read(final Reader in, final Object desc) throws IOException
	{
		getXMLTextPane().read(in, desc);	//let the OEB text pane read the content
	}
*/

	/**Returns a new object reflecting the book's user data. This user data should
		be considered read-only, as its information may reference information in
		the book.
	*/
	protected UserData getUserData()
	{
		return new UserData(this); //create a new user data object to represent the data in this book
	}

	/**Updates the book user data from a <code>UserData</code> object.
	@param userData The object containing the user data.
	*/
	protected void setUserData(final UserData userData)
	{
			//set the bookmarks
		clearBookmarks(); //clear all bookmarks
		final Bookmark[] bookmarks=userData.getBookmarks();  //get the bookmarks in the user data
		for(int i=bookmarks.length-1; i>=0; --i)  //look at each bookmark
		{
			try
			{
				addBookmark(bookmarks[i]);  //add this bookmark, which will automatically attach the bookmark to the document
			}
			catch(BadLocationException e) //if this bookmark represents a bad location
			{
				Debug.warn(e); //ignore the error
			}
		}
			//set the annotations
		clearAnnotations(); //clear all bookmarks
		final Annotation[] annotations=userData.getAnnotations();  //get the annotations in the user data
		for(int i=annotations.length-1; i>=0; --i)  //look at each annotation
		{
			try
			{
				addAnnotation(annotations[i]);  //add this annotation, which will automatically attach the annotation to the document
			}
			catch(BadLocationException e) //if this annotation represents a bad location
			{
				Debug.warn(e); //ignore the error
			}
		}
	}

	/**Called when the document has changed, so that we can update our record of
		the user data file and load it if needed. This method is needed because
		the document is set asynchronously in the AWT thread, not immediately in
		<code>open()</code>.
	@see #open
	*/
	public void documentChange()
	{
		final URI uri=getURI(); //get our current URI
Debug.trace("document change, URI: ", uri);
			//update the actions
		closeAction.setEnabled(uri!=null);  //only enable the close button if there is a book open
	  final RDF rdf=getRDF(); //get the loaded metadata
		getViewPropertiesAction().setEnabled(rdf!=null);  //only enable the properties button if there is RDF
			//update the user data
				//if the URI specifies a file, we can have a user data file
		if(uri!=null && URIConstants.FILE_SCHEME.equals(uri.getScheme()))
		{
Debug.trace("the URI is a file");
		  final File file=new File(uri);  //create a file from the URI
				//create a userdata filename with ".userdata.xml" appended
		  final File userDataFile=new File(file.getParent(), file.getName()+FileConstants.EXTENSION_SEPARATOR+"bookuserdata"+FileConstants.EXTENSION_SEPARATOR+"xml");
Debug.trace("user data file: ", userDataFile);
			getXMLTextPane().getDocument().putProperty(USER_DATA_FILE_PROPERTY, userDataFile); //store the userdata file object in the document
		  if(userDataFile.exists()) //if the user data file exists, try to load it
			{
				try
				{
					final UserData userData=(UserData)XMLStorage.retrieve(userDataFile, UserData.class, true);  //read the user data, using a backup file if the original file doesn't exist
//G***del when works					final UserData userData=(UserData)BeanUtilities.xmlDecode(userDataFile);  //reade the user data
					setUserData(userData);  //set the user data we just loaded
				}
				catch(Exception e)  //if anything went wrong saving the user data
				{
					Debug.error(e); //G***fix; alert the user
				}
			}
		}
	}

	/**Paints the book-specific items such as bookmarks.
	@param graphics The object used for painting.
	*/
	public void paint(Graphics graphics)
	{
		super.paint(graphics);  //do the default painting
	}

	/* ***Page methods*** */

	/**@return The number of pages available.*/
	public int getPageCount()
	{
		return getXMLTextPane().getPageCount();	//return the page count of the OEB text pane
	}

	/**@return The index of the currently displayed page.*/
	public int getPageIndex()
	{
		return getXMLTextPane().getPageIndex();	//return the page index of the OEB text pane
	}

	/**Sets the index of the currently displayed page.
	@param pageIndex The index of the new page to be displayed.
	*/
	public void setPageIndex(final int pageIndex)
	{
		getXMLTextPane().setPageIndex(pageIndex);	//sets the page index of the OEB text pane
	}

	//G***fix this with the correct modelToView() stuff; comment
	public int getPageIndex(final int pos)
	{
		return getXMLTextPane().getPageIndex(pos); //G***comment; decide how we really want to allow access here
	}

	/**@return <code>true</code> if the specified page is one of the pages being
		displayed.
	*/
	public boolean isPageShowing(final int pageIndex)
	{
		return getXMLTextPane().isPageShowing(pageIndex); //ask the paged view whether this page is showing
	}

	/**Advances to the next page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goNextPage()
	{
//G***del System.out.println("OEBBook.goNextPage()");	//G***del
		getXMLTextPane().goNextPage();	//tell the text pane to go to the next page
	}

	/**Changes to the previous page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goPreviousPage()
	{
		getXMLTextPane().goPreviousPage();	//tell the text pane to go to the previous page
	}

	/**Changes to the last position in the history, if previous history is
		available.
	*/
	public void goBack()
	{
		final Position backPosition=decrementHistory(); //get the previous location in history
		if(backPosition!=null)  //if we have a previous position
		  getXMLTextPane().go(backPosition.getOffset());  //go to that offset without storing history information
	}

	/**Changes to the next position in the history, if next history is
		available.
	*/
	public void goForward()
	{
		final Position forwardPosition=incrementHistory(); //get the next location in history
		if(forwardPosition!=null)  //if we have a next position
		  getXMLTextPane().go(forwardPosition.getOffset());  //go to that offset without storing history information
	}

	/**Navigates to specified URL. If the URL is already loaded, it is displayed.
		If the URL is outside the publication, the location is loaded into the
		default browser. The previous location is stored in the history list.
	@param url The destination URL.
	*/
/*G***fix or del	
	public void go(final URL url)	//G***fix all this -- this is just a quick kludge to see if it will work
	{
Debug.trace("Inside OEBBook.goURL()");	//G***del
		storePositionHistory(); //store our position in the history list
Debug.trace("ready to call XMLTextPane.go(URL)");
		getXMLTextPane().go(url);  //tell the text pane to go to the URL
	}
*/

	/**Navigates to specified URI. If the URI is already loaded, it is displayed.
		If the URI is outside the publication, the location is loaded into the
		default browser. The previous location is stored in the history list.
	@param uri The destination URI.
	*/
	public void go(final URI uri)
	{
Debug.trace("Inside OEBBook.goURI()");	//G***del
	  storePositionHistory(); //store our position in the history list
Debug.trace("ready to call XMLTextPane.go(URI)");
		getXMLTextPane().go(uri);  //tell the text pane to go to the URI
	}

	/**Navigates to the specified position. The previous position is stored in
		the history list.
	@param offset The new position to navigate to.
	*/
	public void go(final int offset)
	{
	  storePositionHistory(); //store our position in the history list
		getXMLTextPane().go(offset);  //tell the text pane to go to the specified position
	}

	/**Activates a particular hyperlink from a given hyperlink event.
	@param hyperlinkEvent The event which contains information about the hyperlink
		to be activated.
	*/  //G***add checking for out-of-spine content here
	public void activateLink(final HyperlinkEvent hyperlinkEvent)
	{
Debug.trace("Inside OEBBook.activateLink().");
		final URI hyperlinkURI;	//we'll get the hyperlink event's URI
		if(hyperlinkEvent instanceof XMLLinkEvent)	//if this is an XML link event
		{
			hyperlinkURI=((XMLLinkEvent)hyperlinkEvent).getURI();	//get the XML link event's URI
		}
		else
		{
			try	//TODO check that conversion from URL toURI happens correctly---as URL is ambiguous about encoding, we should probably note any syntax errors that occur when converting to URLs
			{
				hyperlinkURI=new URI(hyperlinkEvent.getURL().toString());	//get the hyperlink event's URI			
			}
			catch(URISyntaxException e)	//if we can't get a URI from the URL
			{
				return;	//don't process this event 
			}
		}
		try
		{
			final XMLDocument xmlDocument=(XMLDocument)getXMLTextPane().getDocument();	//get the loaded document G***should we assume this is an XML document?
			final MediaType mediaType=xmlDocument.getResourceMediaType(hyperlinkURI.toString());	//get the media type of the resource specified by this hyperlink event
Debug.trace("Media type: ", mediaType);	//G***del
			if(mediaType!=null)	//if we think we know the media type of the file involved
			{
					//TODO create convenience utility methods similar to MediaTypeUtilities.isAudio() for all of these checks
				final String topLevelType=mediaType.getTopLevelType();  //get the top-level media type
				if(MediaTypeUtilities.isAudio(mediaType))	//if this is an audio media type
				{
Debug.trace("found an audio file.");
//G***del; fix				  mouseEvent.consume(); //consume the event so that the mouse click won't be interpreted elsewhere
					final Clip clip=(Clip)xmlDocument.getResource(hyperlinkURI.toString());	//get and open a clip to the audio
Debug.trace("ready to start clip.");
					clip.start();	//start the clip playing G***do we need to close it later?
					return;	//don't do any more processing
				}
				else if(topLevelType.equals(MediaType.IMAGE))	//if this is an image media type G***does this work correctly relative to the document base URI?
				{
					viewImage(hyperlinkURI.toString()); //view the image at the given location
					return;	//don't do any more processing
				}
			}
			//G***add an option in goURI() to go later, in the AWT thread
			//G***check to see if we actually went to the hyperlink; if not, try to use the browser
			SwingUtilities.invokeLater(new Runnable()	//invoke the hyperlink traversal until a later time in the event thread, so the mouse click won't be re-interpreted when we arrive at the hyperlink destination
			{
				public void run() {go(hyperlinkURI);}	//if the hyperlink was not for a special-case URI, just go to the URI
			});
		}
		catch(Exception exception)	//if anything goes wrong with any of this G***is this too broad?
		{
			Debug.traceStack(exception);  //G***fix
			Debug.error("Error activating hyperlink "+hyperlinkURI+": "+exception);	//G***fix; this is an important error which should be reported back to the user in a consistent way
		}
	}

	/**Shows an image in a separate image viewing window.
	@param href The absolute or relative reference to the image file. G***fix later when images are relative to documents in other directories
	*/
	protected void viewImage(final String href)
	{
		final Document document=getXMLTextPane().getDocument();	//get the document in the editor pane
		if(document instanceof XMLDocument)	//if this is an XML document
		{
//G***del Debug.trace("OEBBook mouse clicked in an OEBDocument");
			XMLDocument xmlDocument=(XMLDocument)document;	//cast the document to an XML document
		  try
			{
				final Image image=(Image)xmlDocument.getResource(href);	//get the image resource G***check to make sure what is returned is really an image
				viewImage(image, href); //view the image
			}
			catch(URISyntaxException ex)  //G***fix
			{
				Debug.error(ex);
			}
		  catch(IOException ex)  //G***fix
			{
				Debug.error(ex);
			}
		}
	}

	/**Shows an image in a separate image viewing window.
	@param image The image object to display.
	@param title The title to show when viewing the image.
	*/
	public void viewImage(final Image image, final String title)
	{
//G***del; this doesn't seem to make loading faster				ImageUtilities.loadImage(image);  //make sure the image is loaded G***do we need this? this doesn't seem to be causing the blank image problem

		final ImagePanel imagePanel=new ImagePanel(image);  //create an image panel in which to show the image
		JOptionPane.showMessageDialog(this, imagePanel, title, JOptionPane.INFORMATION_MESSAGE);  //show the image panel
/*G***del when works

		final ImageIcon imageIcon=new ImageIcon(image); //G***testing
			//have an option pane create and show a new dialog using our about panel
		new JOptionPane(imageIcon, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(this, title).show();
*/
	}

	/**A highlighter that displays a bookmark icon.*/
	protected static class BookmarkHighlightPainter extends LayeredHighlighter.LayerPainter
	{

		/**The shared bookmark icon for indicating a bookmark.*/
		public final static Icon BOOKMARK_ICON=IconResources.getIcon(IconResources.BOOKMARK_ICON_FILENAME);

		/**This method is never called in a layered highlighter.*/
		public void paint(final Graphics graphics, final int startOffset, final int endOffset, final Shape bounds, final JTextComponent textComponent) {}

		/**Paints the bookmark.
		@param graphics The object for painting the bookmark.
		@param startOffset The starting offset of the part of the highlight that
			crosses the view.
		@param endOffset The ending offset of the part of the highlight that
			crosses the view.
		@param viewBounds The bounds of the view.
		@param textComponent The component painting the highlights.
		@param view The view being rendered.
		*/
		public Shape paintLayer(final Graphics graphics, final int startOffset, final int endOffset, final Shape viewBounds, final JTextComponent textComponent, final View view)
		{
//G***fix; this is abstract; maybe descend from DefaultLayerPainter or whatever it is			super.paintLayer(graphics, startOffset, endOffset, viewBounds, textComponent, view);  //draw the default highlighting first
				//G***save the return value from super.paintLayer() and merge it with what we paint from the icon
			try
			{
				final Shape highlightShape=view.modelToView(startOffset, Position.Bias.Forward, endOffset, Position.Bias.Backward, viewBounds);  //get the display coordinates of this model offset
					//get a rectangle that contains the shape
				final Rectangle highlightRectangle=highlightShape instanceof Rectangle ? (Rectangle)highlightShape : highlightShape.getBounds();
//G***del or fix; this is not immediately updated because it puts it out of the highlight bounds				highlightRectangle.x-=BOOKMARK_ICON.getIconWidth(); //we'll center the icon horizontally at the starting offset
				highlightRectangle.width=BOOKMARK_ICON.getIconWidth(); //update the width of our rectangle to reflect what is painted
				highlightRectangle.height=BOOKMARK_ICON.getIconWidth(); //update the height of our rectangle to reflect what is painted
				highlightRectangle.x-=highlightRectangle.width/2; //center the highlight horizontally
					//paint the bookmark at the correct location
				BOOKMARK_ICON.paintIcon(textComponent, graphics, highlightRectangle.x, highlightRectangle.y);
				return highlightRectangle; //return the area that was actually highlighted by the painting of the icon
			}
			catch(BadLocationException e) //if there was a bad location
			{
				return null;  //show that we did not paint the layer
			}
		}

	}

	/**Contains information regarding the user's use information concerning a
		particular file loaded in the <code>Book</code>. Such user information
		includes bookmarks and notes.
	*/
	public static class UserData
	{

		/**Default constructor.*/
		public UserData() {}

		/**Creates user data from the current contents of a book.
		@param book The book for which user data should be constructed.
		*/
		public UserData(final Book book)
		{
				//create an array to hold the bookmarks, fill the array with the bookmarks, and store the array
			setBookmarks((Bookmark[])book.bookmarkHighlightTagMap.keySet().toArray(new Bookmark[book.bookmarkHighlightTagMap.size()]));
				//create an array to hold the annotations, fill the array with the annotations, and store the array
			setAnnotations((Annotation[])book.annotationHighlightTagMap.keySet().toArray(new Annotation[book.annotationHighlightTagMap.size()]));
		}

		/**The array of bookmarks in the book.*/
		private Bookmark[] bookmarks=new Bookmark[]{};

			/**@return The array of bookmarks in the book.*/
		  public Bookmark[] getBookmarks() {return bookmarks;}

		  /**Sets the array of bookmarks.
		  @param newBookmarks The array of bookmarks in the book.
			*/
			public void setBookmarks(final Bookmark[] newBookmarks) {bookmarks=newBookmarks;}

		/**The array of annotations in the book.*/
		private Annotation[] annotations=new Annotation[]{};

			/**@return The array of annotations in the book.*/
		  public Annotation[] getAnnotations() {return annotations;}

		  /**Sets the array of annotations.
		  @param newAnnotations The array of annotations in the book.
			*/
			public void setAnnotations(final Annotation[] newAnnotations) {annotations=newAnnotations;}

	}

	/**Action for returning to a previously visited location.*/
	class BackAction extends AbstractAction
	{
		/**Default constructor.*/
		public BackAction()
		{
			super("Back");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Back");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Go back to the previous location in the book.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.BACK_VERSION_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
		  goBack();	//go back in history
		  getXMLTextPane().requestFocus(); //put the focus back on the book, in case the focus was transferred G***fix the book's default focus somehow so that we don't have to access deep variables
		}
	}

	/**Action for closing a book.*/
	class CloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public CloseAction()
		{
			super("Close");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Close the open eBook");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Close the currently open eBook.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.BOOK_CLOSED_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			close();	//close the book
			getXMLTextPane().requestFocus(); //put the focus back on the book, in case the focus was transferred G***fix the book's default focus somehow so that we don't have to access deep variables
		}
	}

	/**Action for copying text.*/
	class CopyAction extends AbstractAction
	{
		/**Default constructor.*/
		public CopyAction()
		{
			super("Copy");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Copy selected text.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Copy the selected text to the clipboard.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.COPY_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			getXMLTextPane().copy(); //tell the text pane to copy
		}
	}

	/**Action for defining a word.*/
	class DefineAction extends AbstractAction
	{
		protected String text;

		/**Constructor that indicates the word to be defined.
		@param word The word to be defined
		*/
		public DefineAction(final String word)
		{
			super("Define \""+word+"\"");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "View the definition of \""+word+"\".");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "View the definition of \""+word+"\".");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));  //set the mnemonic key G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.BOOK_QUESTION_ICON_FILENAME)); //load the correct icon
		  text=word; //store the reference to the word
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			try
			{
					//create a string for looking up the text; the BrowserLauncher.openURL() method should automatically URLEncode the text
				final String definitionURLString="http://www.dictionary.com/cgi-bin/dict.pl?term="+text;
				BrowserLauncher.openURL(definitionURLString);	//browse to dictionary.com to lookup the word G***try internal dictionaries first
			}
			catch(IOException ioException)  //if there is an IO exception browsing to the URL
			{
				Debug.error(ioException); //we don't expect to see this exception
			}
		}
	}

	/**Action for deleting an annotation.*/
	class DeleteAnnotationAction extends AbstractAction
	{
		protected Annotation deleteAnnotation;

		/**Constructor.
		@param annotation The annotation to be deleted.
		*/
		public DeleteAnnotationAction(final Annotation annotation)
		{
			super("Delete Annotation");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Delete annoation.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Delete the selected annotation.");	//set the long description G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.NOTEPAD_DELETE_ICON_FILENAME)); //load the correct icon
		  deleteAnnotation=annotation; //store the annotation to be deleted
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
//G***ask for verification
		  removeAnnotation(deleteAnnotation); //remove the annotation
		}
	}

	/**Action for deleting a bookmark.*/
	class DeleteBookmarkAction extends AbstractAction
	{
		protected Bookmark deleteBookmark;

		/**Constructor.
		@param bookmark The bookmark to be deleted.
		*/
		public DeleteBookmarkAction(final Bookmark bookmark)
		{
			super("Delete Bookmark");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Delete bookmark.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Delete the selected bookmark.");	//set the long description G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.BOOKMARK_DELETE_ICON_FILENAME)); //load the correct icon
		  deleteBookmark=bookmark; //store the bookmark to be deleted
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
//G***ask for verification
Debug.trace("Ready to remove bookmark at position: ", deleteBookmark.getOffset());  //G***del
		  removeBookmark(deleteBookmark); //remove the bookmark
		}
	}

	/**Action for inserting a bookmark at the current location.*/
	class InsertBookmarkAction extends AbstractAction
	{
		protected int bookmarkPos;

		/**Constructor that specifies the position of a bookmark.
		@param pos The position in the document at which the bookmark should be added.
		*/
		public InsertBookmarkAction(final int pos)
		{
			super("Insert Bookmark...");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Insert a bookmark.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Insert a bookmark at the current location in the book.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.BOOKMARK_ICON_FILENAME)); //load the correct icon
		  bookmarkPos=pos;  //store the position of the bookmark
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
		  final String defaultBookmarkName="bookmark name";  //we need a default name to work around a JDK 1.3 bug which does not display an input field with no default text G***i18n
				//ask the user for a bookmark name
			final String specifiedName=(String)JOptionPane.showInputDialog(Book.this, "Enter a bookmark name, or leave blank for an unnamed bookmark:", "Insert Bookmark", JOptionPane.QUESTION_MESSAGE, null, null, defaultBookmarkName); //G***i18n
		  if(specifiedName!=null)  //if they didn't cancel
			{
				final String trimmedSpecifiedName=specifiedName.trim(); //trim the bookmark name
				  //if the user didn't specify a name, or they took the default name, don't use a bookmark name
				final String bookmarkName=trimmedSpecifiedName.length()>0 && !trimmedSpecifiedName.equals(defaultBookmarkName)
						? trimmedSpecifiedName : null;
				try
				{
					addBookmark(bookmarkName, bookmarkPos);  //add a bookmark with this name at this offset
				}
				catch(BadLocationException badLocationException) //we should never have a bad location
				{
					Debug.error(badLocationException); //report the error
				}
//G***fix by sending an event that the bookmarks have changed				refreshGoBookmarksMenu(); //update the bookmarks menu G***put this somewhere else, which will catch even the Book's events via a bookmark property change or something
			}
		}
	}

	/**Action for inserting a highlight at the current location.
	Annotations are used to represent highlights.
	*/
	class InsertHighlightAction extends AbstractAction
	{
//G***del		protected int highlightStartPos;

//G***del		protected int highlightEndPos;

//G***del		protected Color highlightColor;

		/**Constructor that specifies the starting and ending position of a highlight.
		@param startPos The starting position in the document at which the highlight
			should be added.
		@param endPos The ending position in the document at which the highlight
			should be added.
		param color The color of the highlight.
		*/
/*G***del
		public InsertHighlightAction(final int startPos, final int endPos, final Color color)
		{
*/
		/**Default constructor for highlighting the current selection using the
		  current highlight color.
		*/
		public InsertHighlightAction()
		{
			super("Insert Highlighted Annotation");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Insert a highlighted annotation.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Insert a highlighted annotation at the current selection in the book.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PAINT_ICON_FILENAME)); //load the correct icon
/*G***del
		  highlightStartPos=startPos;  //store the starting position of the highlight
		  highlightEndPos=endPos;  //store the ending position of the highlight
			highlightColor=color; //store the color of the highlight
*/
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
/*G***fix
		  final String defaultBookmarkName="bookmark name";  //we need a default name to work around a JDK 1.3 bug which does not display an input field with no default text G***i18n
				//ask the user for a bookmark name
			final String specifiedName=(String)JOptionPane.showInputDialog(Book.this, "Enter a bookmark name, or leave blank for an unnamed bookmark:", "Insert Bookmark", JOptionPane.QUESTION_MESSAGE, null, null, defaultBookmarkName); //G***i18n
		  if(specifiedName!=null)  //if they didn't cancel
			{
				final String trimmedSpecifiedName=specifiedName.trim(); //trim the bookmark name
				  //if the user didn't specify a name, or they took the default name, don't use a bookmark name
				final String bookmarkName=trimmedSpecifiedName.length()>0 && !trimmedSpecifiedName.equals(defaultBookmarkName)
						? trimmedSpecifiedName : null;
*/
				try
				{
						//add an annotation with the current selection offsets and current highlight color
					addAnnotation(getXMLTextPane().getSelectionStart(), getXMLTextPane().getSelectionEnd(), getHighlightColor());
				}
				catch(BadLocationException badLocationException) //we should never have a bad location
				{
					Debug.error(badLocationException); //report the error
				}
//G***del			}
		}
	}

	/**Action for going to the previous page.*/
	class PreviousPageAction extends AbstractAction
	{
		/**Default constructor.*/
		public PreviousPageAction()
		{
			super("Previous Page");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Previous Page");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Turn to the previous page in the book.");	//set the long description G***Int
//G***fix			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.HAND_POINT_LEFT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			goPreviousPage();	//tell the book to go to the previous page
		  getXMLTextPane().requestFocus(); //put the focus back on the book, in case the focus was transferred G***fix the book's default focus somehow so that we don't have to access deep variables
		}
	}

	/**Action for going to the next page.*/
	class NextPageAction extends AbstractAction
	{
		/**Default constructor.*/
		public NextPageAction()
		{
			super("Next Page");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Next Page");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Turn to the next page in the book.");	//set the long description G***Int
//G***fix			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.HAND_POINT_RIGHT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			goNextPage();	//tell the book to go to the next page
		  getXMLTextPane().requestFocus(); //put the focus back on the book, in case the focus was transferred G***fix the book's default focus somehow so that we don't have to access deep variables
		}
	}

	/**Action for showing book properties.*/
	class ViewPropertiesAction extends AbstractAction
	{
		/**Default constructor.*/
		public ViewPropertiesAction()
		{
			super("Properties...");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "View properties.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "View the document metadata properties.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));  //set the mnemonic key G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.PROPERTY_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		  final RDF rdf=getRDF(); //get the loaded metadata
			if(rdf!=null) //if we have RDF
			{
			  final OEBPublication oebPublication=getOEBPublication(); //get the loaded publication
					//create a new panel in which to show the RDF
				final RDFPanel rdfPanel=new RDFPanel(new RDFResourceModel(oebPublication, getXMLTextPane().getBaseURI(), getXMLTextPane().getURIInputStreamable()));  
				  //show the properties in an information dialog
				new JOptionPane(rdfPanel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION).createDialog(Book.this, "Properties").show();  //G***i18n
			}
		  getXMLTextPane().requestFocus(); //put the focus back on the text pane, in case the focus was transferred
		}
	}

	/**Action for showing an image.*/
	class ViewImageAction extends AbstractAction
	{
		protected String imageHRef;

		/**Constructor.
		@param href The absolute or relative reference to the image file. G***fix later when images are relative to documents in other directories
		*/
		public ViewImageAction(final String href)
		{
			super("View Image");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "View the image.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "View the image in a separate window.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));  //set the mnemonic key G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.IMAGE_ICON_FILENAME)); //load the correct icon
		  imageHRef=href; //store the reference to the image
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			viewImage(imageHRef); //view the image
		}
	}

}
