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

package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import javax.swing.text.*;
import javax.swing.text.html.ImageView;

import com.globalmentor.log.Log;

/**View that displays an image. The image reference is kept using a soft pointer
	so that when memory is low the JVM can reclaim the image memory. The image,
	therefore, can be loaded or reloaded at any time using <code>loadImage()</code>.
	<p>A class should be derived from this class that correctly sets the image href,
	width, and height.</p>
	<p>This class was written referencing {@link ImageView}
	by Jens Alfke version 1.40 02/02/00, and the original version was based on
	code from that class.</p>
@author Garret Wilson
@see ImageView
@see XMLObjectView#setHeight(int)
@see XMLObjectView#setWidth(int)
@see #setHRef(String)
*/
public abstract class XMLImageView extends XMLObjectView implements ImageObserver	//TODO fix, MouseListener, MouseMotionListener
{

	/**The reference to the image, which can be reclaimed if memory is running low.*/
	private SoftReference imageReference=null;

	/**Whether or not the image has started loading.
	@see #paint
	@see #imageUpdate
	*/
//TODO del; not needed now that FRAMEBITS has been discovered	protected boolean startedLoading=false;
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
/*TODO del if not needed
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
/*TODO fix after deciding how this method should be used
	protected Image loadImage()
	{

			final Image image=(Image)document.getResource(src);	//get the image resource TODO check to make sure what is returned is really an image
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
Log.trace();  //TODO del
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
Log.trace("XMLImageView.getImage(): ", getHRef()); //TODO del
		if(shownImage!=null)  //TODO testing; comment
			return shownImage;  //TODO comment
		else
		{
			//TODO put some sort of assert that the image reference is not equal to null or something; or maybe this isn't needed, since the constructor calls initialize()
			Image image=imageReference!=null ? (Image)imageReference.get() : null;  //get the image to which the soft reference refers
			if(image==null) //if we have not loaded the image yet, or the image memory has been reclaimed
			{ //TODO put all this into a separate function
				Log.trace("loading image"); //TODO del
				if(imageReference!=null)  //if we used to have a reference to an image, but memory was running low and it was reclaimed
				{
					Log.trace("Image memory reclaimed, reloading."); //TODO del
					System.gc();  //indicate that garbage collection should occur to attempt to give us more memory TODO testing memory
				}
				startedLoading=false; //show that the image hasn't started loading, yet
				finishedLoading=false;  //show that the image hasn't finished loading, either
				final XMLDocument document=(XMLDocument)getDocument();  //get the document used to load resources TODO make sure this is an XML document
					//get the href, taking into account that the href is relative to this file's base URL
//TODO del System.out.println("image href: "+getHRef());  //TODO del
			  final String href=XMLStyles.getBaseRelativeHRef(getAttributes(), getHRef());
//TODO del System.out.println("image base relateive href: "+href);  //TODO del
				image=(Image)document.getResource(href);	//get the image resource TODO check to make sure what is returned is really an image
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
/*TODO fix
    public AttributeSet getAttributes() {
	return attr;
    }
*/

    /** Is this image within a link? */
/*TODO fix
    boolean isLink( ) {
	return isLink;
    }
*/

    /** Returns the size of the border to use. */
/*TODO fix
    int getBorder( ) {
        return border;
    }
*/

    /** Returns the amount of extra space to add along an axis. */
/*TODO fix
    int getSpace( int axis ) {
	if (axis == X_AXIS) {
	    return xSpace;
	}
	return ySpace;
    }
*/

    /** Returns the border's color, or null if this is not a link. */
/*TODO fix or delete
    Color getBorderColor( ) {
    	StyledDocument doc = (StyledDocument) getDocument();
        return doc.getForeground(getAttributes());
    }
*/

/*TODO fix
    boolean hasPixels( ImageObserver obs ) {
        return fImage != null && fImage.getHeight(obs)>0
			      && fImage.getWidth(obs)>0;
    }
*/


    /** Return a URL for the image source,
        or null if it could not be determined. */
/*TODO fix
    private URL getSourceURL( ) {
 	String src = "file:/D:/Projects/oeb/understandingoeb/oebobjects_classdiagram.jpg";
//TODO fix 	String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
 	if( src==null ) return null;

//TODO fix	URL reference = ((HTMLDocument)getDocument()).getBase();
        try {
//TODO fix 	    URL u = new URL(reference,src);
 	    URL u = new URL(src);
	    return u;
        } catch (MalformedURLException e) {
	    return null;
        }
    }
*/

