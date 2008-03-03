package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.*;

import com.globalmentor.net.URLUtilities;
import com.globalmentor.util.Debug;

/**View that displays an image. The image reference is kept using a soft pointer
	so that when memory is low the JVM can reclaim the image memory. The image,
	therefore, can be loaded or reloaded at any time using <code>loadImage()</code>.
	A class should be derived from this class that correctly sets the image href,
	width, and height.
	This class was written referencing <code>javax.swing.text.html.ImageView</code>
	by Jens Alfke version 1.40 02/02/00, and the original version was based on
	code from that class.
@author Garret Wilson
@see javax.swing.text.html.ImageView
@see XMLObjectView#setHeight
@see XMLObjectView#setWidth
@see #setHRef
*/
public abstract class XMLImageView extends XMLObjectView implements ImageObserver	//G***fix, MouseListener, MouseMotionListener
{

	/**The reference to the image, which can be reclaimed if memory is running low.*/
	private SoftReference imageReference=null;

	/**Whether or not the image has started loading.
	@see #paint
	@see #imageUpdate
	*/
//G***del; not needed now that FRAMEBITS has been discovered	protected boolean startedLoading=false;
	protected boolean startedLoading=false;

	/**Whether or not the image has finished loading.
	@see #paint
	@see #imageUpdate
	*/
	protected boolean finishedLoading=false;

	/**The source of the image.*/
	private String href=null;

		/**@return The source of the image.*/
		public String getHRef() {return href;}

		/**Sets the source of the image.
		@param newHRef The new source of the image.
		*/
		protected void setHRef(final String newHRef) {href=newHRef;}

    // --- Attribute Values ------------------------------------------
/*G***del if not needed
    public static final String
    	TOP = "top",
    	TEXTTOP = "texttop",
    	MIDDLE = "middle",
    	ABSMIDDLE = "absmiddle",
    	CENTER = "center",
    	BOTTOM = "bottom";
*/


	/**Creates a view that represents an image.
	@param element The element for which to create the view.
	*/
  public XMLImageView(final Element element)
	{
   	super(element);	//do the default constructing
	}

	/**Loads the image immediately and returns it.
	@return The loaded image.
	*/
/*G***fix after deciding how this method should be used
	protected Image loadImage()
	{

			final Image image=(Image)document.getResource(src);	//get the image resource G***check to make sure what is returned is really an image
		  imageReference=new SoftReference(image);  //create a soft reference to the image to store locally

	}
*/

	/**Sets whether or not an view is showing. This version includes the parent
		functionality, and sets the <code>startedLoading</code> and
		<code>finishedLoading</code> variables to <code>false</code> to reflect
		the fact that the image will need to possibly be reloaded. The internal
		hard reference to the image is removed, so that the image memory may be
		reclaimed if needed.
	@param newShowing <code>true</code> if the view is beginning to be shown,
		<code>false</code> if the view is beginning to be hidden.
	@see #isShowing
	*/
	public void setShowing(final boolean newShowing)
	{
Debug.trace();  //G***del
		super.setShowing(newShowing); //do the default functionality
		if(!newShowing) //if we're being hidden
		{
			shownImage=null; //remove our hard reference to the image so that its memory may be reclaimed if needed
			startedLoading=false; //show that the image hasn't started loading, yet
			finishedLoading=false;  //show that the image hasn't finished loading, either
		}
	}

