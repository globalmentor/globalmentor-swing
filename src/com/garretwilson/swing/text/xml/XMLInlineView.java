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
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import com.garretwilson.awt.FontUtilities;

import com.garretwilson.swing.text.xml.css.XMLCSSStyles;

import com.globalmentor.log.Log;

/**View class for all inline elements. Uses CSS attributes.
Based on the Swing {@link LabelView} component.
@author Garret Wilson
@see LabelView
*/
public class XMLInlineView extends GlyphView implements TabableView
{
	/**The cached font.*/
	private Font GlyphFont;

	/**The cached background color.*/
	private Color ForegroundColor;

	/**The cached foreground color.*/
	private Color BackgroundColor;

	/**The cached underline value.*/
	private boolean Underline;

	/**The cached strikethrough value.*/
	private boolean Strikethrough;

	/**The cached vertical align identifier.*/
	private String verticalAlign;

	/**The cached superscript value.*/
//TODO del	private boolean Superscript;

	/**The cached subscript value.*/
//TODO del	private boolean Subscript;

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLInlineView(Element element)
	{
		super(element);	//construct the parent class

//TODO fix Log.trace("glyphpainter: "+getGlyphPainter().getClass().getName()); //TODO del

	}

	/**Retrieves the attributes to use when rendering.
		<p>The XML views reflect XML's layout in which the actual text content is
		represented as a sub-node to the element which defines it. An
		<code>XMLInlineView</code>'s element, returned by <code>getElement()</code>,
		represents this text content. The attributes, however, are contained in
		the attribute set of the containing element. This method therefore returns
		the attributes of the parent element of the underlying text, not of the
		underlying text itself.</p>
		<p>Note that this subtle distinction will not be evident when accessing
		inherited values, as they will automatically be fetched from the containing
		element attribute set when it is recognized that they are not present in the
		underlying text content.</p>
		<p>It's possible, of course, that a view factory might incorrectly create
		an inline view, not for the content itself, but for an inline element that
		is a parent of the content. This method allows for this case by checking
		to see if this element represents content or a branch element.</p>
	@return The attribute set which represents this text content.
	*/
	public AttributeSet getAttributes()
	{
//TODO del Log.trace("getting inline attributes of element: ", AttributeSetUtilities.getAttributeSetString(getElement().getAttributes()));  //TODO del
		Element element=getElement(); //get the element we represent
		if(AbstractDocument.ContentElementName.equals(element.getName()))  //if this is content
			element=element.getParentElement(); //the XML text content never holds the attributes -- its containing element does
	  return element.getAttributes(); //return the attribute set of the element that contains the text we represent
//TODO del when works return getElement().getParentElement().getAttributes(); //TODO del
	}

	/**Synchronizes the view's cached valiues with the model. This causes the
		font, metrics, color, etc to be recached if the cache has been invalidated.
	*/
	protected void synchronize()
	{
		if(GlyphFont==null)	//TODO probably later change to use a list of fonts; TODO use another variable for the synchronization flag, as we probably won't cache the font, anymore
			setPropertiesFromAttributes();	//calculate our properties from our attributes
	 }

	/**Sets whether or not this view is underlined.
	@param underlined Whether or not this view should be underlined
	*/
	protected void setUnderline(boolean underline) {Underline=underline;}

	/**Sets whether or not this view has a line through it.
	@param strikethrough Whether this view should have a strikethrough line.
	*/
	protected void setStrikeThrough(boolean strikethrough) {Strikethrough=strikethrough;}

	/**Sets the cached vertical align property.
	@param newVerticalAlign An identifier specifying the vertical alignment.
	*/
	protected void setVerticalAlign(String newVerticalAlign) {verticalAlign=newVerticalAlign;}

	/**Sets whether or not this view represents a superscript.
	@param superscript Whether this view should be a superscript.
	*/
//TODO del	protected void setSuperscript(boolean superscript) {Superscript=superscript;}

