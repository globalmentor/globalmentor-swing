package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.font.*; //G***del if not needed
import java.awt.geom.*; //G***del if not needed
import java.util.ArrayList;
import java.util.List;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

//G***del import javax.swing.text.Row;	//G*** del if not needed
import javax.swing.text.BoxView;	//G*** del if not needed
import javax.swing.text.Element;	//G*** del if not needed
import javax.swing.text.Position;	//G*** del if not needed

import com.garretwilson.swing.text.CompositeView;
import com.garretwilson.swing.text.FragmentView;
import com.garretwilson.swing.text.SwingTextUtilities;
import com.garretwilson.swing.text.TextLayoutStrategy;
import com.garretwilson.swing.text.ViewsFactory;
import com.garretwilson.swing.text.ViewReleasable;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSView;
import com.garretwilson.swing.text.xml.css.XMLCSSViewPainter;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants; //G***maybe make everything that comes in through the XMLCSSStyleConstants class be agnostic of CSS, and remove the CSS-specific style strings
import com.garretwilson.util.Debug;

/**
 * Displays the a paragraph, and uses css attributes for its
 * configuration.
 *
 * @author  Timothy Prinzing
 * @version 1.17 06/04/99
 */
 //G***comment
public class XMLParagraphView extends javax.swing.text.ParagraphView implements XMLCSSView, ViewReleasable
{

	/**The justification of the paragraph. This is an override of the
		<code>ParagraphView</code> version because ParagraphView provides no
		accessor functions.*/
	private int Justification;

		/**Gets the justification of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			justification.
		@preturn The justification of the paragraph.
		*/
		public int getJustification() {return Justification;}

		/**Sets the justification of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			justification.
		@param justification The justification of the paragraph.
		*/
		protected void setJustification(final int justification) {Justification=justification;}


	/**The line spacing of the paragraph. This is an override of the
		<code>ParagraphView</code> version because ParagraphView provides no
		accessor functions.*/
	private float LineSpacing;

		/**Gets the line spacing of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			line spacing.
		@preturn The line spacing of the paragraph.
		*/
		public float getLineSpacing() {return LineSpacing;}

		/**Sets the line spacing of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			line spacing.
		@param lineSpacing The line spacing of the paragraph.
		*/
		protected void setLineSpacing(final float lineSpacing) {LineSpacing=lineSpacing;}

	/**Whether the first line should be indented according to the specified
		indentation. A value of <code>false</code> will override any specified setting.
	*/
	private boolean firstLineIndented=true;

		/**@return Whether the first line should be indented, which overrides andy
		  specified setting.
		*/
		protected boolean isFirstLineIndented() {return firstLineIndented;}

		/**Sets whether the first line should be indented. If this is set to
		  <code>false</code> (if, for example, there is an image and no text), any
			specified first-line indent value will be ignored.
		@param newFirstLineIndented <code>true</code> if the first line should be
			indented according to the indention value, <code>false</code> if that
			value should be ignored.
		*/
		protected void setFirstLineIndented(final boolean newFirstLineIndented) {firstLineIndented=newFirstLineIndented;}

		/**
		 * Constructs a ParagraphView for the given element.
		 *
		 * @param elem the element that this view is responsible for
		 */
		public XMLParagraphView(Element elem)
		{
			super(elem);
//G***del Debug.trace("Creating XMLParagraphView, i18n property: ", getDocument().getProperty("i18n")); //G***testing
Debug.trace(); //G***testing

		  strategy=new com.garretwilson.swing.text.TextLayoutStrategy();  //G***fix; testing i18n
		}

		/**
		 * Establishes the parent view for this view.  This is
		 * guaranteed to be called before any other methods if the
		 * parent view is functioning properly.
		 * <p>
		 * This is implemented
		 * to forward to the superclass as well as call the
		 * <a href="#setPropertiesFromAttributes">setPropertiesFromAttributes</a>
		 * method to set the paragraph properties from the css
		 * attributes.  The call is made at this time to ensure
		 * the ability to resolve upward through the parents
		 * view attributes.
		 *
		 * @param parent the new parent, or null if the view is
		 *  being removed from a parent it was previously added
		 *  to
		 */
//G***fix comment

		/**Sets the parent of the view.
			This is reimplemented to provide the superclass
			behavior as well as calling the <code>loadChildren</code>
			method if this view does not already have children. This overrides the
			<code>CompositeView</code> method which did not recognize pooled views,
			and therefore would call loadChildren() even when children had already
			been loaded.
		@param parent The parent of the view, or <code>null</code> if none.
		*/
		public void setParent(View parent)  //G***add this implementation to XMLFlowView
		{
//G***del Debug.trace(this, "setParent(), parent: "+(parent!=null ? parent.getClass().getName() : "null"));

//G***del Debug.assert(parent!=null, "XMLParagraphView cannot have a null parent.");
//G***del		Debug.trace("parent ", parent!=null ? parent.getClass().getName() : "null");
//G***del		Debug.trace("child view count ", getViewCount());

		  super.setParent(parent);  //do the default setting of the parent
/*G***del; fix method comment
		  //if we're being parented, and we don't have a layout pool or there are no children in it
		  if(parent!=null && layoutPool!=null && layoutPool.getViewCount()==0)
			{
				loadChildren(getViewFactory()); //load children using our view factory
		  }
*/


//G***del when works	super.setParent(parent);
/*G***del
		final Component component=getContainer(); //get our container
		if(component instanceof XMLTextPane)  //if we're embedded in an XMLTextPane
		{
			((TextLayoutStrategy)strategy).setAntialias(((XMLTextPane)component).isAntialias());  //update our strategy's idea of whether antialiasing should occur
		}
*/
//G***del	setPropertiesFromAttributes();
//G***del if not needed			layoutPool.setParent(this); //G***testing
/*G***del; probably results in this getting called to often
		if(parent!=null)  //G***testing; comment; done to update the strategy when the parent is changed
		{
			//This synthetic changeUpdate() call gives the strategy a chance to initialize.
			strategy.changedUpdate( this, null, null );  //G***testing
		}
*/

		}


	/**Releases any cached information such as pooled views and flows. This
		version releases the entire pooled view.
	*/
	public void release()
	{
		layoutPool=null;  //release the pool of views
		strategy=null;  //G***testing ViewReleasable
		//G***later, maybe release the strategy and the child views as well
	}

	/**Sets the cached properties from the attributes. This overrides the version
		in <code>ParagraphView</code> to work with CSS attributes.
	*/
	protected void setPropertiesFromAttributes()
	{
		final AttributeSet attributeSet=getAttributes();	//get our attributes
		if(attributeSet!=null)	//if we have attributes
		{
			setBackgroundColor(XMLCSSStyleUtilities.getBackgroundColor(attributeSet));	//set the background color from the attributes
			setParagraphInsets(attributeSet);	//G***fix
			setJustification(StyleConstants.getAlignment(attributeSet));	//G***fix
//G***del			setJustification(StyleConstants.ALIGN_JUSTIFIED);	//G***fix
//G***fix			setLineSpacing(StyleConstants.getLineSpacing(attributeSet));	//G***fix
//G***fix			setLineSpacing(2);	//G***fix; testing
//G***del			setLineSpacing(1.5f);	//G***fix; testing
			setLineSpacing(XMLCSSStyleUtilities.getLineHeight(attributeSet));	//set the line height amount from our CSS attributes G***fix this to correctly use the number
		  setVisible(!XMLCSSConstants.CSS_DISPLAY_NONE.equals(XMLCSSStyleUtilities.getDisplay(attributeSet))); //the paragraph is visible only if it doesn't have a display of "none"
//G***del			LineSpacing=3;	//G***fix; testing

			final Document document=getDocument();	//get our document
			if(document instanceof StyledDocument)		//if this is a styled document
			{
				final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
				final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
//G***find some way to cache the font in the attributes
				setFirstLineIndent(XMLCSSStyleUtilities.getTextIndent(attributeSet, font));	//set the text indent amount from the CSS property in the attributes, providing a font in case the length is in ems



//G***fix			firstLineIndent = (int)StyleConstants.getFirstLineIndent(attributeSet);	//G***fix

				//G***we may want to set the insets in setPropertiesFromAttributes(); for
				//percentages, getPreferredeSpan(), etc. will have to look at the preferred
				//span and make calculations based upon the percentages
				//G***probably have some other exernal helper class that sets the margins based upon the attributes
				final short marginTop=(short)Math.round(XMLCSSStyleUtilities.getMarginTop(attributeSet)); //get the top margin from the attributes
				final short marginLeft=(short)Math.round(XMLCSSStyleUtilities.getMarginLeft(attributeSet, font)); //get the left margin from the attributes
				final short marginBottom=(short)Math.round(XMLCSSStyleUtilities.getMarginBottom(attributeSet)); //get the bottom margin from the attributes
				final short marginRight=(short)Math.round(XMLCSSStyleUtilities.getMarginRight(attributeSet, font)); //get the right margin from the attributes
				setInsets(marginTop, marginLeft, marginBottom, marginRight);	//G***fix; testing
			}
		}
	}

