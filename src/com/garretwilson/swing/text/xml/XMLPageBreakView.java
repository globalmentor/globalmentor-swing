package com.garretwilson.swing.text.xml;

/*G***bring back as needed
import java.awt.Shape;
import java.awt.Font;
import java.awt.FontMetrics;
import java.text.BreakIterator;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*; 
import com.garretwilson.text.xml.stylesheets.css.XMLCSSPrimitiveValue;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
*/
import javax.swing.text.Element;

import com.garretwilson.swing.text.InvisibleView;

/**View class that causes a page break.
@see XMLBlockView
@author Garret Wilson
*/
public class XMLPageBreakView extends InvisibleView	//G***maybe go back to XMLParagraphView, if that class knows how to be hidden, but what are the consequences of either? XMLParagraphView
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLPageBreakView(Element element)
	{
		super(element);	//construct the parent class
//G***del System.out.println("Creating view for element: "+element.getAttributes().getAttribute(StyleConstants.NameAttribute));	//G***del
//G***del		StyleSheet sheet = getStyleSheet();
//G***del	attr = sheet.getViewAttributes(this);
//G***del		AttributeSet=new SimpleAttributeSet(element.getAttributes());	//G***testing

//G***del		changedUpdate(null, null, null);	//G***testing
	}

	/**Forces a line break on the vertical axis.
	@param axis The axis to get the break weight for.
	@param pos The position in the document.
	@param len The length available for this view.
	@return The preference for breaking at this location: ForcedBreakWeight for
		the Y axis, and the default break weight for the other axis.
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
		if(axis==Y_AXIS)	//if they want the break weight for the Y axis
		{
//G***del System.out.println("<page-break> view is trying to force a page break.");	//G***del
			return ForcedBreakWeight;	//show that we're forcing a break on this axis
		}
		else	//if they want the break weight on the X axis
			return super.getBreakWeight(axis, pos, len);	//let our parent class determine the break weight
	}

}
