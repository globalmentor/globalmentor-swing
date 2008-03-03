package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;  //G***fix for image mouse clicks
import java.beans.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.prefs.Preferences;
import static java.util.Collections.*;
import javax.mail.internet.ContentType;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;  //G***del when image click code is moved elsewhere
import com.garretwilson.model.ResourceModel;
import com.garretwilson.rdf.*;
import com.garretwilson.resources.icon.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.rdf.*;
import com.garretwilson.swing.text.*;
import com.garretwilson.swing.text.Annotation;
import static com.garretwilson.swing.text.TextComponentConstants.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
import com.globalmentor.io.*;
import com.globalmentor.net.*;
import com.globalmentor.text.xml.oeb.*;
import com.globalmentor.util.*;
import com.globalmentor.util.prefs.PreferencesUtilities;

/**A component shows information in book form. Has an XMLTextPane as a child.
<p>Bound properties:</p>
<dl>
	<dt><code>ANTIALIAS_PROPERTY</code> (<code>Boolean</code>)</dt>
	<dd>Indicates the antialias setting has changed.</dd>
	<dt><code>BOOKMARKS_PROPERTY</code> (<code>null</code>)</dt>
	<dd>Indicates the bookmarks have changed.
		Returns <code>null</code> for old and new values.</dd>
	<dt><code>DISPLAY_PAGE_COUNT_PROPERTY</code> (<code>Integer</code>)</dt>
	<dd>Indicates the number of pages being displayed has changed.</dd>
	<dt><code>HISTORY_INDEX_PROPERTY</code> (<code>Integer</code>)</dt>
	<dd>Indicates the history has changed.
		Returns the old and new history index.</dd>
	<dt><code>ZOOM_PROPERTY</code> (<code>Float</code>)</dt>
	<dd>Indicates the zoom level has changed.</dd>
</dl>
@author Garret Wilson
@see javax.swing.JComponent
@see javax.swing.JPanel
@see com.garretwilson.swing.XMLTextPane
*/
public class Book extends ToolStatusPanel implements PageListener, AdjustmentListener, CaretListener, ProgressListener, HyperlinkListener, MouseListener  //G***testing MouseListener for image viewing
{
	/**The property representing the antialias setting.*/
	public final static String ANTIALIAS_PROPERTY="antialias";

	/**The property representing bookmark changes.*/
	public final static String BOOKMARKS_PROPERTY="bookmarks";

	/**The property representing the display page count.*/
	public final static String DISPLAY_PAGE_COUNT_PROPERTY="displayPageCount";

	/**The property representing the history index.*/
	public final static String HISTORY_INDEX_PROPERTY="historyIndex";

	/**The property which stores the user data <code>File</code> object.*/
//G***del	public final static String USER_DATA_FILE_PROPERTY="userData";

	/**The property representing the zoom level.*/
	public final static String ZOOM_PROPERTY="zoom";

	/**The preference for storing the search text.*/
	protected final String SEARCH_TEXT_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "search.text");

	/**The highlight painter used for displaying bookmark locations.*/
	protected final static BookmarkHighlightPainter bookmarkHighlightPainter=new BookmarkHighlightPainter();

	/**Keeps track of whether a mouse press or release was the popup trigger; if
		so, it disables the normal mouse click functionality from occurring.
	*/
	private boolean mousePressReleasePopupTrigger=false;

	/**The text pane used to display information.*/
	private final XMLTextPane xmlTextPane;

		/**@return The text pane used to display information.*/
		public XMLTextPane getXMLTextPane() {return xmlTextPane;}	//G***maybe later change this to protected access

	/**The scrollbar for showing the position within the book.*/
	private final JScrollBar scrollBar;

		/**@return The scrollbar for showing the position within the book, or
		  <code>null</code> if there is no scrollbar.
		*/
		protected JScrollBar getScrollBar() {return scrollBar;}

		/**Sets the scrollbar for showing the position within the book.
		@param newScrollBar The scrollbar to use for showing the book position.
		*/