    /** Look up an integer-valued attribute. <b>Not</b> recursive. */
/*TODO fix
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
/*TODO del if we don't need
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
Log.trace("(before super) paint() {0}, isShowing: {1} startedLoading: {2} finishedLoading: {3}", new Object[]{getHRef(), new Boolean(isShowing()), new Boolean(startedLoading), new Boolean(finishedLoading)});
		super.paint(graphics, allocation);  //do the default painting
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		  //set pixel interpolation to its highest quality TODO probably do this conditionally, based on some sort of flag
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
Log.trace("(after super) paint() {0}, isShowing: {1} startedLoading: {2} finishedLoading: {3}", new Object[]{getHRef(), new Boolean(isShowing()), new Boolean(startedLoading), new Boolean(finishedLoading)});
//TODO del Log.trace("imageView.paint() currentWidth: "+currentWidth+" currentHeight: "+currentHeight);
//TODO del when works		paintingVisible=true;	//show that calls to getComponent().repaint() are actually causes this image view to get repainted
//TODO fix	Color oldColor = g.getColor();
		  //TODO switch to using getBounds()
		final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();  //get the bounding rectangle of the painting area
//TODO del Log.trace("Inside OEBImageView.paint(), width: "+currentWidth+" height: "+currentHeight+" alloc width: "+alloc.width+" alloc height: "+alloc.height);	//TODO del; testing image
/*TODO fix
	fBounds.setBounds(alloc);
        int border = getBorder();
*/
/*TODO del when works
		int x = fBounds.x + border + getSpace(X_AXIS);
		int y = fBounds.y + border + getSpace(Y_AXIS);
		int width = fWidth;
		int height = fHeight;
*/
		final int x=rectangle.x;	//TODO comment
		final int y=rectangle.y;
		final int width=getCurrentWidth();  //get the current image width
		final int height=getCurrentHeight();  //get the current image height

//TODO fix	int sel = getSelectionState();

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
/*TODO fix
	if( ! hasPixels(this) ) {
	    g.setColor(Color.lightGray);
	    g.drawRect(x,y,width-1,height-1);
	    g.setColor(oldColor);
*/
/*TODO fix
	    loadIcons();
	    Icon icon = fImage==null ?sMissingImageIcon :sPendingImageIcon;
	    if( icon != null )
	        icon.paintIcon(getContainer(), g, x, y);
	}
*/