	/**Returns the constraining span to flow against for the given child index.
		This is called by the <code>FlowStrategy</code> while it is updating the flow.
		This method overrides the standard version to allow first-line indentions.
		@param index The index of the row being updated (>=0 and <getViewCount()).
	@see ParagraphView#getFlowSpan
	@see #getFlowStart
	*/
	public int getFlowSpan(int index)
	{
		int flowSpan=super.getFlowSpan(index);	//get the default flow span
//G***del Debug.trace("getFlowSpan() index: "+index+" first line indented: "+isFirstLineIndented());  //G***del
		if(index==0 && isFirstLineIndented())	//if this is the first row, and we should indent the first row
			flowSpan-=firstLineIndent;	//show that the first line, if it's indented, has less room to flow inside G***use an accessor function here
		return flowSpan;	//return the flow span, which has been updated to compensate for the indented first line
	}

	/**Returns the location along the flow axis that the flow span will start at.
		This is overridden to correctly indent first lines of paragraphs, if needed
	@param index The index of the row being flowed.
	@return The location along the flow axis that the flow span will start at.
	@see ParagraphView#getFlowStart
	@see #getFlowSpan
	*/
	public int getFlowStart(int index)
	{
		int flowStart=super.getFlowStart(index);	//get the default starting location
//G***del Debug.trace("getFlowStart() index: "+index+" first line indented: "+isFirstLineIndented());  //G***del
		if(index==0 && isFirstLineIndented())	//if this is the first row, and we should indent the first row
			flowStart+=firstLineIndent;	//increase this flow start by the amount to indent G***use an accessor function here
		return flowStart;	//return the start of the flow
	}




/*G***del
    public float getPreferredSpan(int axis) { //G***del; testing
Debug.trace("XMLParagraphView.getPreferredSpan axis: "+axis+" ="+super.getPreferredSpan(axis)); //G***del
		return super.getPreferredSpan(axis);	//return the preferred span

    }

    public float getMinimumSpan(int axis) { //G***del; testing
Debug.trace("XMLParagraphView.getMinimum axis: "+axis+" ="+super.getMinimumSpan(axis)); //G***del
		return super.getMinimumSpan(axis);	//return the preferred span
    }

	public float getMaximumSpan(int axis) //G***del; testing
	{
Debug.trace("XMLParagraphView.getMaximumSpan axis: "+axis+" ="+super.getMaximumSpan(axis)); //G***del
		return super.getMaximumSpan(axis);	//return the preferred span
//G***fix		return getPreferredSpan(axis);	//return the preferred span
	}
*/



    /**
     * Paints a child.  By default
     * that is all it does, but a subclass can use this to paint
     * things relative to the child.
     *
     * @param g the graphics context
     * @param alloc the allocated region to paint into
     * @param index the child index, >= 0 && < getViewCount()
		 */
		protected void paintChild(Graphics g, Rectangle alloc, int index) {	//G***testing
	View child = getView(index);
/*G***del when works
	if(index==0)	//G***testing
		alloc.x+=firstLineIndent;	//G***fix
*/
//G***fix	alloc.x+=getFlowStart(index);	//G***testing
	child.paint(g, alloc);
		}

//G***del	protected float LineSpacing;	//G***testing

		/**
		 * Create a View that should be used to hold a
		 * a rows worth of children in a flow.
		 */
//G***testing
		protected View createRow()
		{
//G***del 			return super.createRow();	//G***fix
			Element elem = getElement();
			TestRow row = new TestRow(elem, getLineSpacing());

/*G***del
Debug.trace("In XMLParagraphView.createRow(), layout pool looks like this:"); //G***del
if(Debug.isDebug()) //G***del
	com.globalmentor.mentoract.ReaderFrame.displayView(layoutPool, 0, layoutPool.getDocument()); //G***del; place display logic somewhere better
*/



/*G***del

			if(LineSpacing > 1)
			{
System.out.println("XMLParagraphView has line spacing of: "+LineSpacing);	//G***del
				float height = row.getPreferredSpan(View.Y_AXIS);
System.out.println("Got height of: "+height);	//G***del
				float addition = (height * LineSpacing) - height;
System.out.println("Got addition of: "+addition);	//G***del
				if(addition > 0)
				{
					row.addTopAddition((short)(addition*2));	//G***fix
//G***fix			row.setInsets(row.getTopInset(), row.getLeftInset(), (short)50, row.getRightInset());
				}
			}
*/
			return row;	//G***fix
		}

		/**
		 * Fetches the attributes to use when rendering.  This is
		 * implemented to multiplex the attributes specified in the
		 * model with a StyleSheet.
		 */
/*G***del
		public AttributeSet getAttributes() {
	return attr;
		}
*/

		/**
		 * Sets up the paragraph from css attributes instead of
		 * the values found in StyleConstants (i.e. which are used
		 * by the superclass).  Since
		 */
/*G***del
		protected void setPropertiesFromAttributes() {
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
	painter = sheet.getBoxPainter(attr);
	if (attr != null) {
			super.setPropertiesFromAttributes();
			setInsets((short) painter.getInset(TOP, this),
					(short) painter.getInset(LEFT, this),
					(short) painter.getInset(BOTTOM, this),
					(short) painter.getInset(RIGHT, this));
			Object o = attr.getAttribute(CSS.Attribute.TEXT_ALIGN);
			if (o != null) {
		// set horizontal alignment
		String ta = o.toString();
		if (ta.equals("left")) {
				setJustification(StyleConstants.ALIGN_LEFT);
		} else if (ta.equals("center")) {
				setJustification(StyleConstants.ALIGN_CENTER);
		} else if (ta.equals("right")) {
		    setJustification(StyleConstants.ALIGN_RIGHT);
		} else if (ta.equals("justify")) {
		    setJustification(StyleConstants.ALIGN_JUSTIFIED);
		}
			}
	}
		}
*/

/*G***del
		protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
		}
*/










	/**Perform layout for the minor axis of the box (i.e. the axis orthoginal to
		the axis that it represents). The results of the layout should be placed in
		the given arrays which represent the allocations to the children along the
		minor axis.
		This version performs the default layout and then adds the correct
		indentation amount to the first row.
	@param targetSpan The total span given to the view, which whould be used to
		layout the children.
	@param axis The axis being layed out.
	@param offsets The offsets from the origin of the view for each of the child
		views. This is a return value and is filled in by the implementation of this
		method.
	@param spans The span of each child view. This is a return value and is
		filled in by the implementation of this method.
	@returns the offset and span for each child view in the offsets and spans
		parameters.
	*/
	protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
	{
		super.layoutMinorAxis(targetSpan, axis, offsets, spans);	//do the default layout
		if(offsets.length>0 && isFirstLineIndented())	//if there is at least one row, and the first line should be indented
			offsets[0]+=firstLineIndent;	//add the correct indentation amount to the first row G***use an accessor function here for firstLineIndent, if we can
	}

