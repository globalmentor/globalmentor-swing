package com.garretwilson.swing;

/*G***bring back as needed
import java.awt.*;
import java.awt.event.ActionEvent;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
*/
import java.applet.*;
import java.awt.Graphics;
import java.awt.*;  //G***del if not needed
import java.awt.font.*; //G***del if not needed
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent; //G***testing
import java.awt.event.MouseMotionListener; //G***testing
import java.awt.geom.AffineTransform; //G***del if not needed
/*G***del when works
import java.awt.event.KeyListener;
*/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.EventListenerList;
import com.garretwilson.applet.*;
import com.garretwilson.awt.EventQueueUtilities;
import com.garretwilson.io.*;
import com.garretwilson.net.URIConstants;
import com.garretwilson.net.URIUtilities;
import com.garretwilson.net.URLUtilities;
import com.garretwilson.swing.event.PageEvent;
import com.garretwilson.swing.event.PageListener;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.oeb.OEBEditorKit;  //G***move elsewhere if we can
import com.garretwilson.swing.text.xml.xhtml.XHTMLEditorKit;
import com.garretwilson.swing.text.xml.xhtml.XHTMLLinkController;
import com.garretwilson.swing.text.xml.xhtml.XHTMLViewFactory;
import com.garretwilson.text.xml.XMLReader;
import com.garretwilson.text.xml.oeb.OEBConstants;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.text.xml.xhtml.XHTMLUtilities;
import com.garretwilson.util.Debug;
import com.garretwilson.util.zip.*;
import edu.stanford.ejalbert.BrowserLauncher;

/**A text component that can be marked up with attributes that are represented
	graphically. Most importantly, this class supports paged XML information.
	<p>This component defines the left and right arrow keys and the PageUp/PageDown
	keys for page navigation.</p>
	<p>This class also serves as a context that can keep track of applets and
	load their audio clips and images.</p>
@author Garret Wilson
@see javax.swing.JTextPane
@see com.garretwilson.swing.event.PageEvent
@see com.garretwilson.swing.event.PageListener
*/
public class XMLTextPane extends JTextPane implements AppletContext, /*G***del when works KeyListener, */MouseMotionListener, PageListener, ProgressListener
{

	/**The name of the property that indicates the current document.*/
	public final static String DOCUMENT_PROPERTY="document";

	/**The name of the property that indicates the current editor kit.*/
	public final static String EDITOR_KIT_PROPERTY="editorKit";

	/**The highlighter used for highlighting search results.*/
//G***del when works	protected final Highlighter searchHighlighter=new DefaultHighlighter();

	//TODO fix asynchronous stop-gap kludge to correctly get the asynchronous setting from the document---if that's the best way to do it
	protected boolean asynchronousLoad=false;
	

		public boolean isAsynchronousLoad() {return asynchronousLoad;}

		public void setAsynchronousLoad(final boolean newAsynchronous) {asynchronousLoad=newAsynchronous;}

	/**The highlight painter used for highlighting search results.*/
	protected final static Highlighter.HighlightPainter searchHighlightPainter=new DefaultHighlighter.DefaultHighlightPainter(Color.blue);

	/**The task of constructing a document.*/
	public final static String CONSTRUCT_TASK="construct";

	/**The task of paginating a document, if applicable.
		Identical to <code>XMLPagedView.PAGINATE_TASK</code>.
	@see XMLPagedView#PAGINATE_TASK
	*/
	public final static String PAGINATE_TASK=XMLPagedView.PAGINATE_TASK;

	/**A constant representing that the next search offset should be used, rather
		than an absolute search offset.
	*/
	public final static int NEXT_SEARCH_OFFSET=-1;

//G***del if not needed	/**The property representing the search position.*/
//G***del if not needed	public final static String SEARCH_POSITION_PROPERTY="searchPositionProperty";

	/**The list of page event listeners.*/
	private EventListenerList pageListenerList=new EventListenerList();

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**The left key stroke, predefined for quick comparison.*/
//G***del when keymap works	protected final static KeyStroke LEFT_KEY_STROKE=KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);

	/**The right key stroke, predefined for quick comparison.*/
//G***del when keymap works
//G***del	protected final static KeyStroke RIGHT_KEY_STROKE=KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);

	/**The access to input streams via URIs, if one exists.*/
	private URIInputStreamable uriInputStreamable=null;

		/**@return The access to input streams via URIs, or <code>null</code> if
		  none exists.
		*/
		public URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}

		/**Sets the object for accessing input streams.
		@param newURIInputStreamable The object that allows acces to input streams
			via URIs.
		*/
		public void setURIInputStreamable(final URIInputStreamable newURIInputStreamable) {uriInputStreamable=newURIInputStreamable;}

	/**The current position of the mouse.*/
	private Point mousePosition=new Point(0, 0);

		/**@return The current position of the mouse at its last movement.*/
		protected Point getMousePosition() {return mousePosition;}

		/**Keeps a record of the current position of the mouse at its last movement.
		@param pos The position of the mouse to record
		*/
		protected void setMousePosition(final Point pos) {mousePosition=pos;}

	/**Whether this pane pages its information.*/
	private boolean paged=false;

		/**@return Whether this pane pages its information.*/
		public boolean isPaged() {return paged;}

		/**Sets whether this pane pages its information.
		@param newPaged Whether this pane should show pages.
		*/
		public void setPaged(final boolean newPaged)
		{
		  if(newPaged!=paged) //if value is really changing
			{
			  paged=newPaged; //update whether we're paged
				DocumentUtilities.setPaged(getDocument(), paged);  //store the new paged value in the document
/*G***del when works

						//G***fix the way this sets the paged variable in the XMLEditorKit view factory, because it somehow affects other XMLTextPanes
				final ViewFactory viewFactory=getEditorKit().getViewFactory();  //get our current view factory
				if(viewFactory instanceof XMLEditorKit.XMLViewFactory)  //if our new view factory is an XML view factory
				{
					((XMLEditorKit.XMLViewFactory)viewFactory).setPaged(isPaged()); //tell the editor kit whether it should be paged
				}
*/
/*G***del when works
				removeKeymap(PAGED_KEYMAP_NAME);	//make sure the paged key map is not in effect
				if(newPaged)  //if we should be paged, now
				{
					final Keymap pagedKeymap=addKeymap("Paged Keymap", getKeymap()); //create a new keymap for paging G***use a constant, maybe
					loadKeymap(pagedKeymap, PAGED_KEY_BINDINGS, getActions()); //load our custom keymap G***what happens if this is set and then the editor kit is changed?
				}
*/

/*G***del when works
					final Keymap keyMap=addKeymap();	//we'll determine the new keymap based upon whether we're now paged
				if(newPaged)  //if we should be paged, now
				{
						//G***maybe setup the keymap elsewhere
				  keyMap=addKeymap("Paged Keymap", originalKeymap); //create a new keymap for paging G***use a constant, maybe
						//G***rename DEFAULT_KEY_BINDINGS, perhaps to PAGED_KEY_BINDINGS, after we see what other key bindings are needed
		  		loadKeymap(pagedKeymap, DEFAULT_KEY_BINDINGS, getActions()); //load our custom keymap G***how do our actions get here from the editor kit?
					setKeymap(pagedKeymap); //switch to our special keymap
				}
				else  //if we're not paged
				{
					setKeymap(originalKeymap); //switch back to our original keymap
				}
*/
				updateKeymap();	//update the keymap to reflect our new paged condition
			}
		}

	/**The name of the key map for normal XML key functions.*/
	protected final static String XML_KEYMAP_NAME="xmlKeymap";
	/**The name of the key map for paged key functions.*/
	protected final static String PAGED_KEYMAP_NAME="pagedKeymap";

