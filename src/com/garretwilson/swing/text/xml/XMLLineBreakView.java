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
import javax.swing.text.Segment;

/**View class that causes a line break. Meant to be a child view of
	<code>XMLInlineView</code>.
@see XMLInlineView
@author Garret Wilson
*/
public class XMLLineBreakView extends XMLInlineView
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLLineBreakView(Element element)
	{
		super(element);	//construct the parent class
//G***del System.out.println("Creating view for element: "+element.getAttributes().getAttribute(StyleConstants.NameAttribute));	//G***del
//G***del		StyleSheet sheet = getStyleSheet();
//G***del	attr = sheet.getViewAttributes(this);
//G***del		AttributeSet=new SimpleAttributeSet(element.getAttributes());	//G***testing

//G***del		changedUpdate(null, null, null);	//G***testing
	}


//TODO fix visibility---this doesn't turn off displaying of content, apparently

	/**@return <code>false</code> to indicate that line breaks are not themselves visible.*/
	public boolean isVisible()
	{
		return false;	//show that line breaks views are not visibles G***does this change processing of line breaks?
	}

/*G***del; this doesn't work
	public Segment getText(int p0, int p1)
	{
		return new Segment(new char[]{'X'}, 0, 1);	//G***testing
	}
*/

	/**Forces a line break on the horizontal axis.
	@param axis The axis to get the break weight for.
	@param pos The position in the document.
	@param len The length available for this view.
	@return The preference for breaking at this location: ForcedBreakWeight for
		the X axis, and the default break weight for the other axis.
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
		if(axis==X_AXIS)	//if they want the break weight for the X axis
		{
//G***del System.out.println("<br> is trying to work by returning ForcedBreakWeight.");	//G***del
			return ForcedBreakWeight;	//show that we're forcing a break on this axis
		}
		else	//if they want the break weight on the Y axis
			return super.getBreakWeight(axis, pos, len);	//let our parent class determine the break weight
	}

}