/*G***del if not needed
		protected void setScrollBar(final JScrollBar newScrollBar)
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
*/

	/**The action for navigating to the previous page.*/
	private final Action previousPageAction;

		/**@return The action for navigating to the previous page.*/
		public Action getPreviousPageAction() {return previousPageAction;}

	/**The action for navigating to the next page.*/
	private final Action nextPageAction;

		/**@return The action for navigating to the next page.*/
		public Action getNextPageAction() {return nextPageAction;}

	/**The action for moving backwards in navigation history.*/
	private final Action backAction;

		/**@return The action for moving backwards in navigation history.*/
		public Action getBackAction() {return backAction;}

	/**The action for closing a book.*/
	private final Action closeAction;

		/**@return The action for closing a book.*/
		public Action getCloseAction() {return closeAction;}

	/**The action for copying text.*/
	private final Action copyAction;

		/**@return The action for copying text.*/
		public Action getCopyAction() {return copyAction;}

	/**The action for inserting a highlight.*/
	private final Action insertHighlightAction;

		/**@return The action for inserting a highlight, which is updated to be
		  enabled or disabled at the appropriate times based upon the selection.
		*/
		public Action getInsertHighlightAction() {return insertHighlightAction;}

	/**The action for searching.*/
	private final Action searchAction;

		/**@return The action for searching.*/
		public Action getSearchAction() {return searchAction;}

	/**The action for searching for the next occurrence of text.*/
	private final Action searchAgainAction;

		/**@return The action for searching for the next occurrence of text.*/
		public Action getSearchAgainAction() {return searchAgainAction;}

	/**The action for displaying the book properties.*/
	private final Action viewPropertiesAction;

		/**@return The action for displaying the book properties.*/
		public Action getViewPropertiesAction() {return viewPropertiesAction;}

	/**The action for displaying one page at a time.*/
	private final AbstractToggleAction displayOnePageAction;

		/**@return The action for displaying one page at a time.*/
		public AbstractToggleAction getDisplayOnePageAction() {return displayOnePageAction;}

	/**The action for displaying two pages at a time.*/
	private final AbstractToggleAction displayTwoPagesAction;

		/**@return The action for displaying two pages at a time.*/
		public AbstractToggleAction getDisplayTwoPagesAction() {return displayTwoPagesAction;}

	/**The action for inserting a bookmark at the current location.*/
	private final Action insertBookmarkAction;

		/**@return The action for inserting a bookmark at the current location.*/
		public Action getInsertBookmarkAction() {return insertBookmarkAction;}

	/**The array of default zoom-level actions.*/
	private final ZoomAction[] zoomActions;

		/**@return The array of default zoom-level actions.*/
		public ZoomAction[] getZoomActions() {return zoomActions;}

	/**The action for turning text smoothing on or off.*/
	private final AntialiasAction antialiasAction;

		/**@return The action for turning text smoothing on or off.*/
		public AntialiasAction getAntialiasAction() {return antialiasAction;}

	/**The current highlight color (defaults to yellow).*/
	private Color highlightColor=Color.yellow;

		/**@return The current highlight color.*/
		public Color getHighlightColor() {return highlightColor;}

		/**Sets the current highlight color.
		@param color The new highlight color.
		*/
		public void setHighlightColor(final Color color) {highlightColor=color;}

	/**Whether data saved in the user data file, such as bookmarks and annotations, have been modified.*/
	private boolean userDataModified=false;

		/**@return Whether data saved in the user data file, such as bookmarks and annotations, have been modified.*/
		public boolean isUserDataModified() {return userDataModified;}

	/**The map of highlight tags for the book, keyed to bookmarks. This map
		therefore serves as both a definitive list of bookmarks and a map of
		highlights which correspond to those bookmarks. This tree map ensures
		that the key set will always be in natural bookmark order, from lowest
		to highest.*/  //G***maybe make this a bound property
	private final Map bookmarkHighlightTagMap;

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
				catch(BadLocationException badLocationException) //we should never have a bad location
				{
					throw (AssertionError)new AssertionError(badLocationException.getMessage()).initCause(badLocationException);
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
				userDataModified=true;	//show that the user data has been modified
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
				userDataModified=true;	//show that the user data has been modified
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
			userDataModified=true;	//show that the user data has been modified
			firePropertyChange(BOOKMARKS_PROPERTY, null, null); //fire an event showing that the bookmarks have changed
		}

		/**@return A read-only iterator of all available bookmarks in natural order.*/
		public Iterator getBookmarkIterator()
		{
		  return unmodifiableSet(bookmarkHighlightTagMap.keySet()).iterator(); //return a read-only iterator to the bookmarks (the keys are already sorted because they are stored in a TreeMap)
		}

		/**Creates an action for navigating to a specific bookmark.
		@param bookmark The bookmark for which an action should be created.
		@return A new action representing the given bookmark.
		*/
		public Action createGoBookmarkAction(final Bookmark bookmark)
		{
			return new GoBookmarkAction(bookmark); //create and return a new action to represent the bookmark
		}

		/**Creates an action for navigating to a specific guide.
		@param guide The guide for which an action should be created.
		@return A new action representing the given guide.
		*/
		public Action createGoGuideAction(final OEBGuide guide)
		{
			return new GoGuideAction(guide); //create and return a new action to represent the guide
		}

	/**The map of highlight tags for the book, keyed to annotations. This map
		therefore serves as both a definitive list of annotations and a map of
		highlights which correspond to those annotations. This tree map ensures
		that the key set will always be in natural annotation order, from lowest
		to highest.*/
	private final Map annotationHighlightTagMap;

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
				userDataModified=true;	//show that the user data has been modified
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
				userDataModified=true;	//show that the user data has been modified
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
			userDataModified=true;	//show that the user data has been modified
		}

		/**@return A read-only iterator of all available annotations in natural order.*/
		public Iterator getAnnotationIterator()
		{
		  return unmodifiableSet(annotationHighlightTagMap.keySet()).iterator(); //return a read-only iterator to the annotations (the keys are already sorted because they are stored in a TreeMap)
		}

	/**@return The URI of the loaded publication or file, or <code>null</code> if
		there is no file loaded.
	*/
	public URI getURI()
	{
//G***del; now close() removes the base URI, making this next line work		return DocumentUtilities.getBaseURI(getXMLTextPane().getDocument());	//get the base URI of the document
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
		if(document instanceof BasicStyledDocument) //if the document is a basic styled document
		  return ((BasicStyledDocument)document).getRDF();  //get the RDF data model from the XML document G***why don't we get the property value directly?
	  return null;  //show that we could not find an RDF data model
	}

	/**@return The publication description associated with the document, if this book's
		text pane has a document with a publication description.
	@return The publication description associated with the book, or <code>null</code> if there is none.
	*/
	public RDFResource getPublication()
	{
		final Document document=getXMLTextPane().getDocument(); //get the document associated with the text pane
		if(document instanceof XMLDocument) //if the document is an XML document
		  return ((XMLDocument)document).getPublication();  //get the OEB publication object from the OEB document G***why don't we get the property value directly?
	  return null;  //show that we could not find a publication
	}

	/**@return The file object representing the user data file associated with
		the loaded publication or file, or <code>null</code> if	there is no user
		data file.
	@see #USER_DATA_FILE_PROPERTY
	*/