	/**Sets whether or not this view represents a subscript.
	@param subscript Whether this view should be a subscript.
	*/
//TODO del	protected void setSubscript(boolean subscript) {Subscript=subscript;}

		/**
		 * Set the cached properties from the attributes.
		 */
//TODO fix
	/**Sets the cached properties from the attributes. This function overrides the
		version in GlyphView to work with CSS properties.
	*/
	protected void setPropertiesFromAttributes()
	{
		final AttributeSet attributeSet=getAttributes();	//get our attributes
		if(attributeSet!=null)	//if we have attributes
		{
//TODO del Log.trace("setting properties from inline attributes: ", AttributeSetUtilities.getAttributeSetString(getAttributes()));  //TODO del
			final Document document=getDocument();	//get our document
			if(document instanceof StyledDocument)		//if this is a styled document
			{
				final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
				GlyphFont=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
				ForegroundColor=styledDocument.getForeground(attributeSet);	//let the document get the foreground from the attribute set
				BackgroundColor=styledDocument.getBackground(attributeSet);	//let the document get the background from the attribute set
/*TODO del when works
			  BackgroundColor=XMLCSSStyleConstants.getBackgroundColor(attributeSet);	//get the background color from the attributes
*/
//TODO del Log.trace("Foreground color: ", ForegroundColor);  //TODO del
/*TODO fix
				if(attributeSetattr.isDefined(StyleConstants.Background)) {
				BackgroundColor = doc.getBackground(attr);
				} else {
					BackgroundColor = null;
				}
*/
				setUnderline(XMLCSSStyles.isUnderline(attributeSet));	//find out whether underline is specified in the attributes
				setVerticalAlign(XMLCSSStyles.getVerticalAlign(attributeSet));  //update the vertical alignment value
/*TODO fix
				setStrikeThrough(StyleConstants.isStrikeThrough(attr));
				setSuperscript(StyleConstants.isSuperscript(attr));
				setSubscript(StyleConstants.isSubscript(attr));
*/
			}
			else	//if this isn't a styled document
				throw new RuntimeException("XMLInlineView needs StyledDocument");
//TODO fix		throw new StateInvariantError("LabelView needs StyledDocument");
		}
		//TODO what if we don't have attributes?
	}

    /**
     * Fetch the background color to use to render the
     * glyphs.  If there is no background color, null should
     * be returned.  This is implemented to call
     * <code>StyledDocument.getBackground</code> if the associated
     * document is a styled document, otherwise it returns null.
     */
/*TODO fix
    public Color getBackground() {
			//TODO testing
		  return XMLCSSStyleConstants.getBackgroundColor(getAttributeSet());	//get the background color from the attributes
*/
/*TODO fix cache and comments
	Document doc = getDocument();
	if (doc instanceof StyledDocument) {
	    AttributeSet attr = getAttributes();
	    if (attr.isDefined(StyleConstants.Background)) {
		return ((StyledDocument)doc).getBackground(attr);
	    }
	}
	return null;
*/
//TODO fix    }

    /**
     * Fetch the foreground color to use to render the
     * glyphs.  If there is no foreground color, null should
     * be returned.  This is implemented to call
     * <code>StyledDocument.getBackground</code> if the associated
     * document is a StyledDocument.  If the associated document
     * is not a StyledDocument, the associated components foreground
     * color is used.  If there is no associated component, null
     * is returned.
     */
/*TODO fix
    public Color getForeground() {
		  return XMLCSSStyleConstants.getForeground(getAttributeSet());	//get the foreground color from the attributes
*/
/*TODO fix cache and comments
	Document doc = getDocument();
	if (doc instanceof StyledDocument) {
	    AttributeSet attr = getAttributes();
	    return ((StyledDocument)doc).getForeground(attr);
	}
	Component c = getContainer();
	if (c != null) {
	    return c.getForeground();
	}
	return null;
*/
//TODO fix    }


