package com.garretwilson.swing.text.xml.css;

import java.awt.*;
import java.lang.ref.*;
import java.util.*;
import java.io.*;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
/*G***bring back as needed
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
*/
//G***del import com.garretwilson.swing.text.xml.css.XMLCSSStyleContext;
import com.garretwilson.awt.FontUtilities;
import com.garretwilson.util.Debug;

/**A pool of styles and their associated resources, created to store and
	retrieve CSS styles attribute sets.
	Notably, this class overrides the font caching algorithm in
	<code>StyleContext</code> to allow for soft references so that the memory
	allocated to the fonts may be reclaimed if necessary.
	This class is not currently thread safe.
@see com.garretwilson.awt.FontUtilities
@author Garret Wilson
*/
public class XMLCSSStyleContext extends StyleContext
{

	/**Map of font family names, keyed to either a Unicode block or a
		<code>character</code>.
	*/
//G***del	protected final static Map characterFontFamilyNameMap=new HashMap();

	/**The sorted list of available font family names.*/
//G***del	private static String[] sortedAvailableFontFamilyNameArray=null;

	/**Returns a sorted array of names of available fonts. The returned array is
		shared by other objects in the system, and should not be modified.
//G***del		 The first
//G***del		call to this method will create and cache the array.
	@return A sorted array of names of available fonts.
	*/
/*G***del
	protected String[] getAvailableFontFamilyNames()
	{
		return sortedAvailableFontFamilyNameArray;  //return the array of font family names
	}
*/

	/**The shared precreated key used for searching for fonts in the cache.*/
//G***del	private transient FontKey searchFontKey=new FontKey(null, 0, 0);

	/**A synchronized map of references to fonts that have been loaded.*/
//G***del	protected transient Map fontReferenceMap=new HashMap();

	/**Default constructor which queries available fonts.*/
	public XMLCSSStyleContext()
	{
/*G***del; recomment
		if(sortedAvailableFontFamilyNameArray==null)  //if the array of font family names has not been created
		{
			//get the list of available font family names
			sortedAvailableFontFamilyNameArray=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			Arrays.sort(sortedAvailableFontFamilyNameArray);  //sort the array of font family names
		}
*/
	}

	/**Gets a new font, retrieved from a cache if possible.
		This method is not thread safe.
	@param family The font family (such as "Monospaced").
	@param style The style of the font (such as <code>Font.PLAIN</code>).
	@param size The point size (>=1).
	@return A font with the given parameters, from the cache if possible.
	@see FontUtilities#getFont(String, int, size)
	*/
	public Font getFont(final String family, final int style, final int size)
	{
		return FontUtilities.getFont(family, style, size);  //pass the request on the the font utilities
/*G***del
		searchFontKey.setValue(family, style, size);  //set the values of the font key for searching
		final Reference fontReference=(Reference)fontReferenceMap.get(searchFontKey); //see if this font is already in the map
		Font font=null;  //we'll assign a font to this variable; assume at first that we don't have a font
		if(fontReference!=null) //if we had the font at one time in the cache
		{
			font=(Font)fontReference.get(); //see if the font is still there
			if(font==null)  //if the memory has been reclaimed
			{
			  fontReferenceMap.remove(searchFontKey); //remove the key from the map, since it doesn't contain a reference to a font anymore
			}
		}
		if(font==null)  //if we didn't have a font cached, or if we did but its memory has been reclaimed
		{
		  font=new Font(family, style, size); //create a new font with the desired characteristics
		  final FontKey fontKey=new FontKey(family, style, size); //create a new font key to represent the font
		  fontReferenceMap.put(fontKey, new SoftReference(font));  //store a soft reference to the font in the map, so that the font can be reclaimed, if necessary
		}
		return font;  //return the font we found in the map or created
*/
	}

	/**Gets a new font for the specified character by searching all available fonts.
	@param c The character for which a font should be returned.
	@param style The style of the font (such as <code>Font.PLAIN</cod>).
	@param size The point size (>=1).
	@return The new font, or <code>null</code> if a font could not be found that
		matched this character.
	@see FontUtilities#getFont(char, int, int)
	*/
	public Font getFont(final char c, final int style, final int size)
	{
		return FontUtilities.getFont(c, style, size); //pass the request to the font utilities
	}

	/**Gets the font from an attribute set using CSS names instead of the default
		Swing names. This is implemented to try and fetch a cached font for the given
		AttributeSet, and if that fails the font features are resolved and the font
		is fetched from the low-level font cache.
		Modified from javax.swing.text.StyleContext.getFont().
	@param attributeSet The attribute set from which to retrieve values.
	@return The constructed font.
	@see StyleContext#getFont
	*/
	public Font getFont(AttributeSet attributeSet)
	{
		int style=Font.PLAIN;	//start out assuming we'll have a plain font
		if(XMLCSSStyleUtilities.isBold(attributeSet))	//if the attributes specify bold (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.BOLD;	//add bold to our font style
		if(XMLCSSStyleUtilities.isItalic(attributeSet))	//if the font attributes specify italics (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.ITALIC;	//add italics to our font style
		final String family=StyleConstants.getFontFamily(attributeSet);	//get the font family from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set) G***change to use CSS attributes
		int size=Math.round(XMLCSSStyleUtilities.getFontSize(attributeSet));	//get the font size from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
/*G***del
		//if the attributes specify either superscript or subscript (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
		if(StyleConstants.isSuperscript(attributeSet) || StyleConstants.isSubscript(attributeSet))	//G***change to use CSS attributes
			size-=2;	//reduce the font size by two
*/
		return FontUtilities.getFont(family, style, size);	//get the font based upon the specifications we just got from attribute set
	}

	/**Gets the foreground color from a set of attributes.
	@param attributeSet The set of attributes.
	@return The foreground color.
	*/
	public Color getForeground(final AttributeSet attributeSet)
	{
		return XMLCSSStyleUtilities.getForeground(attributeSet);	//let the CSS style constants get the foreground for us
	}

	/**Takes a set of attributes and turn it into a background color specification.
	@param attributeSet The set of attributes.
	@return The background color.
	*/
	public Color getBackground(final AttributeSet attributeSet)
	{
		return XMLCSSStyleUtilities.getBackgroundColor(attributeSet);	//let the CSS style constants get the background for us G***resolve inconsistency between getForeground() and getBackgroundColor()
	}

	//G***create the transient fontReferenceMap in a readObject

	/**Constructs transient variables before the object is read from a stream.
	@param objectInputStream The stream from which the object is being read.
	*/
	private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException
	{
//G***del		searchFontKey=new FontKey(null, 0, 0);  //create a new font key for searching
//G***del		fontReferenceMap=new HashMap(); //create a new map of references to fonts
		objectInputStream.defaultReadObject();  //read the object normally
	}

}