//G***del when works	protected final Keymap originalKeymap;  //the keymap originally installed; we'll install keymaps under this

	/**XML text pane default key bindings.*/
	protected static final KeyBinding[] DEFAULT_KEY_BINDINGS=
		{
			//bind (ctrl+shift+'e') to the XML editor kit's show element tree action
			new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK), XMLEditorKit.DISPLAY_ELEMENT_TREE_ACTION_NAME)
		};

	/**XML text pane paged key bindings.*/
	protected static final KeyBinding[] PAGED_KEY_BINDINGS=
		{
			//bind (left arrow) to the XML editor kit's previous page action
			new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), XMLEditorKit.PREVIOUS_PAGE_ACTION_NAME),
			//bind (right arrow) to the XML editor kit's next page action
			new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), XMLEditorKit.NEXT_PAGE_ACTION_NAME),
			//bind (pageup) to the XML editor kit's previous page action
			new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), XMLEditorKit.PREVIOUS_PAGE_ACTION_NAME),
			//bind (pagedown) to the XML editor kit's next page action
			new KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), XMLEditorKit.NEXT_PAGE_ACTION_NAME),
		};

	/**The the paged view, if any, displayed on this component. This view does not
		have to be at the root of the view tree.
	*/
	private XMLPagedView PagedView=null;

		/**@return The paged view the pages of which can be controlled, or
			<code>null</code> if there is no paged view.
		@see XMLTextPane#fetchPagedView
		*/
		protected XMLPagedView getPagedView() {return PagedView;}

		/**Sets the paged view the pages of which can be controlled.
			@param pagedView The paged view to be paged, <code>null</code> if there
			should be no paged view.
		@see XMLTextPane#fetchPagedView
		*/
		public void setPagedView(final XMLPagedView pagedView)	//G***make this protected if we can
		{
//G***del			Debug.traceStack("Setting the paged view");  //G***del

			//G***remove the listeners from any previous page view, if present
			PagedView=pagedView;	//set our paged view
			pagedView.addPageListener(this);	//show that we want to be notified of page changes the paged view makes, so that we can forward those events
			pagedView.addProgressListener(this);	//show that we want to be notified of any progress the paged view makes, so that we can forward those events
			pagedView.setDisplayPageCount(DisplayPageCount);	//in case our display page count has previously been set, tell our page view about it now that we have one
//G***del		  setAntialias(antialias);  //set the antialias value to the value we saved, so it will be reflected in the new document
//G***del Debug.trace(this, "Getting paged view's attributes, attribute set is mutable: "+(pagedViewAttributeSet instanceof MutableAttributeSet));  //G***del; testing
		}

	/**The factor by which text should be zoomed.*/
	private float zoomFactor=DocumentConstants.DEFAULT_ZOOM_FACTOR;

		/**@return The factor by which text should be zoomed.
		@see DocumentConstants#DEFAULT_ZOOM_FACTOR
		*/
		public float getZoomFactor() {return zoomFactor;}

		/**Sets the factor by which text should be zoomed.
		@param newZoomFactor The amount by which normal text should be multiplied.
		*/
		public void setZoomFactor(final float newZoomFactor)
		{
//G***del			final float oldZoomFactor=getZoomFactor(); //get the current zoom factor
			if(zoomFactor!=newZoomFactor)  //if the zoom factor is really changing
			{
//G***del Debug.trace("changing view factor from "+oldZoomFactor+" to "+newZoomFactor); //G***del
				zoomFactor=newZoomFactor; //set the new zoom factor
				DocumentUtilities.setZoomFactor(getDocument(), zoomFactor);  //store the new zoom factor in the document
//G***del				document.putProperty(XMLDocument.ZOOM_FACTOR_PROPERTY, new Float(zoomFactor)); //store the new zoom factor in the document
				final XMLPagedView pagedView=getPagedView();  //get a reference to our paged view
				if(pagedView!=null)  //if we have a paged view
				{
//G***del Debug.trace("ready to relayout"); //G***del
					pagedView.relayout();  //relayout the paged view
				}
			}
		}

	/**Whether text in this component is antialiased. We keep a local copy stored
		so that we can update any new text pane when it is installed.*/
	private boolean antialias=true;

		/**@return Whether text in this component is antialiased.*/
		public boolean isAntialias()
		{
//G***del Debug.trace("Inside isAntialias(), returning: "+antialias);
/*G***del
		  final Document document=getDocument();  //get a reference to the associated document
		  Object antialiasProperty=document.getProperty(DocumentConstants.ANTIALIAS_DOCUMENT_PROPERTY); //get the antialias property
		  return antialiasProperty instanceof Boolean ? ((Boolean)antialiasProperty).booleanValue() : false;  //return the boolean value of the antialias property
*/
		  //if we have a paged view, see if it requests antialiasing; otherwise, use our local variable
//G***fix		  return getPagedView()!=null ? XMLStyleConstants.isAntialias(getPagedView().getAttributes()) : antialias;
		  return antialias;
		}

		/**Sets whether text in this component is antialiased.
		@param newAntialias Whether text should be antialias.
		*/
		public void setAntialias(final boolean newAntialias)
		{
			final boolean oldAntialias=isAntialias(); //get the current antialias value
			if(oldAntialias!=newAntialias)  //if the antialias is really changing
			{
//G***del					//store the new value as a property in the document
//G***del				getDocument().putProperty(DocumentConstants.ANTIALIAS_DOCUMENT_PROPERTY, new Boolean(newAntialias));
//G***del				final AttributeSet pagedViewAttributeSet=pagedView.getAttributes();  //get the paged view's attribute set
//G***del				if(pagedViewAttributeSet instanceof MutableAttributeSet)  //if we can change the paged view's attributes
				antialias=newAntialias; //set the new antialias status variable so that we can set whatever new document is installed
				final XMLPagedView pagedView=getPagedView();  //get a reference to our paged view
				if(pagedView!=null)  //if we have a paged view
				{
//G***fix					XMLStyleConstants.setAntialias((MutableAttributeSet)pagedView.getAttributes(), newAntialias);  //set the view's antialias property
Debug.trace("Ready to relayout");
//G***fix, if needed				  pagedView.changedUpdate(new javax.swing.text.AbstractDocument.DefaultDocumentEvent(0, getDocument().getLength(), DocumentEvent.EventType.CHANGE), getBounds(), pagedView.getViewFactory());  //send a synthetic changeUpdate() so that all the children and layout strategies can get a chance to reinitialize

					pagedView.relayout();  //relayout the paged view
				}
/*G***fix
				final XMLPagedView pagedView=getPagedView();  //get a reference to our paged view
				if(pagedView!=null && pagedView.getAttributes() instanceof MutableAttributeSet)  //if we have a paged view, and its attributes are mutable
				{
//G***fix					XMLStyleConstants.setAntialias((MutableAttributeSet)pagedView.getAttributes(), newAntialias);  //set the view's antialias property
					pagedView.relayout();  //relayout the paged view
				}
*/
			}
		}

	/**The position after which searching will begin, or -1 if searching has not
		been performed or no match was found.*/
	private int searchOffset=-1;

		/**Returns the position after which searching will begin, or -1 if searching
			has not been performed or no match was found on the last search. If
			<code>getSearchLength()</code> is greater than zero, this indicates the
			beginning position of the last match found.
		@see #getSearchLength
		*/
		public int getSearchOffset() {return searchOffset;}

		/**Updates the search offset and length, marking where searching last found
		  a match. Removes all search highlighters and adds the appropriate
			highlighter based on the new search position.
		@param newSearchOffset The position after which searching should begin.
		@param newSearchLength The length of the match.
		@exception BadLocationException Thrown if the given search offset and/or
			length do not represent a valid location in the document.
		@see #resetSearchPosition
		*/
		public void setSearchPosition(final int newSearchOffset, final int newSearchLength) throws BadLocationException
		{
			resetSearchPosition();  //reset our search position, removing the search highlights
			searchOffset=newSearchOffset; //update the search offset
//G***del			searchHighlighter.removeAllHighlights();  //remove all highlights from previous searches
				//highlight the new search position
			getHighlighter().addHighlight(newSearchOffset, newSearchOffset+newSearchLength, searchHighlightPainter);
//G***fix			searchHighlighter.addHighlight(newSearchOffset, newSearchOffset+newSearchLength, searchHighlightPainter);
		}

		/**Resets the search position so that the next search will be performed as
			if no previous searches have been performed.
		*/
		public void resetSearchPosition()
		{
			searchOffset=-1; //reset the search offset to -1
				//remove all highlights from previous searches
		  TextComponentUtilities.removeHighlights(this, searchHighlightPainter);
/*G***del when works
		  final Highlighter highlighter=getHighlighter(); //get the current highlighter
				//remove all highlights from previous searches
			Highlighter.Highlight[] highlightArray=highlighter.getHighlights(); //get an array of highlights
			for(int i=0; i<highlightArray.length; ++i)  //look at each highlight
			{
				final Highlighter.Highlight highlight=highlightArray[i];  //get a reference to this height
				if(highlight.getPainter()==searchHighlightPainter) //if this is a search highlight
				  highlighter.removeHighlight(highlight);		//remove the search highlight
			}
*/
		}

	/**The length of the last search match, or zero if there has been no match.*/
//G***del if not needed	private int searchLength=0;

		/**@return The length of the last search match, or zero if there has been
		  no match.*/