	/**Returns the image to be drawn. Because memory usage may cause the image
		memory to be reclaimed, this method may reload the image.
		<code>initialize()</code> must therefore have first been called to
		appropriately set the image information.
	@exception URISyntaxException Thrown if the image href does not allow a syntactically
		correct URI to be contructed.
	@exception IOException Thrown if there is an error getting the image, usually
		because the image needed to be loaded but could not be.
	@see #freeImage
	@see #initialize
	*/
	protected Image getImage() throws URISyntaxException, IOException
	{
Debug.trace("XMLImageView.getImage(): ", getHRef()); //G***del
		if(shownImage!=null)  //G***testing; comment
			return shownImage;  //G***comment
		else
		{
			//G***put some sort of assert that the image reference is not equal to null or something; or maybe this isn't needed, since the constructor calls initialize()
			Image image=imageReference!=null ? (Image)imageReference.get() : null;  //get the image to which the soft reference refers
			if(image==null) //if we have not loaded the image yet, or the image memory has been reclaimed
			{ //G***put all this into a separate function
				Debug.trace("loading image"); //G***del
				if(imageReference!=null)  //if we used to have a reference to an image, but memory was running low and it was reclaimed
				{
					Debug.trace("Image memory reclaimed, reloading."); //G***del
					System.gc();  //indicate that garbage collection should occur to attempt to give us more memory G***testing memory
				}
				startedLoading=false; //show that the image hasn't started loading, yet
				finishedLoading=false;  //show that the image hasn't finished loading, either
				final XMLDocument document=(XMLDocument)getDocument();  //get the document used to load resources G***make sure this is an XML document
					//get the href, taking into account that the href is relative to this file's base URL
//G***del System.out.println("image href: "+getHRef());  //G***del
			  final String href=XMLStyleUtilities.getBaseRelativeHRef(getAttributes(), getHRef());
//G***del System.out.println("image base relateive href: "+href);  //G***del
				image=(Image)document.getResource(href);	//get the image resource G***check to make sure what is returned is really an image
				imageReference=new SoftReference(image);  //create a soft reference to the image to store locally
			}
			return image; //return the image
		}
	}

	/**Frees the image, if one has been loaded. This allows the garbage collector
		to reclaim the memory used by the image, which will cause the image to be
		reloaded the next time <code>getImage()</code> is called.
	@see #getImage
	*/
	protected void freeImage()
	{
		if(imageReference!=null)  //if there is a memory reference
		{
			imageReference.clear();   //clear the reference to the image
			imageReference=null;  //remove the image reference; this frees more memory, and it would have to have been recreated, anyway
		}
	}

	/**The image currently being painted. This hard reference ensures that, while
		this view is showing, the image memory will not be reclaimed. When the view
		is not showing, this image will be set to false so that the image memory
		may be recollected if needed.
	*/
	protected Image shownImage=null;

    /**
     * Fetches the attributes to use when rendering.  This is
     * implemented to multiplex the attributes specified in the
     * model with a StyleSheet.
     */
/*G***fix
    public AttributeSet getAttributes() {
	return attr;
    }
*/

    /** Is this image within a link? */
/*G***fix
    boolean isLink( ) {
	return isLink;
    }
*/

    /** Returns the size of the border to use. */
/*G***fix
    int getBorder( ) {
        return border;
    }
*/

    /** Returns the amount of extra space to add along an axis. */
/*G***fix
    int getSpace( int axis ) {
	if (axis == X_AXIS) {
	    return xSpace;
	}
	return ySpace;
    }
*/

    /** Returns the border's color, or null if this is not a link. */
/*G***fix or delete
    Color getBorderColor( ) {
    	StyledDocument doc = (StyledDocument) getDocument();
        return doc.getForeground(getAttributes());
    }
*/

/*G***fix
    boolean hasPixels( ImageObserver obs ) {
        return fImage != null && fImage.getHeight(obs)>0
			      && fImage.getWidth(obs)>0;
    }
*/


    /** Return a URL for the image source,
        or null if it could not be determined. */
/*G***fix
    private URL getSourceURL( ) {
 	String src = "file:/D:/Projects/oeb/understandingoeb/oebobjects_classdiagram.jpg";
//G***fix 	String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
 	if( src==null ) return null;

//G***fix	URL reference = ((HTMLDocument)getDocument()).getBase();
        try {
//G***fix 	    URL u = new URL(reference,src);
 	    URL u = new URL(src);
	    return u;
        } catch (MalformedURLException e) {
	    return null;
        }
    }
*/

    /** Look up an integer-valued attribute. <b>Not</b> recursive. */
/*G***fix
    private int getIntAttr(HTML.Attribute name, int deflt ) {
    	AttributeSet attr = fElement.getAttributes();
    	if( attr.isDefined(name) ) {		// does not check parents!
    	    int i;
 	    String val = (String) attr.getAttribute(name);
 	    if( val == null )
 	    	i = deflt;
 	    else
 	    	try{
 	            i = Math.max(0, Integer.parseInt(val));
 	    	}catch( NumberFormatException x ) {
 	    	    i = deflt;
 	    	}
	    return i;
	} else
	    return deflt;
    }
*/