		/**
		 * Calculate the needs for the paragraph along the minor axis.
		 * This implemented to use the requirements of the superclass,
		 * modified slightly to set a minimum span allowed.  Typical
		 * html rendering doesn't let the view size shrink smaller than
		 * the length of the longest word.
		 */
/*G***fix
		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	r = super.calculateMinorAxisRequirements(axis, r);

	// PENDING(prinz) Need to make this better so it doesn't require
	// InlineView and works with font changes within the word.

	// find the longest minimum span.
	float min = 0;
	int n = getLayoutViewCount();
	for (int i = 0; i < n; i++) {
			View v = getLayoutView(i);
			if (v instanceof XMLInlineView) {
		float wordSpan = ((XMLInlineView) v).getLongestWordSpan();
		min = Math.max(wordSpan, min);
			} else {
		min = Math.max(v.getMinimumSpan(axis), min);
			}
	}
	r.minimum = (int) min;
	r.preferred = Math.max(r.minimum,  r.preferred);
	r.maximum = Math.max(r.preferred, r.maximum);
	return r;
		}
*/


		/**
		 * Indicates whether or not this view should be
		 * displayed.  If none of the children wish to be
		 * displayed and the only visible child is the
		 * break that ends the paragraph, the paragraph
		 * will not be considered visible.  Otherwise,
		 * it will be considered visible and return true.
		 *
		 * @returns true if the paragraph should be displayed.
		 */
/*G***fix
		public boolean isVisible() {

	int n = getLayoutViewCount() - 1;
	for (int i = 0; i < n; i++) {
			View v = getLayoutView(i);
			if (v.isVisible()) {
		return true;
			}
	}
	if (n > 0) {
			View v = getLayoutView(n);
			if ((v.getEndOffset() - v.getStartOffset()) == 1) {
		return false;
			}
	}
	// If it's the last paragraph and not editable, it shouldn't
	// be visible.
	if (getStartOffset() == getDocument().getLength()) {
			boolean editable = false;
			Component c = getContainer();
			if (c instanceof JTextComponent) {
		editable = ((JTextComponent)c).isEditable();
			}
			if (!editable) {
		return false;
			}
	}
	return true;
		}
*/

		/**
		 * Renders using the given rendering surface and area on that
		 * surface.  This is implemented to delgate to the superclass
		 * after stashing the base coordinate for tab calculations.
		 *
		 * @param graphics the rendering surface to use
		 * @param allocation the allocated region to render into
		 * @see View#paint
		 */
//G***comment
		public void paint(Graphics graphics, Shape allocation)
		{
			if(isVisible()) //if this view is visible
			{

				//G***testing fractional metrics; should this be in XMLInlineView?
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		  //turn on fractional metrics G***probably do this conditionally, based on some sort of flag
		graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

/*G***del
					//G***testing font
			final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
			final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
			final Rectangle2D boundsA=graphics2D.getFont().getStringBounds("A", fontRenderContext); //get the bounds of the string
Debug.trace("bounds for 'A'"+boundsA); //G***del; testing font
			final Rectangle2D boundsAI=graphics2D.getFont().getStringBounds("\u0910", fontRenderContext); //get the bounds of the string
Debug.trace("bounds for '\u0910'"+boundsAI); //G***del; testing font


graphics2D.getFont().getMissingGlyphCode()
			final GlyphVector glyphVector=graphics.getFont().createGlyphVector(fontRenderContext);
			final Shape
*/

/*G***important: turn on antialiasing, but only after making sure the layout metrics compensate for this
			final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		  //antialiasing on G***put this in the paragraph fragment, too
		  graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
*/
/*G***fix; put wherever we turn antialiasing on or off, along with setting that option in TextLayout
			final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
			final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
		  //antialiasing on G***put this in the paragraph fragment, too
		  graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//G***del		  graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
*/

Debug.trace("paragraph layout strategy: "+strategy.getClass().getName()); //G***del


//G***del; ParagraphView doesn't seem to do the same type of style caching			synchronize();	//make sure we have the correct cached property values (newswing)

			XMLCSSViewPainter.paint(graphics, allocation, this, getAttributes());	//paint our CSS-specific parts (newswing)

/*G***fix or del
	Rectangle r;
	if (a instanceof Rectangle) {
			r = (Rectangle) a;
	} else {
			r = a.getBounds();
	}
	painter.paint(g, r.x, r.y, r.width, r.height, this);
				super.paint(g, a);
*/
		  super.paint(graphics, allocation);  //let the super class paint the rest of the paragraph
			}
		}

	/**Determines the preferred span for this view. Returns 0 if the view is not
		visible, otherwise it calls the superclass method to get the preferred span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The span the view would like to be rendered into.
	@see javax.swing.text.ParagraphView#getPreferredSpan
	*/
	public float getPreferredSpan(int axis)
	{
		return isVisible() ? super.getPreferredSpan(axis) : 0;  //return 0 if the view isn't visible
	}

	/**Determines the minimum span for this view along an axis. Returns 0 if the
		view is not visible, otherwise it calls the superclass method to get the
		minimum span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The minimum span the view can be rendered into.
	@see javax.swing.text.ParagraphView#getMinimumSpan
	*/
	public float getMinimumSpan(int axis)
	{
		return isVisible() ? super.getMinimumSpan(axis) : 0;  //return 0 if the view isn't visible
	}

	/**Determines the maximum span for this view along an axis. Returns 0 if the
		view is not visible, otherwise it calls the superclass method to get the
		maximum span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The maximum span the view can be rendered into.
	@see javax.swing.text.ParagraphView#getMaximumSpan
	*/
	public float getMaximumSpan(int axis)
	{
		return isVisible() ? super.getMaximumSpan(axis) : 0;  //return 0 if the view isn't visible
	}

/*G***del
		private AttributeSet attr;
		private StyleSheet.BoxPainter painter;
*/

	//G***del; testing
/*G***del
	public int getBreakWeight(int axis, float pos, float len)
	{
System.out.println("XMLBlockView is telling break weight.");	//G***del
		if(axis==Y_AXIS)	//if they want the break weight for the Y axis
		{
System.out.println("XMLBlockView is trying to force a page break.");	//G***del
			return ForcedBreakWeight;	//show that we're forcing a break on this axis
		}
		else	//if they want the break weight on the X axis
			return super.getBreakWeight(axis, pos, len);	//let our parent class determine the break weight
	}
*/

    // ---- BoxView methods -------------------------------------

