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
/*G***del
if(c<32)  //G***testing; used to get around the initial '\n' stored in a paragraph; fix
	return null;

//G***del Debug.trace("Looking for character "+Integer.toHexString(c));
		final Character character=new Character(c); //create a character object to use as a key to lookup the character in the map
		//see if we know about a font family name for this character
		final String characterFamilyName=(String)characterFontFamilyNameMap.get(character);
		if(characterFamilyName!=null) //if we found a family name for this character
		{
//G***del Debug.trace("found matching character in map");
		  final Font characterFont=getFont(characterFamilyName, style, size); //create the font for the character
			if(characterFont.canDisplay(c)) //if the font can really display the character
				return characterFont; //return the font
			else  //if the font can't display the character
				characterFontFamilyNameMap.remove(character); //remove the character key from the map; it was misleading
		}
		//see which Unicode block this character is in
		final Character.UnicodeBlock unicodeBlock=Character.UnicodeBlock.of(c);
//G***del Debug.trace("character in unicode block: "+unicodeBlock); //G***del
		//see if we know about a font family name for this block
		final String blockFamilyName=(String)characterFontFamilyNameMap.get(unicodeBlock);
		if(blockFamilyName!=null) //if we found a family name for this character
		{
//G***del Debug.trace("found matching Unicode block");
		  final Font blockFont=getFont(blockFamilyName, style, size); //create the font for the Unicode block
			if(blockFont.canDisplay(c)) //if the font can really display the character
				return blockFont; //return the font
			else  //if the font can't display the character
				characterFontFamilyNameMap.remove(unicodeBlock); //remove the unicode block key from the map; it was misleading
		}
		Font chosenFont=null; //if we can't find the font in the map, we'll try to find one
		  //try suggestions
		String[] possibleFontFamilyNames=null; //we'll determine several font family names to try
//G***fix for Arabic				if(unicodeBlock.equals(Character.UnicodeBlock.ARABIC) ||  //if this is an unrecognized Arabic character G***what about the other Arabic sections?
//G***fix for Arabic					return getFont(childView, "Lucinda Sans Regular", font.getStyle(), font.getSize()); //use the font we know can display this character correctly G***fix, use constants G***what about the font.getSize2D()?
//G***fix for Arabic				if(unicodeBlock.equals(Character.UnicodeBlock.ARABIC))  //if this is an unrecognized Arabic character
//G***fix for Arabic					return getFont(childView, "Lucinda Sans Regular", font.getStyle(), font.getSize()); //use the font we know can display this character correctly G***fix, use constants G***what about the font.getSize2D()?
		  //G***add Batang and others to the last-resort fonts
		if(unicodeBlock.equals(Character.UnicodeBlock.ARROWS))  //if this is an arrow
			possibleFontFamilyNames=new String[]{"Lucida Sans Regular", "Berling Antiqua", "Batang"};  //show which font family names we want to try G***use a pre-created static version
		else if(unicodeBlock.equals(Character.UnicodeBlock.LETTERLIKE_SYMBOLS))  //if this is a letter-like symbol
			possibleFontFamilyNames=new String[]{"Lucida Sans Regular", "Berling Antiqua"};  //show which font family names we want to try G***use a pre-created static version
		else if(unicodeBlock.equals(Character.UnicodeBlock.GENERAL_PUNCTUATION))  //if this is general punctuation
			possibleFontFamilyNames=new String[]{"Berling Antiqua", "Lucida Sans Regular"};  //show which font family names we want to try G***use a pre-created static version
		else  //if we have no suggestions
		{
Debug.trace("Font cannot support character: "+Integer.toHexString(c)+", but we have no suggestions");
			possibleFontFamilyNames=new String[]{"Lucida Sans Regular", "Code2000"};  //always try the installed font, along with Code2000 G***use a pre-created static version
		}
			//try the suggested fonts based upon the Unicode block G***we might want to make sure each font is available on the system, first
		if(possibleFontFamilyNames!=null) //if know of several font family names to try
		{
Debug.trace("Trying possible fonts");
			for(int i=0; i<possibleFontFamilyNames.length; ++i)  //look at each font family name
			{
				final Font possibleFont=new Font(possibleFontFamilyNames[i], style, size); //create a font with the possible name, but don't get it using getFont() because we're not sure we want to add it to our cache
				if(possibleFont.canDisplay(c))  //if the font can display the character
				{
Debug.trace("Character "+Integer.toHexString(c)+" used suggested font: "+possibleFont);
					chosenFont=getFont(possibleFontFamilyNames[i], style, size); //choose a font after getting it with getFont(), which will add it to our cache for next time
//G***del				  chosenFont=possibleFont;  //show that we've chosen a font
					break;  //stop searching
//G***del					return possibleFont;  //return the font
				}
			}
		}
		//if none of the suggested fonts contain the character we want, search all available fonts
		//(this takes some time and should be done only as a last resort)
		if(chosenFont==null)  //if we haven't found a font, yet
		{
			final String[] availableFontFamilyNames=getAvailableFontFamilyNames(); //get the available font family names
			for(int i=0; i<availableFontFamilyNames.length; ++i)  //look at each available font
			{
				final Font availableFont=new Font(availableFontFamilyNames[i], style, size); //create a the available font, but don't get it using getFont() because we're not sure we want to add it to our cache
Debug.trace("trying font: ", availableFont);  //G***del
//G***del				final Font availableFont=getFont(availableFontFamilyNames[i], style, size); //create the available font
				if(availableFont.canDisplay(c))  //if the font can display the character
				{
Debug.trace("Character "+Integer.toHexString(c)+" not found; had to search available fonts, found: "+availableFont);
					chosenFont=getFont(availableFontFamilyNames[i], style, size); //choose a font after getting it with getFont(), which will add it to our cache for next time
//G***del				  chosenFont=availableFont;  //show that we've chosen a font
					break;  //stop searching
//G***del					return availableFont;  //return the font
				}
			}
		}
		if(chosenFont!=null)  //if we found a font
		{
//G***del Debug.trace("Storing character keyed to character: "+character);
			characterFontFamilyNameMap.put(character, chosenFont.getFamily()); //store the family name in the map keyed to the character
//G***del Debug.trace("Stored object: "+characterFontFamilyNameMap.get(character)); //G***del; testing
//G***del Debug.trace("Stored object from new character: "+characterFontFamilyNameMap.get(new Character(character.charValue())));
			characterFontFamilyNameMap.put(unicodeBlock, chosenFont.getFamily()); //store the name in the map keyed to the Unicode block
Debug.trace("Finally chose font: ", chosenFont); //G***del
		  return chosenFont;  //return the font we chose
		}
		else  //if we could not find a font for the character
			return null;  //show that we could not find a font that can display the specified character G***it would be nice to cache the not-found characters as well
*/
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
		if(XMLCSSStyleConstants.isBold(attributeSet))	//if the attributes specify bold (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.BOLD;	//add bold to our font style
		if(XMLCSSStyleConstants.isItalic(attributeSet))	//if the font attributes specify italics (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.ITALIC;	//add italics to our font style
		final String family=StyleConstants.getFontFamily(attributeSet);	//get the font family from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set) G***change to use CSS attributes
		int size=Math.round(XMLCSSStyleConstants.getFontSize(attributeSet));	//get the font size from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
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
		return XMLCSSStyleConstants.getForeground(attributeSet);	//let the CSS style constants get the foreground for us
	}

	/**Takes a set of attributes and turn it into a background color specification.
	@param attributeSet The set of attributes.
	@return The background color.
	*/
	public Color getBackground(final AttributeSet attributeSet)
	{
		return XMLCSSStyleConstants.getBackgroundColor(attributeSet);	//let the CSS style constants get the background for us G***resolve inconsistency between getForeground() and getBackgroundColor()
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