    /** My attributes may have changed. */
/*G***del if we don't need
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
if(DEBUG) System.out.println("OEBImageView: changedUpdate begin...");
    	super.changedUpdate(e,a,f);

    	int height = fHeight;
    	int width  = fWidth;

    	initialize(getElement());

    	boolean hChanged = fHeight!=height;
    	boolean wChanged = fWidth!=width;
    	if( hChanged || wChanged) {
    	    if(DEBUG) System.out.println("OEBImageView: calling preferenceChanged");
	    preferenceChanged(null,hChanged,wChanged);
    	}
    }
*/

	/**Paints the image.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see XMLObjectView#paint
	*/
	public void paint(Graphics graphics, Shape allocation)
	{
Debug.trace("(before super) paint() {0}, isShowing: {1} startedLoading: {2} finishedLoading: {3}", new Object[]{getHRef(), new Boolean(isShowing()), new Boolean(startedLoading), new Boolean(finishedLoading)});
		super.paint(graphics, allocation);  //do the default painting
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		  //set pixel interpolation to its highest quality G***probably do this conditionally, based on some sort of flag
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
Debug.trace("(after super) paint() {0}, isShowing: {1} startedLoading: {2} finishedLoading: {3}", new Object[]{getHRef(), new Boolean(isShowing()), new Boolean(startedLoading), new Boolean(finishedLoading)});
//G***del Debug.trace("imageView.paint() currentWidth: "+currentWidth+" currentHeight: "+currentHeight);
//G***del when works		paintingVisible=true;	//show that calls to getComponent().repaint() are actually causes this image view to get repainted
//G***fix	Color oldColor = g.getColor();
		  //G***switch to using getBounds()
		final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();  //get the bounding rectangle of the painting area
//G***del Debug.trace("Inside OEBImageView.paint(), width: "+currentWidth+" height: "+currentHeight+" alloc width: "+alloc.width+" alloc height: "+alloc.height);	//G***del; testing image
/*G***fix
	fBounds.setBounds(alloc);
        int border = getBorder();
*/
/*G***del when works
		int x = fBounds.x + border + getSpace(X_AXIS);
		int y = fBounds.y + border + getSpace(Y_AXIS);
		int width = fWidth;
		int height = fHeight;
*/
		final int x=rectangle.x;	//G***comment
		final int y=rectangle.y;
		final int width=getCurrentWidth();  //get the current image width
		final int height=getCurrentHeight();  //get the current image height

//G***fix	int sel = getSelectionState();

	// Make sure my Component is in the right place:
/*
	if( fComponent == null ) {
	    fComponent = new Component() { };
	    fComponent.addMouseListener(this);
	    fComponent.addMouseMotionListener(this);
	    fComponent.setCursor(Cursor.getDefaultCursor());	// use arrow cursor
	    fContainer.add(fComponent);
	}
	fComponent.setBounds(x,y,width,height);
	*/
	// If no pixels yet, draw gray outline and icon:
/*G***fix
	if( ! hasPixels(this) ) {
	    g.setColor(Color.lightGray);
	    g.drawRect(x,y,width-1,height-1);
	    g.setColor(oldColor);
*/
/*G***fix
	    loadIcons();
	    Icon icon = fImage==null ?sMissingImageIcon :sPendingImageIcon;
	    if( icon != null )
	        icon.paintIcon(getContainer(), g, x, y);
	}
*/

	// Draw image:
	try
	{
//G***del when works		final Image image=getImage(); //get the image, which may include relading it
		shownImage=getImage(); //get the image, which may include relading it; this will for now set a hard reference to the image so that the image memory will not be reclaimed while it it showing
		if(shownImage!=null )
		{
Debug.trace("got the image");
	//G***fix		fImageIcon.paintIcon((Component)getContainer(), g, x, y);	//G***testing

			if(finishedLoading || !startedLoading)	//if we've finished loading the image, or we haven't even started loading it, yet
			{
Debug.trace("ready to draw image with current width {0} and current height {1}", new Object[]{new Integer(getCurrentWidth()), new Integer(getCurrentHeight())});
				//draw the image, which will start loading the image if it isn't loaded yet
				//note that many small images apparently never call imageUpdate(), meaning
				//  this function will return with the image already loaded without
				//  imageUpdate() being called, so we have to manipulate the
				//  finishedLoading and startedLoading variables here as well
//G***bring back; testing				finishedLoading=g.drawImage(image, x, y, alloc.width, alloc.height, this);
Debug.trace("Painting image "+href+" at "+x+", "+y+" width "+getCurrentWidth()+" height "+getCurrentHeight()); //G***del
				finishedLoading=graphics.drawImage(shownImage, x, y, getCurrentWidth(), getCurrentHeight(), this);
	//G***fix or del			final boolean isImageDrawn=g.drawImage(fImage, x, y, alloc.width, alloc.height, this);
	//G***del when works			final boolean isImageDrawn=g.drawImage(fImage, x, y, width/2, height/2, this);

				if(!startedLoading && !finishedLoading) //if the image has not yet started loading (and loading hasn't already finished)
				{
					((Graphics2D)graphics).setPaint(Color.black);
					graphics.setFont(new Font("Arial", Font.PLAIN, 14));  //G***fix all this; use a constant
					final String statusString="Loading image...";  //G***fix; i18n; comment
					final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
							//G***probably make sure that it is antialiased, here
					final Rectangle2D statusBounds=graphics2D.getFont().getStringBounds(statusString, fontRenderContext); //get the bounds of the status string
					final int statusX=x;  //find out where we would paint the status
					final int statusY=y+20;
						//if the string we would draw doesn't go outside our image
					if(statusX+statusBounds.getWidth()<x+getCurrentWidth() && statusY+statusBounds.getHeight()<x+getCurrentHeight())
					{
						graphics.drawString(statusString, statusX, statusY);	//G***testing; i18n
					}

	//G***del if not needed				startedLoading=true;  //G***testing
				}
				//G***should we update startedLoading for consistency, since finishedLoading might be set without startedLoading being set?
			}

	//G***del Debug.trace("image drawn: "+isImageDrawn);	//G***del

//G***fix		g.drawImage(fImage,x, y,width,height,this);


//G***fix	    g.drawImage(fImage, x, y, (int)getParent().getPreferredSpan(X_AXIS)/3, height, this);
	    // Use the following instead of g.drawImage when
	    // BufferedImageGraphics2D.setXORMode is fixed (4158822).

	    //  Use Xor mode when selected/highlighted.
	    //! Could darken image instead, but it would be more expensive.
/*
	    if( sel > 0 )
	    	g.setXORMode(Color.white);
	    g.drawImage(fImage,x, y,
	    		width,height,this);
	    if( sel > 0 )
	        g.setPaintMode();
*/
		}
	}
	catch(URISyntaxException uriSyntaxException)  //if there was an error getting the image G***probably set some sort of flag so that we won't try to load it again next time
	{
		Debug.error(uriSyntaxException); //report the error
		((Graphics2D)graphics).setPaint(Color.black);  //G***fix all this
		graphics.setFont(new Font("Arial", Font.PLAIN, 14));
		graphics.drawString("Error loading image.", x, y+20);	//G***testing; i18n
	}
	catch(IOException ioException)  //if there was an error getting the image G***probably set some sort of flag so that we won't try to load it again next time
	{
		Debug.error(ioException); //report the error
		((Graphics2D)graphics).setPaint(Color.black);  //G***fix all this
		graphics.setFont(new Font("Arial", Font.PLAIN, 14));
		graphics.drawString("Error loading image.", x, y+20);	//G***testing; i18n
	}

	// If selected exactly, we need a black border & grow-box:
/*G***del or fix
	Color bc = getBorderColor();
	if( sel == 2 ) {
	    // Make sure there's room for a border:
	    int delta = 2-border;
	    if( delta > 0 ) {
	    	x += delta;
	    	y += delta;
	    	width -= delta<<1;
	    	height -= delta<<1;
	    	border = 2;
	    }
	    bc = null;
	    g.setColor(Color.black);
	    // Draw grow box:
	    g.fillRect(x+width-5,y+height-5,5,5);
	}
*/

	// Draw border:
/*G***del or fix
	if( border > 0 ) {
	    if( bc != null ) g.setColor(bc);
	    // Draw a thick rectangle:
	    for( int i=1; i<=border; i++ )
	        g.drawRect(x-i, y-i, width-1+i+i, height-1+i+i);
	    g.setColor(oldColor);
	}
*/
    }