//G***del if not needed		public int getSearchLength() {return searchLength;}

	/**A map of view factories, each keyed to a namespace URI string.*/
	private final Map namespaceViewFactoryMap=new HashMap();	//G***fix; this is accessed through a complex sequence from the superclass and is not yet initialized

		/**Registers a view factory for a particular namespace URI. These will be
		  used by any installed <code>XMLEditorKit</code>.
		@param namespaceURI The namespace URI that identifies the namespace,
			elements in which will use the given view factory to create views.
		@param viewFactory The view factory that should be associated with the
			given namespace.
		*/
		public void registerViewFactory(final String namespaceURI, final ViewFactory viewFactory)
		{
			namespaceViewFactoryMap.put(namespaceURI, viewFactory); //store the view factory in the map, keyed to the namespace URI
Debug.trace("XMLTextPane installing view factory for namespace: ", namespaceURI); //G***del
Debug.trace("Current installed editor kit: ", getEditorKit().getClass().getName()); //G***del
		  if(getEditorKit() instanceof XMLEditorKit)  //if the currently installed editor kit is an XMLEditorKit
			{
				final XMLEditorKit xmlEditorKit=(XMLEditorKit)getEditorKit(); //get the editor kit already installed
				xmlEditorKit.registerViewFactory(namespaceURI, viewFactory);  //duplicate the local registration in the current XML editor kit
			}
/*G***del when works
Debug.trace("XMLTextPane installing view factory for namespace: ", namespaceURI); //G***del
Debug.trace("Current installed editor kit: ", getEditorKit().getClass().getName()); //G***del
		  if(getEditorKit() instanceof XMLEditorKit)  //if the currently installed editor kit is an XMLEditorKit
			{
				final XMLEditorKit xmlEditorKit=(XMLEditorKit)getEditorKit(); //get the editor kit already installed
				final XMLEditorKit.XMLViewFactory xmlViewFactory=(XMLEditorKit.XMLViewFactory)xmlEditorKit.getViewFactory();  //get the view factory from the editor kit G***make sure this is an XMLViewFactory
				xmlViewFactory.registerViewFactory(namespaceURI, viewFactory);  //duplicate the local registration in the current XML view factory
			}
*/
		}

		/**Retrieves a view factory for the given namespace, if one has been
			registered.
		@param namespaceURI The namespace for which a view factory should be
			returned.
		@return A view factory for creating views for elements in the given
			namepace, or <code>null</code> if no view factory has been registered
			for the given namespace.
		*/
		public ViewFactory getViewFactory(final String namespaceURI)
		{
			return (ViewFactory)namespaceViewFactoryMap.get(namespaceURI); //return a view factory for the given namespace, if one has been registered
		}

		/**@return An iterator to all the namespaces to which view factories have
		  been associated.
		*/
		public Iterator getViewFactoryNamespaceIterator()
		{
			return namespaceViewFactoryMap!=null ? namespaceViewFactoryMap.keySet().iterator() : new HashSet().iterator(); //return an iterator to the keys, which are namespaces
//G***fix			return namespaceViewFactoryMap.keySet().iterator(); //return an iterator to the keys, which are namespaces
		}

	/**A map of link controllers, each keyed to a namespace URI string.*/
	private Map namespaceLinkControllerMap=new HashMap();

		/**Registers a link controller for a particular namespace URI. These will be
		  used by any installed <code>XMLEditorKit</code>.
		@param namespaceURI The namespace URI that identifies the namespace,
			elements in which will use the given link controllers to generate link
			entry, exit, and activation events.
		@param linkController The link controller that should be associated with
			the given namespace.
		*/
		public void registerLinkController(final String namespaceURI, final XMLLinkController linkController)
		{
			namespaceLinkControllerMap.put(namespaceURI, linkController); //store the link controller in the map, keyed to the namespace URI
		  if(getEditorKit() instanceof XMLEditorKit)  //if the currently installed editor kit is an XMLEditorKit
			{
				final XMLEditorKit xmlEditorKit=(XMLEditorKit)getEditorKit(); //get the editor kit already installed
				xmlEditorKit.registerLinkController(namespaceURI, linkController);  //duplicate the local registration in the current XML editor kit
			}
		}

		/**Retrieves a link controller for the given namespace, if one has been
			registered.
		@param namespaceURI The namespace for which a link controller should be
			returned.
		@return A link controller for creating views for elements in the given
			namepace, or <code>null</code> if no view factory has been registered
			for the given namespace.
		*/
		public XMLLinkController getLinkController(final String namespaceURI)
		{
			return (XMLLinkController)namespaceLinkControllerMap.get(namespaceURI); //return a link controller for the given namespace, if one has been registered
		}

		/**@return An iterator to all the namespaces to which link controllers have
		  been associated.
		*/
		public Iterator getLinkControllerNamespaceIterator()
		{
			return namespaceLinkControllerMap!=null ? namespaceLinkControllerMap.keySet().iterator() : new HashSet().iterator(); //return an iterator to the keys, which are namespaces G***fix
		}

	/**Constructs a new <code>XMLTextPane</code> with a specified XML document model,
		and the editor kit is set to a new instance of <code>XMLEditorKit</code>.
	@param doc The document model, a descendant of com.garretwilson.swing.text.xml.XMLDocument.
	@see com.garretwilson.swing.text.xml.XMLEditorKit
	@see com.garretwilson.swing.text.xml.XMLDocument
	*/
	public XMLTextPane(XMLDocument doc)
	{
		this();	//do the default constructing

//G***testing hint
/*G***del
        Graphics2D graphics2D=(Graphics2D)getGraphics();

                AffineTransform xf
                    = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .getDefaultTransform();
		final FontRenderContext fontRenderContext=new FontRenderContext(xf, false, false);
		  graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
*/


		setXMLDocument(doc);	//set the document
//G***del; this is done in the default constructor		addMouseMotionListener(this);	//keep track of mouse movements G***fix; currently used for updating hyperlink on page change
//G***del when works		addKeyListener(this);  //show that we want to be notified of key presses so that we can implement paging functionality
	}

	/**Constructs a new <code>XMLTextPane</code> and the editor kit is set to a
		new instance of <code>XMLEditorKit</code>. The document model is set to
		<code>null</code>.
	@see com.garretwilson.swing.text.xml.XMLEditorKit
	*/
	public XMLTextPane()
	{
		super();	//construct the parent class
//G***del when works		originalKeymap=getKeymap();  //get the current key map and store it for future use
		final Keymap defaultKeymap=getKeymap();	//get the current keymap
		final Keymap xmlKeymap=addKeymap(XML_KEYMAP_NAME, defaultKeymap); //create a new keymap for our custom actions
		loadKeymap(defaultKeymap, DEFAULT_KEY_BINDINGS, getActions()); //load our custom keymap G***what happens if this is set and then the editor kit changes?
		final Keymap pagedKeymap=addKeymap(PAGED_KEYMAP_NAME, xmlKeymap); //create a new keymap for paging
		loadKeymap(pagedKeymap, PAGED_KEY_BINDINGS, getActions()); //load our custom keymap G***what happens if this is set and then the editor kit is changed?
		updateKeymap();	//update the keymap based upon our current settings
		final ViewFactory xhtmlViewFactory=new XHTMLViewFactory();  //create a view factory fo XHTML
		registerViewFactory(XHTMLConstants.XHTML_NAMESPACE_URI.toString(), xhtmlViewFactory);  //associate the XHTML view factory with XHTML elements
		registerViewFactory(OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.toString(), xhtmlViewFactory);  //associate the XHTML view factory with OEB elements
		final XMLLinkController xhtmlLinkController=new XHTMLLinkController();  //create a link controller fo XHTML
		registerLinkController(XHTMLConstants.XHTML_NAMESPACE_URI.toString(), xhtmlLinkController);  //associate the XHTML view factory with XHTML elements
		registerLinkController(OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.toString(), xhtmlLinkController);  //associate the XHTML link controller with OEB elements
//G***del; doesn't work		setBackground(Color.white); //G***set to get the background color from the document itself
		setEditorKit(new XMLEditorKit());	//create a new XML editor kit and use it
/*G***del when works
		final Keymap keymap=getKeymap();  //get the current key map
		loadKeymap(keymap, DEFAULT_KEY_BINDINGS, getActions()); //load our custom keymap G***how do our actions get here from the editor kit?
*/
//G***del; maybe delete class		setCaret(new XMLCaret(getCaret()));	//G***testing
		addMouseMotionListener(this);	//keep track of mouse movements G***fix; currently used for updating hyperlink on page change
	}

	/**Searches the view hierarchy for an XMLPagedView, and uses the first one
		it finds as this pane's paged view.
	@see XMLPagedView
	@see XMLTextPane#setPagedView
	@see XMLTextPane#fetchPagedView
	*/
/*G***bring this back when we can find a place to call it from
	protected void fetchPagedView()
	{	//G***do we have to to through the UI to get the root view?
		setPagedView(fetchPagedView(getUI().getRootView(this)));	//get the UI's root view and try to fetch a paged view from it; set whatever we get (null or otherwise) as the paged view
	}
*/

	/**Searches the view hierarchy for an XMLPagedView, and uses the first one
		it finds as this pane's paged view. This function is called from fetchPagedView().
	@see XMLPagedView
	@see XMLTextPane#setPagedView
	@see XMLTextPane#fetchPagedView
	*/
/*G***bring this back when we can find a place to call it from
	protected XMLPagedView fetchPagedView(final View view)
	{
		if(view!=null)	//if this is a valid view
		{
			if(view instanceof XMLPagedView)	//if this view is a paged view
				return (XMLPagedView)view;	//cast the view to an XMLPagedView and return it
			else	//if this isn't a paged view, check the view's children
			{
				for(int i=0; i<view.getViewCount(); ++i)	//look at each child view
				{
					final XMLPagedView pagedView=fetchPagedView(view.getView(i));	//get the view at this index and try to fetch a paged view from it
					if(pagedView!=null)	//if this view or its children is a paged view
						return pagedView;		//we found a paged view, so return it
				}
			}
		}
		return null;	//if we made it here, we weren't able to find a paged view, so return null
	}
*/

	/**Associates the editor with a text document. This document must be a
		com.garretwilson.swing.text.xml.XMLDocument.
	@param doc The document to display and/or edit.
	@exception IllegalArgumentException Thrown if doc is not a descendant of
		com.garretwilson.swing.text.xml.XMLDocument.
	@see XMLTextPane#setXMLDocument
	*/
/**G***we can't have this because we the default JTextPane method creates a default editor kit that creates a default document before we can do anything about it
	public void setDocument(Document doc)
	{
		if(doc instanceof XMLDocument)	//if this is an XMLDocument, which we require
			super.setDocument(doc);	//set the document normally
		else	//if this isn't an XMLDocument
			throw new IllegalArgumentException("Model must be XMLDocument");	//throw an exception stating the error
	}
*/

	/**Associates the editor with a text document. This must be a
		<code>StyledDocument</code>.
	<p>This version ensures that properties such as zoom are set on the document.</p>
	@param document The document to display/edit.
	@exception IllegalArgumentException Thrown if doc can't be narrowed to a
		<code>StyledDocument</code> which is the required type of model for this
		text component.
	*/
	public void setDocument(final Document document)
	{
		DocumentUtilities.setPaged(document, isPaged());  //store the new paged value in the document
		DocumentUtilities.setZoomFactor(document, getZoomFactor());  //store the zoom factor in the document
//G***del		document.putProperty(XMLDocument.ZOOM_FACTOR_PROPERTY, new Float(getZoomFactor())); //store the zoom factor in the document
		super.setDocument(document);  //set the document normally
	}

	/**Associates the editor with a text document.
		The currently registered factory is used to build a view for
		the document, which gets displayed by the editor.
	@param document The document to display/edit.
	*/
	public void setStyledDocument(final StyledDocument document)
	{
		//set the document normally so that we can update the document properties
		//  (note that usually super.setStyledDocument() would be called, but the
		//  super version does not call setDocument(), so we must either duplicate
		//  property-setting in this method or simply call setDocument() directly.)
		setDocument(document);  //set the document normally
	}

	/**Associates the editor with an XML document.
	@param document The document to display and/or edit.
	@see XMLTextPane#setDocument
	*/
	public void setXMLDocument(XMLDocument document)
	{
		setDocument(document);	//we know this is an XML document, so just set the document normally
//G***fix; this perhaps isn't called if we have a section view at the bottom
	}

	/**Returns the model associated with the editor. This is a convenience method
		which does the required casting to com.garretwilson.swing.text.xml.XMLDocument.
	@return The document model.
	@see JTextPane#getStyledDocument
	*/
	public XMLDocument getXMLDocument() //G***maybe delete this method
	{
		return (XMLDocument)getDocument();	//get the document, cast it to an XMLDocument, and return it
	}

	/**Returns the editor kit, a descendant of com.garretwilson.swing.text.xml.XMLEditorKit.
		This is a convenience function that automaticaly casts to a com.garretwilson.swing.text.xml.XMLDocument.
	@return The editor kit.
	@see JTextPane#getStyledEditorKit
	*/
	protected final XMLEditorKit getXMLEditorKit()
	{
		return (XMLEditorKit)getEditorKit();	//get the editor kit, cast it to an XML editor kit, and return it
	}


	/* ***JEditorPane methods*** */

	/**Creates and returns the editor kit to use by default. This version returns
		a com.garretwilson.swing.text.xml.XMLEditorKit
	@return The default editor kit.
	*/
	protected EditorKit createDefaultEditorKit()
	{
		return new XMLEditorKit();	//create an XML editor kit and return it
	}

	/**Sets the editor kit for handling content. Since an XMLTextPane requires a
		descendant of XMLEditorKit, an exception will be thrown if this isn't the
		case.
	@param kit The editor kit that specifies the desired behavior.
	@exception IllegalArgumentException Thrown if kit is not a com.garretwilson.swing.text.xml.XMLEditorKit.
	*/
