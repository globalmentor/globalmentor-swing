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

package com.globalmentor.swing.text.xml;

import java.net.URI;
import javax.swing.text.*;
import java.util.*;
import com.globalmentor.log.Log;
import com.globalmentor.net.ContentType;
import com.globalmentor.swing.text.*;
import com.globalmentor.swing.text.xml.css.XMLCSSStyles;
import com.globalmentor.text.xml.XML;
import com.globalmentor.text.xml.stylesheets.css.*;

import org.w3c.dom.css.CSSStyleDeclaration;

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
//TODO del Log.traceStack("Creating XML view");  //TODO del
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
/*TODO fix
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
//TODO del Log.trace();  //TODO del
		final View view=create(element, true);  //create a view for the element, if we can, but get an indication of if there should be several views
		if(view!=null)  //if there is only one view
		{
			addViewList.add(view);  //add the list to the view, mimicking the normal functionality
		}
		else  //if there should be multiple views
		{
//TODO del Log.trace("creating things for each child element");  //TODO del
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
//TODO del Log.trace("ready to create view for element: "+XMLStyleUtilities.getXMLElementLocalName(element.getAttributes())+" of class "+element.getClass().getName());  //TODO 
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attribute set
		final String elementKind=element.getName();	//get the kind of element this is (based on the name of the Swing element, not the Swing element's attribute which holds the name of its corresponding XML element)
		if(elementKind!=null) //if the element has a kind
		{
			if(AbstractDocument.SectionElementName.equals(elementKind))	//if this is the default section element
			{
				if(Documents.isPaged(element.getDocument()))  //if we should page our information
					return new XMLPagedView(element);	//create a paged view for the entire section TODO testing
				else  //if information is not paged
					return new XMLSectionView(element);	//create an unpaged section view
			}
/*TODO bring back if needed
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
//TODO del Log.trace("XMLEditorKit always creates inline views for content."); //TODO del
				return new XMLInlineView(element);	//inline elements are *always* inline views; content (that is, text) elements always take precendence over everything
			}
		}
		if(attributeSet!=null)  //if we have an attribute set
		{
				//check the visibility status before going to any registered view factories
			if(!Styles.isVisible(attributeSet))	//if for some reason this element should not be visible
			{
				return new InvisibleView(element);	//create a hidden view
			}
				//delegate to the registered view factory if we can
			/*TODO fix final */String elementNamespaceURI=XMLStyles.getXMLElementNamespaceURI(attributeSet); //get the namespace of this element, if it has one
//TODO del Log.trace("Looking for view factory for namespace: ", elementNamespaceURI); //TODO del
			if(elementNamespaceURI==null) //if this element has no namespace
			{
				final ContentType mediaType=XMLStyles.getMediaType(attributeSet); //see if this element's document has a media type defined
				if(mediaType!=null) //if there is a media type defined for this element's document	//TODO probably do all this differently later, like registering a view factory with a media type or something or, better yet, registering a namespace with a media type
				{ 
					final URI mediaTypeNamespaceURI=XML.getDefaultNamespaceURI(mediaType);	//see if we can find a default namespace for the media type
					if(mediaTypeNamespaceURI!=null)	//if we found a namespace for the media type
					{
						elementNamespaceURI=mediaTypeNamespaceURI.toString();	//use the namespace for the media type
					}
				}
			}