    /** Request that this view be repainted.
        Assumes the view is still at its last-drawn location. */
/*G***fix
    protected void repaint( long delay ) {
    	if( fContainer != null && fBounds!=null ) {
	    fContainer.repaint(delay,
	   	      fBounds.x,fBounds.y,fBounds.width,fBounds.height);
    	}
    }
*/

    /** Determines whether the image is selected, and if it's the only thing selected.
    	@return  0 if not selected, 1 if selected, 2 if exclusively selected.
    		 "Exclusive" selection is only returned when editable. */
/*G***fix
    protected int getSelectionState( ) {
    	int p0 = fElement.getStartOffset();
    	int p1 = fElement.getEndOffset();
	if (fContainer instanceof JTextComponent) {
	    JTextComponent textComp = (JTextComponent)fContainer;
	    int start = textComp.getSelectionStart();
	    int end = textComp.getSelectionEnd();
	    if( start<=p0 && end>=p1 ) {
		if( start==p0 && end==p1 && isEditable() )
		    return 2;
		else
		    return 1;
	    }
	}
    	return 0;
    }

    protected boolean isEditable( ) {
    	return fContainer instanceof JEditorPane
    	    && ((JEditorPane)fContainer).isEditable();
    }
*/

    /** Returns the text editor's highlight color. */
/*G***fix
    protected Color getHighlightColor( ) {
    	JTextComponent textComp = (JTextComponent)fContainer;
    	return textComp.getSelectionColor();
    }
*/