	/**Loads all of the children to initialize the view.
		This is called by the <code>setParent</code> method.
		This is reimplemented to not load any children directly, as they are created
		in the process of formatting.
		If the layoutPool variable is <code>null</code>, an instance of
		<code>LogicalView</code> is created to represent the logical view that is
		used in the process of formatting.
		This version of <code>loadChildren</code> is implemented to return
		specifically a <code>XMLParagraphView.LogicalView</code>, so that that view
		can implement its own special version of child view construction.
	@param viewFactory The view factory to use for child view creation.
	*/
	protected void loadChildren(ViewFactory f)
	{
//G***del Debug.stackTrace();		  //G***del
Debug.trace();
//G***testing hint
/*G***del
Debug.trace("Getting graphics context");
        Graphics2D g2d =(Graphics2D) getContainer().getGraphics();
        FontRenderContext frc;
        try {
            // FontRenderContexts exist to allow text to be measured at times
            // when a Graphics object is not available.  A TextLayout is
            // drawn using the settings of the FRC it was created with in
            // preferrence to the settings of the Graphics it is drawn with.
            // This keeps measuring and drawing consistent but may be
            // surprising to some users.  We should probably ensure that
            // these two stay in sync.
Debug.trace("Checking graphics context");
            if( g2d != null ) {
Debug.trace("Graphics context is not null");

                frc = g2d.getFontRenderContext();
Debug.trace("fontRenderContext isAntialiased: "+frc.isAntiAliased());
Debug.trace("setting render hints");
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
			  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
Debug.trace("fontRenderContext isAntialiased: "+frc.isAntiAliased());
	            } else {
Debug.trace("Graphics context is null");
                // As a practical matter, this FRC will almost always
                // be the right one.
                AffineTransform xf
                    = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .getDefaultTransform();
                frc = new FontRenderContext(xf, false, false);
            }
        } finally {
*/
/*G***fix
            if( g2d != null )
                g2d.dispose();
*/
//G***del        }
/*G***del
        Graphics2D graphics2D =(Graphics2D) getContainer().getGraphics();  //G***testing hint
			  graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
*/

		if(layoutPool==null)  //if there is no layout pool
		{
//G***del Debug.trace("XMLParagraphView.loadChildren() creating new logical view");
		  layoutPool=new LogicalView(getElement()); //create our own brand of logical view that will correctly create paragraph child views
		}
/*G***fix; this turned off antialiasing
		if(strategy==null) //G***testing ViewReleasable
		  strategy=new com.garretwilson.swing.text.TextLayoutStrategy();  //G***fix; testing i18n
*/
		//we only need to load views if we previously had no views in the pool;
		//this is to compensate for FlowView, which unconditionally loads children
		//when there are no child views, even if there are views in the pool
		final boolean needsLoading=layoutPool.getViewCount()==0;
		layoutPool.setParent(this); //tell the layout pool that we're its parent, so that it can begin creating its child views
//G***del Debug.trace("layout pool has "+layoutPool.getViewCount()+" children.");
//G***del Debug.trace("layout child is of type: "+layoutPool.getView(0).getClass().getName());
		//we also need to check to make sure we have content; if we don't have
		//  content, and we try to do a strategy.insertUpdate(), we'll get all sorts
		//  of ArrayIndexOutOfBounds exceptions when attempting to find content
		//  locations that return -1 (i.e. no location)
		//we have content when (1) there is at least one view in the pool, and (2)
		//  our ending offset is larger than our starting offset (someone could have
		//  placed several no-content inline views in the pool, which would satisfy
		//  only the first condition)
/*G***del; fixed
Debug.trace("checking to see if paragraph needs loading: ", new Boolean(needsLoading));  //G***del
Debug.trace("end offset: ", getEndOffset());  //G***del
Debug.trace("start offset: ", getStartOffset());  //G***del
Debug.trace("layout pool view count: ", layoutPool.getViewCount());  //G***del
if(layoutPool.getViewCount()>0) //G***del
	Debug.trace("first layout pool view: ", layoutPool.getView(0).getClass().getName());  //G***del
*/
		  //if we really need to load things
		if(needsLoading)
		{
			if(layoutPool.getViewCount()>0 && getEndOffset()-getStartOffset()>0)  //if we have at least some content
			{
	//G***del Debug.trace("Updating the strategy and stuff"); //G***del
				final int poolViewCount=layoutPool.getViewCount();  //find out how many views are in the pool
	//G***del Debug.trace("pool view count: ", poolViewCount); //G***del
					//see if there are only object views present, how many inline views there are, etc.
				boolean onlyObjectsPresent=true; //assume there are only objects present in this paragraph
				int inlineViewCount=0;  //we'll keep a record of how many inline views there are
	//G***fix			boolean firstInlineViewHasMultipleWords=false;  //assume the first inline view has multiple words
				for(int i=0; i<poolViewCount; ++i)  //look at each view in the pool
				{
					final View pooledView=layoutPool.getView(i);  //get a reference to this pooled view
	//G***del Debug.trace("Looking at pooled view: ", pooledView.getClass().getName()); //G***del
	//G***del Debug.trace("pooled view start offset: ", pooledView.getStartOffset()); //G***del
	//G***del Debug.trace("pooled view end offset: ", pooledView.getEndOffset()); //G***del
					if(!(pooledView instanceof XMLObjectView)) //if this isn't an object
						onlyObjectsPresent=false; //show that there are other things besides objects present
					else if(pooledView instanceof XMLInlineView)
					{
	/*G***fix
						if(inlineViweCount==0)  //if this is the first inline view we've found
						{
							final Segment segment=getText(pooledView.getStartOffset(), pooledView.getEndOffset());  //get the segment of text the inline view represents
							pooledView.getElement().get
						}
	*/
						++inlineViewCount;  //show that we've found another inline view
					}
				}
	/*G***del
					//see if there is only one child, an object view G***modify to work with multiple objects, but no text
				final boolean onlyObjectPresent=poolViewCount==1
					&& (layoutPool.getView(0) instanceof XMLObjectView);
				boolean firstLineIndented=!onlyObjectPresent; //we'll indent the first line unless there's only an object present
					//see if we're a one-word paragraph inside a table cell
				if(firstLineIndented) //if we still think we're going to indent the first line
				{
	*/
				final View parentView=getParent();  //get our parent view
				final String parentDisplay=XMLCSSStyleUtilities.getDisplay(parentView.getAttributes()); //see what kind of parent we have
				final boolean isInTableCell=XMLCSSConstants.CSS_DISPLAY_TABLE_CELL.equals(parentDisplay);  //see if we're inside a table cell
				/*G***del when works
				if(XMLCSSConstants.CSS_DISPLAY_TABLE_CELL.equals(parentDisplay))  //if we're inside a table cell
				{
						//see if there's only one inline view that has only one word
					int inlineViewCount=0;  //we'll keep a record of how many inline views there are
					boolean onlyObjectsPresent=true; //assume there are only objects present in this paragraph
					for(int i=0; i<poolViewCount; ++i)  //look at each view in the pool
					{
						final View pooledView=layoutPool.getView(i);  //get a reference to this pooled view
						if(!(layoutPool.getView(i) instanceof XMLObjectView)) //if this isn't an object
						{
							onlyObjectsPresent=false; //show that there are other things besides objects present
							break;  //stop looking for other non-objects
						}
					}
				}
				*/

		//G***del Debug.trace("only objects present: "+onlyObjectPresent);
	//G***del when works			setFirstLineIndented(!onlyObjectPresent); //if there are only objects present in this paragraph, we won't indent
				setFirstLineIndented(!onlyObjectsPresent && (inlineViewCount>1 || !isInTableCell)); //if there are only objects present in this paragraph, or if there's only one inline view in a table cell, we won't indent
				//This synthetic insertUpdate call gives the strategy a chance to initialize.
				strategy.insertUpdate( this, null, null );
			}
		}
		else  //if there is no content
;//G***this doesn't seem to work; maybe change the dimensions, or just create a hidden view, if there is no content			setVisible(false);  //make the entire paragraph invisible
	}

//G***we have to use this TestRow because Swing has linespacing errors

		/**
		 * Internally created view that has the purpose of holding
		 * the views that represent the children of the paragraph
		 * that have been arranged in rows.
		 */
		class TestRow extends BoxView {

				TestRow(Element elem, float lineSpacing) {
						super(elem, View.X_AXIS);
						this.LineSpacing=lineSpacing;	//G***fix, comment
//G***del setInsets(getTopInset(), getLeftInset(), (short)50, getRightInset());	//G***testing

				}
/*G***del
				void addTopAddition(short addition)	//G***testing
				{
System.out.println("Adding TestRow addition of: "+addition);	//G***del
					setInsets(getTopInset(), getLeftInset(),	addition, getRightInset());
				}
*/
			private float LineSpacing;	//G***fix