/*G***fix when this isn't declared final in JTextPane
	public final void setEditorKit(EditorKit kit)
	{
		if(kit instanceof XMLEditorKit)	//if this is an XMLEditorKit
			super.setEditorKit(kit);	//set the editor kit normally
		else	//if this isn't an XMLEditorKit
			throw new IllegalArgumentException("Must be XMLEditorKit");	//throw an exception indicating the error
	}
*/
//G***it might also be nice to override setEditorKit() and automatically update whether the editor kit should be paged

	/**Gets the current URL being displayed. 
	If a URL was not specified in the creation of the document, this
		will return <code>null</code>, and relative URL's will not be resolved.
	<p>This version knows how to convert a URI to a URL, if a URI is stored in
		the stream description property.</p>
	@return The page URL, or <code>null</code> if none.
	*/
/*G***del if not needed
	public URL getPage() {
			return (URL) getDocument().getProperty(Document.StreamDescriptionProperty);
	}
*/

	/**Sets the location against which to resolve relative URIs. By default this
		will be the document's URI.
	@param baseURI The new location against which to resolve relative URIs.
	@see #BASE_URI_PROPERTY
	*/
	protected void setBaseURI(final URI baseURI)
	{
		getDocument().putProperty(XMLDocument.BASE_URI_PROPERTY, baseURI);	//store the base URI
	}
	
	/**Gets the location against which to resolve relative URIs.
	@return The location against which to resolve relative URIs, or <code>null</code>
		if there is no base URI.
	@see #BASE_URI_PROPERTY
	*/
	protected URI getBaseURI()	//G***del throws URISyntaxException
	{
		return (URI)getDocument().getProperty(XMLDocument.BASE_URI_PROPERTY);	//return the value of the base URI property
	}

	/**Sets the text to the specified content, which must be well-formed XML.
	<p>If the XML has no root element, it will be wrapped in
	<code>&lt;div&gt;...&lt;/div&gt;</code>. This means that simple text will
	also be considered and processed as XML. (It is usually desired that the
	content type be set to "application/xhtml+xml").</p>
	@param text The new text to be set.
	@see #getText
	*/
	public void setText(String text)
	{
		if(text.length()>0)	//if there is any text
		{
			if(text.charAt(0)!='<')	//if this text doesn't start with markup G***use a constant
				text="<div>"+text+"</div>";	//wrap the text with a <div> element G***this assumes a lot about HTML; make this more generic if we can
		}
		super.setText(text);	//set the text normally
	}

	/**
	 * Returns the text contained in this <code>TextComponent</code>
	 * in terms of the
	 * content type of this editor.  If an exception is thrown while
	 * attempting to retrieve the text, <code>null</code> will be returned.
	 * This is implemented to call <code>JTextComponent.write</code> with
	 * a <code>StringWriter</code>.
	 *
	 * @return the text
	 * @see #setText
	 */