    // --- Progressive display ---------------------------------------------

    // This can come on any thread. If we are in the process of reloading
    // the image and determining our state (loading == true) we don't fire
    // preference changed, or repaint, we just reset the fWidth/fHeight as
    // necessary and return. This is ok as we know when loading finishes
    // it will pick up the new height/width, if necessary.
/*G***del if not needed
    public boolean imageUpdate( Image img, int flags, int x, int y,
    				int width, int height ) {
    	if( fImage==null || fImage != img )
    	    return false;

    	// Bail out if there was an error:
        if( (flags & (ABORT|ERROR)) != 0 ) {
            fImage = null;
            repaint(0);
            return false;
        }
        // Resize image if necessary:
	short changed = 0;
        if( (flags & ImageObserver.HEIGHT) != 0 )
*/
/*G***fix
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT) ) {
		changed |= 1;
            }
*/
//G***del if not needed			changed |= 1;
/*G***fix
        if( (flags & ImageObserver.WIDTH) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.WIDTH) ) {
		changed |= 2;
            }
*/
/*G***del if not needed
		changed |= 2;
	synchronized(this) {
	    if ((changed & 1) == 1) {
		fWidth = width;
	    }
	    if ((changed & 2) == 2) {
		fHeight = height;
	    }
	    if (loading) {
		// No need to resize or repaint, still in the process of
		// loading.
		return true;
	    }
	}
        if( changed != 0 ) {
            // May need to resize myself, asynchronously:
            if( DEBUG ) System.out.println("OEBImageView: resized to "+fWidth+"x"+fHeight);

	    Document doc = getDocument();
	    try {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readLock();
	      }
	      preferenceChanged(null,true,true);
	    } finally {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readUnlock();
	      }
	    }

	    return true;
        }

	// Repaint when done or when new pixels arrive:
	if( (flags & (FRAMEBITS|ALLBITS)) != 0 )
	    repaint(0);
	else if( (flags & SOMEBITS) != 0 )
	    if( sIsInc )
	        repaint(sIncRate);

        return ((flags & ALLBITS) == 0);
    }
*/
					  /*
    /**
     * Static properties for incremental drawing.
     * Swiped from Component.java
     * @see #imageUpdate
     */
//G***fix    private static boolean sIsInc = true;
//G***fix    private static int sIncRate = 100;

    // --- Layout ----------------------------------------------------------


    /**
     * Determines the resizability of the view along the
     * given axis.  A value of 0 or less is not resizable.
     *
     * @param axis View.X_AXIS or View.Y_AXIS
     * @return the weight
     */
/*G***fix after figuring out what can be returned
    public int getResizeWeight(int axis) {
	return 0;
    }
*/