	/**Fetch the FontMetrics used for this view.
	@deprecated FontMetrics are not used for glyph rendering when running in the Java2 SDK.
	*/
	protected FontMetrics getFontMetrics()
	{
		synchronize();	//make sure we have the correct cached property values.
		Log.warn("Java 2 should not use deprecated FontMetrics.");
		return Toolkit.getDefaultToolkit().getFontMetrics(GlyphFont);
	}

	/**Gets the background color to use to render the glyphs. This implementation
		returns a cached background color.
	@return The background color, or <code>null</code> if there is no background color.
	*/
	public Color getBackground()
	{
		synchronize();	//make sure we have the correct cached property values.
//TODO del Log.trace("getting inline background for attributes: ", AttributeSetUtilities.getAttributeSetString(getAttributes()));  //TODO del
//TODO del Log.trace("found color: ", BackgroundColor);  //TODO del

		return BackgroundColor;	//return the background color
	}

	/**Gets the foreground color to use to render the glyphs. This implementation
		returns a cached foreground color.
	@return The foreground color, or <code>null</code> if there is no foreground color.
	*/
	public Color getForeground()
	{
		synchronize();	//make sure we have the correct cached property values.
//TODO del Log.trace("getting inline foreground for attributes: ", AttributeSetUtilities.getAttributeSetString(getAttributes()));  //TODO del
//TODO del Log.trace("found color: ", ForegroundColor);  //TODO del

		return ForegroundColor;	//return the foreground color
	}

	/**Gets the font to use to render the glyphs. This implementation
		returns a cached font.
	@return The font to be used to render the glyphs.
	*/
	public Font getFont()
	{
		return ((StyledDocument)getDocument()).getFont(getAttributes());	//ask the document for the font
//TODO testing to fix antialias and zoom changes
/*TODO del; we can't cache the font unless we know when the document changes zoom and/or antialias properties; get the font from the document, which caches fonts, anyway
		synchronize();	//make sure we have the correct cached property values.
		return GlyphFont;	//return the font
*/
	}

    /**
     * Gets a new font.  This returns a Font from a cache
     * if a cached font exists.  If not, a Font is added to
     * the cache.  This is basically a low-level cache for
     * 1.1 font features.
     *
     */

	/**Gets a specific font based upon the font name, style, and size. This
		implementation passes the request on to the <code>XMLDocument</code> if the
		associated document is an instance of <code>XMLDocument</code>. (This allows
		the font to be retrieved from the same cache because the document uses its
		associated style context to retrieve all fonts.)	If the
		associated document is not an <code>XMLDocument</code>, a new font is
		created.
	@param family The font family (such as "Monospaced")
	@param style The style of the font (such as <code>Font.PLAIN</code>).
	@param size The point size (>=1)
	@return The new font.
	*/
	public Font getFont(final String family, final int style, final int size) //TODO maybe put this in an XMLGlyphView
	{
		final Document document=getDocument();  //get a reference to the associated document
		if(document instanceof XMLDocument) //if this is an XML document
			return ((XMLDocument)document).getFont(family, style, size);  //get the font from the document
		else  //if this is not an XML document
			return new Font(family, style, size); //create the font from scratch
	}

	/**Returns a sorted array of names of available fonts. This
		implementation passes the request on to the <code>XMLDocument</code> if the
		associated document is an instance of <code>XMLDocument</code>. (This allows
		the array to be retrieved from the same cache because the document uses its
		associated style context to retrieve all fonts.)	If the
		associated document is not an <code>XMLDocument</code>, a new array is
		created.
		The returned array is shared by other objects in the system, and should not
		be modified.
	@return A sorted array of names of available fonts.
	@see XMLDocument#getAvailableFontFamilyNames
	*/
/*TODO del
	public String[] getAvailableFontFamilyNames()
	{
		final Document document=getDocument();  //get a reference to the associated document
		if(document instanceof XMLDocument) //if this is an XML document
			return ((XMLDocument)document).getAvailableFontFamilyNames();  //get the array of font names from the document
		else  //if this is not an XML document
		{
			//get the list of available font family names
			final String[] sortedAvailableFontFamilyNameArray=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			Arrays.sort(sortedAvailableFontFamilyNameArray);  //sort the array of font family names
			return sortedAvailableFontFamilyNameArray;  //return the array of font family names
		}
	}
*/

