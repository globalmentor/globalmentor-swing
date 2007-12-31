package com.garretwilson.swing.text.xml;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
/*G***bring back as needed
import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
*/
import java.io.*;
/*G***bring back as needed
*/
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.mail.internet.ContentType;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import com.garretwilson.lang.Objects;
import com.garretwilson.lang.StringBufferUtilities;
import com.garretwilson.lang.Strings;
import com.garretwilson.net.URLUtilities;
import com.garretwilson.util.Debug;
import com.garretwilson.util.NameValuePair;
//G***del when works import com.garretwilson.text.xml.*;
import com.garretwilson.rdf.*;
import com.garretwilson.text.xml.xlink.XLinkConstants;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLNamespaceProcessor;
import com.garretwilson.text.xml.XMLProcessor;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.text.xml.XMLUtilities;
import com.garretwilson.text.xml.oeb.*;	//G***del
import com.garretwilson.text.xml.xhtml.XHTMLConstants;	//G***del
import com.garretwilson.text.xml.stylesheets.css.*;	//G***del if we don't need
//G***del import com.garretwilson.text.xml.stylesheets.css.oeb.DefaultOEBCSSStyleSheet;	//G***del if we don't need
import com.garretwilson.swing.XMLTextPane;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.DocumentUtilities;
import com.garretwilson.swing.text.InvisibleView;
import com.garretwilson.swing.text.StyleUtilities;
import com.garretwilson.swing.text.ViewsFactory;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSS2Properties;
import org.w3c.dom.css.CSSStyleDeclaration;
//G***maybe import all the DOM classes

//G***del these if we don't need thsm
/*G***del
import java.awt.*;
import java.awt.event.*;
*/

/**A factory to build views for an XML document based upon the attributes of
	each element.
	<p>The XML view factory adds a special capability of defining view factories
	for specific namespaces. If an XML element is encountered in a particular
	namespace and a view factory has been registered for that namespace, the
	registered view factory will be used to create the view. Otherwise, this
	view factory will create a view.</p>
	<p>As this factory allows the capability of creating multiple views for
	certain elements, child classes should override
	<code>create(Element, boolean)</code> rather than overriding the normal
	<code>create(Element)</code> method.
*/
public class XMLViewFactory implements ViewsFactory
{

	/**Default constructor.*/
	public XMLViewFactory()
	{
	}

	/**Creates a view for the given element. If the element specifies a
		namespace and a view factory has been registered for the given namespace,
		the view creation will be delegated to the designated view factory.
	@param element The element this view will represent.
	@return A view to represent the given element.
	*/
	public View create(final Element element)
	{
//G***del Debug.traceStack("Creating XML view");  //G***del
		return create(element, false);  //return a single view to represent the given view, giving no indication if multiple views are needed
	}

	/**Creates one or more views for the given element. If the element specifies
		a namespace and a view factory has been registered for the given namespace,
		the view creation will be delegated to the designated view factory.
		This method allows one element (such as a nested inline element within a
		paragraph) to be represented by one level of multiple views rather than
		a hierarchy of views.
	@param element The element the view or views will represent.
	@return One or more views to represent the given element.
	*/
/*G***fix
	public View[] createViews(final Element element)
	{
		final View view=create(element, true);  //create a view for the element, if we can, but get an indication of if there should be several views
		if(view!=null)  //if there is only one view
		{
			return new View[]{view};  //return an array of just one view
		}
		else  //if there should be multiple views
		{
			final List addViewList=new ArrayList(childElementCount); //create a new list in which to store views to add; we know there will be at least as many views as child elements -- maybe more
*/

	/**Creates one or more views for the given element, storing the views in
		the given list.
		This method allows one element (such as a nested inline element within a
		paragraph) to be represented by one level of multiple views rather than
		a hierarchy of views.
	@param element The element the view or views will represent.
	@param addViewList The list of views to which the views should be added.
	*/
	public void create(final Element element, final List<View> addViewList)
	{
//G***del Debug.trace();  //G***del
		final View view=create(element, true);  //create a view for the element, if we can, but get an indication of if there should be several views
		if(view!=null)  //if there is only one view
		{
			addViewList.add(view);  //add the list to the view, mimicking the normal functionality
		}
		else  //if there should be multiple views
		{
//G***del Debug.trace("creating things for each child element");  //G***del
			int childElementCount=element.getElementCount();  //see how many child elements there are
			for(int i=0; i<childElementCount; ++i)  //look at each child element
				create(element.getElement(i), addViewList); //create one or more views for this child element
		}
	}