				/**
				 * This is reimplemented to do nothing since the
				 * paragraph fills in the row with its needed
				 * children.
				 */
				protected void loadChildren(ViewFactory f) {
				}
	/**
	 * Fetches the attributes to use when rendering.  This view
	 * isn't directly responsible for an element so it returns
	 * the outer classes attributes.
	 */
				public AttributeSet getAttributes() {
			View p = getParent();
			return (p != null) ? p.getAttributes() : null;
	}

				public float getAlignment(int axis) {
						if (axis == View.X_AXIS) {
								switch (getJustification()) {
								case StyleConstants.ALIGN_LEFT:
										return 0;
								case StyleConstants.ALIGN_RIGHT:
										return 1;
								case StyleConstants.ALIGN_CENTER:
								case StyleConstants.ALIGN_JUSTIFIED:
										return 0.5f;
								}
						}
						return super.getAlignment(axis);
				}


				/**
				 * Provides a mapping from the document model coordinate space
				 * to the coordinate space of the view mapped to it.  This is
				 * implemented to let the superclass find the position along
				 * the major axis and the allocation of the row is used
				 * along the minor axis, so that even though the children
				 * are different heights they all get the same caret height.
				 *
				 * @param pos the position to convert
				 * @param a the allocated region to render into
				 * @return the bounding box of the given position
				 * @exception BadLocationException  if the given position does not represent a
				 *   valid location in the associated document
				 * @see View#modelToView
				 */
				public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
						Rectangle r = a.getBounds();
			View v = getViewAtPosition(pos, r);
			if ((v != null) && (!v.getElement().isLeaf())) {
		// Don't adjust the height if the view represents a branch.
		return super.modelToView(pos, a, b);
			}
			r = a.getBounds();
						int height = r.height;
						int y = r.y;
						Shape loc = super.modelToView(pos, a, b);
						r = loc.getBounds();
						r.height = height;
						r.y = y;
						return r;
				}

				/**
				 * Range represented by a row in the paragraph is only
				 * a subset of the total range of the paragraph element.
				 * @see View#getRange
				 */

				public int getStartOffset() {
			int offs = Integer.MAX_VALUE;
						int n = getViewCount();
			for (int i = 0; i < n; i++) {
		View v = getView(i);
		offs = Math.min(offs, v.getStartOffset());
			}
//G***del Debug.trace("Paragraph TestRow getStartOffset() called: "+offs);  //G***del
						return offs;
				}

				public int getEndOffset() {
			int offs = 0;
						int n = getViewCount();
			for (int i = 0; i < n; i++) {
		View v = getView(i);
		offs = Math.max(offs, v.getEndOffset());
			}
//G***del Debug.trace("Paragraph TestRow getEndOffset() called: "+offs);  //G***del
						return offs;
				}
	/**
	 * Perform layout for the minor axis of the box (i.e. the
	 * axis orthoginal to the axis that it represents).  The results
	 * of the layout should be placed in the given arrays which represent
	 * the allocations to the children along the minor axis.
	 * <p>
	 * This is implemented to do a baseline layout of the children
	 * by calling BoxView.baselineLayout.
	 *
	 * @param targetSpan the total span given to the view, which
	 *  whould be used to layout the children.
	 * @param axis the axis being layed out.
	 * @param offsets the offsets from the origin of the view for
	 *  each of the child views.  This is a return value and is
	 *  filled in by the implementation of this method.
	 * @param spans the span of each child view.  This is a return
	 *  value and is filled in by the implementation of this method.
	 * @returns the offset and span for each child view in the
	 *  offsets and spans parameters.
	 */
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
		{
			baselineLayout(targetSpan, axis, offsets, spans); //do a baseline layout of the row
			for(int i=0; i<offsets.length; ++i) //look at each of the offsets
			{
				final String verticalAlign; //we'll get the vertical alignment value from the view
				final View childView=getView(i);  //get a reference to this child view
				if(childView instanceof XMLInlineView)  //if this is an XML inline view
					verticalAlign=((XMLInlineView)childView).getVerticalAlign();  //get the cached vertical alignment value directly from the view
				else  //if the view is another type
					verticalAlign=XMLCSSStyleUtilities.getVerticalAlign(childView.getAttributes()); //get the vertical alignment specified by the inline view
				final int relativeIndex=i>0 ? i-1 : (offsets.length>1 ? i+1 : i);  //we'll move the offset relative to the offset before us or, if this is the first offset, the one after us; if there is only one offset, use ourselves
//G***del Debug.trace("Relative index: ", relativeIndex); //G***del
				  //G***this currently doesn't work for subscripts of subscripts or superscripts of superscripts or a superscript followed by a subscript, etc.
				final int relativeOffset=offsets[relativeIndex];  //get the span to which we're relative
				final int relativeSpan=spans[relativeIndex];  //get the span to which we're relative
				if(XMLCSSConstants.CSS_VERTICAL_ALIGN_SUPER.equals(verticalAlign))  //if this is superscript
				{
				  offsets[i]=relativeOffset-Math.round(relativeSpan*.20f); //move the box 20% above the relative box of the relative box's height
				}
				else if(XMLCSSConstants.CSS_VERTICAL_ALIGN_SUB.equals(verticalAlign))  //if this is subscript
				{
						//G***fix; right now, it doesn't compensate for the linespacing
				  offsets[i]=relativeOffset+Math.round(relativeSpan*.60f); //move the box 20% below the relative box of the relative box's height
				}
			}
		}

				protected SizeRequirements calculateMinorAxisRequirements(int axis,
									SizeRequirements r) {
			return baselineRequirements(axis, r);
	}
	/**
	 * Fetches the child view index representing the given position in
	 * the model.
	 *
	 * @param pos the position >= 0
	 * @returns  index of the view representing the given position, or
	 *   -1 if no view represents that position
	 */
	protected int getViewIndexAtPosition(int pos) {
			// This is expensive, but are views are not necessarily layed
			// out in model order.
			if(pos < getStartOffset() || pos >= getEndOffset())
		return -1;
			for(int counter = getViewCount() - 1; counter >= 0; counter--) {
		View v = getView(counter);
		if(pos >= v.getStartOffset() &&
			 pos < v.getEndOffset()) {
				return counter;
		}
			}
			return -1;
	}

		public float getPreferredSpan(int axis)
		{
			return axis==Y_AXIS ? super.getPreferredSpan(axis)*this.LineSpacing : super.getPreferredSpan(axis);	//G***testing
		}

		public float getMaximumSpan(int axis)
		{
			return axis==Y_AXIS ? super.getMaximumSpan(axis)*this.LineSpacing : super.getMaximumSpan(axis);	//G***testing
		}

		public float getMinimumSpan(int axis)
		{
			return axis==Y_AXIS ? super.getMinimumSpan(axis)*this.LineSpacing : super.getMinimumSpan(axis);	//G***testing

		}

	}




/*G***del
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias)	//G***testing
{
Debug.trace("Inside XMLParagraphView.viewToModel for position x: "+x+" y: "+y);
	return super.viewToModel(x, y, a, bias);
    }
*/