    /** Change the size of this image. This alters the HEIGHT and WIDTH
    	attributes of the Element and causes a re-layout. */
/*G***fix
    protected void resize( int width, int height ) {
    	if( width==fWidth && height==fHeight )
    	    return;

    	fWidth = width;
    	fHeight= height;

    	// Replace attributes in document:
	MutableAttributeSet attr = new SimpleAttributeSet();
*/
/*G***fix
	attr.addAttribute(HTML.Attribute.WIDTH ,Integer.toString(width));
	attr.addAttribute(HTML.Attribute.HEIGHT,Integer.toString(height));
*/
/*G***fix
	((StyledDocument)getDocument()).setCharacterAttributes(
			fElement.getStartOffset(),
			fElement.getEndOffset(),
			attr, false);
    }
*/

    // --- Mouse event handling --------------------------------------------

    /** Select or grow image when clicked. */
/*G***fix
    public void mousePressed(MouseEvent e){
    	Dimension size = fComponent.getSize();
    	if( e.getX() >= size.width-7 && e.getY() >= size.height-7
    			&& getSelectionState()==2 ) {
    	    // Click in selected grow-box:
    	    if(DEBUG)System.out.println("OEBImageView: grow!!! Size="+fWidth+"x"+fHeight);
    	    Point loc = fComponent.getLocationOnScreen();
    	    fGrowBase = new Point(loc.x+e.getX() - fWidth,
    	    			  loc.y+e.getY() - fHeight);
    	    fGrowProportionally = e.isShiftDown();
    	} else {
    	    // Else select image:
    	    fGrowBase = null;
    	    JTextComponent comp = (JTextComponent)fContainer;
    	    int start = fElement.getStartOffset();
    	    int end = fElement.getEndOffset();
    	    int mark = comp.getCaret().getMark();
    	    int dot  = comp.getCaret().getDot();
    	    if( e.isShiftDown() ) {
    	    	// extend selection if shift key down:
    	    	if( mark <= start )
    	    	    comp.moveCaretPosition(end);
    	    	else
    	    	    comp.moveCaretPosition(start);
    	    } else {
    	    	// just select image, without shift:
    	    	if( mark!=start )
    	            comp.setCaretPosition(start);
    	        if( dot!=end )
    	            comp.moveCaretPosition(end);
    	    }
    	}
    }
*/

    /** Resize image if initial click was in grow-box: */
/*G***fix
    public void mouseDragged(MouseEvent e ) {
    	if( fGrowBase != null ) {
    	    Point loc = fComponent.getLocationOnScreen();
    	    int width = Math.max(2, loc.x+e.getX() - fGrowBase.x);
    	    int height= Math.max(2, loc.y+e.getY() - fGrowBase.y);

    	    if( e.isShiftDown() && fImage!=null ) {
    	    	// Make sure size is proportional to actual image size:
    	    	float imgWidth = fImage.getWidth(this);
    	    	float imgHeight = fImage.getHeight(this);
    	    	if( imgWidth>0 && imgHeight>0 ) {
    	    	    float prop = imgHeight / imgWidth;
    	    	    float pwidth = height / prop;
    	    	    float pheight= width * prop;
    	    	    if( pwidth > width )
    	    	        width = (int) pwidth;
    	    	    else
    	    	        height = (int) pheight;
    	    	}
    	    }

    	    resize(width,height);
    	}
    }
*/

/*G***fix
    public void mouseReleased(MouseEvent e){
    	fGrowBase = null;
    	//! Should post some command to make the action undo-able
    }
*/

    /** On double-click, open image properties dialog. */
/*G***fix
    public void mouseClicked(MouseEvent e){
    	if( e.getClickCount() == 2 ) {
    	    //$ IMPLEMENT
    	}
    }

    public void mouseEntered(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e ) {
    }
    public void mouseExited(MouseEvent e){
    }
*/