/*G***del if not needed
	public File getUserDataFile()
	{
		final Object userDataFile=getXMLTextPane().getDocument().getProperty(USER_DATA_FILE_PROPERTY); //get the user data file from the document
		return userDataFile instanceof File ? (File)userDataFile : null;  //return the file, if that's really what it is; otherwise, return null
	}
*/

	/**@return The file object representing the user data file associated with
		the loaded publication or file, or <code>null</code> if	there is no user
		data file.
	@see #getURI()
	*/
	public File getUserDataFile()
	{
		final URI uri=getURI(); //get our current URI
				//if the URI specifies a file, we can have a user data file
		if(uri!=null && URIs.FILE_SCHEME.equals(uri.getScheme()))
		{
		  final File file=new File(uri);  //create a file from the URI
				//create a userdata filename with ".userdata.xml" appended
		  final File userDataFile=new File(file.getParent(), file.getName()+FileConstants.EXTENSION_SEPARATOR+"bookuserdata"+FileConstants.EXTENSION_SEPARATOR+"xml");
		  return userDataFile;	//return the user data file
		}
		else	//if there is no URI
		{
			return null;	//there is no user data file
		}
	}
			
	/**@return The number of pages to display at a time.
	@see XMLTextPane#getDisplayPageCount
	@see XMLPagedView#getDisplayPageCount
	*/
	public int getDisplayPageCount() {return getXMLTextPane().getDisplayPageCount();}

	/**Sets the number of pages to display at a time.
	This is a bound property.
	@param newDisplayPageCount The new number of pages to display at a time.
	@see XMLTextPane#setDisplayPageCount
	@see XMLPagedView#setDisplayPageCount
	*/
	public void setDisplayPageCount(final int newDisplayPageCount)
	{
		final int oldDisplayPageCount=getXMLTextPane().getDisplayPageCount();	//get the current display page count
		if(oldDisplayPageCount!=newDisplayPageCount)	//if the display page count is really changing
		{
			getXMLTextPane().setDisplayPageCount(newDisplayPageCount);	//tell the text pane the number of pages do be displayed
			firePropertyChange(DISPLAY_PAGE_COUNT_PROPERTY, oldDisplayPageCount, newDisplayPageCount);	//show that the display page count changed
			//TODO it would probably be better if we listened for the display page count changing, although we would still have to initialize with the correct value
		}
		if(newDisplayPageCount==1)	//update the actions in response to the new value
			getDisplayOnePageAction().setSelected(true);
		else if(newDisplayPageCount==2)
			getDisplayTwoPagesAction().setSelected(true);
	}

	/**@return Whether text is antialiased.*/
	public boolean isAntialias() {return getXMLTextPane().isAntialias();}

	/**Sets whether text is antialiased.
	@param newAntialias Whether text should be antialias.
	*/
	public void setAntialias(final boolean newAntialias)
	{
		final boolean oldAntialias=getXMLTextPane().isAntialias();	//get the current value
		if(oldAntialias!=newAntialias)	//if the value is really changing
		{
			getXMLTextPane().setAntialias(newAntialias);	//change the value
			firePropertyChange(ANTIALIAS_PROPERTY, oldAntialias, newAntialias);	//show that the antialias setting has changed
		}
		getAntialiasAction().setSelected(newAntialias);	//show whether or not antialias is now turned on
	}

	/**@return The value by which text should be zoomed, such as 1.00.*/
	public float getZoom() {return getXMLTextPane().getZoom();}

	/**Sets the factor by which text should be zoomed.
	This is a bound property.
	@param newZoom The amount by which normal text should be multiplied.
	*/
	public void setZoom(final float newZoom)
	{
		final float oldZoom=getXMLTextPane().getZoom();	//get the current value
		if(oldZoom!=newZoom)	//if the zoom is really changing
		{
			getXMLTextPane().setZoom(newZoom);	//change the zoom
			firePropertyChange(ZOOM_PROPERTY, oldZoom, newZoom);	//show that the zoom has changed
		}
			//make sure the correct zoom action is selected (the zoom action selection can initially be out of synch even if the zoom level hasn't changed)
		final ZoomAction[] zoomActions=getZoomActions();	//get the zoom actions
		for(int i=zoomActions.length-1; i>=0; --i)	//look at all our zoom actions
		{
			final ZoomAction zoomAction=zoomActions[i];	//look at this zoom action
			if(zoomAction.getZoom()==newZoom)	//if this action represents the new value
			{
				zoomAction.setSelected(true);	//show that this action is now selected
				break;	//we found a matching zoom action; there should only be one, so don't look further
			}
		}
	}

	//history
	//G***probably make something to limit the size of the history list
	//G***don't forget to clear the history list when a new document is loaded
	//G***tie the history to a property

	/**The list of history positions.*/
	private java.util.List historyList;

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

	/**The implementation to use for retrieving an input stream to a URI.*/
	private URIInputStreamable uriInputStreamable;

		/**@return The implementation to use for retrieving an input stream to a URI.*/
		public URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}
		
		/**Sets the implementation to use for retrieving an input stream to a URI.
		@param inputStreamable The implementation to use for accessing a URI for input.
		*/
		public void setURIInputStreamable(final URIInputStreamable inputStreamable) {uriInputStreamable=inputStreamable;}

	/**The implementation to use for retrieving an output stream to a URI.*/
	private URIOutputStreamable uriOutputStreamable;

		/**@return The implementation to use for retrieving an output stream to a URI.*/
		public URIOutputStreamable getURIOutputStreamable() {return uriOutputStreamable;}
		
		/**Sets the implementation to use for retrieving an output stream to a URI.
		@param outputStreamable The implementation to use for accessing a URI for output.
		*/
		public void setURIOutputStreamable(final URIOutputStreamable outputStreamable) {uriOutputStreamable=outputStreamable;}

	/**Default constructor which displays two pages.*/
	public Book()
	{
		this(2);	//default to showing two pages
	}

	/**Constructs a new book with the specified number of pages
		displayed.
	@param displayPageCount The number of pages to display.
	*/
	public Book(final int displayPageCount)
	{
		super(new XMLTextPane(), true, true, false);	//construct the parent class, but don't initialize the book
		uriInputStreamable=DefaultURIAccessible.getDefaultURIAccessible();	//start with a default method of getting input streams
		uriOutputStreamable=DefaultURIAccessible.getDefaultURIAccessible();	//start with a default method of getting output streams
		xmlTextPane=(XMLTextPane)getContentComponent();	//store the text pane for use in the future (it will be used by setDisplayPageCount())
		previousPageAction=new PreviousPageAction();
		nextPageAction=new NextPageAction();
		backAction=new BackAction();
		closeAction=new CloseAction();
		copyAction=new CopyAction();
		insertHighlightAction=new InsertHighlightAction();
		searchAction=new SearchAction();
		searchAgainAction=new SearchAgainAction();
		viewPropertiesAction=new ViewPropertiesAction();
		displayOnePageAction=new DisplayPageCountAction(1);
		displayTwoPagesAction=new DisplayPageCountAction(2);
		insertBookmarkAction=new InsertBookmarkAction();
		zoomActions=new ZoomAction[]
			{
				new ZoomAction(0.25f),
				new ZoomAction(0.50f),
				new ZoomAction(0.60f),
				new ZoomAction(0.70f),
				new ZoomAction(0.80f),
				new ZoomAction(0.90f),
				new ZoomAction(1.00f),
				new ZoomAction(1.10f),
				new ZoomAction(1.20f),
				new ZoomAction(1.30f),
				new ZoomAction(1.40f),
				new ZoomAction(1.50f),
				new ZoomAction(1.75f),
				new ZoomAction(2.00f)
			};
		antialiasAction=new AntialiasAction();
		bookmarkHighlightTagMap=new TreeMap();
		annotationHighlightTagMap=new TreeMap();
		historyList=new ArrayList();
		scrollBar=new JScrollBar(JScrollBar.HORIZONTAL);
		setDisplayPageCount(displayPageCount);	//set the number of pages to display
		setDefaultFocusComponent(xmlTextPane);	//set the default focus component
		initialize();	//initialize the book
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
/*G***transfer to MentoractReaderPanel	
		final Action fileMenuAction=ActionManager.getFileMenuAction();
		actionManager.addMenuAction(fileMenuAction);	//file
		actionManager.addMenuAction(fileMenuAction, sdiManager.getResourceComponentManager().getOpenAction());	//file|open
*/
			//set up the tool actions
		actionManager.addToolAction(getBackAction());	//back
		actionManager.addToolAction(new ActionManager.SeparatorAction());	//-
		actionManager.addToolAction(getPreviousPageAction());	//previous
		actionManager.addToolAction(getNextPageAction());	//next
		actionManager.addToolAction(new ActionManager.SeparatorAction());	//-
		actionManager.addToolAction(getSearchAction());	//search
		actionManager.addToolAction(getSearchAgainAction());	//search again
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
//	G***fix		setDefaultFocusComponent(burrowTreePanel);	//TODO put this in the constructor, maybe
		super.initializeUI();	//do the default initialization
		backAction.setEnabled(false); //default to no history
		viewPropertiesAction.setEnabled(false); //default to having no properties to view
		closeAction.setEnabled(false); //default to nothing to close
		copyAction.setEnabled(false); //disable all our local actions based on selection state
		insertHighlightAction.setEnabled(false); //disable all our local actions based on selection state
		searchAction.setEnabled(false); //default to not allowing searching
		searchAgainAction.setEnabled(false); //default to not allowing searching
		getXMLTextPane().setAsynchronousLoad(true);	//turn on asynchronous loading TODO fix this better; tidy up throughout the code
		getXMLTextPane().setPaged(true); //show that the text pane should page its information
		setAntialias(true);	//default to antialiasing, updating the action
		setZoom(DocumentConstants.DEFAULT_ZOOM);	//set the default zoom level, selecting the appropriate action
		add(getXMLTextPane(), BorderLayout.CENTER);	//add the text pane to the center of our control
		xmlTextPane.setEditable(false);	//don't let the OEB text pane be edited in this implementation
		xmlTextPane.addProgressListener(this);	//listen for progress events
		xmlTextPane.addHyperlinkListener(this);  //listen for hyperlink events
			//catch all document changes in the text pane, since the document is actually changed in a separate thread
		xmlTextPane.addPropertyChangeListener(DOCUMENT_PROPERTY, new PropertyChangeListener()
			{
			  //if the document property changes, call onDocumentChange()
			  public void propertyChange(final PropertyChangeEvent event) {onDocumentChange();}
			});
	  xmlTextPane.addPageListener(this); //add ourselves as a page listener, so that we can update the forward and backwards actions G***we probably want to get the events directly from the book
/*G***del when works
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
*/
		xmlTextPane.addCaretListener(this);	//listen for caret events so that we can enable or disable certain actions
		xmlTextPane.addMouseListener(this);	//G***testing
//G***fix		statusBar.add(statusProgressBar, new GridBagConstraints(1, 0, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));	//G***testing
//G***fix		statusSlider.setPaintLabels(true);  //turn on label painting
//G***fix		statusSlider.setPaintTicks(true); //turn on tick painting


//G***fix		statusSlider.setPaintTrack(true); //turn on track painting
//G***fix		statusSlider.setMajorTickSpacing(2);  //G***testing
//G***fix		statusSlider.setMajorTickSpacing(1);

		getStatusBar().setStatusVisible(true);	//always show the status label
		getStatusBar().setProgressVisible(true);	//always show the progress bar
		getStatusBar().validate();	//make sure the status bar is correctly sized
		getStatusBar().setPreferredSize(getStatusBar().getPreferredSize());	//fix the status bar at its current preferred size so that it won't change the book dimensions when it's updated
		scrollBar.addAdjustmentListener(this); //tell the scrollbar we want to know when it changes
		add(scrollBar, BorderLayout.SOUTH);	//add the scroll bar to the panel
	}

	/**Invoked when progress has been made.
	@param progressEvent The event object representing the progress made.
	*/
	public void madeProgress(final ProgressEvent progressEvent)
	{
		final StatusBar statusBar=getStatusBar();	//get our status bar
//G***del Debug.trace("made progress: ", progressEvent.getTask());  //G**del
		if(progressEvent.isFinished())  //if the progress is finished (whatever the progress is)
		{
//G***del Debug.trace("is finished: ", progressEvent.getTask());  //G**del
		  if(progressEvent.getTask().equals(XMLTextPane.CONSTRUCT_TASK))  //if the document is finished being constructed, we'll treat this as a "finished loading" notification G***probably add some specific document setting event later, or something; actually, that's already there with the document property changing
			{
//TODO fix				refreshGoGuidesMenu();  //G***testing; comment; G***fix for multithreaded loading
			}
		  else if(progressEvent.getTask().equals(XMLTextPane.PAGINATE_TASK))  //if the document is finished being paginated G***probably add some specific document setting event later, or something; actually, that's already there with the document property changing
			{
				firePropertyChange(BOOKMARKS_PROPERTY, null, null); //fire an event showing that the bookmarks have changed, because pagination changes the pages they point to
			}
//G***del when works			setStatus("");	//clear the status
//G***del Debug.trace("setting status progress to zero"); //G***del
		  statusBar.setProgress("", 0);  //set the value of the progress bar to zero
		}
		else  //if the progress is still ongoing
		{
		  final int value=Math.round(progressEvent.getValue());  //get the current value of the progress
			if(value>=0)  //if they've given us a valid value
			{
				final int maximum=Math.round(progressEvent.getMaximum());  //get the maximum value of the progress
				statusBar.setProgressRange(0, maximum);	//set the range
				statusBar.setProgress(progressEvent.getStatus(), value); //show the progress status and progress value on the status bar
			}
			else	//if there is no progress value
			{
				statusBar.setProgress(progressEvent.getStatus()); //show the progress status on the status bar
			}
/*G***maybe fix for threaded pagination
		  if(e.getTask().equals(XMLPagedView.PAGINATE_TASK))  //if we've paginated another page G***testing; maybe only do this if we have threaded pagination
			{
				final int pageIndex=book.getPageIndex(); //get our current page index
				final int pageCount=book.getPageCount(); //get our current page count
				final int displayPageCount=book.getDisplayPageCount(); //find out how many pages at a time are being displayed
				previousPageAction.setEnabled(pageIndex>0); //we can only go back if the new page index is greater than zero
				nextPageAction.setEnabled(pageIndex+displayPageCount<pageCount); //we can only go forwards if the turning the page would not be over the total number of pages
			}
*/
//TODO fix; doesn't work inside the AWT event thread, but it *must* be called from there:	statusBar.paintImmediately(statusBar.getBounds());	//G***testing
		}
//G***bring back, fix		setStatusLater(e.getStatus());	//update the status later
	}

	/**Invoked when a hyperlink action occurs.
	@param hEvent The hyperlink event.
	*/
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent)
	{
		if(hyperlinkEvent.getEventType()==HyperlinkEvent.EventType.ENTERED)	//if the cursor is entering a hyperlink
			getStatusBar().setStatus(hyperlinkEvent.getURL().toString());	//show the URL in the status
		else if(hyperlinkEvent.getEventType()==HyperlinkEvent.EventType.EXITED)	//if the cursor is exiting a hyperlink
			getStatusBar().setStatus("");	//G***testing
				//G***check about the loadingPage flag
		if(hyperlinkEvent.getEventType()==HyperlinkEvent.EventType.ACTIVATED)	//if the cursor is entering the
		{
			activateLink(hyperlinkEvent);	//activate the link
		}
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
			final XMLTextPane textPane=getXMLTextPane();	//get a reference to our text pane
/*G***del
			final XMLTextPane textPane=getXMLTextPane();	//get a reference to our text pane
			final int absolutePageIndex=textPane.getAbsolutePageIndex(pageIndex);
*/
			//find out how many partially-filled sets of pages there are by finding the absolute page index of the last available page
			final int setCount=(textPane.getAbsolutePageIndex(pageCount-1)/displayPageCount)+1;
Debug.trace("set count", setCount);
			final int maximum=setCount*displayPageCount;	//the maximum page index is the base of the last set---one set less than all available (we already ignored the first set)
Debug.trace("maximum", maximum);
			final int newValue=pageIndex==0 ? pageIndex : textPane.getAbsolutePageIndex(pageIndex);	//other than the first page, the other pages will be offset by the extra space in the first set
Debug.trace("new value", newValue);
			scrollBar.setValues(newValue, displayPageCount, 0, maximum);	//update the scrollbar, pretending all the page sets are full of pages
/*G***del when works
			scrollBar.setMinimum(0); //show that we start with page one G***maybe put this somewhere else
			scrollBar.setExtent(0); //show that we start with page one G***maybe put this somewhere else
			scrollBar.setMaximum(pageCount-1);  //show how many pages there are
			scrollBar.setValue(pageIndex);  //show which page we're on
*/
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
		if(getPageCount()>0)  //if the book has pages
		{
			final int displayPageCount=getDisplayPageCount(); //find out how many pages at a time are being displayed
			final int newValue=adjustmentEvent.getValue();  //get the new slider value
Debug.trace("new adjustment value", newValue);
				//besides the first page, the other pages will be offset by the number of empty pages in the first set
			final int newPageIndex=newValue==0 ? newValue : getXMLTextPane().getLogicalPageIndex(newValue);
Debug.trace("new page index", newPageIndex);
			setPageIndex(newPageIndex); //change to the specified page in the book
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

	/**Sets the given XML data.
	@param xmlDocument The XML document that contains the data.
	@param baseURI The base URI, corresponding to the XML document.
	@param mediaType The media type of the XML document.
	*/
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType)
	{
//TODO del if not needed		setXML(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new ContentType[]{mediaType}, mediaType);	//set the XML using arrays, specifying the media type
		close();  //close whatever book is open
		getXMLTextPane().setContentType(mediaType.toString());	//set the content type of the text pane
		getXMLTextPane().setXML(xmlDocument, baseURI, mediaType);	//tell the XML text pane to set the XML
	}

	/**Sets the given XML data.
	@param xmlDocumentArray The array of XML documents that contain the data.
	@param baseURIArray The array of base URIs, corresponding to the XML documents.
	@param mediaTypeArray The array of media types of the documents.
	@param mediaType The media type of the book itself.
	*/
/*TODO del if not needed
	public void setXML(final org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final ContentType[] mediaTypeArray, final ContentType mediaType)
	{
		close();  //close whatever book is open
		getXMLTextPane().setContentType(mediaType.toString());	//set the content type of the text pane
		getXMLTextPane().setXML(xmlDocumentArray, baseURIArray, mediaTypeArray);	//tell the XML text pane to set the XML
	}
*/	

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
		getXMLTextPane().setPage(uri, getURIInputStreamable());	//tell the text pane to read from the URI
	}

	/**Closes the book, if one is open.*/
	public void close()
	{
		historyList.clear();  //clear the history list G***probably put a clearHistory() method instead
		setHistoryIndex(0); //show that we have no history
		clearBookmarks(); //clear the bookmark list
		getXMLTextPane().setBaseURI(null);	//show that nothing is open (do this so that the new blank document will not have its base URI set automatically) 
		getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());  //create a default document and assign it to the text pane
		userDataModified=false;	//show that the user data has not been modified
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
	public UserData getUserData()
	{
		return new UserData(this); //create a new user data object to represent the data in this book
	}

	/**Updates the book user data from a <code>UserData</code> object.
	@param userData The object containing the user data.
	*/
	public void setUserData(final UserData userData)
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
		userDataModified=false;	//show that the user data not has been modified
	}

	/**Called when the document has changed, so that we can update our record of
		the user data file and load it if needed. This method is needed because
		the document is set asynchronously in the AWT thread, not immediately in
		<code>open()</code>.
	@see #open
	*/
	protected void onDocumentChange()
	{
		final URI uri=getURI(); //get our current URI
Debug.trace("document change, URI: ", uri);
			//update the actions
		closeAction.setEnabled(uri!=null);  //only enable the close button if there is a book open
		searchAction.setEnabled(uri!=null);  //only enable searching if there is a file open
		searchAgainAction.setEnabled(uri!=null);  //only enable searching if there is a file open
//TODO transfer this to MentoractReaderPanel		reloadAction.setEnabled(book.getURI()!=null);  //only enable the reload button if there is a file open
	  final RDF rdf=getRDF(); //get the loaded metadata
		getViewPropertiesAction().setEnabled(rdf!=null);  //only enable the properties button if there is RDF TODO do we want to show the RDF or the publication description?
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

	/**Attempts to navigate to the target of the given OEB guide.
	@param guide The OEB guide which contains the target href location.
	*/
	protected void go(final OEBGuide guide)
	{
		final Document document=getXMLTextPane().getDocument(); //get a reference to the document
	  if(document instanceof XMLDocument) //if this is an XML document
		{
	  	try
	  	{
				final URI guideURI=((XMLDocument)document).getResourceURI(guide.getHRef()); //try to construct a URI from the guide's href
				go(guideURI); //go to the URL specified by the guide
	  	}
	  	catch(final IllegalArgumentException illegalArgumentException)
	  	{
	  		SwingApplication.displayApplicationError(this, illegalArgumentException);
	  	}
		}
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
			final ContentType mediaType=xmlDocument.getResourceMediaType(hyperlinkURI.toString());	//get the media type of the resource specified by this hyperlink event
Debug.trace("Media type: ", mediaType);	//G***del
			if(mediaType!=null)	//if we think we know the media type of the file involved
			{
					//TODO create convenience utility methods similar to MediaTypeUtilities.isAudio() for all of these checks
				final String topLevelType=mediaType.getPrimaryType();  //get the top-level media type
				if(ContentTypes.isAudio(mediaType))	//if this is an audio media type
				{
Debug.trace("found an audio file.");
//G***del; fix				  mouseEvent.consume(); //consume the event so that the mouse click won't be interpreted elsewhere
					final Clip clip=(Clip)xmlDocument.getResource(hyperlinkURI.toString());	//get and open a clip to the audio
Debug.trace("ready to start clip.");
					clip.start();	//start the clip playing G***do we need to close it later?
					return;	//don't do any more processing
				}
				else if(topLevelType.equals(ContentTypes.IMAGE_PRIMARY_TYPE))	//if this is an image media type G***does this work correctly relative to the document base URI?
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

	/**The text to search for, saved between searches.*/
	protected String searchText="";

	/**Ask the user for a search string and searches for text starting on the
	current page.
	*/
	public void search()
	{
		search(getXMLTextPane().getPageStartOffset(getPageIndex()));	//we'll start searching at the beginning of whichever page is showing
	}

	/**Ask the user for a search string and searches for text at the given offset.
	@param searchOffset The offset at which searching should begin, or
		<code>XMLTextPane.NEXT_SEARCH_OFFSET</code> if searching should take place
		where the last search left off.
	@see XMLTextPane#NEXT_SEARCH_OFFSET
	*/
	public void search(final int searchOffset)
	{
		String defaultSearchText=searchText;	//get the current search text
		if(defaultSearchText==null || defaultSearchText.length()==0)	//if there is no default search text, try to find default search text from the preferences
		{
			try
			{
				final Preferences preferences=getPreferences();	//get the preferences
				defaultSearchText=preferences.get(SEARCH_TEXT_PREFERENCE, "");	//get the stored search text
			}
			catch(SecurityException securityException)	//if we can't access preferences
			{
				Debug.warn(securityException);	//warn of the security problem			
			}
		}
		final String newSearchText=(String)JOptionPane.showInputDialog(this, "Enter search word or phrase:", "Search", JOptionPane.QUESTION_MESSAGE, null, null, defaultSearchText);	//G***i18n
		if(newSearchText!=null && newSearchText.length()>0) //if they want to search
		{
			searchText=newSearchText;	//save the search text for other searches
			try
			{
				final Preferences preferences=getPreferences();	//get the preferences
				preferences.put(SEARCH_TEXT_PREFERENCE, searchText);	//store the search text for next time
			}
			catch(SecurityException securityException)	//if we can't access preferences
			{
				Debug.warn(securityException);	//warn of the security problem			
			}
			search(searchText, searchOffset); //search for the text
		}
	}

	/**Searches for the current search text at the last search offset.
	If there is no search text, the user is asked for search text.
	@see #search()
	@see #search(String, int)
	*/
	public void searchAgain()
	{
		if(searchText!=null && searchText.length()>0)  //if there is search text
		{
			search(searchText, XMLTextPane.NEXT_SEARCH_OFFSET); //search for the text at the next search position
		}
		else	//if there is no search text
		{
			search();	//start searching from scratch
		}		
	}

	/**Searches for text at the given offset.
	@param searchText The text for which to search.
	@param searchOffset The offset at which searching should begin, or
		<code>XMLTextPane.NEXT_SEARCH_OFFSET</code> if searching should take place
		where the last search left off.
	@see XMLTextPane#NEXT_SEARCH_OFFSET
	*/
	public void search(final String searchText, final int searchOffset)
	{
		final int matchOffset;  //G***testing; comment; don't access deeply
		getStatusBar().setStatus("Searching...");	//G***testing; i18n G***doesn't work, because this is in a separate AWT thread
		try
		{
			matchOffset=getXMLTextPane().search(searchText, searchOffset);  //ask the XML text pane to search for text
		}
		finally
		{
			getStatusBar().setStatus("");	//G***testing; comment
		}
		if(matchOffset<0) //if the text was not found
		{
			SwingApplication.displayApplicationError(this, "Search Results", "The requested text was not found."); //show that the text was not found
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
		public UserData(final Book book)	//TODO create defensive copies of the data 
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
		}
	}

	/**Action for closing a book.*/
	protected class CloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public CloseAction()
		{
			super("Close");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Close the open eBook");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Close the currently open eBook.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.BOOK_CLOSED_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK)); //add the accelerator
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_CLOSE_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			close();	//close the book
		}
	}

	/**Action for copying text.*/
	protected class CopyAction extends AbstractAction
	{
		/**Default constructor.*/
		public CopyAction()
		{
			super("Copy");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Copy selected text.");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Copy the selected text to the clipboard.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
		  putValue(SMALL_ICON, IconResources.getIcon(IconResources.COPY_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK)); //add the accelerator
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
/*TODO bring back after BrowserLauncher is linked externally
			{
					//create a string for looking up the text; the BrowserLauncher.openURL() method should automatically URLEncode the text
				final String definitionURLString="http://www.dictionary.com/cgi-bin/dict.pl?term="+text;
				BrowserLauncher.openURL(definitionURLString);	//browse to dictionary.com to lookup the word G***try internal dictionaries first
			}
			catch(IOException ioException)  //if there is an IO exception browsing to the URL
			{
				Debug.error(ioException); //we don't expect to see this exception
			}
*/
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

	/**Action for inserting a bookmark at the current or given location.*/
	protected class InsertBookmarkAction extends AbstractAction
	{
		/**The position of the bookmark, or -1 if the current position should be used.*/
		protected final int bookmarkPos;

		/**Default constructor that always uses the current position when performed.*/
		public InsertBookmarkAction()
		{
			this(-1);	//show that we should always use the current position
		}

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
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
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
				if(bookmarkPos>=0)	//if we know where to add the bookmark
				{
					try
					{
						addBookmark(bookmarkName, bookmarkPos);  //add a bookmark with this name at this offset
					}
					catch(BadLocationException badLocationException) //we should never have a bad location
					{
						throw new AssertionError(badLocationException);
					}
				}
				else	//if we don't have a particular location
				{
					addBookmark(bookmarkName); //insert a bookmark at the current location in the book
				}
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
			  final RDFResource publication=getPublication(); //get the loaded publication
					//create a new panel in which to show the RDF
				final RDFPanel<RDFResource, ResourceModel<RDFResource>> rdfPanel=new RDFPanel<RDFResource, ResourceModel<RDFResource>>(new ResourceModel<RDFResource>(publication, getXMLTextPane().getBaseURI(), getXMLTextPane().getURIInputStreamable()));  
				  //show the properties in an information dialog
				BasicOptionPane.showMessageDialog(Book.this, rdfPanel, "Properties", BasicOptionPane.INFORMATION_MESSAGE);	//G***i18n
			}
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

	/**Action for searching for text.*/
	protected class SearchAction extends AbstractAction
	{

		/**Default constructor.*/
		public SearchAction()
		{
			super("Find...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Find text");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Search for text within the book.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SEARCH_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			search();	//start the search
		}
	}

	/**Action for searching again for text.*/
	protected class SearchAgainAction extends AbstractAction
	{

		/**Default constructor.*/
		public SearchAgainAction()
		{
			super("Find Again");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Find text again");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Search for text that occurs after the last search.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SEARCH_AGAIN_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)); //add the accelerator
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			searchAgain();	//continue the search
		}
	}

	/**The group indicating exclusive display page count actions.*/
	private final ActionGroup displayPageCountGroup=new ActionGroup(){};

	/**Action for displaying a particular number of pages at a time.*/
	protected class DisplayPageCountAction extends AbstractToggleAction	//TODO create a factory method to make sure a singleton is returned for each page count  
	{

		/**The number of pages to display.*/
		protected final int displayPageCount;

		/**Constructs an action which changes the number of pages displayed.
		@param displayPageCount How many pages should be displayed when this action is performed.
		*/
		public DisplayPageCountAction(final int displayPageCount)
		{
			super(displayPageCountGroup);	//construct the parent class
			this.displayPageCount=displayPageCount;	//save the display page count
			switch(displayPageCount)	//see how many pages we should display
			{
				case 1:	//if we should show a single page
					putValue(NAME, "1 Page");	//set the correct name G***i18n
					putValue(SHORT_DESCRIPTION, "Single Page");	//set the short description G***Int
					putValue(LONG_DESCRIPTION, "Display only one page at a time.");	//set the long description G***Int
				  putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_1));  //set the mnemonic key G***i18n
					putValue(SMALL_ICON, IconResources.getIcon(IconResources.COLUMNS1_ICON_FILENAME)); //load the correct icon
					break;
				case 2:	//if we should show two pages
				default:	//for any other number of pages
					putValue(NAME, "2 Pages");	//set the correct name G***i18n
					putValue(SHORT_DESCRIPTION, "Facing Pages");	//set the short description G***Int
					putValue(LONG_DESCRIPTION, "Display two pages at a time.");	//set the long description G***Int
					putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_2));  //set the mnemonic key G***i18n
					putValue(SMALL_ICON, IconResources.getIcon(IconResources.COLUMNS2_ICON_FILENAME)); //load the correct icon
					break;
			}
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			setDisplayPageCount(displayPageCount);	//show the correct number of pages at a time
		}
	}

	/**The group indicating exclusive zoom actions.*/
	private final ActionGroup zoomGroup=new ActionGroup(){};

	/**Action for changing the zoom level.*/
	protected class ZoomAction extends AbstractToggleAction
	{
		/**The amount by which to zoom.*/
		protected final float zoom;

			/**@return The amount by which to zoom.*/
			public float getZoom() {return zoom;}

		/**Constructs an action which changes the zoom.
		@param zoom The amount by which normal size will be multiplied.
		*/
		public ZoomAction(final float zoom)
		{
			super(zoomGroup);	//construct the parent class
			this.zoom=zoom; //save the zoom factor
			final String percentString=NumberFormat.getPercentInstance().format(zoom); //create a percentage string from the zoom factor G***i18n use selected locale
			putValue(NAME, percentString);	//set the correct name
			putValue(SHORT_DESCRIPTION, "Zoom "+percentString);	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Display everything "+percentString+" of its original size.");	//set the long description G***i18n
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			setZoom(zoom);	//change the book's zoom factor
		}
	}

	/**Action for turning on text antialiasing.*/
	protected class AntialiasAction extends AbstractToggleAction
	{
		/**Default constructor.*/
		public AntialiasAction()
		{
			super("Smooth Text");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Turn on font smoothing.");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Turn on antialias-based font smoothing.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));  //set the mnemonic key G***i18n
//G***fix			putValue(SMALL_ICON, new ImageIcon(ReaderFrame.class.getResource("info.gif")));	//load the correct icon G***use a constant here
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		  setAntialias(!isAntialias()); //turn antialiasing on or off -- the opposite of what it is now
		}
	}

	/**Action for going to an OEB guide.*/
	protected class GoGuideAction extends AbstractAction
	{
		/**The action guide.*/
		protected final OEBGuide oebGuide;

		/**Constructs an action which represents a guide target.
		@param guide The OEB guide which contains the target information.
		*/
		public GoGuideAction(final OEBGuide guide)
		{
			oebGuide=guide;	//save the guide
			putValue(NAME, guide.getTitle());	//set the correct name
			putValue(SHORT_DESCRIPTION, guide.getTitle());	//set the short description
		  putValue(LONG_DESCRIPTION, guide.getHRef());	//set the long description to equal the actual reference
//G***fix			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));  //set the mnemonic key G***i18n
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		  go(oebGuide);  //go to the guide's target reference
		}
	}

	/**Action for going to a bookmark.*/
	protected class GoBookmarkAction extends AbstractAction
	{
		/**The action bookmark.*/
		protected final Bookmark bookmark;

		/**Constructs an action which represents a bookmark.
		@param bookmark The bookmark which contains the target information.
		*/
		public GoBookmarkAction(final Bookmark bookmark)
		{
Debug.trace("ReaderFrame.GoBookmarkAction constructor, offset:", bookmark.getOffset());
			this.bookmark=bookmark;	//save the bookmark
//G***del when works			setBookmark(bookmark);	//save the bookmark
		  final int pageIndex=getPageIndex(bookmark.getOffset()); //get the page index of the bookmark
			final Document document=getXMLTextPane().getDocument(); //get a reference to the document
Debug.trace("document length", document.getLength());
			final int bookmarkedTextLength=Math.min(document.getLength()-bookmark.getOffset(), 16); //find out how much text to show; make sure we don't go past the document G***use a constant here
			final String bookmarkNameString=bookmark.getName()!=null ? bookmark.getName()+": " : "";  //if there is a bookmark name, include it
			try
			{
				final String bookmarkedText=document.getText(bookmark.getOffset(), bookmarkedTextLength); //get the bookmarked text G***use a constant
				final String bookmarkString=bookmarkNameString+"Page "+(pageIndex+1)+" ("+bookmarkedText+"...)";  //G**i18n; use a getPageNumber(pageIndex) method; comment
				putValue(NAME, bookmarkString);	//set the correct name G***fix
				putValue(SHORT_DESCRIPTION, bookmarkString);	//set the short description G***fix
				putValue(LONG_DESCRIPTION, bookmarkString);	//set the long description to equal the actual reference G***fix
			}
			catch(BadLocationException badLocationException) //we should never get a bad location, since we test the offsets and lengths
			{
				throw new AssertionError(badLocationException);
			}
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		  go(bookmark.getOffset());  //go to the bookmark's offset
		}

	}
	
}