/*G***see if this already works
	public String getText() {
String txt;
try {
		StringWriter buf = new StringWriter();
		write(buf);
		txt = buf.toString();
			} catch (IOException ioe) {
					txt = null;
			}
			return txt;
	}
*/

	/**Initializes from a URL to a file, which could be a document or, for OEB,
		a package or a .zip file. This creates a model of the type appropriate for
		the component (such as an OEB document) and initializes the model using the
		appropriate editor kit.
	@param url The URL of the file which has the information to load.
	@exception IOException as thrown by the stream being used to initialize.
	@deprecated Replaced with setPage(URI) because in ambiguities in URL
		reserved character encoding.
	@see JEditorPane#setPage
	@see EditorKit#createDefaultDocument
	@see #setDocument
	@see #setPage(URI)
	*/
	public void setPage(final URL url) throws IOException
	{
		try
		{
			setPage(URIUtilities.createURI(url));	//set the page using the URI version of the URL
		}
		catch(URISyntaxException uriSyntaxException)	//if the URL can't be converted to a URI (unlikely)
		{
			throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);	//convert the exception to an IO exception
		}
	}

	/**Initializes from a URI to a resource, which could be a document or, for OEB,
		a package or a .zip file. This creates a model of the type appropriate for
		the component (such as an OEB document) and initializes the model using the
		appropriate editor kit.
	<p>This method must be called from inside the AWT event thread.</p>
	<p>A default input stream locator is used.</p>
	@param uri The URI of the resource which has the information to load.
	@exception IOException as thrown by the stream being used to initialize.
	@see JEditorPane#setPage
	@see EditorKit#createDefaultDocument
	@see #setDocument
	@see XMLTextPane#setPage(URI, URIInputStreamable)
	*/
	public void setPage(final URI uri) throws IOException
	{
		setPage(uri, new URIUtilities());  //we'll use an instance of URIUtilities to make direct connections to URIs
	}

	/**Initializes from a URI to a resource, which could be a document or, for OEB,
		a package or a .zip file. This creates a model of the type appropriate for
		the component (such as an OEB document) and initializes the model using the
		appropriate editor kit.
	<p>This method must be called from inside the AWT thread.</p>
	@param uri The URI of the resource which has the information to load.
	@param uriInputStreamable The input stream locator to use for looking up input streams.
	@exception IOException as thrown by the stream being used to initialize.
	@see JEditorPane#setPage
	@see EditorKit#createDefaultDocument
	@see #setDocument
	*/
	public void setPage(URI uri, final URIInputStreamable uriInputStreamable) throws IOException
	{
		setURIInputStreamable(uriInputStreamable);  //use whatever input stream locator they specify
		

//G***make sure we set all the properties like the subclass uses
//G***note that the underlying class calls this.read(), which performs similar but not identical functionality as code here -- it would be good to use that, if possible
Debug.trace();  //G***del
/*G***del when works
		final XMLEditorKit xmlEditorKit=(XMLEditorKit)getUI().getEditorKit(this);	//get the current editor kit, and assume it's an XML editor kit G***we might want to check just to make sure
		final Document document=xmlEditorKit.createDefaultDocument();	//create a default document
		document.putProperty(Document.StreamDescriptionProperty, page);	//store the URL in the document
*/
Debug.trace("setting page: ", uri);  //G***del



//G***del		setURIInputStreamable(null);  //show that we don't yet know how to access streams from URIs
Debug.trace("Getting input stream.");
		InputStream inputStream=getStream(uri);  //get an input stream to the URI; this should set the media type and install the correct editor kit

Debug.trace("installed editor kit is first: ", getEditorKit().getClass().getName());  //G***del

		final String contentType=getContentType();  //see what content type we decided on
Debug.trace("content type is first: ", contentType);  //G***del
		if(MediaTypeConstants.APPLICATION_ZIP.equals(contentType)) //if this appears to be an application/zip file
		{
Debug.trace("found zip file: ", uri);  //G***del
			if(URIConstants.FILE_SCHEME.equals(uri.getScheme()))  //if this is the file scheme
			{
				inputStream.close();  //close the input stream; we'll access the zip file directly G***testing
					//G***look for an OEB publication
			  final File zipFile=new File(uri);  //create a file for accessing the zip file
			  final ZipManager zipManager=new ZipManager(zipFile);  //create a zip manager for accessing the file
				setURIInputStreamable(zipManager);  //we'll use the zip manager as our input stream locator
				final Iterator zipEntryIterator=zipManager.getZipEntryIterator(); //get an iterator to look
				while(zipEntryIterator.hasNext()) //while there are more zip entries
				{
				  final ZipEntry zipEntry=(ZipEntry)zipEntryIterator.next();  //get the next zip entry
					if(zipEntry.getName().endsWith(".opf")) //if this is an OEB publication G***fix to use the media type
					{
/*G***fix
					final MediaType zipEntryMediaType=FileUtilities.getMediaType(zipEntry.getName()); //get the media type of the zip entry
					getMediaType()
*/
						try
						{
							Debug.trace("switching URI to: ", zipManager.getURI(zipEntry)); //G***del
							uri=zipManager.getURI(zipEntry); //use the file inside the zip file instead of this one
							inputStream=getStream(uri);  //get an input stream to the new URI; this should set the media type and install the correct editor kit
						}
						catch(URISyntaxException uriSyntaxException)	//if there is an error with the format of a URI (which shouldn't happen)
						{
							Debug.warn(uriSyntaxException);	//processing can still go on
						} 
					}
				}
			}
			else  //if this is not a zip file, but some other sort of zip access, throw an exception
				Debug.error("Zip file must use URI file protocol");  //G***fix
		}
Debug.trace("installed editor kit is now: ", getEditorKit().getClass().getName());  //G***del
		if(getURIInputStreamable()==null) //if we haven't established an input stream locator
		{
			setURIInputStreamable(new URIUtilities());  //we'll use an instance of URIUtilities to make direct connections to URIs
		}
		setPage(uri, inputStream);	//set the page using the given input stream
	}

	/**Initializes from a URI with its input stream already provided.
		This creates a model of the type appropriate for
		the component (such as an OEB document) and initializes the model using the
		appropriate editor kit.
	<p>The <code>URIInputStreamable</code> should have already been initialized.</p>
	<p>This method must be called from inside the AWT thread.</p>
	@param uri The URI of the resource which has the information to load.
	@param inputStream The input stream from which the document content should be read.
	@exception IOException Thrown if there is an error loading the document.
	@see JEditorPane#setPage
	@see EditorKit#createDefaultDocument
	@see #setDocument
	*/
	public void setPage(final URI uri, final InputStream inputStream) throws IOException
	{
		final XMLEditorKit xmlEditorKit=(XMLEditorKit)getEditorKit();	//get the current editor kit, and assume it's an XML editor kit G***we might want to check just to make sure
		final Document document=xmlEditorKit.createDefaultDocument();	//create a default document

		document.putProperty(Document.StreamDescriptionProperty, URIUtilities.toValidURL(uri));	//store a URL version of the URI in the document, as getPage() expects this to be a URL
		document.putProperty(XMLDocument.BASE_URI_PROPERTY, uri);	//store the base URI in the document
Debug.trace("reading from stream"); //G***del
//G***del		read(inputStream, document);  //read the document from the input stream
		if(document instanceof XMLDocument) //if this is an XML document
		{
			((XMLDocument)document).setURIInputStreamable(getURIInputStreamable()); //give the XML document any input stream locator that we might have, so that it can access files from within zip files, for instance
		}

/*G***del when works
		xmlEditorKit.addProgressListener(this);	//show that we want to be notified of any progress the XML editor kit makes G***should one of these go in the XMLTextPane? will it conflict with this one?
		if(document instanceof XMLDocument) //if this is an XML document
		{
			final XMLDocument xmlDocument=(XMLDocument)document;  //cast the document to an XML document
			xmlDocument.setURIInputStreamable(getURIInputStreamable()); //give the XML document any input stream locator that we might have, so that it can access files from within zip files, for instance
			((XMLDocument)document).addProgressListener(this);	//show that we want to be notified of any progress the XML document makes G***should this go here or elsewhere? should this bubble up to the editor kit instead?
		}
		try
		{
*/
			final DocumentLoader documentLoader=new DocumentLoader(inputStream, document);	//create a thread for loading the document 
			//TODO check for already loading asynchronously, as does JEditorPane
			if(document instanceof AbstractDocument)	//if the document is an abstract document, which can give a load priority
			{
				final AbstractDocument abstractDocument=(AbstractDocument)document;	//cast to an abstract document
				final int priority=abstractDocument.getAsynchronousLoadPriority();	//get the asychronous loading priority
				if(isAsynchronousLoad())	//if we should load asynchronously
//TODO fix the asynchronous flag				if(priority>=0)	//if we should load asynchronously
				{
					documentLoader.start();	//load the document in a separate thread
					return;	//we're finished loading
				}
			}
			documentLoader.load();	//if we shouldn't (or can't) load asynchronously, load the document in our own thread
/*G***del when works
		}
		finally
		{
			xmlEditorKit.removeProgressListener(this);	//show the editor kit that we no longer want to be notified of any progress the XML editor kit makes
			if(document instanceof XMLDocument) //if this is an XML document
				((XMLDocument)document).removeProgressListener(this);	//show the document that we no longer want to be notified of any progress the document makes
		}
*/
	}

	/**Class that allows loading the document in a separate thread.*/
	protected class DocumentLoader extends Thread
	{

		/**The input stream from which the document is to beloaded.*/
		private final InputStream inputStream;
		
		/**The document to be loaded.*/
		private final Document document;
		
		/**Constructs a thread for asynchronously loading a document from an input
			stream.
		@param inputStream The input stream from which the document is to be loaded.
		@param document The document to be loaded.
		 */
		public DocumentLoader(final InputStream inputStream, final Document document)
		{
			super(DocumentLoader.class.getName());	//construct the thread with a user-friendly name
			this.inputStream=inputStream;	//save the input stream
			this.document=document;	//save the document
		}		
		
		/**Reads the document from the input stream, handling any errors that occur.*/
		public void run()
		{
			final EditorKit editorKit=getEditorKit();	//get the current editor kit
			if(editorKit instanceof XMLEditorKit)	//if this is an XML editor kit
			{
					//G***this might all go away when we revamp progress management
				((XMLEditorKit)editorKit).addProgressListener(XMLTextPane.this);	//show that we want to be notified of any progress the XML editor kit makes G***should one of these go in the XMLTextPane? will it conflict with this one?
			}
			if(document instanceof XMLDocument) //if this is an XML document
			{
				((XMLDocument)document).addProgressListener(XMLTextPane.this);	//show that we want to be notified of any progress the XML document makes G***should this go here or elsewhere? should this bubble up to the editor kit instead?
			}
			try
			{
				load();	//load the document
			}
			catch(IOException ioException)	//if there are any IO errors
			{
				Debug.error(ioException);	//TODO fix asynchronous loading error handling
			}
			finally
			{
				if(editorKit instanceof XMLEditorKit)	//if this is an XML editor kit
				{
					((XMLEditorKit)editorKit).removeProgressListener(XMLTextPane.this);	//show the editor kit that we no longer want to be notified of any progress the XML editor kit makes
				}
				if(document instanceof XMLDocument) //if this is an XML document
				{
					((XMLDocument)document).removeProgressListener(XMLTextPane.this);	//show the document that we no longer want to be notified of any progress the document makes
				}
			}
		}

		/**Reads the document from the input stream.
		@exception IOException Thrown if there is an error loading the document.
		*/
		public void load() throws IOException
		{
				//show the wait cursor G***do we want to make sure the cursor is set from the AWT thread?
			final Cursor originalCursor=ComponentUtilities.setCursor(XMLTextPane.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
					//G***when does this get closed?
				read(inputStream, document);  //read the document from the input stream
				fireMadeProgress(new ProgressEvent(this, CONSTRUCT_TASK, "Constructing the document..."));	//G***testing i18n
				try
				{
						//make sure the actual document change occurs in the event queue to prevent exceptions from occurring 
					EventQueueUtilities.invokeInEventQueueAndWait(new Runnable()
							{
								public void run()
								{
									setDocument(document);	//show that the text pane is using this document (this actually creates the views)
								}
							});
				}
				catch(InterruptedException interruptedException)
				{
					Debug.error(interruptedException);	//G***fix
				}
				catch(InvocationTargetException invocationTargetException)
				{
					Debug.error(invocationTargetException);	//G***fix
				}
				fireMadeProgress(new ProgressEvent(this, CONSTRUCT_TASK, "Finished constructing the document...", true));	//G***testing i18n
			}
			finally
			{
				setCursor(originalCursor); //after the event thread is finished setting the document, always set the cursor back to its original form
			}
		}
	}

	/**Fetches a stream for the given URL, which is about to
		be loaded by the <code>setPage</code> method.
	<p>This version calls the URI version.</p>
	@param url The URI of the page.
	@deprecated Replaced with setPage(URI) because in ambiguities in URL
		reserved character encoding.
	@see #getStream(URI)
	*/
	protected InputStream getStream(final URL url) throws IOException
	{
		try
		{
			return getStream(URIUtilities.createURI(url));	//create a URI from the page URL and get the stream from that
		}
		catch(URISyntaxException uriSyntaxException)	//if the URL can't be converted to a URI (unlikely)
		{
			throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);	//convert the exception to an IO exception
		}
	}

    /**
     * Fetches a stream for the given URI, which is about to
     * be loaded by the <code>setPage</code> method.  By
     * default, this simply opens the URL and returns the
     * stream.  This can be reimplemented to do useful things
     * like fetch the stream from a cache, monitor the progress
     * of the stream, etc.
     * <p>
     * This method is expected to have the the side effect of
     * establishing the content type, and therefore setting the
     * appropriate <code>EditorKit</code> to use for loading the stream.
     * <p>
     * If this the stream was an http connection, redirects
     * will be followed and the resulting URL will be set as
     * the <code>Document.StreamDescriptionProperty</code> so that relative
     * URL's can be properly resolved.
     *
     * @param uri The URI of the page
     */
    protected InputStream getStream(final URI uri) throws IOException
		{
			final URIInputStreamable uriInputStreamable=getURIInputStreamable();  //see if we have an input stream locator
			if(uriInputStreamable!=null)  //if we have an input stream locator (if we're reading from a zip file, for instance)
			{
Debug.trace("found input stream locator, getting input stream to URI: ", uri); //G***del
				final InputStream inputStream=uriInputStreamable.getInputStream(uri);  //get an input stream from the page
				final MediaType mediaType=URIUtilities.getMediaType(uri);  //get the media type of the target
				if(mediaType!=null) //if we know the media type of the URL
		  		setContentType(mediaType.toString());  //set the content type based upon our best guess
				return inputStream; //return the input stream we located (with an input stream locator, there's no need for us to try to open a connection to the URL ourselves
			}

			URL page=uri.toURL();	//convert the URI to a URL (we assume that if the URI is not a URL, an input stream locator would have been provided

			URLConnection conn = page.openConnection();
			if (conn instanceof HttpURLConnection) {
					HttpURLConnection hconn = (HttpURLConnection) conn;
					hconn.setInstanceFollowRedirects(false);
					int response = hconn.getResponseCode();
					boolean redirect = (response >= 300 && response <= 399);

					/*
					 * In the case of a redirect, we want to actually change the URL
					 * that was input to the new, redirected URL
					 */
					if (redirect) {
				String loc = conn.getHeaderField("Location");
				if (loc.startsWith("http", 0)) {
						page = new URL(loc);
				} else {
						page = new URL(page, loc);
				}
				return getStream(page);
					}
			}
/*G***fix
			if (pageProperties == null) {
					pageProperties = new Hashtable();
			}
			String type = conn.getContentType();
			if (type != null) {
					setContentType(type);
					pageProperties.put("content-type", type);
			}
			pageProperties.put(Document.StreamDescriptionProperty, page);
			String enc = conn.getContentEncoding();
			if (enc != null) {
					pageProperties.put("content-encoding", enc);
			}
			InputStream in = conn.getInputStream();
			return in;

*/



	String contentType = conn.getContentType();
	if(contentType!=null) //if we receive at least a guess of the content type
	{
//G***del		final MediaType mediaType=new MediaType(contentType); //get a media type object to examine the content type
//TODO eventually don't allow MediaType to compare with strings, if this is what's happening
//G***should we check for application/xml or text/xml?
		if(MediaType.APPLICATION_XML.equals(contentType)) //if this appears to be an application/xml file
		{
			final MediaType mediaType=URLUtilities.getMediaType(page);  //see if we know what media type this is
			if(mediaType!=null) //if we have an idea of what media type this is
				contentType=mediaType.toString();  //our guess of the media type overrides "application/xml"
		}
		setContentType(contentType);  //set the content type based upon our best guess
//G***fix	    pageProperties.put("content-type", type);
	}
//G***fix	pageProperties.put(Document.StreamDescriptionProperty, page);
	String enc = conn.getContentEncoding();
	if (enc != null) {
//G***fix	    pageProperties.put("content-encoding", enc);
	}
	InputStream in = conn.getInputStream();
	return in;
    }


	/**This method initializes from a stream. If the kit is set to be of type
		<code>XMLEditorKit</code>, and the <code>desc</code> parameter is an
		<code>XMLDocument</code>, then it invokes the <code>XMLEditorKit</code> to
		initiate the read. Otherwise it calls the superclass method which loads the
		model as plain text.
	@param in The stream from which to read.
	@param desc an object describing the stream
	@exception IOException Thrown by the stream being used to initialize.
	@see JEditorPane#read
	@see JTextPane#setDocument
	*/
	public void read(InputStream in, Object desc) throws IOException
	{
Debug.trace("inside read()"); //G***fix all this
//G***fix		else  //if this is not an XML document and an XML editor kit
		{
			final String charset=(String)getClientProperty("charset");  //get the character set being used G***use a constant here
				//create a reader from the input stream
			final Reader reader=(charset != null) ? new InputStreamReader(in, charset) : new InputStreamReader(in);
			super.read(reader, desc);  //read from the reader as just plain text
		}
	}

	/**This method invokes the <code>EditorKit</code> to initiate a read.
		In the case where a <code>ChangedCharSetException</code> is thrown this
		exception will contain the new CharSet. Therefore the <code>read</code>
		operation is then restarted after building a new Reader with the new charset.
	@param inputStream The <code>InputStream</code> to use
	@param document The document to load.
	@exception IOException Thrown if there is an error loading the document.
	*/
	void read(InputStream inputStream, Document document) throws IOException
	{
//G***del		final XMLReader xmlReader=createReader(inputStream, getPage()); //create a reader from the input stream and the current URL
Debug.trace("reading from stream into document"); //G***del
		try
		{
			getEditorKit().read(inputStream, document, 0);  //let the editor kit read the document from the input stream
		}
		catch(BadLocationException e)  //if a bad location was specified
		{
	    throw new IOException(e.getMessage());  //rethrow the error as an IO exception
		}
/*G***fix
	try {
	    String charset = (String) getClientProperty("charset");
	    Reader r = (charset != null) ? new InputStreamReader(in, charset) :
		new InputStreamReader(in);
	    kit.read(r, doc, 0);
	} catch (BadLocationException e) {
	    throw new IOException(e.getMessage());
	} catch (ChangedCharSetException e1) {
	    String charSetSpec = e1.getCharSetSpec();
	    if (e1.keyEqualsCharSet()) {
		putClientProperty("charset", charSetSpec);
	    } else {
		setCharsetFromContentTypeParameters(charSetSpec);
	    }
	    in.close();
	    URL url = (URL)doc.getProperty(Document.StreamDescriptionProperty);
	    URLConnection conn = url.openConnection();
	    in = conn.getInputStream();
	    try {
		doc.remove(0, doc.getLength());
	    } catch (BadLocationException e) {}
	    doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
	    read(in, doc);
	}
*/
	}
