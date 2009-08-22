package com.garretwilson.swing.text.xml.css;

import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;

import com.garretwilson.awt.FontUtilities;

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

	/**The available font family names in a sorted array.*/
	protected final static String[] SORTED_AVAILABLE_FONT_FAMILY_NAMES;

	
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
	public Font getFont(final AttributeSet attributeSet)
	{
		return getFont(attributeSet, 1.0f);	//get a font with 100% zoom
	}

	/**Gets the font from an attribute set using CSS names instead of the default
		Swing names. This is implemented to try and fetch a cached font for the given
		AttributeSet, and if that fails the font features are resolved and the font
		is fetched from the low-level font cache.
		Modified from javax.swing.text.StyleContext.getFont().
	@param attributeSet The attribute set from which to retrieve values.
	@param zoomFactor The relative increase or decrease in font size.
	@return The constructed font.
	@see StyleContext#getFont
	*/
	public Font getFont(final AttributeSet attributeSet, final float zoomFactor)
	{
		int style=Font.PLAIN;	//start out assuming we'll have a plain font
		if(XMLCSSStyleUtilities.isBold(attributeSet))	//if the attributes specify bold (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.BOLD;	//add bold to our font style
		if(XMLCSSStyleUtilities.isItalic(attributeSet))	//if the font attributes specify italics (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.ITALIC;	//add italics to our font style
		String family=null; //show that we haven't found a font family
		final String[] fontFamilyNameArray=XMLCSSStyleUtilities.getFontFamilyNames(attributeSet); //get the array of font family names
		for(int i=0; i<fontFamilyNameArray.length; ++i) //look at each of the specified fonts
		{
		  final String fontFamilyName=fontFamilyNameArray[i]; //get this font family name
	//G***del Log.trace("Looking for font family name: ", fontFamilyName);
		  if(fontFamilyName.equals("monospace"))  //G***fix all this; tidy; comment
			{
				family="Monospaced";
				break;
			}
		  else if(fontFamilyName.equals("serif"))  //G***fix all this; tidy; comment
			{
				family="Serif";
				break;
			}
		  else if(fontFamilyName.equals("sans-serif"))  //G***fix all this; tidy; comment
			{
	//G***fix				family="Lucinda Sans Regular";
				family="SansSerif";
				break;
			}
		  else if(fontFamilyName.equals("symbol"))  //G***fix all this; tidy; comment
			{
				family="Symbol";
				break;
			}
			//G***maybe fix for "Symbol"
				//see if we have the specified font
			final int fontFamilyNameIndex=Arrays.binarySearch(SORTED_AVAILABLE_FONT_FAMILY_NAMES, fontFamilyName);
			if(fontFamilyNameIndex>=0)  //if we have the specified font
			{
				family=fontFamilyName;  //show that we found a font family
				break;  //stop searching
			}
		}
		if(family==null)  //if we didn't find a font family
	//G***del			family="Code2000";   //G***testing
	//G***fix			family="Baraha Devanagari Unicode";   //G***testing
			family="Serif";   //use the default G***use a constant; maybe use a different default
		float size=XMLCSSStyleUtilities.getFontSize(attributeSet);	//get the font size from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)		
		/*G***put this in the style instead
			//if the attributes specify either superscript or subscript (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			if(StyleConstants.isSuperscript(attributeSet) || StyleConstants.isSubscript(attributeSet))	//G***change to use CSS attributes
				size-=2;	//reduce the font size by two
		*/
		size*=zoomFactor;	//increase the font size by the specified amout
//TODO del Log.trace("new size", size);
		return getFont(family, style, Math.round(size));	//get the font based upon the specifications we just got from attribute set
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


	static
	{
			//get the list of available font family names
		SORTED_AVAILABLE_FONT_FAMILY_NAMES=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Arrays.sort(SORTED_AVAILABLE_FONT_FAMILY_NAMES);  //sort the array of font family names		
	}
}