	/**Gets a new font for the specified character by searching all available fonts.
		Fonts are cached by Unicode block and character, for faster searching in
		future queries.
		This implementation passes the request on to the <code>XMLDocument</code>
		if the associated document is an instance of <code>XMLDocument</code>, or
		returns <code>null</code> if not.
	@param c The character for which a font should be returned.
	@param style The style of the font (such as <code>Font.PLAIN</cod>).
	@param size The point size (>=1).
	@return The new font, or <code>null</code> if a font could not be found to
		display this character.
	@see XMLDocument#getFont
	*/
	public Font getFont(final char c, final int style, final int size)
	{
		final Document document=getDocument();  //get a reference to the associated document
		if(document instanceof XMLDocument) //if this is an XML document
			return ((XMLDocument)document).getFont(c, style, size);  //get the font for the character from the document
		else  //if this is not an XML document
			return FontUtilities.getFont(c, style, size); //get the font for the character from the font utilities (which is probably where it would ultimately come from through the document, anyway)
//TODO del			return null;  //show that we can't find a font for the character
	}

	/**@return Whether or not the glyphs should be underlined.*/
	public boolean isUnderline()
	{
		synchronize();	//make sure we have the correct cached property values.
		return Underline;	//return whether or not we should be underlined
	}

	/**@return Whether or not there should be a strike line through the glyphs.*/
	public boolean isStrikeThrough()
	{
		synchronize();	//make sure we have the correct cached property values.
		return Strikethrough;	//return whether or not there should be a strikethrough
	}

	/**@return The vertical alignment of this view.*/
	public String getVerticalAlign()
	{
		synchronize();	//make sure we have the correct cached property values.
		return verticalAlign;	//return the vertical alignment value
	}

	/**@return Whether the glyphs should be rendered as subcript.*/
/*TODO del
	public boolean isSubscript()
	{
		synchronize();	//make sure we have the correct cached property values.
		return Subscript;	//return whether the glyphs should be subscript
	}
*/

	/**@return Whether the glyphs should be rendered as superscript.*/
/*TODO del
	public boolean isSuperscript()
	{
		synchronize();	//make sure we have the correct cached property values.
		return Superscript;	//return whether the glyphs should be superscript
	}
*/

	/* ***View methods*** */

	/**Gives notification from the document that attributes were changed
		in a location that this view is responsible for.
	@param e The change information from the associated document.
	@param a The current allocation of the view.
	@param f The factory to use to rebuild if the view has children.
	@see View#changedUpdate
	*/
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f)
	{
		GlyphFont=null;	//set the font to null to indicate that the cache has been invalidated
	}




//TODO del	private SimpleAttributeSet AttributeSet;	//TODO comment when works
//TODO del	Font font=null;	//TODO testing

/*TODO del
	public Font getFont()
	{
//TODO del System.out.println("XMLInlineView.getFont() called.");	//TODO del
//TODO del		return ((StyledDocument)getDocument()).getFont(getAttributes());	//TODO testing

				int style = Font.PLAIN;
				if (StyleConstants.isBold(getAttributes())) {
						style |= Font.BOLD;
				}
				if (StyleConstants.isItalic(getAttributes())) {
						style |= Font.ITALIC;
				}
				String family = StyleConstants.getFontFamily(getAttributes());
				int size = StyleConstants.getFontSize(getAttributes());

				if (StyleConstants.isSuperscript(getAttributes()) ||
						StyleConstants.isSubscript(getAttributes())) {
						size -= 2;
				}

//TODO del				return getFont(family, style, size);


		Font f = new Font(family, style, size);
System.out.println("XMLInlineView.getFont() called for element "+getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+", resulting in font: "+f);	//TODO del
		return f;
	}
*/



		/**
		 * Gives notification from the document that attributes were changed
		 * in a location that this view is responsible for.
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#changedUpdate
		 */
