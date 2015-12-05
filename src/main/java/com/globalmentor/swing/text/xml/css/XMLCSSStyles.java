/*
 * Copyright © 1996-2011 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.swing.text.xml.css;

import java.awt.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import org.w3c.dom.css.*;

import com.globalmentor.log.Log;
import com.globalmentor.xml.dom.impl.stylesheets.css.XMLCSSPrimitiveValue;

import static com.globalmentor.swing.text.Styles.*;
import static com.globalmentor.swing.text.xml.XMLStyles.*;
import static com.globalmentor.w3c.spec.CSS.*;

/**
 * A collection of utilities used for rendering XML using CSS in Swing.
 * @author Garret Wilson
 */
public class XMLCSSStyles {

	//predefined colors
	public static final Color COLOR_BLACK = new Color(CSS_COLOR_BLACK_VALUE);
	public static final Color COLOR_GREEN = new Color(CSS_COLOR_GREEN_VALUE);
	public static final Color COLOR_SILVER = new Color(CSS_COLOR_SILVER_VALUE);
	public static final Color COLOR_LIME = new Color(CSS_COLOR_LIME_VALUE);
	public static final Color COLOR_GRAY = new Color(CSS_COLOR_GRAY_VALUE);
	public static final Color COLOR_OLIVE = new Color(CSS_COLOR_OLIVE_VALUE);
	public static final Color COLOR_WHITE = new Color(CSS_COLOR_WHITE_VALUE);
	public static final Color COLOR_YELLOW = new Color(CSS_COLOR_YELLOW_VALUE);
	public static final Color COLOR_MAROON = new Color(CSS_COLOR_MAROON_VALUE);
	public static final Color COLOR_NAVY = new Color(CSS_COLOR_NAVY_VALUE);
	public static final Color COLOR_RED = new Color(CSS_COLOR_RED_VALUE);
	public static final Color COLOR_BLUE = new Color(CSS_COLOR_BLUE_VALUE);
	public static final Color COLOR_PURPLE = new Color(CSS_COLOR_PURPLE_VALUE);
	public static final Color COLOR_TEAL = new Color(CSS_COLOR_TEAL_VALUE);
	public static final Color COLOR_FUCHSIA = new Color(CSS_COLOR_FUCHSIA_VALUE);
	public static final Color COLOR_AQUA = new Color(CSS_COLOR_AQUA_VALUE);

	/** The name of the XML CSS style declaration map. */
	public static final String XML_CSS_STYLE_ATTRIBUTE_NAME = "$xmlCSSStyle"; //TODO maybe change this to just "$cssStyle" if we decide to completely use the CSS DOM

	/**
	 * Gets the XML CSS style declaration the Swing attributes represent. The attribute is not resolved through the parent attribute set hierarchy.
	 * @param a The attribute set, which may be <code>null</code>.
	 * @return The XML CSS style declaration map.
	 */
	public static CSSStyleDeclaration getXMLCSSStyle(AttributeSet a) {
		return (CSSStyleDeclaration)getDefinedAttribute(a, XML_CSS_STYLE_ATTRIBUTE_NAME); //get the attribute
	}

	/**
	 * Sets the XML CSS style declaration map.
	 * @param a The attribute set
	 * @param cssStyle The XML CSS style declaration map.
	 */
	public static void setXMLCSSStyle(final MutableAttributeSet a, final CSSStyleDeclaration cssStyle) {
		a.addAttribute(XML_CSS_STYLE_ATTRIBUTE_NAME, cssStyle); //add the attribute to the attribute set
	}

	/**
	 * Gets the CSS value object or a particular CSS property.
	 * @param attributeSet The attribute set.
	 * @param cssPropertyName The name of the CSS property to search for.
	 * @param resolve Whether the attribute set's parent hierarchy should be searched to find this CSS value if not found in this attribute set.
	 * @return The CSS value object for the given property, or <code>null</code> if that property cannot be found.
	 */
	public static CSSValue getCSSPropertyCSSValue(AttributeSet attributeSet, final String cssPropertyName, final boolean resolve) {
		final CSSStyleDeclaration cssStyle = getXMLCSSStyle(attributeSet); //get the CSS style from the attribute set
		if(cssStyle != null) { //if there is a CSS style declaration object in the attribute set
			final CSSValue cssValue = cssStyle.getPropertyCSSValue(cssPropertyName); //get the property as a CSSValue object
			if(cssValue != null) //if the style contains the given CSS property
				return cssValue; //return the value
		}
		if(resolve) { //since the property isn't in this attribute set, see if we should resolve up the chain; if so
			final AttributeSet resolveParent = attributeSet.getResolveParent(); //get the parent to use to resolve this attribute
			if(resolveParent != null) //if we have a resolve parent
				return getCSSPropertyCSSValue(resolveParent, cssPropertyName, resolve); //try to get the value from the resolving parent
		}
		return null; //show that we couldn't find the CSS property value anywhere
	}