//G***testing, hacking









	/**Determines how attractive a break opportunity in this view is. This can be
		used for determining which view is the most attractive to call
		<code>breakView</code> on in the process of formatting.  The higher the
		weight, the more attractive the break.  A value equal to or lower than
		<code>View.BadBreakWeight</code> should not be considered for a break. A
		value greater than or equal to <code>View.ForcedBreakWeight</code> should
		be broken.<br/>
		This is implemented to forward to the superclass for axis perpendicular to
		the flow axis. Along the flow axis the following values may be returned:
		<ul>
//G***fix			<li>View.ExcellentBreakWeight: If there is whitespace proceeding the desired break
//G***fix		 *   location.
			<li>View.BadBreakWeight: If the desired break location results in a break
				location of the starting offset (i.e. not even one child view can fit.</li>
			<li>View.GoodBreakWeight: If the other conditions don't occur.
		</ul>
		This will result in the view being broken with the maximum number of child
		views that can fit within the required span.
		@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
		@param pos The potential location of the start of the broken view (>=0).
			This may be useful for calculating tab positions.
		@param len Specifies the relative length from <em>pos</em> where a potential
			break is desired (>=0).
		@return The weight, which should be a value between View.ForcedBreakWeight
			and View.BadBreakWeight.
		@see LabelView
		@see ParagraphView
		@see BadBreakWeight
		@see GoodBreakWeight
		@see ExcellentBreakWeight
		@see ForcedBreakWeight
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
//G***del Debug.trace("Inside XMLParagraphView.getBreakWeight axis: "+axis+" pos: "+pos+" len: "+len+" name: "+XMLStyleConstants.getXMLElementName(getAttributes()));  //G***del

//G***del		final int tileAxis=getAxis();	//get our axis for tiling (this view's axis)
		if(axis==getAxis())	//if they want to break along our tiling axis
		{
			return View.GoodBreakWeight;	//show that this break spot will work
		}
		else	//if they want to break along another axis besides the one we know about
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
	}

	/**Breaks this view on the given axis at the given length. This is implemented
		to attempt to break on the largest number of child views that can fit within
		the given length.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param p0 the location in the model where the fragment should start its
		representation (>=0).
	@param pos the position along the axis that the broken view would occupy (>=0).
		This may be useful for things like tab calculations.
	@param len Specifies the distance along the axis where a potential break is
		desired (>=0).
	@return The fragment of the view that represents the given span, if the view
		can be broken. If the view doesn't support breaking behavior, the view itself
		is returned.
	@see View#breakView
	*/
	public View breakView(int axis, int p0, float pos, float len)
	{
//G***del Debug.trace("Inside XMLParagraphView.breakView axis: "+axis+" p0: "+p0+" pos: "+pos+" len: "+len+" name: "+XMLStyleConstants.getXMLElementName(getAttributes()));  //G***del

	//G***important! if len is sufficiently large, just return ourselves
		if(axis==getAxis())	//if they want to break along our tiling axis
		{

//G***del		System.out.println("breakView() p0: "+p0+" pos: "+pos+" len: "+len+" preferredSpan: "+getPreferredSpan(axis));	//G***del

		  final int childViewCount=getViewCount();  //get the number of child views we have
			if(len>=getPreferredSpan(axis))	//if the break is as large or larger than the view
				return this;	//just return ourselves; there's no need to try to break anything
			else if(childViewCount>0)	//if we have child views
			{
				final boolean isFirstFragment=p0<=getView(0).getStartOffset();  //see if we'll include the first child in any form; if so, we're the first fragment
			  final XMLParagraphFragmentView fragmentView=new XMLParagraphFragmentView(getElement(), getAxis(), isFirstFragment);	//G***testing
//G***del when works				final XMLParagraphFragmentView fragmentView=(XMLParagraphFragmentView)clone();	//create a clone of this view G***rename method



//G***del when works				int p1=p0;	//we'll know if we've found views to include in our broken view when we change p1 to be greater than p0
				float totalSpan=0;	//we'll use this to accumulate the size of each view to be included
				int startOffset=p0;	//we'll continually update this as we create new child view fragments
				int childIndex;	//start looking at the first child to find one that can be included in our break
				for(childIndex=0; childIndex<childViewCount && getView(childIndex).getEndOffset()<=startOffset; ++childIndex);	//find the first child that ends after our first model location
				for(; childIndex<childViewCount && totalSpan<len; ++childIndex)	//look at each child view at and including the first child we found that will go inside this fragment, and keep adding children until we find enough views to fill up the space or we run out of views
				{
					View childView=getView(childIndex);	//get a reference to this child view; we may change this variable if we have to break one of the child views
					if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is too big to fit into our space
					{
							//G***new code; testing
/*G***bring back; see if we really need to add something -- we really only need to add something if this is the root XMLBlockView -- but how do we know that?
						if(fragmentView.getViewCount()==0)	//if we haven't fit any views into our fragment, we must have at least one, even if it doesn't fit
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
*/
//G***bring back						else if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight)	//if this view can be broken to be made to fit
						if(fragmentView.getViewCount()==0)	//if we haven't fit any views into our fragment, we must have at least one, even if it doesn't fit
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
						else if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight)	//if this view can be broken to be made to fit
						{
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
							if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is still too big to fit into our space
								childView=null;	//show that we don't want to add this child
						}
						else	//if this view can't break and we already have views in the fragment
							childView=null;	//show that we don't want to add this child


/*G***del; old, working code that sometimes overshot allocated boundaries
						if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight || fragmentView.getViewCount()==0)	//if this view can be broken to be made to fit, or we haven't fit any views into our fragment
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
						else	//if this view can't break and we already have views in the fragment
							childView=null;	//show that we don't want to add this child
*/
					}
					if(childView!=null)	//if we have something to add
					{
						fragmentView.append(childView);	//add this child view, which could have been chopped up into a fragment itself
/*G***del when works
if(childView==getView(0)) //if this is our first view
  fragmentView.firstLineIndent=firstLineIndent; //G***fix; hacked
*/
						totalSpan+=childView.getPreferredSpan(axis);	//show that we've used up more space
					}
					else	//if we needed more room but couldn't break a view
						break;	//stop trying to fit things
				}


fragmentView.setParent(getParent());				  //G***testing; see if this fixes the problem of fragments without parents
//G***del fragmentView.setParent(this);				  //G***testing; see if this fixes the problem of fragments without parents

				return fragmentView;	//return the new view that's a fragment of ourselves
			}
		}
		return this;	//if they want to break along another axis or we weren't able to break, return our entire view
	}


		/**
		 * Creates a shallow copy.  This is used by the
		 * createFragment and breakView methods.
		 *
		 * @return the copy
		 */
//G***testing
/*G***del when works
		protected final Object clone()  //G***rename to createFragment()
		{
//G***del when works			return new XMLBlockView(getElement(), getAxis());	//G***testing
		  final XMLParagraphFragmentView fragmentView=new XMLParagraphFragmentView(getElement(), getAxis());	//G***testing
			return fragmentView;  //G***fix
*/
//G***fix			return new XMLParagraphView(getElement()/*G***del, getAxis()*/);	//G***testing