/*TODO fix
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {	//TODO fix, tidy, comment
	super.changedUpdate(e, a, f);
//TODO del	StyleSheet sheet = getStyleSheet();
//TODO del	attr = sheet.getViewAttributes(this);
	preferenceChanged(null, true, true);
		}
*/

		/**
		 * Fetches the attributes to use when rendering.  This is
		 * implemented to multiplex the attributes specified in the
		 * model with a StyleSheet.
		 */
/*TODO fix
		public AttributeSet getAttributes() {
	return AttributeSet;	//TODO fix, tidy, comment
		}
*/

		/**
		 * Fetch the span of the longest word in the view.
		 */
//TODO fix
/*TODO fix
		float getLongestWordSpan() {
	// find the longest word
	float span = 0;
	try {
			Document doc = getDocument();
			int p0 = getStartOffset();
			int p1 = getEndOffset();
			if (p1 > p0) {
		Segment segment = new Segment();
		doc.getText(p0, p1 - p0, segment);
		int word0 = p0;
		int word1 = p0;
		BreakIterator words = BreakIterator.getWordInstance();
		words.setText(segment);
		int start = words.first();
		for (int end = words.next(); end != BreakIterator.DONE;
				 start = end, end = words.next()) {

				// update longest word boundary
				if ((end - start) > (word1 - word0)) {
			word0 = start;
			word1 = end;
				}
		}
		// calculate the minimum
		if ((word1 - word0) > 0) {
				FontMetrics metrics = getFontMetrics();
				int offs = segment.offset + word0 - segment.getBeginIndex();
				span = metrics.charsWidth(segment.array, offs, word1 - word0);
		}
			}
	} catch (BadLocationException ble) {
			// If the text can't be retrieved, it can't influence the size.
	}
	return span;
		}
*/

	/**Sets the view properties from the CSS properties in the attributes.*/
/*TODO del when works
	protected void setPropertiesFromAttributes()
	{
		final AttributeSet attr=getAttributes();	//get our attributes
		if (attr != null)
		{
			Document d = getDocument();
			if (d instanceof StyledDocument)
			{
				StyledDocument doc = (StyledDocument) d;

				final XMLCSSSimpleAttributeSet attributeSet=new XMLCSSSimpleAttributeSet();	//TODO testing
				StyleConstants.setFontFamily(attributeSet, "Serif");
				StyleConstants.setFontSize(attributeSet, 10);

				font = doc.getFont(attributeSet);	//TODO del; testing

//TODO fix		font = doc.getFont(attr);
				fg = doc.getForeground(attr);
				if (attr.isDefined(StyleConstants.Background))
				{
						bg = doc.getBackground(attr);
				} else {
						bg = null;
			}
			setUnderline(StyleConstants.isUnderline(attr));
			setStrikeThrough(StyleConstants.isStrikeThrough(attr));
			setSuperscript(StyleConstants.isSuperscript(attr));
			setSubscript(StyleConstants.isSubscript(attr));
		}
		else
		{
	//TODO fix		throw new StateInvariantError("LabelView needs StyledDocument");
			throw new Exception("LabelView needs StyledDocument");
		}
	}
	}
*/