	/**
	 * Gets the a particular value from the attribute set that should be a color.
	 * @param attributeSet The attribute set.
	 * @param cssProperty The name of the CSS property for which a color value should be retrieved.
	 * @param resolve Whether the attribute set's parent hierarchy should be searched to find this CSS value if not found in this attribute set.
	 * @return The specified CSS color, or <code>null</code> if there is no color specified.
	 */
	protected static Color getColorValue(final AttributeSet attributeSet, final String cssProperty, final boolean resolve) {
		final XMLCSSPrimitiveValue colorValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, cssProperty, resolve); //get the value, specifying whether we should resolve up the hierarchy
		return getColor(colorValue); //find the color of the value
	}

	/**
	 * Gets a color from a CSS primitive value.
	 * @param colorValue The CSS primitive value that holds the color. If <code>null</code> is passed, <code>null</code> will be returned..
	 * @return The specified CSS color, or <code>null</code> if the specified color value is null.
	 */
	public static Color getColor(final CSSPrimitiveValue colorValue) {
		if(colorValue != null) { //if we have a color value
			if(colorValue.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) { //if the color is specified by an identifier

				final String colorString = colorValue.getStringValue(); //get the string representing the color
				return getColor(colorString); //return a color based upon the string
			} else if(colorValue.getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) { //if the color is specified by an RGB color value
				return getColor(colorValue.getRGBColorValue()); //convert the color value to a color
			}
		}
		return null; //default to not returning a color
	}

	/**
	 * Gets a color object from a string.
	 * @param colorString The string (possibly <code>null</code>) which represents one of the 16 HTML colors.
	 * @return A color object representing the color, or <code>null</code> if the specified string is <code>null</code> or does not represent a color.
	 */
	public static Color getColor(final String colorString) {
		if(colorString != null) { //if we have a color string
			//TODO in the future, maybe put this in XMLCSSPrimitiveValue or, better yet, in a ColorManipulator class or something -- maybe even in an XHTML-related class
			//TODO add a map to look these up
			if(colorString.equalsIgnoreCase(CSS_COLOR_BLACK)) //see if we recognize any of the colors
				return COLOR_BLACK;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_GREEN))
				return COLOR_GREEN;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_SILVER))
				return COLOR_SILVER;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_LIME))
				return COLOR_LIME;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_GRAY))
				return COLOR_GRAY;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_OLIVE))
				return COLOR_OLIVE;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_WHITE))
				return COLOR_WHITE;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_YELLOW))
				return COLOR_YELLOW;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_MAROON))
				return COLOR_MAROON;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_NAVY))
				return COLOR_NAVY;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_RED))
				return COLOR_RED;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_BLUE))
				return COLOR_BLUE;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_PURPLE))
				return COLOR_PURPLE;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_TEAL))
				return COLOR_TEAL;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_FUCHSIA))
				return COLOR_FUCHSIA;
			else if(colorString.equalsIgnoreCase(CSS_COLOR_AQUA))
				return COLOR_AQUA;
		}
		return null; //default to not returning a color
	}

	/**
	 * Gets a color object from a DOM <code>RGBColor</code> object.
	 * @param rgbColor The object which contains a DOM representation of the RGB color.
	 * @return A color object representing the color.
	 */
	public static Color getColor(final RGBColor rgbColor) {
		return new Color( //create a new color object from the components of the RGBColor
				(int)rgbColor.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER), //get the red value
				(int)rgbColor.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER), //get the green value
				(int)rgbColor.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER)); //get the blue value
	}

	/**
	 * Gets a particular value from the attribute set that should be a length, returning the length in pixels. TODO fix; right now it returns points
	 * @param attributeSet The attribute set.
	 * @param cssProperty The name of the CSS property for which a color value should be retrieved.
	 * @param resolve Whether the attribute set's parent hierarchy should be searched to find this CSS value if not found in this attribute set.
	 * @param defaultValue The default value to use if the property does not exist.
	 * @param font The font to be used when calculating relative lengths, such as <code>ems</code>.
	 * @return The length in pixels. //TODO what happens if the value is not a length? probably just return the default
	 */
	protected static float getPixelLength(final AttributeSet attributeSet, final String cssProperty, final boolean resolve, final float defaultValue,
			final Font font) {
		final XMLCSSPrimitiveValue primitiveValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, cssProperty, resolve); //get CSS value for this property, resolving up the hierarchy if we should
		if(primitiveValue != null) { //if we have a value
			switch(primitiveValue.getPrimitiveType()) { //see which type of primitive type we have
				case XMLCSSPrimitiveValue.CSS_EMS: //if this is the ems property
					if(font != null) //if we have a font
						return font.getSize() * primitiveValue.getFloatValue(XMLCSSPrimitiveValue.CSS_EMS); //TODO fix; this probably isn't the same as the defined font size, which is what CSS calls for for EMS
					break;
				case XMLCSSPrimitiveValue.CSS_PT: //if they want the size in points
					return primitiveValue.getFloatValue(CSSPrimitiveValue.CSS_PT); //get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
			}
		}
		return defaultValue; //if we couldn't determine the value, return the default value
	}

	/**
	 * Gets the CSS <code>display</code> setting from the attribute set without searching the attribute hierarchy.
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The display setting or <code>null</code> if none is specified.
	 */
	public static String getDisplay(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue displayValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_DISPLAY, false); //get the display property for this element, but don't resolve up the attribute set parent hierarchy TODO can we be sure this will be a primitive value?
		return displayValue != null ? displayValue.getStringValue() : null; //return the value of the display property
	}

	/**
	 * Gets a list of CSS font-family values if the property is defined anywhere up the hierarchy. If no font family is defined, a list with the generic font
	 * "serif" is returned. TODO maybe return "monospace"
	 * @param attributeSet The attribute set.
	 * @return An array of names of font families, each either a font family name or a generic family such as "serif".
	 */
	public static String[] getFontFamilyNames(AttributeSet attributeSet) {
		final CSSValue cssValue = getCSSPropertyCSSValue(attributeSet, CSS_PROP_FONT_FAMILY, true); //get the font family, resolving up the hierarchy
		if(cssValue != null) { //if we have a font family
		//TODO del			if(cssValue.getCssValueType()==cssValue.
			final CSSValueList cssValueList = (CSSValueList)cssValue; //cast the value to a value list TODO will font-family *always* be a value list?
			final String[] fontFamilyArray = new String[cssValueList.getLength()]; //create an array of strings to hold the font family names TODO eventually, we might have a utility to automatically create an array of strings from a cssValueList
			for(int i = 0; i < cssValueList.getLength(); ++i) { //look at each font family in the list
				final CSSPrimitiveValue cssPrimitiveValue = (CSSPrimitiveValue)cssValueList.item(i); //get this font family name
				fontFamilyArray[i] = cssPrimitiveValue.getStringValue(); //get the font family name and store it int he array
				//TODO del				final String familyName=cssPrimitiveValue.getStringValue();  //get the font family name
			}
			return fontFamilyArray; //return the array of values
		} else
			//if no font family is defined
			return new String[] { "serif" }; //return the default, "serif" TODO use a constant here; create this beforehand so we don't have to create it every time
	}

	/**
	 * Gets the CSS font size setting from the attribute set in points.
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The font size in points or, if no font size is specified, the default point size, which is 12.
	 */
	public static float getFontSize(AttributeSet attributeSet) {
		final CSSStyleDeclaration cssStyle = getXMLCSSStyle(attributeSet); //get the CSS style
		//TODO del		final XMLCSSPrimitiveValue fontSizeValue=cssStyle!= null ? (XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_FONT_SIZE);	//get the CSS font size value
		//TODO del System.out.println("Getting font size for attribute set of: "+attributeSet.getAttribute(StyleConstants.NameAttribute));	//TODO del
		//TODO del when works		if(attributeSet.isDefined(CSS_PROP_FONT_SIZE))	//if this attribute set has the font size property defined
		if(cssStyle != null && cssStyle.getPropertyCSSValue(CSS_PROP_FONT_SIZE) != null) { //if there is a style and it has the font size property defined TODO seve the CSS value, now that we have it
		//TODO del System.out.println("Found font size attribute in: "+attributeSet.getAttribute(StyleConstants.NameAttribute));	//TODO del
		//TODO del			final XMLCSSPrimitiveValue fontSizeValue=(XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_FONT_SIZE);	//get the CSS font size value
			final XMLCSSPrimitiveValue fontSizeValue = (XMLCSSPrimitiveValue)cssStyle.getPropertyCSSValue(CSS_PROP_FONT_SIZE); //get the CSS font size value
			if(fontSizeValue != null) { //if we have a font size value
				if(fontSizeValue.isAbsoluteLength()) //if this is an absolute length
					return fontSizeValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT); //get the value in points and round to the nearest integer point size
				else { //if this is not an absolute length, it could be an absolute size string, relative length, or relative size string
					float percentageValue = 1; //if we find a percentage value, we'll store it here (we won't really store a percentage, but the actual float number; that is, percentage/100) (we're defaulting to 1 because the compiler will complain without some value, even though this always gets changed by the logic of the code)
					boolean isPercentage = fontSizeValue.getPrimitiveType() == XMLCSSPrimitiveValue.CSS_PERCENTAGE; //see if this is a percentage value
					if(isPercentage) //if this is an explicit percentage value
						percentageValue = fontSizeValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PERCENTAGE) / 100; //store the percentage value as a scaling value
					//if this isn't an explicit percentage value, it might be a relative value, an absolute string value, or a relative string value that is the same as a percentage
					else if(fontSizeValue.isStringType()) { //if this is a string type
						final String fontSizeString = fontSizeValue.getStringValue(); //get the font size string
						//TODO del System.out.println("Font size is a string type: "+fontSizeString);	//TODO del
						if(fontSizeString.equals(CSS_FONT_SIZE_MEDIUM)) //if this is a medium-sized font
							return DEFAULT_FONT_SIZE; //medium fonts get the default size
						else if(fontSizeString.equals(CSS_FONT_SIZE_LARGE)) //if this is a large-sized font
							return DEFAULT_FONT_SIZE * (FONT_SIZE_SCALING_FACTOR); //scale the large font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_X_LARGE)) //if this is an x-large-sized font
							return DEFAULT_FONT_SIZE * (FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR); //scale the x-large font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_XX_LARGE)) //if this is an xx-large-sized font
							return DEFAULT_FONT_SIZE * (FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR); //scale the xx-large font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_SMALL)) //if this is a small-sized font
							return DEFAULT_FONT_SIZE / (FONT_SIZE_SCALING_FACTOR); //scale the small font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_X_SMALL)) //if this is an x-small-sized font
							return DEFAULT_FONT_SIZE / (FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR); //scale the x-small font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_XX_SMALL)) //if this is an xx-small-sized font
							return DEFAULT_FONT_SIZE / (FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR * FONT_SIZE_SCALING_FACTOR); //scale the xx-small font by the CSS2 scaling factor
						else if(fontSizeString.equals(CSS_FONT_SIZE_LARGER)) { //if this is a larger font
							isPercentage = true; //this is the same a giving a percentage
							percentageValue = FONT_SIZE_SCALING_FACTOR; //store the scaling factor as our percentage

						} else if(fontSizeString.equals(CSS_FONT_SIZE_SMALLER)) { //if this is a smaller font
							isPercentage = true; //this is the same a giving a percentage
							percentageValue = 1 / FONT_SIZE_SCALING_FACTOR; //store the inverse of the scaling factor as our percentage
						}
					}
					if(isPercentage) { //if this is a percentage value (either an explicit percentage or a relative string value)
						final AttributeSet resolveParent = attributeSet.getResolveParent(); //get the parent to use to resolve this attribute
						if(resolveParent != null) //if we have a resolve parent
							return percentageValue * getFontSize(resolveParent); //multiply the percentage with the font size from the parent
						else
							//if we don't have a resolve parent
							return percentageValue * DEFAULT_FONT_SIZE; //multiply the percentage with the default font size
					}
					//TODO add support for relative sizes (ems, exs, etc.) here: else
				}
			}
		} else { //if this property isn't defined in this attribute set
		//TODO del System.out.println("Did not find font size attribute, checking parent.");	//TODO del
			//we'll resolve it ourselves; we're doing this because, with automatically
			//resolving relative values, we can't just get 50%, say, of the resolving
			//parent's value, because that may where the original 50% came from in the
			//first place
			final AttributeSet resolveParent = attributeSet.getResolveParent(); //get the parent to use to resolve this attribute
			if(resolveParent != null) //if we have a resolve parent
				return getFontSize(resolveParent); //get the font size from the parent, which will works its way up the chain, guaranteeing that the body of this function only works on attribute sets with the value defined
		}
		return DEFAULT_FONT_SIZE; //return the default point size, since we couldn't find an alternative
	}

	/**
	 * Sets the font size attribute.
	 *
	 * @param a the attribute set
	 * @param s the font size
	 */
	/*TODO fix
			public static void setFontSize(MutableAttributeSet a, int s) {
					a.addAttribute(FontSize, new Integer(s));
			}
	*/

	/**
	 * Tells whether the CSS bold attribute is set.
	 * @param attributeSet The attribute set with CSS attributes.
	 * @return Whether the bold attribute is set.
	 */
	public static boolean isBold(AttributeSet attributeSet) {
		//TODO del		final XMLCSSPrimitiveValue boldValue=(XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_FONT_WEIGHT);	//get the bold value
		final XMLCSSPrimitiveValue boldValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_FONT_WEIGHT, true); //get the bold value, resolving up the hierarchy if necessary
		if(boldValue != null) { //if we have a bold value
			final String fontWeightString = boldValue.getStringValue(); //get the string representing the font weight
			if(fontWeightString.equals(CSS_FONT_WEIGHT_BOLD) || fontWeightString.equals(CSS_FONT_WEIGHT_BOLDER)) //if we should use bold here
				return true; //show that we should use bold
		}
		return false; //default to not displaying bold
	}

	/**
	 * Sets the bold attribute.
	 *
	 * @param a the attribute set
	 * @param b specifies true/false for setting the attribute
	 */
	/*TODO fix
			public static void setBold(MutableAttributeSet a, boolean b) {
					a.addAttribute(Bold, new Boolean(b));
			}
	*/

	/**
	 * Tells whether the CSS italic attribute is set.
	 * @param attributeSet The attribute set with CSS attributes.
	 * @return Whether the italic attribute is set.
	 */
	public static boolean isItalic(AttributeSet attributeSet) {
		//TODO del		final XMLCSSPrimitiveValue italicValue=(XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_FONT_STYLE);	//get the italic value
		final XMLCSSPrimitiveValue italicValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_FONT_STYLE, true); //get the italic value, resolving up the hierarchy if necessary
		if(italicValue != null) { //if we have an italic value
			final String fontStyleString = italicValue.getStringValue(); //get the string representing the font style
			if(fontStyleString.equals(CSS_FONT_STYLE_ITALIC) || fontStyleString.equals(CSS_FONT_STYLE_OBLIQUE)) //if we should use italics here
				return true; //show that we should use italics
		}
		return false; //default to not displaying italics
	}

	/**
	 * Sets the italic attribute.
	 *
	 * @param a the attribute set
	 * @param b specifies true/false for setting the attribute
	 */
	/*TODO fix
			public static void setItalic(MutableAttributeSet a, boolean b) {
					a.addAttribute(Italic, new Boolean(b));
			}
	*/

	/**
	 * Checks whether the underline attribute is set.
	 * @param attributeSet The attribute set.
	 * @return <code>true</code> if <code>text-decoration</code> has the value <code>underline</code> in its list of values, else false.
	 */
	public static boolean isUnderline(AttributeSet attributeSet) {
		/*TODO fix; throws an exception because an XMLCSSPrimitiveValue is returned
				final XMLCSSValueList textDecorationValueList=(XMLCSSValueList)getCSSPropertyCSSValue(attributeSet, CSS_PROP_TEXT_DECORATION, true);	//get the text decoration value, resolving up the hierarchy if necessary TODO text-decoration shouldn' resolve up the chain exactly like this
					//TODO what if the value sent back isn't really an XMLCSSValueList? Perhaps we should do an assert
				if(textDecorationValueList!=null) {	//if we have a list of text decoration value
					for(int i=0; i<textDecorationValueList.getLength(); ++i) {	//look at each text decoration value
						final XMLCSSPrimitiveValue textDecorationValue=(XMLCSSPrimitiveValue)textDecorationValueList.item(i);	//get the value for this item in the list TODO what if this isn't a primitive value?
						final String textDecorationString=textDecorationValue.getStringValue();	//get the string representing the test decoration
						if(textDecorationString.equals(CSS_TEXT_DECORATION_UNDERLINE))	//if this item specifies underline decoration
							return true;	//show that we should use underline
					}
				}
		*/
		return false; //default to not underlining
	}

	/**
	 * Gets the foreground color from the attribute set.
	 * @param attributeSet The attribute set.
	 * @return The specified CSS color, or <code>Color.black</code> if there is no color specified.
	 */
	//TODO why isn't this getColor()? TODO so that StyledDocument can call it TODO but does that matter? this is a static function called directly
	public static Color getForeground(AttributeSet attributeSet) {
		final Color color = getColorValue(attributeSet, CSS_PROP_COLOR, true); //get the color value, resolving up the hierarchy if necessary
		return color != null ? color : Color.black; //default to a black foreground if no color is found
	}

	/**
	 * Sets the foreground color.
	 *
	 * @param a the attribute set
	 * @param fg the color
	 */
	/*TODO fix
	    public static void setForeground(MutableAttributeSet a, Color fg) {
	        a.addAttribute(Foreground, fg);
	    }
	*/

	/**
	 * Gets the background color from the attribute set.
	 * @param attributeSet The attribute set.
	 * @return The CSS background color, or <code>null</code> if the background should be transparent.
	 */
	public static Color getBackgroundColor(AttributeSet attributeSet) {
		return getColorValue(attributeSet, CSS_PROP_BACKGROUND_COLOR, false); //get the color value without resolving up the chain, which will return null if there is no background color
	}

	/**
	 * Gets the effective background color from the attribute set. Because the CSS background color is not inherited, <code>getBackgroundColor()</code> returns
	 * <code>null</code> unless a local background is set. This method walks the parent hierarchy until the correct background color is found.
	 * @param attributeSet The attribute set.
	 * @return The effective CSS background color, or <code>Color.white</code> if there is no color specified anywhere up the hierarchy.
	 */
	public static Color getEffectiveBackgroundColor(AttributeSet attributeSet) {
		final Color backgroundColor = getColorValue(attributeSet, CSS_PROP_BACKGROUND_COLOR, true); //get the background color value, resolving up the hierarchy if necessary
		return backgroundColor != null ? backgroundColor : Color.white; //default to a white background if no color is found
	}

	/**
	 * Sets the foreground color.
	 *
	 * @param a the attribute set
	 * @param fg the color
	 */
	/*TODO fix
	    public static void setBackground(MutableAttributeSet a, Color fg) {
	        a.addAttribute(Foreground, fg);
	    }
	*/

	/**
	 * Gets the CSS "line-height" setting from the attribute as a number.
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The line height as a number or, if line height is not specified, the default amount, which is 1.0.
	 */
	//TODO this all needs to be fixed; this is just hacked at the moment
	public static float getLineHeight(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue lineHeightValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_LINE_HEIGHT, true); //get CSS value for this property, resolving up the hierarchy if necessary
		if(lineHeightValue != null) { //if we have a value
			if(lineHeightValue.getPrimitiveType() == XMLCSSPrimitiveValue.CSS_NUMBER) //if this is a number (fix all this, because the number doesn't mean a percentage of the normal line flow height, which is how we interpret it now)
				return lineHeightValue.getFloatValue(XMLCSSPrimitiveValue.CSS_NUMBER); //return the number

			//TODO fix for percentages here as well

			/*TODO fix
						if(textIndentValue.isAbsoluteLength()) {	//if this is an absolute length
			//TODO del System.out.println("It's an absolute length: "+textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PX));	//TODO del
							return Math.round(textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT));	//get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
						}
						else {	//if this is not an absolute length, it could be a relative length or a percentage
			//TODO fix
						}
			*/
		}
		return DEFAULT_LINE_HEIGHT; //return the default value, since we couldn't find an alternative
	}

	/**
	 * Sets the font size attribute.
	 *
	 * @param a the attribute set
	 * @param s the font size
	 */
	/*TODO fix
			public static void setLineHeight(MutableAttributeSet a, int s) {
					a.addAttribute(FontSize, new Integer(s));
			}
	*/

	/**
	 * Gets the inherited CSS <code>list-style-type</code> setting from the attribute set.
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The left list style type, or <code>null</code> if the property is not specified.
	 */
	public static String getListStyleType(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue listStyleTypeValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_LIST_STYLE_TYPE, true); //get CSS value for this property, resolving up the hierarchy
		if(listStyleTypeValue != null) { //if we have a value
			return listStyleTypeValue.getStringValue(); //return the value as a string
		}
		return null; //show that the list style was not specified
	}

	/**
	 * Gets the CSS <code>margin-bottom</code> setting from the attribute set in pixels. TODO actually, right now it returns the value in points; fix this
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The bottom margin size in pixels or, if the property is not specified, the default amount of 0.
	 */
	public static float getMarginBottom(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue marginBottomValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_MARGIN_BOTTOM, false); //get CSS value for this property without resolving up the hierarchy
		if(marginBottomValue != null) { //if we have a value
			if(marginBottomValue.isAbsoluteLength()) { //if this is an absolute length
				return marginBottomValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT); //get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
			} else { //if this is not an absolute length, it could be a relative length or a percentage
				//TODO fix
			}
		}
		return 0; //return the default value, since we couldn't find an alternative
	}

	/**
	 * Gets the CSS <code>margin-left</code> setting from the attribute set in pixels. TODO actually, right now it returns the value in points; fix this
	 * @param attributeSet The attribute set containing CSS properties.
	 * @param font The font to be used when calculating relative lengths, such as <code>ems</code>.
	 * @return The left margin size in pixels or, if the property is not specified, the default amount of 0.
	 */
	public static float getMarginLeft(final AttributeSet attributeSet, final Font font) {
		return getPixelLength(attributeSet, CSS_PROP_MARGIN_LEFT, false, 0, font); //return the length in pixels without resolving
		/*TODO del when works
				final XMLCSSPrimitiveValue marginLeftValue=(XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_MARGIN_LEFT, false);	//get CSS value for this property without resolving up the hierarchy
				if(marginLeftValue!=null) {	//if we have a value
					if(marginLeftValue.isAbsoluteLength()) {	//if this is an absolute length
						return marginLeftValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT);	//get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
					}
					else {	//if this is not an absolute length, it could be a relative length or a percentage
						//TODO fix
					}
				}
				return 0;	//return the default value, since we couldn't find an alternative
		*/
	}

	/**
	 * Gets the CSS <code>margin-right</code> setting from the attribute set in pixels. TODO actually, right now it returns the value in points; fix this
	 * @param attributeSet The attribute set containing CSS properties.
	 * @param font The font to be used when calculating relative lengths, such as <code>ems</code>.
	 * @return The right margin size in pixels or, if the property is not specified, the default amount of 0.
	 */
	public static float getMarginRight(AttributeSet attributeSet, final Font font) {
		return getPixelLength(attributeSet, CSS_PROP_MARGIN_RIGHT, false, 0, font); //return the length in pixels without resolving
		/*TODO del
				final XMLCSSPrimitiveValue marginRightValue=(XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_MARGIN_RIGHT, false);	//get CSS value for this property without resolving up the hierarchy
				if(marginRightValue!=null) {	//if we have a value
					if(marginRightValue.isAbsoluteLength()) {	//if this is an absolute length
						return marginRightValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT);	//get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
					}
					else {	//if this is not an absolute length, it could be a relative length or a percentage
						//TODO fix
					}
				}
				return 0;	//return the default value, since we couldn't find an alternative
		*/
	}

	/**
	 * Gets the CSS <code>margin-top</code> setting from the attribute set in pixels. TODO actually, right now it returns the value in points; fix this
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The top margin size in pixels or, if the property is not specified, the default amount of 0.
	 */
	public static float getMarginTop(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue marginTopValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_MARGIN_TOP, false); //get CSS value for this property without resolving up the hierarchy
		if(marginTopValue != null) { //if we have a value
			if(marginTopValue.isAbsoluteLength()) { //if this is an absolute length
				return marginTopValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT); //get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
			} else { //if this is not an absolute length, it could be a relative length or a percentage
				//TODO fix
			}
		}
		return 0; //return the default value, since we couldn't find an alternative
	}

	/**
	 * Gets the CSS <code>page-break-after</code> setting from the attribute set without searching the attribute hierarchy unless "inherit" is specified. TODO fix
	 * inherit
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The page break after setting or <code>null</code> if none is specified.
	 */
	public static String getPageBreakAfter(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue pageBreakAfterValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_PAGE_BREAK_AFTER, false); //get the property for this element, but don't resolve up the attribute set parent hierarchy TODO can we be sure this will be a primitive value?
		return pageBreakAfterValue != null ? pageBreakAfterValue.getStringValue() : null; //return the value of the property
	}

	/**
	 * Gets the CSS <code>page-break-before</code> setting from the attribute set without searching the attribute hierarchy unless "inherit" is specified. TODO
	 * fix inherit
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The page break after setting or <code>null</code> if none is specified.
	 */
	public static String getPageBreakBefore(AttributeSet attributeSet) {
		final XMLCSSPrimitiveValue pageBreakBeforeValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_PAGE_BREAK_BEFORE, false); //get the property for this element, but don't resolve up the attribute set parent hierarchy TODO can we be sure this will be a primitive value?
		return pageBreakBeforeValue != null ? pageBreakBeforeValue.getStringValue() : null; //return the value of the property
	}

	/**
	 * Gets the CSS <code>text-indent</code> setting from the attribute set in pixels. TODO actually, right now it returns the value in points; fix this
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The text indent amount in pixels or, if text indent is not specified, the default amount, which is 0. //TODO testing font; comment
	 */
	public static float getTextIndent(final AttributeSet attributeSet, final Font font) { //TODO we'll probably need to pass a length here or something
	//TODO del System.out.println("Getting font size for attribute set of: "+attributeSet.getAttribute(StyleConstants.NameAttribute));	//TODO del
		Log.trace("Getting text indent for attribute set."); //TODO del
		//TODO del		final XMLCSSPrimitiveValue textIndentValue=(XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_TEXT_INDENT);	//get the CSS value for this property
		final XMLCSSPrimitiveValue textIndentValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_TEXT_INDENT, true); //get CSS value for this property, resolving up the hierarchy if necessary
		if(textIndentValue != null) { //if we have a value
			Log.trace("Fount text indent value."); //TODO del
			//TODO del if not needed			if(textIndentValue.isAbsoluteLength())	//if this is an absolute length
			{
				switch(textIndentValue.getPrimitiveType()) { //see which type of primitive type we have
				//TODO del Log.trace("It's an absolute length: "+textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PX));	//TODO del
				//TODO del				if(textIndentValue.getPrimitiveType()==textIndentValue.CSS_EMS) //if they are asking for a length in ems
					case XMLCSSPrimitiveValue.CSS_EMS: //TODO testing
						return font.getSize() * textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_EMS); //TODO fix; this probably isn't the same as the defined font size, which is what CSS calls for for EMS
						/*TODO fix
												{
							Log.trace("They want ems.");
															// As a practical matter, this FRC will almost always
															// be the right one.
															AffineTransform xf
																	= GraphicsEnvironment.getLocalGraphicsEnvironment()
																	.getDefaultScreenDevice().getDefaultConfiguration()
																	.getDefaultTransform(); //TODO testing
													final FontRenderContext fontRenderContext=new FontRenderContext(xf, false, false);  //TODO we should really get the font render context from somewhere else; for now, this should get close
													final float emSize=(float)font.getStringBounds("m", fontRenderContext).getWidth(); //get the size of an em
								Log.trace("each em is: ", new Float(emSize)); //TODO del
													return emSize*textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_EMS);  //TODO testing
												}
						*/
					case XMLCSSPrimitiveValue.CSS_PT: //TODO testing
						return textIndentValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PT); //get the value in pixels and round to the nearest integer pixel length TODO fix to use pixels instead of points, as it does now
				}
			}
			//TODO del if not needed			else	//if this is not an absolute length, it could be a relative length or a percentage
			{
				/*TODO fix
									float percentageValue=1;	//if we find a percentage value, we'll store it here (we won't really store a percentage, but the actual float number; that is, percentage/100) (we're defaulting to 1 because the compiler will complain without some value, even though this always gets changed by the logic of the code)
									boolean isPercentage=;	//see if this is a percentage value
									if(isPercentage)	//if this is an explicit percentage value
										percentageValue=fontSizeValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PERCENTAGE)/100;	//store the percentage value as a scaling value
										//if this isn't an explicit percentage value, it might be a relative value, an absolute string value, or a relative string value that is the same as a percentage
									if(textIndentValue.getPrimitiveType()==XMLCSSPrimitiveValue.CSS_PERCENTAGE) {	//if this is a percentage value
										final AttributeSet resolveParent=attributeSet.getResolveParent();	//get the parent to use to resolve this attribute
										if(resolveParent!=null)	//if we have a resolve parent
											return Math.round(percentageValue*getFontSize(resolveParent));	//multiply the percentage with the font size from the parent
										else	//if we don't have a resolve parent
											return Math.round(percentageValue*DEFAULT_FONT_SIZE);	//multiply the percentage with the default font size
									}
									//TODO add support for relative sizes (ems, exs, etc.) here: else
				*/
			}
		}
		return DEFAULT_TEXT_INDENT; //return the default value, since we couldn't find an alternative
	}

	/**
	 * Sets the font size attribute.
	 *
	 * @param a the attribute set
	 * @param s the font size
	 */
	/*TODO fix
			public static void setTextIndent(MutableAttributeSet a, int s) {
					a.addAttribute(FontSize, new Integer(s));
			}
	*/

	/**
	 * Gets the CSS "vertical-align" setting from the attribute as an identifier, without searching up the hierarchy. Currently percentages and lengths are not
	 * supported
	 * @param attributeSet The attribute set containing CSS properties.
	 * @return The vertical alignment as an identifier number or, if a value is not specified, the default value "baseline".
	 */
	public static String getVerticalAlign(AttributeSet attributeSet) { //TODO fix for lengths and percentages
		final XMLCSSPrimitiveValue verticalAlignValue = (XMLCSSPrimitiveValue)getCSSPropertyCSSValue(attributeSet, CSS_PROP_VERTICAL_ALIGN, false); //get CSS value for this property without resolving up the hierarchy
		if(verticalAlignValue != null) { //if we have a value
			return verticalAlignValue.getStringValue(); //return the value as a string
		}
		return CSS_VERTICAL_ALIGN_BASELINE; //return the default value, since we couldn't find an alternative
		//TODO fix for percentages and lengths here as well
	}

}