    // --- Static icon accessors -------------------------------------------

//G***fix    private Icon makeIcon(final String gifFile) throws IOException {
        /* Copy resource into a byte array.  This is
         * necessary because several browsers consider
         * Class.getResource a security risk because it
         * can be used to load additional classes.
         * Class.getResourceAsStream just returns raw
         * bytes, which we can convert to an image.
         */
/*G***fix
	InputStream resource = HTMLEditorKit.getResourceAsStream(gifFile);

        if (resource == null) {
            System.err.println(OEBImageView.class.getName() + "/" +
                               gifFile + " not found.");
            return null;
        }
        BufferedInputStream in =
            new BufferedInputStream(resource);
        ByteArrayOutputStream out =
            new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
        in.close();
        out.flush();

        buffer = out.toByteArray();
        if (buffer.length == 0) {
            System.err.println("warning: " + gifFile +
                               " is zero-length");
            return null;
        }
        return new ImageIcon(buffer);
    }
*/

/*G***fix
    private void loadIcons( ) {
        try{
            if( sPendingImageIcon == null )
            	sPendingImageIcon = makeIcon(PENDING_IMAGE_SRC);
            if( sMissingImageIcon == null )
            	sMissingImageIcon = makeIcon(MISSING_IMAGE_SRC);
	}catch( Exception x ) {
	    System.err.println("OEBImageView: Couldn't load image icons");
	}
    }
*/

/*G***fix
    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }
*/

    // --- member variables ------------------------------------------------

//G***fix    private AttributeSet attr;
//G***fix    private Element   fElement;
//G***del when works		private Image     fImage;

//G***fix    private Container fContainer;
//G***fix    private Rectangle fBounds;
//G***fix    private Component fComponent;
//G***fix    private Point     fGrowBase;        // base of drag while growing image
//G***fix    private boolean   fGrowProportionally;	// should grow be proportional?
    /** Set to true, while the receiver is locked, to indicate the reciever
     * is loading the image. This is used in imageUpdate. */
//G***fix    private boolean   loading;
//G***fix    private boolean   isLink;
//G***fix    private int       border;
//G***fix    private int       xSpace;
//G***fix    private int       ySpace;

    // --- constants and static stuff --------------------------------

/*G***fix
    private static Icon sPendingImageIcon,
    			sMissingImageIcon;
    private static final String
        PENDING_IMAGE_SRC = "icons/image-delayed.gif",  // both stolen from HotJava
        MISSING_IMAGE_SRC = "icons/image-failed.gif";
*/

//G***test    private static final boolean DEBUG = false;

//G***del    private static final boolean DEBUG = true;	//G***del; testing


    //$ move this someplace public
//G***fix    static final String IMAGE_CACHE_PROPERTY = "imageCache";

    // Height/width to use before we know the real size:
/*G***fix
    private static final int
        DEFAULT_WIDTH = 32,
        DEFAULT_HEIGHT= 32,
    // Default value of BORDER param:      //? possibly move into stylesheet?
        DEFAULT_BORDER=  2;
*/


