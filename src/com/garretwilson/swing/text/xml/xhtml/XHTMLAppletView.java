package com.garretwilson.swing.text.xml.xhtml;

import javax.swing.text.*;
import com.garretwilson.lang.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.xml.xhtml.*;
import com.garretwilson.util.*;

//G***add fallback element initialization as we do for images, putting the init stuff in XMLObjectView

/**A view that displays an applet, intended to support the XHTML
	<code>&lt;object&gt;</code> element.
@author Garret Wilson
*/
public class XHTMLAppletView extends XMLAbstractAppletView implements XHTMLConstants
{

	/**Creates a new view that represents an XHTML applet.
	@param element The element for which to create the view.
	*/
  public XHTMLAppletView(Element element)
	{
   	super(element);	//do the default constructing
		initialize(element);	//do the necessary applet loading and other processing
//G***del Debug.trace("Finished constructing XHTMLAppletView");	//G***del
	}

	/**Initializes the information needed to render the applet.
	@param element The element which contains the applet information.
	*/
	protected void initialize(Element element)
	{
//G***del Debug.trace("XHTMLAppletView.initialize()");
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attributes
		final String javaPrefix="java:";  //the prefix the classid, as a URI, will probably have G***use a constant here
//G***del when works		final String classPostfix=".class"; //the ending postfix the classid URI may have G***use a constant here
		final String classID=(String)attributeSet.getAttribute(ELEMENT_OBJECT_ATTRIBUTE_CLASSID);	//get the classid of the applet G***check about resolving parents (we don't want to resolve), use namespaces, and comment
		final String classHRef=StringUtilities.trimBeginning(classID, javaPrefix);  //remove the "java:" prefix if present
		setClassHRef(classHRef);  //set the href to the class
/*G***del when not needed
		className=StringUtilities.trimEnd(className, classPostfix);  //remove the ".class" postfix if present
		setClassName(className);  //set the class name we constructed
*/
//G***del Debug.trace("Set class name: ", className);  //G***del
		//check for errors here
		final int height=Integer.parseInt((String)attributeSet.getAttribute(ELEMENT_OBJECT_ATTRIBUTE_HEIGHT));	//get the requested height of the applet G***check about resolving parents (we don't want to resolve), use namespaces, and comment
		final int width=Integer.parseInt((String)attributeSet.getAttribute(ELEMENT_OBJECT_ATTRIBUTE_WIDTH));	//get the requested width of the applet G***check about resolving parents (we don't want to resolve), use namespaces, and comment
		setHeight(height);  //update the standard and current heights
		setWidth(width);  //update the standard and current widths
//G***del Debug.trace("OEBAppletView.initialize(), width: "+fWidth+", height: "+fHeight);	//G***del
	}

	/**Unconditionally loads all parameters from child elements. Called by the
		<code>XMLAbstractAppletView.setParent()</code> method.
	@see XMLAbstractAppletView#setParent
	*/
	protected void loadParameters()
	{
		final Element element=getElement(); //get this view's element
		int elementCount=element.getElementCount(); //see how many elements there are
		for(int i=0; i<elementCount; i++) //look at each element
		{
			final Element childElement=element.getElement(i); //get a reference to this child element
			final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child's attributes


			final String childElementName=XMLStyleUtilities.getXMLElementName(childAttributeSet);	//get the name of this child element
//G***del		  final String childElementName=(String)childAttributeSet.getAttribute(StyleConstants.NameAttribute);	//get the name of this child element
		  if(childElementName.equals(ELEMENT_PARAM))  //if this is a param element
			{
				final String parameterName=(String)childAttributeSet.getAttribute(ELEMENT_PARAM_ATTRIBUTE_NAME);	//get the param name attribute G***check about resolving parents (we don't want to resolve), use namespaces
				Debug.assert(parameterName!=null, "<param> name is null");  //G***fix
				final String parameterValue=(String)childAttributeSet.getAttribute(ELEMENT_PARAM_ATTRIBUTE_VALUE);	//get the param value attribute G***check about resolving parents (we don't want to resolve), use namespaces
				Debug.assert(parameterValue!=null, "<param> value is null");  //G***fix
//G***del Debug.trace("Param name: "+parameterName+" value: "+parameterValue); //G***del
				final Parameter parameter=new Parameter(parameterName, parameterValue); //create a new parameter
				setParameter(parameter);  //set the parameter
			}
		}
	}

}