/*G***fix
	Object o;
	try {
			o = super.clone();
	} catch (CloneNotSupportedException cnse) {
			o = null;
	}
	return o;
*/
//G***del when works		}


	/**Creates a view that represents a portion of the element. This is
		potentially useful during formatting operations for taking measurements of
		fragments of the view. If the view doesn't support fragmenting, it should
		return itself.<br/>
		This view does support fragmenting. It is implemented to return a new view
		that contains the required child views.
		@param p0 The starting offset (>=0). This should be a value greater or equal
			to the element starting offset and less than the element ending offset.
		@param p1 The ending offset (>p0).  This should be a value less than or
			equal to the elements end offset and greater than the elements starting offset.
		@returns The view fragment, or itself if the view doesn't support breaking
			into fragments.
		@see View#createFragment
	*/
	public View createFragment(int p0, int p1)
	{
//G***del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1+" name: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes()));	//G***del
//G***del Debug.trace("Inside XMLParagraphView.createFragment(), p0: "+p0+" p1: "+p1+" name: "+XMLStyleConstants.getXMLElementName(getAttributes()));  //G***del
//G***del Debug.trace("Our startOffset: "+getStartOffset()+" endOffset: "+getEndOffset());	//G***del
//G***del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1);	//G***del
//G***del System.out.println("Our startOffset: : "+getStartOffset()+" endOffset: "+getEndOffset());	//G***del





//G***del		XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
		if(p0<=getStartOffset() && p1>=getEndOffset())	//if the range they want encompasses all of our view
			return this;	//return ourselves; there's no use to try to break ourselves up
		else	//if the range they want only includes part of our view
		{
//G***del			final BoxView fragmentView=new BoxView(getElement(), getAxis());	//G***testing! highly unstable! trying to fix vertical spacing bug
			final int childViewCount=getViewCount();  //find out how many child views there are
				//see if we'll include the first child in any form; if so, we're the first fragment
		  final boolean isFirstFragment=childViewCount>0 && p0<=getView(0).getStartOffset();
		  final XMLParagraphFragmentView fragmentView=new XMLParagraphFragmentView(getElement(), getAxis(), isFirstFragment);	//G***testing
//G***del when works			final XMLParagraphFragmentView fragmentView=(XMLParagraphFragmentView)clone();	//create a clone of this view G***rename method

//G***don't we need to set the parent of the fragment somewhere, like for XMLBlockView?

	//G***fix		final XMLBlockFragmentView fragmentView=new XMLBlockFragmentView(this);	//create a fragment to hold part of our content
			for(int i=0; i<childViewCount; ++i)	//look at each child view
			{
				final View childView=getView(i);	//get a reference to this child view
				if(childView.getStartOffset()<p1 && childView.getEndOffset()>p0)	//if this view is within our range
				{
	//G***del when works			if(childView.getStartOffset()>=p0 && childView.getEndOffset()<=p1)	//if this view is within our range
					final int startPos=Math.max(p0, childView.getStartOffset());	//find out where we want to start, staying within this child view
					final int endPos=Math.min(p1, childView.getEndOffset());	//find out where we want to end, staying within this child view
					fragmentView.append(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our fragment
/*G***del when works
if(i==0)  //if this is our first view
  fragmentView.firstLineIndent=firstLineIndent; //G***fix; hacked
*/
				}
			}

		  fragmentView.setParent(getParent());				  //G***testing; see if this fixes the problem of fragments without parents
//G***del		  fragmentView.setParent(this);				  //G***testing; see if this fixes the problem of fragments without parents

			return fragmentView;	//return the fragment view we constructed
		}
	}




	/**The class that serves as a pool of view representing the logical view of
		the paragraph. It keeps the children updated to reflect the state of the
		model, gives the logical child views access to the view hierarchy, and
		calculates a preferred span. It doesn't do any rendering, layout, or
		model/view translation. This class replicates functionality of
		<code>FlowView.LogicalView</code> with special view loading for paragraph
		children to accomodate nested style tags. This class does not descend from
		<code>FlowView.LogicalView</code> because that class has package visibility.
	@see FlowView#LogicalView
	*/
	protected static class LogicalView extends CompositeView  //G***perhaps derive this from our own version of CompositeView, but that may slow things down with the customer setParent(), and we may not need that; comment and explain why
	{

		/**Logical view constructor.
		@param element The element this logical view represents.
		*/
		LogicalView(Element element)
		{
		  super(element);  //construct the class normally
		}

		/**Fetches the attributes to use when rendering. This view isn't directly
		responsible for an element so it returns the attributes of the outer class.
		@return The parent view's attributes, or <code>null</code> if there is no
			parent view or the parent view has no attributes.
		*/
		public AttributeSet getAttributes()
		{
	    final View parentView=getParent(); //get our parent, which is the XMLParagraphView
	    return parentView!=null ? parentView.getAttributes() : null;  //return the parent's attributes, or null if there is no parent
		}

		/**Loads all of the children to initialize the view.
			This is called by the <a href="#setParent">setParent</a> method.
			This version initially attempts to create a view for each child element,
			as normal. If the view factory returns <code>null</code> for an element,
			however, the element's children will be iterated and the same process will
			take place. This allows nested inline elements to be correctly turned into
			inline views by a view factory which recognizes this procedure, with the
			view factory determining which element hierarchies should be iterated
			(such as XHTML <code>&lt;em&gt;</code>) and which should not (such as
			XHTML <code>&img;</code>). View factories which do not support this
			procedure will function as normal.
		@param viewFactory The view factory.
		@see #setParent
		*/
		protected void loadChildren(ViewFactory viewFactory)
		{
//G***del Debug.trace("XMLParagraphView.LayoutView.loadChildren() in");
		  if(viewFactory==null) //if there is no view factory, the parent view has somehow changed
		    return; //don't create children
		  final Element element=getElement(); //get the element for which we're loading child views
//G***del Debug.trace("loading child views for element: ", XMLStyleUtilities.getXMLElementLocalName(element.getAttributes()));  //G***del
		  int childElementCount=element.getElementCount();  //see how many child elements there are
			if(childElementCount>0) //if there are child elements
			{
				final Element[] childElements=SwingTextUtilities.getChildElements(element);	//put our child elements into an array
				final View[] addViewArray=createViews(childElements, viewFactory);	//create views for the child elements
//G***del Debug.trace("XMLParagraphView.LayoutView.loadChildren() getting ready to add views: "+addViewArray.length);
		    replace(0, 0, addViewArray);  //add the views to our view
			}
//G***del Debug.trace("XMLParagraphView.LayoutView.loadChildren() out");
    }

		/**Creates views for the given elements.
		<p>If the given view factory implements <code>ViewsFactory</code>, this
			implementation will ask the views factory to create as many views as
			needed for the entire element hierarchy, essentially collapsing all child
			elements into a single level.</p> 
		@param elements The elements for each of which one or more views should be
			created.
		@param viewFactory The view factory to use for creating views. For correct
			formatting, this should be a <code>ViewsFactory</code>.
		@return An array of views representing the given elements.
		@see ViewsFactory
		*/
		protected View[] createViews(final Element[] elements, final ViewFactory viewFactory)
		{
			final List viewList=new ArrayList(elements.length); //create a new list in which to store views to add; we know there will be at least as many views as elements---maybe more
			if(viewFactory instanceof ViewsFactory) //if this view factory knows how to create multiple views
			{
				final ViewsFactory viewsFactory=(ViewsFactory)viewFactory;  //cast the view factory to a views factory
				for(int i=0; i<elements.length; ++i)  //look at each child element
					viewsFactory.create(elements[i], viewList);  //create one or more views and add them to our list
			}
			else  //if this is a normal view factory
			{
				for(int i=0; i<elements.length; ++i)  //look at each child element
					viewList.add(viewFactory.create(elements[i]));  //create a single view for this element and add it to our list
			}
			//create an array of views, making it the correct size (we now know how many views there will be), and placing the contents of the list into the array
			return (View[])viewList.toArray(new View[viewList.size()]);
		}

		/**Updates the child views in response to receiving notification that the 
			model changed, and there is change record for the element this view is
			responsible for.
		<p>This version correctly collapses the child inline view hierarchy.</p>
		@param elementChange The change information for the element this view is
			responsible for. This should not be <code>null</code> if this method gets
			called.
		@param documentEvent The change information from the associated document.
		@param viewFactory The factory to use to build child views.
		@return Whether or not the child views represent the child elements of the
			element this view is responsible for.
		*/
		protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory)
		{
			final Element[] removedElems=elementChange.getChildrenRemoved();
			final Element[] addedElems=elementChange.getChildrenAdded();
			View[] added=null;
			if(addedElems!=null)
			{
				added=createViews(addedElems, viewFactory);	//create views for the added elements
			}
			int nremoved=0;
			int index=elementChange.getIndex();
			if(removedElems!=null)
			{
				nremoved= removedElems.length;
			}
			replace(index, nremoved, added);
			return true;
		}


		/**Fetches the child view index representing the given position in the model.
			Since the logical view of a paragraph does not necessarily have one view
			for each child element (a child element in a logical view may be
			represented by have several views, representing nested elements), this
			method is implemented to correctly iterate views to locate one matching
			the position.
		@param pos The requested position (>=0).
		@returns The index of the view representing the given position, or -1 if no
			view represents that position.
		*/
    protected int getViewIndexAtPosition(int pos)
		{
//G***del Debug.trace("XMLParagraphView.LogicalView.getViewIndexAtPosition(): "+pos+" would be: "+getElement().getElementIndex(pos));  //G***del
	    final int viewCount=getViewCount(); //get the number of views in the pool
	    for(int i=0; i<viewCount; i++)  //look at each view in the pool
			{
				final View view=getView(i); //get a reference to this view
				if(pos>=view.getStartOffset() && pos<view.getEndOffset()) //if this position falls within the range of this view
				{
//G***del Debug.trace("Found view: "+i);  //G***del
					return i; //return this view's index
				}
			}
			return -1;  //show that we couldn't find a matching view
    }



	/**
	 * Determines the preferred span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
	 //G***comment
		public float getPreferredSpan(int axis)
		{
	    float maxpref = 0;
	    float pref = 0;
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		pref += v.getPreferredSpan(axis);
		if (v.getBreakWeight(axis, 0, Short.MAX_VALUE) >= ForcedBreakWeight) {
		    maxpref = Math.max(maxpref, pref);
		    pref = 0;
		}
	    }
	    maxpref = Math.max(maxpref, pref);

/*G***del
Debug.trace("After XMLParagraphView.LayoutView.getPreferredSpan(), view looks like this:"); //G***del
if(Debug.isDebug()) //G***del
	com.globalmentor.mentoract.ReaderFrame.displayView(this, 0, getElement().getDocument()); //G***del; place display logic somewhere better
*/

			return maxpref;
	}

	/**
	 * Determines the minimum span for this view along an
	 * axis.  The is implemented to find the minimum unbreakable
	 * span.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
	 //G***comment
        public float getMinimumSpan(int axis) {
	    float maxmin = 0;
	    float min = 0;
	    boolean nowrap = false;
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		if (v.getBreakWeight(axis, 0, Short.MAX_VALUE) == BadBreakWeight) {
		    min += v.getPreferredSpan(axis);
		    nowrap = true;
		} else if (nowrap) {
		    maxmin = Math.max(min, maxmin);
		    nowrap = false;
		    min = 0;
		}
	    }
	    maxmin = Math.max(maxmin, min);
	    return maxmin;
	}

	/**
	 * Forward the DocumentEvent to the given child view.  This
	 * is implemented to reparent the child to the logical view
	 * (the children may have been parented by a row in the flow
	 * if they fit without breaking) and then execute the superclass
	 * behavior.
	 *
	 * @param v the child view to forward the event to.
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see #forwardUpdate
	 * @since 1.3
	 */
	 //G***comment
        protected void forwardUpdateToView(View v, DocumentEvent e,
					   Shape a, ViewFactory f) {
	    v.setParent(this);
	    super.forwardUpdateToView(v, e, a, f);
	}

	// The following methods don't do anything useful, they
	// simply keep the class from being abstract.


	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to do nothing, the logical
	 * view is never visible.
	 *
	 * @param g the rendering surface to use
	 * @param allocation the allocated region to render into
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape allocation) {
	}

	/**
	 * Tests whether a point lies before the rectangle range.
	 * Implemented to return false, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the rectangle
	 * @return true if the point is before the specified range
	 */
        protected boolean isBefore(int x, int y, Rectangle alloc) {
	    return false;
	}

	/**
	 * Tests whether a point lies after the rectangle range.
	 * Implemented to return false, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the rectangle
	 * @return true if the point is after the specified range
	 */
        protected boolean isAfter(int x, int y, Rectangle alloc) {
	    return false;
	}

	/**
	 * Fetches the child view at the given point.
	 * Implemented to return null, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the parent's allocation on entry, which should
	 *   be changed to the child's allocation on exit
	 * @return the child view
	 */
        protected View getViewAtPoint(int x, int y, Rectangle alloc) {
	    return null;
	}

	/**
	 * Returns the allocation for a given child.
	 * Implemented to do nothing, as the logical view doesn't
	 * perform layout on the children.
	 *
	 * @param index the index of the child, >= 0 && < getViewCount()
	 * @param a  the allocation to the interior of the box on entry,
	 *   and the allocation of the child view at the index on exit.
	 */
        protected void childAllocation(int index, Rectangle a) {
	}
    }


	/**The class that serves as a fragment if the paragraph is broken.
	@author Garret Wilson
	*/
	protected class XMLParagraphFragmentView extends XMLBlockView implements FragmentView //G***descend from XMLFragmentBlockView
	{

//G***del when works	  int firstLineIndent=0; //G***fix; hacked; fix with new fragment index

		/**Whether this is the first fragment of the original view.*/
		private boolean isFirstFragment;

			/**@return <code>true</code> if this is the first fragment of the original view.*/
			public boolean isFirstFragment() {return isFirstFragment;}

		/**Whether this is the last fragment of the original view.*/
//G***del if not needed		private boolean isLastFragment;

			/**@return <code>true</code> if this is the last fragment of the original view.*/
//G***del if not needed			public boolean isLastFragment() {return isLastFragment;}

		/**Constructs a fragment view for the paragraph.
		@param element The element this view is responsible for.
		@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
		@param firstFragment Whether this is the first fragement of the original view.
		*/
		public XMLParagraphFragmentView(Element element, int axis, final boolean firstFragment)
		{
			super(element, axis); //do the default construction
			isFirstFragment=firstFragment;  //save whether we are the first fragment of the original view
		}

		/**Perform layout for the minor axis of the box (i.e. the axis orthoginal to
			the axis that it represents). The results of the layout should be placed in
			the given arrays which represent the allocations to the children along the
			minor axis.
			This version performs the default layout and then adds the correct
			indentation amount to the first row.
		@param targetSpan The total span given to the view, which whould be used to
			layout the children.
		@param axis The axis being layed out.
		@param offsets The offsets from the origin of the view for each of the child
			views. This is a return value and is filled in by the implementation of this
			method.
		@param spans The span of each child view. This is a return value and is
			filled in by the implementation of this method.
		@returns the offset and span for each child view in the offsets and spans
			parameters.
		*/
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)  //G***testing
		{
			super.layoutMinorAxis(targetSpan, axis, offsets, spans);	//do the default layout
		  //if we're the first fragement, there is at least one row, and the first line should be indented
		  if(isFirstFragment() && offsets.length>0 && isFirstLineIndented())
//G***del when works			if(offsets.length>0)	//if there is at least one row, and the first line should be indented
				offsets[0]+=firstLineIndent;	//add the correct indentation amount to the first row G***use an accessor function here for firstLineIndent, if we can
		}

	}

	//newswing

	/**Whether this view is visible.*/
	private boolean visible;

		/**@return Whether this view should be visible.*/
		public boolean isVisible() {return visible;}

		/**Sets whether this view is visible.
		@param newVisible Whether the view should be visible.
		*/
		protected void setVisible(final boolean newVisible) {visible=newVisible;}

	/**The background color of the block view.*/
	private Color backgroundColor;

		/**Gets the background color of the block view.
		@return The background color of the block view.
		*/
		public Color getBackgroundColor()
		{
//G***del; ParagraphView doesn't seem to do the same type of caching			synchronize();	//make sure we have the correct cached property values
			return backgroundColor;
		}

		/**Sets the background color of the block view.
		@param newBackgroundColor The color of the background.
		*/
		protected void setBackgroundColor(final Color newBackgroundColor) {backgroundColor=newBackgroundColor;}

}