	/**Creates a view for the given element. If the element specifies a
		namespace and a view factory has been registered for the given namespace,
		the view creation will be delegated to the designated view factory.
		As this class implements <code>ViewsFactory</code>, which allows multiple
		views to be created, this method can optionally indicate multiple views
		are needed by returning <code>null</code>.
	@param element The element this view will represent.
	@param indicateMultipleViews Whether <code>null</code> should be returned to
		indicate multiple views should represent the given element.
	@return A view to represent the given element, or <code>null</code>
		indicating the element should be represented by multiple views.
	*/
	public View create(final Element element, final boolean indicateMultipleViews)
	{
//G***del Debug.trace("ready to create view for element: "+XMLStyleUtilities.getXMLElementLocalName(element.getAttributes())+" of class "+element.getClass().getName());  //G***
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attribute set
		final String elementKind=element.getName();	//get the kind of element this is (based on the name of the Swing element, not the Swing element's attribute which holds the name of its corresponding XML element)
		if(elementKind!=null) //if the element has a kind
		{
			if(AbstractDocument.SectionElementName.equals(elementKind))	//if this is the default section element
			{
				if(DocumentUtilities.isPaged(element.getDocument()))  //if we should page our information
					return new XMLPagedView(element);	//create a paged view for the entire section G***testing
				else  //if information is not paged
					return new XMLSectionView(element);	//create an unpaged section view
			}
/*G***bring back if needed
			else if(AbstractDocument.ParagraphElementName.equals(elementKind))	//if this is is a generic paragraph element
			{
				final Element parentElement=element.getParentElement();	//get the element's parent
				if(AbstractDocument.SectionElementName.equals(parentElement.getName()))	//if this element is a direct child of the section
				{
						//if this element is the last child element of the section element, it's the dummy '\n' element---create a hidden view for it
					if(parentElement.getElementCount()>0 && parentElement.getElement(parentElement.getElementCount()-1)==element)
					{
						return new XMLHiddenView(element);	//hide the dummy ending '\n' hierarchy
					}
				}
			}
*/
			else if(AbstractDocument.ContentElementName.equals(elementKind))	//if this is is content
			{
//G***del Debug.trace("XMLEditorKit always creates inline views for content."); //G***del
				return new XMLInlineView(element);	//inline elements are *always* inline views; content (that is, text) elements always take precendence over everything
			}
		}
		if(attributeSet!=null)  //if we have an attribute set
		{
				//check the visibility status before going to any registered view factories
			if(!StyleUtilities.isVisible(attributeSet))	//if for some reason this element should not be visible
			{
				return new InvisibleView(element);	//create a hidden view
			}
				//delegate to the registered view factory if we can
			/*G***fix final */String elementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet); //get the namespace of this element, if it has one
//G***del Debug.trace("Looking for view factory for namespace: ", elementNamespaceURI); //G***del
			if(elementNamespaceURI==null) //if this element has no namespace
			{
				final ContentType mediaType=XMLStyleUtilities.getMediaType(attributeSet); //see if this element's document has a media type defined
				if(mediaType!=null) //if there is a media type defined for this element's document	//G***probably do all this differently later, like registering a view factory with a media type or something or, better yet, registering a namespace with a media type
				{ 
					final URI mediaTypeNamespaceURI=XMLNamespaceProcessor.getDefaultNamespaceURI(mediaType);	//see if we can find a default namespace for the media type
					if(mediaTypeNamespaceURI!=null)	//if we found a namespace for the media type
					{
						elementNamespaceURI=mediaTypeNamespaceURI.toString();	//use the namespace for the media type
					}
				}
			}
//G***del Debug.trace("Decided namespace is really: ", elementNamespaceURI); //G***del
			final ViewFactory namespaceViewFactory=getViewFactory(elementNamespaceURI); //see if a view factory has been registered for this namespace (which may be null)
			if(namespaceViewFactory!=null && namespaceViewFactory!=this)  //if a view factory has been registered for this namespace, and it's a different view factory (if tried to call ourselves, this would result in infinite recursion)
			{
//G***del Debug.trace("Using view factory: ", namespaceViewFactory.getClass().getName()); //G***del
				if(namespaceViewFactory instanceof ViewsFactory) //if this view factory knows how to create multiple views
				{
//G***del Debug.trace("Is a views factory");
					final ViewsFactory namespaceViewsFactory=(ViewsFactory)namespaceViewFactory;  //cast the view factory to a views factory
						//let the views factory create the element, indicating if multiple views should be indicated
				  return namespaceViewsFactory.create(element, indicateMultipleViews);
				}
				else  //if this is a normal view factory
				{
					final View view=namespaceViewFactory.create(element); //ask the registered view factory to create a view for the element
					if(view!=null)  //if a view was successfully created (this is an extra compensation step, because the view factory should always return a valid view for this method)
						return view;  //return the view the view factory for the namespace created
				}
			}
			final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element G***shouldn't we use getXMLLocalName()?
//G***del Debug.trace("XMLViewFactory: Creating a view for element: "+elementName+" of kind: "+elementKind);  //G***replace with a better Debug trace
//G***del Debug.trace("Indicate multiple views: "+indicateMultipleViews);	//G***del
//G***del Debug.trace("1"); //G***del
				//those elements marked to have page break views take precedence over paragraphs and their block/inline specification
			if(XMLStyleUtilities.isPageBreakView(attributeSet))	//show that this element should have a page-break view
				return new XMLPageBreakView(element);	//create a page break view
//G***del Debug.trace("2"); //G***del


			final CSSStyleDeclaration cssStyle=XMLCSSStyleUtilities.getXMLCSSStyle(attributeSet); //get the CSS style of the element


//G***del				if(cssStyle!=null)  //if this element has style
			boolean hasBlockChildren=false, hasInlineChildren=false;	//check to see if there are block and/or inline children
			for(int childIndex=element.getElementCount()-1; childIndex>=0 && !(hasBlockChildren && hasInlineChildren); --childIndex) //look at each child element (stop looking when we've found both block and inline child nodes)
			{
				final Element childElement=element.getElement(childIndex);  //get a reference to this child element
				final CSSStyleDeclaration childCSSStyle=XMLCSSStyleUtilities.getXMLCSSStyle(childElement.getAttributes()); //get the CSS style of the element (this method makes sure the attributes are present)
//G***del Debug.trace("Child "+childIndex+" attributes: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(childElement.getAttributes()));
//G***del Debug.trace("Child "+childIndex+" style: ", childCSSStyle);
					//if this element is inline (text is always inline, regardless of what the display property says)
				if(XMLCSSUtilities.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName()))
					hasInlineChildren=true;	//show that there are inline children
				else	//if this isn't inline, we'll assume it's some sort of block element
					hasBlockChildren=true;	//show that there are block children
			}
			final boolean isBlockElement=!XMLCSSUtilities.isDisplayInline(cssStyle);	//see if this is a block-level element
			final boolean isTableRow; //we'll see if this element has table row display
			if(cssStyle!=null)  //if this element has style
			{
//G***del Debug.trace("Element has style"); //G***del
//G***del Debug.trace("Element has style: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(attributeSet));  //G***del

	/*G***fix when our XMLCSSStyleDeclaration implements CSS2Properties
				Debug.assert(cssStyle instanceof CSS2Properties, "DOM implementation does not support CSS2Properties interface for CSSStyleDeclaration"); //G***do we want to take action if the style does not implement CSS2Properties?
				final CSS2Properties cssProperties=(CSS2Properties)cssStyle;  //get the CSS2Properties interface, which is expected to be implemented by the DOM CSSStyleDeclaration
	*/
				final XMLCSSStyleDeclaration cssProperties=(XMLCSSStyleDeclaration)cssStyle;  //get the CSS2Properties interface, which is expected to be implemented by the DOM CSSStyleDeclaration G***fix
				isTableRow=XMLCSSConstants.CSS_DISPLAY_TABLE_ROW.equals(cssProperties.getDisplay());  //G***testing tableflow
			}
			else  //if this element has no style, it can't be a table row
				isTableRow=false; //this element is not a table row
//G***del Debug.trace("Element has block children: "+hasBlockChildren); //G***del
//G***del Debug.trace("Element has inline children: "+hasInlineChildren); //G***del

//G***del Debug.trace("is Paragraph view:"+(!isTableRow && isBlockElement && hasInlineChildren && !hasBlockChildren));  //G***del
			//a block element that has only inline children and no block children will be a paragraph view
			if(!isTableRow && isBlockElement && hasInlineChildren && !hasBlockChildren)
			{
				return new XMLParagraphView(element);	//create and return a paragraph view G***change name to paragraph view
			}
/*G***del when works
			//those elements marked to have paragraph views take precedence over their block/inline specification
			if(XMLStyleConstants.isParagraphView(attributeSet))	//show that the anonymous block should be a paragraph view
				return new XMLParagraphView(element);	//create and return a paragraph view G***change name to paragraph view
*/










/*G***fix
	if((isTableRow || hasBlockChildren) && hasInlineChildren)	//if this element has both block and inline children, we'll have to create some anonymous blocks
	{
		boolean startedButNotFinishedAnonymousBlock=false;	//show that we haven't started started any anonymous blocks yet
//G***del when works			XMLCSSSimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set each time we create and anonymous block
		SimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set each time we create and anonymous block
		for(int childIndex=0; childIndex<element.getChildNodes().getLength(); childIndex++)	//look at each child node
		{
			final XMLNode node=(XMLNode)element.getChildNodes().item(childIndex);	//look at this node
//G***del				final boolean childIsBlock=node.getNodeType()==XMLNode.ELEMENT_NODE && ((XMLElement)node).getCSSStyle().getDisplay().equals(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//see if this child has block formatting or not
			final boolean childIsBlock=node.getNodeType()==XMLNode.ELEMENT_NODE && !((XMLElement)node).getCSSStyle().isDisplayInline();	//see if this child has block formatting or not
			if(!startedButNotFinishedAnonymousBlock)	//if we haven't started an anonymous block, yet
			{
				if(!childIsBlock)	//if this is an inline element and we haven't started an anonymous block, yet
				{

//G***testing to see if this correctly removes unwanted CR/LF in block-only elements; perhaps this should go in the XML parser
//G***maybe don't do this for preformatted elements
					if(node.getNodeType()==XMLNode.TEXT_NODE)	//if this is a text node G***what about CDATA sections?
					{
//G***del System.out.println("This is a text node");
						if(((XMLText)node).getData().trim().length()==0)	//if this text node has only whitespace
							continue;	//skip this child node
					}


					anonymousAttributeSet=new SimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
//G***del when works						anonymousAttributeSet=new XMLCSSSimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
//G***fix						XMLCSSStyleConstants.setXMLElementName(anonymousAttributeSet, XMLStyleConstants.AnonymousNameValue);	//show by its name that this is an anonymous box
					XMLStyleConstants.setXMLElementName(anonymousAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
//G***del						anonymousAttributeSet.addAttribute(StyleConstants.NameAttribute, XMLCSSStyleConstants.AnonymousAttributeValue);	//show by its name that this is an anonymous box G***maybe change this to setAnonymous
					XMLCSSStyleConstants.setParagraphView(anonymousAttributeSet, true);	//show that the anonymous block should be a paragraph view
					elementSpecList.add(new DefaultStyledDocument.ElementSpec(anonymousAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of an anonyous block element
					startedButNotFinishedAnonymousBlock=true;	//show that we've started an anonymous block
				}
			}
			else if(childIsBlock)	//if we *have* started an anonymous block and we've found a real block element
			{
				elementSpecList.add(new DefaultStyledDocument.ElementSpec(anonymousAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the anonymous block
				startedButNotFinishedAnonymousBlock=false;	//show that we've finished the anonymous block
			}
			appendElementSpecListNode(elementSpecList, node, attributeSet, baseURL);	//append this node's information normally
		}
		if(startedButNotFinishedAnonymousBlock)	//if we started an anonymous block but never finished it (i.e. the last child was inline)
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(anonymousAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the anonymous block
	}
	else	//if this object either has all block children or all inline children, the block and inline children get created normally
	{
Debug.trace("Ready to create children for: ", element.getNodeName());  //G***del
		for(int childIndex=0; childIndex<element.getChildNodes().getLength(); childIndex++)	//look at each child node
		{
			final XMLNode node=(XMLNode)element.getChildNodes().item(childIndex);	//look at this node
			appendElementSpecListNode(elementSpecList, node, attributeSet, baseURL);	//append this node's information
		}
	}
*/





Debug.trace("3"); //G***del
//G***del				final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSConstants.CSS_PROP_DISPLAY);	//get the display property G***can we be sure this will be a primitive value?
			//G***use the style constants to get the display property, maybe
			final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)XMLCSSStyleUtilities.getCSSPropertyCSSValue(attributeSet, XMLCSSConstants.CSS_PROP_DISPLAY, false);	//get the display property for this element, but don't resolve up the attribute set parent hierarchy G***can we be sure this will be a primitive value?