/*G***del
		public final void setContentType(String type)
		{
			Debug.notify("Content type: "+type);  //G***del; fix
			super(type);  //G***fix


//G***fix			xmlEditorKit.read(url, document, 0);	//read the file from the given URL




    }
*/

	/**This is called when a type is requested that doesn't match the currently
		installed type.
		If the component doesn't have an <code>EditorKit</code> registered for the
		given type, it will try to create an <code>EditorKit</code> from the default
		<code>EditorKit</code> registry.
		If that fails, a <code>PlainEditorKit</code> is used on the assumption that
		all text documents can be represented as plain text.
		<p>This method is overridden because the custom
		<code>createEditorKitForContentType()</code>, which is static, needs to be
		called.</p>
	@param type The non-</code>null</code> content type.
	@return The editor kit for the specified requested content type.
	*/
  public EditorKit getEditorKitForContentType(String type)
	{

	//G***fix
//G***del		Debug.notify("GetEditorKitForContentType: "+type);  //G***del; fix
//G***fix		  return super.getEditorKitForContentType(type);  //G***del
/*G***fix
      if (typeHandlers == null) {
          typeHandlers = new Hashtable(3);
      }
      EditorKit k = (EditorKit) typeHandlers.get(type);
      if (k == null) {
*/
		EditorKit editorKit;
		editorKit=createEditorKitForContentType(type);  //create an editor kit for this type
		if(editorKit!=null) //if we created an editor kit
		{
			setEditorKitForContentType(type, editorKit);  //set the editor kit so that we find it quickly in the future
		}
		if(editorKit==null) //if we still don't have an editor kit
		{
			editorKit=createDefaultEditorKit(); //create a default editor kit
		}
		return editorKit;
  }

    /**
     * Directly sets the editor kit to use for the given type.  A
     * look-and-feel implementation might use this in conjunction
     * with <code>createEditorKitForContentType</code> to install handlers for
     * content types with a look-and-feel bias.
     *
     * @param type the non-<code>null</code> content type
     * @param k the editor kit to be set
     */
/*G***fix to be consistent; right now, with the new getEditorKit...() the typeHandlers won't be used
    public void setEditorKitForContentType(String type, EditorKit k) {
        if (typeHandlers == null) {
            typeHandlers = new Hashtable(3);
        }
        typeHandlers.put(type, k);
    }
*/


    /**
     * Creates a handler for the given type from the default registry
     * of editor kits.  The registry is created if necessary.  If the
     * registered class has not yet been loaded, an attempt
     * is made to dynamically load the prototype of the kit for the
     * given type.  If the type was registered with a <code>ClassLoader</code>,
     * that <code>ClassLoader</code> will be used to load the prototype.
     * If there was no registered <code>ClassLoader</code>,
     * <code>Class.forName</code> will be used to load the prototype.
     * <p>
     * Once a prototype <code>EditorKit</code> instance is successfully
     * located, it is cloned and the clone is returned.
     *
     * @param type the content type
     * @return the editor kit, or <code>null</code> if there is nothing
     *   registered for the given type
     */
    public static EditorKit createEditorKitForContentType(String type)
		{
			final MediaType mediaType=new MediaType(type);  //create a new media type
/*G***make sure this is an XML media type; if not, call the base class
			if(mediaType.getTopLevelType().eq
*/
/*G***del
Debug.trace("creating editor kit for content type: ", type);  //G***del; fix
		  if(mediaType.equals(mediaType.APPLICATION_ZIP)) //if this is a zip file
			{
				return new OEBEditorKit();  //assume that this is an OEB file in a zip file G***allow for other types of files in zip files later by exploring the package
			}
*/
			if(XHTMLUtilities.isHTML(mediaType))	//if this is an XHTML media type
			{
				return new XHTMLEditorKit(mediaType);	//create a new XHTML editor kit for this media type
			}
				//if this is an OEB package
		  else if(/*G***fix mediaType.equals(OEBConstants.OEB10_DOCUMENT_MEDIA_TYPE) || */mediaType.equals(OEBConstants.OEB10_PACKAGE_MEDIA_TYPE))
			{
Debug.trace("creating OEB editor kit"); //G***del
				return new OEBEditorKit();  //create a new OEB editor kit for the OEB package
			}
//TODO have a XMLUtilities.isXML(mediaType), and if not, create an editor kit normally using the parent class
		  return new XMLEditorKit(mediaType); //create a new XML editor kit for the specified type


/*G***fix
        EditorKit k = null;
        Hashtable kitRegistry = getKitRegisty();
	k = (EditorKit) kitRegistry.get(type);
        if (k == null) {
            // try to dynamically load the support
            String classname = (String) getKitTypeRegistry().get(type);
	    ClassLoader loader = (ClassLoader) getKitLoaderRegistry().get(type);
            try {
		Class c;
		if (loader != null) {
		    c = loader.loadClass(classname);
		} else {
		    c = Class.forName(classname);
		}
                k = (EditorKit) c.newInstance();
                kitRegistry.put(type, k);
            } catch (Throwable e) {
                k = null;
            }
        }

        // create a copy of the prototype or null if there
        // is no prototype.
        if (k != null) {
            return (EditorKit) k.clone();
        }
        return null;
*/
    }



	/**Updates the installed keymap based upon the current settings, such as
		whether the text pane is currently paged.
	*/
	protected void updateKeymap()
	{
		if(isPaged())	//if we're in paged mode
		{
			setKeymap(getKeymap(PAGED_KEYMAP_NAME));	//install the paged keymap
		}
		else	//if we aren't in paged mode
		{
			setKeymap(getKeymap(XML_KEYMAP_NAME));	//install the normal keymap
		}
	}


    // --- Scrollable  ----------------------------------------

    /**
		 * Returns true if a viewport should always force the width of this
     * Scrollable to match the width of the viewport.
     *
     * @return true if a viewport should force the Scrollables width to match its own.
		 */
/*G***fix or delete
		public boolean getScrollableTracksViewportWidth() {
				return true;
		}
*/

	/* ***JTextComponent methods*** */

	/**Overrides the document being changed so that we can get fetch the new paged vew
		if necessary.
	@param e The document event.
	*/
	//G***is there a better place to put this?
/*G***del
	public void insertUpdate(DocumentEvent e)
	{
System.out.println("Inside XMLTextPage.insertUpdate(), fetching new paged view.");
		fetchPagedView();	//see if there's a paged view in the view hierarchy
		super.insertUpdate(e);	//do the default action
	}
*/

	/**Converts the given location in the model to a place in
		the view coordinate system.
	<p>This version provides access to the text UI version that allows a
		determination based upon a bias.</p>
	@param pos The local location in the model to translate (>=0).
  @return The coordinates as a rectangle.
	@exception BadLocationException  if the given position does not
		represent a valid location in the associated document.
	@see TextUI#modelToView
	*/
	public Rectangle modelToView(int pos, Position.Bias bias) throws BadLocationException
	{
		return getUI().modelToView(this, pos, bias);	//ask the UI for the correct view rectangle
	}

	/**Override of the <code>paint()</code> method to make sure a valid page is
		being shown.
	@param g The graphics object to be used for painting.
	*/
	public void paint(Graphics g)
	{
		if(isPaged()) //if we're paging our information
		{
			final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying at a time
			int pageIndex=getPageIndex();	//get the current page index
	Debug.trace("page count: ", getPageCount());
	Debug.trace("page index: ", pageIndex);
			pageIndex=(pageIndex/displayPageCount)*displayPageCount;	//make sure the page index is on the first of any page sets
			if(!isPaginating())	//if we're not paginating (otherwise, we wouldn't have an acccurate page count)
			{
				final int pageCount=getPageCount();	//get the page count
				if(pageIndex>=pageCount)	//if we're not showing any valid pages
					pageIndex=(pageCount-1/displayPageCount)*displayPageCount;	//go to the last set of displayed pages on the first page
			}
	Debug.trace("new page index: ", pageIndex);
			if(pageIndex!=getPageIndex())	//if we've decided to change the page index
				setPageIndex(pageIndex);	//change the page index
		}
		super.paint(g);	//paint normally
			//create a fake mouse move event so that the current cursor will be updated and the hyperlink events fired
				  //G***this is somewhat of a hack; do something better, such as having a locationChanged event on page changes and navigation (on non-paged compontents, for example)
		final MouseEvent mouseEvent=new MouseEvent(this, MouseEvent.MOUSE_MOVED,
			  System.currentTimeMillis(), 0,
				getMousePosition().x, getMousePosition().y, 0, false);
		processMouseMotionEvent(mouseEvent);  //process the mouse event
	}

	/* ***Page-related methods*** */

	/**@return <code>true</code> if this text page is currently in the process
		of being paginated.
	*/
	public boolean isPaginating()
	{
		return getPagedView()!=null && getPagedView().isPaginating()==true;	//return true if we have a paged view and it is currently paginating
	}

	/**@return The number of pages, if we have a paged view, or one.*/
	public int getPageCount()
	{
		return getPagedView()!=null ? getPagedView().getPageCount() : 1;
	}

	/**The index of the currently displayed page.*/