/**
 * Gets the CSS "text-indent" setting from the attribute set in pixels.
 * @param attributeSet The attribute set containing CSS properties.
 * @return The text indent amount as a length or a percentage or, if text indent is specified, the default amount, which is 0.
 */
/*TODO a template for a property that needs to get a relative value from its parent
	public static int getTextIndent(AttributeSet attributeSet)
	{
//TODO del System.out.println("Getting font size for attribute set of: "+attributeSet.getAttribute(StyleConstants.NameAttribute));	//TODO del
		if(attributeSet.isDefined(CSS_PROP_TEXT_INDENT)) {	//if this attribute set has the property defined
//TODO del System.out.println("Found font size attribute in: "+attributeSet.getAttribute(StyleConstants.NameAttribute));	//TODO del
			final XMLCSSPrimitiveValue fontTextIndent=(XMLCSSPrimitiveValue)attributeSet.getAttribute(CSS_PROP_TEXT_INDENT);	//get the CSS value for this property
			if(textIndentValue!=null) {	//if we have a value
				if(textIndentValue.isAbsoluteLength())	//if this is an absolute length
					return Math.round(textIndent.getFloatValue(XMLCSSPrimitiveValue.CSS_PX));	//get the value in pixels and round to the nearest integer pixel length
				else {	//if this is not an absolute length, it could be a relative length or a percentage
					float percentageValue=1;	//if we find a percentage value, we'll store it here (we won't really store a percentage, but the actual float number; that is, percentage/100) (we're defaulting to 1 because the compiler will complain without some value, even though this always gets changed by the logic of the code)
					boolean isPercentage=;	//see if this is a percentage value
					if(isPercentage)	//if this is an explicit percentage value
						percentageValue=fontSizeValue.getFloatValue(XMLCSSPrimitiveValue.CSS_PERCENTAGE)/100;	//store the percentage value as a scaling value
						//if this isn't an explicit percentage value, it might be a relative value, an absolute string value, or a relative string value that is the same as a percentage




					if(textIndentValue.getPrimitiveType()==XMLCSSPrimitiveValue.CSS_PERCENTAGE) {	//if this is a percentage value
						final AttributeSet resolveParent=attributeSet.getResolveParent();	//get the parent to use to resolve this attribute
						if(resolveParent!=null)	//if we have a resolve parent
							return Math.round(percentageValue*getFontSize(resolveParent));	//multiply the percentage with the font size from the parent
						else	//if we don't have a resolve parent
							return Math.round(percentageValue*DEFAULT_FONT_SIZE);	//multiply the percentage with the default font size
					}
					//TODO add support for relative sizes (ems, exs, etc.) here: else
				}
			}
		}
		else {	//if this property isn't defined in this attribute set
			//resolve to the parent immediately before checking values to ensure a value is present to work with percentages
			final AttributeSet resolveParent=attributeSet.getResolveParent();	//get the parent to use to resolve this attribute
			if(resolveParent!=null)	//if we have a resolve parent
				return getTextIndent(resolveParent);	//get the property from the parent, which will works its way up the chain, guaranteeing that the body of this function only works on attribute sets with the value defined
		}
		return DEFAULT_TEXT_INDENT;	//return the default value, since we couldn't find an alternative
	}
*/