Debug.trace("4"); //G***del
			if(cssDisplayProperty!=null)	//if this element has a CSS display property
			{
Debug.trace("Found display property: ", cssDisplayProperty);
				final String cssDisplayString=cssDisplayProperty.getStringValue();	//get the display value
//G***del System.out.println("XML view factory found element with display: "+cssDisplayString);	//G***del
				if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_BLOCK))	//if this should be block display
					return new XMLBlockView(element, View.Y_AXIS);	//create a block view
				else if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_LIST_ITEM))	//if this should be a list item
					return new XMLListItemView(element, View.Y_AXIS);	//create a list item view G***change this to an XMLListItem(element)
				else if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_TABLE))	//if this should be a table
					return new XMLTableView(element);	//create a table view
				else if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_TABLE_CELL))	//if this should be table cell
					return new XMLBlockView(element, View.Y_AXIS);	//create a table cell view G***change to the correct table cell view class
				else if(XMLCSSConstants.CSS_DISPLAY_NONE.equals(cssDisplayString))	//if this element should have no display
					return new InvisibleView(element);	//create a hidden view
/*G***important; fix


						//if this element specifies itself to be inline, we'll assume it's in
						//a paragraph and return null so that views will instead be created
						//for its children
//G***all this needs fixed
				else if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_INLINE))	//see if this element specifies itself as inline
					return new XMLBlockView(element, View.Y_AXIS);	//create a table cell view G***change to the correct table cell view class

*/



//G***later make sure invisible (display: none) views are covered; right now they are made inline but not painted or given a size
			}
		}
Debug.trace("5"); //G***del
//G***del Debug.trace("XMLViewFactory creating inline view.");  //G***del
		if(indicateMultipleViews) //if we should indicate multiple views are needed
		{
			Debug.trace("returning null for element: ", XMLStyleUtilities.getXMLElementName(element.getAttributes()));  //G***del
			return null;  //return null so that the XMLParagraphView.LayoutView can create views for the children of the inline view, eventually creating XMLInlineViews for the actual content
		}
		else  //if we're not allowed to indicate multiple views
			return new XMLInlineView(element);	//everything we don't know what to do with gets to be an inline view G***should this be the default?
	}

	/**Retreives a view factory for the given namespace.
		This version defaults to returning this view factory. A child class can
		override this method to return a view factory specifically for a given
		namespace.
	@param namespaceURI The namespace URI of the element for which a view factory
		should be returned, or <code>null</code> if the element has no namespace.
	@return A view factory for the given namespace.
	*/
	protected ViewFactory getViewFactory(final String namespaceURI)
	{
		return this;  //default to just using this view factory
	}

}