//G***del	private int PageIndex;

	/**@return The index of the currently displayed page.*/
	public int getPageIndex()
	{
		return getPagedView()!=null ? getPagedView().getPageIndex() : 0;
	}

	/**Sets the index of the currently displayed page.
	@param pageIndex The index of the new page to be displayed.
	*/
	public void setPageIndex(final int pageIndex)
	{
		if(getPagedView()!=null)	//we can only change pages when we have a paged view
		{
			final int oldPageIndex=getPageIndex();	//get our current page index
			if(pageIndex!=oldPageIndex) //if we're really changing pages
			{
				resetSearchPosition();  //reset our search offset since we're changing pages
				getPagedView().setPageIndex(pageIndex);	//set the new page index
			}
/*G***del if not needed
			final int newPageIndex=getPageIndex();	//get our new page index
System.out.println("XMLTextPane just changed the page index from: "+oldPageIndex+" to: "+newPageIndex);	//G***del
			if(oldPageIndex!=newPageIndex)	//if we actually changed pages
				firePageEvent(new PageEvent(this, newPageIndex));	//report that our page has changed
*/
		}
	}

	//G***fix this with the correct modelToView() stuff; comment
	public int getPageIndex(final int pos)
	{
		if(getPagedView()!=null)	//G***comment
			return getPagedView().getPageIndex(pos);  //G***comment
		else  //G***comment
			return -1;  //G***comment
	}

	/**The variable used to remember the number of pages to display in case there
		is no paged view, yet.*/
	private int DisplayPageCount=1;

		/**@return The number of pages to display at a time.
		@see XMLPagedView#getDisplayPageCount
		*/
		public int getDisplayPageCount()
		{
//G***del when works			return getPagedView()!=null ? getPagedView().getDisplayPageCount() : 1;	//return the number of displayed pages if we have a paged view, or 1 if not
			return DisplayPageCount;	//return our page count, which should always be the same as what we've set the paged view to be
		}

		/**Sets the number of pages to display at a time.
		@param displayPageCount The new number of pages to display at a time.
		@see XMLPagedView#setDisplayPageCount
		*/
		public void setDisplayPageCount(final int displayPageCount)
		{
			DisplayPageCount=displayPageCount;	//always keep a local copy of the display page count
			if(getPagedView()!=null)	//if we have a paged view
			{
				getPagedView().setDisplayPageCount(displayPageCount);	//tell it to update the number of pages it displays at a time
				invalidate();	//show that the text pane needs to be revalidated G***testing
			}
		}

	//G***fix this with the correct modelToView() stuff; make sure the error return value is correct
	public int getPageStartOffset(final int pageIndex)
	{
		return getPagedView()!=null ? getPagedView().getPageStartOffset(pageIndex) : -1;  //return the paged view's starting offset
	}

	//G***fix this with the correct modelToView() stuff; make sure the error return value is correct
	public int getPageEndOffset(final int pageIndex)
	{
		return getPagedView()!=null ? getPagedView().getPageEndOffset(pageIndex) : -1;  //return the paged view's ending offset
	}

	/**@return <code>true</code> if the specified page is one of the pages being
		displayed.
	*/
	public boolean isPageShowing(final int pageIndex)
	{
		return getPagedView().isPageShowing(pageIndex); //ask the paged view whether this page is showing
	}

	/**Advances to the next page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goNextPage()
	{
//G***del System.out.println("XMLTextPane.goNextPage()");	//G***del
		if(getPagedView()!=null)	//if we have a paged view
			getPagedView().goNextPage();	//tell it to go to the next page
	}

	/**Changes to the previous page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goPreviousPage()
	{
		if(getPagedView()!=null)	//if we have a paged view
			getPagedView().goPreviousPage();	//tell it to go to the previous page
	}

	/**Navigates to the specified URL. If the URL is already loaded, it is displayed.
		If the URL is outside the publication, the location is loaded into the
		default browser.
	@param url The destination URL.
	*/
/*G***fix or del	
	public void go(final URL url)
	{
			catch(URISyntaxException uriSyntaxException)
			{
				Debug.error(uriSyntaxException);	//G***fix				
			}
		
	}
*/	

	/**Navigates to the specified URI. If the URI is already loaded, it is displayed.
		If the URI is outside the publication, the location is loaded into the
		default browser.
	@param uri The destination URI.
	*/
	public void go(final URI uri)
	{
Debug.trace("Inside XMLTextPane.goURI()");	//G***del
		final Document document=getDocument();  //get the document associated with the text pane
		if(document instanceof XMLDocument) //if this is an XML document
		{
//G***del			final XMLDocument xmlDocument=(XMLDocument)document;  //case the document to an XML document
				//cast the document to an XML document and get the element that the URI represents, if possible
			final Element element=((XMLDocument)document).getElement(XMLStyleUtilities.TARGET_URI_ATTRIBUTE_NAME, uri);
			if(element!=null)	//if we found a matching element in the document
			{
/*G***del
try{
Debug.notify("For target ID: "+url+" the text is "+getOEBTextPane().getDocument().getText(element.getStartOffset(), 5)+" and the offset is: "+element.getStartOffset());
}catch(Ex
*/
			  final int offset=element.getStartOffset(); //get the starting position of the element
				go(offset);  //go to the beginning of the element
			}
			else  //if there is no matching element in the document
			{
				try
				{
					BrowserLauncher.openURL(uri.toString());	//G***testing; comment; decide if we want this done here or by the caller
				}
				catch(IOException e)  //if there is an IO exception browsing to the URI
				{
					Debug.error(e); //we don't expect to see this exception
				}
			}
		}
	}

	/**Navigates to the specified position.
	@param offset The new position to navigate to.
	*/
	public void go(final int offset)
	{
		final int pageIndex=getPagedView().getPageIndex(offset); //get the page index of the specified position
		if(pageIndex!=-1)
		{
		  setPageIndex(pageIndex);	//navigate to the specified page G***what if going to this page puts us on the wrong index?
		}
	}

	/**Searches the document for the given text and returns its first
		encountered offset. Searching begins after the last search position, if
		<code>getSearchOffset()</code>>=0. If the search position is -1, the
		beginning of the current page is used.
		Search offset and length are updated so that the results will be correctly
		highlighted.
	@param searchText The text for which to search. This version does a case
		insensitive comparison.
	@return The model offset of the first text match.
	*/
	public int search(final String searchText)
	{
		return search(searchText, NEXT_SEARCH_OFFSET); //search for the text at the next search offset
	}

	/**Searches the document for the given text and returns its first
		encountered offset. Searching begins at <code>searchOffset</code>, if it
		does not equal <code>NEXT_SEARCH_OFFSET</code>. Otherwise, searching begins
		after the last search position, if <code>getSearchOffset()</code>>=0. If
		the search position is -1, the beginning of the current page is used.
		Search offset and length are updated so that the results will be correctly
		highlighted.
	@param searchText The text for which to search. This version does a case
		insensitive comparison.
	@param searchOffset The offset at which the search should begin, or
		<code>NEXT_SEARCH_OFFSET</code> if the search should begin after the match
		position of the last search.
	@return The model offset of the first text match.
	*/
	public int search(final String searchText, int searchOffset)
	{
		final Document document=getDocument();  //get a reference to the document
		final int documentLength=document.getLength();  //find out how long the document is
		if(searchOffset==NEXT_SEARCH_OFFSET)  //if they want to start searching after the results of the last search
		{
			searchOffset=getSearchOffset(); //get the current search offset
			if(searchOffset>=0) //if we have a valid search offset (that is, the last search returned something valid
				++searchOffset; //we'll start searchig at a different location next time
			else  //if there was no search position last time, meaning we need to start from scratch
				searchOffset=getPageStartOffset(getPageIndex());  //start searching at the beginning of the first showing page
		}
		if(searchOffset<0 || searchOffset>=documentLength)  //if the search position is invalid
			return -1;  //we can't search outside the document range

		try
		{
			final String documentText=document.getText(0, documentLength).toUpperCase();  //G***fix; this seems very bad
	/*G***fix
			final Segment documentSegment=new Segment();  //create a segment to hold the entire text of the document
			document.getText(0, document.getLength(), documentSegment)
	*/
			searchOffset=documentText.indexOf(searchText.toUpperCase(), searchOffset); //G**testing
			if(searchOffset!=-1) //if a match was found
			{
				final int searchPageIndex=getPageIndex(searchOffset);  //get the page index of this offset
				if(!isPageShowing(searchPageIndex))  //if the match is on a page that isn't showing
					setPageIndex(searchPageIndex); //change to the page on which the match lies; this will reset our search position, but we will immediately update it
//G***del Debug.trace("Setting search offset to: "+searchOffset);  //G***del
				setSearchPosition(searchOffset, searchText.length());  //update the search position, which updates our highlights
//G***del Debug.trace("Search offset is now: "+getSearchOffset());  //G***del
			}
			return searchOffset;
		}
		catch(BadLocationException e) //since we're controlling everything, we should never get a bad location
		{
			return -1;  //show that the text was not found (but we should never encounter this error)
		}
	}

	/* ***Events*** */

	/**Invoked when a key has been typed.
		This event occurs when a key press is followed by a key release.
		Present to fulfill obligations of <code>KeyListener</code> interface.
	@param e The event generated from the key press and release.
	*/