/*TODO fix
		//convert CSS font properties to Swing attributes *before* calling the parent class, so that it can setup the font correctly
			//TODO assuming this is a mutable attribute set sounds dangerous; is there a better way to do this?

		final MutableAttributeSet attributeSet=(MutableAttributeSet)getElement().getAttributes();		//get a reference to our attributes
System.out.println("Setting view properties for element: "+attributeSet.getAttribute(XMLCSSStyleConstants.XMLElementNameName));	//TODO del
System.out.println("Current element attributes: "+attributeSet);	//TODO del

//TODO del		int fontStyle=getFont().getStyle();	//get the style of this font
			//check the font style
		final XMLCSSPrimitiveValue fontStyleProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSStyleDeclaration.PROP_FONT_STYLE);	//get the font-style property TODO can we be sure this will be a primitive value?
		if(fontStyleProperty!=null)	//if we have this property
		{
			final String fontStyleString=fontStyleProperty.getStringValue();	//get the string representing the font style
System.out.println("Font style is not null: "+fontStyleString);	//TODO del
			if(fontStyleString.equals(XMLCSSStyleDeclaration.CSS_FONT_STYLE_ITALIC) || fontStyleString.equals(XMLCSSStyleDeclaration.CSS_FONT_STYLE_OBLIQUE))	//we should use italics here
				StyleConstants.setItalic(attributeSet, true);	//turn italics on
//TODO del				fontStyle|=Font.ITALIC;	//add italic to this font's list of attributes
			if(fontStyleString.equals(XMLCSSStyleDeclaration.CSS_FONT_STYLE_NORMAL))	//we should not use italics here
				StyleConstants.setItalic(attributeSet, false);	//turn italics off
//TODO del				fontStyle&=~Font.ITALIC;	//remove italic from this font's list of attributes
		}
		final XMLCSSPrimitiveValue fontWeightProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSStyleDeclaration.PROP_FONT_WEIGHT);	//get the font-weight property TODO can we be sure this will be a primitive value?
		if(fontWeightProperty!=null)	//if we have this property
		{
			final String fontWeightString=fontWeightProperty.getStringValue();	//get the string representing the font weight
System.out.println("Font weight is not null: "+fontWeightString);	//TODO del
			if(fontWeightString.equals(XMLCSSStyleDeclaration.CSS_FONT_WEIGHT_BOLD) || fontWeightString.equals(XMLCSSStyleDeclaration.CSS_FONT_WEIGHT_BOLDER))	//we should use bold here
				StyleConstants.setBold(attributeSet, true);	//turn bold on
//TODO del				fontStyle|=Font.BOLD;	//add bold to this font's list of attributes
			if(fontWeightString.equals(XMLCSSStyleDeclaration.CSS_FONT_WEIGHT_NORMAL))	//we should not use bold here
;//TODO fix				StyleConstants.setBold(attributeSet, false);	//turn bold off
//TODO del				fontStyle&=~Font.BOLD;	//remove bold from this font's list of attributes
		}
//TODO del		getFont().setStyle(fontStyle);	//change the font's style to reflect any changes we might have made
System.out.println("New element attributes: "+attributeSet);	//TODO del
*/

//TODO del System.out.println("Setting view properties for element: "+getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName));	//TODO del


//TODO del when works		super.setPropertiesFromAttributes();	//to the default setting of properties


//TODO del System.out.println("After setting inline properties, font is: "+getFont());	//TODO del


//TODO del		int fontStyle=getFont().getStyle();	//get the style of this font

/*TODO fix
	Object decor = a.getAttribute(CSS.Attribute.TEXT_DECORATION);
	boolean u = (decor != null) ?
		(decor.toString().indexOf("underline") >= 0) : false;
	setUnderline(u);
	boolean s = (decor != null) ?
		(decor.toString().indexOf("line-through") >= 0) : false;
	setStrikeThrough(s);
				Object vAlign = a.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
	s = (vAlign != null) ? (vAlign.toString().indexOf("sup") >= 0) : false;
	setSuperscript(s);
	s = (vAlign != null) ? (vAlign.toString().indexOf("sub") >= 0) : false;
	setSubscript(s);
*/
//TODO fix		}

/*TODO del if we don't need
		protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
		}

		AttributeSet attr;
*/


}