	// Draw image:
	try
	{
//TODO del when works		final Image image=getImage(); //get the image, which may include relading it
		shownImage=getImage(); //get the image, which may include relading it; this will for now set a hard reference to the image so that the image memory will not be reclaimed while it it showing
		if(shownImage!=null )
		{
Log.trace("got the image");
	//TODO fix		fImageIcon.paintIcon((Component)getContainer(), g, x, y);	//TODO testing

			if(finishedLoading || !startedLoading)	//if we've finished loading the image, or we haven't even started loading it, yet
			{
Log.trace("ready to draw image with current width {0} and current height {1}", new Object[]{new Integer(getCurrentWidth()), new Integer(getCurrentHeight())});
				//draw the image, which will start loading the image if it isn't loaded yet
				//note that many small images apparently never call imageUpdate(), meaning
				//  this function will return with the image already loaded without
				//  imageUpdate() being called, so we have to manipulate the
				//  finishedLoading and startedLoading variables here as well
//TODO bring back; testing				finishedLoading=g.drawImage(image, x, y, alloc.width, alloc.height, this);
Log.trace("Painting image "+href+" at "+x+", "+y+" width "+getCurrentWidth()+" height "+getCurrentHeight()); //TODO del
				finishedLoading=graphics.drawImage(shownImage, x, y, getCurrentWidth(), getCurrentHeight(), this);
	//TODO fix or del			final boolean isImageDrawn=g.drawImage(fImage, x, y, alloc.width, alloc.height, this);
	//TODO del when works			final boolean isImageDrawn=g.drawImage(fImage, x, y, width/2, height/2, this);

				if(!startedLoading && !finishedLoading) //if the image has not yet started loading (and loading hasn't already finished)
				{
					((Graphics2D)graphics).setPaint(Color.black);
					graphics.setFont(new Font("Arial", Font.PLAIN, 14));  //TODO fix all this; use a constant
					final String statusString="Loading image...";  //TODO fix; i18n; comment
					final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
							//TODO probably make sure that it is antialiased, here
					final Rectangle2D statusBounds=graphics2D.getFont().getStringBounds(statusString, fontRenderContext); //get the bounds of the status string
					final int statusX=x;  //find out where we would paint the status
					final int statusY=y+20;
						//if the string we would draw doesn't go outside our image
					if(statusX+statusBounds.getWidth()<x+getCurrentWidth() && statusY+statusBounds.getHeight()<x+getCurrentHeight())
					{
						graphics.drawString(statusString, statusX, statusY);	//TODO testing; i18n
					}

	//TODO del if not needed				startedLoading=true;  //TODO testing
				}
				//TODO should we update startedLoading for consistency, since finishedLoading might be set without startedLoading being set?
			}

	//TODO del Log.trace("image drawn: "+isImageDrawn);	//TODO del

//TODO fix		g.drawImage(fImage,x, y,width,height,this);


//TODO fix	    g.drawImage(fImage, x, y, (int)getParent().getPreferredSpan(X_AXIS)/3, height, this);
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
	catch(URISyntaxException uriSyntaxException)  //if there was an error getting the image TODO probably set some sort of flag so that we won't try to load it again next time
	{
		Log.error(uriSyntaxException); //report the error
		((Graphics2D)graphics).setPaint(Color.black);  //TODO fix all this
		graphics.setFont(new Font("Arial", Font.PLAIN, 14));
		graphics.drawString("Error loading image.", x, y+20);	//TODO testing; i18n
	}
	catch(IOException ioException)  //if there was an error getting the image TODO probably set some sort of flag so that we won't try to load it again next time
	{
		Log.error(ioException); //report the error
		((Graphics2D)graphics).setPaint(Color.black);  //TODO fix all this
		graphics.setFont(new Font("Arial", Font.PLAIN, 14));
		graphics.drawString("Error loading image.", x, y+20);	//TODO testing; i18n
	}

	// If selected exactly, we need a black border & grow-box:
/*TODO del or fix
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
/*TODO del or fix
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
/*TODO fix
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
/*TODO fix
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
/*TODO fix
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
/*TODO del if not needed
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
/*TODO fix
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT) ) {
		changed |= 1;
            }
*/
//TODO del if not needed			changed |= 1;
/*TODO fix
        if( (flags & ImageObserver.WIDTH) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.WIDTH) ) {
		changed |= 2;
            }
*/
/*TODO del if not needed
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
//TODO fix    private static boolean sIsInc = true;
//TODO fix    private static int sIncRate = 100;

    // --- Layout ----------------------------------------------------------


    /**
     * Determines the resizability of the view along the
     * given axis.  A value of 0 or less is not resizable.
     *
     * @param axis View.X_AXIS or View.Y_AXIS
     * @return the weight
     */
/*TODO fix after figuring out what can be returned
    public int getResizeWeight(int axis) {
	return 0;
    }
*/

    /** Change the size of this image. This alters the HEIGHT and WIDTH
    	attributes of the Element and causes a re-layout. */
/*TODO fix
    protected void resize( int width, int height ) {
    	if( width==fWidth && height==fHeight )
    	    return;

    	fWidth = width;
    	fHeight= height;

    	// Replace attributes in document:
	MutableAttributeSet attr = new SimpleAttributeSet();
*/
/*TODO fix
	attr.addAttribute(HTML.Attribute.WIDTH ,Integer.toString(width));
	attr.addAttribute(HTML.Attribute.HEIGHT,Integer.toString(height));
*/
/*TODO fix
	((StyledDocument)getDocument()).setCharacterAttributes(
			fElement.getStartOffset(),
			fElement.getEndOffset(),
			attr, false);
    }
*/

    // --- Mouse event handling --------------------------------------------

    /** Select or grow image when clicked. */
/*TODO fix
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
/*TODO fix
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

/*TODO fix
    public void mouseReleased(MouseEvent e){
    	fGrowBase = null;
    	//! Should post some command to make the action undo-able
    }
*/

    /** On double-click, open image properties dialog. */
/*TODO fix
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

//TODO fix    private Icon makeIcon(final String gifFile) throws IOException {
        /* Copy resource into a byte array.  This is
         * necessary because several browsers consider
         * Class.getResource a security risk because it
         * can be used to load additional classes.
         * Class.getResourceAsStream just returns raw
         * bytes, which we can convert to an image.
         */
/*TODO fix
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

/*TODO fix
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

/*TODO fix
    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }
*/

    // --- member variables ------------------------------------------------

//TODO fix    private AttributeSet attr;
//TODO fix    private Element   fElement;
//TODO del when works		private Image     fImage;

//TODO fix    private Container fContainer;
//TODO fix    private Rectangle fBounds;
//TODO fix    private Component fComponent;
//TODO fix    private Point     fGrowBase;        // base of drag while growing image
//TODO fix    private boolean   fGrowProportionally;	// should grow be proportional?
    /** Set to true, while the receiver is locked, to indicate the reciever
     * is loading the image. This is used in imageUpdate. */
//TODO fix    private boolean   loading;
//TODO fix    private boolean   isLink;
//TODO fix    private int       border;
//TODO fix    private int       xSpace;
//TODO fix    private int       ySpace;

    // --- constants and static stuff --------------------------------

/*TODO fix
    private static Icon sPendingImageIcon,
    			sMissingImageIcon;
    private static final String
        PENDING_IMAGE_SRC = "icons/image-delayed.gif",  // both stolen from HotJava
        MISSING_IMAGE_SRC = "icons/image-failed.gif";
*/

//TODO test    private static final boolean DEBUG = false;

//TODO del    private static final boolean DEBUG = true;	//TODO del; testing


    //$ move this someplace public
//TODO fix    static final String IMAGE_CACHE_PROPERTY = "imageCache";

    // Height/width to use before we know the real size:
/*TODO fix
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
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)	//TODO testing
	{
//TODO del Log.trace(getHRef());  //TODO del
/*TODO del
Log.trace("imageUpdate() {0}, infoflags: {1}"+
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
/*TODO fix
		if((infoflags & SOMEBITS)!=0)	//if there are some bits coming in, the image has started loading
			startedLoading=true;	//show that the image has started loading TODO what if, for a small image,

		if(

		if(!infoflags & ALLBITS)!=0)	//if all bits have been loaded
*/
//TODO fix Log.trace("imageUpdate() visible: "+paintingVisible+" Started loading: "+startedLoading+" Finished loading: "+finishedLoading);
/*TODO del
Log.trace("Parent: "+getParent());  //TODO del; testing
if(getParent()!=null) //TODO del
{
	Log.trace("Parent's parent: "+getParent().getParent());  //TODO del; testing
	if(getParent().getParent()!=null) //TODO del
		Log.trace("Parent's parent's parent: "+getParent().getParent().getParent());  //TODO del; testing
}
Log.trace("Container: "+getContainer());  //TODO del; testing
*/
//TODO del Log.trace(getHRef()+" allbits: ", infoflags & ALLBITS);
//TODO del Log.trace(getHRef()+" framebits: ", infoflags & FRAMEBITS);
		startedLoading=true;	//if imageUpdate() is ever called, we've at least started loading the image
		if((infoflags & (ALLBITS|FRAMEBITS))!=0)	//if we at any time receive all the bits, or if we're suddenly receiving frames from a multiple frame image, we've finished loading the image
			finishedLoading=true;	//show that we've finished loading the image
		if(finishedLoading) //only repaint the image if it has finished loading
		{
Log.trace("Finished loading ", getHRef());
/*TODO del
Log.trace("image finished loading, ready to repaint\n{0}, infoflags: {1}"+
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
//TODO del when works			if(paintingVisible)	//if calls to getComponent().repaint() are actually causes this image view to get repainted
			if(isShowing())	//if this view is showing
			{
Log.trace("Is showing ", getHRef());
//TODO del Log.trace("*********OEBImageView.imageUpdate(), SOMEBITS: "+(infoflags & SOMEBITS)+" ALLBITS: "+(infoflags & ALLBITS)+" FRAMEBITS: "+(infoflags & FRAMEBITS));	//TODO del
/*TODO del; Debug no longer has a format trace method
Log.trace("image finished loading, ready to repaint\n{0}, infoflags: {1}"+
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
				if(getContainer()!=null)  //TODO testing
				{
Log.trace("Have container ", getHRef());
//TODO del when works					paintingVisible=false;	//for our next repaint force paint() to prove once more that it is still getting called
				  final Rectangle bounds=getBounds(); //get our painting bounds
Log.trace(getHRef()+" painting bounds: ", bounds);
/*TODO del; gives wrong coordinates
					bounds.x=x; //TODO testing image repaint
					bounds.y=y;
*/
//TODO del Log.trace("Found container, ready to call repaint with bounds: "+bounds);
				  if(bounds.width==0 || bounds.height==0) //TODO testing; kludge to compensate for table layout errors
					{
						bounds.width=getCurrentWidth();  //get the current image width
						bounds.height=getCurrentHeight();  //get the current image height
					}
Log.trace("Repainting image "+href+" with bounds: ", bounds); //TODO del
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
//TODO del		return ((infoflags & (ALLBITS|ABORT)) == 0) && isShowing();  //TODO testing isShowing()
	}


}