//G***del when works	public void keyTyped(KeyEvent e) {}

	/**Invoked when a key has been pressed. Creates necessary paging functionality
		in response to paging keys.
	*/
/**G***del when works
	public void keyPressed(KeyEvent e)
	{
//G***fix			Debug.notify(KeyStroke.getKeyStrokeForEvent(e).toString()); //G***testing
	}
*/

	/**Invoked when a key has been released.
		Present to fulfill obligations of <code>KeyListener</code> interface.
	@param e The event generated from the key release.
	*/
//G***del when works	public void keyReleased(KeyEvent e) {}

	/**Adds a listener that will be notified when the page is changed.
	@param listener The object to be notified.
	*/
	public void addPageListener(PageListener listener)
	{
		pageListenerList.add(PageListener.class, listener);	//add this listener to our list
	}

	/**Removes a listener that no longer wants to be notified when the page is changed.
	@param listener The object to be removed.
	*/
	public void removePageListener(PageListener listener)
	{
		pageListenerList.remove(PageListener.class, listener);	//remove this listener from our list
	}

	/**Notifies all page listeners that the page has been changed.
	@param e The notification event.
	*/
	protected void firePageEvent(PageEvent e)
	{
		final Object[] listeners=pageListenerList.getListenerList();	//get an array of listeners, guaranteed to be non-null
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each of the listener classes (the fact that we're going from back to front is not significant)
		{
			if(listeners[i]==PageListener.class)	//if this is a page listener
				((PageListener)listeners[i+1]).pageChanged(e);	//tell this listener our displayed page has changed
		}
	}

	/**Called when the displayed page has changed in the paged view, so that we
		can forward the event up the chain.
	@param e The page event.
	*/
	public void pageChanged(PageEvent e)
	{
		firePageEvent(e);	//refire that event to our listeners G***perhaps do some manipulation here
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
		final Object[] listeners=progressListenerList.getListenerList();	//get the non-null array of listeners
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each listener, from last to first
		{
			if(listeners[i]==ProgressListener.class)	//if this is a progress listener (it should always be)
				((ProgressListener)listeners[i+1]).madeProgress(progressEvent);
     }
	}

	/**Invoked when progress has been made by, for example, the editor kit.
	@param e The event object representing the progress made.
	*/
	public void madeProgress(final ProgressEvent e)
	{
		fireMadeProgress(e);	//refire that event to our listeners G***perhaps do some manipulation here
	}

	/**Called when the mouse is dragged. This version ignores mouse dragging.
	@param mouseEvent The mouse event.
	*/
	public void mouseDragged(final MouseEvent mouseEvent)
	{
		setMousePosition(mouseEvent.getPoint());  //update our record of the mouse position
	}

	/**Called when the mouse is moved.
	@param mouseEvent The mouse event.
	*/
	public void mouseMoved(final MouseEvent mouseEvent)
	{
		setMousePosition(mouseEvent.getPoint());  //update our record of the mouse position
	}


	/**The position in the document the user once was	at. Useful for going "back".
	@author Garret Wilson
	*/
//G***del; Position now used	public static class HistoryPosition
//G***del; Position now used	{
		/**The name of the position.*/
//G***fix		private String name;

		/**The offset in the document this position represents.*/
//G***del; Position now used		private int offset;

		  /**@return The offset of the position.*/
//G***del; Position now used			public int getOffset() {return offset;}

		/**Creates a history position with a name and offset.
//G***fix		@param newName The name of the position.
		@param newOffset
		 */
//G***del; Position now used		public HistoryPosition(/*G***fix final String newName, */final int newOffset)
//G***del; Position now used		{
//G***del; Position now used			offset=newOffset; //set the offset
//G***del; Position now used		}

		/**@return The string representation of this history position.*/
/*G***fix
		public String toString()
		{
		  return name;
		}
*/

//G***del; Position now used	}

	//AppletContext methods

	/**Creates an audio clip.
	This method conforms to the <code>AppletContext</code> interface.
	@param URL An absolute URI giving the location of the audio clip.
	@return The audio clip at the specified URL.
	*/
	public AudioClip getAudioClip(final URL url)
	{
//G***del Debug.notify("Applet.getAudioClip(): "+url);  //G***del
		final Document document=getDocument();  //get the document associated with the text pane
		if(document instanceof XMLDocument) //if this is an XML document
		{
			final XMLDocument xmlDocument=(XMLDocument)document; //cast the document to an XML document
			try
			{
				final Clip clip=(Clip)xmlDocument.getResource(url.toString());	//get and open a clip to the audio
					//G***we need to fix this better; right now, we get a ClassCastException if they give a URL to an image, for instance
	//G***del Debug.trace("ready to start clip.");
				return new ClipAudioClip(clip); //create an audio clip from the clip and return it
			}
			catch(URISyntaxException uriSyntaxException)  //if there's a problem with the audio clip location
			{
				Debug.warn(uriSyntaxException);  //show that we can't load the clip G***fix better with Java console info
				return null;  //show that we couldn't load the audio clip
			}
			catch(IOException ioException)  //if there's a problem loading the audio clip
			{
				Debug.warn(ioException);  //show that we can't load the clip G***fix better with Java console info
				return null;  //show that we couldn't load the audio clip
			}
		}
		else  //if this is not an XML document, there's nothing we can do
			return null;  //show that we can
	}

    /**
     * Returns an <code>Image</code> object that can then be painted on
     * the screen. The <code>url</code> argument<code> </code>that is
     * passed as an argument must specify an absolute URL.
     * <p>
     * This method always returns immediately, whether or not the image
     * exists. When the applet attempts to draw the image on the screen,
     * the data will be loaded. The graphics primitives that draw the
     * image will incrementally paint on the screen.
     *
     * @param   url   an absolute URL giving the location of the image.
     * @return  the image at the specified URL.
     * @see     java.awt.Image
     */
    public Image getImage(URL url) {return null;} //G***fix

    /**
     * Finds and returns the applet in the document represented by this
     * applet context with the given name. The name can be set in the
     * HTML tag by setting the <code>name</code> attribute.
     *
     * @param   name   an applet name.
     * @return  the applet with the given name, or <code>null</code> if
     *          not found.
     */
    public Applet getApplet(String name) {return null;} //G***fix

    /**
     * Finds all the applets in the document represented by this applet
     * context.
     *
     * @return  an enumeration of all applets in the document represented by
     *          this applet context.
     */
    public Enumeration getApplets() {return null;} //G***fix

    /**
     * Replaces the Web page currently being viewed with the given URL.
     * This method may be ignored by applet contexts that are not
     * browsers.
     *
     * @param   url   an absolute URL giving the location of the document.
     */
    public void showDocument(URL url) {} //G***fix

    /**
     * Requests that the browser or applet viewer show the Web page
     * indicated by the <code>url</code> argument. The
     * <code>target</code> argument indicates in which HTML frame the
     * document is to be displayed.
     * The target argument is interpreted as follows:
     * <p>
     * <center><table border="3">
     * <tr><td><code>"_self"</code>  <td>Show in the window and frame that
     *                                   contain the applet.</tr>
     * <tr><td><code>"_parent"</code><td>Show in the applet's parent frame. If
     *                                   the applet's frame has no parent frame,
     *                                   acts the same as "_self".</tr>
     * <tr><td><code>"_top"</code>   <td>Show in the top-level frame of the applet's
     *                                   window. If the applet's frame is the
     *                                   top-level frame, acts the same as "_self".</tr>
     * <tr><td><code>"_blank"</code> <td>Show in a new, unnamed
     *                                   top-level window.</tr>
     * <tr><td><i>name</i><td>Show in the frame or window named <i>name</i>. If
     *                        a target named <i>name</i> does not already exist, a
     *                        new top-level window with the specified name is created,
     *                        and the document is shown there.</tr>
     * </table> </center>
     * <p>
     * An applet viewer or browser is free to ignore <code>showDocument</code>.
     *
     * @param   url   an absolute URL giving the location of the document.
     * @param   target   a <code>String</code> indicating where to display
     *                   the page.
     */
    public void showDocument(URL url, String target) {} //G***fix

    /**
     * Requests that the argument string be displayed in the
     * "status window". Many browsers and applet viewers
     * provide such a window, where the application can inform users of
     * its current state.
     *
     * @param   status   a string to display in the status window.
     */
    public void showStatus(String status) {} //G***fix

		/**
		 * Associates the specified stream with the specified key in this
		 * applet context. If the applet context previously contained a mapping 
		 * for this key, the old value is replaced. 
		 * <p>
		 * For security reasons, mapping of streams and keys exists for each 
		 * codebase. In other words, applet from one codebase cannot access 
		 * the streams created by an applet from a different codebase
		 * <p>
		 * @param key key with which the specified value is to be associated.
		 * @param stream stream to be associated with the specified key. If this
		 *               parameter is <code>null<code>, the specified key is removed 
		 *               in this applet context.
		 * @throws <code>IOException</code> if the stream size exceeds a certain
		 *         size limit. Size limit is decided by the implementor of this
		 *         interface.
		 * @since JDK1.4
		 */
		public void setStream(String key, InputStream stream) throws IOException {}	//G***fix

		/**
		 * Returns the stream to which specified key is associated within this 
		 * applet context. Returns <tt>null</tt> if the applet context contains 
		 * no stream for this key.  
		 * <p>
		 * For security reasons, mapping of streams and keys exists for each 
		 * codebase. In other words, applet from one codebase cannot access 
		 * the streams created by an applet from a different codebase
		 * <p>
		 * @return the stream to which this applet context maps the key
		 * @param key key whose associated stream is to be returned.
		 * @since JDK1.4
		 */
		public InputStream getStream(String key) {return null;}	//G***fix

		/**
		 * Finds all the keys of the streams in this applet context.
		 * <p>
		 * For security reasons, mapping of streams and keys exists for each 
		 * codebase. In other words, applet from one codebase cannot access 
		 * the streams created by an applet from a different codebase
		 * <p>
		 * @return  an Iterator of all the names of the streams in this applet 
		 *          context.
		 * @since JDK1.4
		 */
		public Iterator getStreamKeys() {return null;}	//G***fix


}