	/**Called when information about an image which was previously requested
		using an asynchronous interface becomes available.
	@param img The image being observed.
	@param infoflags The bitwise inclusive OR of the following flags:
		<code>WIDTH</code>, <code>HEIGHT</code>, <code>PROPERTIES</code>,
		<code>SOMEBITS</code>, <code>FRAMEBITS</code>, <code>ALLBITS</code>,
		<code>ERROR</code>, <code>ABORT</code>.
 	@param x The <i>x</i> coordinate.
	@param y The <i>y</i> coordinate.
	@param width The width.
	@param height The height.
	@return <code>false</code> if the infoflags indicate that the image is
		completely loaded; <code>true</code> otherwise.
	*/
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)	//G***testing
	{
//G***del Debug.trace(getHRef());  //G***del
/*G***del
Debug.trace("imageUpdate() {0}, infoflags: {1}"+
		" isShowing: {2} finishedLoading: {3}\n parent: {4}\n parent parent: {5}"+
		"\n parent parent parent: {6}\n container: {7}", new Object[]
			{
				getHRef(),
				new Integer(infoflags),
				new Boolean(isShowing()),
				new Boolean(finishedLoading),
				(getParent()!=null ? getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null ? getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getClass().getName() : "null"),
				getContainer()
			}
		);
*/
/*G***fix
		if((infoflags & SOMEBITS)!=0)	//if there are some bits coming in, the image has started loading
			startedLoading=true;	//show that the image has started loading G***what if, for a small image,

		if(

		if(!infoflags & ALLBITS)!=0)	//if all bits have been loaded
*/
//G***fix Debug.trace("imageUpdate() visible: "+paintingVisible+" Started loading: "+startedLoading+" Finished loading: "+finishedLoading);
/*G***del
Debug.trace("Parent: "+getParent());  //G***del; testing
if(getParent()!=null) //G***del
{
	Debug.trace("Parent's parent: "+getParent().getParent());  //G***del; testing
	if(getParent().getParent()!=null) //G***del
		Debug.trace("Parent's parent's parent: "+getParent().getParent().getParent());  //G***del; testing
}
Debug.trace("Container: "+getContainer());  //G***del; testing
*/
//G***del Debug.trace(getHRef()+" allbits: ", infoflags & ALLBITS);
//G***del Debug.trace(getHRef()+" framebits: ", infoflags & FRAMEBITS);
		startedLoading=true;	//if imageUpdate() is ever called, we've at least started loading the image
		if((infoflags & (ALLBITS|FRAMEBITS))!=0)	//if we at any time receive all the bits, or if we're suddenly receiving frames from a multiple frame image, we've finished loading the image
			finishedLoading=true;	//show that we've finished loading the image
		if(finishedLoading) //only repaint the image if it has finished loading
		{
Debug.trace("Finished loading ", getHRef());
/*G***del
Debug.trace("image finished loading, ready to repaint\n{0}, infoflags: {1}"+
		" isShowing: {2} finishedLoading: {3}\n parent: {4}\n parent parent: {5}"+
		"\n parent parent parent: {6}\n container: {7}", new Object[]
			{
				getHRef(),
				new Integer(infoflags),
				new Boolean(isShowing()),
				new Boolean(finishedLoading),
				(getParent()!=null ? getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null ? getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getClass().getName() : "null"),
				getContainer()
			}
		);
*/
//G***del when works			if(paintingVisible)	//if calls to getComponent().repaint() are actually causes this image view to get repainted
			if(isShowing())	//if this view is showing
			{
Debug.trace("Is showing ", getHRef());
//G***del Debug.trace("*********OEBImageView.imageUpdate(), SOMEBITS: "+(infoflags & SOMEBITS)+" ALLBITS: "+(infoflags & ALLBITS)+" FRAMEBITS: "+(infoflags & FRAMEBITS));	//G***del
/*G***del; Debug no longer has a format trace method
Debug.trace("image finished loading, ready to repaint\n{0}, infoflags: {1}"+
		" isShowing: {2} finishedLoading: {3}\n parent: {4}\n parent parent: {5}"+
		"\n parent parent parent: {6}"+
		"\n parent parent parent parent: {7}"+
		"\n parent parent parent parent parent: {8}\n container: {9}", new Object[]
			{
				getHRef(),
				new Integer(infoflags),
				new Boolean(isShowing()),
				new Boolean(finishedLoading),
				(getParent()!=null ? getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null ? getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getParent().getParent().getClass().getName() : "null"),
				getContainer()
			}
		);
*/
				if(getContainer()!=null)  //G***testing
				{
Debug.trace("Have container ", getHRef());
//G***del when works					paintingVisible=false;	//for our next repaint force paint() to prove once more that it is still getting called
				  final Rectangle bounds=getBounds(); //get our painting bounds
Debug.trace(getHRef()+" painting bounds: ", bounds);
/*G***del; gives wrong coordinates
					bounds.x=x; //G***testing image repaint
					bounds.y=y;
*/
//G***del Debug.trace("Found container, ready to call repaint with bounds: "+bounds);
				  if(bounds.width==0 || bounds.height==0) //G***testing; kludge to compensate for table layout errors
					{
						bounds.width=getCurrentWidth();  //get the current image width
						bounds.height=getCurrentHeight();  //get the current image height
					}
Debug.trace("Repainting image "+href+" with bounds: ", bounds); //G***del
						//since the image is still visible (and imageUpdate() is still being called), repaint the image -- but only the areas within our bounds
					getContainer().repaint(bounds.x, bounds.y, bounds.width, bounds.height);  //repaint only the areas within our bounds
				}
			}
		}
		final boolean isAnimation=(infoflags & FRAMEBITS)!=0;  //see if this image has multiple frames
		final boolean isFinished=(infoflags & (ALLBITS|ABORT))!=0;  //see if loading has finished or has been aborted
			//we want to keep getting image data until we're finished, or until a multi-frame image is hidden
			//(put another way, we want to continue loading non-animation images, even if they are hidden
		return !isFinished && (isShowing() || !isAnimation);
//G***del		return ((infoflags & (ALLBITS|ABORT)) == 0) && isShowing();  //G***testing isShowing()
	}


}