//TODO del Log.trace("Decided namespace is really: ", elementNamespaceURI); //TODO del
			final ViewFactory namespaceViewFactory=getViewFactory(elementNamespaceURI); //see if a view factory has been registered for this namespace (which may be null)
			if(namespaceViewFactory!=null && namespaceViewFactory!=this)  //if a view factory has been registered for this namespace, and it's a different view factory (if tried to call ourselves, this would result in infinite recursion)
			{
//TODO del Log.trace("Using view factory: ", namespaceViewFactory.getClass().getName()); //TODO del
				if(namespaceViewFactory instanceof ViewsFactory) //if this view factory knows how to create multiple views
				{
//TODO del Log.trace("Is a views factory");
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
			final String elementName=XMLStyles.getXMLElementName(attributeSet); //get the name of this element TODO shouldn't we use getXMLLocalName()?
//TODO del Log.trace("XMLViewFactory: Creating a view for element: "+elementName+" of kind: "+elementKind);  //TODO replace with a better Debug trace
//TODO del Log.trace("Indicate multiple views: "+indicateMultipleViews);	//TODO del
//TODO del Log.trace("1"); //TODO del
				//those elements marked to have page break views take precedence over paragraphs and their block/inline specification
			if(XMLStyles.isPageBreakView(attributeSet))	//show that this element should have a page-break view
				return new XMLPageBreakView(element);	//create a page break view
//TODO del Log.trace("2"); //TODO del


			final CSSStyleDeclaration cssStyle=XMLCSSStyles.getXMLCSSStyle(attributeSet); //get the CSS style of the element


//TODO del				if(cssStyle!=null)  //if this element has style
			boolean hasBlockChildren=false, hasInlineChildren=false;	//check to see if there are block and/or inline children
			for(int childIndex=element.getElementCount()-1; childIndex>=0 && !(hasBlockChildren && hasInlineChildren); --childIndex) //look at each child element (stop looking when we've found both block and inline child nodes)
			{
				final Element childElement=element.getElement(childIndex);  //get a reference to this child element
				final CSSStyleDeclaration childCSSStyle=XMLCSSStyles.getXMLCSSStyle(childElement.getAttributes()); //get the CSS style of the element (this method makes sure the attributes are present)
//TODO del Log.trace("Child "+childIndex+" attributes: ", com.globalmentor.swing.text.AttributeSetUtilities.getAttributeSetString(childElement.getAttributes()));
//TODO del Log.trace("Child "+childIndex+" style: ", childCSSStyle);
					//if this element is inline (text is always inline, regardless of what the display property says)
				if(XMLCSS.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName()))
					hasInlineChildren=true;	//show that there are inline children
				else	//if this isn't inline, we'll assume it's some sort of block element
					hasBlockChildren=true;	//show that there are block children
			}
			final boolean isBlockElement=!XMLCSS.isDisplayInline(cssStyle);	//see if this is a block-level element
			final boolean isTableRow; //we'll see if this element has table row display
			if(cssStyle!=null)  //if this element has style
			{
//TODO del Log.trace("Element has style"); //TODO del
//TODO del Log.trace("Element has style: ", com.globalmentor.swing.text.AttributeSetUtilities.getAttributeSetString(attributeSet));  //TODO del

	/*TODO fix when our XMLCSSStyleDeclaration implements CSS2Properties
				Debug.assert(cssStyle instanceof CSS2Properties, "DOM implementation does not support CSS2Properties interface for CSSStyleDeclaration"); //TODO do we want to take action if the style does not implement CSS2Properties?
				final CSS2Properties cssProperties=(CSS2Properties)cssStyle;  //get the CSS2Properties interface, which is expected to be implemented by the DOM CSSStyleDeclaration
	*/
				final XMLCSSStyleDeclaration cssProperties=(XMLCSSStyleDeclaration)cssStyle;  //get the CSS2Properties interface, which is expected to be implemented by the DOM CSSStyleDeclaration TODO fix
				isTableRow=XMLCSS.CSS_DISPLAY_TABLE_ROW.equals(cssProperties.getDisplay());  //TODO testing tableflow
			}
			else  //if this element has no style, it can't be a table row
				isTableRow=false; //this element is not a table row
//TODO del Log.trace("Element has block children: "+hasBlockChildren); //TODO del
//TODO del Log.trace("Element has inline children: "+hasInlineChildren); //TODO del

//TODO del Log.trace("is Paragraph view:"+(!isTableRow && isBlockElement && hasInlineChildren && !hasBlockChildren));  //TODO del
			//a block element that has only inline children and no block children will be a paragraph view
			if(!isTableRow && isBlockElement && hasInlineChildren && !hasBlockChildren)
			{
				return new XMLParagraphView(element);	//create and return a paragraph view TODO change name to paragraph view
			}
/*TODO del when works
			//those elements marked to have paragraph views take precedence over their block/inline specification
			if(XMLStyleConstants.isParagraphView(attributeSet))	//show that the anonymous block should be a paragraph view
				return new XMLParagraphView(element);	//create and return a paragraph view TODO change name to paragraph view
*/










/*TODO fix
	if((isTableRow || hasBlockChildren) && hasInlineChildren)	//if this element has both block and inline children, we'll have to create some anonymous blocks
	{
		boolean startedButNotFinishedAnonymousBlock=false;	//show that we haven't started started any anonymous blocks yet
//TODO del when works			XMLCSSSimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set each time we create and anonymous block
		SimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set each time we create and anonymous block
		for(int childIndex=0; childIndex<element.getChildNodes().getLength(); childIndex++)	//look at each child node
		{
			final XMLNode node=(XMLNode)element.getChildNodes().item(childIndex);	//look at this node
//TODO del				final boolean childIsBlock=node.getNodeType()==XMLNode.ELEMENT_NODE && ((XMLElement)node).getCSSStyle().getDisplay().equals(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//see if this child has block formatting or not
			final boolean childIsBlock=node.getNodeType()==XMLNode.ELEMENT_NODE && !((XMLElement)node).getCSSStyle().isDisplayInline();	//see if this child has block formatting or not
			if(!startedButNotFinishedAnonymousBlock)	//if we haven't started an anonymous block, yet
			{
				if(!childIsBlock)	//if this is an inline element and we haven't started an anonymous block, yet
				{

//TODO testing to see if this correctly removes unwanted CR/LF in block-only elements; perhaps this should go in the XML parser
//TODO maybe don't do this for preformatted elements
					if(node.getNodeType()==XMLNode.TEXT_NODE)	//if this is a text node TODO what about CDATA sections?
					{
//TODO del System.out.println("This is a text node");
						if(((XMLText)node).getData().trim().length()==0)	//if this text node has only whitespace
							continue;	//skip this child node
					}


					anonymousAttributeSet=new SimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
//TODO del when works						anonymousAttributeSet=new XMLCSSSimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
//TODO fix						XMLCSSStyleConstants.setXMLElementName(anonymousAttributeSet, XMLStyleConstants.AnonymousNameValue);	//show by its name that this is an anonymous box
					XMLStyleConstants.setXMLElementName(anonymousAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box TODO maybe change this to setAnonymous
//TODO del						anonymousAttributeSet.addAttribute(StyleConstants.NameAttribute, XMLCSSStyleConstants.AnonymousAttributeValue);	//show by its name that this is an anonymous box TODO maybe change this to setAnonymous
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
Log.trace("Ready to create children for: ", element.getNodeName());  //TODO del
		for(int childIndex=0; childIndex<element.getChildNodes().getLength(); childIndex++)	//look at each child node
		{
			final XMLNode node=(XMLNode)element.getChildNodes().item(childIndex);	//look at this node
			appendElementSpecListNode(elementSpecList, node, attributeSet, baseURL);	//append this node's information
		}
	}
*/





Log.trace("3"); //TODO del
//TODO del				final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSConstants.CSS_PROP_DISPLAY);	//get the display property TODO can we be sure this will be a primitive value?
			//TODO use the style constants to get the display property, maybe
			final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)XMLCSSStyles.getCSSPropertyCSSValue(attributeSet, XMLCSS.CSS_PROP_DISPLAY, false);	//get the display property for this element, but don't resolve up the attribute set parent hierarchy TODO can we be sure this will be a primitive value?
Log.trace("4"); //TODO del
			if(cssDisplayProperty!=null)	//if this element has a CSS display property
			{
Log.trace("Found display property: ", cssDisplayProperty);
				final String cssDisplayString=cssDisplayProperty.getStringValue();	//get the display value
//TODO del System.out.println("XML view factory found element with display: "+cssDisplayString);	//TODO del
				if(cssDisplayString.equals(XMLCSS.CSS_DISPLAY_BLOCK))	//if this should be block display
					return new XMLBlockView(element, View.Y_AXIS);	//create a block view
				else if(cssDisplayString.equals(XMLCSS.CSS_DISPLAY_LIST_ITEM))	//if this should be a list item
					return new XMLListItemView(element, View.Y_AXIS);	//create a list item view TODO change this to an XMLListItem(element)
				else if(cssDisplayString.equals(XMLCSS.CSS_DISPLAY_TABLE))	//if this should be a table
					return new XMLTableView(element);	//create a table view
				else if(cssDisplayString.equals(XMLCSS.CSS_DISPLAY_TABLE_CELL))	//if this should be table cell
					return new XMLBlockView(element, View.Y_AXIS);	//create a table cell view TODO change to the correct table cell view class
				else if(XMLCSS.CSS_DISPLAY_NONE.equals(cssDisplayString))	//if this element should have no display
					return new InvisibleView(element);	//create a hidden view
/*TODO important; fix


						//if this element specifies itself to be inline, we'll assume it's in
						//a paragraph and return null so that views will instead be created
						//for its children
//TODO all this needs fixed
				else if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_INLINE))	//see if this element specifies itself as inline
					return new XMLBlockView(element, View.Y_AXIS);	//create a table cell view TODO change to the correct table cell view class

*/



//TODO later make sure invisible (display: none) views are covered; right now they are made inline but not painted or given a size
			}
		}
Log.trace("5"); //TODO del
//TODO del Log.trace("XMLViewFactory creating inline view.");  //TODO del
		if(indicateMultipleViews) //if we should indicate multiple views are needed
		{
			Log.trace("returning null for element: ", XMLStyles.getXMLElementName(element.getAttributes()));  //TODO del
			return null;  //return null so that the XMLParagraphView.LayoutView can create views for the children of the inline view, eventually creating XMLInlineViews for the actual content
		}
		else  //if we're not allowed to indicate multiple views
			return new XMLInlineView(element);	//everything we don't know what to do with gets to be an inline view TODO should this be the default?
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
